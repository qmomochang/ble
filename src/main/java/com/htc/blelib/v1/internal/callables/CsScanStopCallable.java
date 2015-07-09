package com.htc.blelib.v1.internal.callables;

import java.util.concurrent.Callable;

import com.htc.blelib.interfaces.ICsConnectivityScanner.ScanResult;
import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.internal.component.le.CsBleScanner;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;



public class CsScanStopCallable implements Callable<Integer> {

    private final static String TAG = "CsScanStopCallable";

    protected CsBleScanner mCsBleScanner;
    protected Messenger mMessenger;



    public CsScanStopCallable(CsBleScanner scanner, Messenger messenger) {

        mCsBleScanner = scanner;
        mMessenger = messenger;
    }



    @Override
    public Integer call() throws Exception {

        Integer ret = 0;

        if (mCsBleScanner.scanStop()) {

            Message outMsg = Message.obtain();
            outMsg.what = ICsConnectivityService.CB_BLE_SCAN_RESULT;
            Bundle outData = new Bundle();
            outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ScanResult.SCAN_RESULT_COMPLETE);
            outMsg.setData(outData);

            mMessenger.send(outMsg);

        } else {

            Message outMsg = Message.obtain();
            outMsg.what = ICsConnectivityService.CB_BLE_SCAN_RESULT;
            Bundle outData = new Bundle();
            outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ScanResult.SCAN_RESULT_ERROR);
            outMsg.setData(outData);

            mMessenger.send(outMsg);
        }

        return ret;
    }
}
