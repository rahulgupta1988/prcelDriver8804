package com.parcelsixd.parcel.driver.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parcelsixd.parcel.driver.R;
import com.parcelsixd.parcel.driver.model.History;

import java.util.ArrayList;

/**
 * Created by Praveen on 19-Dec-17.
 */

public class DeliveryHistryListAdapter extends RecyclerView.Adapter<DeliveryHistryListAdapter.MyViewHolder>  {
    Context mContext;
    ArrayList<History> historyList;

    public DeliveryHistryListAdapter(Context mContext,ArrayList<History> historyList){
        this.mContext=mContext;
        this.historyList=historyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deliveryhistrylist_item, parent, false);

        return new MyViewHolder(itemView);
    }

    LinearLayout lastClickedLay=null;
    int lastClickePOS=-1;
    @Override
    public void onBindViewHolder(final DeliveryHistryListAdapter.MyViewHolder holder, final int position) {

        Log.i("posi001",""+position);
        holder.card_lay.setVisibility(View.GONE);
        if(position==lastClickePOS)
            holder.card_lay.setVisibility(View.VISIBLE);

        holder.downup_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("posi",""+position);
                if(lastClickedLay!=null) {
                    lastClickedLay.setVisibility(View.GONE);
                }

                lastClickedLay=holder.card_lay;
                holder.card_lay.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout card_lay;
        ImageView downup_ic;
        public MyViewHolder(View view) {
            super(view);

            card_lay=(LinearLayout)view.findViewById(R.id.card_lay);
            downup_ic=(ImageView)view.findViewById(R.id.downup_ic);

        }
    }

}
