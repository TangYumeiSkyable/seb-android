package com.rihuisoft.loginmode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;
import com.supor.suporairclear.adapter.FAQAdapter;
import com.supor.suporairclear.config.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/28.
 */

public class FAQChildActivity extends BaseActivity {


    private ListView mListView;
    private ACObject twoGradeContentACobject;
    private List<ACObject> mSonContentsACObject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_faq);
            initView();

            setNavBtn(R.drawable.ico_back, 0);
            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Controls the initialization
     */
    private void initView() {
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FAQChildActivity.this, FAQDocumentActivity.class);
                intent.putExtra(Constants.KEY_FAQBEAN, mSonContentsACObject.get(position));
                FAQChildActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        switch (component) {
            case LEFT:
                finish();
                break;
        }
    }

    private void getData() {

        Intent intent = getIntent();
        twoGradeContentACobject = (ACObject) intent.getSerializableExtra(Constants.KEY_FAQBEAN);

        mSonContentsACObject = twoGradeContentACobject.get("sonContents");

        //设置标题
        setTitle(twoGradeContentACobject.get("title").toString());

        List<String> adapterContent = new ArrayList<>();

        if (mSonContentsACObject != null) {
            for (ACObject acObject : mSonContentsACObject) {
                adapterContent.add(acObject.get("title").toString());
            }
            mListView.setAdapter(new FAQAdapter(adapterContent, FAQChildActivity.this.getApplicationContext()));
        }
    }

}
