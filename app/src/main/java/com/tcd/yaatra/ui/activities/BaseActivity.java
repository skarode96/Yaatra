package com.tcd.yaatra.ui.activities;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import dagger.android.AndroidInjection;

public abstract class BaseActivity<DataBindingClass extends ViewDataBinding> extends AppCompatActivity {

    protected DataBindingClass layoutDataBinding;

    abstract int getLayoutResourceId();

    public void initEventHandlers(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        handleOnCreate();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleOnCreate();
    }

    protected void handleOnCreate(){
        configureDagger();

        this.layoutDataBinding = DataBindingUtil.setContentView(this, this.getLayoutResourceId());

        initEventHandlers();
    }

    private void configureDagger(){
        AndroidInjection.inject(this);
    }
}
