package com.tcd.yaatra.WifiDirectP2PHelper.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TravellerInfo {

    private int age;
    private Gender gender = Gender.NotSpecified;
    private String name = "";
    private String source = "";
    private String destination = "";
    private TravellerStatus status = TravellerStatus.None;
    private LocalDateTime requestStartTime = LocalDateTime.now();
    private String ipAddress = "";
    private int portNumber;

    private static final String AGE = "AGE";
    private static final String GENDER = "GENDER";
    private static final String NAME = "NAME";
    private static final String SOURCE = "SOURCE";
    private static final String DESTINATION = "DESTINATION";
    private static final String STATUS = "STATUS";
    private static final String REQUEST_START_TIME = "REQUESTSTARTTIME";
    private static final String IP_ADDRESS = "IPADDRESS";
    private static final String PORT_NUMBER = "PORTNUMBER";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public TravellerStatus getStatus() {
        return status;
    }

    public void setStatus(TravellerStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestStartTime() {
        return requestStartTime;
    }

    public void setRequestStartTime(LocalDateTime requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public Map<String, String> SerializeToMap(){

        Map<String, String> travellerInfoRecord = new HashMap<String, String>();
        travellerInfoRecord.put(NAME, getName());
        travellerInfoRecord.put(GENDER, getGender().toString());
        travellerInfoRecord.put(AGE, Integer.toString(getAge()));
        travellerInfoRecord.put(SOURCE, getSource());
        travellerInfoRecord.put(DESTINATION, getDestination());
        travellerInfoRecord.put(STATUS, getStatus().toString());
        travellerInfoRecord.put(REQUEST_START_TIME, getRequestStartTime().format(DATE_TIME_FORMATTER));
        travellerInfoRecord.put(IP_ADDRESS, getIpAddress());
        travellerInfoRecord.put(PORT_NUMBER, Integer.toString(getPortNumber()));

        return travellerInfoRecord;
    }

    public static TravellerInfo DeserializeFromMap(Map<String, String> travellerInfoRecord){
        TravellerInfo info = new TravellerInfo();

        info.setName(travellerInfoRecord.get(NAME));
        info.setGender(Gender.valueOf(travellerInfoRecord.get(GENDER)));
        info.setAge(Integer.parseInt(travellerInfoRecord.get(AGE)));
        info.setSource(travellerInfoRecord.get(SOURCE));
        info.setDestination(travellerInfoRecord.get(DESTINATION));
        info.setStatus(TravellerStatus.valueOf(travellerInfoRecord.get(STATUS)));
        info.setRequestStartTime(LocalDateTime.parse(travellerInfoRecord.get(REQUEST_START_TIME), DATE_TIME_FORMATTER));
        info.setIpAddress(travellerInfoRecord.get(IP_ADDRESS));
        info.setPortNumber(Integer.parseInt(travellerInfoRecord.get(PORT_NUMBER)));

        return info;
    }
}