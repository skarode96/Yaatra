package com.tcd.yaatra.ui.fragments;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.FragmentOfflineMapsBinding;
import com.tcd.yaatra.databinding.FragmentSettingsBinding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class OfflineMaps extends BaseFragment<FragmentOfflineMapsBinding> {

    @Override
    public int getFragmentResourceId() {
        return R.layout.fragment_offline_maps;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        File apkStorage = null;

        String sPermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (!checkPermission(sPermission, getActivity()))
        {
            String sPermission2 = android.Manifest.permission.ACCESS_FINE_LOCATION;
            requestPermission(new String[]{sPermission, sPermission2});
        }

        DownloadManager dm = (DownloadManager) getActivity().getSystemService(getContext().DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://vsrv15044.customer.xenway.de/maps/maps/2019-10/europe_ireland-and-northern-ireland.ghz"));

        apkStorage = new File(
                Environment.getExternalStorageDirectory() + "/"
                        + "Yaatra Downloads");
        if (!apkStorage.exists()) {
            apkStorage.mkdir();
        }
        File outputFile = new File(apkStorage, "ireland_map.ghz");

        request.setDestinationUri(Uri.fromFile(outputFile));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/yaatra");
        long enqueueId = dm.enqueue(request);

        File idFile = new File(apkStorage, "ireland_map.id");

        try(FileWriter sw = new FileWriter(idFile, false);
            BufferedWriter bw = new BufferedWriter(sw))
        {
            bw.write("" + enqueueId);
            bw.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return view;

    }

    private void requestPermission(String[] sPermission){
        if(getActivity() != null)
            requestPermissions(sPermission, 1);
    }

    private static boolean checkPermission(String sPermission, Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, sPermission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}
