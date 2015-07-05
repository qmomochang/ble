package com.htc.blelib.interfaces;

import com.htc.blelib.internal.common.CommonBase;

// define
//  1. error codes
//  2. results
//  3. events
//  4. status

public interface ICsConnectivityServiceBase {

    public static final int ERROR_SUCCESS                   = CommonBase.ERROR_SUCCESS;
    public static final int ERROR_BOOT_UP_CS                = CommonBase.ERROR_BOOT_UP_CS;
    public static final int ERROR_FAIL                      = CommonBase.ERROR_FAIL;
    public static final int ERROR_GATT_READ                 = CommonBase.ERROR_GATT_READ;
    public static final int ERROR_GATT_WRITE                = CommonBase.ERROR_GATT_WRITE;
    public static final int ERROR_GATT_SET_NOTIFICATION     = CommonBase.ERROR_GATT_SET_NOTIFICATION;
    public static final int ERROR_GATT_RECEIVE_NOTIFICATION = CommonBase.ERROR_GATT_RECEIVE_NOTIFICATION;
    public static final int ERROR_CONN_FAILURE              = CommonBase.ERROR_CONN_FAILURE;

    public enum Result {

        RESULT_SUCCESS,
        RESULT_FAIL,
    }

    public enum LongTermEvent {

        LTEVENT_NONE,
        LTEVENT_DISCONNECTED_FROM_GATT_SERVER,
        LTEVENT_HW_STATUS,
        LTEVENT_METADATA,
        LTEVENT_REQUEST_GPS_INFO,
        LTEVENT_CAMERA_STATUS,
        LTEVENT_CAMERA_ERROR,
        LTEVENT_HOTSPOT_CONTROL,
        LTEVENT_AUTO_BACKUP_ERROR,
        LTEVENT_AUTO_BACKUP_PROGRESS,
        LTEVENT_AUTO_BACKUP_AP_SCAN_RESULT,
        LTEVENT_AUTO_BACKUP_ERROR2,
        LTEVENT_BROADCAST_VIDEO_URL_RECEIVED,
        LTEVENT_BROADCAST_ERROR,
        LTEVENT_BROADCAST_LIVE_BEGIN,
        LTEVENT_BROADCAST_LIVE_END,
        LTEVENT_CAMERA_MODE,
        LTEVENT_SMS_RECEIVED,
        LTEVENT_LTE_CAMPING_STATUS,
    }



    public enum SwitchOnOff {

        SWITCH_OFF,
        SWITCH_ON,
    }



    public enum PlugIO {

        PLUG_IN,
        PLUG_OUT,
    }



    public enum Module {

        MODULE_NONE,
        MODULE_GPS,
    }



    public enum VerifyPasswordStatus {

        VPSTATUS_NOT_CHANGED_AND_CORRECT,
        VPSTATUS_NOT_CHANGED_AND_INCORRECT,
        VPSTATUS_CHANGED_AND_CORRECT,
        VPSTATUS_CHANGED_AND_INCORRECT,
    }



}
