package com.wilbert.sysusdcs.activity;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by wilbert on 2016/5/4.
 */
public abstract class BaseActivity  extends AppCompatActivity implements View.OnClickListener{
    abstract void initView();
    abstract void initData();
}
