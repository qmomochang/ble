package com.htc.blelib.internal.common;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.htc.blelib.interfaces.ICsConnectivityDevice;



public class CsConnectivityDevice implements ICsConnectivityDevice {

    private final static String TAG = "CsConnectivityDevice";

    private BluetoothDevice mBluetoothDevice;
    private String mName;
    private String mAddress;
    private CsState mCsState;

    private CsStateBle mCsStateBle;

    private BootUpReady mBootUpReady;
    private String mIpAddress;

    private int mConnectCount;
    private int mDisconnectCount;
    private int mVersionBle;
    private CsVersion mVersion;



    public CsConnectivityDevice(BluetoothDevice device) {

        mBluetoothDevice = device;
        mName = device.getName();
        mAddress = device.getAddress();

        mCsState = CsState.CSSTATE_STANDBY;

        mCsStateBle = CsStateBle.CSSTATE_BLE_DISCONNECTED;

        mBootUpReady = BootUpReady.BOOTUP_UNKNOWN;
        mIpAddress = null;

        mConnectCount = 0;
        mDisconnectCount = 0;

        mVersionBle = -1;
        mVersion = CsVersion.UNKNOWN;
    }



    public void setCsState(CsState state) {

        Log.d(TAG, "[CS] setCsState: " + mCsState + " --> " + state);
        mCsState = state;
    }



    @Override
    public CsState getCsState() {

        return mCsState;
    }



    public void setCsStateBle(CsStateBle state) {

        Log.d(TAG, "[CS] setCsStateBle: " + mCsStateBle + " --> " + state);
        mCsStateBle = state;
    }



    public CsStateBle getCsStateBle() {

        return mCsStateBle;
    }



    public void setCsBootUpReady(BootUpReady ready) {

        mBootUpReady = ready;
    }



    public BootUpReady getCsBootUpReady() {

        return mBootUpReady;
    }



    public void setIpAddress(String ip) {

        mIpAddress = ip;
    }



    public String getIpAddress() {

        return mIpAddress;
    }



    public void setConnectCount(int count) {

        mConnectCount = count;
    }



    public int getConnectCount() {

        return mConnectCount;
    }



    public void setDisconnectCount(int count) {

        mDisconnectCount = count;
    }



    public int getDisconnectCount() {

        return mDisconnectCount;
    }



    public void setVersionBle(int version) {

        Log.d(TAG, "[CS] setVersionBle() = " + version);
        mVersionBle = version;
    }



    public int getVersionBle() {

        Log.d(TAG, "[CS] getVersionBle() = " + mVersionBle);
        return mVersionBle;
    }



    @Override
    public BluetoothDevice getBluetoothDevice() {

        return mBluetoothDevice;
    }



    @Override
    public String getName() {

        return mName;
    }



    @Override
    public void setName(String name) {

        mName = name;
    }



    @Override
    public String getAddress() {

        return mAddress;
    }



    @Override
    public void setAddress(String address) {

        mAddress = address;
    }



    @Override
    public CsVersion getVersion() {
        return mVersion;
    }



    @Override
    public void setVersion(CsVersion version) {
        mVersion = version;
    }
}
