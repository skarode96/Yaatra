package com.tcd.yaatra.services.api.yaatra.models;

import java.util.List;

public class DailyCommuteResponse {
    private List<Object> objects;

    public DailyCommuteResponse(List<Object> objects) {
        this.objects = objects;
    }

    public Object getObjects() {
        return objects;
    }

    public void setObjects(List<Object> objects) {
        this.objects = objects;
    }

    @Override
    public String toString() {
        return "DailyCommuteResponse{" +
                "objects=" + this.objects +
                '}';
    }
}
