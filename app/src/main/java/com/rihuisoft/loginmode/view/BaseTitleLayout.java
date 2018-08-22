package com.rihuisoft.loginmode.view;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.groupeseb.airpurifier.R;


/**
 * Created by zhaoqy on 2015/6/18.
 */
public class BaseTitleLayout extends LinearLayout {

    public TextView title;
    public LinearLayout leftll;
    public RelativeLayout rightrl;
    public TextView leftBtn;
    public TextView rightBtn;
    public TextView right2Btn;

    private View titleBar;

    public BaseTitleLayout(Context context, int layoutId) {
    	super(context);
    	try {
    		
            this.setOrientation(LinearLayout.VERTICAL);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //titleBar
            titleBar = inflater.inflate(R.layout.titlebar_layout, this, false);
            addView(titleBar);

            //content
            View content = inflater.inflate(layoutId, null);
            LayoutParams contentParam = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(content, contentParam);

//            FontManager.changeFonts(this);
            title = getView(R.id.header_title);
            leftll = getView(R.id.header_left_ll);
            leftBtn = getView(R.id.header_left_btn);
            rightBtn = getView(R.id.header_right_btn);
            right2Btn = getView(R.id.header_right2_btn);
            rightrl =  getView(R.id.header_right_rl);
    	} catch(Exception e) {
			e.printStackTrace();
		}
        
    }

    public View getTitleBar() {
        return titleBar;
    }

    private <T extends View> T getView(int id) {
        return (T) titleBar.findViewById(id);
    }
}