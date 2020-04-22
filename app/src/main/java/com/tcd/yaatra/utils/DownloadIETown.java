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
        InputStream is = context.getResources().openRawResource(R.raw.ie_towns_sample);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";
        try
        {
            reader.readLine();
            while((line=reader.readLine())!=null)
            {
                String[] tokens = line.split(",");

                IETownData ieTownData = new IETownData();
//                ieTownData.setId(Integer.parseInt(tokens[0]));
                ieTownData.setName(tokens[1]);
//                ieTownData.setIrish_name(tokens[2]);
//                ieTownData.setCounty(tokens[3]);
//                ieTownData.setCountry(tokens[4]);
//                ieTownData.setEircode(tokens[5]);
//                ieTownData.setGrid_reference(tokens[6]);
//                ieTownData.setEasting(Long.parseLong(tokens[7]));
//                ieTownData.setNorthing(Long.parseLong(tokens[8]));
                ieTownData.setLatitude(Double.parseDouble(tokens[9]));
                ieTownData.setLongitude(Double.parseDouble(tokens[10]));
//                ieTownData.setPostal_town(tokens[11]);
//                ieTownData.setLocal_government_area(tokens[12]);
//                ieTownData.setProvince(tokens[13]);
//                ieTownData.setNuts3_region(tokens[14]);
//                ieTownData.setType(tokens[15]);

                ieTownDataList.add(ieTownData);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return ieTownDataList;    }
}
