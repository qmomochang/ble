package com.htc.blelib.v1.internal.component.le;

import java.nio.charset.Charset;
import java.util.UUID;

import com.htc.blelib.v1.interfaces.ICsConnectivityService.Operation;
import com.htc.blelib.v1.interfaces.ICsConnectivityService.OperationEvent;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;




public class CsBleGattAttributeUtil {

    private final static String TAG = "CsBleGattAttributeUtil";
    private final static int CONFIG_DATA_LENGTH = 5;


    public static short byteArrayToShort(byte[] byteArray, int offset) {

        short ret = 0;
        int shortSize = Short.SIZE / 8;

        if (byteArray.length >= (offset + shortSize)) {

            for (int idx = 0; idx < shortSize; idx++) {

                ret = (short)(ret | ((short)(byteArray[idx + offset] & 0xff) << (idx * 8)));
            }
        }

        return ret;
    }



    public static int byteArrayToInt(byte[] byteArray, int offset) {

        int ret = 0;
        int intSize = Integer.SIZE / 8;

        if (byteArray.length >= (offset + intSize)) {

            for (int idx = 0; idx < intSize; idx++) {

                ret = ret | ((int)(byteArray[idx + offset] & 0xff) << (idx * 8));
            }
        }

        return ret;
    }



    public static long byteArrayToLong(byte[] byteArray, int offset) {

        long ret = 0;
        int longSize = Long.SIZE / 8;

        for (int idx = 0; idx < longSize; idx++) {

            ret = ret | ((long)(byteArray[idx + offset] & 0xff) << (idx * 8));
        }

        return ret;
    }



    public static String byteArrayToString(byte[] byteArray, int offset, int arrayLength) {

        String ret = "";
        try {
            int length = Math.min(byteArray.length - 6, arrayLength);
            ret = new String(byteArray, offset, length, Charset.defaultCharset());
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "[CS] byteArrayToString create ret fail", e);
        }

        return ret;
    }



    public static boolean compareArray(byte[] beforeArray, byte[] afterArray) {

        boolean ret = true;

        if (beforeArray.length == afterArray.length) {

            for (int cnt = 0; cnt < beforeArray.length; cnt++) {

                ///Log.d(TAG, "[CS] beforeArray[" + cnt + "] = " + beforeArray[cnt] + ", afterArray[" + cnt + "] = " + afterArray[cnt]);

                if (beforeArray[cnt] != afterArray[cnt]) {

                    Log.d(TAG, "[CS] compareArray fail because of data.");
                    return false;
                }
            }

        } else {

            Log.d(TAG, "[CS] compareArray fail because of data length.");
            return false;
        }

        return ret;
    }



    public static BluetoothGattCharacteristic convertLTNotify(BluetoothGattCharacteristic characteristic) {

        BluetoothGattCharacteristic retCharacteristic =    characteristic;
/*
        if (characteristic != null) {

            UUID uuidCurr = characteristic.getUuid();

            if (uuidCurr.toString().equals(CsBleGattAttributes.CS_SHORT_COMMAND_NOTIFY) || uuidCurr.toString().equals(CsBleGattAttributes.CS_LONG_COMMAND_NOTIFY)) {

                byte[] value = characteristic.getValue();
                Byte id = value[0];
                String uuidString = CsBleGattAttributes.lookupUuid(id);

                if ((value.length > 1) && (uuidString != null)) {

                    UUID uuidNew = UUID.fromString(uuidString);

                    retCharacteristic =    new BluetoothGattCharacteristic(uuidNew, characteristic.getProperties(), characteristic.getPermissions());

                    byte[] valueNew = new byte[value.length - 1];
                    for (int cnt = 0; cnt < (value.length - 1); cnt++) {

                        valueNew[cnt] = value[cnt + 1];
                    }

                    retCharacteristic.setValue(valueNew);
                }

            } else if (uuidCurr.toString().equals(CsBleGattAttributes.CS_PSW_VERIFY)) {

                retCharacteristic = characteristic;
            }
        }
*/
        return retCharacteristic;
    }


    public static boolean isBootUpLinuxReady(BluetoothGattCharacteristic characteristic) {
        boolean ret = false;
        byte[] value = characteristic.getValue();

        if (value[0] == CsBleGattAttributes.CsV1CommandEnum.POWER_ON_STATUS_EVENT.getID())
        {
            //value[1] == 1: linux, value[2] == 1: turn on
            if( (value[1] == 0x01) && (value[2] == 0x01))
            {
                ret = true;
            }
        }

        return ret;
    }
    //verfied
    public static boolean isBootUpReady(BluetoothGattCharacteristic characteristic) {
        boolean ret = false;
        byte[] value = characteristic.getValue();

        if (value[0] == CsBleGattAttributes.CsV1CommandEnum.POWER_ON_STATUS_EVENT.getID())
        {
            // 0x00: Standby (Only BLE)
            // 0x01: Sync (BLE, WiFi)
            // 0x02: Normal (Could be measured)
            // 0x03: Firmware updating
            // 0x04: Factory reseting (WiFi & BLE disconnect??)
            // 0x05: BLE Disconnection (Due to low battery)
            // 0xFF: unknown
            if( value[1] == 0x02 )
            {
                ret = true;
            }
            Log.v(TAG,"isBootUpReady data = " + String.format("0x%20x",value[1]));
        }
        return ret;
    }
    //verified
    public static boolean isFirmwareUpdating(BluetoothGattCharacteristic characteristic) {
          boolean ret = false;
          byte[] value = characteristic.getValue();
           if (value[0] == CsBleGattAttributes.CsV1CommandEnum.POWER_ON_STATUS_EVENT.getID())
           {
              //value[1] == 0x03: Firmware updating
               if( (value[1] == 0x03))
               {
                  ret = true;
              }
           }
        return ret;
    }

    public static byte[] getWifiConnectResult(BluetoothGattCharacteristic characteristic) {

        Log.d(TAG, "[CS] getWifiConnectResult UUID = " + characteristic.getUuid());

        byte[] result = new byte[CONFIG_DATA_LENGTH];

        if (characteristic.getValue()[0] == CsBleGattAttributes.CsV1CommandEnum.WIFI_CONFIG_STATUS_EVENT.getID()) {

            byte[] value = characteristic.getValue();

            result[0] = value[1];//Error result
            if (value.length > CONFIG_DATA_LENGTH) {
                result[1] = value[2];// IP address
                result[2] = value[3];
                result[3] = value[4];
                result[4] = value[5];
            } else {
                Log.w(TAG, "[CS] invalid config data length: " + value.length);
            }
        } else {
            Log.w(TAG, "[CS] unmatch command id");
        }

        Log.d(TAG, "[CS] Wifi connect result[0] = " + result[0]);

        return result;
    }

    public static byte[] getWifiDisconnectResult(BluetoothGattCharacteristic characteristic) {

        Log.d(TAG, "[CS] getWifiDisconnectResult UUID = " + characteristic.getUuid());

        byte[] result = new byte[1];

        if (characteristic.getValue()[0] == CsBleGattAttributes.CsV1CommandEnum.WIFI_CONFIG_STATUS_EVENT.getID()) {

            byte[] value = characteristic.getValue();

            result[0] = value[1];//Error result
        } else {
            Log.w(TAG, "[CS] unmatch command id");
        }

        Log.d(TAG, "[CS] Wifi connect result[0] = " + result[0]);

        return result;
    }



    public static String getIpAddress(BluetoothGattCharacteristic characteristic) {

        Log.d(TAG, "[CS] getIPAddress UUID = " + characteristic.getUuid());

        String str = "";

        byte[] value = characteristic.getValue();
        if (value.length > 2) {
            for (int i = 2; i < value.length; i++) {

                int val = 0;
                if ((value[i] & 0x80) == 0x80) {
                    val = 128 + (value[i] & 0x7f);
                } else {
                    val = value[i];
                }

                str = str + val;

                if (i < (value.length - 1)) {
                    str = str + ".";
                }
            }
        } else {
            Log.w(TAG, "[CS] invalid value length: " + value.length);
        }

        Log.d(TAG, "[CS] IP address = " + str);

        return str;
    }



    public static int getHwStatus_BatteryLevel(BluetoothGattCharacteristic characteristic) {

        Log.d(TAG, "[CS] getHwStatus_BatteryLevel UUID = " + characteristic.getUuid());

        int ret = -1;

        byte[] value = characteristic.getValue();

        if ((value[1] & (byte) 0x1) != 0) {
            ret = value[2];
        }

        Log.d(TAG, "[CS] Battery level = " + ret);

        return ret;
    }



    public static int getHwStatus_USBStatus(BluetoothGattCharacteristic characteristic) {

        Log.d(TAG, "[CS] getHwStatus_USBStatus UUID = " + characteristic.getUuid());

        int ret = -1;

        byte[] value = characteristic.getValue();

        if ((value[1] & (byte) 0x2) != 0) {
            ret = value[3];
        }

        Log.d(TAG, "[CS] USB status = " + ret);

        return ret;
    }



    public static int getHwStatus_AdapterStatus(BluetoothGattCharacteristic characteristic) {

        Log.d(TAG, "[CS] getHwStatus_AdapterStatus UUID = " + characteristic.getUuid());

        int ret = -1;

        byte[] value = characteristic.getValue();

        if ((value[1] & (byte) 0x4) != 0) {
            ret = value[4];
        }

        Log.d(TAG, "[CS] Adapter status = " + ret);

        return ret;
    }



    public static String getBleFWVersion(BluetoothGattCharacteristic characteristic) {

        String ret = "";

        Log.d(TAG, "[CS] getBleFWVersion UUID = " + characteristic.getUuid());

        if (characteristic.getUuid().toString().equals(CsBleGattAttributes.CS_FW_REVISION)) {

            byte[] value = characteristic.getValue();

            Log.d(TAG, "[CS] getBleFWVersion value.length = " + value.length);

            for (int i = 0; i < value.length; i++) {

                ret = ret + String.format("%c", value[i]);
            }
        }

        Log.d(TAG, "[CS] getBleFWVersion ret = " + ret);

        return ret;
    }



    public static boolean getRequestGpsInfoSwitch(BluetoothGattCharacteristic characteristic) {

        boolean ret = false;

        Log.d(TAG, "[CS] getRequestGpsInfoSwitch UUID = " + characteristic.getUuid());

        if (characteristic.getUuid().toString().equals(CsBleGattAttributes.CS_REQUEST_GPS_DATA)) {

            byte[] value = characteristic.getValue();

            if (value[0] == 0x01) {

                ret = true;
            }
        }

        return ret;
    }



    public static int getOperationResult(BluetoothGattCharacteristic characteristic, Operation operation) {

        int ret = -1;
        byte[] resultArray = characteristic.getValue();

        Log.d(TAG, "[CS] getOperationResult CommandID = " + resultArray[0]);

        //if (resultArray[0] == CsBleGattAttributes.CsV1CommandEnum.OPERATION_STATUS_EVENT.getID()) {

        //    byte type = resultArray[1];

        //    if (OperationEvent.OPEVENT_START_CAPTURING.ordinal() == (int)type && (operation.equals(Operation.OPERATION_CAPTURE_START))) {
        //        ret = 0;
        //    } else if (OperationEvent.OPEVENT_START_RECORDING.ordinal() == (int)type && (operation.equals(Operation.OPERATION_VIDEO_RECORDING_NORMAL_START) || operation.equals(Operation.OPERATION_VIDEO_RECORDING_SLOW_MOTION_START) || operation.equals(Operation.OPERATION_BROADCAST_START))) {
        //        ret = 0;
        //    } else if (OperationEvent.OPEVENT_STOP_RECORDING.ordinal() == (int)type && (operation.equals(Operation.OPERATION_VIDEO_RECORDING_NORMAL_STOP) || operation.equals(Operation.OPERATION_VIDEO_RECORDING_SLOW_MOTION_STOP) || operation.equals(Operation.OPERATION_BROADCAST_STOP))) {
        //        ret = 0;
        //    } else if (OperationEvent.OPEVENT_TIME_LAPSE_RECORDING_START.ordinal() == (int)type && (operation.equals(Operation.OPERATION_TIME_LAPS_RECORDING_START))) {
        //        ret = 0;
        //    } else if (OperationEvent.OPEVENT_TIME_LAPSE_RECORDING_STOP.ordinal() == (int)type && (operation.equals(Operation.OPERATION_TIME_LAPS_RECORDING_STOP))) {
        //        ret = 0;
        //    } else if (OperationEvent.OPEVENT_TIME_LAPSE_RECORDING_PAUSE.ordinal() == (int)type && (operation.equals(Operation.OPERATION_TIME_LAPS_RECORDING_PAUSE))) {
        //        ret = 0;
        //    } else if (OperationEvent.OPEVENT_TIME_LAPSE_RECORDING_RESUME.ordinal() == (int)type && (operation.equals(Operation.OPERATION_TIME_LAPS_RECORDING_RESUME))) {
        //        ret = 0;
        //    } else if (OperationEvent.OPEVENT_GET_DR_STATUS.ordinal() == (int)type && (operation.equals(Operation.OPERATION_GET_DR_STATUS))) {
        //        ret = 0;
        //    } else if (OperationEvent.OPEVENT_GET_FREE_SPACE.ordinal() == (int)type && (operation.equals(Operation.OPERATION_GET_FREE_SPACE))) {
        //        ret = 0;
        //    } else if (Operation.OPERATION_GET_TIME_LAPS_SETTING.ordinal() == (int)type && (operation.equals(Operation.OPERATION_GET_TIME_LAPS_SETTING))){
        //        ret = 0;
        //    }
        //}

        return ret;
    }


    public static String getCsName(BluetoothGattCharacteristic characteristic) {

        String ret = "";

        Log.d(TAG, "[CS] getCsName UUID = " + characteristic.getUuid());

        byte[] value = characteristic.getValue();
        if (value[0] == CsBleGattAttributes.CsV1CommandEnum.CS_BLE_NAME_REQUEST.getID()) {
            Log.d(TAG, "[CS] getCsName value.length = " + value.length);

            for (int i = 2; i < value.length; i++) {//The first 2 bytes are "id" and "type"

                if ((value[i] > 0) && (value[i] < 128)) {

                    ret = ret + String.format("%c", value[i]);

                } else {

                    break;
                }
            }
        }

        Log.d(TAG, "[CS] getCsName ret = " + ret);

        return ret;
    }

    public static byte [] getGeneralPurposeEvent(BluetoothGattCharacteristic characteristic) {

        Log.d(TAG, "[CS] getGeneralPurposeEvent UUID = " + characteristic.getUuid());

        byte[] ret = characteristic.getValue();
        if ( ret[0] == CsBleGattAttributes.CsV1CommandEnum.CS_GENERAL_PURPOSE_REQUEST.getID()) {
            Log.d(TAG, "[CS] getGeneralPurposeEvent = " + ret[1] +
                                                   ", " + ret[2] +
                                                   ", " + ret[3] +
                                                   ", " + ret[4] );
        }

        return ret;
    }

    //public static String getProxy(BluetoothGattCharacteristic characteristic) {

    //    String ret = null;

    //    Log.d(TAG, "[CS] getProxy UUID = " + characteristic.getUuid());

    //    if (characteristic.getUuid().toString().equals(CsBleGattAttributes.CS_AUTO_BACKUP_GET_PROXY)) {

    //        byte[] value = characteristic.getValue();

    //        Log.d(TAG, "[CS] getProxy value.length = " + value.length);

    //        if ((value.length > 2) && value[0] == 0x00) {

    //            ret = "";

    //            for (int i = 0; i < value[3]; i++) {

    //                if ((value[i + 4] > 0) && (value[i + 4] < 128)) {

    //                    ret = ret + String.format("%c", value[i + 4]);

    //                } else {

    //                    break;
    //                }
    //            }
    //        }
    //    }

    //    return ret;
    //}
    public static byte getBatteryLevel(BluetoothGattCharacteristic characteristic) {

        Log.d(TAG, "[CS] getBatteryLevel UUID = " + characteristic.getUuid());

        byte [] ret = characteristic.getValue();
        Log.d(TAG, "[CS] getBatteryLevel ret = " + ret[0] );

        return ret[0];
    }

    public static byte [] getWifiConfigEvent(BluetoothGattCharacteristic characteristic) {

        Log.d(TAG, "[CS] getWifiConfigEvent uuid = " + characteristic.getUuid());

        byte [] ret = characteristic.getValue();
        Log.d(TAG, "[CS] getWifiConfigEvent ret = " + ret );

        return ret;

    }

    public static byte [] getWifiScanResultEvent(BluetoothGattCharacteristic characteristic) {

        Log.d(TAG, "[CS] getWifiScanResultEvent uuid = " + characteristic.getUuid());

        byte [] ret = characteristic.getValue();
        Log.d(TAG, "[CS] getWifiScanResultEvent ret = " + ret );

        return ret;

    }

    public static byte [] getDatetimeResultEvent(BluetoothGattCharacteristic characteristic) {
        Log.d(TAG, "[CS] getWifiScanResultEvent uuid = " + characteristic.getUuid());

        byte [] ret = characteristic.getValue();
        Log.d(TAG, "[CS] getWifiScanResultEvent ret = " + ret );

        return ret;
    }

    public static byte [] getDeviceInfo(BluetoothGattCharacteristic characteristic) {
        Log.d(TAG, "[CS] getDeviceInfo uuid = " + characteristic.getUuid());

        byte [] ret = characteristic.getValue();
        Log.d(TAG, "[CS] getDeviceInfo ret = " + ret );

        return ret;
    }

    public static byte [] getLatestSyncStatus(BluetoothGattCharacteristic characteristic) {
        Log.d(TAG, "[CS] getLatestSyncStatus uuid = " + characteristic.getUuid());

        byte [] ret = characteristic.getValue();
        Log.d(TAG, "[CS] getLatestSyncStatus ret = " + ret );

        return ret;
    }

}
