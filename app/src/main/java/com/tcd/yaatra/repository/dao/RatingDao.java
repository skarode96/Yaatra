package com.tcd.yaatra.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.tcd.yaatra.services.api.yaatra.models.Rating;
import java.util.List;

@Dao
public interface RatingDao {

    @Query("Select * from Rating")
    LiveData<List<Rating>> getRatings();

    @Query("Select * from Rating where username = :userName")
    LiveData<Rating> getRating(String userName);

    @Insert
    void insertRating(Rating rating);

    @Query("UPDATE Rating SET value = :value WHERE username = :userName")
    void updateRating(String userName, Double value);

    @Delete
    void deleteRating(Rating rating);
}
