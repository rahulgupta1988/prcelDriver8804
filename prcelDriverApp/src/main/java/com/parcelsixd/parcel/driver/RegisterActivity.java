package com.parcelsixd.parcel.driver;

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
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.parcelsixd.parcel.driver.base.ActionBarBaseActivitiy;
import com.parcelsixd.parcel.driver.fragment.LoginFragment;
import com.parcelsixd.parcel.driver.fragment.RegisterFragment_New;
import com.parcelsixd.parcel.driver.gcm.CommonUtilities;
import com.parcelsixd.parcel.driver.gcm.GCMRegisterHendler;
import com.parcelsixd.parcel.driver.utills.AndyConstants;
import com.parcelsixd.parcel.driver.utills.AndyUtils;
import com.parcelsixd.parcel.driver.utills.AppLog;
import com.parcelsixd.parcel.driver.utills.PreferenceHelper;
import com.parcelsixd.parcel.driver.widget.MyFontTextView;

/**
 * @author Elluminati elluminati.in
 * 
 */
public class RegisterActivity extends ActionBarBaseActivitiy {
	public ActionBar actionBar;
	private PreferenceHelper preferenceHelper;
	private boolean isRecieverRegister = false;
	int PERMISSION_ALL=109;

	boolean isRegisterFragment=false;

	final static String[] PERMISSIONS = {
			android.Manifest.permission.ACCESS_FINE_LOCATION,
			android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
			android.Manifest.permission.CAMERA

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		actionBar = getSupportActionBar();

		preferenceHelper = new PreferenceHelper(this);
		if (!TextUtils.isEmpty(preferenceHelper.getUserId())) {
			startActivity(new Intent(this, MapActivity.class));
			this.finish();
			return;
		}


		if (TextUtils.isEmpty(new PreferenceHelper(this)
				.getDeviceToken())) {
			isRecieverRegister = true;
			registerGcmReceiver(mHandleMessageReceiver);
		} else {

			AppLog.Log("", "device already registerd with :"
					+ new PreferenceHelper(this).getDeviceToken());
		}


		if (hasPermissions(this, PERMISSIONS))
		{  }
		else { ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL); }

		// addFragment(new UberMainFragment(), false,
		// AndyConstants.MAIN_FRAGMENT_TAG);
		if (getIntent().getBooleanExtra("isSignin", false)) {
			isRegisterFragment=false;
			addFragment(new LoginFragment(), true,
					AndyConstants.LOGIN_FRAGMENT_TAG, true);
		} else {
			isRegisterFragment=true;
			addFragment(new RegisterFragment_New(), true,
					AndyConstants.REGISTER_FRAGMENT_TAG, true);
		}
	}

	public void goToRegister(){
		isRegisterFragment=true;
		addFragment(new RegisterFragment_New(), true,
				AndyConstants.REGISTER_FRAGMENT_TAG, true);
	}

	public void goToLogin(){
		isRegisterFragment=false;
		addFragment(new LoginFragment(), true,
				AndyConstants.LOGIN_FRAGMENT_TAG, true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnActionNotification:
			onBackPressed();
			break;

		default:
			break;
		}

	}







	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onBackPressed() {

		Fragment signinFragment = getSupportFragmentManager()
				.findFragmentByTag(AndyConstants.LOGIN_FRAGMENT_TAG);
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(
				AndyConstants.REGISTER_FRAGMENT_TAG);


		//super.onBackPressed();
		/*if (fragment != null && fragment.isVisible()) {

			goToMainActivity();
		} else if (signinFragment != null && signinFragment.isVisible()) {
			goToMainActivity();
		} else {
			super.onBackPressed();

		}*/

		   if(isRegisterFragment) {
			    isRegisterFragment=false;
				goToLogin();
			}

			else{
				openExitDialog();
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
					AppLog.Log("device code", "Result code-----> " + resultCode);
					if (resultCode == Activity.RESULT_OK) {
						setResultCode(Activity.RESULT_OK);
					} else {
						Toast.makeText(getApplicationContext(),
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
			new GCMRegisterHendler(this, mHandleMessageReceiver);

		}
	}

	public void unregisterGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
		if (mHandleMessageReceiver != null) {

			if (mHandleMessageReceiver != null) {
				unregisterReceiver(mHandleMessageReceiver);
			}

		}

	}

	private MyFontTextView tvExitOk, tvExitCancel;
	public void openExitDialog() {
		final Dialog dialog = new Dialog(this);
		Window window = dialog.getWindow();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(true);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.lgout_confirm_dailog);
		window.setType(WindowManager.LayoutParams.FIRST_SUB_WINDOW);
		window.setLayout(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
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
