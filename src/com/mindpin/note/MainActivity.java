package com.mindpin.note;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;


public class MainActivity extends FragmentActivity {
    public static int screenWidth, screeHeight;
    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new NoteListFragment())
                    .commit();
        }
//		ATManager.addActivity(this);
		// 本应用程序最高可用内存是多少
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screeHeight = displayMetrics.heightPixels;
	}

	private int screeWidth;

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	       
	     Log.v("mandy", "on activity result:" + resultCode);
	     NoteEditFragment editFragment = new NoteEditFragment();
	     editFragment.onActivityResult(requestCode, resultCode, data);
	     
	     
	     
	}*/

	private void exit() {
		new AlertDialog.Builder(this).setTitle("退出").setMessage("你确定要退出程序吗?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
//						onDestroy(); // 退出程序
					    finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	// 退出程序
	@Override
	protected void onDestroy() {
//		ATManager.exitClient(getApplicationContext());
		super.onDestroy();
	}

	@Override
	/**返回键 退出	*/
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// 如果用户按返回键了
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
