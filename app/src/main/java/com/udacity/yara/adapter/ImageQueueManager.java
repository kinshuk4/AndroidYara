package com.udacity.yara.adapter;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


public class ImageQueueManager {
    private static String LOG_TAG = ImageQueueManager.class.getSimpleName();
    private static ImageQueueManager mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private ImageQueueManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        // Use the custome made bitmap cache class so delete the commented out sections
    /*    mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);
    */
        mImageLoader = new ImageLoader(mRequestQueue, new  LruBitmapCache(
                LruBitmapCache.getCacheSize(mCtx)));

    /*              @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    */
    }

    public static synchronized ImageQueueManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ImageQueueManager(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}