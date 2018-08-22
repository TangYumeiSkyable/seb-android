package com.rihuisoft.loginmode.utils;

import java.util.Iterator;
import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

public class AppManager {
    public static Activity context = null;

    private static Stack<Activity> activityStack;
    private static AppManager instance;

    public AppManager() {
    }

    /**
     * A single instance
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * The Activity is added to the stack
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * Gets the current Activity (stack last pressed into)
     */
    public Activity currentActivity() {
        if (activityStack == null) {
            return null;
        }
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * The last of the end of the current Activity (stack into)
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * The end of the specified Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && activityStack != null) {
            if (activityStack.contains(activity)) {
                activityStack.remove(activity);
                activity.finish();
                activity = null;

            }
        }
    }

    /**
     * End of the specified class name of the Activity
     */
    public void finishActivity(Class<?> cls) {
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
//				iterator.remove(); 
            }
        }
    }

    /**
     * End all the Activity
     */
    public void finishAllActivity() {
        try {
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                if (null != activityStack.get(i)) {
                    activityStack.get(i).finish();
                }
            }
            activityStack.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Exit the application
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }
}
