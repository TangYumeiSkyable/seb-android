package com.supor.suporairclear.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.accloud.service.ACObject;
import com.bumptech.glide.Glide;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.MoreActivity;
import com.rihuisoft.loginmode.utils.CircleTransform;
import com.squareup.picasso.Picasso;
import com.supor.suporairclear.activity.DeviceListActivity;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.model.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enyva on 16/5/28.
 */
public class DeviceListAdapter extends BaseAdapter {
    private List<DeviceInfo> list = new ArrayList<DeviceInfo>();
    private Context context;
    private int curPosition = -1;

    public DeviceListAdapter(List<DeviceInfo> listItems, Context context) {
        this.list = listItems;
        this.context = context;
    }

    public void reset(List<DeviceInfo> listItems) {
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

    private class ViewHolder {
        private TextView tv_type;
        private TextView tv_device;
        private ImageView iv_insertimg;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        try {
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.item_device, null);
                try {
                    holder.tv_type = (TextView) view.findViewById(R.id.tv_type);
                    holder.tv_device = (TextView) view.findViewById(R.id.tv_device);
                    holder.iv_insertimg = (ImageView) view.findViewById(R.id.iv_insertimg);
                    holder.tv_type.setTypeface(AppConstant.ubuntuRegular);
                    holder.tv_device.setTypeface(AppConstant.ubuntuRegular);
                } catch (Exception e) {
                }
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            String deviceType = "";
            String deviceImg = "";
            // fix bug 479
            try {
                if (ConstantCache.deviceModeList != null && !ConstantCache.deviceModeList.isEmpty()) {
                    for (ACObject item : ConstantCache.deviceModeList) {
                        if (item.contains("subDomainId") && item.get("subDomainId").equals(String.valueOf(list.get(position).getSubDomainId()))) {
                            deviceType = item.get("name").toString();
                            deviceImg = item.get("img").toString();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            holder.tv_type.setText(String.format(context.getString(R.string.airpurifier_more_show_devicemodelnumber_text), deviceType));
            holder.tv_device.setText(String.format(context.getString(R.string.placeholder), context.getString(R.string.airpurifier_more_mydevice_device_name)) + list.get(position).getDeviceName());

            //没有获取图片地址 不显示图片
            if (!"".equals(deviceImg)) {
                Glide.with(context).load(deviceImg.replace("{size}", "original"))
                        .placeholder(R.drawable.img_p1).error(R.drawable.img_p1).into(holder.iv_insertimg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}
