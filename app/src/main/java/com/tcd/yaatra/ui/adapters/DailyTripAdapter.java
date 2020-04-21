package com.tcd.yaatra.ui.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.JourneyFrequency;
import com.tcd.yaatra.services.api.yaatra.models.JourneyDetails;
import com.tcd.yaatra.ui.fragments.DailyFragment;
import com.tcd.yaatra.R;
import com.tcd.yaatra.utils.MapUtils;

import java.util.ArrayList;
import java.util.List;

public class DailyTripAdapter extends RecyclerView.Adapter<DailyTripAdapter.DailyTripViewHolder> {

    private ArrayList<JourneyDetails> dataSet;
    JourneyFrequency daily = JourneyFrequency.DAILY;
    JourneyFrequency weekly = JourneyFrequency.WEEKLY;
    JourneyFrequency weekend = JourneyFrequency.WEEKEND;
    Context context;

    public DailyTripAdapter(ArrayList<JourneyDetails> data) {
        this.dataSet = data;
    }

    @Override
    public DailyTripViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.daily_trip_row, parent, false);
        context = parent.getContext();
        view.setOnClickListener(DailyFragment.dailyTripOnClickListener);

        DailyTripViewHolder myViewHolder = new DailyTripViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final DailyTripViewHolder holder, final int listPosition) {


        TextView title = holder.title;
        TextView source = holder.source;
        TextView destination = holder.destination;
        TextView frequency = holder.frequency;
        TextView time = holder.time;
        TextView startDate = holder.startDate;
        ImageView imageView = holder.imageViewIcon;

        title.setText(dataSet.get(listPosition).getJourney_title());
        startDate.setText(dataSet.get(listPosition).getStartTime());

        String sourceName = MapUtils.locationName(context,dataSet.get(listPosition).getSourceLat(),dataSet.get(listPosition).getSourceLong());
        source.setText(sourceName);

        String destinationName = MapUtils.locationName(context,dataSet.get(listPosition).getDestinationLat(),dataSet.get(listPosition).getDestinationLong());
        destination.setText(destinationName);

        if(dataSet.get(listPosition).getJourneyFrequency()==daily.intValue)
            frequency.setText(daily.stringLabel);
        else if(dataSet.get(listPosition).getJourneyFrequency()==weekly.intValue)
            frequency.setText(weekly.stringLabel);
        else if(dataSet.get(listPosition).getJourneyFrequency()==weekend.intValue)
            frequency.setText(weekend.stringLabel);

        time.setText(dataSet.get(listPosition).getTimeOfCommute());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class DailyTripViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView source;
        TextView destination;
        TextView frequency;
        TextView time;
        TextView startDate;
        ImageView imageViewIcon;

        public DailyTripViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.Title);
            this.source = (TextView) itemView.findViewById(R.id.Source);
            this.destination = (TextView) itemView.findViewById(R.id.Destination);
            this.frequency = (TextView) itemView.findViewById(R.id.Frequency);
            this.time = (TextView) itemView.findViewById(R.id.Time);
            this.startDate = (TextView) itemView.findViewById(R.id.startDate);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
