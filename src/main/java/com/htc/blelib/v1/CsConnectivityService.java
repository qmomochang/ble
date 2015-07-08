package com.htc.blelib.v1;

import java.util.Calendar;
import java.util.List;

import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.internal.common.CsConnectivityTask;
import com.htc.blelib.v1.internal.component.le.CsBleGattAttributes;

//import com.htc.blelib.v1.internal.tasks.CsAutoBackupTask;
import com.htc.blelib.v1.internal.tasks.CsBleConnectTask;
//import com.htc.blelib.v1.internal.tasks.CsCameraErrorTask;
//import com.htc.blelib.v1.internal.tasks.CsCameraModeNotifyTask;
//import com.htc.blelib.v1.internal.tasks.CsGetBroadcastSMSContentTask;
//import com.htc.blelib.v1.internal.tasks.CsGetBroadcastUserNameTask;
//import com.htc.blelib.v1.internal.tasks.CsSetBroadcastSMSContentTask;
//import com.htc.blelib.v1.internal.tasks.CsUnlockSimPinTask;
//import com.htc.blelib.v1.internal.tasks.CsFirmwareUpdateTask;
//import com.htc.blelib.v1.internal.tasks.CsFlightModeTask;
//import com.htc.blelib.v1.internal.tasks.CsGeneralPurposeCommandNotifyTask;
//import com.htc.blelib.v1.internal.tasks.CsGetAllFwVersionTask;
//import com.htc.blelib.v1.internal.tasks.CsGetAutoBackupAccountTask;
//import com.htc.blelib.v1.internal.tasks.CsGetAutoBackupIsAvailableTask;
//import com.htc.blelib.v1.internal.tasks.CsGetAutoBackupPreferenceTask;
//import com.htc.blelib.v1.internal.tasks.CsGetAutoBackupStatusTask;
//import com.htc.blelib.v1.internal.tasks.CsGetBleFWVersionTask;
//import com.htc.blelib.v1.internal.tasks.CsGetBroadcastErrorListTask;
//import com.htc.blelib.v1.internal.tasks.CsGetBroadcastInvitationListTask;
//import com.htc.blelib.v1.internal.tasks.CsGetBroadcastPlatformTask;
//import com.htc.blelib.v1.internal.tasks.CsGetBroadcastPrivacyTask;
//import com.htc.blelib.v1.internal.tasks.CsGetBroadcastSettingTask;
//import com.htc.blelib.v1.internal.tasks.CsGetBroadcastStatusTask;
//import com.htc.blelib.v1.internal.tasks.CsGetBroadcastVideoUrlTask;
//import com.htc.blelib.v1.internal.tasks.CsGetCameraModeTask;
//import com.htc.blelib.v1.internal.tasks.CsGetModemStatusTask;
//import com.htc.blelib.v1.internal.tasks.CsGpsInfoTask;
import com.htc.blelib.v1.internal.tasks.CsHwStatusTask;
//import com.htc.blelib.v1.internal.tasks.CsLTECampingStatusTask;
//import com.htc.blelib.v1.internal.tasks.CsLongTermNotifyTask;
//import com.htc.blelib.v1.internal.tasks.CsMetadataTask;
import com.htc.blelib.v1.internal.tasks.CsNameTask;
//import com.htc.blelib.v1.internal.tasks.CsOperationTask;
//import com.htc.blelib.v1.internal.tasks.CsPasswordTask;
//import com.htc.blelib.v1.internal.tasks.CsSetBroadcastUserNameTask;
//import com.htc.blelib.v1.internal.tasks.CsSetCameraModeTask;
//import com.htc.blelib.v1.internal.tasks.CsSetAutoBackupAccountTask;
//import com.htc.blelib.v1.internal.tasks.CsSetAutoBackupPreferenceTask;
//import com.htc.blelib.v1.internal.tasks.CsSetAutoBackupProviderTokenTask;
//import com.htc.blelib.v1.internal.tasks.CsSetAutoSleepTimerOffsetTask;
//import com.htc.blelib.v1.internal.tasks.CsSetBroadcastInvitationListTask;
//import com.htc.blelib.v1.internal.tasks.CsSetBroadcastPlatformTask;
//import com.htc.blelib.v1.internal.tasks.CsSetBroadcastPrivacyTask;
//import com.htc.blelib.v1.internal.tasks.CsSetBroadcastSettingTask;
//import com.htc.blelib.v1.internal.tasks.CsSetDateTimeTask;
//import com.htc.blelib.v1.internal.tasks.CsSimHwStatusTask;
//import com.htc.blelib.v1.internal.tasks.CsSoftAPConnectTask;
//import com.htc.blelib.v1.internal.tasks.CsWifiDisconnectTask;
//import com.htc.blelib.v1.internal.tasks.CsWifiGroupTask;
//import com.htc.blelib.v1.internal.tasks.CsWifiStationConnectTask;
import com.htc.blelib.v1.internal.tasks.CsBootTask;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Messenger;
import android.util.Log;



public class CsConnectivityService extends CsConnectivityServiceImpl implements ICsConnectivityService {

    private final static String TAG = "CsConnectivityService";



    public CsConnectivityService(Context context, Messenger messenger) {

        super(context, messenger);
    }

    @Override
    public boolean csBootUp(BluetoothDevice device)
    {
        Log.d(TAG, "[CS] csBootUp++");
        boolean ret = false;

        try {

            CsConnectivityTask task = new CsBootTask(mCsBleTransceiver, mMessenger, mExecutor, device);
            addTask(task);
            ret = true;
        } catch (Exception e) {
            Log.d(TAG, "[CS] csBootUp exception: " + e);
        }

        Log.d(TAG, "[CS] csBootUp--");

        return ret;
    }

    @Override
    public boolean csOpen() {

        Log.d(TAG, "[CS] csOpen++");

        boolean ret = false;

        try {

            open();

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csOpen exception: " + e);
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

            Log.d(TAG, "[CS] csClose exception: " + e);
        }

        Log.d(TAG, "[CS] csClose--");

        return ret;
    }



//    @Override
//    public boolean csCreateWifiP2pGroup() {
//
//        Log.d(TAG, "[CS] csCreateWifiP2pGroup++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsWifiGroupTask(mCsBleTransceiver, mMessenger, mExecutor, true, false);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csCreateWifiP2pGroup exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csCreateWifiP2pGroup--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csRemoveWifiP2pGroup() {
//
//        Log.d(TAG, "[CS] csRemoveWifiP2pGroup++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsWifiGroupTask(mCsBleTransceiver, mMessenger, mExecutor, false, false);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csRemoveWifiP2pGroup exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csRemoveWifiP2pGroup--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csRemoveWifiP2pGroupForce() {
//
//        Log.d(TAG, "[CS] csRemoveWifiP2pGroupForce++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsWifiGroupTask(mCsBleTransceiver, mMessenger, mExecutor, false, false, true);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csRemoveWifiP2pGroupForce exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csRemoveWifiP2pGroupForce--");
//
//        return ret;
//    }
//
//
//
    @Override
    public boolean csBleConnect(BluetoothDevice device) {

        Log.d(TAG, "[CS] csBleConnect++");

        boolean ret = false;

        try {

            CsConnectivityTask task = new CsBleConnectTask(mCsBleTransceiver, mMessenger, mExecutor, device, true);
            addTask(task);

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csBleConnect exception: " + e);
        }

        Log.d(TAG, "[CS] csBleConnect--");

        return ret;
    }



    @Override
    public boolean csBleDisconnect(BluetoothDevice device) {

        Log.d(TAG, "[CS] csBleDisconnect++");

        boolean ret = false;

        try {

            CsConnectivityTask task = new CsBleConnectTask(mCsBleTransceiver, mMessenger, mExecutor, device, false);
            addTask(task);

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csBleDisconnect exception: " + e);
        }

        Log.d(TAG, "[CS] csBleDisconnect--");

        return ret;
    }



    @Override
    public boolean csBleDisconnectForce(BluetoothDevice device) {

        Log.d(TAG, "[CS] csBleDisconnectForce++");

        boolean ret = false;

        try {

            CsConnectivityTask task = new CsBleConnectTask(mCsBleTransceiver, mMessenger, mExecutor, device, false, true);
            addTask(task);

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csBleDisconnectForce exception: " + e);
        }

        Log.d(TAG, "[CS] csBleDisconnectForce--");

        return ret;
    }
//
//
//
//    @Override
//    public boolean csWifiConnect(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csWifiConnect++");
//
//        boolean ret = false;
//
//        try {
//
//            ///CsConnectivityTask taskT = new CsWifiGroupTask(mCsBleTransceiver, mMessenger, mExecutor, true, true);
//            ///addTask(taskT);
//
//            ///CsConnectivityTask task = new CsWifiConnectTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            ///addTask(task);
//
//            CsConnectivityTask task = new CsWifiStationConnectTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csWifiConnect exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csWifiConnect--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csWifiDisconnect(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csWifiDisconnect++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsWifiDisconnectTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csWifiDisconnect exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csWifiDisconnect--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csSetPowerOnOff(BluetoothDevice device, Module module, SwitchOnOff onoff) {
//        // TODO Auto-generated method stub
//        return false;
//    }
//
//
//
//    @Override
//    public boolean csGetPowerOnOff(BluetoothDevice device, Module module) {
//        // TODO Auto-generated method stub
//        return false;
//    }
//
//
//
    @Override
    public boolean csGetHwStatus(BluetoothDevice device) {

        Log.d(TAG, "[CS] csGetHwStatus++");

        boolean ret = false;

        try {

            CsConnectivityTask task = new CsHwStatusTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsHwStatusTask.ACTION_GET_HW_STATUS);
            addTask(task);

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csGetHwStatus exception: " + e);
        }

        Log.d(TAG, "[CS] csGetHwStatus--");

        return ret;
    }
//
//
//
//    @Override
//    public boolean csSetHwStatusLTEvent(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csSetHwStatusLTEvent++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsHwStatusTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsHwStatusTask.ACTION_SET_HW_STATUS_LTEVENT);
//            addTask(task);
//
//            registerLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.HWSTATUS_EVENT);//CsBleGattAttributes.CS_HW_STATUS);
//            registerLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.POWER_ON_STATUS_EVENT);//CsBleGattAttributes.CS_BOOT_UP_READY);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetHwStatusLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetHwStatusLTEvent--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csClrHwStatusLTEvent(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csClrHwStatusLTEvent++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsHwStatusTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsHwStatusTask.ACTION_CLR_HW_STATUS_LTEVENT);
//            addTask(task);
//
//            unregisterLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.HWSTATUS_EVENT);//CsBleGattAttributes.CS_HW_STATUS);
//            unregisterLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.POWER_ON_STATUS_EVENT);//CsBleGattAttributes.CS_BOOT_UP_READY);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csClrHwStatusLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csClrHwStatusLTEvent--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csGetSimHwStatus(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetSimHwStatus++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsSimHwStatusTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsSimHwStatusTask.ACTION_GET_STATUS);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csGetSimHwStatus exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetSimHwStatus--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csSetOperationLTEvent(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csSetOperationLTEvent++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsOperationTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsOperationTask.ACTION_SET_OPERATION_LTEVENT, Operation.OPERATION_NONE);
//            addTask(task);
//
//            registerLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.OPERATION_STATUS_EVENT);//CsBleGattAttributes.CS_CAMERA_STATUS);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetOperationLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetOperationLTEvent--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csClrOperationLTEvent(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csClrOperationLTEvent++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsOperationTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsOperationTask.ACTION_CLR_OPERATION_LTEVENT, Operation.OPERATION_NONE);
//            addTask(task);
//
//            unregisterLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.OPERATION_STATUS_EVENT);//CsBleGattAttributes.CS_CAMERA_STATUS);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csClrOperationLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csClrOperationLTEvent--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csSetOperation(BluetoothDevice device, Operation operation) {
//
//        Log.d(TAG, "[CS] csSetOperation++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsOperationTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsOperationTask.ACTION_SET_OPERATION, operation);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetOperation exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetOperation--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csSetDateTime(BluetoothDevice device, Calendar calendar) {
//
//        Log.d(TAG, "[CS] csSetDateTime++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsSetDateTimeTask(mCsBleTransceiver, mMessenger, mExecutor, device, calendar);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetDateTime exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetDateTime--");
//
//        return ret;
//    }
//
//
//
    @Override
    public boolean csSetName(BluetoothDevice device, String name) {

        Log.d(TAG, "[CS] csSetName++");

        boolean ret = false;

        try {

            CsConnectivityTask task = new CsNameTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsNameTask.ACTION_SET_NAME, name);
            addTask(task);

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csSetName exception: " + e);
        }

        Log.d(TAG, "[CS] csSetName--");

        return ret;
    }



    @Override
    public boolean csGetName(BluetoothDevice device) {

        Log.d(TAG, "[CS] csGetName++");

        boolean ret = false;

        try {

            CsConnectivityTask task = new CsNameTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsNameTask.ACTION_GET_NAME, null);
            addTask(task);

            ret = true;

        } catch (Exception e) {

            Log.d(TAG, "[CS] csGetName exception: " + e);
        }

        Log.d(TAG, "[CS] csGetName--");

        return ret;
    }
//
//
//
//    @Override
//    public boolean csSetGpsInfoLTEvent(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csSetGpsInfoLTEvent++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsGpsInfoTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsGpsInfoTask.ACTION_SET_GPS_INFO_LTEVENT, null, 0, 0, 0);
//            addTask(task);
//
//            registerLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.REQUEST_GSP_DATE_EVENT);//CsBleGattAttributes.CS_REQUEST_GPS_DATA);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetGpsInfoLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetGpsInfoLTEvent--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csClrGpsInfoLTEvent(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csClrGpsInfoLTEvent++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsGpsInfoTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsGpsInfoTask.ACTION_CLR_GPS_INFO_LTEVENT, null, 0, 0, 0);
//            addTask(task);
//
//            unregisterLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.REQUEST_GSP_DATE_EVENT);//CsBleGattAttributes.CS_REQUEST_GPS_DATA);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csClrGpsInfoLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csClrGpsInfoLTEvent--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csSetGpsInfo(BluetoothDevice device, Calendar calendar, double longitude, double latitude, double altitude) {
//
//        Log.d(TAG, "[CS] csSetGpsInfo++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsGpsInfoTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsGpsInfoTask.ACTION_SET_GPS_INFO, calendar, longitude, latitude, altitude);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetGpsInfo exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetGpsInfo--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csGetBleFWVersion(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csGetBleFWVersion++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsGetBleFWVersionTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csGetBleFWVersion exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetBleFWVersion--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csVerifyPassword(BluetoothDevice device, String password) {
//
//        Log.d(TAG, "[CS] csVerifyPassword++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsPasswordTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsPasswordTask.ACTION_VERIFY_PASSWORD, password);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csVerifyPassword exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csVerifyPassword--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csChangePassword(BluetoothDevice device, String password) {
//
//        Log.d(TAG, "[CS] csChangePassword++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsPasswordTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsPasswordTask.ACTION_CHANGE_PASSWORD, password);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csChangePassword exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csChangePassword--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csSetCameraModeLTEvent(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csSetCameraModeLTEvent++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsCameraModeNotifyTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsCameraModeNotifyTask.ACTION_SET_LTEVENT);
//            addTask(task);
//
//            registerLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.GET_CAMERA_MODE_REQUEST_EVENT);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetCameraModeLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetCameraModeLTEvent--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csClrCameraModeLTEvent(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csClrCameraModeLTEvent++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsCameraModeNotifyTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsCameraModeNotifyTask.ACTION_CLR_LTEVENT);
//            addTask(task);
//
//            unregisterLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.GET_CAMERA_MODE_REQUEST_EVENT);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csClrCameraModeLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csClrCameraModeLTEvent--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csSetCameraMode(BluetoothDevice device, CameraMode mode) {
//        Log.d(TAG, "[CS] csSetCameraMode++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsSetCameraModeTask(mCsBleTransceiver, mMessenger, mExecutor, device, mode);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetCameraMode exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetCameraMode--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csGetCameraMode(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetCameraMode++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsGetCameraModeTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csGetCameraMode exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetCameraMode--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csSetMetadataLTEvent(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csSetMetadataLTEvent++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsMetadataTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsMetadataTask.ACTION_SET_METADATA_LTEVENT);
//            addTask(task);
//
//            registerLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.GET_METADATA_EVENT);//CsBleGattAttributes.CS_METADATA);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetMetadataLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetMetadataLTEvent--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csClrMetadataLTEvent(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csClrMetadataLTEvent++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsMetadataTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsMetadataTask.ACTION_CLR_METADATA_LTEVENT);
//            addTask(task);
//
//            unregisterLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.GET_METADATA_EVENT);//CsBleGattAttributes.CS_METADATA);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csClrMetadataLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csClrMetadataLTEvent--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csSetCameraErrorLTEvent(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csSetCameraErrorLTEvent++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsCameraErrorTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsCameraErrorTask.ACTION_SET_CAMERA_ERROR_LTEVENT);
//            addTask(task);
//
//            registerLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.CAMERA_ERROR_EVENT);//CsBleGattAttributes.CS_CAMERA_ERROR);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetCameraErrorLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetCameraErrorLTEvent--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csClrCameraErrorLTEvent(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csClrCameraErrorLTEvent++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsCameraErrorTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsCameraErrorTask.ACTION_CLR_CAMERA_ERROR_LTEVENT);
//            addTask(task);
//
//            unregisterLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.CAMERA_ERROR_EVENT);//CsBleGattAttributes.CS_CAMERA_ERROR);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csClrCameraErrorLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csClrCameraErrorLTEvent--");
//
//        return ret;
//    }
//
//    //tesla++
//    @Override
//    public boolean csSoftAPConnect(BluetoothDevice device, String passwd) {
//
//        Log.d(TAG, "[CS] csSoftAPConnect++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsSoftAPConnectTask(mCsBleTransceiver, mMessenger, mExecutor, device, passwd);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSoftAPConnect exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSoftAPConnect--");
//
//        return ret;
//    }
//    //tesla--
//
//
//
//    @Override
//    public boolean csSetAutoBackupLTEvent(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csSetAutoBackupLTEvent++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsAutoBackupTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsAutoBackupTask.ACTION_SET_AUTO_BACKUP_LTEVENT, -1);
//            addTask(task);
//
//            registerLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.WIFI_SCAN_RESULT_EVENT);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetAutoBackupLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetAutoBackupLTEvent--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csClrAutoBackupLTEvent(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csClrAutoBackupLTEvent++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsAutoBackupTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsAutoBackupTask.ACTION_CLR_AUTO_BACKUP_LTEVENT, -1);
//            addTask(task);
//
//            unregisterLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.WIFI_SCAN_RESULT_EVENT);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csClrAutoBackupLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csClrAutoBackupLTEvent--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csSetAutoBackupAP(BluetoothDevice device, String ssid, String passwd, byte security) {
//
//        Log.d(TAG, "[CS] csSetAutoBackupAP++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsAutoBackupTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsAutoBackupTask.ACTION_SET_AUTO_BACKUP_AP, ssid, passwd, security);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetAutoBackupAP exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetAutoBackupAP--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csClrAutoBackupAP(BluetoothDevice device, byte security, String ssid) {
//
//        Log.d(TAG, "[CS] csClrAutoBackupAP++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsAutoBackupTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsAutoBackupTask.ACTION_CLR_AUTO_BACKUP_AP, ssid, null, security);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csClrAutoBackupAP exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csClrAutoBackupAP--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csSetLTNotify(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csSetLTNotify++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsLongTermNotifyTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsLongTermNotifyTask.ACTION_SET_LTNOTIFY);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetLTNotify exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetLTNotify--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csClrLTNotify(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csClrLTNotify++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsLongTermNotifyTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsLongTermNotifyTask.ACTION_CLR_LTNOTIFY);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csClrLTNotify exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csClrLTNotify--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csSetAutoBackupAPScan(BluetoothDevice device, int startorstop, int option) {
//
//        Log.d(TAG, "[CS] csSetAutoBackupAPScan++");
//
//        boolean ret = false;
//
//        try {
//            CsConnectivityTask task;
//            if (startorstop == 0)
//                task = new CsAutoBackupTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsAutoBackupTask.ACTION_SET_AUTO_BACKUP_AP_SCAN_START, option);
//            else
//                task = new CsAutoBackupTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsAutoBackupTask.ACTION_SET_AUTO_BACKUP_AP_SCAN_STOP, option);
//            addTask(task);
//
//            //registerLTEvent(device, CsBleGattAttributes.CS_AUTO_BACKUP_SCAN_RESULT);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetAutoBackupAPScan exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetAutoBackupAPScan--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csSetAutoBackupProxy(BluetoothDevice device, int port, byte security, String ssid, String proxy) {
//
//        Log.d(TAG, "[CS] csSetAutoBackupProxy++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsAutoBackupTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsAutoBackupTask.ACTION_SET_AUTO_BACKUP_PROXY, ssid, port, proxy, security);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetAutoBackupProxy exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetAutoBackupProxy--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csGetAutoBackupProxy(BluetoothDevice device, byte security, String ssid) {
//
//        Log.d(TAG, "[CS] csGetAutoBackupProxy++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsAutoBackupTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsAutoBackupTask.ACTION_GET_AUTO_BACKUP_PROXY, ssid, 0, null, security);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csGetAutoBackupProxy exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetAutoBackupProxy--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csGetAllFwVersion(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csGetAllFwVersion++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsGetAllFwVersionTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csGetAllFwVersion exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetAllFwVersion--");
//
//        return ret;
//    }
//
//
//
//    @Override
//    public boolean csGetAutoBackupStatus(BluetoothDevice device) {
//
//        Log.d(TAG, "[CS] csGetAutoBackupStatus++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsGetAutoBackupStatusTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csGetAutoBackupStatus exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetAutoBackupStatus--");
//
//        return ret;
//    }
//
//    @Override
//    public boolean csGetAutoBackupIsAvailable(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetAutoBackupIsAvailable++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsGetAutoBackupIsAvailableTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csGetAutoBackupIsAvailable exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetAutoBackupIsAvailable--");
//
//        return ret;
//    }
//
//    @Override
//    public boolean csSetAutoBackupAccount(BluetoothDevice device, String name) {
//        Log.d(TAG, "[CS] csSetAutoBackupAccount++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsSetAutoBackupAccountTask(mCsBleTransceiver, mMessenger, mExecutor, device, name);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetAutoBackupAccount exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetAutoBackupAccount--");
//
//        return ret;
//    }
//
//    @Override
//    public boolean csGetAutoBackupAccount(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetAutoBackupAccount++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsGetAutoBackupAccountTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csGetAutoBackupAccount exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetAutoBackupAccount--");
//
//        return ret;
//    }
//
//    @Override
//    public boolean csSetAutoBackupPreference(BluetoothDevice device, boolean enableBackup, boolean deleteAfterBackup, boolean backupWithoutAC) {
//        Log.d(TAG, "[CS] csSetAutoBackupPreference++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsSetAutoBackupPreferenceTask(mCsBleTransceiver, mMessenger, mExecutor, device, enableBackup, deleteAfterBackup, backupWithoutAC);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csSetAutoBackupPreference exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetAutoBackupPreference--");
//
//        return ret;
//    }
//
//    @Override
//    public boolean csGetAutoBackupPreference(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetAutoBackupPreference++");
//
//        boolean ret = false;
//
//        try {
//
//            CsConnectivityTask task = new CsGetAutoBackupPreferenceTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//
//            ret = true;
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "[CS] csGetAutoBackupPreference exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetAutoBackupPreference--");
//
//        return ret;
//    }
//
//    @Override
//    public boolean csSetFlightMode(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csSetFlightMode++");
//
//        boolean ret = false;
//        try {
//            CsConnectivityTask task = new CsFlightModeTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csSetFlightMode exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetFlightMode--");
//        return ret;
//    }
//
//    @Override
//    public boolean csTriggerFWUpdate(BluetoothDevice device,
//                    boolean update_rtos, boolean update_modem, boolean update_mcu, String firmwareVersion) {
//            Log.d(TAG, "[CS] csTriggerFWUpdate++");
//
//            boolean ret = false;
//            try {
//                    CsConnectivityTask task = new CsFirmwareUpdateTask(mCsBleTransceiver, mMessenger, mExecutor, device, update_rtos, update_modem, update_mcu, firmwareVersion);
//                    addTask(task);
//                    ret = true;
//            } catch (Exception e) {
//                    Log.d(TAG, "[CS] csTriggerFWUpdate exception: " + e);
//            }
//
//            Log.d(TAG, "[CS] csTriggerFWUpdate--");
//            return ret;
//    }
//
//    @Override
//    public boolean csSetAutoSleepTimerOffset(BluetoothDevice device, int offset_sec) {
//        Log.d(TAG, "[CS] csSetAutoSleepTimerOffset++");
//
//        boolean ret = false;
//        try {
//            CsConnectivityTask task = new CsSetAutoSleepTimerOffsetTask(mCsBleTransceiver, mMessenger, mExecutor, device, offset_sec);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csSetAutoSleepTimerOffset exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetAutoSleepTimerOffset--");
//        return ret;
//    }
//
//    @Override
//    public boolean csSetAutoBackupToken(BluetoothDevice device,BackupProviderIdIndex pidx, BackupTokenType type, String token) {
//        Log.d(TAG, "[CS] csSetAutoBackupToken++");
//
//        boolean ret = false;
//        try {
//            CsConnectivityTask task = new CsSetAutoBackupProviderTokenTask(mCsBleTransceiver, mMessenger, mExecutor, device, pidx, type, token);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csSetAutoBackupToken exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetAutoBackupToken--");
//        return ret;
//    }
//
//    @Override
//    public boolean csSetBroadcastSetting(BluetoothDevice device, BroadcastSetting setting) {
//        Log.d(TAG, "[CS] csSetBroadcastSetting++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsSetBroadcastSettingTask(mCsBleTransceiver, mMessenger, mExecutor, device, setting);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csSetBroadcastSetting exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetBroadcastSetting--");
//        return ret;
//    }
//
//    @Override
//    public boolean csGetBroadcastSetting(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetBroadcastSetting++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsGetBroadcastSettingTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csGetBroadcastSetting exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetBroadcastSetting--");
//        return ret;
//    }
//
//    @Override
//    public boolean csSetBroadcastPlatform(BluetoothDevice device, BroadcastPlatform platform, BroadcastTokenType tokenType, String token) {
//        Log.d(TAG, "[CS] csSetBroadcastPlatform++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsSetBroadcastPlatformTask(mCsBleTransceiver, mMessenger, mExecutor, device, platform, tokenType, token);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csSetBroadcastPlatform exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetBroadcastPlatform--");
//        return ret;
//    }
//
//    @Override
//    public boolean csSetBroadcastInvitationList(BluetoothDevice device, List<String> invitationList) {
//        Log.d(TAG, "[CS] csSetBroadcastInvitationList++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsSetBroadcastInvitationListTask(mCsBleTransceiver, mMessenger, mExecutor, device, invitationList);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csSetBroadcastInvitationList exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetBroadcastInvitationList--");
//        return ret;
//    }
//
//    @Override
//    public boolean csSetBroadcastPrivacy(BluetoothDevice device, BroadcastPrivacy privacy) {
//        Log.d(TAG, "[CS] csSetBroadcastPrivacy++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsSetBroadcastPrivacyTask(mCsBleTransceiver, mMessenger, mExecutor, device, privacy);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csSetBroadcastPrivacy exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetBroadcastPrivacy--");
//        return ret;
//    }
//
//    @Override
//    public boolean csGetBroadcastStatus(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetBroadcastStatus++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsGetBroadcastStatusTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csGetBroadcastStatus exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetBroadcastStatus--");
//        return ret;
//    }
//
//    @Override
//    public boolean csGetBroadcastInvitationList(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetBroadcastInvitationList++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsGetBroadcastInvitationListTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csGetBroadcastInvitationList exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetBroadcastInvitationList--");
//        return ret;
//    }
//
//    @Override
//    public boolean csGetBroadcastPrivacy(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetBroadcastPrivacy++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsGetBroadcastPrivacyTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csGetBroadcastPrivacy exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetBroadcastPrivacy--");
//        return ret;
//    }
//
//    @Override
//    public boolean csGetBroadcastPlatform(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetBroadcastPlatform++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsGetBroadcastPlatformTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csGetBroadcastPlatform exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetBroadcastPlatform--");
//        return ret;
//    }
//
//    @Override
//    public boolean csGetBroadcastVideoUrl(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetBroadcastVideoUrl++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsGetBroadcastVideoUrlTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csGetBroadcastVideoUrl exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetBroadcastVideoUrl--");
//        return ret;
//    }
//
//    @Override
//    public boolean csGetBroadcastErrorList(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetBroadcastErrorList++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsGetBroadcastErrorListTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csGetBroadcastErrorList exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetBroadcastErrorList--");
//        return ret;
//    }
//
//    @Override
//    public boolean csSetBroadcastUserName(BluetoothDevice device, String userName) {
//        Log.d(TAG, "[CS] csSetBroadcastUserName++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsSetBroadcastUserNameTask(mCsBleTransceiver, mMessenger, mExecutor, device, userName);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csSetBroadcastUserName exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetBroadcastUserName--");
//        return ret;
//    }
//
//    @Override
//    public boolean csSetBroadcastSMSContent(BluetoothDevice device, String smsContent) {
//        Log.d(TAG, "[CS] csSetBroadcastSMSContent++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsSetBroadcastSMSContentTask(mCsBleTransceiver, mMessenger, mExecutor, device, smsContent);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csSetBroadcastSMSContent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetBroadcastSMSContent--");
//        return ret;
//    }
//
//    @Override
//    public boolean csGetBroadcastUserName(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetBroadcastUserName++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsGetBroadcastUserNameTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csGetBroadcastUserName exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetBroadcastUserName--");
//        return ret;
//    }
//
//    @Override
//    public boolean csGetBroadcastSMSContent(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetBroadcastSMSContent++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsGetBroadcastSMSContentTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csGetBroadcastSMSContent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetBroadcastSMSContent--");
//        return ret;
//    }
//
//    @Override
//    public boolean csSetGeneralPurposeCommandLTNotify(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csSetGeneralPurposeCommandLTNotify++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsGeneralPurposeCommandNotifyTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsGeneralPurposeCommandNotifyTask.ACTION_SET_LTEVENT);
//            addTask(task);
//
//            registerLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.GENERAL_PURPOSE_NOTIFY_EVENT);
//
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csSetGeneralPurposeCommandLTNotify exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetGeneralPurposeCommandLTNotify--");
//        return ret;
//    }
//
//    @Override
//    public boolean csClrGeneralPurposeCommandLTNotify(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csClrGeneralPurposeCommandLTNotify++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsGeneralPurposeCommandNotifyTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsGeneralPurposeCommandNotifyTask.ACTION_CLR_LTEVENT);
//            addTask(task);
//
//            unregisterLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.GENERAL_PURPOSE_NOTIFY_EVENT);
//
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csClrGeneralPurposeCommandLTNotify exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csClrGeneralPurposeCommandLTNotify--");
//        return ret;
//    }
//
//    @Override
//    public boolean csGetLTECampingStatus(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetLTECampingStatus++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsLTECampingStatusTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsLTECampingStatusTask.ACTION_GET);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csGetLTECampingStatus exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetLTECampingStatus--");
//        return ret;
//    }
//
//    @Override
//    public boolean csSetLTECampingStatusLTEvent(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csSetLTECampingStatusLTEvent++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsLTECampingStatusTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsLTECampingStatusTask.ACTION_SET_LTEVENT);
//            addTask(task);
//
//            registerLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.LTE_CAMPING_STATUS_REQUEST_EVENT);
//
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csSetLTECampingStatusLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csSetLTECampingStatusLTEvent--");
//        return ret;
//    }
//
//    @Override
//    public boolean csClrLTECampingStatusLTEvent(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csClrLTECampingStatusLTEvent++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsLTECampingStatusTask(mCsBleTransceiver, mMessenger, mExecutor, device, CsLTECampingStatusTask.ACTION_CLR_LTEVENT);
//            addTask(task);
//
//            unregisterLTEvent(device, CsBleGattAttributes.CsV1CommandEnum.LTE_CAMPING_STATUS_REQUEST_EVENT);
//
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csClrLTECampingStatusLTEvent exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csClrLTECampingStatusLTEvent--");
//        return ret;
//    }
//
//    @Override
//    public boolean csGetModemStatus(BluetoothDevice device) {
//        Log.d(TAG, "[CS] csGetModemStatus++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsGetModemStatusTask(mCsBleTransceiver, mMessenger, mExecutor, device);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csGetModemStatus exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csGetModemStatus--");
//        return ret;
//    }
//
//    @Override
//    public boolean csUnlockSimPin(BluetoothDevice device, String pinCode) {
//        Log.d(TAG, "[CS] csUnlockSimPin++");
//
//        boolean ret=  false;
//        try {
//            CsConnectivityTask task = new CsUnlockSimPinTask(mCsBleTransceiver, mMessenger, mExecutor, device, pinCode);
//            addTask(task);
//            ret = true;
//        } catch (Exception e) {
//            Log.d(TAG, "[CS] csUnlockSimPin exception: " + e);
//        }
//
//        Log.d(TAG, "[CS] csUnlockSimPin--");
//        return ret;
//    }
}
