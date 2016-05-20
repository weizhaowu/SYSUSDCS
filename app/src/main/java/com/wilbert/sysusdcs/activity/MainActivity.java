package com.wilbert.sysusdcs.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import com.wilbert.sysusdcs.R;
import com.wilbert.sysusdcs.adapter.TopicPagerAdapter;
import com.wilbert.sysusdcs.network.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    public static final int GET_BITMAP = 1234;
    public static final String BITMAP_KEY = "bitmap";
    private static final int DISK_CACHE_INDEX = 0;
    private static final int IO_BUFFER_SIZE = 8*1024;
    int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
    int cacheSize = maxMemory / 8;
    LruCache<String, Bitmap> mMemryCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }
    };

    DiskLruCache diskLruCache;


    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    private List<String> topicList;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    @Override
    void initView() {
            viewPager = (ViewPager)findViewById(R.id.viewPager);
        }

    @Override
    void initData() {
        File file = getDiskCacheDir(this.getApplicationContext(), "bitmap");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            diskLruCache = DiskLruCache.open(file, 1,1, 1024*1024*10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        topicList = new ArrayList<>();
        topicList.add(getString(R.string.College));
        topicList.add(getString(R.string.Postgraduate));
        topicList.add(getString(R.string.Undergraduate));
        topicList.add(getString(R.string.Student));

        pagerAdapter = new TopicPagerAdapter(getSupportFragmentManager(), topicList);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private Bitmap getBitmapFromDisk(String uriString) throws IOException {
        Bitmap bitmap = null;
        String key = hashKeyFormUrl(uriString);
        Log.d("w", "from disk");
        DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
        if (snapshot != null) {
            Log.d("w", "snapshot is not null");
            InputStream fileInputStream = snapshot.getInputStream(0);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            if (bitmap == null) {
                Log.d("w", "stream could not be decoded");
            }
            if (bitmap != null) {
                Log.d("w", "bitmap is not null");
                mMemryCache.put(key, bitmap);
            }
        }
        return bitmap;
    }

    private Bitmap downloadBitmapFromUri(String uriString) throws IOException {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        OutputStream outputStream = null;
        try {
            Log.d("w", "from uri");
            final URL url = new URL(uriString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8*1024);


/*
            bitmap = BitmapFactory.decodeStream(in);
            mMemryCache.put(hashKeyFormUrl(uriString), bitmap);
*/


            DiskLruCache.Editor editor = diskLruCache.edit(hashKeyFormUrl(uriString));
            if (editor != null) {
                outputStream = editor.newOutputStream(0);
                if (downloadUrlToStream(uriString, outputStream)) {
                    Log.d("w", "download url to stream true");
                    editor.commit();
                } else {
                    Log.d("w", "down load url to stream false");
                    editor.abort();
                }
                diskLruCache.flush();
            }
/*
            out = new BufferedOutputStream(outputStream, 8*1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            editor.commit();
            diskLruCache.flush();
*/
            bitmap = getBitmapFromDisk(uriString);
        } catch (final IOException e) {
            Log.e("w", "Error in downloadBitmap: " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    /*--------------------------------------------------------------------------*/
/*
        if (diskLruCache == null) {
            return null;
        }

        String key = hashKeyFormUrl(uriString);
        DiskLruCache.Editor editor = diskLruCache.edit(key);
        if (editor != null) {
            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
            if (downloadUrlToStream(uriString, outputStream)) {
                Log.d("w", "downloadurltostream true");
                editor.commit();
            } else {
                Log.d("w", "downloadurltostream false");
                editor.abort();
            }
            diskLruCache.flush();
        }
        return getBitmapFromDisk(uriString);
*/
    }

    private boolean downloadUrlToStream(String uriString, OutputStream outputStream) throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(uriString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),
                    IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            Log.e("w", "downloadBitmap failed." + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            out.close();
            in.close();
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private String hashKeyFormUrl(String url) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    public File getDiskCacheDir(Context context, String uniqueName) {
        boolean externalStorageAvailable = Environment
                .getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }

        return new File(cachePath + File.separator + uniqueName);
    }
    @Override
     public void onBackPressed() {
        if(tabLayout.getSelectedTabPosition() != 0) {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
            return;
        } else {
            super.onBackPressed();
        }
     }
}
