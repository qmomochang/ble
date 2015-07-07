package com.htc.blelib.v1.internal.callables;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.htc.blelib.internal.common.CommonBase.CsBleTransceiverErrorCode;
import com.htc.blelib.v1.internal.common.LongCommandCollector;
import com.htc.blelib.v1.internal.component.le.CsBleGattAttributeUtil;
import com.htc.blelib.v1.internal.component.le.CsBleGattAttributes;
import com.htc.blelib.v1.internal.component.le.CsBleTransceiver;
import com.htc.blelib.v1.internal.component.le.CsBleTransceiverListener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;



public class CsBleReceiveNotificationCallable implements Callable<BluetoothGattCharacteristic> {

	private final static String TAG = "CsBleReceiveNotificationCallable";

	private final static int DEFAULT_CALLABLE_TIMEOUT = 60000;

	protected CsBleTransceiver mCsBleTransceiver;
	protected BluetoothDevice mBluetoothDevice;
	protected CsBleGattAttributes.CsV1CommandEnum mCommandID;
	protected boolean m_isLongFormat;
	protected LongCommandCollector mCollector;
	protected int mCallableTimeout;
	private final LinkedBlockingQueue<CallbackObject> mCallbackQueue = new LinkedBlockingQueue<CallbackObject>();



	private CsBleTransceiverListener mCsBleTransceiverListener = new CsBleTransceiverListener() {

		@Override
		public void onDisconnectedFromGattServer(BluetoothDevice device) {

			Log.d(TAG, "[CS] onDisconnectedFromGattServer device = " + device);

			if (device.equals(mBluetoothDevice)) {

				addCallback(new CallbackObject(device, null, CsBleTransceiverErrorCode.ERROR_DISCONNECTED_FROM_GATT_SERVER));
			}
		}



		@Override
		public void onNotificationReceive(BluetoothDevice device, BluetoothGattCharacteristic characteristic) {

			Log.d(TAG, "[CS] onNotificationReceive!!");

			if (device.equals(mBluetoothDevice) && characteristic.getValue()[0] == mCommandID.getID()) {
				addCallback(new CallbackObject(device, characteristic, CsBleTransceiverErrorCode.ERROR_NONE));
			}
		}



		@Override
		public void onError(BluetoothDevice device, BluetoothGattCharacteristic characteristic, CsBleTransceiverErrorCode errorCode) {

			Log.d(TAG, "[CS] onError. device = " + device + ", errorCode = " + errorCode);
			Log.d(TAG, "[CS] onError. characteristic.getUuid().toString() = " + characteristic.getUuid().toString() + ", Command id:" + mCommandID);

			//Todo: Need to confirm Error code format
			//if (device.equals(mBluetoothDevice) && characteristic.getUuid().toString().equals(mUuidString)) {

				///addCallback(new CallbackObject(device, null, errorCode));
			//}
		}
	};



	public CsBleReceiveNotificationCallable(CsBleTransceiver transceiver, BluetoothDevice device, CsBleGattAttributes.CsV1CommandEnum commandID) {

		this(transceiver, device, commandID, DEFAULT_CALLABLE_TIMEOUT);
	}



	public CsBleReceiveNotificationCallable(CsBleTransceiver transceiver, BluetoothDevice device, CsBleGattAttributes.CsV1CommandEnum commandID, int timeout) {

		mCsBleTransceiver = transceiver;
		mBluetoothDevice = device;
		mCommandID = commandID;
		m_isLongFormat = CsBleGattAttributes.isLongFormat(commandID);

		if (m_isLongFormat) {

			mCollector = new LongCommandCollector(mBluetoothDevice, commandID);
		}

		if (timeout > 0) {

			mCallableTimeout = timeout;

		} else {

			mCallableTimeout = DEFAULT_CALLABLE_TIMEOUT;
		}

		/// Register listener in constructor in order to avoid notification missing problem.
		mCsBleTransceiver.registerListener(mCsBleTransceiverListener);
	}



	@Override
	public BluetoothGattCharacteristic call() throws Exception {

		CallbackObject callbackObject = null;
		BluetoothGattCharacteristic ret = null;
		boolean isComplete = false;

		do {

			callbackObject = mCallbackQueue.poll(mCallableTimeout, TimeUnit.MILLISECONDS);
			if (callbackObject != null) {

				if (m_isLongFormat) {

					isComplete = mCollector.update(mBluetoothDevice, callbackObject.mCharacteristic);

				} else {

					ret = callbackObject.mCharacteristic;
				}

			} else {

				Log.d(TAG, "[CS] Failed to poll callbackObject!!");

				if (mCollector != null) {

					mCollector.reset();
				}

				break;
			}

		} while (!isComplete && m_isLongFormat);

		mCsBleTransceiver.unregisterListener(mCsBleTransceiverListener);

		if (m_isLongFormat)
			return mCollector.getCharacteristic();
		else
			return ret;
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
