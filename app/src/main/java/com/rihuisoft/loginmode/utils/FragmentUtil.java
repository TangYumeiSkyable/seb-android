package com.rihuisoft.loginmode.utils;

import android.content.ContextWrapper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.groupeseb.airpurifier.R;


/**
 * Created by liuxiaofeng on 26/05/2017.
 */

public class FragmentUtil {

    public static void replaceSupportFragment(AppCompatActivity activity, int containerId, Class<? extends Fragment> fragmentClass, String tag, boolean addToBackStack, boolean haveAnim) {
        try {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            if (haveAnim) {
                transaction.setCustomAnimations(R.anim.backstack_push_enter, R.anim.backstack_push_exit, R.anim.backstack_pop_enter, R.anim.backstack_pop_exit);
            }
            transaction.replace(containerId, fragmentClass.newInstance(), tag);
            transaction.commitAllowingStateLoss();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void replaceSupportFragment(FragmentActivity activity, int containerId, Fragment fragment, String tag, boolean addToBackStack, boolean haveAnim) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        if (haveAnim) {
            transaction.setCustomAnimations(R.anim.backstack_push_enter, R.anim.backstack_push_exit, R.anim.backstack_pop_enter, R.anim.backstack_pop_exit);
        }
        transaction.replace(containerId, fragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     * add hide show 的方式显示fragment
     */
    public static void replaceSupportFragment(FragmentActivity activity, int containerId, Fragment tagretFragment, Fragment currentFragment, String tag, boolean addToBackStack, boolean haveAnim) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        if (haveAnim) {
            transaction.setCustomAnimations(R.anim.backstack_push_enter, R.anim.backstack_push_exit, R.anim.backstack_pop_enter, R.anim.backstack_pop_exit);
        }
        if(!tagretFragment.isAdded()){
            if(currentFragment == null){
                transaction.add(containerId,tagretFragment,tag);
            }else {
                transaction.hide(currentFragment).add(containerId,tagretFragment,tag);
            }
        }else {
            transaction.hide(currentFragment).show(tagretFragment);
        }
        transaction.commitAllowingStateLoss();
    }


    /**
     * 判断当前Fragment的getActivity是否可用
     *
     * @param fragment
     * @return
     */
    public static boolean judgeGetActivityCanUse(Fragment fragment) {
        if (null != fragment) {
            FragmentActivity activity = fragment.getActivity();
            if (null != activity
                    && !activity.isFinishing()
                    && !fragment.isDetached()
                    && activity instanceof ContextWrapper) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
