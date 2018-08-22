package com.rihuisoft.loginmode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACMsg;
import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.ToastUtil;
import com.supor.suporairclear.adapter.FAQAdapter;
import com.supor.suporairclear.config.Constants;
import com.supor.suporairclear.config.DCPServiceUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentLibraries;

/**
 * Created by Administrator on 2017/4/28.
 */

public class FAQActivity extends BaseActivity {


    private ListView mListView;
    private ArrayList<ACObject> mObjects;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_faq);
            initView();
            setTitle(getString(R.string.airpurifier_more_userinfomation_FAQ));
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
                Intent intent = new Intent(FAQActivity.this, FAQChildActivity.class);
                intent.putExtra(Constants.KEY_FAQBEAN, mObjects.get(position));
                FAQActivity.this.startActivity(intent);
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
        Single
                .create(new SingleOnSubscribe<ArrayList<ACObject>>() {
                    @Override
                    public void subscribe(final SingleEmitter<ArrayList<ACObject>> emitter) throws Exception {
                        DCPServiceUtils.syncContent(DCPServiceContentLibraries, new PayloadCallback<ACMsg>() {
                            @Override
                            public void error(ACException e) {
                                if (!emitter.isDisposed())
                                    emitter.onError(e);
                            }

                            @Override
                            public void success(ACMsg acMsg) {
                                mObjects = (ArrayList<ACObject>) ((ACObject) acMsg.getObjectData().get("content")).get("objects");
                                emitter.onSuccess(mObjects);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<ArrayList<ACObject>>() {
                            @Override
                            public void accept(ArrayList<ACObject> arrayList) throws Exception {
                                List<String> content = new ArrayList<>();
                                for (ACObject oneGradeContent : arrayList) {
                                    Object title = oneGradeContent.get("title");
                                    if (title != null)
                                        content.add(title.toString());
                                }
                                mListView.setAdapter(new FAQAdapter(content, FAQActivity.this.getApplicationContext()));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ToastUtil.showToast(FAQActivity.this,getString(R.string.airpurifier_faq_failed_get_data));
                            }
                        });
    }
}
