package com.htc.blelib.v1.internal.component.le;

import java.util.HashMap;

import android.util.Log;



public class CsBleGattAttributes {

	static public enum CsV2CommandEnum{
		CS_SERVICE                      ((byte)0x00),//not defined in spec
		CS_DESCRIPTOR                   ((byte)0x01),
		CS_SHORT_COMMAND_NOTIFY         ((byte)0x05),//not defined in spec
		CS_LONG_COMMAND_NOTIFY          ((byte)0x06),//not defined in spec
		POWER_ON_REQUEST                ((byte)0x11),
		POWER_ON_STATUS_EVENT           ((byte)0x12),
		TRIGGER_FWUPDATE_REQUEST        ((byte)0x13),
		TRIGGER_FWUPDATE_RESULT_EVENT   ((byte)0x14),
		LAST_FWUPDATE_RESULT_EVENT      ((byte)0x15),
		WIFI_CONFIG_REQUEST             ((byte)0x21),
		WIFI_SET_SSID_REQUEST           ((byte)0x22),
		WIFI_SET_PASSWORD_REQUEST       ((byte)0x23),
		WIFI_SOFTAP_GETSSID_EVENT       ((byte)0x24),
		WIFI_SOFTAP_GETPASSWORD_EVENT   ((byte)0x25),
		WIFI_CONFIG_STATUS_EVENT        ((byte)0x26),
		WIFI_SCAN_REQUEST               ((byte)0x27),
		WIFI_SCAN_RESULT_EVENT          ((byte)0x28),
		WIFI_ERASE_AP_CONFIG_REQUEST    ((byte)0x29),
		//TODO
		// sim apn request notify event ((byte)0x2d)
		GET_MODEM_STATUS_REQUEST_EVENT	((byte)0x2A),
		SIM_PIN_ACTION_REQUEST			((byte)0x2B),
		SIM_PIN_ACTION_RESULT_EVENT		((byte)0x2C),
                                                      //0x2d
		LTE_CAMPING_STATUS_REQUEST_EVENT((byte)0x2E),
		HWSTATUS_EVENT                  ((byte)0x31),
		SIM_HW_STATUS_EVENT             ((byte)0x32),
		OPERATION_REQUEST               ((byte)0x41),
		OPERATION_STATUS_EVENT          ((byte)0x42),
		CAMERA_ERROR_EVENT              ((byte)0x43),
		GET_METADATA_EVENT              ((byte)0x44),
		SET_CAMERA_MODE_REQUEST         ((byte)0x45),
		SET_CAMERA_MODE_EVENT           ((byte)0x46),
		GET_CAMERA_MODE_REQUEST_EVENT   ((byte)0x47),
		SET_TIMEOUT_OFFSET_REQUEST      ((byte)0x51),
		SET_GPS_DATA_REQUEST            ((byte)0x61),
		SET_DATE_REQUEST                ((byte)0x62),
		GET_VERSION_EVENT               ((byte)0x63),
		REQUEST_GSP_DATE_EVENT          ((byte)0x64),
		SET_CS_NAME_REQUEST             ((byte)0x71),
		VERIFY_PASSWORD_REQUEST         ((byte)0x72),
		VERIFY_PASSWORD_EVENT           ((byte)0x73),
		BACKUP_SET_PROVIDER_REQUEST     ((byte)0x81),//not defined in spec
		BACKUP_GET_STATUS_EVENT         ((byte)0x82),
		BACKUP_GENERAL_EVENT            ((byte)0x83),//not defined in spec
		BACKUP_PERFERENCE_REQUEST       ((byte)0x84),
		GENERAL_PURPOSE_WRITE_REQUEST   ((byte)0x90),
		GENERAL_PURPOSE_READ_REQUEST    ((byte)0x91),
		GENERAL_PURPOSE_NOTIFY_EVENT    ((byte)0x92);

		private final byte id;
		CsV2CommandEnum(byte idx)
		{
			this.id = (byte)idx;
		}

		public byte getID()
		{
			return this.id;
		}

		public static CsV2CommandEnum findCommandID(byte id)
		{
			for (CsV2CommandEnum idselected:CsV2CommandEnum.values())
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
	public static String CS_V2_COMMANDTYPE0				= "00005678-0000-1000-8000-00805f9b34fb";
	public static String CS_V2_COMMANDTYPE1				= "0000cf01-0000-1000-8000-00805f9b34fb";
	public static String CS_V2_COMMANDTYPE2 			= "0000cf02-0000-1000-8000-00805f9b34fb";
	public static String CS_V2_COMMANDTYPE3	    		= "0000cf03-0000-1000-8000-00805f9b34fb";
	public static String CS_V2_COMMANDTYPE4	    		= "00002902-0000-1000-8000-00805f9b34fb";


	public static String CS_SERVICE 					= "0000a000-0000-1000-8000-00805f9b34fb";
	public static String CS_DEVICE_INFORMATION			= "0000180a-0000-1000-8000-00805f9b34fb";
	public static String CS_DESCRIPTOR 					= "00002902-0000-1000-8000-00805f9b34fb";
	
	public static String CS_FW_REVISION 				= "00002a26-0000-1000-8000-00805f9b34fb";
	
	public static String CS_BOOT_UP_READY 				= "0000a101-0000-1000-8000-00805f9b34fb";
	public static String CS_HW_STATUS 					= "0000a102-0000-1000-8000-00805f9b34fb";
	public static String CS_NAME 						= "0000a104-0000-1000-8000-00805f9b34fb";
	public static String CS_PSW_ACTION 					= "0000a105-0000-1000-8000-00805f9b34fb";
	public static String CS_PSW_VERIFY					= "0000a106-0000-1000-8000-00805f9b34fb";
	public static String CS_BOOT_UP_CS					= "0000a107-0000-1000-8000-00805f9b34fb";
	public static String CS_ALL_FW_VERSION				= "0000a108-0000-1000-8000-00805f9b34fb";
	public static String CS_WIFI_SERVER_BAND 			= "0000a201-0000-1000-8000-00805f9b34fb";
	public static String CS_DSC_SSID	 				= "0000a202-0000-1000-8000-00805f9b34fb";
	public static String CS_DSC_PASSWORD				= "0000a203-0000-1000-8000-00805f9b34fb";
	public static String CS_DSC_WIFI_CFG 				= "0000a204-0000-1000-8000-00805f9b34fb";
	public static String CS_PHONE_SSID	 				= "0000a301-0000-1000-8000-00805f9b34fb";
	public static String CS_PHONE_PASSWORD 				= "0000a302-0000-1000-8000-00805f9b34fb";
	public static String CS_PHONE_WIFI_CFG 				= "0000a303-0000-1000-8000-00805f9b34fb";
	public static String CS_PHONE_WIFI_ERROR 			= "0000a304-0000-1000-8000-00805f9b34fb";
	public static String CS_AUTO_BACKUP_ACTION 			= "0000a401-0000-1000-8000-00805f9b34fb";
	public static String CS_AUTO_BACKUP_RESPONSE		= "0000a402-0000-1000-8000-00805f9b34fb";
	public static String CS_AUTO_BACKUP_SCAN_RESULT		= "0000a404-0000-1000-8000-00805f9b34fb";
	public static String CS_AUTO_BACKUP_GENERAL_RESULT	= "0000a405-0000-1000-8000-00805f9b34fb";
	public static String CS_AUTO_BACKUP_PROXY			= "0000a406-0000-1000-8000-00805f9b34fb";
	public static String CS_AUTO_BACKUP_ERASE_AP		= "0000a407-0000-1000-8000-00805f9b34fb";
	public static String CS_AUTO_BACKUP_GET_PROXY		= "0000a408-0000-1000-8000-00805f9b34fb";
	public static String CS_REQUEST_GPS_DATA			= "0000a501-0000-1000-8000-00805f9b34fb";
	public static String CS_GPS_DATA 					= "0000a502-0000-1000-8000-00805f9b34fb";
	public static String CS_DATE_TIME 					= "0000a601-0000-1000-8000-00805f9b34fb";
	public static String CS_METADATA					= "0000a801-0000-1000-8000-00805f9b34fb";
	public static String CS_OPERATION					= "0000a802-0000-1000-8000-00805f9b34fb";
	public static String CS_OPERATION_RESULT			= "0000a803-0000-1000-8000-00805f9b34fb";
	public static String CS_CAMERA_STATUS				= "0000a804-0000-1000-8000-00805f9b34fb";
	public static String CS_CAMERA_ERROR				= "0000a805-0000-1000-8000-00805f9b34fb";
	public static String CS_SHORT_COMMAND_NOTIFY		= "0000ae01-0000-1000-8000-00805f9b34fb";
	public static String CS_LONG_COMMAND_NOTIFY			= "0000ae02-0000-1000-8000-00805f9b34fb";
	public static String CS_BLE_RESET	 				= "0000af01-0000-1000-8000-00805f9b34fb";

	public static String CS_TEST_SERVICE 				= "0000ffe0-0000-1000-8000-00805f9b34fb";
	public static String CS_TEST_NOTIFY 				= "0000ffe1-0000-1000-8000-00805f9b34fb";
	public static String CS_TEST_DESCRIPTOR 			= "00002902-0000-1000-8000-00805f9b34fb";

	
	private static HashMap<Byte, String> mUuidV2Map = new HashMap<Byte, String>();
	
	
    static {
    	mUuidV2Map.put(CsV2CommandEnum.CS_SERVICE.getID()                      , CS_V2_COMMANDTYPE0);
    	mUuidV2Map.put(CsV2CommandEnum.CS_DESCRIPTOR.getID()                   , CS_V2_COMMANDTYPE4);
    	mUuidV2Map.put(CsV2CommandEnum.CS_SHORT_COMMAND_NOTIFY.getID()         , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.CS_LONG_COMMAND_NOTIFY.getID()          , CS_V2_COMMANDTYPE2);
    	mUuidV2Map.put(CsV2CommandEnum.POWER_ON_REQUEST.getID()                , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.POWER_ON_STATUS_EVENT.getID()           , CS_V2_COMMANDTYPE1);
	   	mUuidV2Map.put(CsV2CommandEnum.TRIGGER_FWUPDATE_REQUEST.getID()        , CS_V2_COMMANDTYPE1);
       	mUuidV2Map.put(CsV2CommandEnum.TRIGGER_FWUPDATE_RESULT_EVENT.getID()   , CS_V2_COMMANDTYPE1);
       	mUuidV2Map.put(CsV2CommandEnum.LAST_FWUPDATE_RESULT_EVENT.getID()      , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.WIFI_CONFIG_REQUEST.getID()             , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.WIFI_SET_SSID_REQUEST.getID()           , CS_V2_COMMANDTYPE2);
    	mUuidV2Map.put(CsV2CommandEnum.WIFI_SET_PASSWORD_REQUEST.getID()       , CS_V2_COMMANDTYPE2);
    	mUuidV2Map.put(CsV2CommandEnum.WIFI_SOFTAP_GETSSID_EVENT.getID()       , CS_V2_COMMANDTYPE2);
    	mUuidV2Map.put(CsV2CommandEnum.WIFI_SOFTAP_GETPASSWORD_EVENT.getID()   , CS_V2_COMMANDTYPE2);
    	mUuidV2Map.put(CsV2CommandEnum.WIFI_CONFIG_STATUS_EVENT.getID()        , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.WIFI_SCAN_REQUEST.getID()               , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.WIFI_SCAN_RESULT_EVENT.getID()          , CS_V2_COMMANDTYPE2);
    	mUuidV2Map.put(CsV2CommandEnum.WIFI_ERASE_AP_CONFIG_REQUEST.getID()    , CS_V2_COMMANDTYPE2);
    	mUuidV2Map.put(CsV2CommandEnum.GET_MODEM_STATUS_REQUEST_EVENT.getID()  , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.SIM_PIN_ACTION_REQUEST.getID()          , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.SIM_PIN_ACTION_RESULT_EVENT.getID()     , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.LTE_CAMPING_STATUS_REQUEST_EVENT.getID(), CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.HWSTATUS_EVENT.getID()                  , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.SIM_HW_STATUS_EVENT.getID()             , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.OPERATION_REQUEST.getID()               , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.OPERATION_STATUS_EVENT.getID()          , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.CAMERA_ERROR_EVENT.getID()              , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.GET_METADATA_EVENT.getID()              , CS_V2_COMMANDTYPE2);
    	mUuidV2Map.put(CsV2CommandEnum.SET_CAMERA_MODE_REQUEST.getID()         , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.SET_CAMERA_MODE_EVENT.getID()           , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.GET_CAMERA_MODE_REQUEST_EVENT.getID()   , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.SET_TIMEOUT_OFFSET_REQUEST.getID()      , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.SET_GPS_DATA_REQUEST.getID()            , CS_V2_COMMANDTYPE2);
    	mUuidV2Map.put(CsV2CommandEnum.SET_DATE_REQUEST.getID()                , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.GET_VERSION_EVENT.getID()               , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.REQUEST_GSP_DATE_EVENT.getID()          , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.SET_CS_NAME_REQUEST.getID()             , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.VERIFY_PASSWORD_REQUEST.getID()         , CS_V2_COMMANDTYPE3);
    	mUuidV2Map.put(CsV2CommandEnum.VERIFY_PASSWORD_EVENT.getID()           , CS_V2_COMMANDTYPE3);
    	mUuidV2Map.put(CsV2CommandEnum.BACKUP_SET_PROVIDER_REQUEST.getID()     , CS_V2_COMMANDTYPE2);
    	mUuidV2Map.put(CsV2CommandEnum.BACKUP_GET_STATUS_EVENT.getID()         , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.BACKUP_GENERAL_EVENT.getID()            , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.BACKUP_PERFERENCE_REQUEST.getID()       , CS_V2_COMMANDTYPE1);
    	mUuidV2Map.put(CsV2CommandEnum.GENERAL_PURPOSE_WRITE_REQUEST.getID()   , CS_V2_COMMANDTYPE2);
    	mUuidV2Map.put(CsV2CommandEnum.GENERAL_PURPOSE_READ_REQUEST.getID()    , CS_V2_COMMANDTYPE2);
    	mUuidV2Map.put(CsV2CommandEnum.GENERAL_PURPOSE_NOTIFY_EVENT.getID()    , CS_V2_COMMANDTYPE2);
    }

    
    public static String getUuid(CsV2CommandEnum id){
    	String result = mUuidV2Map.get((byte)id.getID());

    	if (result == null)
    	{
    		//do some error handle here
    		Log.d("CsBleGattAttributes", "[CS] getUuid cannot get UUID, uuidx:" + (byte)id.getID());
    	}
    	return result;
    }

    
    public static boolean isLongFormat(CsV2CommandEnum id) {
    	
    	boolean ret = false;
    	String result = mUuidV2Map.get(id.getID());

    	if ( result != null && result.compareTo(CS_V2_COMMANDTYPE2) == 0)
    	{
    		ret = true;
    	}

    	return ret;
    }
}
