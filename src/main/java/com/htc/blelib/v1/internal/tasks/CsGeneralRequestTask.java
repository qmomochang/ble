package com.htc.blelib.v1.internal.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.htc.blelib.v1.interfaces.ICsConnectivityService;

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

public class CsGeneralRequestTask extends CsConnectivityTask {

    private final static String TAG = "CsGeneralRequestTask";

    public final static int ACTION_READ = 0;
    public final static int ACTION_WRITE = 1;
    public final static int ACTION_GET_HW_STATUS = 2;

    private BluetoothDevice mBluetoothDevice;
    private int mAction;
    private int mUnit;
    private int mLanguage;
    private int mSound;
    private int mBei;

    public CsGeneralRequestTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device, int action, int unit, int language, int sound, int bei ) {

        super(csBleTransceiver, messenger, executor);

        mBluetoothDevice    = device;

        mAction             = action;
        mUnit               = unit;
        mLanguage           = language;
        mSound              = sound;
        mBei                = bei;
    }

    @Override
    public void execute() throws Exception {

        super.execute();

        super.from();

        BluetoothGattCharacteristic result;
        Future<BluetoothGattCharacteristic> futureA0, futureA1, futureB;

        byte[] dataArray = new byte[5];
        dataArray[0] = (byte) mAction;
        dataArray[1] = (byte) mUnit;
        dataArray[2] = (byte) mLanguage;
        dataArray[3] = (byte) mSound;
        dataArray[4] = (byte) mBei;

        futureA0 = mExecutor.submit(new CsBleReceiveNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.CS_GENERAL_PURPOSE_REQUEST));
        futureA1 = mExecutor.submit(new CsBleSetNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.CS_GENERAL_PURPOSE_REQUEST, true));

        if (futureA1.get() == null) {
            Log.v(TAG,"[CS] futureA1.get == null, notification enable failed.");
            sendMessage(false, null);
            unregisterNotify(CsBleGattAttributes.CsV1CommandEnum.CS_GENERAL_PURPOSE_REQUEST);
            return;
        }

        futureB  = mExecutor.submit(new CsBleWriteCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.CS_GENERAL_PURPOSE_REQUEST, dataArray));
        result = futureB.get();

        if (result != null)
        {
            result = futureA0.get();
            if (result != null)
            {
                byte[] retArray = CsBleGattAttributeUtil.getCharValue(result);
                sendMessage(true, retArray);
            }
            else
            {
                sendMessage(false, null);
                unregisterNotify(CsBleGattAttributes.CsV1CommandEnum.CS_GENERAL_PURPOSE_REQUEST);
            }
        }
        else
        {
            sendMessage(false, null);
        }

        super.to(TAG);
    }



    private void sendMessage(boolean result, byte [] retArr) {

        try {

            Message outMsg = Message.obtain();

            outMsg.what = ICsConnectivityService.CB_GET_GENERAL_PURPOSE_RESULT;

            Bundle outData = new Bundle();

            if (result) {
                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_SUCCESS);
            } else {
                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_FAIL);
            }

            outData.putByteArray(ICsConnectivityService.PARAM_GENERAL_PURPOSE_EVENT_RESULT, retArr);

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
