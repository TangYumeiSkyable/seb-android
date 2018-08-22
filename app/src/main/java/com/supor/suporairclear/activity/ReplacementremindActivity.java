package com.supor.suporairclear.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.supor.suporairclear.model.Replacementremind;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by emma on 2017/5/18.
 */

public class ReplacementremindActivity extends BaseActivity {
    private Replacementremind replacementremind;

    private ListView lv_fMsgList;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_replacementremind);
        setTitle(getString(R.string.airpurifier_more_show_replacementremind_text));
        lv_fMsgList = (ListView) findViewById(R.id.lv_fmsg_List);

        setNavBtn(R.drawable.ico_back, 0);

        lv_fMsgList.setAdapter(new CustomSimpleAdapter(ReplacementremindActivity.this,
                createData(),
                R.layout.item_replacementremind));
        lv_fMsgList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }

    class CustomSimpleAdapter extends SimpleAdapter {
        private int mResource;
        private List<? extends Map<String, ?>> mData;

        public CustomSimpleAdapter(Context context,
                                   List<? extends Map<String, ?>> data, int resource) {
            super(context, data, resource, null, null);
            this.mResource = resource;
            this.mData = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(mResource, null);
            TextView text = (TextView) view.findViewById(R.id.textView2);
            text.setText(Html.fromHtml(mData.get(position).get("item").toString()));
            return view;
        }
    }

    private List<Map<String, String>> createData() {
        List<Map<String, String>> newsList = new ArrayList<Map<String, String>>();
        Intent intent = this.getIntent();
        String deviceName = intent.getExtras().getString("deviceName");
        Long createTimes = intent.getExtras().getLong("createTimes");

        String text = intent.getExtras().getString("text");
        if (intent.getExtras().getLong("initial_filter") <= 0 && text.contains("Pre-filter")) {
            String filterName = getString(R.string.airpurifier_more_show_prefilter_tex);
            Map<String, String> content = new HashMap<String, String>();
            long initial_filter = intent.getExtras().getLong("initial_filter");
            content.put("item", getString(R.string.airpurifier_more_show_filteritem_text) + " " + filterName + getString(R.string.airpurifier_more_show_filteritemone_text) + " <font color=\"#36424a\">" + deviceName + "</font>" + getString(R.string.airpurifier_more_show_filteritemtwo_text) + " " + intent.getExtras().getLong("initial_filter") + " " + getString(R.string.airpurifier_more_show_filteritemthree_text) + " " + (100 - (initial_filter / 10)) + getString(R.string.airpurifier_percentage));
            newsList.add(content);
        }

        if (intent.getExtras().getLong("act_filter") <= 200 && intent.getExtras().getString("text").contains("Active carbon filter")) {
            String filterName = getString(R.string.airpurifier_more_show_activefilter_tex);
            Map<String, String> content = new HashMap<String, String>();
            content.put("item", getString(R.string.airpurifier_more_show_filteritem_text) + " " + filterName + " " + getString(R.string.airpurifier_more_show_filteritemone_text) + " <font color=\"#36424a\">" + deviceName + "</font>" + getString(R.string.airpurifier_more_show_filteritemtwo_text) + " " + intent.getExtras().getLong("act_filter") + " " + getString(R.string.airpurifier_more_show_filteritemthree_text) + " " + (100 - (intent.getExtras().getLong("act_filter") / 10)) + getString(R.string.airpurifier_percentage));
            newsList.add(content);
        }

        if (intent.getExtras().getLong("HEPA_filter") <= 200 && intent.getExtras().getString("text").contains("HEPA filter")) {
            String filterName = getString(R.string.airpurifier_more_show_hepafilter_tex);
            Map<String, String> content = new HashMap<String, String>();
            content.put("item", getString(R.string.airpurifier_more_show_filteritem_text) + " " + filterName + " " + getString(R.string.airpurifier_more_show_filteritemone_text) + " <font color=\"#36424a\">" + deviceName + "</font>" + getString(R.string.airpurifier_more_show_filteritemtwo_text) + " " + intent.getExtras().getLong("HEPA_filter") + " " + getString(R.string.airpurifier_more_show_filteritemthree_text) + " " + (100 - (intent.getExtras().getLong("HEPA_filter") / 10)) + getString(R.string.airpurifier_percentage));
            newsList.add(content);
        }

        if (intent.getExtras().getLong("nano_filter") <= 200 && intent.getExtras().getString("text").contains("Nano capture")) {
            String filterName = getString(R.string.airpurifier_more_show_nanofilter_tex);
            Map<String, String> content = new HashMap<String, String>();
            content.put("item", getString(R.string.airpurifier_more_show_filteritem_text) + " " + filterName + " " + getString(R.string.airpurifier_more_show_filteritemone_text) + " <font color=\"#36424a\">" + deviceName + "</font>" + getString(R.string.airpurifier_more_show_filteritemtwo_text) + " " + intent.getExtras().getLong("nano_filter") + " " + getString(R.string.airpurifier_more_show_filteritemthree_text) + " " + (100 - (intent.getExtras().getLong("nano_filter") / 10)) + getString(R.string.airpurifier_percentage));
            newsList.add(content);
        }
        return newsList;
    }


    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        try {
            if (component == TitleBar.LEFT) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
