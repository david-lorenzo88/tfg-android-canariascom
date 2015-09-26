/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canarias.rentacar.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * This helper class download images from the Internet and binds those with the provided ImageView.
 * <p/>
 * <p>It requires the INTERNET permission, which should be added to your application's manifest
 * file.</p>
 * <p/>
 * A local cache of downloaded images is maintained internally to improve performance.
 */
public class ImageDownloader {
    private static final String LOG_TAG = "ImageDownloader";
    private static final String CAR_IMAGE_PATH = "cars_images";
    private static final int CACHE_TIME_FILESYSTEM = 30; //days
    private static final int HARD_CACHE_CAPACITY = 10;
    // Hard cache, with a fixed maximum capacity and a life duration
    private final HashMap<String, Bitmap> sHardBitmapCache =
            new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
                    if (size() > HARD_CACHE_CAPACITY) {
                        // Entries push-out of hard reference cache are transferred to soft reference cache
                        sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
                        return true;
                    } else
                        return false;
                }
            };
    // Soft cache for bitmaps kicked out of hard cache
    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache =
            new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);
    private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds
    private final Handler purgeHandler = new Handler();
    private final Runnable purger = new Runnable() {
        public void run() {
            clearCache();
        }
    };

    /*
     * Same as download but the image is always downloaded and the cache is not used.
     * Kept private at the moment as its interest is not clear.
       private void forceDownload(String url, ImageView view) {
          forceDownload(url, view, null);
       }
     */
    private Mode mode = Mode.CORRECT;
    private int mMaxWidth;
    private Context context;

    public ImageDownloader(int maxWidth, Context context) {
        mMaxWidth = maxWidth;
        this.context = context;
    }

    /**
     * Returns true if the current download has been canceled or if there was no download in
     * progress on this image view.
     * Returns false if the download in progress deals with the same url. The download is not
     * stopped in that case.
     */
    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active download task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    /**
     * Download the specified image from the Internet and binds it to the provided ImageView. The
     * binding is immediate if the image is found in the cache and will be done asynchronously
     * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
     *
     * @param url       The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public void download(String url, ImageView imageView) {
        //Fix to change 'small' to 'large' in the URL
        //in order to download the big image
        url = url.replace("small", "large");


        resetPurgeTimer();
        Bitmap bitmap = getBitmapFromCache(url);

        if (bitmap == null) {

            //Try to load it from internal storage
            //If not found in file system, this method calls forceDownload to download it
            //from Internet
            loadImageFromStorage(url, imageView);

        } else {
            Log.v("TEST", "IMAGE: " + url + " - FROM CACHE");
            cancelPotentialDownload(url, imageView);
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Same as download but the image is always downloaded and the cache is not used.
     * Kept private at the moment as its interest is not clear.
     */
    private void forceDownload(String url, ImageView imageView) {
        // State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
        if (url == null) {
            imageView.setImageDrawable(null);
            return;
        }

        if (cancelPotentialDownload(url, imageView)) {
            switch (mode) {
                case NO_ASYNC_TASK:
                    Bitmap bitmap = downloadBitmap(url);
                    addBitmapToCache(url, bitmap);
                    imageView.setImageBitmap(bitmap);
                    break;

                case NO_DOWNLOADED_DRAWABLE:
                    imageView.setMinimumHeight(156);
                    BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
                    task.execute(url);
                    break;

                case CORRECT:
                    task = new BitmapDownloaderTask(imageView);
                    DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
                    imageView.setImageDrawable(downloadedDrawable);
                    //imageView.setMinimumHeight(156);
                    task.execute(url);
                    break;
            }
        }
    }

    /**
     * Saves Image to internal storage
     * @param bitmapImage image to save
     * @param name name
     * @return directory path
     */
    private String saveToInternalSorage(Bitmap bitmapImage, String name) {

        // path to /data/data/yourapp/app_data/cars_images
        File directory = context.getDir(CAR_IMAGE_PATH, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, name);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    /**
     * Load image from internal storage, if not found, then try to download it from url
     * @param url remote image Url
     * @param imageView View to assign image to
     */
    private void loadImageFromStorage(String url, ImageView imageView) {

        try {
            //Try to find it in file system
            File f = new File(context.getDir(CAR_IMAGE_PATH, Context.MODE_PRIVATE), getUrlFilename(url));

            //if found, check last modified date, if greater than CACHE_TIME_FILYSYSTEM then
            //force download. Otherwise, set as bitmap on imageView
            if (f.lastModified() < (new Date().getTime() - TimeUnit.MILLISECONDS.convert(CACHE_TIME_FILESYSTEM, TimeUnit.DAYS))) {
                Log.v("TEST", "IMAGE FOUND IN FILE SYSTEM BUT WAS OBSOLETE, downloading... " + url);
                forceDownload(url, imageView);
            } else {
                Log.v("TEST", "IMAGE FOUND IN FILE SYSTEM... " + url);
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                imageView.setImageBitmap(b);
            }
        } catch (FileNotFoundException e) {
            //If not found, force download
            Log.v("TEST", "IMAGE NOT FOUND IN FILE SYSTEM, downloading... " + url);
            forceDownload(url, imageView);
        }


    }

    /**
     * Returns filename part of a url
     * @param url the url
     * @return the filename part
     */
    private String getUrlFilename(String url) {

        String[] urlParts = url.split("/");
        String filename = urlParts[urlParts.length - 1];
        return filename;

    }

    
    /*
     * Cache-related fields and methods.
     * 
     * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the
     * Garbage Collector.
     */

    Bitmap downloadBitmap(String url) {
        final int IO_BUFFER_SIZE = 4 * 1024;
        Bitmap b = null;
        // AndroidHttpClient is not allowed to be used from the main thread
        //final HttpClient client = (mode == Mode.NO_ASYNC_TASK) ? new DefaultHttpClient() :
         //       AndroidHttpClient.newInstance("Android");

        ;

        try {
            URL _url = new URL(url);
            HttpURLConnection  connection  =  (HttpURLConnection)_url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            /*if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving bitmap from " + url);
                return null;
            }*/

            //final HttpEntity entity = response.getEntity();
            //if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = connection.getInputStream();


                    // return BitmapFactory.decodeStream(inputStream);
                    // Bug on slow connections, fixed in future release.
                    b = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));

                    saveToInternalSorage(b, getUrlFilename(url));

                    if (b != null) {
                        int width = b.getWidth();
                        int height = b.getHeight();

                        if (width >= mMaxWidth) {
                            while (true) {
                                if (width <= mMaxWidth) {
                                    break;
                                }
                                width /= 2;
                                height /= 2;
                            }
                            b = Bitmap.createScaledBitmap(b, width, height, false);
                        }

                        Log.v("CANARIAS", "URL Downloaded: " + url);
                        return b;
                    }
                    return null;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    //entity.consumeContent();
                }
            //}
        } catch (IOException e) {

            Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
        } catch (IllegalStateException e) {

            Log.w(LOG_TAG, "Incorrect URL: " + url);
        } catch (Exception e) {

            Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
        } finally {

            /*if ((client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close();
            }*/
        }
        return null;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        clearCache();
    }

    /**
     * Adds this bitmap to the cache.
     *
     * @param bitmap The newly downloaded bitmap.
     */
    private void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(url, bitmap);
            }
        }
    }

    /**
     * @param url The URL of the image that will be retrieved from the cache.
     * @return The cached bitmap or null if it was not found.
     */
    private Bitmap getBitmapFromCache(String url) {
        // First try the hard reference cache
        synchronized (sHardBitmapCache) {
            final Bitmap bitmap = sHardBitmapCache.get(url);
            if (bitmap != null) {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                sHardBitmapCache.remove(url);
                sHardBitmapCache.put(url, bitmap);
                return bitmap;
            }
        }

        // Then try the soft reference cache
        SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
        if (bitmapReference != null) {
            final Bitmap bitmap = bitmapReference.get();
            if (bitmap != null) {
                // Bitmap found in soft cache
                return bitmap;
            } else {
                // Soft reference has been Garbage Collected
                sSoftBitmapCache.remove(url);
            }
        }

        return null;
    }

    /**
     * Clears the image cache used internally to improve performance. Note that for memory
     * efficiency reasons, the cache will automatically be cleared after a certain inactivity delay.
     */
    public void clearCache() {
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
    }

    /**
     * Allow a new delay before the automatic cache clear is done.
     */
    private void resetPurgeTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
    }

    public enum Mode {NO_ASYNC_TASK, NO_DOWNLOADED_DRAWABLE, CORRECT}

    /*
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    /**
     * A fake Drawable that will be attached to the imageView while the download is in progress.
     * <p/>
     * <p>Contains a reference to the actual download task, so that a download task can be stopped
     * if a new binding is required, and makes sure that only the last started download process can
     * bind its result, independently of the download finish order.</p>
     */
    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            super(Color.WHITE);
            bitmapDownloaderTaskReference =
                    new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }

    /**
     * The actual AsyncTask that will asynchronously download the image.
     */
    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String url;

        public BitmapDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        /**
         * Actual download method.
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            return downloadBitmap(url);
        }

        /**
         * Once the image is downloaded, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            addBitmapToCache(url, bitmap);

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
                // Change bitmap only if this process is still associated with it
                // Or if we don't use any bitmap to task association (NO_DOWNLOADED_DRAWABLE mode)
                if ((this == bitmapDownloaderTask) || (mode != Mode.CORRECT)) {

                    imageView.setImageBitmap(bitmap);

                }
            }
        }
    }
}
