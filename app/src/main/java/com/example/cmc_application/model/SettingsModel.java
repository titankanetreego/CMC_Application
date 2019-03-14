package com.example.cmc_application.model;

public class SettingsModel {

    public int id = 0;
    public String key = "";
    public String time = "";
    public String voltage = "";
    public String current = "";
    public String year = "";
    public String month = "";
    public String serial = "";

    public SettingsModel(){
    }

    public SettingsModel(int id, String key, String time, String voltage, String current, String year, String month, String serial){
        this.id = id;
        this.key = key;
        this.time = time;
        this.voltage = voltage;
        this.current = current;
        this.year = year;
        this.month = month;
        this.serial = serial;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrent() {
        return current;
    }

    public String getKey() {
        return key;
    }

    public String getMonth() {
        return month;
    }

    public String getSerial() {
        return serial;
    }

    public String getTime() {
        return time;
    }

    public String getVoltage() {
        return voltage;
    }

    public String getYear() {
        return year;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
