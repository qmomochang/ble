package com.htc.blelib.interfaces;

import android.bluetooth.BluetoothDevice;



public interface ICsConnectivityDevice {

	enum CsState {

		CSSTATE_STANDBY,
		CSSTATE_RESET_PAIRING_RECORD,
		CSSTATE_NORMAL_BLE_CONNECTING,
		CSSTATE_NORMAL_BOOT_UP_READY_CHECKING,
		CSSTATE_NORMAL_PAIRING_CHECKING,
		CSSTATE_NORMAL_PAIRING_WAITING,
		CSSTATE_NORMAL_CONNECTED,
	}



	enum BootUpReady {

		BOOTUP_UNKNOWN,
		BOOTUP_NON_READY,
		BOOTUP_READY,
	}



	public enum CsStateBle {

		CSSTATE_BLE_NONE,
		CSSTATE_BLE_CONNECTING,
		CSSTATE_BLE_CONNECTED,
		CSSTATE_BLE_DISCONNECTING,
		CSSTATE_BLE_DISCONNECTED,
	}



	public enum CsVersion {
		UNKNOWN,
		CS1,
		CS2,
	}



	public CsState getCsState();

	public String getName();
	public void setName(String name);

	public String getAddress();
	public void setAddress(String address);

	public CsVersion getVersion();
	public void setVersion(CsVersion version);

	public BluetoothDevice getBluetoothDevice();

}
