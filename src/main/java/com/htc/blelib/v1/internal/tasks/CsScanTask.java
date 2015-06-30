package com.htc.blelib.v1.internal.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.htc.blelib.v1.internal.callables.CsScanStartCallable;
import com.htc.blelib.v1.internal.callables.CsScanStopCallable;
import com.htc.blelib.v1.internal.common.BaseAlarmService;
import com.htc.blelib.v1.internal.common.Common;
import com.htc.blelib.v1.internal.common.CsConnectivityTask;
import com.htc.blelib.v1.internal.common.IAlarmService;
import com.htc.blelib.v1.internal.component.le.CsBleScanner;
import android.content.Context;
import android.os.Messenger;
import android.util.Log;



public class CsScanTask extends CsConnectivityTask {

	private final static String TAG = "CsScanTask";

	private static final int DEFAULT_SCAN_PERIOD_MS = 3000;

	private final CsBleScanner mCsBleScanner;
	private final int mScanPeriodMs;
	private final boolean bScan;
	private static BaseAlarmService alarmTimeoutRequest = null;



	public CsScanTask(CsBleScanner csBleScanner, Messenger messenger, ExecutorService executor, int periodMs, boolean scan) {

		super(null, messenger, executor);

		mCsBleScanner = csBleScanner;

		if (periodMs <= 0) {

			mScanPeriodMs = DEFAULT_SCAN_PERIOD_MS;

		} else {

			mScanPeriodMs = periodMs;
		}

		bScan = scan;
	}



	@Override
	public void execute() throws Exception {

		super.execute();

		if (mCsBleScanner != null) {

			Integer result;
			Callable<Integer> callable;
			Future<Integer> future;

			Log.d(TAG, "[CS] bScan = " + bScan);

			if (bScan) {

				callable = new CsScanStartCallable(mCsBleScanner, mMessenger);
				future = mExecutor.submit(callable);

				result = future.get();
				Log.d(TAG, "[CS] future result = " + result);

				if (result == 0) {

					addScanTimeoutRequestAlarm(mScanPeriodMs);
				}

			} else {

				removeScanTimeoutRequestAlarm();

            	callable = new CsScanStopCallable(mCsBleScanner, mMessenger);
    			future = mExecutor.submit(callable);

    			result = future.get();
    			Log.d(TAG, "[CS] future result = " + result);
			}

		}
	}



	private synchronized void addScanTimeoutRequestAlarm(long periodMs) {

        Context context = mCsBleScanner.getContext();
        final int id = Common.ALARM_SCAN_TIMEOUT;

        Log.d(TAG, "[CS] addScanTimeoutRequestAlarm periodMs = " + periodMs);

        if (context == null) return;

        if (alarmTimeoutRequest != null) {
        	alarmTimeoutRequest.deinitAlarm(id);
        	alarmTimeoutRequest = null;
        }

        if (context != null) {

        	alarmTimeoutRequest = new BaseAlarmService("CsScanTimeout", context);

            try {

        		IAlarmService alarmService = new IAlarmService() {

                    @Override
                    public void onAlarm() {

                    	Log.d(TAG, "[CS] onAlarm: ALARM_SCAN_TIMEOUT");
            			Integer result;
            			Callable<Integer> callable;
            			Future<Integer> future;

                        if (alarmTimeoutRequest != null) {
                        	alarmTimeoutRequest.deinitAlarm(id);
                        	alarmTimeoutRequest = null;
                        }

                        try {

                        	callable = new CsScanStopCallable(mCsBleScanner, mMessenger);
                			future = mExecutor.submit(callable);

                			result = future.get();
                			Log.d(TAG, "[CS] future result = " + result);

            			} catch (Exception e) {

							e.printStackTrace();
						}
                    }
        		};

        		alarmTimeoutRequest.initAlarm(System.currentTimeMillis() + periodMs, id, alarmService);

            } catch (Exception e) {

                Log.d(TAG, "[CS] addScanTimeoutRequestAlarm e: " + e);
            }
        }
    }



	private synchronized void removeScanTimeoutRequestAlarm() {

        final int id = Common.ALARM_SCAN_TIMEOUT;

        if (alarmTimeoutRequest != null) {
        	alarmTimeoutRequest.deinitAlarm(id);
        	alarmTimeoutRequest = null;
        }
	}



	@Override
	public void error(Exception e) {

	}
}
