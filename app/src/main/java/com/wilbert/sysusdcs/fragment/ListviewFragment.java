package com.wilbert.sysusdcs.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.wilbert.sysusdcs.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by wilbert on 2016/5/20.
 */
public class ListviewFragment extends Fragment {
    private String topicString;
    private ListView listView;
    public void setTopicString(String topicString) {
        this.topicString = topicString;
    }

    public String getTopicString() {
        return topicString;
    }
    private TextView textView;
    private ViewGroup rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_student_work, container,
                false);
        initView();
        return rootView;
    }

    public void initView() {
        textView = (TextView)rootView.findViewById(R.id.testTextView);
        textView.setText(topicString);

        listView = (ListView)rootView.findViewById(R.id.fragmentListView);
    }
}
