package com.rihuisoft.loginmode.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.ProgressCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACACL;
import com.accloud.service.ACException;
import com.accloud.service.ACFileInfo;
import com.accloud.service.ACFileMgr;
import com.accloud.service.ACMsg;
import com.accloud.utils.PreferencesUtils;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.CircleTransform;
import com.rihuisoft.loginmode.utils.DensityUtil;
import com.rihuisoft.loginmode.utils.FileUtil;
import com.rihuisoft.loginmode.utils.Logger;
import com.rihuisoft.loginmode.utils.ToastUtil;
import com.rihuisoft.loginmode.view.AlertDialogUserDifine;
import com.rihuisoft.loginmode.view.CustomProgressDialog;
import com.rihuisoft.loginmode.view.SelectPicPopupWindow;
import com.squareup.picasso.Picasso;
import com.supor.suporairclear.activity.ModifyNameActivity;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.config.Constants;
import com.supor.suporairclear.config.DCPServiceUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.zhy.autolayout.AutoRelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1458;
    private static final int REQUEST_CODE_MODIFY_NAME = 1457;
    private static final int REQUEST_CODE_SELECT_MODE = 1456;
    private String mCurrentPhotoPath;
    private File mTempDir;
    private TextView[] tvs;
    private Button btn_logout;
    private View rl_avatar;
    private View rl_nickname;
    private AutoRelativeLayout rl_changepwd;
    private ImageView iv_userimg;
    private CustomProgressDialog p = null;
    private TextView tv_nickname;
    private long userId;
    private Uri imageUri; //照片存放路径
    private Object mImageFromGallery;
    private Disposable mAvatarUpLoad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);
        setTitle(getString(R.string.airpurifier_more_show_personaltitle_text));
        AppManager.getAppManager().addActivity(this);
        initView();
        setNavBtn(R.drawable.ico_back, 0);
    }

    /**
     * Page to initialize
     */
    public void initView() {
        tvs = new TextView[]{(TextView) findViewById(R.id.tv_avatar)
                , (TextView) findViewById(R.id.tv_nickname_tag)
                , (TextView) findViewById(R.id.tv_nickname)};

        tv_nickname = (TextView) findViewById(R.id.tv_nickname);


        for (int i = 0; i < tvs.length; i++) {
            tvs[i].setTypeface(AppConstant.ubuntuRegular);
        }

        btn_logout = (Button) findViewById(R.id.btn_logout);
        rl_avatar = (View) findViewById(R.id.rl_avatar);
        rl_nickname = (View) findViewById(R.id.rl_nickname);
        rl_changepwd = (AutoRelativeLayout) findViewById(R.id.rl_changepwd);
        iv_userimg = (ImageView) findViewById(R.id.iv_pi_userimg);

        btn_logout.setTypeface(AppConstant.ubuntuRegular);
        btn_logout.setOnClickListener(this);
        rl_avatar.setOnClickListener(this);
        rl_nickname.setOnClickListener(this);
        rl_changepwd.setOnClickListener(this);

        mTempDir = new File(Environment.getExternalStorageDirectory(), "suporAirClear_img");
        if (!mTempDir.exists()) {
            mTempDir.mkdirs();
        }
        userId = PreferencesUtils.getLong(PersonalInfoActivity.this, "uid");
        tv_nickname.setText(PreferencesUtils.getString(PersonalInfoActivity.this, "name", ""));
        NetWorkGetHeadIcomUrl(iv_userimg, userId);
    }

    protected void NetWorkGetHeadIcomUrl(final ImageView img, final Long userid) {
        if (userid != null) {
            ACFileInfo fileInfo = new ACFileInfo(getResources().getString(R.string.airpurifier_application_show_appname_text) + "_img", "header_" + userid + ".jpg");
            AC.fileMgr().getDownloadUrl(fileInfo, 0, new PayloadCallback<String>() {
                @Override
                public void success(String arg0) {
                    if (arg0 != null && !"".equals(arg0)) {
                        Picasso.with(PersonalInfoActivity.this).load(arg0).noFade().transform(new CircleTransform()).into(img);
                    } else {
                        img.setImageResource(R.drawable.img_big_avator);
                    }
                }

                @Override
                public void error(ACException arg0) {
                    img.setImageResource(R.drawable.img_big_avator);
                }
            });
        } else {
            img.setImageResource(R.drawable.img_big_avator);
        }
    }

    @Override
    public void onClick(View view) {
        try {
            Intent intent = null;
            switch (view.getId()) {
                case R.id.btn_logout:
                    new AlertDialogUserDifine(PersonalInfoActivity.this).builder()
                            .setMsg(getString(R.string.airpurifier_more_show_areyousureexit_text))
                            .setPositiveButton(getString(R.string.airpurifier_public_ok), new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    logOut();
                                }
                            })
                            .setNegativeButton(getString(R.string.airpurifier_more_show_cancel_button), new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            })
                            .show();

                    break;
                case R.id.rl_nickname:
                    intent = new Intent(PersonalInfoActivity.this, ModifyNameActivity.class);
                    //flag 0:user 1:device
                    intent.putExtra("flag", 0);
                    intent.putExtra("userName", tv_nickname.getText());
                    startActivityForResult(intent, REQUEST_CODE_MODIFY_NAME);
                    break;
                case R.id.rl_avatar:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        new RxPermissions(PersonalInfoActivity.this)
                                .request(Manifest.permission.CAMERA)
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception {
                                        if (aBoolean) {
                                            prepare();
                                        } else {
                                            ToastUtil.showToast(PersonalInfoActivity.this, getString(R.string.no_permission));
                                        }
                                    }
                                });
                    } else {
                        prepare();
                    }

                    break;
                case R.id.rl_changepwd:
                    intent = new Intent(PersonalInfoActivity.this, ChangepasswordActivity.class);
                    startActivity(intent);
                    break;
            }
        } catch (Exception e) {

        }
    }

    /**
     * 退出登录
     */
    private void logOut() {
        Completable
                .create(new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(final CompletableEmitter e) throws Exception {
                        DCPServiceUtils.logout(new PayloadCallback<ACMsg>() {
                            @Override
                            public void success(ACMsg arg0) {
                                if (!e.isDisposed()) {
                                    e.onComplete();
                                }
                            }

                            @Override
                            public void error(ACException arg0) {
                                if (!e.isDisposed()) {
                                    e.onError(arg0);
                                }
                            }
                        });
                    }
                })
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ShowToast(getString(R.string.logout_failed));
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Intent intent = new Intent();
                        intent.setClass(PersonalInfoActivity.this, LoginActivity.class);
                        startActivity(intent);
                        ConstantCache.appManager.finishAllActivity();
                    }
                })
                .observeOn(Schedulers.newThread())
                .andThen(Completable.create(new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(final CompletableEmitter e) throws Exception {
                        PushAgent mPushAgent = PushAgent.getInstance(PersonalInfoActivity.this);
                        long userId = PreferencesUtils.getLong(MainApplication.getInstance(), "uid");
                        mPushAgent.deleteAlias(userId + "", "ablecloud", new UTrack.ICallBack() {

                            @Override
                            public void onMessage(boolean isSuccess, String message) {
                                Logger.i("delete alias:" + isSuccess + ",message:" + message);
                                if (!e.isDisposed())
                                    e.onComplete();
                            }

                        });
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });


    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        try {
            switch (component) {
                case LEFT:
                    finish();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void prepare() {
        File rootDirectory = new File(Environment.getExternalStorageDirectory() + Constants.IMAGE_SAVE_PATH);
        if (rootDirectory.exists()) {
            if (rootDirectory.isDirectory())
                rootDirectory.delete();
        }
        rootDirectory.mkdirs();
        File file = new File(rootDirectory.getPath(), Constants.HEAD_IMAGE);
        imageUri = Uri.fromFile(file);
        Intent intent = new Intent(PersonalInfoActivity.this, SelectPicPopupWindow.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_MODE);
    }

    protected void viaCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(cameraIntent, Constants.REQUEST_CODE_TAKEPHOTO);
    }

    private int mode = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Constants.REQUEST_CODE_TAKEPHOTO) {
                handleTakePhotoResult();
            } else if (requestCode == Constants.REQUEST_CODE_SELECTPHOTO) {
                handleSelectPhotoResult(data);
            } else if (requestCode == Constants.REQUEST_CODE_CUTPHOTO) {
                handleCutPhotoResult(data);
            } else if (requestCode == REQUEST_CODE_SELECT_MODE) {
                mode = data.getExtras().getInt("mode");
                if (mode == 1) {
                    //拍照
                    viaCamera();
                } else {
                    //选择照片
                    viaGallery();
                }
            } else if (requestCode == REQUEST_CODE_MODIFY_NAME) {
                tv_nickname.setText(PreferencesUtils.getString(PersonalInfoActivity.this, "name", ""));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void handleSelectPhotoResult(Intent data) {
        startPhotoZoom(data.getData());
    }

    private void handleTakePhotoResult() {
        startPhotoZoom(imageUri);
    }

    private void handleCutPhotoResult(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap headImg = extras.getParcelable("data");
            //开始上传
            avatarUpload(headImg);
        }
    }

    private void avatarUpload(Bitmap headImg) {
        //加载框
        p = CustomProgressDialog.createDialog(PersonalInfoActivity.this);
        p.setMessage(getString(R.string.airpurifier_personal_loading));
        p.show();

        final ACFileMgr fileMsg = AC.fileMgr();
        ACACL acl = new ACACL();
        acl.setPublicReadAccess(true);
        acl.setUserAccess(ACACL.OpType.WRITE, userId);

        final ACFileInfo fileInfo = new ACFileInfo(getResources().getString(R.string.airpurifier_application_show_appname_text) + "_img", "header_" + String.valueOf(userId) + ".jpg");
        fileInfo.setACL(acl);


        //缩放
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        headImg = Bitmap.createBitmap(headImg, 0, 0, headImg.getWidth(),
                headImg.getHeight(), matrix, true);

        //图片质量压缩
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        headImg.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();


        fileInfo.setData(bytes);

        final Bitmap finalHeadImg = headImg;
        mAvatarUpLoad = Completable
                .create(new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(final CompletableEmitter emmiter) throws Exception {
                        fileMsg.uploadFile(fileInfo, new ProgressCallback() {
                            @Override
                            public void progress(double v) {
                                Logger.i("progress:" + v);
                            }
                        }, new VoidCallback() {
                            @Override
                            public void success() {
                                if (!emmiter.isDisposed())
                                    emmiter.onComplete();
                            }

                            @Override
                            public void error(ACException e) {
                                if (!emmiter.isDisposed())
                                    emmiter.onError(e);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        p.dismiss();
                        iv_userimg.setImageBitmap(finalHeadImg);
                        ShowToast(getString(R.string.airpurifier_more_show_uplode_success));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        p.dismiss();
                        ShowToast(getString(R.string.airpurifier_more_show_uplode_failed));
                    }
                });

    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", DensityUtil.dipToPx(this, 80));
        intent.putExtra("outputY", DensityUtil.dipToPx(this, 80));
        intent.putExtra("return-data", true);

        startActivityForResult(intent, Constants.REQUEST_CODE_CUTPHOTO);
    }

    public void viaGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
        // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, Constants.REQUEST_CODE_SELECTPHOTO);
    }

    @Override
    protected void onDestroy() {
        if (mAvatarUpLoad != null && !mAvatarUpLoad.isDisposed()) mAvatarUpLoad.dispose();
        super.onDestroy();

    }
}