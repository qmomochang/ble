package com.htc.blelib.v1.internal.component.le;

import com.htc.blelib.interfaces.ICsConnectivityDevice.CsVersion;

import android.bluetooth.BluetoothDevice;



public interface ICsBleScannerListener {

    public void onScanHit(BluetoothDevice device, CsVersion deviceVersion);
    public void onScanHitConnected(BluetoothDevice device, CsVersion deviceVersion);
       public void onScanComplete();
}
