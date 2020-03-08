package com.tcd.yaatra.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityMenuBinding;
import com.tcd.yaatra.repository.UserInfoRepository;
import com.tcd.yaatra.ui.fragments.DailyFragment;
import com.tcd.yaatra.ui.fragments.SettingsFragment;
import com.tcd.yaatra.utils.SharedPreferenceUtils;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class MenuContainerActivity extends BaseActivity<ActivityMenuBinding> implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    SharedPreferences loginPreferences;

    @Inject
    UserInfoRepository userInfoRepository;

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_menu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBarDrawerToggle toggle = initActionBarDrawer();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(layoutDataBinding.fragmentContainer.getId(), new MapFragment()).commit();
            layoutDataBinding.navView.setCheckedItem(R.id.ad_hoc);
            this.drawer.addDrawerListener(toggle);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.ad_hoc:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
                break;
            case R.id.daily:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DailyFragment()).commit();
                break;
            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
            case R.id.logout:
                handleLogout();
                break;
            default: break;
        }
        this.drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleLogout() {
        SharedPreferenceUtils.clearAuthToken();
        SharedPreferenceUtils.clearUserName();
        SharedPreferenceUtils.clearUserId();
        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @NotNull
    private ActionBarDrawerToggle initActionBarDrawer() {
        this.drawer = layoutDataBinding.drawerLayout;
        layoutDataBinding.navView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, layoutDataBinding.drawerLayout, layoutDataBinding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.addDrawerListener(toggle);
        toggle.syncState();
        return toggle;
    }
}
