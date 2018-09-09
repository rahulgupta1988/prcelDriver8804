package com.parcelsixd.parcel.driver;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.parcelsixd.parcel.driver.gcm.CommonUtilities;
import com.parcelsixd.parcel.driver.gcm.GCMRegisterHendler;
import com.parcelsixd.parcel.driver.utills.AndyUtils;
import com.parcelsixd.parcel.driver.utills.AppLog;
import com.parcelsixd.parcel.driver.utills.PreferenceHelper;
import com.parcelsixd.parcel.driver.widget.MyFontTextView;

public class MainActivity extends Activity implements OnClickListener {

	private boolean isRecieverRegister = false;
	private static final String TAG = "FirstFragment";
	private PreferenceHelper preferenceHelper;
	// private Animation topToBottomAnimation, bottomToTopAnimation;
	private MyFontTextView tvExitOk, tvExitCancel;

	// private MyFontTextView tvMainBottomView;
	/**
	 * Called when the activity is first created.
	 */

	int PERMISSION_ALL=109;

	final static String[] PERMISSIONS = {
			android.Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.WRITE_EXTERNAL_STORAGE

	};

	public static boolean hasPermissions(Context context, String... permissions) {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
			for (String permission : permissions) {

				if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}
		return true;
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		// Window window = getWindow();
		// window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		// window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// window.setStatusBarColor(getResources().getColor(
		// R.color.color_action_bar_main));
		// }


		preferenceHelper = new PreferenceHelper(this);
		if (!TextUtils.isEmpty(preferenceHelper.getUserId())) {
			startActivity(new Intent(this, MapActivity.class));
			this.finish();
			return;
		}
		setContentView(R.layout.fragment_main_new);
		// tvMainBottomView = (MyFontTextView) mainFragmentView
		// .findViewById(R.id.tvMainBottomView);

		if (hasPermissions(this, PERMISSIONS))
		{  }
		else { ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL); }

		findViewById(R.id.btnFirstSignIn).setOnClickListener(this);
		findViewById(R.id.btnFirstRegister).setOnClickListener(this);

		if (TextUtils.isEmpty(new PreferenceHelper(MainActivity.this)
				.getDeviceToken())) {
			isRecieverRegister = true;
			registerGcmReceiver(mHandleMessageReceiver);
		} else {

			AppLog.Log(TAG, "device already registerd with :"
					+ new PreferenceHelper(MainActivity.this).getDeviceToken());
		}
	}

	private BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			AndyUtils.removeCustomProgressDialog();
			if (intent.getAction().equals(
					CommonUtilities.DISPLAY_MESSAGE_REGISTER)) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					int resultCode = bundle.getInt(CommonUtilities.RESULT);
					AppLog.Log(TAG, "Result code-----> " + resultCode);
					if (resultCode == Activity.RESULT_OK) {
						setResultCode(Activity.RESULT_OK);
					} else {
						Toast.makeText(MainActivity.this,
								getString(R.string.register_gcm_failed),
								Toast.LENGTH_SHORT).show();
						setResultCode(Activity.RESULT_CANCELED);
						finish();
					}

				}
			}
		}
	};

	public void registerGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
		if (mHandleMessageReceiver != null) {
			AndyUtils.showCustomProgressDialog(this, getResources()
					.getString(R.string.progress_loading), false);
			new GCMRegisterHendler(MainActivity.this, mHandleMessageReceiver);

		}
	}

	public void unregisterGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
		if (mHandleMessageReceiver != null) {

			if (mHandleMessageReceiver != null) {
				unregisterReceiver(mHandleMessageReceiver);
			}

		}

	}

	@Override
	public void onClick(View v) {

		Intent startRegisterActivity = new Intent(MainActivity.this,
				RegisterActivity.class);
		switch (v.getId()) {

		case R.id.btnFirstRegister:
			if (hasPermissions(this, PERMISSIONS))
				{
					if (!AndyUtils.isNetworkAvailable(MainActivity.this)) {
					     AndyUtils.showToast(getResources().getString(R.string.toast_no_internet),
							MainActivity.this);
					return;
				   }

				}
				else {
				      ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
			          return;
			      }

			      startRegisterActivity.putExtra("isSignin", false);

			break;

		case R.id.btnFirstSignIn:


			if (hasPermissions(this, PERMISSIONS))
			{
				if (!AndyUtils.isNetworkAvailable(MainActivity.this)) {
					AndyUtils.showToast(getResources().getString(R.string.toast_no_internet),
							MainActivity.this);
					return;
				}

			}
			else {
				ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
				return;
			}
			startRegisterActivity.putExtra("isSignin", true);
			break;

		default:
			break;
		}
		startActivity(startRegisterActivity);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();

	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		if (isRecieverRegister) {
			unregisterGcmReceiver(mHandleMessageReceiver);
			isRecieverRegister = false;
		}

	}

	@Override
	public void onBackPressed() {
		openExitDialog();
	}

	public void openExitDialog() {
		final Dialog dialog = new Dialog(this);
		Window window = dialog.getWindow();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(true);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.lgout_confirm_dailog);
		window.setType(WindowManager.LayoutParams.FIRST_SUB_WINDOW);
		window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
		window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		MyFontTextView msg_yesno=(MyFontTextView) dialog
				.findViewById(R.id.msg_yesno);
		msg_yesno.setText("Are you sure,  You want to Exit?");
		tvExitOk = (MyFontTextView) dialog
				.findViewById(R.id.tvLogoutOk);
		tvExitCancel = (MyFontTextView) dialog
				.findViewById(R.id.tvLogoutCancel);


		tvExitOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				finish();
			}
		});
		tvExitCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

}
