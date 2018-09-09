package com.parcelsixd.parcel.driver.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parcelsixd.parcel.driver.R;
import com.parcelsixd.parcel.driver.base.BaseMapFragment;
import com.parcelsixd.parcel.driver.model.RequestDetail;
import com.parcelsixd.parcel.driver.parse.AsyncTaskCompleteListener;
import com.parcelsixd.parcel.driver.utills.AndyConstants;
import com.parcelsixd.parcel.driver.utills.PreferenceHelper;

/**
 * Created by Praveen on 16-Jan-18.
 */

public class RequestHandlerFragment  extends BaseMapFragment implements
        AsyncTaskCompleteListener{

    View requestHandlerFragmentView;
    private Bundle mBundle;
    private RequestDetail requestDetail;
    TextView client_name,pak_type,pak_id,booking_date,delivery_date,pickup_add,dest_address,price;
    RelativeLayout startjob_lay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        requestHandlerFragmentView = inflater.inflate(R.layout.fragment_job_new, container,
                false);
        preferenceHelper = new PreferenceHelper(getActivity());
        initView();
        return requestHandlerFragmentView;
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
    }

    public void initView(){
        client_name=(TextView)requestHandlerFragmentView.findViewById(R.id.tvClientName);
        pak_type=(TextView)requestHandlerFragmentView.findViewById(R.id.pak_type);
        pak_id=(TextView)requestHandlerFragmentView.findViewById(R.id.pak_id);
        booking_date=(TextView)requestHandlerFragmentView.findViewById(R.id.booking_date);
        delivery_date=(TextView)requestHandlerFragmentView.findViewById(R.id.delivery_date);
        pickup_add=(TextView)requestHandlerFragmentView.findViewById(R.id.pickup_add);
        dest_address=(TextView)requestHandlerFragmentView.findViewById(R.id.dest_address);
        price=(TextView)requestHandlerFragmentView.findViewById(R.id.price);


    }

    private void setClientDetails(RequestDetail requestDetail) {
        client_name.setText(requestDetail.getClientName());

    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

    }
}
