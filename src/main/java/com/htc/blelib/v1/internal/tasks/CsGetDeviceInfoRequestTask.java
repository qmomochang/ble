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



public class CsGetDeviceInfoRequestTask extends CsConnectivityTask {

	private final static String TAG = "CsGetDeviceInfoRequestTask";

	private BluetoothDevice mBluetoothDevice;
	private int mAction;
	private String mName;

	public CsGetDeviceInfoRequestTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device, int action, String name) {

		super(csBleTransceiver, messenger, executor);

		mBluetoothDevice = device;
		mAction = action;
	}

	@Override
	public void execute() throws Exception {

		super.execute();

		super.from();

        BluetoothGattCharacteristic result;
        Future<BluetoothGattCharacteristic> futureA0, futureA1, futureB;

        byte[] inArray = new byte[1];
        inArray[0] = (byte) 0x00;

        futureA0 = mExecutor.submit(new CsBleReceiveNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.DEVICEINFO_REQUEST_EVENT));
        futureA1 = mExecutor.submit(new CsBleSetNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.DEVICEINFO_REQUEST_EVENT, true));

        if (futureA1.get() == null) {
            sendMessage(false, null);
            unregisterNotify(CsBleGattAttributes.CsV1CommandEnum.DEVICEINFO_REQUEST_EVENT);
            return;
        }

        futureB = mExecutor.submit(new CsBleWriteCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.DEVICEINFO_REQUEST_EVENT, inArray));

        result = futureB.get();
        if (result != null) {
            result = futureA0.get();
            if (result != null)
            {
                byte [] retArr = CsBleGattAttributeUtil.getDeviceInfo(result);
                sendMessage(true, retArr);
            }
            else
            {
                sendMessage(false, null);
                unregisterNotify(CsBleGattAttributes.CsV1CommandEnum.DEVICEINFO_REQUEST_EVENT);
            }

        } else {

            sendMessage(false, null);
        }

        super.to(TAG);
    }

    private void sendMessage(boolean result, byte [] retArr) {

        try {

			Message outMsg = Message.obtain();

            outMsg.what = ICsConnectivityService.CB_GET_DEVICE_INFO_RESULT;

			Bundle outData = new Bundle();

			if (result) {
				outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_SUCCESS);
			} else {
				outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_FAIL);
			}

            outData.putString(ICsConnectivityService.PARAM_CS_NAME, new String(retArr));

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
