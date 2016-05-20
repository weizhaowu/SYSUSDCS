package com.wilbert.sysusdcs.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wilbert.sysusdcs.R;
import com.wilbert.sysusdcs.activity.DetailActivity;
import com.wilbert.sysusdcs.network.UrlData;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wilbert on 2016/5/20.
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    public static final String W = "wilbert";
    private List<String> titleList;
    private Context context;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewHolder viewHolder = (ViewHolder)v.getTag();
            int position = viewHolder.getAdapterPosition();
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("key", Integer.toString(position));
            context.startActivity(intent);
        }
    };
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.item_text_view);
        }
    }

    public ContentAdapter(Context context, List<String> titleList) {
        this.context = context;
        this.titleList = titleList;
    }
    @Override
    public ContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycle_view,
                                            parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContentAdapter.ViewHolder holder, int position) {
        holder.textView.setText(titleList.get(position));
        holder.textView.setOnClickListener(onClickListener);
        holder.textView.setTag(holder);
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }
}
