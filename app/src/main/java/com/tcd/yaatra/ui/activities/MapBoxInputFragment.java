package com.tcd.yaatra.ui.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityMapboxInputBinding;
import com.tcd.yaatra.ui.fragments.BaseFragment;

public class MapBoxInputFragment extends BaseFragment<ActivityMapboxInputBinding> {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int getFragmentResourceId() {
        return R.layout.activity_mapbox_input;
    }

    @Override
    public void initEventHandlers() {
        super.initEventHandlers();
        this.layoutDataBinding.goToDestination.setOnClickListener(view -> {
            showRoutes();
        });
    }

    public void showRoutes(){
        Toast.makeText(getActivity(),"Show Routes",Toast.LENGTH_SHORT).show();
    }
}
