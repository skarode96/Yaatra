package com.tcd.yaatra.ui.activities;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseActivity<DataBindingClass extends ViewDataBinding> extends AppCompatActivity {

    protected DataBindingClass layoutDataBinding;

    abstract int getLayoutResourceId();

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

    private void handleOnCreate(){
        this.layoutDataBinding = DataBindingUtil.setContentView(this, this.getLayoutResourceId());
    }
}
