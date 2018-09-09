package com.parcelsixd.parcel.driver.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dpizarro.pinview.library.PinView;
import com.parcelsixd.parcel.driver.R;
import com.parcelsixd.parcel.driver.base.BaseRegisterFragment;
import com.parcelsixd.parcel.driver.parse.AsyncTaskCompleteListener;
import com.parcelsixd.parcel.driver.parse.HttpRequester;
import com.parcelsixd.parcel.driver.parse.MultiPartRequester;
import com.parcelsixd.parcel.driver.parse.ParseContent;
import com.parcelsixd.parcel.driver.utills.AndyConstants;
import com.parcelsixd.parcel.driver.utills.AndyUtils;
import com.parcelsixd.parcel.driver.utills.AppLog;
import com.parcelsixd.parcel.driver.utills.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Praveen on 13-Dec-17.
 */

public class RegisterFragment_New  extends BaseRegisterFragment implements AsyncTaskCompleteListener {

    private ParseContent parseContent;
    ImageView back_btn,smpac_img,larpac_img,accept_rd;
    Button btnBack, tvRegistr;
    EditText etRegisterFName,etphoneNumber,etpemail,etpaddress,etCarMake,etModel,etCarYear,etLicNum,etPass,etConFirmPass;
    LinearLayout smallpacklay,largepacklay;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View registerFragmentView = inflater.inflate(
                R.layout.fragment_register_new, container, false);
        back_btn = (ImageView) registerFragmentView.findViewById(R.id.back_btn);
        btnBack = (Button) registerFragmentView.findViewById(R.id.btnBack);
        tvRegistr = (Button) registerFragmentView.findViewById(R.id.tvRegistr);
        etRegisterFName = (EditText) registerFragmentView.findViewById(R.id.etRegisterFName);
        etModel = (EditText) registerFragmentView.findViewById(R.id.etModel);
        etphoneNumber = (EditText) registerFragmentView.findViewById(R.id.etphoneNumber);
        etpemail = (EditText) registerFragmentView.findViewById(R.id.etpemail);
        etpaddress = (EditText) registerFragmentView.findViewById(R.id.etpaddress);
        etCarMake = (EditText) registerFragmentView.findViewById(R.id.etCarMake);
        etCarYear = (EditText) registerFragmentView.findViewById(R.id.etCarYear);
        etLicNum = (EditText) registerFragmentView.findViewById(R.id.etLicNum);
        etPass = (EditText) registerFragmentView.findViewById(R.id.etPass);
        etConFirmPass = (EditText) registerFragmentView.findViewById(R.id.etConFirmPass);
        smpac_img = (ImageView) registerFragmentView.findViewById(R.id.smpac_img);
        larpac_img = (ImageView) registerFragmentView.findViewById(R.id.larpac_img);
        accept_rd= (ImageView) registerFragmentView.findViewById(R.id.accept_rd);


        smallpacklay=(LinearLayout)registerFragmentView.findViewById(R.id.smallpacklay);
        largepacklay=(LinearLayout)registerFragmentView.findViewById(R.id.largepacklay);
        smallpacklay.setOnClickListener(this);
        largepacklay.setOnClickListener(this);

        back_btn.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        tvRegistr.setOnClickListener(this);
        accept_rd.setOnClickListener(this);

        return registerFragmentView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerActivity.actionBar.hide();
        parseContent = new ParseContent(registerActivity);
    }

    int accept_radio=0;
    String package_type="1";

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                registerActivity.goToLogin();
                break;
            case R.id.btnBack:
                registerActivity.goToLogin();
                break;

            case R.id.tvRegistr:
                onRegisterButtonClick();
                break;

            case R.id.smallpacklay:
                smallpacklay.setBackground(getResources().getDrawable(R.drawable.registration_btn_shape));
                largepacklay.setBackground(getResources().getDrawable(R.drawable.registration_btn_shape_large));
                smpac_img.setVisibility(View.VISIBLE);
                larpac_img.setVisibility(View.GONE);
                package_type="1";
                break;

            case R.id.largepacklay:
                smallpacklay.setBackground(getResources().getDrawable(R.drawable.registration_btn_shape_large));
                largepacklay.setBackground(getResources().getDrawable(R.drawable.registration_btn_shape));
                smpac_img.setVisibility(View.GONE);
                larpac_img.setVisibility(View.VISIBLE);
                package_type="2";
                break;

            case R.id.accept_rd:
                if(accept_radio==0){
                    accept_rd.setImageDrawable(getResources().getDrawable(R.drawable.radio_checked_reg));
                    accept_radio=1;
                }
                    else{
                    accept_rd.setImageDrawable(getResources().getDrawable(R.drawable.radio_regis));
                    accept_radio=0;
                }

                break;


        }
    }


    private void onRegisterButtonClick() {

        if (etRegisterFName.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_empty_fname),
                    registerActivity);
            return;
        } else if (etphoneNumber.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_phone_no),
                    registerActivity);
            return;
        }

        else if (etphoneNumber.getText().length() < 10) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_phone_no_len),
                    registerActivity);
            return;
        }

        else if (etpemail.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_empty_email),
                    registerActivity);
            return;
        }

        else if (!AndyUtils.eMailValidation(etpemail.getText()
                .toString())) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_valid_email),
                    registerActivity);
            return;
        }


        else if (etpaddress.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_empty_address),
                    registerActivity);
            return;
        }


        else if (etCarMake.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_car_make_no),
                    registerActivity);
            return;
        }

        else if (etModel.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_car_mode_no),
                    registerActivity);
            return;
        }

        else if (etCarYear.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_car_year_no),
                    registerActivity);
            return;
        }

        else if (etLicNum.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_license_no),
                    registerActivity);
            return;
        }

        else if (etPass.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_password),
                    registerActivity);
            return;
        }

        else if (etPass.getText().length() < 6) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_password_len),
                    registerActivity);
            return;
        }

        else if (etConFirmPass.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_confirm_password),
                    registerActivity);
            return;
        }

        else if(!etConFirmPass.getText().toString().equals(etPass.getText().toString())){
            AndyUtils.showToast(
                    getResources().getString(R.string.error_new_confirm_pass), getActivity());
            return;
        }


        else if (accept_radio == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_terms),
                    registerActivity);
            return;
        }

        else {
            register();
        }


    }


    private void register() {

        if (!AndyUtils.isNetworkAvailable(registerActivity)) {
            AndyUtils.showToast(
                    getResources().getString(R.string.toast_no_internet),
                    registerActivity);
            return;
        }

        AndyUtils.showCustomProgressDialog(registerActivity, getResources()
                .getString(R.string.progress_dialog_register), false);

            AppLog.Log("Registraion", "Simple Register method");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(AndyConstants.URL, AndyConstants.ServiceType.REGISTER);



            map.put(AndyConstants.Params.FULL_NAME_REG, etRegisterFName.getText()
                    .toString());
            map.put(AndyConstants.Params.PHONE_NUM_REG, etphoneNumber.getText()
                    .toString());
           map.put(AndyConstants.Params.EMAIL, etpemail.getText()
                .toString());
           map.put(AndyConstants.Params.ADDRESS, etpaddress.getText()
                .toString());

            map.put(AndyConstants.Params.CAR_MAKE_REG, etCarMake.getText()
                    .toString());
            map.put(AndyConstants.Params.CAR_MODEL_REG, etModel.getText()
                    .toString());
            map.put(AndyConstants.Params.CAR_YEAR_REG, etCarYear.getText()
                    .toString());
            map.put(AndyConstants.Params.LIC_NUM_REG, etLicNum.getText()
                    .toString());
            map.put(AndyConstants.Params.PASS_REG, etPass.getText()
                    .toString());
            map.put(AndyConstants.Params.CAR_TYPE_REG, package_type
                .toString());
            map.put(AndyConstants.Params.DEVICE_TYPE,
                    AndyConstants.DEVICE_TYPE_ANDROID);

            map.put(AndyConstants.Params.DEVICE_TOKEN, new PreferenceHelper(
                    registerActivity).getDeviceToken());


        new MultiPartRequester(registerActivity, map, AndyConstants.ServiceCode.REGISTER, this);

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case AndyConstants.ServiceCode.REGISTER:
                AndyUtils.removeCustomProgressDialog();
                AppLog.Log("regis response", "Register response :" + response);

                if (parseContent.isSuccess(response)) {
                    // AndyUtils.showToast(
                    // registerActivity.getResources().getString(
                    // R.string.toast_register_success),
                    // registerActivity);
                    parseContent.parseUserAndStoreToDb(response);

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        if (jsonObject.has("token")) {
                            new PreferenceHelper(registerActivity).putSessionToken(jsonObject.getString("token"));
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    new PreferenceHelper(getActivity())
                            .putPassword(etPass.getText().toString());
                    //showRegistrationConfirmationDialog();
                    // Intent intent = new Intent(registerActivity,
                    // MapActivity.class);
                    // startActivity(intent);
                    /*registerActivity.addFragment(new LoginFragment(), false,
                            AndyConstants.LOGIN_FRAGMENT_TAG, false);*/
                    showOTPdDialog();

                    // registerActivity.finish();
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    String tel_num=etphoneNumber.getText().toString();
                    if(tel_num.length()==10)
                    tel_num=tel_num.substring(0,2)+"******"+tel_num.substring(8);
                    tvotp_txt.setText("Please type the verification code sent to "+tel_num);
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
                    registerActivity.goToLogin();
                }
                break;
        }
    }

    private void showRegistrationConfirmationDialog() {
        final Dialog dialog = new Dialog(registerActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        Button okBtn = (Button) dialog.findViewById(R.id.ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private Dialog otpDialog;
    ImageView back_bt;
    Button tvbtnotp,tvbtnotp_sub;
    TextView tvotp_txt;
    LinearLayout otp_lay;
    String otp_str="";
    PinView pinView;

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