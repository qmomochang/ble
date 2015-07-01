package com.htc.blelib.v1.internal.common;

import java.util.concurrent.ExecutorService;

import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.internal.component.le.CsBleTransceiver;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;



public class CsConnectivityTask implements Comparable<CsConnectivityTask> {

    private final static String TAG = "CsConnectivityTask";
    private final static boolean bPerformanceNotify = true;

    protected Messenger mMessenger;
    protected ExecutorService mExecutor;
    protected CsBleTransceiver mCsBleTransceiver;
    protected long mTimePrev;



    public CsConnectivityTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor) {

        mCsBleTransceiver = csBleTransceiver;
        mMessenger = messenger;
        mExecutor = executor;
    }



    @Override
    public int compareTo(CsConnectivityTask that) {

        return 0;
    }



    public void execute() throws Exception {

    }



    public void error(Exception e) {


    }



    protected void from() {

        if (bPerformanceNotify) {

            mTimePrev = System.currentTimeMillis();
        }
    }



    protected void to(String task) {

        if (bPerformanceNotify) {

            long timeCurr = System.currentTimeMillis();
            long timeDiff = timeCurr - mTimePrev;

            Log.d(TAG, "[CS][MPerf] [" + task + "] costs: " + timeDiff + " ms");

            sendMessage(task, timeDiff);
        }
    }



    private void sendMessage(String task, long timeCost) {

        try {

            Message outMsg = Message.obtain();
            outMsg.what = ICsConnectivityService.CB_PERFORMANCE_RESULT;
            Bundle outData = new Bundle();

            outData.putString(ICsConnectivityService.PARAM_TASK_NAME, task);
            outData.putLong(ICsConnectivityService.PARAM_TIME_COST_MS, timeCost);

            outMsg.setData(outData);

            mMessenger.send(outMsg);

        } catch (RemoteException e) {

            e.printStackTrace();
        }
    }
}
