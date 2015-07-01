package com.htc.blelib.v1.internal.component.le;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.htc.blelib.interfaces.ICsConnectivityDevice.CsStateBle;
import com.htc.blelib.interfaces.ICsConnectivityScanner.ScanState;
import com.htc.blelib.internal.common.CsConnectivityDevice;
import com.htc.blelib.internal.common.CsConnectivityDeviceGroup;



public class CsBleScanner {

    private final static String TAG = "CsBleScanner";

    private Context mContext;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private LinkedList<CsBleScannerListener> mListeners = new LinkedList<CsBleScannerListener>();

    private CsConnectivityDeviceGroup mCsConnectivityDeviceGroup;
    private ScanState mScanState = ScanState.SCAN_STATE_NONE;



    public CsBleScanner(Context context, BluetoothManager bluetoothManager) throws Exception {

        mContext = context;
        mBluetoothManager = bluetoothManager;

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {

            throw new Exception("Unable to obtain a BluetoothAdapter.");
        }

        mCsConnectivityDeviceGroup = CsConnectivityDeviceGroup.getInstance();
        setScanState(ScanState.SCAN_STATE_STANDBY);
    }



    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            if (getCsConnectivityDeviceGroup().addDevice(device, scanRecord)) {

                CsConnectivityDevice csDevice = getCsConnectivityDeviceGroup().getDevice(device);

                Log.d(TAG, "[CS] addDevice OK: " + device.getAddress());

                final LinkedList<CsBleScannerListener> listeners;
                synchronized(mListeners){

                    listeners = new LinkedList<CsBleScannerListener>(mListeners);
                }

                for (CsBleScannerListener listener : listeners) {

                    listener.onScanHit(device, csDevice.getVersion());
                }
            }
        }
    };



    public Context getContext() {

        return mContext;
    }



    public synchronized void registerListener(CsBleScannerListener listener) {

        synchronized(mListeners) {

            mListeners.add(listener);

            Log.d(TAG, "[CS] After registerListener mListeners.size() = " + mListeners.size());
        }
    }



    public synchronized void unregisterListener(CsBleScannerListener listener) {

        synchronized(mListeners) {

            mListeners.remove(listener);

            Log.d(TAG, "[CS] After unregisterListener mListeners.size() = " + mListeners.size());
        }
    }



    public CsConnectivityDeviceGroup getCsConnectivityDeviceGroup() {

        return mCsConnectivityDeviceGroup;
    }



    synchronized public ScanState getScanState() {

        return mScanState;
    }



    synchronized private void setScanState(ScanState state) {

        mScanState = state;
    }



    private void updateCsConnectivityGroup() {

        if (mBluetoothManager != null) {

            List<BluetoothDevice> connectedDeviceList = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);

            ArrayList<CsConnectivityDevice> csDeviceList = new ArrayList<CsConnectivityDevice>();
            csDeviceList.addAll(mCsConnectivityDeviceGroup.getDeviceList());

            for (int cnt = 0; cnt < csDeviceList.size(); cnt++) {

                CsConnectivityDevice csDevice = csDeviceList.get(cnt);

                if (!connectedDeviceList.contains(csDevice.getBluetoothDevice())) {

                    mCsConnectivityDeviceGroup.removeDevice(csDevice);

                } else {

                    if (csDevice.getCsStateBle() != CsStateBle.CSSTATE_BLE_CONNECTED) {

                        Log.d(TAG, "[CS] CS BLE state is " + csDevice.getCsStateBle() + ", which is not at CSSTATE_BLE_CONNECTED before scanning");
                        csDevice.setCsStateBle(CsStateBle.CSSTATE_BLE_CONNECTED);
                    }
                }
            }

        } else {

            Log.d(TAG, "[CS] updateCsConnectivityGroup. mBluetoothManager is null.");
        }
    }



    public boolean scanStart() {

        boolean ret = false;

        Log.d(TAG, "[CS] scanStart++");
        Log.d(TAG, "[CS] scanStart getScanState() = " + getScanState());

        if (mBluetoothAdapter == null) {

            Log.d(TAG, "[CS] BluetoothAdapter not initialized.");
            return false;

        } else {

            if (!mBluetoothAdapter.isEnabled()) {

                Log.d(TAG, "[CS] Bluetooth is unavailable and please enable it.");
                return false;
            }
        }

        if (getScanState() == ScanState.SCAN_STATE_STANDBY) {

            setScanState(ScanState.SCAN_STATE_SCANNING);

            updateCsConnectivityGroup();

            /// Add exist and connected device.
            for (int cnt = 0; cnt < getCsConnectivityDeviceGroup().getCount(); cnt++) {

                CsConnectivityDevice csDevice = getCsConnectivityDeviceGroup().getDeviceList().get(cnt);

                Log.d(TAG, "[CS] add exist and connected device OK: " + csDevice.getAddress());

                final LinkedList<CsBleScannerListener> listeners;
                synchronized(mListeners){

                    listeners = new LinkedList<CsBleScannerListener>(mListeners);
                }

                for (CsBleScannerListener listener : listeners) {

                    listener.onScanHitConnected(csDevice.getBluetoothDevice(), csDevice.getVersion());
                }
            }

            if (mBluetoothAdapter.startLeScan(mLeScanCallback)) {

                ret = true;

            } else {

                setScanState(ScanState.SCAN_STATE_STANDBY);
            }

        } else {

            Log.d(TAG, "[CS] The scan state is not correct for scanStart(). getScanState = " + getScanState());
        }

        Log.d(TAG, "[CS] scanStart--");

        return ret;
    }



    public boolean scanStop() {

        boolean ret = false;

        Log.d(TAG, "[CS] scanStop++");
        Log.d(TAG, "[CS] scanStop getScanState() = " + getScanState());

        if (mBluetoothAdapter == null) {

            Log.d(TAG, "[CS] BluetoothAdapter not initialized.");
            return false;

        } else {

            if (!mBluetoothAdapter.isEnabled()) {

                Log.d(TAG, "[CS] Bluetooth is unavailable and please enable it.");
                return false;
            }
        }

        if (getScanState() == ScanState.SCAN_STATE_SCANNING) {

            mBluetoothAdapter.stopLeScan(mLeScanCallback);

            setScanState(ScanState.SCAN_STATE_STANDBY);

            ret = true;

        } else {

            Log.d(TAG, "[CS] The scan state is not correct for scanStop(). getScanState = " + getScanState());
        }

        Log.d(TAG, "[CS] scanStop--");

        return ret;
    }

}
