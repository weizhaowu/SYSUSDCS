package com.wilbert.sysusdcs.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wilbert.sysusdcs.R;
import com.wilbert.sysusdcs.adapter.ContentAdapter;
import com.wilbert.sysusdcs.network.UrlData;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wilbert on 2016/5/20.
 */
public class ListviewFragment extends Fragment {
    public static final String W = "wilbert";
    private String topicString;

    private Map<String, String> urlMap = new HashMap<>();

    public void setTopicString(String topicString) {
        this.topicString = topicString;
    }

    public String getTopicString() {
        return topicString;
    }
    private TextView textView;
    private ViewGroup rootView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ContentAdapter contentAdapter;
    private RequestQueue requestQueue;
    private Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_student_work, container,
                false);
        context = getActivity();
        initView();
        initData();
        return rootView;
    }

    private void initData() {
        urlMap.put(context.getString(R.string.Student), UrlData.StudentUrl);
        urlMap.put(context.getString(R.string.Undergraduate), UrlData.UndergraduateUrl);
        urlMap.put(context.getString(R.string.Postgraduate), UrlData.PostgraduateUrl);
        urlMap.put(context.getString(R.string.College), UrlData.CollegeUrl);
        requestQueue = Volley.newRequestQueue(context);
        Log.d(W, "before volley " + topicString + " " + urlMap.get(topicString));
        final String StudentUrl
                = "http://sdcs.sysu.edu.cn/?cat=58";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, StudentUrl
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(W, "on response");
                Log.d(W, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(W, "error" + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if (headers == null)
                    headers = new HashMap<>();
                headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
                headers.put("Accept-Encoding", "gzip, deflate, sdch");
                headers.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
                return headers;
            }
        };
        //requestQueue.add(stringRequest);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection;
                try {
                    Uri.Builder uriBuilder = new Uri.Builder();
                    uriBuilder.encodedAuthority("http://sdcs.sysu.edu.cn/")
                            .appendQueryParameter("cat", "58")
                            .scheme("http");
                    URL url = new URL(uriBuilder.build().toString());
                    Log.d(W, "url " + url.toString());
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        builder.append(line).append("\n");
                    }
                    Log.d(W, "url connection " + builder.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void initView() {
        textView = (TextView)rootView.findViewById(R.id.testTextView);
        textView.setText(topicString);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycleView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        String [] strings={"a","b","c","d"};
        List<String> stringList = new ArrayList<>();
        for (String s:strings) {
            stringList.add(s);
            stringList.add(s);
            stringList.add(s);
        }
        contentAdapter = new ContentAdapter(getActivity(), stringList);
        recyclerView.setAdapter(contentAdapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
    }
}
