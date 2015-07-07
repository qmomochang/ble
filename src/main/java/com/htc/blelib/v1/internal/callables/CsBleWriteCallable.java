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



public class CsBleWriteCallable implements Callable<BluetoothGattCharacteristic> {

	private final static String TAG = "CsBleWriteCallable";

	private final static int DEFAULT_CALLABLE_TIMEOUT = 20000;

	private final static int DEFAULT_RETRY_TIMES = 10;

	protected CsBleTransceiver mCsBleTransceiver;
	protected BluetoothDevice mBluetoothDevice;
	//protected String mUuidString;
	protected CsBleGattAttributes.CsV1CommandEnum mCommandID;
	protected byte[] mWriteData;

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
		public void onCharacteristicWrite(BluetoothDevice device, BluetoothGattCharacteristic characteristic) {

			Log.d(TAG, "[CS] onCharacteristicWrite!!");

			if (device.equals(mBluetoothDevice) /*&& characteristic.getValue()[0] == mCommandID.getID()*/) {

				addCallback(new CallbackObject(device, characteristic, CsBleTransceiverErrorCode.ERROR_NONE));
			}
		}



		@Override
		public void onError(BluetoothDevice device, BluetoothGattCharacteristic characteristic, CsBleTransceiverErrorCode errorCode) {

			Log.d(TAG, "[CS] onError. device = " + device + ", errorCode = " + errorCode);
			Log.d(TAG, "[CS] onError. characteristic.getUuid().toString() = " + characteristic.getUuid().toString() + " Command ID:" + mWriteData[0]);

			if (device.equals(mBluetoothDevice) &&  characteristic.getValue()[0] == mCommandID.getID()) {

				addCallback(new CallbackObject(device, null, errorCode));
			}
		}
	};



	public CsBleWriteCallable(CsBleTransceiver transceiver, BluetoothDevice device, CsBleGattAttributes.CsV1CommandEnum commandID, byte[] writeData) {

		mCsBleTransceiver = transceiver;
		mBluetoothDevice = device;
		mCommandID = commandID;
		mWriteData = writeData;
        Log.v(TAG,"[CS] CsBleWriteCallable mCommandID = "+mCommandID+", mWriteData = "+String.format("0x%20x",writeData[0])+","+String.format("0x%20x",writeData[1]));
	}



	@Override
	public BluetoothGattCharacteristic call() throws Exception {

		CallbackObject callbackObject = null;

		mCsBleTransceiver.registerListener(mCsBleTransceiverListener);

		mRetryTimes = DEFAULT_RETRY_TIMES;

		do {

			int writeSize = mCsBleTransceiver.writeCsCommand(mBluetoothDevice, mCommandID, mWriteData, 0);
			if (writeSize < 0) {

				mCsBleTransceiver.unregisterListener(mCsBleTransceiverListener);
				return null;
			}

			boolean writeError = false;

			while (writeSize > 0) {

				callbackObject = mCallbackQueue.poll(DEFAULT_CALLABLE_TIMEOUT, TimeUnit.MILLISECONDS);

				if (callbackObject == null) {

					writeSize = 0;

				} else {

					if (callbackObject.mErrorCode.equals(CsBleTransceiverErrorCode.ERROR_DISCONNECTED_FROM_GATT_SERVER)) {

						writeSize = 0;

					} else if (callbackObject.mErrorCode.equals(CsBleTransceiverErrorCode.ERROR_NONE)) {

						writeSize--;

					} else {

						writeError = true;
						writeSize--;
					}
				}
			}

			if (callbackObject == null) {

				mRetryTimes = 0;

			} else {

				if (!writeError || callbackObject.mErrorCode.equals(CsBleTransceiverErrorCode.ERROR_DISCONNECTED_FROM_GATT_SERVER)) {

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
