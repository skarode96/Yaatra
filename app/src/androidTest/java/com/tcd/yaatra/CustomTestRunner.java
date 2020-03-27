package com.tcd.yaatra;

import android.app.Application;
import android.content.Context;
import androidx.test.runner.AndroidJUnitRunner;

public class CustomTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        return super.newApplication(cl, TestApp.class.getName(), context);
    }
}
