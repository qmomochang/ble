package com.htc.blelib.v1;

import java.util.Calendar;
import java.util.List;

import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.internal.common.CsConnectivityTask;
import com.htc.blelib.v1.internal.component.le.CsBleGattAttributes;

import com.htc.blelib.v1.internal.tasks.CsBleConnectTask;
import com.htc.blelib.v1.internal.tasks.CsNameTask;
import com.htc.blelib.v1.internal.tasks.CsBleReadBatteryTask;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Messenger;
import android.util.Log;



public class CsConnectivityService extends CsConnectivityServiceImpl implements ICsConnectivityService {

    private final static String TAG = "CsConnectivityService";



    public CsConnectivityService(Context context, Messenger messenger) {

        super(context, messenger);
    }

    @Override
    public boolean csOpen() {

        Log.d(TAG, "[CS] csOpen++");

        boolean ret = false;

        try {

            open();

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csOpen exception: " + e);
        }

        Log.d(TAG, "[CS] csOpen--");

        return ret;
    }



    @Override
    public boolean csClose() {

        Log.d(TAG, "[CS] csClose++");

        boolean ret = false;

        try {

            close();

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csClose exception: " + e);
        }

        Log.d(TAG, "[CS] csClose--");

        return ret;
    }

    @Override
    public boolean csBleConnect(BluetoothDevice device) {

        Log.d(TAG, "[CS] csBleConnect++");

        boolean ret = false;

        try {

            CsConnectivityTask task = new CsBleConnectTask(mCsBleTransceiver, mMessenger, mExecutor, device, true);
            addTask(task);

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csBleConnect exception: " + e);
        }

        Log.d(TAG, "[CS] csBleConnect--");

        return ret;
    }



    @Override
    public boolean csBleDisconnect(BluetoothDevice device) {

        Log.d(TAG, "[CS] csBleDisconnect++");

        boolean ret = false;

        try {

            CsConnectivityTask task = new CsBleConnectTask(mCsBleTransceiver, mMessenger, mExecutor, device, false);
            addTask(task);

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csBleDisconnect exception: " + e);
        }

        Log.d(TAG, "[CS] csBleDisconnect--");

        return ret;
    }



    @Override
    public boolean csBleDisconnectForce(BluetoothDevice device) {

        Log.d(TAG, "[CS] csBleDisconnectForce++");

        boolean ret = false;

        try {

            CsConnectivityTask task = new CsBleConnectTask(mCsBleTransceiver, mMessenger, mExecutor, device, false, true);
            addTask(task);

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csBleDisconnectForce exception: " + e);
        }

        Log.d(TAG, "[CS] csBleDisconnectForce--");

        return ret;
    }

    @Override
    public boolean csSetName(BluetoothDevice device, String name) {

        Log.d(TAG, "[CS] csSetName++");

        boolean ret = false;

        try {

            CsConnectivityTask task = new CsNameTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsNameTask.ACTION_SET_NAME, name);
            addTask(task);

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csSetName exception: " + e);
        }

        Log.d(TAG, "[CS] csSetName--");

        return ret;
    }



    @Override
    public boolean csGetName(BluetoothDevice device) {

        Log.d(TAG, "[CS] csGetName++");

        boolean ret = false;

        try {

            CsConnectivityTask task = new CsNameTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsNameTask.ACTION_GET_NAME, null);
            addTask(task);

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csGetName exception: " + e);
        }

        Log.d(TAG, "[CS] csGetName--");

        return ret;
    }

    @Override
    public boolean csBleReadBattery(BluetoothDevice device) {

        Log.d(TAG, "[CS] csBleReadBattery++");

        boolean ret = false;

        try {

            CsConnectivityTask task = new CsBleReadBatteryTask(mCsBleTransceiver, mMessenger, mExecutor, device);
            addTask(task);

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csBleReadBattery exception: " + e);
        }

        Log.d(TAG, "[CS] csBleReadBattery--");

        return ret;
    }

}
