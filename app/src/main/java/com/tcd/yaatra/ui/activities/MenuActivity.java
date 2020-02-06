package com.tcd.yaatra.ui.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityDailyCommuteBinding;
import com.tcd.yaatra.databinding.ActivityMenuBinding;

public class MenuActivity extends BaseActivity<ActivityMenuBinding> {
    private DrawerLayout drawerLayout;
    @Override
    int getLayoutResourceId() {
        return R.layout.activity_menu;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar=findViewById(R.id.toobar);
        setSupportActionBar(toolbar);

    }


}
