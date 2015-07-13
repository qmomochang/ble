/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.os.Messenger;
import android.os.Message;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.htc.blelib.interfaces.ICsConnectivityScanner;
import com.htc.blelib.interfaces.ICsConnectivityDevice.CsVersion;
import com.htc.blelib.interfaces.ICsConnectivityServiceBase;

import com.htc.blelib.v1.CsConnectivityService;
import com.htc.blelib.v1.interfaces.ICsConnectivityService;


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private Context mContext;

    private Messenger mMessenger;
    private Handler mHandler;

    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    CsConnectivityService m_CsConnectivityService;
    BluetoothDevice m_device;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
    };

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        mContext = this;
        final Intent intent = getIntent();
        //mDeviceName = "ww_e1411";
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        //mDeviceAddress = "D4:0B:1A:0E:14:11";
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        Bundle b = intent.getExtras();
        m_device = b.getParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE);
        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        //bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        final BluetoothManager bluetoothManager =
                        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        mHandler = new Handler() {
            @Override
                public void handleMessage(Message msg) {
                    Bundle b;
                    ICsConnectivityServiceBase.Result result;
                    String str;
                    byte value;
                    byte [] valueArr;
                    switch(msg.what){

                        case ICsConnectivityService.CB_BLE_CONNECT_RESULT:
                            b = msg.getData();
                            result = (ICsConnectivityServiceBase.Result) b.getSerializable(ICsConnectivityService.PARAM_RESULT);
                            Log.v(TAG,"[CS] handleMessage ICsConnectivityService.CB_BLE_CONNECT_RESULT: result = "+ result);
                            break;

                        case ICsConnectivityService.CB_SET_NAME_RESULT:
                            b = msg.getData();
                            result = (ICsConnectivityServiceBase.Result) b.getSerializable(ICsConnectivityService.PARAM_RESULT);
                            str = b.getString(ICsConnectivityService.PARAM_CS_NAME);

                            Log.v(TAG,"[CS] handleMessage CB_SET_NAME_RESULT r = "+result+", name = "+str);
                            break;

                        case ICsConnectivityService.CB_GET_NAME_RESULT:
                            b = msg.getData();
                            result = (ICsConnectivityServiceBase.Result) b.getSerializable(ICsConnectivityService.PARAM_RESULT);
                            str = b.getString(ICsConnectivityService.PARAM_CS_NAME);

                            Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();

                            Log.v(TAG,"[CS] handleMessage CB_GET_NAME_RESULT r = "+result+", name = "+str);
                            break;
                        case ICsConnectivityService.CB_GET_POWER_LEVEL_RESULT:
                            b = msg.getData();
                            result = (ICsConnectivityServiceBase.Result) b.getSerializable(ICsConnectivityService.PARAM_RESULT);
                            value = b.getByte(ICsConnectivityService.PARAM_BATTERY_LEVEL);

                            Log.v(TAG,"[CS] handleMessage CB_GET_POWER_LEVEL_RESULT r = "+result+", battery_level = "+value);
                            String str2 = "battery level = "+value;

                            Toast.makeText(mContext, str2, Toast.LENGTH_LONG).show();

                            break;
                        case ICsConnectivityService.CB_SET_CLIENT_CREDENTIALS_RESULT:
                            b = msg.getData();
                            result = (ICsConnectivityServiceBase.Result) b.getSerializable(ICsConnectivityService.PARAM_RESULT);
                            valueArr = b.getByteArray(ICsConnectivityService.PARAM_CLIENT_CREDENTIALS_EVENT_RESULT);
                            String str3;
                            if (valueArr != null) {
                                Log.v(TAG,"[CS] handleMessage CB_SET_CLIENT_CREDENTIALS_RESULT r = "+result+", array = "+ new String(valueArr));
                                str3 = "ret array = "+new String(valueArr);
                            } else {
                                Log.v(TAG,"[CS] handleMessage CB_SET_CLIENT_CREDENTIALS_RESULT r = "+result+", array = null");
                                str3 = "ret array = null";
                            }

                            Toast.makeText(mContext, str3, Toast.LENGTH_LONG).show();
                            break;

                        default:break;
                    }
                    super.handleMessage(msg);
                }
        };

        mMessenger = new Messenger(mHandler);

        if (m_CsConnectivityService == null) {
            m_CsConnectivityService = new CsConnectivityService(this,mMessenger);
            m_CsConnectivityService.csBleConnect(m_device);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:

                //boolean connect = mBluetoothLeService.connect(mDeviceAddress);
                //m_CsConnectivityService.csSetName(m_device,"HT543YV11113");
                byte [] clientToken =  "0000000000aaaaaaaaaa1111111111bbbbbbbbbb2222222222cccccccccc3333".getBytes();
                byte [] clientSecret =  "4444444444dddddddddd5555555555eeeeeeeeee6666666666ffffffffff7777".getBytes();

                m_CsConnectivityService.csBleSetCredentials(m_device, 0, "MikeTestToken", clientToken, clientSecret );
                Log.v("CS", "m_CsConnectivityService.csSetCredentials");
                //Log.v("CS","m_CsConnectivityService.csSetName");
                //Log.v("MC","BDA is "+mDeviceAddress.toString()+", suc = "+connect);
                return true;
            case R.id.menu_disconnect:
                //mBluetoothLeService.disconnect();
                Log.v("CS","R.id.menu_disconnect:");
                return true;
            case android.R.id.home:
                Log.v("CS","onBackPressed");
                onBackPressed();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
