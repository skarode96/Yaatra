package com.tcd.yaatra.repository.datasource;

import androidx.lifecycle.LiveData;
import com.tcd.yaatra.services.api.yaatra.models.Rating;

import java.util.List;

public interface RatingRepository {
    LiveData<List<Rating>> getRatings();
    LiveData<Rating> getRating(String userName);
    void insertRating(Rating rating);
    void updateRating(String userName, Double value);
    void deleteRating(Rating rating);
}
