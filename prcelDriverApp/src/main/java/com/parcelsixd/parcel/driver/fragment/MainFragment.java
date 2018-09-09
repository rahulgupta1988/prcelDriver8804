package com.parcelsixd.parcel.driver.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parcelsixd.parcel.driver.R;
import com.parcelsixd.parcel.driver.base.BaseRegisterFragment;
import com.parcelsixd.parcel.driver.gcm.CommonUtilities;
import com.parcelsixd.parcel.driver.utills.AndyConstants;
import com.parcelsixd.parcel.driver.utills.AndyUtils;
import com.parcelsixd.parcel.driver.utills.AppLog;
import com.parcelsixd.parcel.driver.utills.PreferenceHelper;

/**
 * @author Elluminati elluminati.in
 * 
 */
public class MainFragment extends BaseRegisterFragment implements
		OnClickListener {
	private boolean isRecieverRegister = false;
	private static final String TAG = "FirstFragment";

	// private Animation topToBottomAnimation, bottomToTopAnimation;

	// private MyFontTextView tvMainBottomView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getHasForFacebook();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mainFragmentView = inflater.inflate(R.layout.fragment_main,
				container, false);

		// tvMainBottomView = (MyFontTextView) mainFragmentView
		// .findViewById(R.id.tvMainBottomView);

		mainFragmentView.findViewById(R.id.btnFirstSignIn).setOnClickListener(
				this);
		mainFragmentView.findViewById(R.id.btnFirstRegister)
				.setOnClickListener(this);
		// getActivity().getActionBar().hide();
		registerActivity.actionBar.hide();
		return mainFragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		registerActivity.setActionBarTitle(getResources().getString(
				R.string.app_name));

		if (TextUtils.isEmpty(new PreferenceHelper(registerActivity)
				.getDeviceToken())) {
			isRecieverRegister = true;
			registerActivity.registerGcmReceiver(mHandleMessageReceiver);
		} else {

			AppLog.Log(TAG, "device already registerd with :"
					+ new PreferenceHelper(registerActivity).getDeviceToken());
		}

		// topToBottomAnimation = AnimationUtils.loadAnimation(registerActivity,
		// R.anim.top_bottom);
		// rlLoginRegisterLayout.setAnimation(topToBottomAnimation);
		// rlLoginRegisterLayout.startAnimation(topToBottomAnimation);
		// bottomToTopAnimation = AnimationUtils.loadAnimation(registerActivity,
		// R.anim.bottom_top);
		// tvMainBottomView.setAnimation(bottomToTopAnimation);
		// tvMainBottomView.startAnimation(bottomToTopAnimation);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btnFirstRegister:
			if (!AndyUtils.isNetworkAvailable(registerActivity)) {
				AndyUtils.showToast(
						getResources().getString(R.string.toast_no_internet),
						registerActivity);
				return;
			}
			registerActivity.addFragment(new RegisterFragment_New(), true,
					AndyConstants.REGISTER_FRAGMENT_TAG, false);
			break;

		case R.id.btnFirstSignIn:
			registerActivity.addFragment(new LoginFragment(), true,
					AndyConstants.LOGIN_FRAGMENT_TAG, false);
			break;

		default:
			break;
		}
	}

	// private void getHasForFacebook() {
	// try {
	// PackageInfo info = registerActivity.getPackageManager()
	// .getPackageInfo(registerActivity.getPackageName(),
	// PackageManager.GET_SIGNATURES);
	// for (Signature signature : info.signatures) {
	// MessageDigest md = MessageDigest.getInstance("SHA");
	// md.update(signature.toByteArray());
	// AppLog.Log(TAG,
	// Base64.encodeToString(md.digest(), Base64.DEFAULT));
	// }
	// } catch (NameNotFoundException e) {
	// e.printStackTrace();
	// } catch (NoSuchAlgorithmException e) {
	// e.printStackTrace();
	// }
	// }

	@Override
	public void onDestroy() {
		if (isRecieverRegister) {
			registerActivity.unregisterGcmReceiver(mHandleMessageReceiver);
			isRecieverRegister = false;
		}
		super.onDestroy();
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

					} else {
						Toast.makeText(
								registerActivity,
								registerActivity.getResources().getString(
										R.string.register_gcm_failed),
								Toast.LENGTH_SHORT).show();
						registerActivity.finish();
					}

				}
			}
		}
	};

}
