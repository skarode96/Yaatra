package com.tcd.yaatra.utils;

import android.content.Context;

import com.tcd.yaatra.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DownloadIETown {

    private List<IETownData> ieTownDataList = new ArrayList<>();

    public List<IETownData> getIeTownDataList() {
        return ieTownDataList;
    }

    public void setIeTownDataList(List<IETownData> ieTownDataList) {
        this.ieTownDataList = ieTownDataList;
    }

    @Inject
    public DownloadIETown(){}

    public List<IETownData> readCsv(Context context)
    {
        ieTownDataList = new ArrayList<>();
        InputStream is = context.getResources().openRawResource(R.raw.multiuse_community_centres);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";
        try
        {
            reader.readLine();
            while((line=reader.readLine())!=null)
            {
                String[] tokens = line.split(",");

                IETownData ieTownData = new IETownData();
                ieTownData.setLatitude(Double.parseDouble(tokens[1]));
                ieTownData.setLongitude(Double.parseDouble(tokens[0]));
                ieTownData.setName(tokens[4]);

                ieTownDataList.add(ieTownData);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return ieTownDataList;    }
}
