package com.tcd.yaatra.utils;

import org.junit.Test;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.ByteOrder;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class NetworkUtilsTest {

    int ipAddr = 1234;

    NetworkUtils networkUtils = new NetworkUtils();

    @Test
    public void getDottedDecimalIP() {
        String str_ip = networkUtils.getDottedDecimalIP(ipAddr);
        assertEquals("210.4.0.0",str_ip);
    }
    }

