package com.tcd.yaatra.ui.fragments;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.FragmentOfflineMapsBinding;
import com.tcd.yaatra.databinding.FragmentSettingsBinding;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class OfflineMaps extends BaseFragment<FragmentOfflineMapsBinding> {

    @Override
    public int getFragmentResourceId() {
        return R.layout.fragment_offline_maps;
    }

    public static final int BUFFER_SIZE = 8 * 1024;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        TextView sampleText = (TextView) this.layoutDataBinding.sampleText;

        BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(final Context context, Intent intent) {

                if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){

                    unZipFiles();
                    sampleText.setText("Downloading and unzipping completed");
                }

            }
        };

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

        File zipFile = new File(apkStorage, "ireland_map-gh");


        if (!outputFile.exists()) {

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
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            getContext().registerReceiver(downloadReceiver, filter);


        }
        else if (!zipFile.exists()) {
            unZipFiles();
            sampleText.setText("Downloading and unzipping completed");
        }

        return view;

    }

    void unZipFiles(){
        ZipInputStream zipIn = null;
        try {
            zipIn = new ZipInputStream(new FileInputStream(Environment.getExternalStorageDirectory() + "/"
                    + "Yaatra Downloads/ireland_map.ghz"));
            File mapFolder = new File(Environment.getExternalStorageDirectory() + "/"
                    + "Yaatra Downloads/ireland_map" + "-gh");
            File destDir = new File(mapFolder.getAbsolutePath());
            if (destDir.exists()) {
                recursiveDelete(destDir);
            }
            if (!destDir.exists()) {
                destDir.mkdir();
            }
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                String filePath = mapFolder.getAbsolutePath() + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    extractFile(zipIn, filePath);
                } else {
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (zipIn!=null) {
                try {
                    zipIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void recursiveDelete(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) for (File child : fileOrDirectory.listFiles())
            recursiveDelete(child);
        try {
            fileOrDirectory.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractFile(ZipInputStream zipIn,
                             String filePath) throws IOException {
        BufferedOutputStream bos = null;
        try{
            bos = new BufferedOutputStream(new FileOutputStream(filePath));
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
        finally
        {
            if (bos!=null) bos.close();
        }
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
