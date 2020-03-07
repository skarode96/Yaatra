package com.tcd.yaatra.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tcd.yaatra.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserRatingAdapter extends RecyclerView.Adapter<UserRatingAdapter.ViewHolder> {

    Context context;
    List<String> user;

    public UserRatingAdapter(Context applicationContext, String rate[]) {
        this.context = applicationContext;
        this.user = Arrays.asList(rate);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rating_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.userName.setText(user.get(position));
        holder.rating.setRating(3);
    }

    @Override
    public int getItemCount() {
        return user.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView userName;
        private RatingBar rating;

        public ViewHolder(View view){
            super(view);
            userName = view.findViewById(R.id.uName);
            rating = view.findViewById(R.id.ratingBar);
        }
    }
}
