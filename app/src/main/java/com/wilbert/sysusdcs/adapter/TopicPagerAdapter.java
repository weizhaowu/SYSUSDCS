package com.wilbert.sysusdcs.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wilbert.sysusdcs.fragment.ListviewFragment;

import java.util.List;

/**
 * Created by wilbert on 2016/5/20.
 */
public class TopicPagerAdapter extends FragmentStatePagerAdapter {

    public TopicPagerAdapter(FragmentManager fm, List<String> topicList) {
        super(fm);
        this.topicList = topicList;
    }
    private List<String> topicList;
    @Override
    public Fragment getItem(int position) {
        ListviewFragment listviewFragment = new ListviewFragment();
        listviewFragment.setTopicString(topicList.get(position));
        return listviewFragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return topicList.get(position);
    }
}
