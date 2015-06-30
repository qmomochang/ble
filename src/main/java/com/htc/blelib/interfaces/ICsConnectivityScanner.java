package com.htc.blelib.interfaces;

import com.htc.blelib.v1.interfaces.ICsConnectivityService;



public interface ICsConnectivityScanner {

	public enum ScanState {

		SCAN_STATE_NONE,
		SCAN_STATE_STANDBY,
		SCAN_STATE_SCANNING,
	}

	public enum ScanResult {

		SCAN_RESULT_HIT,
		SCAN_RESULT_HIT_CONNECTED,
		SCAN_RESULT_COMPLETE,
		SCAN_RESULT_ERROR,
	}

	/// Interfaces
	public boolean csOpen();
	public boolean csClose();
	public boolean csScan(int period);
	public boolean csStopScan();

	/// Callback
	public static final int CB_BLE_SCAN_RESULT 							= ICsConnectivityService.CB_BLE_SCAN_RESULT;

	/// Parameters
	public static final String PARAM_RESULT								= ICsConnectivityService.PARAM_RESULT;
	public static final String PARAM_BLUETOOTH_DEVICE					= ICsConnectivityService.PARAM_BLUETOOTH_DEVICE;
	public static final String PARAM_BLUETOOTH_DEVICE_VERSION			= "bluetooth_device_version";
}
