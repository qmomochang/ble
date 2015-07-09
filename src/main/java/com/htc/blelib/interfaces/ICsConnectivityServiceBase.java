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

}
