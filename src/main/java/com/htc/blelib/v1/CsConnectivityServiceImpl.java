package com.htc.blelib.v1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.htc.blelib.v1.internal.common.CsConnectivityTask;
import com.htc.blelib.v1.internal.component.le.CsBleGattAttributes;
import com.htc.blelib.v1.internal.component.le.CsBleTransceiver;
import com.htc.blelib.v1.internal.tasks.CsLongTermEventTask;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Messenger;
import android.util.Log;



public class CsConnectivityServiceImpl {

	private final static String TAG = "CsConnectivityServiceImpl";

	protected Context mContext;
	protected Messenger mMessenger;
	protected ExecutorService mExecutor;
	protected BluetoothManager mBluetoothManager;
	protected CsBleTransceiver mCsBleTransceiver;

	private boolean bServiceAvailable = false;

	private Thread mTaskThread;
	private final LinkedBlockingQueue<CsConnectivityTask> mTaskQueue = new LinkedBlockingQueue<CsConnectivityTask>();
	private AtomicBoolean mIsTaskThreadInterrupted = new AtomicBoolean(false);

	private Thread mLongTermEventThread;
	private CsLongTermEventTask mCsConnectivityLongTermEventTask;

	private int mSkipTaskCount;



	private ICsConnectivityServiceListener mCsConnectivityServiceListener = new ICsConnectivityServiceListener() {

		@Override
		public void onError(int errorCode) {

			Log.d(TAG, "[CS] onError errorCode = " + errorCode);

			if (errorCode == 881) {

				try {

					mSkipTaskCount = mTaskQueue.size();

					Log.d(TAG, "[CS] onError mSkipTaskCount = " + mSkipTaskCount);

					///clearTaskQueue();

				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		}
	};



	public CsConnectivityServiceImpl(Context context, Messenger messenger) {

		try {

			Log.d(TAG, "[CS] onCreate");

			mContext = context;
			mMessenger = messenger;
			mSkipTaskCount = 0;

			// For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
	        if (mBluetoothManager == null) {

	        	mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
	            if (mBluetoothManager == null) {
	                Log.e(TAG, "Unable to initialize BluetoothManager.");
	                return;
	            }
	        }

			mCsBleTransceiver = new CsBleTransceiver(mContext, mBluetoothManager);

			mCsConnectivityLongTermEventTask = new CsLongTermEventTask(mCsBleTransceiver, mMessenger, mExecutor, mCsConnectivityServiceListener);

			open();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}



	protected synchronized void addTask(CsConnectivityTask task) throws Exception {

		Log.d(TAG, "[CS] addTask task = " + task);

		if (task != null) {

			mTaskQueue.add(task);
		}
	}



	protected synchronized void clearTaskQueue() throws Exception {

		mTaskQueue.clear();
	}



	protected synchronized boolean getServiceAvailable() {

		return bServiceAvailable;
	}



	protected synchronized void setServiceAvailable(boolean value) {

		bServiceAvailable = value;
	}



	protected void registerLTEvent(BluetoothDevice device, CsBleGattAttributes.CsV1CommandEnum commandID) {

		mCsConnectivityLongTermEventTask.registerUuid(device, commandID);
	}



	protected void unregisterLTEvent(BluetoothDevice device, CsBleGattAttributes.CsV1CommandEnum commandID) {

		mCsConnectivityLongTermEventTask.unregisterUuid(device, commandID);
	}



	protected void open() {

		Log.d(TAG, "[CS] open");

		if (mExecutor == null) {

			mExecutor = Executors.newFixedThreadPool(6);
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {

					if (mTaskThread == null) {

						mTaskThread = new Thread(mTaskRunnable, "CsConnectivityTaskThread");
						mTaskThread.start();
					}

					if (mLongTermEventThread == null) {

						mLongTermEventThread = new Thread(mLongTermEventRunnable, "CsConnectivityLongTermEventThread");
						mLongTermEventThread.start();
					}

				} catch (Exception e) {

					Log.d(TAG, "[CS] open e" + e);
				}
			}

		}).start();
	}



	protected void close() {

		Log.d(TAG, "[CS] close");

		try {

			if (mExecutor != null) {

				mExecutor.shutdown();
			}

			if (mTaskThread != null) {

				mIsTaskThreadInterrupted.set(true);

				Log.d(TAG, "[CS] waiting for task executing...");

				mTaskThread.join();
			}

			if (mLongTermEventThread != null) {

				mLongTermEventThread.interrupt();
				mLongTermEventThread.join();
			}

			mCsBleTransceiver.deInit();

		} catch (Exception e) {

			Log.d(TAG, "[CS] close e" + e);
		}
	}



	private final Runnable mTaskRunnable = new Runnable() {

		@Override
		public void run() {

			while (mIsTaskThreadInterrupted.get() == false) {

				CsConnectivityTask task = null;

				try {
					task = mTaskQueue.poll(500, TimeUnit.MILLISECONDS);

					if (task != null) {
						Log.d(TAG, "[CS] got task, reamin mTaskQueue.size() = " + mTaskQueue.size() + ", mSkipTaskCount = " + mSkipTaskCount);

						if (mSkipTaskCount > 0) {
							mSkipTaskCount--;
							Log.d(TAG, "[CS] Skipping task = " + task);
							task.error(null);
						} else {
							mSkipTaskCount = 0;
							Log.d(TAG, "[CS] Executing task = " + task);
							task.execute();
						}
					}
				} catch (Exception e) {
					Log.d(TAG, "[CS] mTaskRunnable e = " + e);
					e.printStackTrace();
					if (task != null) {
						task.error(e);
					}
				}
			}
		}
	};



	private final Runnable mLongTermEventRunnable = new Runnable() {

		@Override
		public void run() {

			while (mLongTermEventThread.isInterrupted() == false) {

				try {

					mCsConnectivityLongTermEventTask.execute();

				} catch (InterruptedException e) {

					Log.d(TAG, "[CS] mLongTermEventRunnable interrupted");
					break;
				} catch (Exception e) {

					Log.d(TAG, "[CS] mLongTermEventRunnable e = " + e);
				}

			}
		}
	};
}
