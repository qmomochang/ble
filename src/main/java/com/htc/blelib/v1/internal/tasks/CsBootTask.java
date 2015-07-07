package com.htc.blelib.v1.internal.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.htc.blelib.interfaces.ICsConnectivityServiceBase.SwitchOnOff;
import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.internal.common.Common;
import com.htc.blelib.v1.internal.common.CsConnectivityTask;
import com.htc.blelib.v1.internal.component.le.CsBleTransceiver;
import com.htc.blelib.v1.internal.callables.CsBootUpCallable;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;



public class CsBootTask extends CsConnectivityTask {

	private final static String TAG = "CsBootTask";
	private BluetoothDevice mBluetoothDevice;


	public CsBootTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device) {

		super(csBleTransceiver, messenger, executor);
		mBluetoothDevice = device;
	}



	@Override
	public void execute() throws Exception {

		super.execute();

		Integer resultBoot;
		Future<Integer> futureBoot;

		futureBoot = mExecutor.submit(new CsBootUpCallable(mCsBleTransceiver, mExecutor, mBluetoothDevice, mMessenger));
		resultBoot = futureBoot.get();

		if (resultBoot == Common.ERROR_SUCCESS) {
			sendMessage(SwitchOnOff.SWITCH_ON);
		}
	}

	private void sendMessage(SwitchOnOff bootupReady) {

		try {
			Message outMsg = Message.obtain();
			outMsg.what = ICsConnectivityService.CB_SET_POWER_ONOFF_RESULT;

			Bundle outData = new Bundle();
			outData.putSerializable(ICsConnectivityService.PARAM_CS_POWER, (SwitchOnOff) bootupReady);
			outMsg.setData(outData);
			mMessenger.send(outMsg);

		} catch (RemoteException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void error(Exception e) {

	}
}
