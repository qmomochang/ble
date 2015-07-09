package com.htc.blelib.v1.internal.callables;

import java.util.concurrent.Callable;

import com.htc.blelib.interfaces.ICsConnectivityDevice.CsVersion;
import com.htc.blelib.interfaces.ICsConnectivityScanner;
import com.htc.blelib.interfaces.ICsConnectivityScanner.ScanResult;
import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.internal.component.le.CsBleScanner;
import com.htc.blelib.v1.internal.component.le.CsBleScannerListener;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;



public class CsScanStartCallable implements Callable<Integer> {

    private static final String TAG = "CsScanStartCallable";

    protected CsBleScanner mCsBleScanner;
    protected Messenger mMessenger;



    private CsBleScannerListener mCsBleScannerListener = new CsBleScannerListener() {

        @Override
        public void onScanHit(BluetoothDevice device, CsVersion deviceVersion) {

            Log.d(TAG, "[CS] onScanHit. device = " + device);

            try {

                Message outMsg = Message.obtain();
                outMsg.what = ICsConnectivityService.CB_BLE_SCAN_RESULT;
                Bundle outData = new Bundle();
                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ScanResult.SCAN_RESULT_HIT);
                outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);
                outData.putSerializable(ICsConnectivityScanner.PARAM_BLUETOOTH_DEVICE_VERSION, deviceVersion);
                outMsg.setData(outData);

                mMessenger.send(outMsg);

            } catch (RemoteException e) {

                e.printStackTrace();
            }
        }
    };



    public CsScanStartCallable(CsBleScanner scanner, Messenger messenger) {

        mCsBleScanner = scanner;
        mMessenger = messenger;
    }



    @Override
    public Integer call() throws Exception {

        Integer ret = 0;

        mCsBleScanner.registerListener(mCsBleScannerListener);

        if (mCsBleScanner.scanStart()) {


        } else {

            Message outMsg = Message.obtain();
            outMsg.what = ICsConnectivityService.CB_BLE_SCAN_RESULT;
            Bundle outData = new Bundle();
            outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ScanResult.SCAN_RESULT_ERROR);
            outMsg.setData(outData);

            mMessenger.send(outMsg);

            ret = -1;
        }

        mCsBleScanner.unregisterListener(mCsBleScannerListener);

        return ret;
    }
}
