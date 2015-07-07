package com.htc.blelib.v1.internal.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.internal.callables.CsBleReadCallable;
import com.htc.blelib.v1.internal.callables.CsBleReceiveNotificationCallable;
import com.htc.blelib.v1.internal.callables.CsBleSetNotificationCallable;
import com.htc.blelib.v1.internal.callables.CsBleWriteCallable;
import com.htc.blelib.v1.internal.callables.CsBootUpCallable;
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



public class CsNameTask extends CsConnectivityTask {

	private final static String TAG = "CsNameTask";

	public final static int ACTION_SET_NAME = 1;
	public final static int ACTION_GET_NAME = 0;
	public final static int MAX_DATA_LENGTH = 15;
	public final static int DATA_OFFSET_INDEX = 1;
	public final static int DATA_ADD_END = 1;

	private BluetoothDevice mBluetoothDevice;
	private int mAction;
	private String mName;



	public CsNameTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device, int action, String name) {

		super(csBleTransceiver, messenger, executor);

		mBluetoothDevice = device;
		mAction = action;

		if (name != null) {

			mName = name;

		} else {

			mName = "hTC CS";
		}
	}

	private void setName(String name)
	{
		mName = name;
	}

	@Override
	public void execute() throws Exception {

		super.execute();

		super.from();

		Future<Integer> futureBoot;
		Integer bootResult;
		futureBoot = mExecutor.submit(new CsBootUpCallable(mCsBleTransceiver, mExecutor, mBluetoothDevice, mMessenger));
		bootResult = futureBoot.get();

		if (bootResult == Common.ERROR_SUCCESS)
		{
			BluetoothGattCharacteristic result;
			Future<BluetoothGattCharacteristic> futureA0, futureA1, futureB;

			if (mAction == ACTION_SET_NAME) {

				char[] temp = mName.toCharArray();

				if (temp.length > MAX_DATA_LENGTH) {
					sendMessage(false, null);
					return;
				}

				byte[] nameArray = new byte[temp.length + DATA_OFFSET_INDEX + DATA_ADD_END];
				nameArray[0] = (byte)ACTION_SET_NAME;
				nameArray[temp.length + DATA_OFFSET_INDEX] = 0x00;
				for (int cnt = 0; cnt < temp.length; cnt++) {
					nameArray[cnt + DATA_OFFSET_INDEX] = (byte) temp[cnt];
				}
				Log.d(TAG, "[CS] setName:" + mName + ", length:" + mName.length());

				futureA0 = mExecutor.submit(new CsBleWriteCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.CS_BLE_NAME_REQUEST, nameArray));

				result = futureA0.get();

				if (result != null) {

					sendMessage(true, null);

				} else {

					sendMessage(false, null);
				}

			} else if (mAction == ACTION_GET_NAME) {

				byte[] nameArray = new byte[DATA_OFFSET_INDEX];
				nameArray[0] = (byte)ACTION_GET_NAME;
				//Here is no data!!
				futureA0 = mExecutor.submit(new CsBleReceiveNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.CS_BLE_NAME_REQUEST));
				futureA1 = mExecutor.submit(new CsBleSetNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.CS_BLE_NAME_REQUEST, true));

				if (futureA1.get() == null) {

					sendMessage(false, null);
					unregisterNotify(CsBleGattAttributes.CsV1CommandEnum.CS_BLE_NAME_REQUEST);
					return;
				}

				futureB = mExecutor.submit(new CsBleWriteCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.CS_BLE_NAME_REQUEST, nameArray));

				result = futureB.get();

				if (result != null) {

					result = futureA0.get();
					if (result != null)
					{
						setName(CsBleGattAttributeUtil.getCsName(result));
						sendMessage(true, mName);
					}
					else
					{
						sendMessage(false, null);
						unregisterNotify(CsBleGattAttributes.CsV1CommandEnum.CS_BLE_NAME_REQUEST);
					}

				} else {

					sendMessage(false, null);
				}
			}
		}
		else
		{
			Log.d(TAG, "[CS] boot up is fail");
			sendMessage(false, null);
		}

		super.to(TAG);
	}



	private void sendMessage(boolean result, String name) {

		try {

			Message outMsg = Message.obtain();

			if (mAction == ACTION_SET_NAME) {

				outMsg.what = ICsConnectivityService.CB_SET_NAME_RESULT;

			} else if (mAction == ACTION_GET_NAME) {

				outMsg.what = ICsConnectivityService.CB_GET_NAME_RESULT;
			}

			Bundle outData = new Bundle();

			if (result) {

				outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_SUCCESS);

			} else {

				outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_FAIL);
			}

			if (name != null) {

				outData.putString(ICsConnectivityService.PARAM_CS_NAME, name);
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

	private void unregisterNotify(CsBleGattAttributes.CsV1CommandEnum commandID) throws Exception {

		Future<BluetoothGattCharacteristic> future;

		future = mExecutor.submit(new CsBleSetNotificationCallable(mCsBleTransceiver, mBluetoothDevice, commandID, false));
		if (future.get() == null) {

			Log.d(TAG, "[CS] unregisterNotify error!!!");
			return;
		}
	}

}
