package com.htc.blelib.v1.internal.callables;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.htc.blelib.internal.common.CommonBase.CsBleTransceiverErrorCode;
import com.htc.blelib.v1.internal.component.le.CsBleGattAttributes;
import com.htc.blelib.v1.internal.component.le.CsBleTransceiver;
import com.htc.blelib.v1.internal.component.le.CsBleTransceiverListener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;



public class CsBleReadCallable implements Callable<BluetoothGattCharacteristic> {

	private final static String TAG = "CsBleReadCallable";

	private final static int DEFAULT_CALLABLE_TIMEOUT = 20000;

	private final static int DEFAULT_RETRY_TIMES = 3;

	protected CsBleTransceiver mCsBleTransceiver;
	protected BluetoothDevice mBluetoothDevice;
	protected String mUuidServiceString;
	protected String mUuidString;

	private final LinkedBlockingQueue<CallbackObject> mCallbackQueue = new LinkedBlockingQueue<CallbackObject>();
	private int mRetryTimes = DEFAULT_RETRY_TIMES;



	private CsBleTransceiverListener mCsBleTransceiverListener = new CsBleTransceiverListener() {

		@Override
		public void onDisconnectedFromGattServer(BluetoothDevice device) {

			Log.d(TAG, "[CS] onDisconnectedFromGattServer device = " + device);

			if (device.equals(mBluetoothDevice)) {

				addCallback(new CallbackObject(device, null, CsBleTransceiverErrorCode.ERROR_DISCONNECTED_FROM_GATT_SERVER));
			}
		}



		@Override
		public void onCharacteristicRead(BluetoothDevice device, BluetoothGattCharacteristic characteristic) {

			Log.d(TAG, "[CS] onCharacteristicRead!!");

			if (device.equals(mBluetoothDevice) && characteristic.getUuid().toString().equals(mUuidString)) {

				addCallback(new CallbackObject(device, characteristic, CsBleTransceiverErrorCode.ERROR_NONE));
			}
		}



		@Override
		public void onError(BluetoothDevice device, BluetoothGattCharacteristic characteristic, CsBleTransceiverErrorCode errorCode) {

			Log.d(TAG, "[CS] onError. device = " + device + ", errorCode = " + errorCode);
			Log.d(TAG, "[CS] onError. characteristic.getUuid().toString() = " + characteristic.getUuid().toString() + ", mUuidString = " + mUuidString);

			if (device.equals(mBluetoothDevice) && characteristic.getUuid().toString().equals(mUuidString)) {

				addCallback(new CallbackObject(device, null, errorCode));
			}
		}
	};



	public CsBleReadCallable(CsBleTransceiver transceiver, BluetoothDevice device, String uuidString) {

		mCsBleTransceiver = transceiver;
		mBluetoothDevice = device;
		mUuidServiceString = null;
		mUuidString = uuidString;
	}



	public CsBleReadCallable(CsBleTransceiver transceiver, BluetoothDevice device, String uuidServiceString, String uuidString) {

		mCsBleTransceiver = transceiver;
		mBluetoothDevice = device;
		mUuidServiceString = uuidServiceString;
		mUuidString = uuidString;
	}



	@Override
	public BluetoothGattCharacteristic call() throws Exception {

		CallbackObject callbackObject = null;

		mCsBleTransceiver.registerListener(mCsBleTransceiverListener);

		mRetryTimes = DEFAULT_RETRY_TIMES;

		do {

			int retValue;
			if (mUuidServiceString == null) {

				retValue = mCsBleTransceiver.readCsCommand(mBluetoothDevice, CsBleGattAttributes.CS_SERVICE, mUuidString);

			} else {

				retValue = mCsBleTransceiver.readCsCommand(mBluetoothDevice, mUuidServiceString, mUuidString);
			}

			if (retValue < 0) {

				break;
			}

			callbackObject = mCallbackQueue.poll(DEFAULT_CALLABLE_TIMEOUT, TimeUnit.MILLISECONDS);

			if (callbackObject == null) {

				mRetryTimes = 0;

			} else {

				if (callbackObject.mErrorCode.equals(CsBleTransceiverErrorCode.ERROR_NONE) ||
				    callbackObject.mErrorCode.equals(CsBleTransceiverErrorCode.ERROR_DISCONNECTED_FROM_GATT_SERVER)) {

					mRetryTimes = 0;

				} else {

					mRetryTimes--;
				}

				Log.d(TAG, "[CS] errorCode = " + callbackObject.mErrorCode + ", mRetryTimes = " + mRetryTimes);
			}

		} while (mRetryTimes > 0);

		mCsBleTransceiver.unregisterListener(mCsBleTransceiverListener);

		if (callbackObject == null) {

			return null;

		} else {

			return callbackObject.mCharacteristic;
		}
	}



	protected synchronized void addCallback(CallbackObject callbackObject) {

		Log.d(TAG, "[CS] addCallback!!");

		mCallbackQueue.add(callbackObject);
	}



	private class CallbackObject {

		public final BluetoothDevice mDevice;
		public final BluetoothGattCharacteristic mCharacteristic;
		public final CsBleTransceiverErrorCode mErrorCode;

		public CallbackObject(BluetoothDevice device, BluetoothGattCharacteristic characteristic, CsBleTransceiverErrorCode errorCode) {

			mDevice = device;
			mCharacteristic = characteristic;
			mErrorCode = errorCode;
		}
	}
}
