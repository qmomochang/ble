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



public class CsWifiConfigRequestTask extends CsConnectivityTask {

	private final static String TAG = "CsWifiConfigRequestTask";

	public final static int ACTION_CONNECT = 0;
	public final static int ACTION_DISCONNECT = 1;

	public final static int SECURITY_OPEN       = 0;
	public final static int SECURITY_WEP        = 1;
	public final static int SECURITY_WEP_PSK    = 2;
	public final static int SECURITY_WPA2_PSK   = 3;

	private BluetoothDevice mBluetoothDevice;
	private int mAction;
	private int mSecurity;

	public CsWifiConfigRequestTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device, int action, int security)
    {
        super(csBleTransceiver, messenger, executor);

		mBluetoothDevice = device;
        // 0x00: connect
        // 0x01: disconnect
		mAction = action;

        // 0x00: open
        // 0x01: WEP
        // 0x02: WPA PSK
        // 0x03: WPA2 PSK
        mSecurity = security;
	}

	@Override
	public void execute() throws Exception {

		super.execute();
		super.from();

        BluetoothGattCharacteristic result;
        Future<BluetoothGattCharacteristic> futureA0, futureA1, futureB;

        byte[] array = new byte[2];
        array[0] = (byte)mAction;
        array[1] = (byte)mSecurity;

        futureA0 = mExecutor.submit(new CsBleReceiveNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.WIFI_CONFIG_REQUEST));
        futureA1 = mExecutor.submit(new CsBleSetNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.WIFI_CONFIG_REQUEST, true));

        if (futureA1.get() == null) {
            sendMessage(false, null);
            unregisterNotify(CsBleGattAttributes.CsV1CommandEnum.WIFI_CONFIG_REQUEST);
            return;
        }

        futureB = mExecutor.submit(new CsBleWriteCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.WIFI_CONFIG_REQUEST, array));

        result = futureB.get();

        if (result != null) {

            result = futureA0.get();
            if (result != null)
            {
                byte [] retArr;
                retArr = CsBleGattAttributeUtil.getWifiConfigEvent(result);
                sendMessage(true, retArr);
            }
            else
            {
                sendMessage(false, null);
                unregisterNotify(CsBleGattAttributes.CsV1CommandEnum.WIFI_CONFIG_REQUEST);
            }

        } else {

            sendMessage(false, null);
        }

        super.to(TAG);
    }



    private void sendMessage(boolean result, byte [] retArr) {

        try {

            Message outMsg = Message.obtain();

            outMsg.what = ICsConnectivityService.CB_WIFI_CONFIG_RESULT;

            Bundle outData = new Bundle();

            if (result) {
				outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_SUCCESS);
			} else {
				outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_FAIL);
			}

            outData.putByteArray(ICsConnectivityService.PARAM_CS_NAME, retArr);

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
