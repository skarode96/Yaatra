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
        public TextView peerSourceName;
        public TextView peerDestinationName;
        public TextView peerModeOfTravel;
        public TextView peerStatus;
        public ImageView profileIcon;

        public ViewHolder(View v){

            super(v);
            peerUsername = (TextView)v.findViewById(R.id.peer_username);
            peerSourceName = (TextView)v.findViewById(R.id.peer_source_name);
            peerDestinationName = (TextView)v.findViewById(R.id.peer_destination_name);
            peerModeOfTravel = (TextView)v.findViewById(R.id.peer_mode_of_travel);
            profileIcon = (ImageView) v.findViewById(R.id.profile_icon);
            peerStatus = (TextView) v.findViewById(R.id.peer_status);

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
        holder.peerSourceName.setText(travellerInfos.get(position).getSourceName());
        holder.peerStatus.setText(travellerInfos.get(position).getStatus().toString());
        holder.peerDestinationName.setText(travellerInfos.get(position).getDestinationName());
        holder.peerModeOfTravel.setText(travellerInfos.get(position).getModeOfTravel());
        holder.profileIcon.setImageResource(gender == Gender.MALE ? R.drawable.guy : R.drawable.girl);
    }

    @Override
    public int getItemCount(){
        return this.travellerInfos != null ? this.travellerInfos.size() : 0;
    }
}


