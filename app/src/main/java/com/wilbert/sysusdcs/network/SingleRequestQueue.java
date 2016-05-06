package com.wilbert.sysusdcs.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by wilbert on 2016/5/4.
 */
public class SingleRequestQueue {
    private static SingleRequestQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;
    private ImageLoader mImageLoader;

    private SingleRequestQueue(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap>
                    cache = new LruCache<String, Bitmap>(20);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static synchronized SingleRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SingleRequestQueue(context);
        }
        return mInstance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request)  {
        mRequestQueue.add(request);
    }
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
