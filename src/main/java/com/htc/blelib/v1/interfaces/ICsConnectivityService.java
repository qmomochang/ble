package com.htc.blelib.v1.interfaces;

import java.util.Calendar;
import java.util.List;

import com.htc.blelib.interfaces.ICsConnectivityServiceBase;

import android.bluetooth.BluetoothDevice;

// extend interface ICsConnectivityServiceBase

public interface ICsConnectivityService extends ICsConnectivityServiceBase {

    // General request
    public static class GeneralRequest {
        public enum ACTION {
            READ                    ((byte)0x00),
            WRITE                   ((byte)0x01);

            private final byte val;
            ACTION (byte val) { this.val = val;}
            public byte getValue() {return this.val;}
        }

        public enum UNIT {
            KG                      ((byte)0x00),
            LBS                     ((byte)0x01);

            private final byte val;
            UNIT (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }

        public enum LANGUAGE {
            ENGLISH                 ((byte)0x00),
            TRANDITIONAL_CHINESE    ((byte)0x01),
            SIMPLIFIED_CHINESE      ((byte)0x02),
            JAPANESE                ((byte)0x03),
            FRENCH                  ((byte)0x04),
            SPANISH                 ((byte)0x05),
            GERMAN                  ((byte)0x06);

            private final byte val;
            LANGUAGE (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }

        public enum SOUND {
            OFF                     ((byte)0x00),
            ON                      ((byte)0x01);

            private final byte val;
            SOUND (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }

        public enum BEI{
            OFF                     ((byte)0x00),
            ON                      ((byte)0x01);

            private final byte val;
            BEI (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }
    } // END General Request

    // Power request
    public static class PowerRequest {
        public enum ACTION {
            POWER_CHECK             ((byte)0x00);

            private final byte val;
            ACTION (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }
    } // END Power Request

    // Power event
    public static class PowerEvent {

        public enum STATE {
            STANDBY                 ((byte)0x00),   // only BLE
            SYNC                    ((byte)0x01),   // BLE, Wifi
            NORMAL                  ((byte)0x02),   // Could be measured
            FIRMWARE                ((byte)0x03),
            FACTORY_RESETING        ((byte)0x04),   // Wifi & BLE disconnect
            BLE_DISCONNECTION       ((byte)0x05),   // Due to low battery
            UNKNOWN                 ((byte)0xFF);

            private final byte val;
            STATE (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }
    } // Power event

    // Firmware Update
    public static class FirmwareUpdateResuleEvent {
        public enum ERROR_CODE {
            SUCCESS_TO_START        ((byte)0x00),
            NO_UPDATE_IMAGE         ((byte)0x01),
            LOW_BATTERY             ((byte)0x02);

            private final byte val;
            ERROR_CODE (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }

        public enum UPDATE_STATUS {
            NONE_UPDATE             ((byte)0x00),
            UPDAING                 ((byte)0x01),
            UPDATED                 ((byte)0x02),
            UPDATE_ERROR            ((byte)0x03);

            private final byte val;
            UPDATE_STATUS (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }

    } // END Firmware Update

    // Factory Reset Result Event
    public static class FactoryResetResultEvent {

        public enum STATUS {
            EVENT_SUCCESS           ((byte)0x00),
            EVENT_FAIL              ((byte)0x01),
            LOW_BATTERY             ((byte)0x02);

            private final byte val;
            STATUS (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }
    } // END Factory Reset Request Event

    // Wifi Configuration Request
    public static class WifiConfigurationRequest {
        public enum TYPE {
            CONNECT                 ((byte)0x00),
            DISCONNECT              ((byte)0x01);

            private final byte val;
            TYPE (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }

        public enum COUNTRY_CODE {
            USE_DEFULT              ((byte)0x00);

            private final byte val;
            COUNTRY_CODE (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }

        public enum BAND {
            ALL_BAND                ((byte)0x00),
            BAND_2_4G               ((byte)0x01),
            BAND_5G                 ((byte)0x02);

            private final byte val;
            BAND (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }

        public enum SECURITY {
            OPEN                    ((byte)0x00),
            WEP                     ((byte)0x01),
            WPA_PSK                 ((byte)0x02),
            WPA2_PSK                ((byte)0x03);

            private final byte val;
            SECURITY (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }

        public enum CHANNEL {
            SCAN_ALL_CHANNEL        ((byte)0x00);

            private final byte val;
            CHANNEL (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }
    } // END Wifi Configuration

    // Wifi Configuration Event
    public static class WifiConfigurationEvent {
        public enum RESULT {
            WIFIMGR_ERR_SUCCESS                             ((byte)0x00),
            WIFIMGR_ERR_FAIL                                ((byte)0x01),
            WIFIMGR_ERR_WIFI_INIT_FAILED                    ((byte)0x02),
            WIFIMGR_ERR_WIFI_BUSY                           ((byte)0x03),
            WIFIMGR_ERR_BAD_SSID                            ((byte)0x04),
            WIFIMGR_ERR_BAD_SSID_LENGTH                     ((byte)0x05),
            WIFIMGR_ERR_BAD_KEY                             ((byte)0x06),
            WIFIMGR_ERR_BAD_KEY_LENGTH                      ((byte)0x07),
            WIFIMGR_ERR_BAD_KEYMGMT                         ((byte)0x08),
            WIFIMGR_ERR_CAN_NOT_FIND_AP                     ((byte)0x09),
            WIFIMGR_ERR_SEARCH_AP_TIMEOUT                   ((byte)0x0A),
            WIFIMGR_ERR_AUTH_NO_PASSWORD                    ((byte)0x0B),
            WIFIMGR_ERR_AUTH_PASSWORD_NOMATCH               ((byte)0x0C),
            WIFIMGR_ERR_AUTH_REQ_TIMEOUT                    ((byte)0x0D),
            WIFIMGR_ERR_AUTH_RSP_TIMEOUT                    ((byte)0x0E),
            WIFIMGR_ERR_ASOCIATE_REQ_TIMEOUT                ((byte)0x0F),
            WIFIMGR_ERR_ASOCIATE_RSP_TIMEOUT                ((byte)0x10),
            WIFIMGR_ERR_HANDSHAKE_REQ_TIMEOUT               ((byte)0x11),
            WIFIMGR_ERR_HANDSHAKE_REQ_CONF_TIMEOUT          ((byte)0x12),
            WIFIMGR_ERR_HANDSHAKE_RSP_TIMEOUT               ((byte)0x13),
            WIFIMGR_ERR_HANDSHAKE_RSP_CONF_TIMEOUT          ((byte)0x14),
            WIFIMGR_ERR_GET_IP_FAIL                         ((byte)0x15),
            WIFIMGR_ERR_GET_IP_TIMEOUT                      ((byte)0x16),
            WIFIMGR_ERR_START_AP_FAILED                     ((byte)0x17),
            WIFIMGR_ERR_START_DHCP_FAILED                   ((byte)0x18);

            private final byte val;
            RESULT (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }
    } // END Wifi Configuration Event

    public static class WifiScanRequest {
        public enum ACTION {
            SCAN                    ((byte)0x00),
            STOP_SCANNING           ((byte)0x01);

            private final byte val;
            ACTION (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }

        public enum SCAN_OPTION {
            SCAN_MODE_ALL           ((byte)0x00),
            RESERVED                ((byte)0x01);

            private final byte val;
            SCAN_OPTION (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }
    }

    public static class WifiScanResultEvent {
        public enum WIFI_SECURITY {
            OPEN                    ((byte)0x00),
            WEP                     ((byte)0x01),
            WPA_PSK                 ((byte)0x02),
            WPA2_PSK                ((byte)0x03);

            private final byte val;
            WIFI_SECURITY (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }

        public enum WIFI_AUTHORIZATION {
            UN_AUTHORIZATION        ((byte)0x00),
            AUTHORIZATION           ((byte)0x01);

            private final byte val;
            WIFI_AUTHORIZATION (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }
    }

    public static class EraseWifiApConfigRequest {
        public enum WIFI_SECURITY {
            OPEN                    ((byte)0x00),
            WEP                     ((byte)0x01),
            WPA_PSK                 ((byte)0x02),
            WPA2_PSK                ((byte)0x03);

            private final byte val;
            WIFI_SECURITY (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }

    }

    public static class HWStatusEvent {
        public enum TYPE {
            BATTERY_LEVEL           ((byte)0x00),
            RESERVED                ((byte)0x01);
            private final byte val;
            TYPE (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }

        public enum MCUBatteryLevel {
            MCU_BATTERY_INSUFFICIENT      ((byte)1), //0%
            MCU_BATTERY_NEAR_INSUFFICIENT ((byte)2), // <= 5%
            MCU_BATTERY_LOW               ((byte)3), //25%~6%
            MCU_BATTERY_HALF              ((byte)4), //50%~26%
            MCU_BATTERY_NEAR_FULL         ((byte)5), //75%~51%
            MCU_BATTERY_FULL              ((byte)6); //100%~76%

            private final byte val;
            MCUBatteryLevel (byte val) {this.val = val;}

            public static MCUBatteryLevel findLevel(int level) {
                for (MCUBatteryLevel mcuBatteryLevel : MCUBatteryLevel.values()) {
                    if (mcuBatteryLevel.getValue() == level) {
                        return mcuBatteryLevel;
                    }
                }
                //Should not be here
                return null;
            }

            public byte getValue() {return this.val;}
        }
    }

    public static class CSBleNameRequest {
        public enum TYPE {
            GET_NAME                ((byte)1),
            SET_NAME                ((byte)2);

            private final byte val;
            TYPE (byte val) {this.val = val;}
            public byte getValue() {return this.val;}
        }
    }

    public enum Operation {

        OPERATION_NONE,
        OPERATION_CAPTURE_START,
        OPERATION_VIDEO_RECORDING_NORMAL_START,
        OPERATION_VIDEO_RECORDING_NORMAL_STOP,
        OPERATION_VIDEO_RECORDING_SLOW_MOTION_START,
        OPERATION_VIDEO_RECORDING_SLOW_MOTION_STOP,
        OPERATION_TIME_LAPS_RECORDING_START,
        OPERATION_TIME_LAPS_RECORDING_STOP,
        OPERATION_TIME_LAPS_RECORDING_PAUSE,
        OPERATION_TIME_LAPS_RECORDING_RESUME,
        OPERATION_GET_DR_STATUS,
        OPERATION_GET_FREE_SPACE,
        OPERATION_GET_TIME_LAPS_SETTING,
        OPERATION_BROADCAST_START,
        OPERATION_BROADCAST_STOP,
    }



    public enum OperationEvent {

        OPEVENT_NONE,
        OPEVENT_START_CAPTURING,
        OPEVENT_COMPLETE_CAPTURING,
        OPEVENT_START_RECORDING,
        OPEVENT_STOP_RECORDING,
        OPEVENT_COMPLETE_RECORDING,
        OPEVENT_TIME_LAPSE_RECORDING_START,
        OPEVENT_TIME_LAPSE_CAPTURE_ONE,
        OPEVENT_TIME_LAPSE_RECORDING_PAUSE,
        OPEVENT_TIME_LAPSE_RECORDING_RESUME,
        OPEVENT_TIME_LAPSE_RECORDING_STOP,
        OPEVENT_TIME_LAPSE_COMPLETE_RECORDING,
        OPEVENT_GET_TIMELAPSE_SETTING,
        OPEVENT_GET_FREE_SPACE,
        OPEVENT_GET_DR_STATUS
    }


//    /// Callback  used in callable.sendMessage()
//    public static final int CB_OPEN_RESULT                                     = 7000;
//    public static final int CB_CLOSE_RESULT                                 = 7001;
    public static final int CB_LONG_TERM_EVENT_RESULT                        = 7002;
    public static final int CB_PERFORMANCE_RESULT                            = 7003;
    public static final int CB_BLE_SCAN_RESULT                                 = 8000;
//    public static final int CB_CREATE_WIFI_P2P_GROUP_RESULT                 = 8001;
//    public static final int CB_REMOVE_WIFI_P2P_GROUP_RESULT                 = 8002;
//    public static final int CB_REMOVE_WIFI_P2P_GROUP_FORCE_RESULT             = 8003;
    public static final int CB_BLE_CONNECT_RESULT                            = 8100;
    public static final int CB_BLE_DISCONNECT_RESULT                        = 8101;
    public static final int CB_BLE_DISCONNECT_FORCE_RESULT                    = 8102;
//    public static final int CB_WIFI_CONNECT_RESULT                            = 8200;
//    public static final int CB_WIFI_DISCONNECT_RESULT                        = 8201;
    public static final int CB_SET_POWER_ONOFF_RESULT                        = 8300;
//    public static final int CB_GET_POWER_ONOFF_RESULT                        = 8301;
    public static final int CB_SET_HW_STATUS_LTEVENT_RESULT                    = 8302;
    public static final int CB_CLR_HW_STATUS_LTEVENT_RESULT                    = 8303;
    public static final int CB_GET_HW_STATUS_RESULT                            = 8304;
//    public static final int CB_SET_FLIGHT_MODE_RESULT                        = 8305;
//    public static final int CB_SET_AUTOSLEEP_TIMER_OFFSET_RESULT            = 8306;
//    public static final int CB_TRIGGER_FWUPDATE_RESULT                        = 8307;
    public static final int CB_FWUPDATE_RESULT                                = 8308;
//    public static final int CB_SET_SIM_HW_STATUS_LTEVENT_RESULT                = 8309;
//    public static final int CB_CLR_SIM_HW_STATUS_LTEVENT_RESULT                = 8310;
//    public static final int CB_GET_SIM_HW_STATUS_RESULT                        = 8311;
//    public static final int CB_SET_OPERATION_LTEVENT_RESULT                    = 8400;
//    public static final int CB_CLR_OPERATION_LTEVENT_RESULT                    = 8401;
//    public static final int CB_SET_OPERATION_RESULT                            = 8402;
//    public static final int CB_SET_CAMERA_MODE_LTEVENT_RESULT                = 8403;
//    public static final int CB_CLR_CAMERA_MODE_LTEVENT_RESULT                = 8404;
//    public static final int CB_SET_CAMERA_MODE_RESULT                        = 8405;
//    public static final int CB_GET_CAMERA_MODE_RESULT                        = 8406;
//    public static final int CB_SET_DATE_TIME_RESULT                            = 8500;
    public static final int CB_SET_NAME_RESULT                                = 8501;
    public static final int CB_GET_NAME_RESULT                                = 8502;
//    public static final int CB_SET_GPS_INFO_LTEVENT_RESULT                    = 8503;
//    public static final int CB_CLR_GPS_INFO_LTEVENT_RESULT                    = 8504;
//    public static final int CB_SET_GPS_INFO_RESULT                            = 8505;
//    public static final int CB_GET_BLE_FW_VERSION_RESULT                    = 8600;
//    public static final int CB_GET_ALL_FW_VERSION_RESULT                    = 8601;
//    public static final int CB_VERIFY_PASSWORD_RESULT                        = 8700;
//    public static final int CB_CHANGE_PASSWORD_RESULT                        = 8701;
//    public static final int CB_SET_AUTO_BACKUP_LTEVENT_RESULT                = 8800;
//    public static final int CB_CLR_AUTO_BACKUP_LTEVENT_RESULT                = 8801;
//    public static final int CB_SET_AUTO_BACKUP_AP_RESULT                    = 8802;
//    public static final int CB_CLR_AUTO_BACKUP_AP_RESULT                    = 8803;
//    public static final int CB_SET_AUTO_BACKUP_PROXY_RESULT                    = 8804;
//    public static final int CB_GET_AUTO_BACKUP_PROXY_RESULT                    = 8805;
//    public static final int CB_SET_AUTO_BACKUP_AP_SCAN_START_RESULT            = 8806;
//    public static final int CB_SET_AUTO_BACKUP_AP_SCAN_STOP_RESULT            = 8807;
//    public static final int CB_GET_AUTO_BACKUP_STATUS_RESULT                = 8808;
//    public static final int CB_SET_AUTO_BACKUP_TOKEN_RESULT                    = 8809;
//    public static final int CB_SET_AUTO_BACKUP_PREFERENCE_RESULT            = 8810;
//    public static final int CB_GET_AUTO_BACKUP_PREFERENCE_RESULT            = 8811;
//    public static final int CB_GET_AUTO_BACKUP_IS_AVAILABLE_RESULT            = 8812;
//    public static final int CB_SET_AUTO_BACKUP_ACCOUNT_RESULT                = 8813;
//    public static final int CB_GET_AUTO_BACKUP_ACCOUNT_RESULT                = 8814;
//    public static final int CB_SET_METADATA_LTEVENT_RESULT                    = 8900;
//    public static final int CB_CLR_METADATA_LTEVENT_RESULT                    = 8901;
//    public static final int CB_SET_CAMERA_ERROR_LTEVENT_RESULT                = 9000;
//    public static final int CB_CLR_CAMERA_ERROR_LTEVENT_RESULT                = 9001;
//    public static final int CB_SET_LTNOTIFY_RESULT                            = 9100;
//    public static final int CB_CLR_LTNOTIFY_RESULT                            = 9101;
//    public static final int CB_SET_BROADCAST_SETTING_RESULT                    = 9200;
//    public static final int CB_GET_BROADCAST_SETTING_RESULT                    = 9201;
//    public static final int CB_SET_BROADCAST_PLATFORM_RESULT                = 9202;
//    public static final int CB_SET_BROADCAST_INVITATION_LIST_RESULT            = 9203;
//    public static final int CB_SET_BROADCAST_PRIVACY_RESULT                    = 9204;
//    public static final int CB_GET_BROADCAST_STATUS_RESULT                    = 9205;
//    public static final int CB_GET_BROADCAST_INVITATION_LIST_RESULT            = 9206;
//    public static final int CB_GET_BROADCAST_PRIVACY_RESULT                    = 9207;
//    public static final int CB_GET_BROADCAST_PLATFORM_RESULT                = 9208;
//    public static final int CB_GET_BROADCAST_VIDEO_URL_RESULT                = 9209;
//    public static final int CB_GET_BROADCAST_ERROR_LIST_RESULT                = 9210;
//    public static final int CB_SET_BROADCAST_USER_NAME_RESULT                = 9211;
//    public static final int CB_SET_BROADCAST_SMS_CONTENT_RESULT                = 9212;
//    public static final int CB_GET_BROADCAST_USER_NAME_RESULT                = 9213;
//    public static final int CB_GET_BROADCAST_SMS_CONTENT_RESULT                = 9214;
//    public static final int CB_SET_GENERAL_PURPOSE_COMMAND_LTNOTIFY_RESULT    = 9300;
//    public static final int CB_CLR_GENERAL_PURPOSE_COMMAND_LTNOTIFY_RESULT    = 9301;
//    public static final int CB_GET_LTE_CAMPING_STATUS_RESULT                = 9400;
//    public static final int CB_SET_LTE_CAMPING_STATUS_LTEVENT_RESULT        = 9401;
//    public static final int CB_CLR_LTE_CAMPING_STATUS_LTEVENT_RESULT        = 9402;
//    public static final int CB_GET_MODEM_STATUS_RESULT                        = 9500;
//    public static final int CB_UNLOCK_SIM_PIN_RESULT                        = 9501;

    public static final int CB_GET_POWER_LEVEL_RESULT                        = 10000;
    public static final int CB_GET_GENERAL_PURPOSE_RESULT                    = 10001;

//    /// Parameters
    public static final String PARAM_RESULT                                = "result";
//    public static final String PARAM_RESULT_SOFTAP                        = "result_softap";
    public static final String PARAM_LONG_TERM_EVENT                    = "long_term_event";
    public static final String PARAM_TASK_NAME                            = "task_name";
    public static final String PARAM_TIME_COST_MS                        = "time_cost_ms";
    public static final String PARAM_BLUETOOTH_DEVICE                    = "bluetooth_device";
//    public static final String PARAM_WIFI_ERROR_CODE                    = "wifi_error_code";
//    public static final String PARAM_DEVICE_IP_ADDRESS                    = "device_ip_address";
//    public static final String PARAM_MODULE                                = "module";
//    public static final String PARAM_MODULE_POWER_STATE                    = "module_power_state";
    public static final String PARAM_CS_NAME                            = "cs_name";
//    public static final String PARAM_REQUEST_GPS_INFO_SWITCH            = "request_gps_info_switch";
    public static final String PARAM_BATTERY_LEVEL                        = "battery_level";
//    public static final String PARAM_USB_STORAGE                        = "usb_storage";
//    public static final String PARAM_ADAPTER_PLUGIN                        = "adapter_plugin";
//    public static final String PARAM_SIM_HW_STATUS                        = "sim_hw_status";
    public static final String PARAM_CS_POWER                            = "cs_power";
    public static final String PARAM_CS_POWER_LEVEL                            = "cs_power_level";// spec v0.6
//    public static final String PARAM_OPERATION                            = "operation";
//    public static final String PARAM_OPERATION_ERROR_CODE                = "operation_error_code";
//    public static final String PARAM_CAMERA_MODE                        = "camera_mode";
//    public static final String PARAM_A12_FW_VERSION                        = "a12_fw_version";
//    public static final String PARAM_MOD_FW_VERSION                        = "modem_fw_version";
//    public static final String PARAM_MCU_FW_VERSION                        = "mcu_fw_version";
//    public static final String PARAM_BLE_FW_VERSION                        = "ble_fw_version";
//    public static final String PARAM_TRIGGER_FWUPDATE_RESULT            = "trigger_fw_update_result";
//    public static final String PARAM_FWUPDATE_RESULT                    = "fw_update_result";
//    public static final String PARAM_VERIFY_PASSWORD_STATUS                = "verify_password_status";
//    public static final String PARAM_AUTO_BACKUP_ERROR_TYPE                = "auto_backup_error_type";
//    public static final String PARAM_AUTO_BACKUP_ERROR_CODE                = "auto_backup_error_code";
//    public static final String PARAM_AUTO_BACKUP_PROCESS_STATUS            = "auto_backup_process_status";
//    public static final String PARAM_AUTO_BACKUP_PROVIDER_INDEX            = "auto_backup_provider_index";
//    public static final String PARAM_AUTO_BACKUP_UNBACKUP_ITEM_NUMBER    = "auto_backup_unbackup_item_number";
//    public static final String PARAM_AUTO_BACKUP_TOTAL_ITEM_NUMBER        = "auto_backup_total_item_number";
//    public static final String PARAM_AUTO_BACKUP_LAST_BACKUP_DATE_TIME    = "auto_backup_last_backup_date_time";
//    public static final String PARAM_AUTO_BACKUP_IS_ENABLE_BACKUP        = "auto_backup_is_enable_backup";
//    public static final String PARAM_AUTO_BACKUP_IS_DELETE_AFTER_BACKUP    = "auto_backup_is_delete_after_backup";
//    public static final String PARAM_AUTO_BACKUP_IS_BACKUP_WITHOUT_AC    = "auto_backup_is_backup_without_ac";
//    public static final String PARAM_AUTO_BACKUP_IS_AVAILABLE            = "auto_backup_is_available";
//    public static final String PARAM_AUTO_BACKUP_ACCOUNT                = "auto_backup_account";
//    public static final String PARAM_AUTO_BACKUP_ERROR2_MESSAGE            = "auto_backup_error2_message";
//    public static final String PARAM_BROADCAST_ERROR_CODE                = "broadcast_error_code";
//    public static final String PARAM_BROADCAST_ERROR_TIMESTAMP            = "broadcast_error_timestamp";
//    public static final String PARAM_BROADCAST_SETTING                    = "broadcast_setting";
//    public static final String PARAM_BROADCAST_STATUS                    = "broadcast_status";
//    public static final String PARAM_BROADCAST_INVITATION_LIST            = "broadcast_invitation_list";
//    public static final String PARAM_BROADCAST_PRIVACY                    = "broadcast_privacy";
//    public static final String PARAM_BROADCAST_PLATFORM                    = "broadcast_platform";
//    public static final String PARAM_BROADCAST_VIDEO_URL                = "broadcast_video_url";
//    public static final String PARAM_BROADCAST_ERROR_LIST                = "broadcast_error_list";
//    public static final String PARAM_BROADCAST_USER_NAME                = "broadcast_user_name";
//    public static final String PARAM_BROADCAST_SMS_CONTENT                = "broadcast_sms_content";
//    public static final String PARAM_FILE_ID                            = "file_id";
//    public static final String PARAM_FOLDER_NAME                        = "folder_name";
//    public static final String PARAM_FILE_NAME                            = "file_name";
//    public static final String PARAM_FILE_TYPE                            = "file_type";
//    public static final String PARAM_FILE_CREATE_TIME                    = "file_create_time";
//    public static final String PARAM_FILE_SIZE                            = "file_size";
//    public static final String PARAM_VIDEO_DURATION                        = "video_duration";
//    public static final String PARAM_DR_STATUS                            = "dr_status";
//    public static final String PARAM_DR_STATUS_COUNT                    = "dr_status_count";
//    public static final String PARAM_FREE_SPACE                            = "free_space";
//    public static final String PARAM_TOTAL_SPACE                        = "total_space";
//    public static final String PARAM_OPERATION_EVENT                    = "operation_event";
//    public static final String PARAM_READY_BIT                            = "ready_bit";
//    public static final String PARAM_IMAGE_REMAIN_COUNT                    = "image_remain_count";
//    public static final String PARAM_VIDEO_REMAIN_SECOND                = "video_remain_second";
//    public static final String PARAM_TIME_LAPSE_REMAIN_COUNT            = "time_lapse_remain_count";
//    public static final String PARAM_SLOW_MOTION_REMAIN_SECOND            = "slow_motion_remain_second";
//    public static final String PARAM_TIME_LAPSE_CURRENT_COUNT            = "time_lapse_current_count";
//    public static final String PARAM_TIME_LAPSE_TOTAL_COUNT                = "time_lapse_total_count";
//    public static final String PARAM_VIDEO_CURRENT_SECOND                = "video_current_second";
//    public static final String PARAM_CAMERA_ERROR_INDEX                    = "camera_error_index";
//    public static final String PARAM_CAMERA_ERROR_CODE                    = "camera_error_code";
//    public static final String PARAM_SWITCH_ON_OFF                        = "switch_on_off";
//    public static final String PARAM_REMAIN_FILE_COUNT                    = "remain_file_count";
//    public static final String PARAM_TOTAL_FILE_COUNT                    = "total_file_count";
    public static final String PARAM_AP_ANY_SCAN_RESULT                    = "ap_any_scan_result";
    public static final String PARAM_AP_END_OF_SCAN_LIST                = "ap_end_of_scan_list";
    public static final String PARAM_AP_INDEX_OF_SCAN_LIST                = "ap_index_of_scan_list";
    public static final String PARAM_AP_RSSI                            = "ap_rssi";
    public static final String PARAM_AP_SECURITY                        = "ap_security";
    public static final String PARAM_AP_AUTHORIZATION                    = "ap_authorization";
    public static final String PARAM_AP_SSID                            = "ap_ssid";
//    public static final String PARAM_AP_PROXY                            = "ap_proxy";
//    public static final String PARAM_AP_PORT                            = "ap_port";
//    public static final String PARAM_SMS_DATE_TIME                        = "sms_date_time";
//    public static final String PARAM_SMS_PHONE_NUMBER                    = "sms_phone_number";
//    public static final String PARAM_SMS_MESSAGE_CONTENT                = "sms_message_content";
//    public static final String PARAM_LTE_CAMPING_STATUS                    = "lte_camping_status";
//    public static final String PARAM_SIM_LOCK_TYPE                        = "sim_lock_type";
//    public static final String PARAM_SIM_PIN_RETRY_COUNT                = "sim_pin_retry_count";
//    public static final String PARAM_SIM_PUK_RETRY_COUNT                = "sim_puk_retry_count";
//    public static final String PARAM_SIM_UNLOCK_PIN_RESULT                = "sim_unlock_pin_result";

    public static final String PARAM_GENERAL_PURPOSE_UNITS                  = "general_purpose_unit";
    public static final String PARAM_GENERAL_PURPOSE_LANGUAGE               = "general_purpose_language";
    public static final String PARAM_GENERAL_PURPOSE_SOUND                  = "general_purpose_sound";
    public static final String PARAM_GENERAL_PURPOSE_BEI                    = "general_purpose_bei";


    /// Interfaces
    public boolean csBootUp(BluetoothDevice device);
    public boolean csOpen();
    public boolean csClose();

    public boolean csBleConnect(BluetoothDevice device);
    public boolean csBleDisconnect(BluetoothDevice device);
    public boolean csBleDisconnectForce(BluetoothDevice device);

//    public boolean csSetWifiAcc(BluetoothDevice device,String account);
//    public boolean csSetWifiPw(BluetoothDevice device,String password);
//
//    public boolean csSetPowerOnOff(BluetoothDevice device, Module module, SwitchOnOff onoff);
//    public boolean csGetPowerOnOff(BluetoothDevice device, Module module);
//    public boolean csTriggerFWUpdate(BluetoothDevice device, boolean update_rtos, boolean update_modem, boolean update_mcu, String firmwareVersion);
//    public boolean csSetHwStatusLTEvent(BluetoothDevice device);
//    public boolean csClrHwStatusLTEvent(BluetoothDevice device);
    public boolean csGetHwStatus(BluetoothDevice device);
//    //public boolean csGetSimHwStatus(BluetoothDevice device);
//    //public boolean csSetFlightMode(BluetoothDevice device);
//    //public boolean csSetAutoSleepTimerOffset(BluetoothDevice device, int offset_sec);
//    public boolean csSetOperationLTEvent(BluetoothDevice device);
//    public boolean csClrOperationLTEvent(BluetoothDevice device);
//    public boolean csSetOperation(BluetoothDevice device, Operation operation);
//    public boolean csSetDateTime(BluetoothDevice device, Calendar calendar);
    public boolean csSetName(BluetoothDevice device, String name);
    public boolean csGetName(BluetoothDevice device);
//    //public boolean csSetGpsInfoLTEvent(BluetoothDevice device);
//    //public boolean csClrGpsInfoLTEvent(BluetoothDevice device);
//    //public boolean csSetGpsInfo(BluetoothDevice device, Calendar calendar, double longitude, double latitude, double altitude);
//    public boolean csGetBleFWVersion(BluetoothDevice device);
//    public boolean csVerifyPassword(BluetoothDevice device, String password);
//    public boolean csChangePassword(BluetoothDevice device, String password);
//    public boolean csSetCameraModeLTEvent(BluetoothDevice device);
//    public boolean csClrCameraModeLTEvent(BluetoothDevice device);
//    public boolean csSetMetadataLTEvent(BluetoothDevice device);
//    public boolean csClrMetadataLTEvent(BluetoothDevice device);
//    //public boolean csSetCameraErrorLTEvent(BluetoothDevice device);
//    //public boolean csClrCameraErrorLTEvent(BluetoothDevice device);
//    //public boolean csSoftAPConnect(BluetoothDevice device, String passwd);
//    //public boolean csSetAutoBackupLTEvent(BluetoothDevice device);
//    //public boolean csClrAutoBackupLTEvent(BluetoothDevice device);
//    //public boolean csSetAutoBackupAP(BluetoothDevice device, String ssid, String passwd, byte security);
//    //public boolean csClrAutoBackupAP(BluetoothDevice device, byte security, String ssid);
//    public boolean csSetLTNotify(BluetoothDevice device);
//    public boolean csClrLTNotify(BluetoothDevice device);
//    //public boolean csSetAutoBackupToken(BluetoothDevice device, BackupProviderIdIndex pidx, BackupTokenType type, String token);
//    //public boolean csSetAutoBackupAPScan(BluetoothDevice device, int startORstop , int option);
//    //public boolean csSetAutoBackupProxy(BluetoothDevice device, int port, byte security, String ssid, String proxy);
//    //public boolean csGetAutoBackupProxy(BluetoothDevice device, byte security, String ssid);
//    public boolean csGetAllFwVersion(BluetoothDevice device);
//    //public boolean csGetAutoBackupStatus(BluetoothDevice device);
//    //public boolean csGetAutoBackupIsAvailable(BluetoothDevice device);
//    //public boolean csSetAutoBackupAccount(BluetoothDevice device, String name);
//    //public boolean csGetAutoBackupAccount(BluetoothDevice device);
//    //public boolean csSetAutoBackupPreference(BluetoothDevice device, boolean enableBackup, boolean deleteAfterBackup, boolean backupWithoutAC);
//    //public boolean csGetAutoBackupPreference(BluetoothDevice device);
//    //public boolean csSetBroadcastSetting(BluetoothDevice device, BroadcastSetting setting);
//    //public boolean csGetBroadcastSetting(BluetoothDevice device);
//    //public boolean csSetBroadcastPlatform(BluetoothDevice device, BroadcastPlatform platform, BroadcastTokenType tokenType, String token);
//    //public boolean csSetBroadcastInvitationList(BluetoothDevice device, List<String> invitationList);
//    //public boolean csSetBroadcastPrivacy(BluetoothDevice device, BroadcastPrivacy privacy);
//    //public boolean csGetBroadcastStatus(BluetoothDevice device);
//    //public boolean csGetBroadcastInvitationList(BluetoothDevice device);
//    //public boolean csGetBroadcastPrivacy(BluetoothDevice device);
//    //public boolean csGetBroadcastPlatform(BluetoothDevice device);
//    //public boolean csGetBroadcastVideoUrl(BluetoothDevice device);
//    //public boolean csGetBroadcastErrorList(BluetoothDevice device);
//    //public boolean csSetBroadcastUserName(BluetoothDevice device, String userName);
//    //public boolean csSetBroadcastSMSContent(BluetoothDevice device, String smsContent);
//    //public boolean csGetBroadcastUserName(BluetoothDevice device);
//    //public boolean csGetBroadcastSMSContent(BluetoothDevice device);
//    public boolean csSetGeneralPurposeCommandLTNotify(BluetoothDevice device);
//    public boolean csClrGeneralPurposeCommandLTNotify(BluetoothDevice device);
//    public boolean csGetLTECampingStatus(BluetoothDevice device);
//    public boolean csSetLTECampingStatusLTEvent(BluetoothDevice device);
//    public boolean csClrLTECampingStatusLTEvent(BluetoothDevice device);
//    public boolean csGetModemStatus(BluetoothDevice device);
//    //public boolean csUnlockSimPin(BluetoothDevice device, String pinCode);
}
