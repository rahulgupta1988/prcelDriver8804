package com.parcelsixd.parcel.driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.parcelsixd.parcel.driver.utills.PreferenceHelper;
import com.parcelsixd.parcel.driver.widget.MyFontTextView;

/**
 * Created by Praveen on 27-Dec-17.
 */

public class SplashActivity extends Activity {

    TextView tv_version;
    Context mContext;
    private PreferenceHelper preferenceHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.fragment_job_new);
        //setContentView(R.layout.fragment_job_arrived);
        setContentView(R.layout.splashactivity);
        setVersion();
        mContext=this;
       // preferenceHelper = new PreferenceHelper(this);

        Thread timer = new Thread() {

            public void run() {

                try {
                   // preferenceHelper.Logout();
                    sleep(2000);

                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                finally
                {
                    Intent startRegisterActivity = new Intent(mContext,
                            RegisterActivity.class);
                    startRegisterActivity.putExtra("isSignin", true);
                    startActivity(startRegisterActivity);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                   /* Intent intent=new Intent(mContext,SignatureActivity.class);
                    startActivity(intent);*/

                 finish();
                }

            }

        };
        timer.start();
    }

    public void setVersion(){
        tv_version=(MyFontTextView)findViewById(R.id.tv_version);
        try {
            String appVersion=getPackageManager().getPackageInfo(getPackageName(),0).versionName;
            tv_version.setText("Version: " + appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
