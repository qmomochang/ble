package com.htc.blelib.v1.internal.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.htc.blelib.interfaces.ICsConnectivityServiceBase.PlugIO;
import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.interfaces.ICsConnectivityService.GeneralRequest;

import com.htc.blelib.v1.internal.callables.CsBleReceiveNotificationCallable;
import com.htc.blelib.v1.internal.callables.CsBleWriteCallable;
import com.htc.blelib.v1.internal.callables.CsBootUpCallable;
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



public class CsGeneralRequestTask extends CsConnectivityTask {

    private final static String TAG = "CsGeneralRequestTask";

    public final static int ACTION_READ = 0;
    public final static int ACTION_WRITE = 1;
    public final static int ACTION_GET_HW_STATUS = 2;

    private BluetoothDevice mBluetoothDevice;
    private int mAction;
    private int mUnit;
    private int mLanguage;
    private int mSound;
    private int mBei;



    public CsGeneralRequestTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device,
        GeneralRequest.ACTION action,
        GeneralRequest.UNIT unit,
        GeneralRequest.LANGUAGE language,
        GeneralRequest.SOUND sound,
        GeneralRequest.BEI bei ) {

        super(csBleTransceiver, messenger, executor);

        mBluetoothDevice    = device;

        mAction             = action.getValue();
        mUnit               = unit.getValue();
        mLanguage           = language.getValue();
        mSound              = sound.getValue();
        mBei                = bei.getValue();
    }



    @Override
    public void execute() throws Exception {

        super.execute();

        super.from();

        BluetoothGattCharacteristic result;
        Future<BluetoothGattCharacteristic> future, futureA;

        if (mAction == ACTION_GET_HW_STATUS) {
            Future<Integer> futureBoot;
            Integer bootResult;
            futureBoot = mExecutor.submit(new CsBootUpCallable(mCsBleTransceiver, mExecutor, mBluetoothDevice, mMessenger));
            bootResult = futureBoot.get();
            if (bootResult == Common.ERROR_SUCCESS)
            {

                byte[] dataArray = new byte[5];
                dataArray[0] = (byte) mAction;
                dataArray[1] = (byte) mUnit;
                dataArray[2] = (byte) mLanguage;
                dataArray[3] = (byte) mSound;
                dataArray[4] = (byte) mBei;

                futureA = mExecutor.submit(new CsBleReceiveNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.CS_GENERAL_PURPOSE_REQUEST));
                future  = mExecutor.submit(new CsBleWriteCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.CS_GENERAL_PURPOSE_REQUEST, dataArray));
                result = future.get();
                if (result != null)
                {
                    result = futureA.get();
                    if (result != null)
                    {
                         byte[] retArray = CsBleGattAttributeUtil.getGeneralPurposeEvent(result);

                        sendMessage(true, retArray);
                    }
                    else
                    {
                        sendMessage(false, null);
                    }
                }
                else
                {
                    sendMessage(false, null);
                }
            }
            else
            {
                Log.d(TAG, "[CS] boot up is fail");
                sendMessage(false, null);
            }
        }

        super.to(TAG);
    }



    private void sendMessage(boolean result, byte [] retDataArr) {

        try {

            Message outMsg = Message.obtain();

            outMsg.what = ICsConnectivityService.CB_GET_GENERAL_PURPOSE_RESULT;

            Bundle outData = new Bundle();

            if (result) {
                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_SUCCESS);
            } else {
                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_FAIL);
            }

            if (retDataArr != null ) {
                outData.putSerializable(ICsConnectivityService.PARAM_GENERAL_PURPOSE_UNITS,     retDataArr[1]);
                outData.putSerializable(ICsConnectivityService.PARAM_GENERAL_PURPOSE_LANGUAGE,  retDataArr[2]);
                outData.putSerializable(ICsConnectivityService.PARAM_GENERAL_PURPOSE_SOUND,     retDataArr[3]);
                outData.putSerializable(ICsConnectivityService.PARAM_GENERAL_PURPOSE_BEI,       retDataArr[4]);
            }
            outMsg.setData(outData);

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
