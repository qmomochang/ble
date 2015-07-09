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



public class CsSetProfileGeneralRequest extends CsConnectivityTask {

	private final static String TAG = "CsSetProfileGeneralRequest";

	private BluetoothDevice mBluetoothDevice;
	private int mAction;
	private String mName;
    private String mUserID;
    private String mUserName;
    private String mDataSrcId;
    private byte [] mAccessToken;
    private byte [] mRefreshToken;
    private int mBirthYear;
    private int mBirthMonth;
    private int mBirthDay;
    private int mHeight;
    private int mWeight;
    private byte [] mUserNamePic;

	public CsSetProfileGeneralRequest(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, BluetoothDevice device,
        int action,     String userID,  String userName,    byte [] accessToken,    byte [] refreshToken,   String dataSrcId,
        int birthYear,  int birthMonth, int birthDay,       int height,             int weight,             byte [] userNamePic) {

		super(csBleTransceiver, messenger, executor);

		mBluetoothDevice = device;
		mAction = action;
        mUserID = userID;
        mUserName = userName;
        mDataSrcId = dataSrcId;
        mAccessToken = accessToken;
        mRefreshToken = refreshToken;
        mDataSrcId = dataSrcId;
        mBirthYear = birthYear;
        mBirthMonth = birthMonth;
        mBirthDay = birthDay;
        mHeight = height;
        mWeight = weight;
        mUserNamePic = userNamePic;
	}

	private void setName(String name)
	{
		mName = name;
	}

	@Override
	public void execute() throws Exception {

		super.execute();

		super.from();

        BluetoothGattCharacteristic result;
        Future<BluetoothGattCharacteristic> futureA0, futureA1, futureB;

        byte[] inArray = new byte[195];
        inArray[0] = (byte) mAction;

        byte[] byteUserID       = mUserID.getBytes("ISO-8859-1");
        byte[] byteUserName     = mUserName.getBytes("ISO-8859-1");
        byte[] byteDataSrcId    = mDataSrcId.getBytes("ISO-8859-1");

        int i;
        for(i=1;i<=15;i++)
            inArray[i] = byteUserID[i-1];
        for(i=16;i<=31;i++)
            inArray[i] = byteUserName[i-16];
        for(i=32;i<=63;i++)
            inArray[i] = mAccessToken[i-32];
        for(i=64;i<=95;i++)
            inArray[i] = mRefreshToken[i-64];
        for(i=96;i<=128;i++)
            inArray[i] = byteDataSrcId[i-96];
        inArray[129] = (byte) (mBirthYear & (int)0x00FF);;
        inArray[130] = (byte) ((mBirthYear >> 8) & (int)0x00FF);;
        inArray[131] = (byte) mBirthMonth;
        inArray[132] = (byte) mBirthDay;
        inArray[133] = (byte) mHeight;
        inArray[134] = (byte) mWeight;
        for(i=135;i<194;i++)
            inArray[i] = mUserNamePic[i-135];


        futureA0 = mExecutor.submit(new CsBleReceiveNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.PROFILE_GENERAL_EVENT));
        futureA1 = mExecutor.submit(new CsBleSetNotificationCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.PROFILE_GENERAL_EVENT, true));

        if (futureA1.get() == null) {

            sendMessage(false, null);
            unregisterNotify(CsBleGattAttributes.CsV1CommandEnum.PROFILE_GENERAL_EVENT);
            return;
        }

        futureB = mExecutor.submit(new CsBleWriteCallable(mCsBleTransceiver, mBluetoothDevice, CsBleGattAttributes.CsV1CommandEnum.PROFILE_GENERAL_REQUEST, inArray));

        result = futureB.get();

        if (result != null) {

            result = futureA0.get();
            if (result != null)
            {
                byte [] retArray = CsBleGattAttributeUtil.getCharValue(result);
                sendMessage(true, retArray);
            }
            else
            {
                sendMessage(false, null);
                unregisterNotify(CsBleGattAttributes.CsV1CommandEnum.PROFILE_GENERAL_EVENT);
            }

        } else {
            sendMessage(false, null);
        }

        super.to(TAG);
    }

    private void sendMessage(boolean result, byte [] retArray) {

        try {

			Message outMsg = Message.obtain();

            outMsg.what = ICsConnectivityService.CB_SET_PROFILE_RESULT;

			Bundle outData = new Bundle();

			if (result) {
				outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_SUCCESS);
			} else {
				outData.putSerializable(ICsConnectivityService.PARAM_RESULT, ICsConnectivityService.Result.RESULT_FAIL);
			}

            outData.putByteArray(ICsConnectivityService.PARAM_PROFILE_GENERAL_EVENT_RESULT, retArray);

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

	private void unregisterNotify(CsBleGattAttributes.CsV1CommandEnum commandID) throws Exception {

		Future<BluetoothGattCharacteristic> future;

		future = mExecutor.submit(new CsBleSetNotificationCallable(mCsBleTransceiver, mBluetoothDevice, commandID, false));
		if (future.get() == null) {

			Log.d(TAG, "[CS] unregisterNotify error!!!");
			return;
		}
	}

}
