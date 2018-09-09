package com.parcelsixd.parcel.driver.fragment;

import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;
import com.parcelsixd.parcel.driver.R;
import com.parcelsixd.parcel.driver.base.BaseMapFragment;
import com.parcelsixd.parcel.driver.locationupdate.LocationHelper;
import com.parcelsixd.parcel.driver.model.RequestDetail;
import com.parcelsixd.parcel.driver.parse.AsyncTaskCompleteListener;
import com.parcelsixd.parcel.driver.parse.HttpRequester;
import com.parcelsixd.parcel.driver.utills.AndyConstants;
import com.parcelsixd.parcel.driver.utills.AndyUtils;
import com.parcelsixd.parcel.driver.utills.AppLog;
import com.parcelsixd.parcel.driver.widget.MyFontTextView;

import java.util.HashMap;

/**
 * Created by Praveen on 24-Jan-18.
 */

public class StartJobFragment extends BaseMapFragment implements
        AsyncTaskCompleteListener, LocationHelper.OnLocationReceived {

    private Bundle mBundle;
    private RequestDetail requestDetail;
    View startJobFragmentView;
    private RelativeLayout btn_startjob;
    ImageView notify_ic1;
    MyFontTextView tvClientName,client_id,pak_type,pak_id,booking_date,delivery_date,pickup_add,dest_address,price;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        startJobFragmentView = inflater.inflate(R.layout.fragment_job_new, container,
                false);
        setWakerStartedViews();
        return startJobFragmentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestDetail = (RequestDetail) getArguments().getSerializable(
                AndyConstants.REQUEST_DETAIL);
        setClientDetails(requestDetail);
        // alert animation
		playAlert();
		rotate1();

    }


    public void setWakerStartedViews(){
        btn_startjob=(RelativeLayout)startJobFragmentView.findViewById(R.id.btn_startjob);
        btn_startjob.setOnClickListener(this);

        tvClientName = (MyFontTextView) startJobFragmentView.findViewById(R.id.tvClientName);
        client_id = (MyFontTextView) startJobFragmentView.findViewById(R.id.client_id);
        pak_type = (MyFontTextView) startJobFragmentView.findViewById(R.id.pak_type);
        pak_id = (MyFontTextView) startJobFragmentView.findViewById(R.id.pak_id);
        booking_date = (MyFontTextView) startJobFragmentView.findViewById(R.id.booking_date);
        delivery_date = (MyFontTextView) startJobFragmentView.findViewById(R.id.delivery_date);
        pickup_add = (MyFontTextView) startJobFragmentView.findViewById(R.id.pickup_add);
        dest_address = (MyFontTextView) startJobFragmentView.findViewById(R.id.dest_address);
        price = (MyFontTextView) startJobFragmentView.findViewById(R.id.price);
        notify_ic1=(ImageView)startJobFragmentView.findViewById(R.id.notify_ic1);

    }

    private void setClientDetails(RequestDetail requestDetail) {
        tvClientName.setText("" + requestDetail.getClientName());
        pak_type.setText("" + requestDetail.getPackage_type() + " Package");
        booking_date.setText("" + requestDetail.getBooking_date());
        pickup_add.setText("" + requestDetail.getSrc_address());
        dest_address.setText("" + requestDetail.getDest_address());
        price.setText("" + requestDetail.getTotal());
        client_id.setText("ID:" + requestDetail.getRequest_id());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_startjob:
                mapActivity.clearAll();
                walkerStarted();
                break;
        }
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        AppLog.Log("start responce", "walker started response " + response);
        if (parseContent.isSuccess(response)) {
            JobFragment jobFragment = new JobFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(AndyConstants.JOB_STATUS,
                    AndyConstants.IS_WALKER_ARRIVED);
            bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
                    requestDetail);
            jobFragment.setArguments(bundle);
            mapActivity.addFragment(jobFragment, false,
                    AndyConstants.JOB_FRGAMENT_TAG, true);
        }
    }

    @Override
    public void onLocationReceived(LatLng latlong) {

    }

    @Override
    public void onLocationReceived(Location location) {

    }

    @Override
    public void onConntected(Bundle bundle) {

    }

    @Override
    public void onConntected(Location location) {

    }


    private void walkerStarted() {
        if (!AndyUtils.isNetworkAvailable(mapActivity)) {
            AndyUtils.showToast(
                    getResources().getString(R.string.toast_no_internet),
                    mapActivity);
            return;
        }

        AndyUtils.showCustomProgressDialog(mapActivity, getResources()
                .getString(R.string.progress_send_request), false);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.WALKER_STARTED);
        map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
        map.put(AndyConstants.Params.REQUEST_ID,
                String.valueOf(preferenceHelper.getRequestId()));
        map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
        map.put(AndyConstants.Params.LATITUDE,
                preferenceHelper.getWalkerLatitude());
        map.put(AndyConstants.Params.LONGITUDE,
                preferenceHelper.getWalkerLongitude());

        new HttpRequester(mapActivity, map,
                AndyConstants.ServiceCode.WALKER_STARTED, this);
        // requestQueue.add(new VolleyHttpRequest(Method.POST, map,
        // AndyConstants.ServiceCode.WALKER_STARTED, this, this));
    }


    int anim_count=1;
    final RotateAnimation rotateAnim1 = new RotateAnimation(0.0f, 45.0f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f);

    final RotateAnimation rotateAnim2 = new RotateAnimation(45.0f, 0.0f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f);
    private void rotate1() {


        rotateAnim1.setDuration(300);
        rotateAnim1.setFillAfter(true);
        notify_ic1.startAnimation(rotateAnim1);

        rotateAnim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rotate2();


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    RotateAnimation rotateAnim=null;
    private void rotate2() {
        rotateAnim2.setDuration(300);
        rotateAnim2.setFillAfter(true);
        notify_ic1.startAnimation(rotateAnim2);

        rotateAnim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                anim_count++;
                if(anim_count<=5) {
                    rotate1();
                }
                else{
                    ringtone.stop();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    Ringtone ringtone=null;
    public void playAlert(){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(getContext(),uri);
        ringtone.play();
    }
}
