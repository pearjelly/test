package me.pearjelly.wxrobot.net.pojo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by hxb on 2016/5/1.
 */
public class DeviceInfo {
    public DeviceInfo() {
    }

    public DeviceInfo(int diid, String serial, String imei, String wifimac, String bluemac, String androidid, String brand, String manufacturer, String model, String netcountryiso, String simcountryiso, String phonenumber, String imsi, String simserial, String wxpasswd) {
        this.diid = diid;
        this.serial = serial;
        this.imei = imei;
        this.wifimac = wifimac;
        this.bluemac = bluemac;
        this.androidid = androidid;
        this.brand = brand;
        this.manufacturer = manufacturer;
        this.model = model;
        this.netcountryiso = netcountryiso;
        this.simcountryiso = simcountryiso;
        this.phonenumber = phonenumber;
        this.imsi = imsi;
        this.simserial = simserial;
        this.wxpasswd = wxpasswd;
    }

    public int diid;
    public String serial;
    public String imei;
    public String wifimac;
    public String bluemac;
    public String androidid;
    public String brand;
    public String manufacturer;
    public String model;
    public String netcountryiso;
    public String simcountryiso;
    public String phonenumber;
    public String imsi;
    public String simserial;
    public String wxpasswd;

    public static DeviceInfo getDeviceInfo(Activity activity) {
        TelephonyManager phone = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        WifiManager wifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);

        String imei = phone.getDeviceId();
        String imsi = phone.getSubscriberId();
        String phonenumber = phone.getLine1Number();
        String simcountryiso = phone.getSimCountryIso();
        String netcountryiso = phone.getNetworkCountryIso();
        String simserial = phone.getSimSerialNumber();
        String wifimac = wifi.getConnectionInfo().getMacAddress();
        String bluemac = BluetoothAdapter.getDefaultAdapter().getAddress();
        String androidid = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        String serial = Build.SERIAL;
        String brand = Build.BRAND;
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (imei.equals("357070058816663")) {
//            if (TextUtils.isEmpty(phonenumber)) {
//                phonenumber = "+8613161294255";
//            }
            if (TextUtils.isEmpty(simserial)) {
                simserial = "75763419623121926899";
            }
            if (TextUtils.isEmpty(imsi)) {
                imsi = "963764816161647";
            }
        }
        if (!TextUtils.isEmpty(phonenumber) && phonenumber.startsWith("+86")) {
            phonenumber = phonenumber.substring(3);
        }
        return new DeviceInfo(0, serial, imei, wifimac, bluemac, androidid, brand, manufacturer, model, netcountryiso, simcountryiso, phonenumber, imsi, simserial, "");
    }

    public int getDiid() {
        return diid;
    }

    public void setDiid(int diid) {
        this.diid = diid;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getWifimac() {
        return wifimac;
    }

    public void setWifimac(String wifimac) {
        this.wifimac = wifimac;
    }

    public String getBluemac() {
        return bluemac;
    }

    public void setBluemac(String bluemac) {
        this.bluemac = bluemac;
    }

    public String getAndroidid() {
        return androidid;
    }

    public void setAndroidid(String androidid) {
        this.androidid = androidid;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNetcountryiso() {
        return netcountryiso;
    }

    public void setNetcountryiso(String netcountryiso) {
        this.netcountryiso = netcountryiso;
    }

    public String getSimcountryiso() {
        return simcountryiso;
    }

    public void setSimcountryiso(String simcountryiso) {
        this.simcountryiso = simcountryiso;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getSimserial() {
        return simserial;
    }

    public void setSimserial(String simserial) {
        this.simserial = simserial;
    }

    public String getWxpasswd() {
        return wxpasswd;
    }

    public void setWxpasswd(String wxpasswd) {
        this.wxpasswd = wxpasswd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceInfo that = (DeviceInfo) o;

        if (diid != that.diid) return false;
        if (serial != null ? !serial.equals(that.serial) : that.serial != null) return false;
        if (imei != null ? !imei.equals(that.imei) : that.imei != null) return false;
        if (wifimac != null ? !wifimac.equals(that.wifimac) : that.wifimac != null) return false;
        if (bluemac != null ? !bluemac.equals(that.bluemac) : that.bluemac != null) return false;
        if (androidid != null ? !androidid.equals(that.androidid) : that.androidid != null)
            return false;
        if (brand != null ? !brand.equals(that.brand) : that.brand != null) return false;
        if (manufacturer != null ? !manufacturer.equals(that.manufacturer) : that.manufacturer != null)
            return false;
        if (model != null ? !model.equals(that.model) : that.model != null) return false;
        if (netcountryiso != null ? !netcountryiso.equals(that.netcountryiso) : that.netcountryiso != null)
            return false;
        if (simcountryiso != null ? !simcountryiso.equals(that.simcountryiso) : that.simcountryiso != null)
            return false;
        if (phonenumber != null ? !phonenumber.equals(that.phonenumber) : that.phonenumber != null)
            return false;
        if (imsi != null ? !imsi.equals(that.imsi) : that.imsi != null) return false;
        if (simserial != null ? !simserial.equals(that.simserial) : that.simserial != null)
            return false;
        return wxpasswd != null ? wxpasswd.equals(that.wxpasswd) : that.wxpasswd == null;

    }

    @Override
    public int hashCode() {
        int result = diid;
        result = 31 * result + (serial != null ? serial.hashCode() : 0);
        result = 31 * result + (imei != null ? imei.hashCode() : 0);
        result = 31 * result + (wifimac != null ? wifimac.hashCode() : 0);
        result = 31 * result + (bluemac != null ? bluemac.hashCode() : 0);
        result = 31 * result + (androidid != null ? androidid.hashCode() : 0);
        result = 31 * result + (brand != null ? brand.hashCode() : 0);
        result = 31 * result + (manufacturer != null ? manufacturer.hashCode() : 0);
        result = 31 * result + (model != null ? model.hashCode() : 0);
        result = 31 * result + (netcountryiso != null ? netcountryiso.hashCode() : 0);
        result = 31 * result + (simcountryiso != null ? simcountryiso.hashCode() : 0);
        result = 31 * result + (phonenumber != null ? phonenumber.hashCode() : 0);
        result = 31 * result + (imsi != null ? imsi.hashCode() : 0);
        result = 31 * result + (simserial != null ? simserial.hashCode() : 0);
        result = 31 * result + (wxpasswd != null ? wxpasswd.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "diid=" + diid +
                ", serial='" + serial + '\'' +
                ", imei='" + imei + '\'' +
                ", wifimac='" + wifimac + '\'' +
                ", bluemac='" + bluemac + '\'' +
                ", androidid='" + androidid + '\'' +
                ", brand='" + brand + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", netcountryiso='" + netcountryiso + '\'' +
                ", simcountryiso='" + simcountryiso + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", imsi='" + imsi + '\'' +
                ", simserial='" + simserial + '\'' +
                ", wxpasswd='" + wxpasswd + '\'' +
                '}';
    }
}
