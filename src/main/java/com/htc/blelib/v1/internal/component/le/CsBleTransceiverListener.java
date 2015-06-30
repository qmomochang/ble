package com.htc.blelib.v1.internal.component.le;

import com.htc.blelib.internal.common.CommonBase.CsBleTransceiverErrorCode;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;



public class CsBleTransceiverListener implements ICsBleTransceiverListener {

	@Override
	public void onBonded(BluetoothDevice device) {

	}

	@Override
	public void onConnected(BluetoothDevice device) {

	}

	@Override
	public void onDisconnected(BluetoothDevice device) {

	}

	@Override
	public void onDisconnectedFromGattServer(BluetoothDevice device) {

	}

	@Override
	public void onCharacteristicRead(BluetoothDevice device, BluetoothGattCharacteristic characteristic) {

	}

	@Override
	public void onCharacteristicWrite(BluetoothDevice device, BluetoothGattCharacteristic characteristic) {

	}

	@Override
	public void onDescriptorWrite(BluetoothDevice device, BluetoothGattDescriptor descriptor) {

	}

	@Override
	public void onNotificationReceive(BluetoothDevice device, BluetoothGattCharacteristic characteristic) {

	}

	@Override
	public void onError(BluetoothDevice device, BluetoothGattCharacteristic characteristic, CsBleTransceiverErrorCode errorCode) {

	}
}
