package me.pearjelly.wxrobot.data.pojo;

/**
 * Created by xiaobinghan on 16/4/17.
 */
public class AccountInfo {
    public AccountInfo(int diid, String serial, String imei, String macaddr, String bluemac, String androidid, String brand,  String manufacturer, String model, String number, String imsi, String simserial, String wxpasswd) {
        this.diid = diid;
        this.serial = serial;
        this.imei = imei;
        this.macaddr = macaddr;
        this.bluemac = bluemac;
        this.androidid = androidid;
        this.brand = brand;
        this.manufacturer = manufacturer;
        this.model = model;
        this.number = number;
        this.imsi = imsi;
        this.simserial = simserial;
        this.wxpasswd = wxpasswd;
    }

    int diid;
    String serial;
    String imei;
    String macaddr;
    String bluemac;
    String androidid;
    String brand;
    String manufacturer;
    String model;
    String number;
    String imsi;
    String simserial;
    String wxpasswd;

    public int getDiid() {
        return diid;
    }

    public String getSerial() {
        return serial;
    }

    public String getImei() {
        return imei;
    }

    public String getMacaddr() {
        return macaddr;
    }

    public String getBluemac() {
        return bluemac;
    }

    public String getAndroidid() {
        return androidid;
    }

    public String getNumber() {
        return number;
    }

    public String getImsi() {
        return imsi;
    }

    public String getBrand() {
        return brand;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getSimserial() {
        return simserial;
    }

    public String getWxpasswd() {
        return wxpasswd;
    }

    @Override
    public String toString() {
        return "AccountInfo{" +
                "diid=" + diid +
                ", serial='" + serial + '\'' +
                ", imei='" + imei + '\'' +
                ", macaddr='" + macaddr + '\'' +
                ", bluemac='" + bluemac + '\'' +
                ", androidid='" + androidid + '\'' +
                ", brand='" + brand + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", number='" + number + '\'' +
                ", imsi='" + imsi + '\'' +
                ", simserial='" + simserial + '\'' +
                ", wxpasswd='" + wxpasswd + '\'' +
                '}';
    }
}
