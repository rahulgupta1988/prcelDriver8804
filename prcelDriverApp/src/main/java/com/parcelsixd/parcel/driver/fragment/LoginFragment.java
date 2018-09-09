package com.parcelsixd.parcel.driver.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dpizarro.pinview.library.PinView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.Plus.PlusOptions;
import com.google.android.gms.plus.model.people.Person;
import com.parcelsixd.parcel.driver.MapActivity;
import com.parcelsixd.parcel.driver.R;
import com.parcelsixd.parcel.driver.base.BaseRegisterFragment;
import com.parcelsixd.parcel.driver.parse.AsyncTaskCompleteListener;
import com.parcelsixd.parcel.driver.parse.HttpRequester;
import com.parcelsixd.parcel.driver.parse.ParseContent;
import com.parcelsixd.parcel.driver.utills.AndyConstants;
import com.parcelsixd.parcel.driver.utills.AndyUtils;
import com.parcelsixd.parcel.driver.utills.AppLog;
import com.parcelsixd.parcel.driver.utills.PreferenceHelper;
import com.parcelsixd.parcel.driver.widget.MyFontEdittextView;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class LoginFragment extends BaseRegisterFragment implements
		OnClickListener, ConnectionCallbacks, OnConnectionFailedListener,
		AsyncTaskCompleteListener {
	private MyFontEdittextView etLoginEmail, etLoginPassword, etForgetEmail;
	private ImageButton btnFb, btnGplus;
	private ConnectionResult mConnectionResult;
	private GoogleApiClient mGoogleApiClient;
	private SimpleFacebook mSimpleFacebook;
	private SimpleFacebookConfiguration facebookConfiguration;
	private ParseContent parseContent;
	private boolean mSignInClicked, mIntentInProgress;
	private final String TAG = "LoginFragment";
	private static final int RC_SIGN_IN = 0;
	private Dialog forgotPasswordDialog;

	Permission[] facebookPermissions = new Permission[] { Permission.EMAIL };

	int PERMISSION_ALL=109;


	final static String[] PERMISSIONS = {
			android.Manifest.permission.ACCESS_FINE_LOCATION,
			android.Manifest.permission.WRITE_EXTERNAL_STORAGE

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		View loginFragmentView = inflater.inflate(R.layout.fragment_login_new,
				container, false);
		etLoginEmail = (MyFontEdittextView) loginFragmentView
				.findViewById(R.id.etLoginEmail);
		etLoginPassword = (MyFontEdittextView) loginFragmentView
				.findViewById(R.id.etLoginPassword);
		btnFb = (ImageButton) loginFragmentView.findViewById(R.id.btnLoginFb);
		btnGplus = (ImageButton) loginFragmentView
				.findViewById(R.id.btnLoginGplus);

		loginFragmentView.findViewById(R.id.tvLoginForgetPassword)
				.setOnClickListener(this);
		loginFragmentView.findViewById(R.id.tvLoginSignin).setOnClickListener(
				this);
		loginFragmentView.findViewById(R.id.btnBackSignIn).setOnClickListener(
				this);
		preferenceHelper = new PreferenceHelper(getContext());

		return loginFragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerActivity.actionBar.hide();
		// registerActivity.setStatusBarColor(getResources().getColor(
		// R.color.color_action_bar_main));
		// registerActivity.setActionBarTitle(getResources().getString(
		// R.string.text_signin));
		registerActivity.btnActionInfo.setVisibility(View.INVISIBLE);
		// registerActivity.setActionBarIcon(R.drawable.taxi);
		parseContent = new ParseContent(registerActivity);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// facebook api initialization
		/*facebookConfiguration = new SimpleFacebookConfiguration.Builder()
				.setAppId(getResources().getString(R.string.app_id))
				.setNamespace(getResources().getString(R.string.app_name))
				.setPermissions(facebookPermissions).build();
		SimpleFacebook.setConfiguration(facebookConfiguration);*/

		// Google plus api initialization
		Scope scope = new Scope(AndyConstants.GOOGLE_API_SCOPE_URL);
		mGoogleApiClient = new GoogleApiClient.Builder(registerActivity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API, PlusOptions.builder().build())
				.addScope(scope).build();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLoginGplus:
			mSignInClicked = true;
			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
			break;
		case R.id.btnLoginFb:
			if (!mSimpleFacebook.isLogin()) {

				registerActivity.setFbTag(AndyConstants.LOGIN_FRAGMENT_TAG);
				mSimpleFacebook.login(new OnLoginListener() {
                    @Override
                    public void onException(Throwable throwable) {
                        Log.d("tag", "onException");
                    }

                    @Override
                    public void onFail(String reason) {
                        Toast.makeText(registerActivity, getString(R.string.error_facebook_login_failed),
                                Toast.LENGTH_SHORT).show();
                        Log.d("tag", "onFail");
                    }

                    @Override
                    public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
                        Toast.makeText(registerActivity, getString(R.string.text_success), Toast.LENGTH_SHORT)
                                .show();
                        Log.d("tag", "onLogin");
                    }

                    @Override
                    public void onCancel() {

                    }

				});
			} else {
				getFbProfile();
			}
			break;

		case R.id.tvLoginForgetPassword:
			showForgotPasswordDialog();
			// registerActivity.addFragment(new ForgetPasswordFragment(), true,
			// AndyConstants.FOREGETPASS_FRAGMENT_TAG, true);
			break;

		case R.id.tvLoginSignin:


			if (hasPermissions(registerActivity, PERMISSIONS))
			{
				if (!AndyUtils.isNetworkAvailable(registerActivity)) {
					AndyUtils.showToast(getResources().getString(R.string.toast_no_internet),
							registerActivity);
					return;
				}

			}
			else {
				ActivityCompat.requestPermissions(registerActivity, PERMISSIONS, PERMISSION_ALL);
				return;
			}


			if (etLoginEmail.getText().length() == 0) {
				AndyUtils.showToast(
						getResources().getString(R.string.error_phone_no),
						registerActivity);
				return;
			} /*else if (!AndyUtils.eMailValidation(etLoginEmail.getText()
					.toString())) {
				AndyUtils.showToast(
						getResources().getString(R.string.error_valid_email),
						registerActivity);
				return;
			} */else if (etLoginPassword.getText().length() == 0) {
				AndyUtils
						.showToast(
								getResources().getString(
										R.string.error_empty_password),
								registerActivity);
				return;
			} else {
				login();
			}

			break;

		case R.id.btnBackSignIn:

			if (hasPermissions(registerActivity, PERMISSIONS))
			{
				if (!AndyUtils.isNetworkAvailable(registerActivity)) {
					AndyUtils.showToast(getResources().getString(R.string.toast_no_internet),
							registerActivity);
					return;
				}

			}
			else {
				ActivityCompat.requestPermissions(registerActivity, PERMISSIONS, PERMISSION_ALL);
				return;
			}


			registerActivity.goToRegister();
			break;

		/*case R.id.tvForgetSubmit:
			if (etForgetEmail.getText().length() == 0) {
				AndyUtils.showToast(
						getResources().getString(R.string.error_empty_email),
						registerActivity);
				return;
			} else if (!AndyUtils.eMailValidation(etForgetEmail.getText()
					.toString())) {
				AndyUtils.showToast(
						getResources().getString(R.string.error_valid_email),
						registerActivity);
				return;
			} else {
				if (!AndyUtils.isNetworkAvailable(registerActivity)) {
					AndyUtils.showToast(
							getResources()
									.getString(R.string.toast_no_internet),
							registerActivity);
					return;
				}
				forgetPassowrd();
			}
			break;*/

			case R.id.tvSignin:
				if (etPhone.getText().length() == 0) {
					AndyUtils.showToast(
							getResources().getString(R.string.error_phone_no),
							registerActivity);
					return;
				} else {
					if (!AndyUtils.isNetworkAvailable(registerActivity)) {
						AndyUtils.showToast(
								getResources()
										.getString(R.string.toast_no_internet),
								registerActivity);
						return;
					}
					forgetPassowrd();
				}
				break;


			default:
			break;
		}
	}

	private void login() {
		if (!AndyUtils.isNetworkAvailable(registerActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					registerActivity);
			return;
		}
		AndyUtils.showCustomProgressDialog(registerActivity, getResources()
				.getString(R.string.progress_dialog_sign_in), false);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, AndyConstants.ServiceType.LOGIN);
		map.put(AndyConstants.Params.PHONE_LOGIN, etLoginEmail.getText().toString());
		map.put(AndyConstants.Params.PASSWORD, etLoginPassword.getText()
				.toString());

		map.put("latitude","0.0");
		map.put("longitude", "0.0");

		map.put(AndyConstants.Params.DEVICE_TYPE,
				AndyConstants.DEVICE_TYPE_ANDROID);
		map.put(AndyConstants.Params.DEVICE_TOKEN, new PreferenceHelper(
				registerActivity).getDeviceToken());
		map.put(AndyConstants.Params.LOGIN_BY, AndyConstants.MANUAL);

		new HttpRequester(registerActivity, map,
				AndyConstants.ServiceCode.LOGIN, this);
		// requestQueue.add(new VolleyHttpRequest(Method.POST, map,
		// AndyConstants.ServiceCode.LOGIN, this, this));
	}

	private void loginSocial(String id, String loginType) {
		if (!AndyUtils.isNetworkAvailable(registerActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					registerActivity);
			return;
		}

		AndyUtils.showCustomProgressDialog(registerActivity, getResources()
				.getString(R.string.progress_dialog_sign_in), false);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, AndyConstants.ServiceType.LOGIN);
		map.put(AndyConstants.Params.SOCIAL_UNIQUE_ID, id);
		map.put(AndyConstants.Params.DEVICE_TYPE,
				AndyConstants.DEVICE_TYPE_ANDROID);
		map.put(AndyConstants.Params.DEVICE_TOKEN, new PreferenceHelper(
				registerActivity).getDeviceToken());
		map.put(AndyConstants.Params.LOGIN_BY, loginType);

		new HttpRequester(registerActivity, map,
				AndyConstants.ServiceCode.LOGIN, this);

		// requestQueue.add(new VolleyHttpRequest(Method.POST, map,
		// AndyConstants.ServiceCode.LOGIN, this, this));
	}

	private void getFbProfile() {
		AndyUtils.showCustomProgressDialog(registerActivity,
				getString(R.string.text_getting_info_facebook), true);
		mSimpleFacebook.getProfile(new OnProfileListener() {
			@Override
			public void onComplete(Profile profile) {
				// AndyUtils.removeSimpleProgressDialog();
				Log.i("Uber", "My profile id = " + profile.getId());
				btnFb.setEnabled(false);
				btnGplus.setEnabled(false);
				AndyUtils.removeCustomProgressDialog();
				loginSocial(profile.getId(), AndyConstants.SOCIAL_FACEBOOK);
			}
		});
	}

	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				registerActivity.startIntentSenderForResult(mConnectionResult
						.getResolution().getIntentSender(), RC_SIGN_IN, null,
						0, 0, 0, AndyConstants.LOGIN_FRAGMENT_TAG);
			} catch (SendIntentException e) {
				// The intent was canceled before it was sent. Return to the
				// default
				// state and attempt to connect to get an updated
				// ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the
			// user clicks
			// 'sign-in'.
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {

			if (resultCode != Activity.RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		} else {
			AppLog.Log("TAG", "on activity result facebook");
			mSimpleFacebook.onActivityResult(requestCode,
					resultCode, data);
			if (mSimpleFacebook.isLogin()) {
				getFbProfile();
			} else {
				Toast.makeText(
						registerActivity,
						getResources().getString(
								R.string.toast_facebook_login_failed),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		// String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
		Person currentPerson = Plus.PeopleApi
				.getCurrentPerson(mGoogleApiClient);

		// String personName = currentPerson.getDisplayName();
		// String personPhoto = currentPerson.getImage().toString();
		// String personGooglePlusProfile = currentPerson.getUrl();
		// Toast.makeText(
		// registerActivity,
		// "email: " + email + "\nName:" + personName + "\n Profile URL:"
		// + personGooglePlusProfile + "\nPhoto:" + personPhoto
		// + "\nBirthday:" + currentPerson.getBirthday()
		// + "\n GENDER: " + currentPerson.getGender(),
		// Toast.LENGTH_LONG).show();
		btnGplus.setEnabled(false);
		btnFb.setEnabled(false);
		AndyUtils.removeCustomProgressDialog();
		loginSocial(currentPerson.getId(), AndyConstants.SOCIAL_GOOGLE);

	}

	@Override
	public void onConnectionSuspended(int arg0) {

	}

	@Override
	public void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		registerActivity.currentFragment = AndyConstants.LOGIN_FRAGMENT_TAG;
//		mSimpleFacebook = SimpleFacebook.getInstance(registerActivity);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		AndyUtils.removeCustomProgressDialog();
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		AndyUtils.removeCustomProgressDialog();
		AppLog.Log(TAG, response);
		switch (serviceCode) {
		case AndyConstants.ServiceCode.LOGIN:
			if (!parseContent.isSuccess(response)) {
				return;
			}
			if (parseContent.isSuccessWithId(response)) {
				parseContent.parseUserAndStoreToDb(response);
				new PreferenceHelper(getActivity()).putPassword(etLoginPassword
						.getText().toString());

				try {
					JSONObject jsonObject = new JSONObject(response);
					if(jsonObject.getString(AndyConstants.Params.OTP_VERFIED).equals("0")){
						AndyUtils.showToast("Number not verify.",getContext());
						showOTPdDialog();
					}

					else{
						AndyUtils.showToast("LoggedIn Successfully.",getContext());
						startActivity(new Intent(registerActivity, MapActivity.class));
						registerActivity.finish();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}


			}
			break;
		case AndyConstants.ServiceCode.FORGET_PASSWORD:
			AppLog.Log("TAG", "forget res:" + response);
			if (new ParseContent(registerActivity).isSuccess(response)) {

				try {
					JSONObject jsonObject = new JSONObject(response);


					AndyUtils.showToast(
							getResources().getString(
									R.string.toast_forget_password_success) +" "+jsonObject.getString("password") ,
							registerActivity);


				} catch (Exception e) {
					e.printStackTrace();
				}



				forgotPasswordDialog.dismiss();

			}
			break;

			case AndyConstants.ServiceCode.REQUEST_OTP:
				AndyUtils.removeCustomProgressDialog();
				AppLog.Log("otp response", "otp response :" + response);

				if (parseContent.isSuccess(response)) {


					try {
						JSONObject jsonObject = new JSONObject(response);


						AndyUtils.showToast(
								getResources().getString(
										R.string.otp_success)+"   "+jsonObject.getString("otp"),
								registerActivity);




					} catch (Exception e) {
						e.printStackTrace();
					}





					tvbtnotp.setVisibility(View.GONE);
					tvbtnotp_sub.setVisibility(View.VISIBLE);
					tvotp_txt.setVisibility(View.VISIBLE);
					otp_lay.setVisibility(View.VISIBLE);





				}

				break;

			case AndyConstants.ServiceCode.VERIFY_OTP:
				AndyUtils.removeCustomProgressDialog();
				AppLog.Log("otp response", "otp response :" + response);

				if (parseContent.isSuccess(response)) {

					AndyUtils.showToast(
							getResources().getString(
									R.string.verify_otp_success),
							registerActivity);
					otpDialog.dismiss();

					AndyUtils.showToast("LogedIn Successfully.",getContext());
					/*startActivity(new Intent(registerActivity, MapActivity.class));
					registerActivity.finish();*/

				}
				break;

		default:
			break;
		}

	}

	ImageView back_btn;
	EditText etPhone;
	Button tvSignin;

	private void showForgotPasswordDialog() {

		forgotPasswordDialog = new Dialog(getActivity(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		forgotPasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	/*	forgotPasswordDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));*/
		//forgotPasswordDialog.setContentView(R.layout.fragment_forgetpassword);
		forgotPasswordDialog.setContentView(R.layout.reset_pass_lay);
		forgotPasswordDialog.setCancelable(false);
		/*etForgetEmail = (MyFontEdittextView) forgotPasswordDialog
				.findViewById(R.id.etForgetEmail);
		forgotPasswordDialog.findViewById(R.id.tvForgetSubmit)
				.setOnClickListener(this);
		etForgetEmail.requestFocus();
		showKeyboard(etForgetEmail);*/

		forgotPasswordDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
		 back_btn=(ImageView) forgotPasswordDialog.findViewById(R.id.back_btn);
		 etPhone=(EditText)forgotPasswordDialog.findViewById(R.id.etPhone);
		 tvSignin=(Button)forgotPasswordDialog.findViewById(R.id.tvSignin);
		 tvSignin.setOnClickListener(this);

		back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				forgotPasswordDialog.dismiss();
			}
		});


		forgotPasswordDialog.show();

	}

	public void showKeyboard(View v) {
		InputMethodManager inputManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		// View view = activity.getCurrentFocus();
		// if (view != null) {
		inputManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
		// }
	}

	private void forgetPassowrd() {

		AndyUtils.showCustomProgressDialog(registerActivity,
				getString(R.string.progress_loading), false);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, AndyConstants.ServiceType.FORGET_PASSWORD);
		map.put(AndyConstants.Params.TYPE, 1 + "");
		map.put(AndyConstants.Params.FORGOT_PHONE, etPhone.getText().toString());

		Log.i("phone 0012",""+etPhone.getText().toString());

		new HttpRequester(registerActivity, map,
				AndyConstants.ServiceCode.FORGET_PASSWORD, this);

		// requestQueue.add(new VolleyHttpRequest(Method.POST, map,
		// AndyConstants.ServiceCode.FORGET_PASSWORD, this, this));

	}

	private Dialog otpDialog;
	ImageView back_bt;
	Button tvbtnotp,tvbtnotp_sub;
	TextView tvotp_txt;
	LinearLayout otp_lay;
	String otp_str="";
	PinView pinView;
	PreferenceHelper preferenceHelper;
	private void showOTPdDialog() {


		otpDialog = new Dialog(getActivity(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		otpDialog.setContentView(R.layout.otp_activity);
		otpDialog.setCancelable(false);


		otpDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
		back_bt=(ImageView) otpDialog.findViewById(R.id.back_bt);
		tvbtnotp=(Button)otpDialog.findViewById(R.id.tvbtnotp);
		tvbtnotp_sub=(Button)otpDialog.findViewById(R.id.tvbtnotp_sub);
		tvotp_txt=(TextView)otpDialog.findViewById(R.id.tvotp_txt);
		otp_lay=(LinearLayout)otpDialog.findViewById(R.id.otp_lay);
		pinView = (PinView) otpDialog.findViewById(R.id.pinView);

		back_bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				preferenceHelper.Logout();
				otpDialog.dismiss();
				registerActivity.addFragment(new LoginFragment(), false,
						AndyConstants.LOGIN_FRAGMENT_TAG, false);
			}
		});

		tvbtnotp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				requestOTP();
			}
		});

		pinView.setOnCompleteListener(new PinView.OnCompleteListener() {

			@Override
			public void onComplete(boolean completed, String pinResults) {
				otp_str=pinResults;
			}
		});


		tvbtnotp_sub.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if(otp_str.toString().length()!=4){
					AndyUtils.showToast(
							getResources().getString(R.string.error_otp_verify),
							registerActivity);
					return;
				}

				verifyOTP();

			}
		});


		otpDialog.show();

	}

	private void requestOTP() {

		AndyUtils.showCustomProgressDialog(registerActivity,
				getString(R.string.progress_loading), false);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, AndyConstants.ServiceType.REQUEST_OTP);
		map.put(AndyConstants.Params.DEVICE_TYPE,
				AndyConstants.DEVICE_TYPE_ANDROID);
		map.put(AndyConstants.Params.TOKEN, new PreferenceHelper(
				registerActivity).getSessionToken());

		new HttpRequester(registerActivity, map,
				AndyConstants.ServiceCode.REQUEST_OTP, this);

		// requestQueue.add(new VolleyHttpRequest(Method.POST, map,
		// AndyConstants.ServiceCode.FORGET_PASSWORD, this, this));

	}

	private void verifyOTP() {

		AndyUtils.showCustomProgressDialog(registerActivity,
				getString(R.string.progress_loading), false);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, AndyConstants.ServiceType.VERIFY_OTP);

		map.put(AndyConstants.Params.OTP,otp_str);
		map.put(AndyConstants.Params.DEVICE_TYPE,
				AndyConstants.DEVICE_TYPE_ANDROID);
		map.put(AndyConstants.Params.TOKEN, new PreferenceHelper(
				registerActivity).getSessionToken());

		new HttpRequester(registerActivity, map,
				AndyConstants.ServiceCode.VERIFY_OTP, this);



	}

}
