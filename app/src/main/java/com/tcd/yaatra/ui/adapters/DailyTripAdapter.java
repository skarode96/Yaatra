package com.tcd.yaatra.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tcd.yaatra.services.api.yaatra.models.Journey;
import com.tcd.yaatra.ui.fragments.DailyFragment;
import com.tcd.yaatra.R;

import java.util.ArrayList;

public class DailyTripAdapter extends RecyclerView.Adapter<DailyTripAdapter.DailyTripViewHolder> {

    private ArrayList<Journey> dataSet;

    public DailyTripAdapter(ArrayList<Journey> data) {
        this.dataSet = data;
    }

    @Override
    public DailyTripViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.daily_trip_row, parent, false);

        view.setOnClickListener(DailyFragment.dailyTripOnClickListener);

        DailyTripViewHolder myViewHolder = new DailyTripViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final DailyTripViewHolder holder, final int listPosition) {

        TextView textViewName = holder.textViewName;
        TextView textViewVersion = holder.textViewVersion;
        ImageView imageView = holder.imageViewIcon;

        textViewName.setText(dataSet.get(listPosition).getJourneyId());
        textViewVersion.setText(dataSet.get(listPosition).getTitle());
        //imageView.setImageResource(dataSet.get(listPosition).getImage());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class DailyTripViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewVersion;
        ImageView imageViewIcon;

        public DailyTripViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewVersion = (TextView) itemView.findViewById(R.id.textViewVersion);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
