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



public class CsEraseWifiApConfigRequestTask extends CsConnectivityTask {

	private final static String TAG = "CsEraseWifiApConfigRequestTask";
    private final static int DATA_OFFSET_INDEX = 2;
    private final static int DATA_ADD_END = 1;
    private final static int MAX_DATA_LENGTH = 33;

	private BluetoothDevice mBluetoothDevice;
	private int mSecurity;
	private int mSsidLength;
	private String mSsid;

	public CsEraseWifiApConfigRequestTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device, int security, String ssid) {

		super(csBleTransceiver, messenger, executor);

		mBluetoothDevice = device;
		mSecurity = security;
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
            sendMessage(false, null);
            return;
        }

        byte[] inArray = new byte[DATA_OFFSET_INDEX + mSsid.length() + DATA_ADD_END];
        inArray[0] = (byte) mSecurity;
        inArray[1] = (byte) mSsid.length();
        for (int cnt = 0; cnt < temp.length; cnt++) {
            inArray[cnt + DATA_OFFSET_INDEX] = (byte) temp[cnt];
        }
        Log.d(TAG, "[CS] eraseWifiApSSid:" + mSsid + ", length:" + mSsid.length());

        futureA0 = mExecutor.submit(new CsBleWriteCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.WIFI_ERASE_AP_CONFIG_REQUEST, inArray));

        result = futureA0.get();

        if (result != null) {
            sendMessage(true, null);
        } else {
            sendMessage(false, null);
        }

        super.to(TAG);
    }

    private void sendMessage(boolean result, String name) {

        try {

			Message outMsg = Message.obtain();

            outMsg.what = ICsConnectivityService.CB_ERASE_WIFI_AP_CONFIG_RESULT;

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

		sendMessage(false, null);
	}

}
