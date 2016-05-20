package com.wilbert.sysusdcs.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wilbert.sysusdcs.R;

public class DetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent =getIntent();
        Log.d(W, intent.getStringExtra("key"));
    }

    @Override
    void initView() {

    }

    @Override
    void initData() {

    }

    @Override
    public void onClick(View v) {

    }
}
