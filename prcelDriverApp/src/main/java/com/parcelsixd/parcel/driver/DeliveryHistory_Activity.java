package com.parcelsixd.parcel.driver;

import android.app.Activity;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parcelsixd.parcel.driver.model.History;
import com.parcelsixd.parcel.driver.parse.AsyncTaskCompleteListener;
import com.parcelsixd.parcel.driver.parse.HttpRequester;
import com.parcelsixd.parcel.driver.parse.ParseContent;
import com.parcelsixd.parcel.driver.utills.AndyConstants;
import com.parcelsixd.parcel.driver.utills.AndyUtils;
import com.parcelsixd.parcel.driver.utills.PreferenceHelper;
import com.parcelsixd.parcel.driver.widget.MyFontTextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by Praveen on 19-Dec-17.
 */

public class DeliveryHistory_Activity extends Activity implements AsyncTaskCompleteListener {
    Context mContext;
    ImageView back_btn;
    //RecyclerView deliverylist;
    private PreferenceHelper preferenceHelper;
    private ParseContent parseContent;
    private ArrayList<History> historyList;
    private LinearLayout historyViews;
    RotateAnimation wheelRotation1,wheelRotation2;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deliveryhistory_activity);
        mContext=this;
        historyList = new ArrayList<History>();
        preferenceHelper = new PreferenceHelper(this);
        parseContent = new ParseContent(this);
        setAnimation();
        initView();
    }

    public void initView(){
        historyViews=(LinearLayout)findViewById(R.id.historyViews);
        back_btn=(ImageView)findViewById(R.id.back_btn);
       // deliverylist=(RecyclerView)findViewById(R.id.deliverylist);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getHistory();

    }

    public void setAnimation(){
        wheelRotation1 = new RotateAnimation(0.0f,180.0f,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        wheelRotation1.setDuration(300);
        wheelRotation1.isFillEnabled();
        wheelRotation1.setFillAfter(true);

        wheelRotation2 = new RotateAnimation(180.0f,360.0f,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        wheelRotation2.setDuration(300);
        wheelRotation2.isFillEnabled();
        wheelRotation2.setFillAfter(true);
    }

   /* public void initDeliveryList(){
        DeliveryHistryListAdapter deliveryHistryListAdapter=new DeliveryHistryListAdapter(mContext,historyList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        deliverylist.setLayoutManager(mLayoutManager);
        deliverylist.setItemAnimator(new DefaultItemAnimator());
        deliverylist.setAdapter(deliveryHistryListAdapter);
    }*/

   

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void getHistory() {

        AndyUtils.showCustomProgressDialog(this,
                getString(R.string.progress_loading), false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.DELIVERY_HISTORY);
        map.put(AndyConstants.Params.DEVICE_TYPE,
                AndyConstants.DEVICE_TYPE_ANDROID);
        map.put(AndyConstants.Params.TOKEN, new PreferenceHelper(
                this).getSessionToken());
        map.put("id", preferenceHelper.getUserId());
        new HttpRequester(this, map,
                AndyConstants.ServiceCode.DELIVERY_HISTORY, this);

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        Log.i("history 124",""+response);
        switch (serviceCode) {

            case AndyConstants.ServiceCode.DELIVERY_HISTORY:
                if (!parseContent.isSuccess(response)) {
                    return;
                }

                historyList.clear();
                parseContent.parseDeliveryHistory(response, historyList);

                Collections.sort(historyList, new Comparator<History>() {
                    @Override
                    public int compare(History o1, History o2) {

                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "yyyy-MM-dd hh:mm:ss");
                        try {

                            String firstStrDate = o1.getDelivery_date();
                            String secondStrDate = o2.getDelivery_date();
                            Date date2 = dateFormat.parse(secondStrDate);
                            Date date1 = dateFormat.parse(firstStrDate);
                            int value = date2.compareTo(date1);
                            return value;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });

                setHistoryViews();
               // initDeliveryList();

        }
    }

    View lastClickedView=null;
    LinearLayout lastClickedLay=null;
    ImageView lastImage=null;
    public void setHistoryViews(){
        historyViews.removeAllViews();
        LayoutInflater layoutInflater= (LayoutInflater) getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        for(int k=0;k<historyList.size();k++){

            View view=layoutInflater.inflate(R.layout.deliveryhistrylist_item,null);
            final  LinearLayout card_lay=(LinearLayout)view.findViewById(R.id.card_lay);
            final ImageView downup_ic=(ImageView)view.findViewById(R.id.downup_ic);
            final MyFontTextView packid=(MyFontTextView)view.findViewById(R.id.packid);
            final MyFontTextView bookon=(MyFontTextView)view.findViewById(R.id.bookon);
            final MyFontTextView deliveryon=(MyFontTextView)view.findViewById(R.id.deliveryon);
            final MyFontTextView pickupon=(MyFontTextView)view.findViewById(R.id.pickupon);
            final MyFontTextView destinationon=(MyFontTextView)view.findViewById(R.id.destinationon);
            final MyFontTextView price_txt=(MyFontTextView)view.findViewById(R.id.price_txt);

            History history=historyList.get(k);

            packid.setText("Pkg ID: "+history.getPackage_id());
            bookon.setText(""+history.getBooking_date()+" - "+history.getBooking_time());
            deliveryon.setText(""+history.getDelivery_date()+" - "+history.getDelivery_time());
            pickupon.setText(""+history.getPickup_address());
            destinationon.setText(""+history.getDestination_address());
            price_txt.setText(""+history.getPrice());


            downup_ic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if(lastClickedView!=null && lastClickedView.equals(view)){
                        if(lastClickedLay.getVisibility()==View.GONE){
                            card_lay.setVisibility(View.VISIBLE);
                            downup_ic.startAnimation(wheelRotation1);
                           // downup_ic.setImageResource(R.drawable.up_arrow);
                           // card_lay.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right));

                            RotateAnimation wheelRotation = new RotateAnimation(-30.0f,0.0f);
                            wheelRotation.setDuration(100);
                            //wheelRotation.setInterpolator(mContext, android.R.interpolator.accelerate_decelerate);
                            card_lay.startAnimation(wheelRotation);
                        }

                        else{

                            RotateAnimation wheelRotation = new RotateAnimation(0.0f,90.0f);
                            wheelRotation.setDuration(100);
                            //wheelRotation.setInterpolator(mContext, android.R.interpolator.accelerate_decelerate);
                            card_lay.startAnimation(wheelRotation);

                            wheelRotation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                   // downup_ic.setImageResource(R.drawable.down_arrow);
                                    downup_ic.startAnimation(wheelRotation2);
                                    lastClickedLay.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });


                        }


                    }

                    else{
                        if(lastClickedLay!=null) {
                           // lastImage.setImageResource(R.drawable.down_arrow);
                            lastImage.startAnimation(wheelRotation2);
                            lastClickedLay.setVisibility(View.GONE);
                        }
                        downup_ic.startAnimation(wheelRotation1);
                       // downup_ic.setImageResource(R.drawable.up_arrow);
                        lastImage=downup_ic;
                        lastClickedView=view;
                        lastClickedLay = card_lay;
                        card_lay.setVisibility(View.VISIBLE);
                        //card_lay.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right));

                        RotateAnimation wheelRotation = new RotateAnimation(-30.0f,0.0f);
                        wheelRotation.setDuration(100);
                        wheelRotation.setInterpolator(mContext, android.R.interpolator.accelerate_decelerate);
                        card_lay.startAnimation(wheelRotation);
                    }



                }
            });
            lastClickedView=view;
            historyViews.addView(view);

        }

    }
}
