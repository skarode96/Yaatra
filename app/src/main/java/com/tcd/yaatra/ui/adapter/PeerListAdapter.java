package com.tcd.yaatra.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tcd.yaatra.R;
import com.tcd.yaatra.repository.models.Gender;
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
        public TextView peerModeOfTravel;
        public ImageView profileIcon;

        public ViewHolder(View v){

            super(v);
            peerUsername = (TextView)v.findViewById(R.id.peer_username);
            peerSourceLattitude = (TextView)v.findViewById(R.id.peer_source_latitude);
            peerSourceLongitude = (TextView)v.findViewById(R.id.peer_source_longitude);
            peerDestinationLattitude = (TextView)v.findViewById(R.id.peer_destination_latitude);
            peerDestinationLongitude = (TextView)v.findViewById(R.id.peer_destination_longitude);
            peerModeOfTravel = (TextView)v.findViewById(R.id.peer_mode_of_travel);
            profileIcon = (ImageView) v.findViewById(R.id.profile_icon);

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
        Gender gender = travellerInfos.get(position).getGender();
        holder.peerUsername.setText(travellerInfos.get(position).getUserName());
        holder.peerSourceLattitude.setText(travellerInfos.get(position).getSourceLatitude().toString());
        holder.peerSourceLongitude.setText(travellerInfos.get(position).getSourceLongitude().toString());
        holder.peerDestinationLattitude.setText(travellerInfos.get(position).getDestinationLatitude().toString());
        holder.peerDestinationLongitude.setText(travellerInfos.get(position).getDestinationLongitude().toString());
        holder.peerModeOfTravel.setText(travellerInfos.get(position).getModeOfTravel());
        holder.profileIcon.setImageResource(gender == Gender.MALE ? R.drawable.guy : R.drawable.girl);
    }

    @Override
    public int getItemCount(){
        return this.travellerInfos != null ? this.travellerInfos.size() : 0;
    }
}


