package com.htc.blelib.internal.common;

import java.util.ArrayList;

import com.htc.blelib.interfaces.ICsConnectivityDevice.CsStateBle;
import com.htc.blelib.interfaces.ICsConnectivityDevice.CsVersion;
import com.htc.blelib.v2.internal.component.le.CsBleScanner;

import android.bluetooth.BluetoothDevice;
import android.util.Log;



public class CsConnectivityDeviceGroup {

	private final static String TAG = "CsConnectivityDeviceGroup";

	private static Object accessLock = new Object();

	private static CsConnectivityDeviceGroup sInstance;

    private ArrayList<CsConnectivityDevice> mCsConnectivityDeviceList;


    public static CsConnectivityDeviceGroup getInstance() {
    	CsConnectivityDeviceGroup instance = sInstance;
		if (instance == null) {
			synchronized (accessLock) {
				instance = sInstance;
				if (instance == null) {
					instance = sInstance = new CsConnectivityDeviceGroup();
				}
			}
		}
		return instance;
    }



    private CsConnectivityDeviceGroup() {

    	mCsConnectivityDeviceList = new ArrayList<CsConnectivityDevice>();
    }



    public boolean addDevice(BluetoothDevice device, byte[] scanRecord) {

    	boolean ret = false;

    	if (device != null) {

			boolean isCs1Device = checkCs1DeviceKey(scanRecord);
			boolean isCs2Device = checkCs2DeviceKey(scanRecord);

    		if ((isCs1Device) ||
				(isCs2Device) ||
    			((device.getName() != null) && (device.getName().contains("hTC CS")))) {

    			CsVersion csVersion = isCs2Device ? CsVersion.CS2 : (isCs1Device ? CsVersion.CS1 : CsVersion.UNKNOWN);

    	    	for (int cnt = 0; cnt < getCount(); cnt++) {

    	    		if (getDeviceList().get(cnt).getBluetoothDevice().equals(device)) {

    	    			CsConnectivityDevice csTempDevice = getDeviceList().get(cnt);

    	    			csTempDevice.setVersion(csVersion);

    	    			if (csTempDevice.getCsStateBle() == CsStateBle.CSSTATE_BLE_CONNECTED) {

    	    				csTempDevice.setCsStateBle(CsStateBle.CSSTATE_BLE_DISCONNECTED);

    	    				Log.d(TAG, "[CS] addDevice just disconnected device = " + device);

    	    				return true;

    	    			} else {

    	    				Log.d(TAG, "[CS] addDevice duplicated device = " + device);

    	    				return false;
    	    			}
    	    		}
    	    	}

    	    	CsConnectivityDevice csDevice = new CsConnectivityDevice(device);
    	    	csDevice.setVersion(csVersion);
    	    	getDeviceList().add(csDevice);

    	    	ret = true;

    		} else {

    			Log.d(TAG, "[CS] addDevice not matched device = " + device);
    		}
    	}

    	return ret;
    }



    public boolean addDevice(BluetoothDevice device) {

    	boolean ret = false;

    	if (device != null) {

	    	for (int cnt = 0; cnt < getCount(); cnt++) {

	    		if (getDeviceList().get(cnt).getBluetoothDevice().equals(device)) {

	    			return false;
	    		}
	    	}

	    	CsConnectivityDevice csDevice = new CsConnectivityDevice(device);
	    	getDeviceList().add(csDevice);

	    	ret = true;
    	}

    	return ret;
    }



    public CsConnectivityDevice getDevice(BluetoothDevice device) {

    	for (int cnt = 0; cnt < getCount(); cnt++) {

    		CsConnectivityDevice csDevice = getDeviceList().get(cnt);

    		if (csDevice.getBluetoothDevice().equals(device)) {

    			return csDevice;
    		}
    	}

    	return null;
    }



    public void removeDevice(CsConnectivityDevice csDevice) {

    	if (csDevice != null) {

    		getDeviceList().remove(csDevice);
    	}
    }



    public ArrayList<CsConnectivityDevice> getDeviceList() {

    	return mCsConnectivityDeviceList;
    }



    public void clear() {

    	mCsConnectivityDeviceList.clear();
    }



    public int getCount() {

    	return mCsConnectivityDeviceList.size();
    }



    private boolean checkCs1DeviceKey(byte[] scanRecord) {

    	boolean ret = false;

		if ((scanRecord != null) && (scanRecord.length > 7)) {

    		String key = String.format("%02x%02x", scanRecord[6], scanRecord[5]);

    		if (key.equals("a000")) {

    			ret = true;
    		}
		}

		return ret;
    }



    private boolean checkCs2DeviceKey(byte[] scanRecord) {

    	boolean ret = false;

		if (scanRecord != null) {
			// scan record is composed of several sections
			// section format:
			// 1 byte: length
			// 1 byte: id
			// N bytes: section data (N == length - 1)
			byte sectionId;
			byte sectionLength;
			int i = 0;
			while (i < scanRecord.length) {
				sectionLength = scanRecord[i];
				if (sectionLength <= 0) { // end of scan record
					break;
				}

				sectionId = scanRecord[i + 1];
				if (sectionId == (byte) 0xFF) { // manufacturer specific data
					if (scanRecord[i + 2] == (byte) 0x0F &&
						scanRecord[i + 3] == (byte) 0x00 &&
						scanRecord[i + 4] == (byte) 0xCF &&
						scanRecord[i + 5] == (byte) 0x00) {
						ret = true;
					}
					break;
				}

				i += (sectionLength + 1);
			}
		}
		return ret;
    }
}
