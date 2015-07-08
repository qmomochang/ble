package com.htc.blelib.v1.internal.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.nio.charset.Charset;

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



public class CsSetWifiApSsidTask extends CsConnectivityTask {

    private final static String TAG = "CsSetWifiApSsidTask";

    public final static int MAX_DATA_LENGTH = 32;

    private BluetoothDevice mBluetoothDevice;
    private String mSsid;

    public CsSetWifiApSsidTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device, String ssid) {

        super(csBleTransceiver, messenger, executor);

        mBluetoothDevice = device;
        mSsid = ssid;
    }

    @Override
    public void execute() throws Exception {

        super.execute();

        super.from();

        BluetoothGattCharacteristic result;
        Future<BluetoothGattCharacteristic> futureA0, futureA1, futureB;

        char[] temp = mSsid.toCharArray();

        if (temp.length > MAX_DATA_LENGTH) {
            sendMessage(false);
            return;
        }

        byte[] ssidArray = new byte[32];
        ssidArray = mSsid.getBytes(Charset.forName("UTF-8"));

        futureA0 = mExecutor.submit(new CsBleWriteCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.WIFI_SET_SSID_REQUEST, ssidArray));

        result = futureA0.get();

        if (result != null) {
            sendMessage(true);
        } else {
            sendMessage(false);
        }

        super.to(TAG);
    }

    private void sendMessage(boolean result) {

        try {

            Message outMsg = Message.obtain();

            outMsg.what = ICsConnectivityService.CB_WIFI_SET_SSID;

            Bundle outData = new Bundle();

            if (result) {
                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_SUCCESS);
            } else {
                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_FAIL);
            }

            outMsg.setData(outData);

            mMessenger.send(outMsg);

        } catch (RemoteException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void error(Exception e) {

        sendMessage(false);
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
