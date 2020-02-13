package com.tcd.yaatra.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjection;

public abstract class BaseFragment<DataBindingClass extends ViewDataBinding> extends Fragment {

    protected DataBindingClass layoutDataBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        configureDagger();
        initDataBinding(inflater, container);
        initEventHandlers();
        return this.layoutDataBinding.getRoot();
    }

    private void initDataBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        this.layoutDataBinding = DataBindingUtil.inflate(inflater, getFragmentResourceId(), container, false);
    }

    private void configureDagger() {
        AndroidInjection.inject(getActivity());
    }

    public abstract int getFragmentResourceId();

    public void initEventHandlers(){};
}
