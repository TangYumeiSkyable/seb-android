package com.supor.suporairclear.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACDeviceUser;
import com.accloud.service.ACException;
import com.accloud.service.ACFileInfo;
import com.accloud.service.ACFileMgr;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.CircleTransform;
import com.squareup.picasso.Picasso;
import com.supor.suporairclear.config.AppConstant;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by enyva on 16/5/28.
 */
public class MemberListAdapter extends BaseAdapter {
    private List<ACDeviceUser>  list = new ArrayList<ACDeviceUser>();
    private Boolean  owner = false;
    private Context context;
    private int curPosition = -1;
    private ACFileMgr fileMgr = AC.fileMgr();
    private UnbindDeviceListener unbindDeviceListener;

    public MemberListAdapter(Context context, List<ACDeviceUser> list,Boolean owner) {
        this.owner = owner;
        this.list = list;
        this.context = context;
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
        private TextView tv_username;
        private TextView tv_operation;
        private ImageView item_userimg;
    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        try{
            if(view == null){
                holder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.item_member, null);
                try {
                    holder.tv_username = (TextView) view.findViewById(R.id.tv_username);
                    holder.tv_operation = (TextView) view.findViewById(R.id.tv_operation);
                    holder.item_userimg = (ImageView) view.findViewById(R.id.item_userimg);

                    holder.tv_username.setTypeface(AppConstant.ubuntuRegular);
                    holder.tv_operation.setTypeface(AppConstant.ubuntuRegular);
                } catch(Exception e){}
                view.setTag(holder);
            } else{
                holder=(ViewHolder)view.getTag();
            }
            holder.tv_username.setText(list.get(position).getName());
            if(owner){
                //Unbundling button
                holder.tv_operation.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                unbindDeviceListener.unbindDeviceLis(position);
                            }
                        }
                );
                holder.tv_operation.setVisibility(View.VISIBLE);
            }else{
                holder.tv_operation.setVisibility(View.INVISIBLE);
            }
            NetWorkGetHeadIcomUrl(holder.item_userimg,list.get(position).getUserId());
        } catch(Exception e){
            e.printStackTrace();
        }

        return view;
    }

    /*
	 * According to the ACFileInfo head is the second parameter for the cloud in the Url
	 */
    protected void NetWorkGetHeadIcomUrl(final ImageView img, final Long userid) {

        //Upload the first parameter
        ACFileInfo fileInfo = new ACFileInfo(context.getResources().getString(R.string.airpurifier_application_show_appname_text) + "_img", "header_"+userid+".jpg");
        AC.fileMgr().getDownloadUrl(fileInfo, 0, new PayloadCallback<String>() {
            @Override
            public void success(String arg0) {
                Picasso.with(context).load(arg0).transform(new CircleTransform()).into(img);
            }

            @Override
            public void error(ACException arg0) {
                img.setImageResource(R.drawable.img_big_avator);
            }
        });

    }

    public void setUnbindDeviceListener(UnbindDeviceListener l) {
        this.unbindDeviceListener = l;
    }
    public interface UnbindDeviceListener {
        public void unbindDeviceLis(int myPosition);
    }
}
