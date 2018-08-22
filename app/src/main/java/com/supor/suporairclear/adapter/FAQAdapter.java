package com.supor.suporairclear.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.groupeseb.airpurifier.R;

import java.util.List;

/**
 * Created by feng.jian on 2018/3/2.
 */

public class FAQAdapter extends BaseAdapter {
 
 
    private List<String> mItemContent;
    private Context mContext;

    public FAQAdapter(List<String> itemContent, Context context) {
        mItemContent = itemContent;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mItemContent == null ? 0 : mItemContent.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemContent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.list_item_faq, parent, false);
        TextView tvContent = (TextView) inflate.findViewById(R.id.tv_content);
        tvContent.setText(mItemContent.get(position));
        return inflate;
    }
}
