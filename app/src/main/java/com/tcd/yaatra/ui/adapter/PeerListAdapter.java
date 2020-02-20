package com.tcd.yaatra.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tcd.yaatra.R;
import com.tcd.yaatra.repository.models.TravellerInfo;

import java.util.ArrayList;

public class PeerListAdapter extends RecyclerView.Adapter<PeerListAdapter.ViewHolder> {
    ArrayList<TravellerInfo> travellerInfos;
    Context context;

    public PeerListAdapter(Context context1, ArrayList<TravellerInfo> travellerInfos){
        this.travellerInfos = travellerInfos;
        this.context = context1;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView peerUsername;
        public TextView peerGender;
        public TextView peerSourceLattitude;
        public TextView peerSourceLongitude;
        public TextView peerDestinationLattitude;
        public TextView peerDestinationLongitude;

        public ViewHolder(View v){

            super(v);
            peerUsername = (TextView)v.findViewById(R.id.peer_username);
            peerGender = (TextView)v.findViewById(R.id.peer_gender);
            peerSourceLattitude = (TextView)v.findViewById(R.id.peer_source_lattitude);
            peerSourceLongitude = (TextView)v.findViewById(R.id.peer_source_longitude);
            peerDestinationLattitude = (TextView)v.findViewById(R.id.peer_destination_lattitude);
            peerDestinationLongitude = (TextView)v.findViewById(R.id.peer_destination_longitude);

        }
    }

    @Override
    public PeerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_items,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        ArrayList<TravellerInfo> travellerInfos = this.travellerInfos;
        holder.peerUsername.setText(travellerInfos.get(position).getUserName());
        holder.peerGender.setText(travellerInfos.get(position).getGender().toString());
        holder.peerSourceLattitude.setText(travellerInfos.get(position).getSourceLatitude().toString());
        holder.peerSourceLongitude.setText(travellerInfos.get(position).getSourceLongitude().toString());
        holder.peerDestinationLattitude.setText(travellerInfos.get(position).getDestinationLatitude().toString());
        holder.peerDestinationLongitude.setText(travellerInfos.get(position).getDestinationLongitude().toString());
    }

    @Override
    public int getItemCount(){
        return this.travellerInfos != null ? this.travellerInfos.size() : 0;
    }
}


