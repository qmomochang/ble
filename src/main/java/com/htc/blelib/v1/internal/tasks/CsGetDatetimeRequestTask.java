package com.htc.blelib.v1.internal.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.internal.callables.CsBleReadCallable;
import com.htc.blelib.v1.internal.callables.CsBleReceiveNotificationCallable;
import com.htc.blelib.v1.internal.callables.CsBleSetNotificationCallable;
import com.htc.blelib.v1.internal.callables.CsBleWriteCallable;
import com.htc.blelib.v1.internal.common.Common;
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



public class CsGetDatetimeRequestTask extends CsConnectivityTask {

    private final static String TAG = "CsGetDatetimeRequestTask";

    private BluetoothDevice mBluetoothDevice;

    public CsGetDatetimeRequestTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device) {

        super(csBleTransceiver, messenger, executor);

        mBluetoothDevice = device;
    }

    @Override
    public void execute() throws Exception {

        super.execute();

        super.from();

        BluetoothGattCharacteristic result;
        Future<BluetoothGattCharacteristic> futureA0, futureA1, futureB;

        byte[] inArray = new byte[1];
        inArray[0] = (byte) 0x00;

        futureA0 = mExecutor.submit(new CsBleReceiveNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.SET_DATETIME_REQUEST));
        futureA1 = mExecutor.submit(new CsBleSetNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.SET_DATETIME_REQUEST, true));

        if (futureA1.get() == null) {

            sendMessage(false, null);
            unregisterNotify(CsBleGattAttributes.CsV1CommandEnum.SET_DATETIME_REQUEST);
            return;
        }

        futureB = mExecutor.submit(new CsBleWriteCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.GET_DATETIME_REQUEST, inArray));

        result = futureB.get();

        if (result != null) {

            result = futureA0.get();
            if (result != null)
            {
                byte [] retArray;
                retArray = CsBleGattAttributeUtil.getDatetimeResultEvent(result);
                sendMessage(true, retArray);
            }
            else
            {
                sendMessage(false, null);
                unregisterNotify(CsBleGattAttributes.CsV1CommandEnum.CS_BLE_NAME_REQUEST);
            }

        } else {

            sendMessage(false, null);
        }
        super.to(TAG);
    }

    private void sendMessage(boolean result, byte [] retArray) {

        try {

            Message outMsg = Message.obtain();

            outMsg.what = ICsConnectivityService.CB_GET_DATE_TIME_RESULT;

            Bundle outData = new Bundle();

            if (result) {
                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_SUCCESS);
            } else {
                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_FAIL);
            }

            outData.putByteArray(ICsConnectivityService.PARAM_CS_NAME, retArray);

            outMsg.setData(outData);

            mMessenger.send(outMsg);

        } catch (RemoteException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void error(Exception e) {

        sendMessage(false, null);
    }

    private void unregisterNotify(CsBleGattAttributes.CsV1CommandEnum commandID) throws Exception {

        Future<BluetoothGattCharacteristic> future;

        future = mExecutor.submit(new CsBleSetNotificationCallable(mCsBleTransceiver, mBluetoothDevice, commandID, false));
        if (future.get() == null) {

            Log.d(TAG, "[CS] unregisterNotify error!!!");
            return;
        }
    }

}
