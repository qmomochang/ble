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



public class CsSetClientCredentialsRequestTask extends CsConnectivityTask {

    private final static String TAG = "CsSetClientCredentialsRequestTask";

    private BluetoothDevice mBluetoothDevice;
    private int mAction;
    private String mClientID;
    private String mClientSecret;
    private byte [] mClientAccessToken;
    private byte [] mClientRefreshToken;

    public CsSetClientCredentialsRequestTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device, int action, String clientID, String clientSecret, byte [] clientAccessToken, byte [] clientRefreshToken) {

        super(csBleTransceiver, messenger, executor);

        mBluetoothDevice = device;

        mAction = action;
        mClientID = clientID;
        mClientSecret = clientSecret;
        mClientAccessToken = clientAccessToken;
        mClientRefreshToken = clientRefreshToken;
    }

    @Override
    public void execute() throws Exception {

        super.execute();

        super.from();

        BluetoothGattCharacteristic result;
        Future<BluetoothGattCharacteristic> futureA0, futureA1, futureB;


        byte[] byteClientID       = mClientID.getBytes("ISO-8859-1");
        byte[] byteClientSecret   = mClientSecret.getBytes("ISO-8859-1");

        byte[] inArray = new byte[96];

        int i;
        inArray[0] = (byte) mAction;
        for(i=1;i<=15;i++)
            inArray[i] = byteClientID[i-1];
        for(i=16;i<=31;i++)
            inArray[i] = byteClientSecret[i-16];
        for(i=32;i<=63;i++)
            inArray[i] = mClientAccessToken[i-32];
        for(i=64;i<=95;i++)
            inArray[i] = mClientRefreshToken[i-64];

        futureA0 = mExecutor.submit(new CsBleReceiveNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.CLIENT_CREDENTIALS_EVENT));
        futureA1 = mExecutor.submit(new CsBleSetNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.CLIENT_CREDENTIALS_EVENT, true));

        if (futureA1.get() == null) {

            sendMessage(false, null);
            unregisterNotify(CsBleGattAttributes.CsV1CommandEnum.CLIENT_CREDENTIALS_EVENT);
            return;
        }

        futureB = mExecutor.submit(new CsBleWriteCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.CLIENT_CREDENTIALS_REQUEST, inArray));

        result = futureB.get();

        if (result != null) {
            result = futureA0.get();
            if (result != null)
            {
                byte [] retArray = CsBleGattAttributeUtil.getCharValue(result);
                sendMessage(true, retArray);
            }
            else
            {
                sendMessage(false, null);
                unregisterNotify(CsBleGattAttributes.CsV1CommandEnum.CLIENT_CREDENTIALS_EVENT);
            }

        } else {

            sendMessage(false, null);
        }
        super.to(TAG);
    }

    private void sendMessage(boolean result, byte [] retArray) {

        try {

            Message outMsg = Message.obtain();

            outMsg.what = ICsConnectivityService.CB_SET_NAME_RESULT;

            Bundle outData = new Bundle();

            if (result) {
                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_SUCCESS);
            } else {
                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_FAIL);
            }

            outData.putByteArray(ICsConnectivityService.PARAM_CLIENT_CREDENTIALS_EVENT_RESULT, retArray);

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
