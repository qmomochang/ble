package com.htc.blelib.v1.internal.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.htc.blelib.interfaces.ICsConnectivityServiceBase.PlugIO;
import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.interfaces.ICsConnectivityService.HWStatusEvent.MCUBatteryLevel;
import com.htc.blelib.v1.internal.callables.CsBleReceiveNotificationCallable;
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



public class CsHwStatusTask extends CsConnectivityTask {

	private final static String TAG = "CsHwStatusTask";

	public final static int ACTION_SET_HW_STATUS_LTEVENT = 0;
	public final static int ACTION_CLR_HW_STATUS_LTEVENT = 1;
	public final static int ACTION_GET_HW_STATUS = 2;

	private BluetoothDevice mBluetoothDevice;
	private int mAction;



	public CsHwStatusTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device, int action) {

		super(csBleTransceiver, messenger, executor);

		mBluetoothDevice = device;
		mAction = action;
	}



	@Override
	public void execute() throws Exception {

		super.execute();

		super.from();

		BluetoothGattCharacteristic result;
		Future<BluetoothGattCharacteristic> future, futureA;

		if (mAction == ACTION_GET_HW_STATUS) {
			Future<Integer> futureBoot;
			Integer bootResult;
			futureBoot = mExecutor.submit(new CsBootUpCallable(mCsBleTransceiver, mExecutor, mBluetoothDevice, mMessenger));
			bootResult = futureBoot.get();
			if (bootResult == Common.ERROR_SUCCESS)
			{

				byte[] getType = {(byte)0x00}; // 0x00 battery level, 0x01 reserved
				futureA = mExecutor.submit(new CsBleReceiveNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.HWSTATUS_EVENT));
				future  = mExecutor.submit(new CsBleWriteCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.HWSTATUS_EVENT, getType));
				result = future.get();
				if (result != null)
				{
					result = futureA.get();
					if (result != null)
					{
						int battery_cap = CsBleGattAttributeUtil.getHwStatus_BatteryLevel(result);

						sendMessage(true, battery_cap);
					}
					else
					{
						sendMessage(false, -1);
					}
				}
				else
				{
					sendMessage(false, -1);
				}
			}
			else
			{
				Log.d(TAG, "[CS] boot up is fail");
				sendMessage(false, -1);
			}
		} else if (mAction == ACTION_SET_HW_STATUS_LTEVENT) {

			sendMessage(true, -1);

		} else if (mAction == ACTION_CLR_HW_STATUS_LTEVENT) {

			sendMessage(true, -1);
		}

		super.to(TAG);
	}



	private void sendMessage(boolean result, int level) {

		try {

			Message outMsg = Message.obtain();

			if (mAction == ACTION_GET_HW_STATUS) {

				outMsg.what = ICsConnectivityService.CB_GET_HW_STATUS_RESULT;

			} else if (mAction == ACTION_SET_HW_STATUS_LTEVENT) {

				outMsg.what = ICsConnectivityService.CB_SET_HW_STATUS_LTEVENT_RESULT;

			} else if (mAction == ACTION_CLR_HW_STATUS_LTEVENT) {

				outMsg.what = ICsConnectivityService.CB_CLR_HW_STATUS_LTEVENT_RESULT;
			}

			Bundle outData = new Bundle();

			if (result) {

				outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_SUCCESS);

			} else {

				outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_FAIL);
			}

			if (level >= 1) {

				outData.putSerializable(ICsConnectivityService.PARAM_BATTERY_LEVEL, MCUBatteryLevel.findLevel(level));
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
