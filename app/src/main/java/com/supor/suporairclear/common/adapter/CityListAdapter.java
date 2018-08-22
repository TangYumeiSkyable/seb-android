package com.supor.suporairclear.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enyva on 16/5/28.
 */
public class CityListAdapter extends BaseAdapter {
    private List<ACObject> list = new ArrayList<ACObject>();
    private Context context;
    public CityListAdapter(List<ACObject> listItems, Context context) {
        this.list = listItems;
        this.context = context;
    }
    public void reset(List<ACObject> listItems) {
    	this.list = listItems;
    	notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        private TextView tv_name;
        private ImageView iv_sel;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        try{
            if(view == null){
                holder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.item_city, null);
                try {
                    holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                    holder.iv_sel = (ImageView) view.findViewById(R.id.iv_sel);
                } catch(Exception e){}
                view.setTag(holder);
            } else{
                holder=(ViewHolder)view.getTag();
            }
            holder.tv_name.setText(list.get(position).getString("name"));
            if (list.get(position).getBoolean("check")) {
                holder.iv_sel.setVisibility(View.VISIBLE);
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.default_blue));
            } else {
                holder.iv_sel.setVisibility(View.GONE);
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.default_text));
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        return view;
    }
}
