package com.htc.blelib.v1.internal.component.le;

import com.htc.blelib.internal.common.CommonBase.CsBleTransceiverErrorCode;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;



public interface ICsBleTransceiverListener {

   	public void onBonded(BluetoothDevice device);

   	public void onConnected(BluetoothDevice device);
   	public void onDisconnected(BluetoothDevice device);
   	public void onDisconnectedFromGattServer(BluetoothDevice device);

   	public void onCharacteristicRead(BluetoothDevice device, BluetoothGattCharacteristic characteristic);
   	public void onCharacteristicWrite(BluetoothDevice device, BluetoothGattCharacteristic characteristic);
   	public void onDescriptorWrite(BluetoothDevice device, BluetoothGattDescriptor descriptor);
   	public void onNotificationReceive(BluetoothDevice device, BluetoothGattCharacteristic characteristic);
        
   	public void onError(BluetoothDevice device, BluetoothGattCharacteristic characteristic, CsBleTransceiverErrorCode errorCode);
}
