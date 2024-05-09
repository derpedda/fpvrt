package net.pedda.fpvracetimer.db;

import java.util.LinkedList;
import java.util.List;

public class BLEObjectModel {
    private String uuid;
    private String rssi;
    private String type;
    private String instance;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }


    public BLEObjectModel(String uuid, String rssi, String type, String instance) {
        this.uuid = uuid;
        this.rssi = rssi;
        this.type = type;
        this.instance = instance;
    }

    public static List<BLEObjectModel> genList() {
        LinkedList<BLEObjectModel> l = new LinkedList<BLEObjectModel>();

        l.add(new BLEObjectModel("TESTESTEST","-20dBm","Eddy","012345"));

        return l;
    }

}
