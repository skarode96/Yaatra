package com.tcd.yaatra.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcd.yaatra.R;
import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.JourneyFrequency;
import com.tcd.yaatra.services.api.yaatra.models.JourneyDetails;
import com.tcd.yaatra.services.api.yaatra.models.TravellerDetails;
import com.tcd.yaatra.ui.fragments.DailyFragment;
import com.tcd.yaatra.utils.MapUtils;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class DailyCommuteDetailsAdapter extends RecyclerView.Adapter<DailyCommuteDetailsAdapter.DailyCommuteDetailsViewHolder> {

    private ArrayList<TravellerDetails> dataSet;
    Gender male = Gender.MALE;
    Gender female = Gender.FEMALE;
    Gender noPref = Gender.NOT_SPECIFIED;
    Gender other = Gender.OTHER;

    Context context;

    public DailyCommuteDetailsAdapter(ArrayList<TravellerDetails> data) {
        this.dataSet = data;
    }

    @Override
    public DailyCommuteDetailsViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.daily_commute_details_row, parent, false);
        context = parent.getContext();

        DailyCommuteDetailsViewHolder myViewHolder = new DailyCommuteDetailsViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final DailyCommuteDetailsViewHolder holder, final int listPosition) {


        TextView firstName = holder.firstName;
        TextView lastName = holder.lastName;
        TextView gender = holder.gender;
        TextView age = holder.age;
        TextView rating = holder.rating;
        ImageView imageView = holder.imageViewIcon;

        firstName.setText(dataSet.get(listPosition).getFirstName());
        lastName.setText(dataSet.get(listPosition).getLastName());
        if(dataSet.get(listPosition).getGender().equalsIgnoreCase(male.idName)) {
            gender.setText(male.stringLabel);
            imageView.setImageResource(R.drawable.guy);
        }
        else if(dataSet.get(listPosition).getGender().equalsIgnoreCase(female.idName)) {
            gender.setText(female.stringLabel);
            imageView.setImageResource(R.drawable.girl);
        }
        else if(dataSet.get(listPosition).getGender().equalsIgnoreCase(other.idName)) {
            gender.setText(other.stringLabel);
            imageView.setImageResource(R.drawable.guy);
        }
        age.setText(Integer.toString(dataSet.get(listPosition).getAge()));
        rating.setText(Double.toString(dataSet.get(listPosition).getRating()));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class DailyCommuteDetailsViewHolder extends RecyclerView.ViewHolder {

        TextView firstName;
        TextView lastName;
        TextView gender;
        TextView age;
        TextView rating;
        ImageView imageViewIcon;

        public DailyCommuteDetailsViewHolder(View itemView) {
            super(itemView);
            this.firstName = (TextView) itemView.findViewById(R.id.FirstName);
            this.lastName = (TextView) itemView.findViewById(R.id.LastName);
            this.gender = (TextView) itemView.findViewById(R.id.Gender);
            this.age = (TextView) itemView.findViewById(R.id.Age);
            this.rating = (TextView) itemView.findViewById(R.id.Rating);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
