package com.tcd.yaatra.WifiDirectP2PHelper;

import java.util.Map;

public interface PeerListener {

    void processPeerServiceInfo(Map<String, String> receivedInfo);
}