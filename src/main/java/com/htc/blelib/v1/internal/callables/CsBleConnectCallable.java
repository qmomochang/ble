package com.htc.blelib.v1.internal.callables;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.htc.blelib.internal.common.CommonBase.CsBleTransceiverErrorCode;
import com.htc.blelib.v1.internal.component.le.CsBleTransceiver;
import com.htc.blelib.v1.internal.component.le.CsBleTransceiverListener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;



public class CsBleConnectCallable implements Callable<Integer> {

	private final static String TAG = "CsBleConnectCallable";

	private final static int DEFAULT_CALLABLE_TIMEOUT = 60000;

	protected CsBleTransceiver mCsBleTransceiver;
	protected BluetoothDevice mBluetoothDevice;

	private final LinkedBlockingQueue<CsBleTransceiverErrorCode> mCallbackQueue = new LinkedBlockingQueue<CsBleTransceiverErrorCode>();
	private Integer mStatus;

	private CsBleTransceiverListener mCsBleTransceiverListener = new CsBleTransceiverListener() {

		@Override
		public void onConnected(BluetoothDevice device) {

			Log.d(TAG, "[CS] onConnected. device = " + device);

			if (device.equals(mBluetoothDevice)) {

				addCallback(CsBleTransceiverErrorCode.ERROR_NONE);
			}
		}

		@Override
		public void onDisconnectedFromGattServer(BluetoothDevice device) {

			Log.d(TAG, "[CS] onDisconnectedFromGattServer device = " + device);

			if (device.equals(mBluetoothDevice)) {

				addCallback(CsBleTransceiverErrorCode.ERROR_DISCONNECTED_FROM_GATT_SERVER);
			}
		}

		@Override
		public void onError(BluetoothDevice device, BluetoothGattCharacteristic characteristic, CsBleTransceiverErrorCode errorCode) {

			Log.d(TAG, "[CS] onError. device = " + device + ", errorCode = " + errorCode);

			if (device.equals(mBluetoothDevice)) {

				addCallback(errorCode);
			}
		}
	};

	public CsBleConnectCallable(CsBleTransceiver transceiver, BluetoothDevice device) {

		mCsBleTransceiver = transceiver;
		mBluetoothDevice = device;
	}

	@Override
	public Integer call() throws Exception {

		Integer ret = 0;

		mCsBleTransceiver.registerListener(mCsBleTransceiverListener);

		mStatus = 0;

		if (mCsBleTransceiver.connect(mBluetoothDevice, false)) {

			CsBleTransceiverErrorCode errorCode = mCallbackQueue.poll(DEFAULT_CALLABLE_TIMEOUT, TimeUnit.MILLISECONDS);

			if (errorCode == null) {

				mCsBleTransceiver.disconnectForce(mBluetoothDevice);

				mStatus = -2;

			} else if (errorCode != CsBleTransceiverErrorCode.ERROR_NONE) {

				mStatus = -1;
			}

		} else {

			mStatus = -3;
		}

		mCsBleTransceiver.unregisterListener(mCsBleTransceiverListener);

		ret = mStatus;

		return ret;
	}

	protected synchronized void addCallback(CsBleTransceiverErrorCode errorCode) {

		Log.d(TAG, "[CS] addCallback errorCode = " + errorCode);

		if (errorCode != null) {

			mCallbackQueue.add(errorCode);
		}
	}
}
