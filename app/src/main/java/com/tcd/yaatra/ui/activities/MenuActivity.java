package com.tcd.yaatra.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityDailyCommuteBinding;
import com.tcd.yaatra.databinding.ActivityMenuBinding;

public class MenuActivity extends BaseActivity<ActivityMenuBinding> implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_menu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = layoutDataBinding.toobar;

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Journey Sharing");

        drawer = layoutDataBinding.drawerLayout;
        //drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = layoutDataBinding.navView;//findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(layoutDataBinding.fragmentContainer.getId(), new Ad_HocFragment()).commit();
            navigationView.setCheckedItem(R.id.ad_hoc);
            drawer.addDrawerListener(toggle);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.ad_hoc:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Ad_HocFragment()).commit();
                break;
            case R.id.daily:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DailyFragment()).commit();
                break;
            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }
}
