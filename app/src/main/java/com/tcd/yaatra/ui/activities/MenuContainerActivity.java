package com.tcd.yaatra.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.tcd.yaatra.R;
import com.tcd.yaatra.WifiDirectP2PHelper.PeerCommunicator;
import com.tcd.yaatra.databinding.ActivityMenuBinding;
import com.tcd.yaatra.repository.datasource.UserInfoRepository;
import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.ui.fragments.DailyFragment;
import com.tcd.yaatra.ui.fragments.MapFragment;
import com.tcd.yaatra.ui.fragments.SettingsFragment;
import com.tcd.yaatra.utils.DownloadIETown;
import com.tcd.yaatra.utils.SharedPreferenceUtils;
import com.tcd.yaatra.ui.fragments.OfflineMaps;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class MenuContainerActivity extends BaseActivity<ActivityMenuBinding> implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    SharedPreferences loginPreferences;

    @Inject
    DownloadIETown downloadIETown;

    @Inject
    UserInfoRepository userInfoRepository;

    @Inject
    PeerCommunicator peerCommunicator;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_menu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //To solve double time rendering issue of drawer image and username
//    @Override
//    protected void onResume() {
        //super.onResume();
        downloadIETown.setIeTownDataList(downloadIETown.readCsv(getApplicationContext()));
        ActionBarDrawerToggle toggle = initActionBarDrawer();
        //if (savedInstanceState == null) {

        layoutDataBinding.navView.setCheckedItem(R.id.ad_hoc);

        this.drawer.addDrawerListener(toggle);
        //}

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);

        //reference to views
        ImageView profileImage = (ImageView) headerView.findViewById(R.id.profile_image);
        TextView userName = (TextView) headerView.findViewById(R.id.profile_username);
        if(SharedPreferenceUtils.getUserName() != null) {
            userInfoRepository.getUserProfile(SharedPreferenceUtils.getUserName()).observe(this, response -> {
                if(response != null) {
                    userName.setText(response.getUsername());
                    profileImage.setImageResource(response.getGender().equals("m") ? R.drawable.guy : R.drawable.girl);
                }
            });
        }
//    }

        requestExternalStoragePermission();
    }

    @Override
    protected void onResume() {
        super.onResume();

        addFragmentAsPerMenuSelection(layoutDataBinding.navView.getCheckedItem());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        peerCommunicator.cleanup();
        addFragmentAsPerMenuSelection(menuItem);
        this.drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addFragmentAsPerMenuSelection(@NonNull MenuItem menuItem) {
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
            case R.id.offline_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OfflineMaps()).commit();
                break;
            case R.id.logout:
                handleLogout();
                break;
            default:
                break;
        }
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

    @Override
    protected void onPause() {
        performCleanup();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        performCleanup();
        super.onDestroy();
    }

    private void requestExternalStoragePermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 245);
        } else {
            //
        }
    }

    private void performCleanup() {
        peerCommunicator.cleanup();

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }
}
