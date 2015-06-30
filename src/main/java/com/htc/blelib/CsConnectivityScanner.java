package com.htc.blelib;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.htc.blelib.interfaces.ICsConnectivityScanner;
import com.htc.blelib.interfaces.ICsConnectivityDevice.CsVersion;
import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.internal.common.CsConnectivityTask;
import com.htc.blelib.v1.internal.component.le.CsBleScanner;
import com.htc.blelib.v1.internal.component.le.CsBleScannerListener;
import com.htc.blelib.v1.internal.tasks.CsScanTask;

public class CsConnectivityScanner implements ICsConnectivityScanner {

	private static final String TAG = "CsConnectivityScanner";

	private Context mContext;
	private Messenger mMessenger;
	private ExecutorService mExecutor;
	private CsBleScanner mCsBleScanner;

	private Thread mTaskThread;
	private final LinkedBlockingQueue<CsConnectivityTask> mTaskQueue = new LinkedBlockingQueue<CsConnectivityTask>();
	private AtomicBoolean mIsTaskThreadInterrupted = new AtomicBoolean(false);

	private CsBleScannerListener mCsBleScannerListener;

	public CsConnectivityScanner(Context context, Messenger messenger) {
		try {
			mContext = context;
			mMessenger = messenger;

			// For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
	        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return;
            }

            mCsBleScanner = new CsBleScanner(context, bluetoothManager);

			mCsBleScannerListener = new CsBleScannerListener(){

				@Override
				public void onScanHit(BluetoothDevice device, CsVersion deviceVersion) {
					Log.d(TAG, "[CS] onScanHit. device = " + device);

					try {

						Message outMsg = Message.obtain();
						outMsg.what = ICsConnectivityService.CB_BLE_SCAN_RESULT;
						Bundle outData = new Bundle();
						outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ScanResult.SCAN_RESULT_HIT);
						outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);
						outData.putSerializable(ICsConnectivityScanner.PARAM_BLUETOOTH_DEVICE_VERSION, deviceVersion);
						outMsg.setData(outData);

						mMessenger.send(outMsg);

					} catch (RemoteException e) {

						e.printStackTrace();
					}
				}

				@Override
				public void onScanHitConnected(BluetoothDevice device, CsVersion deviceVersion) {
					Log.d(TAG, "[CS] onScanHitConnected. device = " + device);

					try {

						Message outMsg = Message.obtain();
						outMsg.what = ICsConnectivityService.CB_BLE_SCAN_RESULT;
						Bundle outData = new Bundle();
						outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ScanResult.SCAN_RESULT_HIT_CONNECTED);
						outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);
						outData.putSerializable(ICsConnectivityScanner.PARAM_BLUETOOTH_DEVICE_VERSION, deviceVersion);
						outMsg.setData(outData);

						mMessenger.send(outMsg);

					} catch (Exception e) {

						e.printStackTrace();
					}
				}
			};
			mCsBleScanner.registerListener(mCsBleScannerListener);

			open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean csOpen() {
		Log.d(TAG, "[CS] csOpen++");

		boolean ret = false;

		try {
			open();

			ret = true;
		} catch (Exception e) {
			Log.e(TAG, "[CS] csOpen exception: " + e);
		}

		Log.d(TAG, "[CS] csOpen--");

		return ret;
	}

	@Override
	public boolean csClose() {
		Log.d(TAG, "[CS] csClose++");

		boolean ret = false;

		try {
			close();

			ret = true;
		} catch (Exception e) {
			Log.e(TAG, "[CS] csClose exception: " + e);
		}

		Log.d(TAG, "[CS] csClose--");

		return ret;
	}

	@Override
	public boolean csScan(int period) {
		Log.d(TAG, "[CS] csScan++");

		boolean ret = false;

		try {
			CsConnectivityTask task = new CsScanTask(mCsBleScanner, mMessenger, mExecutor, period, true);
			addTask(task);

			ret = true;
		} catch (Exception e) {
			Log.d(TAG, "[CS] csScan exception: " + e);
		}

		Log.d(TAG, "[CS] csScan--");

		return ret;
	}

	@Override
	public boolean csStopScan() {
		Log.d(TAG, "[CS] csStopScan++");

		boolean ret = false;

		try {
			CsConnectivityTask task = new CsScanTask(mCsBleScanner, mMessenger, mExecutor, 0, false);
			addTask(task);

			ret = true;
		} catch (Exception e) {
			Log.d(TAG, "[CS] csStopScan exception: " + e);
		}

		Log.d(TAG, "[CS] csStopScan--");

		return ret;
	}

	private void open() {
		if (mExecutor == null) {
			mExecutor = Executors.newCachedThreadPool();
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
                    Log.v(TAG,"[CS] mTaskThread = "+mTaskThread);
					if (mTaskThread == null) {
						Runnable taskRunnable = new Runnable() {

							@Override
							public void run() {
                                Log.v(TAG,"[CS] run() mIsTaskThreadInterrupted.get() = "+mIsTaskThreadInterrupted.get());
								while (mIsTaskThreadInterrupted.get() == false) {
									CsConnectivityTask task = null;
									try {
										task = mTaskQueue.poll(500, TimeUnit.MILLISECONDS);
										if (task != null) {
											Log.d(TAG, "[CS] Executing task = " + task);
											task.execute();
										}
									} catch (Exception e) {
										Log.d(TAG, "[CS] taskRunnable e = " + e);
										e.printStackTrace();

										if (task != null) {
											task.error(e);
										}
									}
								}
							}
						};
						mTaskThread = new Thread(taskRunnable, "CsConnectivityScannerTaskThread");
						mTaskThread.start();
					}
				} catch (Exception e) {

					Log.d(TAG, "[CS] open e" + e);
				}
			}

		}).start();
	}

	private void close() {
		try {
			if (mExecutor != null) {
				mExecutor.shutdown();
			}

			if (mTaskThread != null) {

				mIsTaskThreadInterrupted.set(true);

				Log.d(TAG, "[CS] waiting for task executing...");

				mTaskThread.join();
			}
		} catch (InterruptedException e) {
			Log.d(TAG, "[CS] close e" + e);
		}
	}

	private void addTask(CsConnectivityTask task) throws Exception {
		Log.d(TAG, "[CS] addTask task = " + task);
		if (task != null) {
			mTaskQueue.add(task);
		}
	}
}
