package com.htc.blelib.v1.internal.component.le;

import java.util.HashMap;

import android.util.Log;



public class CsBleGattAttributes {

    static public enum CsV1CommandEnum{
        CS_GENERAL_PURPOSE_REQUEST                ((byte)0x01), // GENERAL_REQUEST
        CS_DESCRIPTOR                     ((byte)0x11),

        POWER_REQUEST                  ((byte)0x11),
        POWER_STATUS_EVENT             ((byte)0x12),
//        TRIGGER_FWUPDATE_REQUEST          ((byte)0x13),
//        TRIGGER_FWUPDATE_RESULT_EVENT     ((byte)0x14),
//        LAST_FWUPDATE_RESULT_EVENT        ((byte)0x15),
        FACTORY_RESET_REQUEST             ((byte)0x16),
        FACTORY_RESET_RESULT_EVENT        ((byte)0x17),

        WIFI_CONFIG_REQUEST               ((byte)0x21),
        WIFI_SET_SSID_REQUEST             ((byte)0x22),
        WIFI_SET_PASSWORD_REQUEST         ((byte)0x23),
        WIFI_CONFIG_STATUS_EVENT          ((byte)0x26),
        WIFI_SCAN_REQUEST                 ((byte)0x27),
        WIFI_SCAN_RESULT_EVENT            ((byte)0x28),
        WIFI_ERASE_AP_CONFIG_REQUEST      ((byte)0x29),

        HWSTATUS_EVENT                    ((byte)0x31),
        CS_BLE_NAME_REQUEST               ((byte)0x32),
        SET_AUTO_SLEEP_TIMEOUT_OFFSET_REQ ((byte)0x33),

        PROFILE_GENERAL_REQUEST           ((byte)0x41),
        PROFILE_GENERAL_EVENT             ((byte)0x42),

        CLIENT_CREDENTIALS_REQUEST        ((byte)0x45),
        CLIENT_CREDENTIALS_EVENT          ((byte)0x46),

        SET_DATETIME_REQUEST              ((byte)0x51), //spec defined SET_DATETIME_REQUEST, GET_DATETIME_EVENT
        CS_VERSION_EVENT                  ((byte)0x52),
        GET_DATETIME_REQUEST              ((byte)0x53),
        DEVICEINFO_REQUEST_EVENT          ((byte)0x54),
        LATEST_SYNC_STATUS_REQUEST        ((byte)0x55),
        LATEST_SYNC_STATUS_EVENT          ((byte)0x56);


        private final byte id;
        CsV1CommandEnum(byte idx)
        {
            this.id = (byte)idx;
        }

        public byte getID()
        {
            return this.id;
        }

        public static CsV1CommandEnum findCommandID(byte id)
        {
            for (CsV1CommandEnum idselected:CsV1CommandEnum.values())
            {
                if ( idselected.id == id)
                {
                    return idselected;
                }
            }
            //Should not be here
            return null;
        }
    }
    public static String CS_V1_COMMANDTYPE1             = "0000df01-0000-1000-8000-00805f9b34fb";// short command write/notify
    public static String CS_V1_COMMANDTYPE2             = "0000df02-0000-1000-8000-00805f9b34fb";// long command write/notify
    public static String CS_V1_BATTERY_LEVEL            = "00002a19-0000-1000-8000-00805f9b34fb";
    public static String CS_V1_MFG_NAME_STRING          = "00002a29-0000-1000-8000-00805f9b34fb";
    public static String CS_V1_MODEL_NUMBER_STRING      = "00002a24-0000-1000-8000-00805f9b34fb";
    public static String CS_V1_FIRMWARE_REV_STRING      = "00002a26-0000-1000-8000-00805f9b34fb";


    public static String CS_SERVICE                     = "00005678-0000-1000-8000-00805f9b34fb";
    public static String CS_DEVICE_INFORMATION          = "0000180a-0000-1000-8000-00805f9b34fb";// CS_BLE_SDK_v0.6
    public static String CS_BATTERY_SERVICE             = "0000180f-0000-1000-8000-00805f9b34fb";// CS_BLE_SDK_v0.6
    public static String CS_DESCRIPTOR                  = "00002902-0000-1000-8000-00805f9b34fb";

    public static String CS_FW_REVISION                 = "00002a26-0000-1000-8000-00805f9b34fb";// dupe with CS_V1_FIRMWARE_REV_STRING

    public static String CS_BOOT_UP_READY               = "0000a101-0000-1000-8000-00805f9b34fb";
    public static String CS_HW_STATUS                   = "0000a102-0000-1000-8000-00805f9b34fb";
    public static String CS_NAME                        = "0000a104-0000-1000-8000-00805f9b34fb";
    public static String CS_PSW_ACTION                  = "0000a105-0000-1000-8000-00805f9b34fb";
    public static String CS_PSW_VERIFY                  = "0000a106-0000-1000-8000-00805f9b34fb";
    public static String CS_BOOT_UP_CS                  = "0000a107-0000-1000-8000-00805f9b34fb";
    public static String CS_ALL_FW_VERSION              = "0000a108-0000-1000-8000-00805f9b34fb";
    public static String CS_WIFI_SERVER_BAND            = "0000a201-0000-1000-8000-00805f9b34fb";
    public static String CS_DSC_SSID                    = "0000a202-0000-1000-8000-00805f9b34fb";
    public static String CS_DSC_PASSWORD                = "0000a203-0000-1000-8000-00805f9b34fb";
    public static String CS_DSC_WIFI_CFG                = "0000a204-0000-1000-8000-00805f9b34fb";
    public static String CS_PHONE_SSID                  = "0000a301-0000-1000-8000-00805f9b34fb";
    public static String CS_PHONE_PASSWORD              = "0000a302-0000-1000-8000-00805f9b34fb";
    public static String CS_PHONE_WIFI_CFG              = "0000a303-0000-1000-8000-00805f9b34fb";
    public static String CS_PHONE_WIFI_ERROR            = "0000a304-0000-1000-8000-00805f9b34fb";
    public static String CS_AUTO_BACKUP_ACTION          = "0000a401-0000-1000-8000-00805f9b34fb";
    public static String CS_AUTO_BACKUP_RESPONSE        = "0000a402-0000-1000-8000-00805f9b34fb";
    public static String CS_AUTO_BACKUP_SCAN_RESULT     = "0000a404-0000-1000-8000-00805f9b34fb";
    public static String CS_AUTO_BACKUP_GENERAL_RESULT  = "0000a405-0000-1000-8000-00805f9b34fb";
    public static String CS_AUTO_BACKUP_PROXY           = "0000a406-0000-1000-8000-00805f9b34fb";
    public static String CS_AUTO_BACKUP_ERASE_AP        = "0000a407-0000-1000-8000-00805f9b34fb";
    public static String CS_AUTO_BACKUP_GET_PROXY       = "0000a408-0000-1000-8000-00805f9b34fb";
    public static String CS_REQUEST_GPS_DATA            = "0000a501-0000-1000-8000-00805f9b34fb";
    public static String CS_GPS_DATA                    = "0000a502-0000-1000-8000-00805f9b34fb";
    public static String CS_DATE_TIME                   = "0000a601-0000-1000-8000-00805f9b34fb";
    public static String CS_METADATA                    = "0000a801-0000-1000-8000-00805f9b34fb";
    public static String CS_OPERATION                   = "0000a802-0000-1000-8000-00805f9b34fb";
    public static String CS_OPERATION_RESULT            = "0000a803-0000-1000-8000-00805f9b34fb";
    public static String CS_CAMERA_STATUS               = "0000a804-0000-1000-8000-00805f9b34fb";
    public static String CS_CAMERA_ERROR                = "0000a805-0000-1000-8000-00805f9b34fb";
    public static String CS_SHORT_COMMAND_NOTIFY        = "0000ae01-0000-1000-8000-00805f9b34fb";
    public static String CS_LONG_COMMAND_NOTIFY         = "0000ae02-0000-1000-8000-00805f9b34fb";
    public static String CS_BLE_RESET                   = "0000af01-0000-1000-8000-00805f9b34fb";

    public static String CS_TEST_SERVICE                = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String CS_TEST_NOTIFY                 = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String CS_TEST_DESCRIPTOR             = "00002902-0000-1000-8000-00805f9b34fb";


    private static HashMap<Byte, String> mUuidV1Map = new HashMap<Byte, String>();


    static {
        //mUuidV1Map.put(CsV1CommandEnum.GENERAL_REQUEST.getID()                  , CS_V1_COMMANDTYPE1);     //((byte)0x01),
        mUuidV1Map.put(CsV1CommandEnum.CS_GENERAL_PURPOSE_REQUEST.getID()               , CS_V1_COMMANDTYPE1);     //((byte)0x01),
        mUuidV1Map.put(CsV1CommandEnum.CS_DESCRIPTOR.getID()                    , CS_V1_COMMANDTYPE1);     //((byte)0x11),

        mUuidV1Map.put(CsV1CommandEnum.POWER_REQUEST.getID()                 , CS_V1_COMMANDTYPE1);     //((byte)0x11),
        mUuidV1Map.put(CsV1CommandEnum.POWER_STATUS_EVENT.getID()            , CS_V1_COMMANDTYPE1);     //((byte)0x12),
//        mUuidV1Map.put(CsV1CommandEnum.TRIGGER_FWUPDATE_REQUEST.getID()         , CS_V1_COMMANDTYPE1);     //((byte)0x13),
//        mUuidV1Map.put(CsV1CommandEnum.TRIGGER_FWUPDATE_RESULT_EVENT.getID()    , CS_V1_COMMANDTYPE1);     //((byte)0x14),
//        mUuidV1Map.put(CsV1CommandEnum.LAST_FWUPDATE_RESULT_EVENT.getID()       , CS_V1_COMMANDTYPE1);     //((byte)0x15),
        mUuidV1Map.put(CsV1CommandEnum.FACTORY_RESET_REQUEST.getID()      , CS_V1_COMMANDTYPE1);     //((byte)0x16),
        mUuidV1Map.put(CsV1CommandEnum.FACTORY_RESET_RESULT_EVENT.getID()       , CS_V1_COMMANDTYPE1);     //((byte)0x17),

        mUuidV1Map.put(CsV1CommandEnum.WIFI_CONFIG_REQUEST.getID()              , CS_V1_COMMANDTYPE1);     //((byte)0x21),
        mUuidV1Map.put(CsV1CommandEnum.WIFI_SET_SSID_REQUEST.getID()            , CS_V1_COMMANDTYPE2);     //((byte)0x22),
        mUuidV1Map.put(CsV1CommandEnum.WIFI_SET_PASSWORD_REQUEST.getID()        , CS_V1_COMMANDTYPE2);     //((byte)0x23),
        mUuidV1Map.put(CsV1CommandEnum.WIFI_CONFIG_STATUS_EVENT.getID()         , CS_V1_COMMANDTYPE1);     //((byte)0x26),
        mUuidV1Map.put(CsV1CommandEnum.WIFI_SCAN_REQUEST.getID()                , CS_V1_COMMANDTYPE1);     //((byte)0x27),
        mUuidV1Map.put(CsV1CommandEnum.WIFI_SCAN_RESULT_EVENT.getID()           , CS_V1_COMMANDTYPE2);     //((byte)0x28),
        mUuidV1Map.put(CsV1CommandEnum.WIFI_ERASE_AP_CONFIG_REQUEST.getID()     , CS_V1_COMMANDTYPE2);     //((byte)0x29),

        mUuidV1Map.put(CsV1CommandEnum.HWSTATUS_EVENT.getID()                   , CS_V1_COMMANDTYPE1);     //((byte)0x31),
        mUuidV1Map.put(CsV1CommandEnum.CS_BLE_NAME_REQUEST.getID()              , CS_V1_COMMANDTYPE1);     //((byte)0x32),
        mUuidV1Map.put(CsV1CommandEnum.SET_AUTO_SLEEP_TIMEOUT_OFFSET_REQ.getID(), CS_V1_COMMANDTYPE1);     //((byte)0x33),

        mUuidV1Map.put(CsV1CommandEnum.PROFILE_GENERAL_REQUEST.getID()          , CS_V1_COMMANDTYPE2);     //((byte)0x41),
        mUuidV1Map.put(CsV1CommandEnum.PROFILE_GENERAL_EVENT.getID()            , CS_V1_COMMANDTYPE1);     //((byte)0x42),
        mUuidV1Map.put(CsV1CommandEnum.CLIENT_CREDENTIALS_REQUEST.getID()       , CS_V1_COMMANDTYPE2);     //((byte)0x45),
        mUuidV1Map.put(CsV1CommandEnum.CLIENT_CREDENTIALS_EVENT.getID()         , CS_V1_COMMANDTYPE2);     //((byte)0x46),

        mUuidV1Map.put(CsV1CommandEnum.SET_DATETIME_REQUEST.getID()             , CS_V1_COMMANDTYPE1);     //((byte)0x51),
        mUuidV1Map.put(CsV1CommandEnum.CS_VERSION_EVENT.getID()                 , CS_V1_COMMANDTYPE1);     //((byte)0x52),
        mUuidV1Map.put(CsV1CommandEnum.GET_DATETIME_REQUEST.getID()             , CS_V1_COMMANDTYPE1);     //((byte)0x53),
        mUuidV1Map.put(CsV1CommandEnum.DEVICEINFO_REQUEST_EVENT.getID()         , CS_V1_COMMANDTYPE1);     //((byte)0x54),

    }


    public static String getUuid(CsV1CommandEnum id){
        String result = mUuidV1Map.get((byte)id.getID());

        if (result == null)
        {
            //do some error handle here
            Log.d("CsBleGattAttributes", "[CS] getUuid cannot get UUID, uuidx:" + (byte)id.getID());
        }
        return result;
    }


    public static boolean isLongFormat(CsV1CommandEnum id) {

        boolean ret = false;
        String result = mUuidV1Map.get(id.getID());

        if ( result != null && result.compareTo(CS_V1_COMMANDTYPE2) == 0)
        {
            ret = true;
        }

        return ret;
    }
}
