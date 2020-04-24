package com.tcd.yaatra.ui.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.tcd.yaatra.CustomFragmentFactory;
import com.tcd.yaatra.R;
import com.tcd.yaatra.extendedFragments.ExtendedDailyCommuteFragment;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteResponse;
import com.tcd.yaatra.services.api.yaatra.models.JourneyDetails;
import com.tcd.yaatra.ui.viewmodels.DailyCommuteActivityViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DailyFragmentTest {

    FragmentScenario<ExtendedDailyCommuteFragment> testObjectDailyFragment;

    @Mock
    DailyCommuteActivityViewModel dailyCommuteActivityViewModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        List<JourneyDetails> journeys = new ArrayList<>();
        LiveData<AsyncData<DailyCommuteResponse>> response = new LiveData<AsyncData<DailyCommuteResponse>>() {
            @Override
            public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super AsyncData<DailyCommuteResponse>> observer) {
                super.observe(owner, observer);
            }
        };

        Mockito.when(dailyCommuteActivityViewModel.getDailyCommute()).thenReturn(response);

        CustomFragmentFactory customFragmentFactory = new CustomFragmentFactory(dailyCommuteActivityViewModel);
        testObjectDailyFragment = FragmentScenario.launchInContainer(ExtendedDailyCommuteFragment.class
                , new Bundle(), R.style.Theme_AppCompat, customFragmentFactory);
    }

    @Test
    public void verify() {

        Mockito.verify(dailyCommuteActivityViewModel, Mockito.times(1)).getDailyCommute();

    }
}
