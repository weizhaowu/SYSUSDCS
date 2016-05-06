package com.wilbert.sysusdcs.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.wilbert.sysusdcs.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends BaseActivity {
    public static final int GET_BITMAP = 1234;
    public static final String BITMAP_KEY = "bitmap";
    int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
    int cacheSize = maxMemory / 8;
    LruCache<String, Bitmap> mMemryCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            imageView.setImageBitmap(bitmap);
            }
        };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private ImageView imageView;
    private Button button;
    @Override
    void initView() {
        imageView = (ImageView)findViewById(R.id.imageView);
        button = (Button)findViewById(R.id.testButton);
        button.setOnClickListener(this);
    }

    @Override
    void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.testButton:
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //handler 发送 Bitmap对象
                        Bitmap bitmap = null;
                        HttpURLConnection urlConnection = null;
                        BufferedInputStream in = null;

                        String uriString = "http://b.hiphotos.baidu.com/zhidao/pic/item/a6efce1b9d16fdfafee0cfb5b68f8c5495ee7bd8.jpg";
                        try {
                            Log.d("w", "hashkey");
                            bitmap = mMemryCache.get(hashKeyFormUrl(uriString));
                            if (bitmap == null) {
                                Log.d("w", "from uriString");
                                URL url = new URL(uriString);
                                urlConnection = (HttpURLConnection) url.openConnection();
                                in = new BufferedInputStream(urlConnection.getInputStream());
                                bitmap = BitmapFactory.decodeStream(in);
                                mMemryCache.put(hashKeyFormUrl(uriString), bitmap);
                            }
                            handler.obtainMessage(1,bitmap).sendToTarget();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
        }
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

}
