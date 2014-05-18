package com.mindpin.note;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

public class ATManager {
	private static List<Activity> activityList = new LinkedList<Activity>();

	public static void addActivity(Activity activity) {
		activityList.add(activity);
		System.out.println("ActivityManager.addActivity|activity=" + activity
				+ ",activityList size=" + activityList.size());
	}

	public static void exitClient(Context context) {
		for (int i = 0; i < activityList.size(); i++) {
			if (null != activityList.get(i)) {
				activityList.get(i).finish();
			}
		}
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		int version = 0;

		if (Build.VERSION.SDK != null) {
			version = Integer.parseInt(Build.VERSION.SDK);
		}

		if (version < 8) {
			activityManager.restartPackage("com.yizhao.activity");
			System.exit(0);
		} else {
			activityManager.killBackgroundProcesses("com.yizhao.activity");
			System.exit(0);
		}
	}
}