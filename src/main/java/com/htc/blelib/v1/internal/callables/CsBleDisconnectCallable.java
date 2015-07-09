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



public class CsBleDisconnectCallable implements Callable<Integer> {

    private final static String TAG = "CsBleDisconnectCallable";

    private final static int DEFAULT_CALLABLE_TIMEOUT = 60000;

    protected CsBleTransceiver mCsBleTransceiver;
    protected BluetoothDevice mBluetoothDevice;
    protected boolean bForce;

    private final LinkedBlockingQueue<CsBleTransceiverErrorCode> mCallbackQueue = new LinkedBlockingQueue<CsBleTransceiverErrorCode>();
    private Integer mStatus;



    private CsBleTransceiverListener mCsBleTransceiverListener = new CsBleTransceiverListener() {

        @Override
        public void onDisconnected(BluetoothDevice device) {

            Log.d(TAG, "[CS] onDisconnected. device = " + device);

            if (device.equals(mBluetoothDevice)) {

                addCallback(CsBleTransceiverErrorCode.ERROR_NONE);
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



    public CsBleDisconnectCallable(CsBleTransceiver transceiver, BluetoothDevice device, boolean force) {

        mCsBleTransceiver = transceiver;
        mBluetoothDevice = device;
        bForce = force;
    }



    @Override
    public Integer call() throws Exception {

        Integer ret = 0;

        mCsBleTransceiver.registerListener(mCsBleTransceiverListener);

        mStatus = 0;

        if (bForce) {

            mCsBleTransceiver.disconnectForce(mBluetoothDevice);

        } else {

            if (mCsBleTransceiver.disconnect(mBluetoothDevice)) {

                CsBleTransceiverErrorCode errorCode = mCallbackQueue.poll(DEFAULT_CALLABLE_TIMEOUT, TimeUnit.MILLISECONDS);

                if (errorCode != CsBleTransceiverErrorCode.ERROR_NONE) {

                    mStatus = -1;
                }

            } else {

                mStatus = -3;
            }
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
