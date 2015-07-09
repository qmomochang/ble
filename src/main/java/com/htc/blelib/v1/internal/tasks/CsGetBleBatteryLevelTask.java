package com.htc.blelib.v1.internal.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.htc.blelib.internal.common.CsConnectivityDevice;
import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.internal.callables.CsBleReadCallable;
import com.htc.blelib.v1.internal.common.CsConnectivityTask;
import com.htc.blelib.v1.internal.component.le.CsBleGattAttributeUtil;
import com.htc.blelib.v1.internal.component.le.CsBleGattAttributes;
import com.htc.blelib.v1.internal.component.le.CsBleTransceiver;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;



public class CsGetBleBatteryLevelTask extends CsConnectivityTask {

    private final static String TAG = "CsGetBleBatteryLevelTask";
    private BluetoothDevice mBluetoothDevice;



    public CsGetBleBatteryLevelTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device) {

        super(csBleTransceiver, messenger, executor);

        mBluetoothDevice = device;
    }



    @Override
    public void execute() throws Exception {

        super.execute();

        super.from();

        BluetoothGattCharacteristic result;
        Future<BluetoothGattCharacteristic> future;

        future = mExecutor.submit(new CsBleReadCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CS_BATTERY_SERVICE, CsBleGattAttributes.CS_V1_BATTERY_LEVEL));
        result = future.get();
        if (result != null) {

            int batteryLevel = CsBleGattAttributeUtil.getHwStatus_BatteryLevel(result);

            CsConnectivityDevice csDevice = mCsBleTransceiver.getCsConnectivityDeviceGroup().getDevice(mBluetoothDevice);
            Log.d(TAG, "[CS] csDevice = " + csDevice);
            if (csDevice != null) {

                Integer value = new Integer(batteryLevel);
                if (value != null) {

                    csDevice.setVersionBle((int)(value));
                }
            }

            sendMessage(true, batteryLevel);

        } else {

            sendMessage(false, -1);
        }

        super.to(TAG);
    }



    private void sendMessage(boolean result, int powerLevel) {

        try {

            Message outMsg = Message.obtain();
            outMsg.what = ICsConnectivityService.CB_GET_POWER_LEVEL_RESULT;
            Bundle outData = new Bundle();

            if (result) {

                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_SUCCESS);

            } else {

                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_FAIL);
            }

            if (powerLevel != -1) {

                outData.putInt(ICsConnectivityService.PARAM_CS_POWER_LEVEL, powerLevel);
            }

            outMsg.setData(outData);

            mMessenger.send(outMsg);

        } catch (RemoteException e) {

            e.printStackTrace();
        }
    }



    @Override
    public void error(Exception e) {

        sendMessage(false, -1);
    }
}
