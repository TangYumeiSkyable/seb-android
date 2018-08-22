package com.supor.suporairclear.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.groupeseb.airpurifier.R;

import org.json.JSONArray;

/**
 * Created by enyva on 2017/3/21.
 */

public class MessageItemAdapter extends BaseAdapter {
    private String str;
    private JSONArray list;
    private Context context;

    public MessageItemAdapter(Context context, JSONArray list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {

        return list.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            str = list.get(position).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        try {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_messagelist, null);

                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                holder.iv_read = (ImageView) convertView.findViewById(R.id.iv_read);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_title.setText(list.getJSONObject(position).get("title").toString());
            holder.tv_time.setText(getDD_MMTypeData(list.getJSONObject(position).get("createTime").toString()));
            holder.tv_content.setText(Html.fromHtml(list.getJSONObject(position).get("item").toString()));
            holder.iv_read.setImageResource((int) list.getJSONObject(position).get("read"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView tv_title;
        private TextView tv_time;
        private TextView tv_content;
        private ImageView iv_read;
    }

    //03-16 Fri
    private String getDD_MMTypeData(String srcData) {
        String result = "";
        if (srcData != null) {
            String month = srcData.substring(0, 2);
            String day = srcData.substring(3, 5);
            result = new StringBuffer().append(day).append("-").append(month).toString();
        }
        return result;
    }
}
