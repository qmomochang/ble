package com.htc.blelib.v1.internal.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.internal.callables.CsBleReadCallable;
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



public class CsBleReadBatteryTask extends CsConnectivityTask {

	private final static String TAG = "CsBleReadBatteryTask";

	public final static int ACTION_SET_NAME = 1;
	public final static int ACTION_GET_NAME = 0;
	public final static int MAX_DATA_LENGTH = 15;
	public final static int DATA_OFFSET_INDEX = 1;
	public final static int DATA_ADD_END = 1;

	private BluetoothDevice mBluetoothDevice;
	private int mAction;

	public CsBleReadBatteryTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device) {

		super(csBleTransceiver, messenger, executor);

		mBluetoothDevice = device;
	}

	@Override
	public void execute() throws Exception {

		super.execute();

		super.from();

		Future<Integer> futureBoot;
		Integer bootResult;
        bootResult = Common.ERROR_SUCCESS;
		//futureBoot = mExecutor.submit(new CsBootUpCallable(mCsBleTransceiver, mExecutor, mBluetoothDevice, mMessenger));
		//bootResult = futureBoot.get();

		if (bootResult == Common.ERROR_SUCCESS)
		{
            Log.v(TAG,"[CS] CsBleReadBatteryTask");
			BluetoothGattCharacteristic result;
			Future<BluetoothGattCharacteristic> future;

            future = mExecutor.submit(new CsBleReadCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CS_BATTERY_SERVICE, CsBleGattAttributes.CS_V1_BATTERY_LEVEL));

            result = future.get();

            if (result != null) {
                sendMessage(true, CsBleGattAttributeUtil.getBatteryLevel(result));
            } else {

                sendMessage(false, (byte)-1);
            }
		}
		else
		{
			Log.d(TAG, "[CS] boot up is fail");
			sendMessage(false, (byte)-1);
		}

		super.to(TAG);
	}



	private void sendMessage(boolean result, byte batteryLevel) {

		try {

			Message outMsg = Message.obtain();

            outMsg.what = ICsConnectivityService.CB_GET_POWER_LEVEL_RESULT;

			Bundle outData = new Bundle();

			if (result) {
				outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_SUCCESS);
			} else {
				outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_FAIL);
			}

            outData.putByte(ICsConnectivityService.PARAM_BATTERY_LEVEL, batteryLevel);

			outMsg.setData(outData);
			mMessenger.send(outMsg);

		} catch (RemoteException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void error(Exception e) {

		sendMessage(false, (byte)-1);
	}

}
