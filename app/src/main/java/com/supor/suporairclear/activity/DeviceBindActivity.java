package com.supor.suporairclear.activity;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.view.NoScrollViewPager;
import com.supor.suporairclear.common.adapter.BindFragmentAdapter;
import com.supor.suporairclear.common.fragment.DeviceBindFragment;
import com.supor.suporairclear.common.fragment.DevicebindCallback;
import com.supor.suporairclear.common.fragment.ModelFragment;
import com.supor.suporairclear.common.fragment.WifiConfFragment;
import com.supor.suporairclear.config.AppConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enyva on 16/5/28.
 */
public class DeviceBindActivity extends FragmentActivity implements DevicebindCallback {
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private BindFragmentAdapter mFragmentAdapter;
    private NoScrollViewPager mPageVp;
    /**
     * The Tab display content TextView
     */
    private TextView mTabModelTv, mTabWifiConfTv, mTabDeviceBindTv;

    /**
     * The guide line of the Tab
     */
    private ImageView mTabLineIv;

    /**
     * Fragment1 Product selection
     */
//    private ProductFragment mProductFg;

    /**
     * Fragment1 Model selection
     */
    private ModelFragment mModelFg;

    /**
     * Fragment wifisetting
     */
    private WifiConfFragment mWifiConfFg;

    /**
     * Fragment Equipment binding
     */
    private DeviceBindFragment mDeviceBindFg;


    /**
         * ViewPager the currently selected page
     */
    private int currentIndex;

    /**
     * The width of the screen
     */
    private int screenWidth;
    private TextView header_title;
    private TextView header_left_btn;

    private String subDomainId="",name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	try {
    		super.onCreate(savedInstanceState);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.dark_blue));
                window.setNavigationBarColor(getResources().getColor(R.color.dark_blue));
            }
            setContentView(R.layout.activity_devicebind);
            AppManager.getAppManager().addActivity(this);
            findById();
            init();
            initTabLineWidth();

    	} catch(Exception e) {
			e.printStackTrace();
		}
        

    }

    /**
     * Controls the initialization
     */
    private void findById() {
    	try {
//    		mTabProductTv = (TextView) this.findViewById(R.id.tv_product);
            mTabModelTv = (TextView) this.findViewById(R.id.tv_model);
            mTabWifiConfTv = (TextView) this.findViewById(R.id.tv_wifi);
            mTabDeviceBindTv = (TextView) this.findViewById(R.id.tv_bind);
            
//            mTabProductTv.setTypeface(AppConstant.pfMedium);
            mTabModelTv.setTypeface(AppConstant.ubuntuRegular);
            mTabWifiConfTv.setTypeface(AppConstant.ubuntuLight);
            mTabDeviceBindTv.setTypeface(AppConstant.ubuntuLight);

            header_title = (TextView) this.findViewById(R.id.header_title);
            header_title.setText(R.string.airpurifier_moredevice_show_adddevice_text);
            header_left_btn = (TextView) this.findViewById(R.id.header_left_btn);
            header_left_btn.setBackgroundResource(R.drawable.ico_back);
            header_left_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            mTabLineIv = (ImageView) this.findViewById(R.id.iv_tab_line);

            mPageVp = (NoScrollViewPager) this.findViewById(R.id.vp_page);
            mPageVp.setScanScroll(false);
    	} catch(Exception e) {
			e.printStackTrace();
		}
        
    }

    /**
     * Data initialization
     */
    private void init() {
    	try {
//    		    mProductFg = new ProductFragment();
    	        mModelFg = new ModelFragment();
    	        mWifiConfFg = new WifiConfFragment();
    	        mDeviceBindFg = new DeviceBindFragment();

//    	        mFragmentList.add(mProductFg);
    	        mFragmentList.add(mModelFg);
    	        mFragmentList.add(mWifiConfFg);
    	        mFragmentList.add(mDeviceBindFg);


    	        mFragmentAdapter = new BindFragmentAdapter(
    	                this.getSupportFragmentManager(), mFragmentList);
    	        mPageVp.setAdapter(mFragmentAdapter);
    	        mPageVp.setCurrentItem(0);

    	        mPageVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

    	            /**
    	             * The state of the sliding state Has three states (0) : 1 is sliding 2: sliding over 0: didn't do anything。
    	             */
    	            @Override
    	            public void onPageScrollStateChanged(int state) {

    	            }

    	            /**
    	             * position :The current page, and the page you click on the slide
    	             * offset:The percentage of the current page migration
    	             * offsetPixels:The current page offset pixel location
    	             */
    	            @Override
    	            public void onPageScrolled(int position, float offset,
    	                                       int offsetPixels) {
    	            	try {
    	            		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
        	                        .getLayoutParams();

        	                Log.e("offset:", offset + "");
        	                /**
        	                 * Using currentIndex (the current page) and position (the next page), and offset
        	                 * Set the mTabLineIv from sliding to the left of the scene:
        	                 * Write three pages,
        	                 * From left to right are 0, respectively
        	                 * 0->1; 1->2; 2->1; 1->0
        	                 */
        	                if (currentIndex == 0 && position == 0)// 0->1
                            {
                                lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex
                                        * (screenWidth / 3));

                            } else if (currentIndex == 1 && position == 0) // 1->0
                            {
                                lp.leftMargin = (int) (-(1 - offset)
                                        * (screenWidth * 1.0 / 3) + currentIndex
                                        * (screenWidth / 3));

                            } else if (currentIndex == 1 && position == 1) // 1->2
                            {
                                lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex
                                        * (screenWidth / 3) - 6);
                            } else if (currentIndex == 2 && position == 1) // 2->1
                            {
                                lp.leftMargin = (int) (-(1 - offset)
                                        * (screenWidth * 1.0 / 3) + currentIndex
                                        * (screenWidth / 3));
                            }
                            mTabLineIv.setLayoutParams(lp);
    	            	}  catch(Exception e) {
    	        			e.printStackTrace();
    	        		}
    	                
    	            }

    	            @Override
    	            public void onPageSelected(int position) {
    	            	try {
    	            		 resetTextView();
    	    	                switch (position) {
//    	    	                    case 0:
//    	    	                        mTabProductTv.setTextColor(getApplicationContext().getResources().getColor(R.color.default_orange));
//    	    	                        break;
    	    	                    case 0:
                                        mTabModelTv.setTypeface(AppConstant.ubuntuRegular);
    	    	                        mTabModelTv.setTextColor(getApplicationContext().getResources().getColor(R.color.default_blue));
    	    	                        break;
    	    	                    case 1:
                                        mTabWifiConfTv.setTypeface(AppConstant.ubuntuRegular);
    	    	                        mTabWifiConfTv.setTextColor(getApplicationContext().getResources().getColor(R.color.default_blue));
    	    	                        break;
    	    	                    case 2:
                                        mTabDeviceBindTv.setTypeface(AppConstant.ubuntuRegular);
    	    	                        mTabDeviceBindTv.setTextColor(getApplicationContext().getResources().getColor(R.color.default_blue));
    	    	                        break;
    	    	                }
    	    	                currentIndex = position;
    	            	} catch(Exception e) {
    	        			e.printStackTrace();
    	        		}
    	               
    	            }
    	        });
    	} catch(Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * Set the width of the slider to a third of the screen (depending on the number of Tab)
     */
    private void initTabLineWidth() {
    	try {
    		DisplayMetrics dpMetrics = new DisplayMetrics();
            getWindow().getWindowManager().getDefaultDisplay()
                    .getMetrics(dpMetrics);
            screenWidth = dpMetrics.widthPixels;
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                    .getLayoutParams();
            lp.width = (screenWidth - 10) / 3;
            mTabLineIv.setLayoutParams(lp);
    	} catch(Exception e) {
			e.printStackTrace();
		}
        
    }

    /**
     * Reset the color
     */
    private void resetTextView() {
    	try {
//    		 mTabProductTv.setTextColor(getApplicationContext().getResources().getColor(R.color.gray_tab));
    	        mTabModelTv.setTextColor(getApplicationContext().getResources().getColor(R.color.gray_tab));
    	        mTabWifiConfTv.setTextColor(getApplicationContext().getResources().getColor(R.color.gray_tab));
    	        mTabDeviceBindTv.setTextColor(getApplicationContext().getResources().getColor(R.color.gray_tab));
            mTabModelTv.setTypeface(AppConstant.ubuntuLight);
            mTabWifiConfTv.setTypeface(AppConstant.ubuntuLight);
            mTabDeviceBindTv.setTypeface(AppConstant.ubuntuLight);
    	} catch(Exception e) {
			e.printStackTrace();
		}
       
    }

    @Override
    public void callbackFun1(Bundle arg) {
//        if(null == mModelFg){//可以避免切换的时候重复创建
//            mModelFg = new ModelFragment();
//        }
//        Bundle data = new Bundle();
//        data.putString("TEXT", "传递给fragment2");
//        mModelFg.setArguments(data);
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
//        fragmentTransaction.add(R.id.rl_container,fragment2);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commitAllowingStateLoss();
//        currentFragment = fragment2;
        mPageVp.setCurrentItem(0);

    }

    @Override
    public void callbackFun2(Bundle arg) {
        subDomainId=arg.getString("subDomainId");
        name=arg.getString("name");
        mPageVp.setCurrentItem(1);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        final LinearLayout lv_doc = (LinearLayout) findViewById(R.id.lv_doc);
        final TextView et_pwd = (TextView) findViewById(R.id.et_pwd);
    	
    	Rect outRect = new Rect();
    	getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lv_doc.getLayoutParams();
        params.height = outRect.bottom - outRect.top;
        
        lv_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	try {
        			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        			imm.hideSoftInputFromWindow(et_pwd.getWindowToken(), 0);       		       			
        		}  catch (Exception e) {
            		e.printStackTrace();
            	}
            }
        });
    }
    @Override
    public void callbackFun3(Bundle arg) {
        mPageVp.setCurrentItem(2);
        mDeviceBindFg.init(subDomainId,name);
    }

    @Override
    public void callbackCapture(Bundle arg) {

    }
}

