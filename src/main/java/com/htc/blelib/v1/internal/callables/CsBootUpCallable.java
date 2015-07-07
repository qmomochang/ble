package com.htc.blelib.v1.internal.callables;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.internal.common.Common;
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



public class CsBootUpCallable implements Callable<Integer> {

	private final static String TAG = "CsBootUpCallable";
	private final static boolean bPerformanceNotify = true;

	private final static int DEFAULT_RETRY_TIMES = 5;

	protected ExecutorService mExecutor;
	protected CsBleTransceiver mCsBleTransceiver;
	protected BluetoothDevice mBluetoothDevice;
	protected long mTimePrev;

	private int mRetryTimes = DEFAULT_RETRY_TIMES;
	protected Messenger mMessenger;


	public CsBootUpCallable(CsBleTransceiver csBleTransceiver, ExecutorService executor, BluetoothDevice device, Messenger messenger) {

		mCsBleTransceiver = csBleTransceiver;
		mExecutor = executor;
		mBluetoothDevice = device;
		mMessenger = messenger;
	}



	@Override
	public Integer call() throws Exception {

		from();

/*		Integer ret = Common.ERROR_BOOT_UP_CS;

		if (mCsBleTransceiver != null) {

			mRetryTimes = DEFAULT_RETRY_TIMES;

			int versionBle = -1;

			/// Version protect, can remove later.
			CsConnectivityDevice csDevice = mCsBleTransceiver.getCsConnectivityDeviceGroup().getDevice(mBluetoothDevice);
			if (csDevice != null) {

				versionBle = csDevice.getVersionBle();
			}

			do {

				if ((csDevice != null) && (csDevice.getCsStateBle() != CsStateBle.CSSTATE_BLE_CONNECTED)) {

					Log.d(TAG, "[CS] CS bootUp fail because BLE is disconnected");
					return Common.ERROR_BOOT_UP_CS;
				}

				ret = bootUp();

				if (ret == Common.ERROR_SUCCESS) {

					mRetryTimes = 0;

				} else {

					mRetryTimes--;

					Log.d(TAG, "[CS] CS bootUp mRetryTimes = " + mRetryTimes);
				}

			} while (mRetryTimes > 0);
		}*/
		Integer ret = bootUp();
		to(TAG);

		return ret;
	}



	private Integer bootUp() throws Exception {

		BluetoothGattCharacteristic result;
		Future<BluetoothGattCharacteristic> futureA, futureC, futureD;

		futureA = mExecutor.submit(new CsBleReceiveNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.POWER_ON_STATUS_EVENT, 30000));

		byte[] bootCsArray = new byte[2];
	    bootCsArray[0] = (byte) 0x01;
		futureC = mExecutor.submit(new CsBleWriteCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.POWER_ON_REQUEST, bootCsArray));
		if (futureC.get() == null) {

			Log.d(TAG, "[CS] Boot up CS fails: " + Common.ERROR_GATT_WRITE);
			return Common.ERROR_BOOT_UP_CS;
		}

		result = futureA.get();
		if (result != null) {
				if (CsBleGattAttributeUtil.isFirmwareUpdating(result))
				{
					Log.d(TAG, "[CS] Firmware updating+");
					futureD = mExecutor.submit(new CsBleReceiveNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.LAST_FWUPDATE_RESULT_EVENT, 300000));
					result = futureD.get();
					if (result != null)
					{
						byte[] resultValue = result.getValue();
						while ((resultValue[1] != (byte) 4) && (resultValue[1] != (byte) 5))
						{
							Log.d(TAG, "[CS] Firmware updating:" + resultValue[1]);
							futureD = mExecutor.submit(new CsBleReceiveNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.LAST_FWUPDATE_RESULT_EVENT, 300000));
							result = futureD.get();
							if (result != null)
							{
								resultValue = result.getValue();
							}
							else
							{
								Log.d(TAG, "[CS] Firmware updating no response");

								sendFwUpdateResultMessage(false);

								return Common.ERROR_FW_UPDATE_FAIL;
							}
						}

						Log.d(TAG, "[CS] Firmware updating-");
						Log.d(TAG, "[CS] After sending command, CS is booted up!!");
						Log.d(TAG, "[CS] Last firmware update status:" + result.getValue()[1] );

						sendFwUpdateResultMessage(true);

						return Common.ERROR_FW_UPDATE_SUCCESS;

					} else {
						Log.d(TAG, "[CS] Firmware update begin no response");

						sendFwUpdateResultMessage(false);

						return Common.ERROR_FW_UPDATE_FAIL;
					}

				}
				else
				{
					if ( CsBleGattAttributeUtil.isBootUpReady(result))
					{
						Log.d(TAG, "[CS] After sending command, CS linux boots up!!");
						return Common.ERROR_SUCCESS;
					}
					else
					{
						Log.d(TAG, "[CS] After sending command, CS boot up is failed!!");
					}

				}
		} else {

			Log.d(TAG, "[CS] Boot up CS fails TIMEOUT: " + Common.ERROR_GATT_RECEIVE_NOTIFICATION);
		}

		return Common.ERROR_BOOT_UP_CS;
	}


	/*
	private Integer bootUpOld() throws Exception {

		Integer ret = Common.ERROR_BOOT_UP_CS;

		BluetoothGattCharacteristic result;
		Future<BluetoothGattCharacteristic> futureA, futureB, futureC;

		futureA = mExecutor.submit(new CsBleReadCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CS_BOOT_UP_READY));
		result = futureA.get();

		if (result != null) {

			if (CsBleGattAttributeUtil.isBootUpReady(result)) {

				Log.d(TAG, "[CS] CS is already boot up!!");

				ret = Common.ERROR_SUCCESS;

			} else {

				Log.d(TAG, "[CS] CS is standby!!");

				futureA = mExecutor.submit(new CsBleReceiveNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CS_BOOT_UP_READY, 2500));
				///futureB = mExecutor.submit(new CsBleSetNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CS_BOOT_UP_READY, true));
				///if (futureB.get() == null) {

					///unregisterNotify(CsBleGattAttributes.CS_BOOT_UP_READY);
					///return Common.ERROR_GATT_SET_NOTIFICATION;
				///}

				byte[] bootCsArray = {(byte) 0x01};
				futureC = mExecutor.submit(new CsBleReliableWriteCallable(mCsBleTransceiver, mExecutor, mBluetoothDevice, CsBleGattAttributes.CS_BOOT_UP_CS, bootCsArray));
				if (futureC.get() == null) {

					///unregisterNotify(CsBleGattAttributes.CS_BOOT_UP_READY);
					Log.d(TAG, "[CS] Boot up CS fails: " + Common.ERROR_GATT_WRITE);
					return Common.ERROR_BOOT_UP_CS;
				}

				result = futureA.get();
				if (result != null) {

					if (CsBleGattAttributeUtil.isBootUpReady(result)) {

						Log.d(TAG, "[CS] After sending command, CS is boot up!!");

						ret = Common.ERROR_SUCCESS;
					}

				} else {

					Log.d(TAG, "[CS] Boot up CS fails TIMEOUT: " + Common.ERROR_GATT_RECEIVE_NOTIFICATION);
					ret = Common.ERROR_BOOT_UP_CS;
				}

				///unregisterNotify(CsBleGattAttributes.CS_BOOT_UP_READY);
			}

		} else {

			Log.d(TAG, "[CS] Boot up CS fails: " + Common.ERROR_GATT_READ);
			ret = Common.ERROR_BOOT_UP_CS;
		}

		return ret;
	}
	*/


	private void from() {

		if (bPerformanceNotify) {

			mTimePrev = System.currentTimeMillis();
		}
	}



	private void to(String task) {

		if (bPerformanceNotify) {

			long timeCurr = System.currentTimeMillis();
			long timeDiff = timeCurr - mTimePrev;

			Log.d(TAG, "[CS][MPerf] [" + task + "] costs: " + timeDiff + " ms");
		}
	}

	private void sendFwUpdateResultMessage(boolean isSuccess) {

		try {
			Message outMsg = Message.obtain();
			outMsg.what = ICsConnectivityService.CB_FWUPDATE_RESULT;

			Bundle outData = new Bundle();
			outData.putSerializable(ICsConnectivityService.PARAM_RESULT, isSuccess ? ICsConnectivityService.Result.RESULT_SUCCESS : ICsConnectivityService.Result.RESULT_FAIL);
			outMsg.setData(outData);

			mMessenger.send(outMsg);

		} catch (RemoteException e) {

			e.printStackTrace();
		}
	}
}
