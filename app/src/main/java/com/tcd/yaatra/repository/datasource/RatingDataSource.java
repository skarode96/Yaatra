package com.tcd.yaatra.repository.datasource;

import androidx.lifecycle.LiveData;
import com.tcd.yaatra.repository.dao.RatingDao;
import com.tcd.yaatra.services.api.yaatra.models.Rating;
import java.util.List;
import javax.inject.Inject;

public class RatingDataSource implements RatingRepository {

    private RatingDao ratingDao;

    @Inject
    public RatingDataSource(RatingDao ratingDao) {
        this.ratingDao = ratingDao;
    }

    @Override
    public LiveData<List<Rating>> getRatings() {
        return ratingDao.getRatings();
    }

    @Override
    public LiveData<Rating> getRating(String userName) {
        return ratingDao.getRating(userName);
    }

    @Override
    public void insertRating(Rating rating) {
        ratingDao.insertRating(rating);
    }

    @Override
    public void updateRating(String userName, Double value) {
        ratingDao.updateRating(userName, value);
    }

    @Override
    public void deleteRating(Rating rating) {
        ratingDao.deleteRating(rating);
    }
}
