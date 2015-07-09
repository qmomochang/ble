package com.htc.blelib.v1.internal.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.internal.callables.CsBleReadCallable;
import com.htc.blelib.v1.internal.callables.CsBleReceiveNotificationCallable;
import com.htc.blelib.v1.internal.callables.CsBleSetNotificationCallable;
import com.htc.blelib.v1.internal.callables.CsBleWriteCallable;
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



public class CsSetDatetimeRequestTask extends CsConnectivityTask {

    private final static String TAG = "CsSetDatetimeRequestTask";

    private BluetoothDevice mBluetoothDevice;
    private int mUwYear16;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private int mSecond;
    private int mTimeZone;

    public CsSetDatetimeRequestTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device, int uwYear16, int month, int day, int hour, int minute, int second, int timeZone) {

        super(csBleTransceiver, messenger, executor);

        mBluetoothDevice = device;

        mUwYear16   = uwYear16;
        mMonth      = month;    // 0-11
        mDay        = day;      // 1-31
        mHour       = hour;     // 0-23
        mMinute     = minute;   // 0-59
        mSecond     = second;   // 0-59
        mTimeZone   = timeZone; // offset in minutes (Default offset: 1000, Range:280 ~ 1840(Original Range: -720 ~ 840))  (0x0: non Time zone)
    }

    @Override
    public void execute() throws Exception {

        super.execute();

        super.from();

        BluetoothGattCharacteristic result;
        Future<BluetoothGattCharacteristic> futureB;

        byte [] inArray = new byte[8];
        inArray[0] = (byte) ((mUwYear16) & (int)0x00FF);
        inArray[1] = (byte) ((mUwYear16 >> 8) & (int)0x00FF);
        inArray[2] = (byte) mMonth;
        inArray[3] = (byte) mDay;
        inArray[4] = (byte) mHour;
        inArray[5] = (byte) mMinute;
        inArray[6] = (byte) mSecond;
        inArray[7] = (byte) mTimeZone;

        futureB = mExecutor.submit(new CsBleWriteCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.SET_DATETIME_REQUEST, inArray));

        result = futureB.get();

        if (result != null) {
            sendMessage(true, null);
        } else {
            sendMessage(false, null);
        }
        super.to(TAG);
    }

    private void sendMessage(boolean result, String reserved) {

        try {

            Message outMsg = Message.obtain();

            outMsg.what = ICsConnectivityService.CB_SET_DATE_TIME_RESULT;

            Bundle outData = new Bundle();

            if (result) {
                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_SUCCESS);
            } else {
                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_FAIL);
            }

            //outMsg.setData(outData);

            mMessenger.send(outMsg);

        } catch (RemoteException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void error(Exception e) {

        sendMessage(false, null);
    }

}
