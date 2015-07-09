package com.htc.blelib.v1.internal.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.internal.callables.CsBleBondCallable;
import com.htc.blelib.v1.internal.callables.CsBleConnectCallable;
import com.htc.blelib.v1.internal.callables.CsBleDisconnectCallable;
import com.htc.blelib.v1.internal.common.CsConnectivityTask;
import com.htc.blelib.v1.internal.component.le.CsBleTransceiver;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;



public class CsBleConnectTask extends CsConnectivityTask {

    private final static String TAG = "CsBleConnectTask";

    protected BluetoothDevice mBluetoothDevice;
    protected boolean bConnect;
    protected boolean bForce;

    public CsBleConnectTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device, boolean connect) {

        this(csBleTransceiver, messenger, executor, device, connect, false);
    }

    public CsBleConnectTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device, boolean connect, boolean force) {

        super(csBleTransceiver, messenger, executor);
        mBluetoothDevice = device;
        bConnect = connect;
        bForce = force;
    }



    @Override
    public void execute() throws Exception {

        super.execute();

        super.from();

        if (mCsBleTransceiver != null) {

            Integer result;
            Callable<Integer> callable;
            Future<Integer> future;

            if (bConnect) {

                callable = new CsBleConnectCallable(mCsBleTransceiver, mBluetoothDevice);

                future = mExecutor.submit(callable);

                if (future.get() != 0) {

                    sendMessage(ICsConnectivityService.CB_BLE_CONNECT_RESULT, -1);

                } else {

/*                    callable = new CsBleBondCallable(mCsBleTransceiver, mBluetoothDevice);
                    future = mExecutor.submit(callable);

                    if (future.get() != 0) {

                        sendMessage(ICsConnectivityService.CB_BLE_CONNECT_RESULT, -1);

                    } else {

                        sendMessage(ICsConnectivityService.CB_BLE_CONNECT_RESULT, 0);
                    }*/
                    sendMessage(ICsConnectivityService.CB_BLE_CONNECT_RESULT, 0);
                }

            } else {

                callable = new CsBleDisconnectCallable(mCsBleTransceiver, mBluetoothDevice, bForce);

                future = mExecutor.submit(callable);

                result = future.get();
                Log.d(TAG, "[CS] future result = " + result);

                if (bForce) {

                    sendMessage(ICsConnectivityService.CB_BLE_DISCONNECT_FORCE_RESULT, result);

                } else {

                    sendMessage(ICsConnectivityService.CB_BLE_DISCONNECT_RESULT, result);
                }
            }
        }

        super.to(TAG);
    }



    private void sendMessage(int type, int result) {

        try {

            Message outMsg = Message.obtain();
            outMsg.what = type;
            Bundle outData = new Bundle();

            if (result == 0) {

                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_SUCCESS);

            } else {

                outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_FAIL);
            }

            outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, mBluetoothDevice);

            outMsg.setData(outData);

            mMessenger.send(outMsg);

        } catch (RemoteException e) {

            e.printStackTrace();
        }
    }



    @Override
    public void error(Exception e) {

        if (bConnect) {

            sendMessage(ICsConnectivityService.CB_BLE_CONNECT_RESULT, -1);

        } else {

            sendMessage(ICsConnectivityService.CB_BLE_DISCONNECT_RESULT, -1);
        }
    }
}
