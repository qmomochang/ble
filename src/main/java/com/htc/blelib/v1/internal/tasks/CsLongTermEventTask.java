package com.htc.blelib.v1.internal.tasks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.htc.blelib.interfaces.ICsConnectivityServiceBase.LongTermEvent;
import com.htc.blelib.interfaces.ICsConnectivityServiceBase.PlugIO;
import com.htc.blelib.interfaces.ICsConnectivityServiceBase.SwitchOnOff;
import com.htc.blelib.internal.common.CommonBase.CsBleTransceiverErrorCode;
import com.htc.blelib.v1.ICsConnectivityServiceListener;
import com.htc.blelib.v1.interfaces.ICsConnectivityService;
import com.htc.blelib.v1.interfaces.ICsConnectivityService.HWStatusEvent.MCUBatteryLevel;
import com.htc.blelib.v1.interfaces.ICsConnectivityService.OperationEvent;
import com.htc.blelib.v1.internal.common.CsConnectivityTask;
import com.htc.blelib.v1.internal.common.LongCommandCollector;
import com.htc.blelib.v1.internal.common.ReceiveSMSCollector;
import com.htc.blelib.v1.internal.component.le.CsBleGattAttributeUtil;
import com.htc.blelib.v1.internal.component.le.CsBleGattAttributes;
import com.htc.blelib.v1.internal.component.le.CsBleTransceiver;
import com.htc.blelib.v1.internal.component.le.CsBleTransceiverListener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;



public class CsLongTermEventTask extends CsConnectivityTask {

    private final static String TAG = "CsLongTermEventTask";

    private boolean mEnable;
    private final PriorityBlockingQueue<Notification> mNotificationQueue = new PriorityBlockingQueue<Notification>();
    private final HashMap<BluetoothDevice, ArrayList<CsBleGattAttributes.CsV1CommandEnum>> mNotificationMap = new HashMap<BluetoothDevice, ArrayList<CsBleGattAttributes.CsV1CommandEnum>>();
    private final HashMap<BluetoothDevice, ArrayList<LongCommandCollector>> mLongCommandCollectorMap = new HashMap<BluetoothDevice, ArrayList<LongCommandCollector>>();
    private final ICsConnectivityServiceListener mCsConnectivityServiceListener;
    private final ReceiveSMSCollector mReceiveSMSCollector = new ReceiveSMSCollector();



    private CsBleTransceiverListener mCsBleTransceiverListener = new CsBleTransceiverListener() {

        @Override
        public void onDisconnectedFromGattServer(BluetoothDevice device) {

            Log.d(TAG, "[CS] onDisconnectedFromGattServer device = " + device);

            Notification notification = new Notification(device, CsBleTransceiverErrorCode.ERROR_DISCONNECTED_FROM_GATT_SERVER);
            addNotification(notification);
        }



        @Override
        public void onNotificationReceive(BluetoothDevice device, BluetoothGattCharacteristic characteristic) {

            Log.d(TAG, "[CS] onNotificationReceive!!");

            BluetoothGattCharacteristic tempCharacteristic = new BluetoothGattCharacteristic(characteristic.getUuid(), characteristic.getProperties(), characteristic.getPermissions());
            tempCharacteristic.setValue(characteristic.getValue());

            Notification notification = new Notification(device, tempCharacteristic);
            addNotification(notification);
        }



        @Override
        public void onError(BluetoothDevice device, BluetoothGattCharacteristic characteristic, CsBleTransceiverErrorCode errorCode) {

            Log.d(TAG, "[CS] onError!!");

            Notification notification = new Notification(device, errorCode);
            addNotification(notification);
        }
    };



    public CsLongTermEventTask(CsBleTransceiver csBleTransceiver, Messenger messenger, ExecutorService executor, ICsConnectivityServiceListener listener) {

        super(csBleTransceiver, messenger, executor);

        mCsConnectivityServiceListener = listener;

        setEnable(true);
    }



    @Override
    public void execute() throws Exception {

        super.execute();

        mCsBleTransceiver.registerListener(mCsBleTransceiverListener);

        Log.d(TAG, "[CS] mEnable = " + mEnable);

        while (mEnable) {

            Log.d(TAG, "[CS] mNotificationQueue.size() = " + mNotificationQueue.size());
            Notification notification = mNotificationQueue.poll(Long.MAX_VALUE, TimeUnit.SECONDS);
            processNotification(notification);
        }

        mCsBleTransceiver.unregisterListener(mCsBleTransceiverListener);
    }



    @Override
    public void error(Exception e) {

    }



    public void setEnable(boolean enable) {

        mEnable = enable;
    }



    public void registerUuid(BluetoothDevice device, CsBleGattAttributes.CsV1CommandEnum commandID) {

        ArrayList<CsBleGattAttributes.CsV1CommandEnum> uuidCommandList = mNotificationMap.get(device);
        ArrayList<LongCommandCollector> collectorList = mLongCommandCollectorMap.get(device);

        if (uuidCommandList != null) {

            for (int cnt = 0; cnt < uuidCommandList.size(); cnt++) {

                if (uuidCommandList.get(cnt) == commandID) {

                    return;
                }
            }

            uuidCommandList.add(commandID);

        } else {

            uuidCommandList = new ArrayList<CsBleGattAttributes.CsV1CommandEnum>();
            mNotificationMap.put(device, uuidCommandList);

            uuidCommandList.add(commandID);
        }

        if (CsBleGattAttributes.isLongFormat(commandID)) {

            if (collectorList != null) {

                for (int cnt = 0; cnt < collectorList.size(); cnt++) {

                    if (collectorList.get(cnt).getUuid() == commandID) {

                        return;
                    }
                }

                collectorList.add(new LongCommandCollector(device, commandID));

            } else {

                collectorList = new ArrayList<LongCommandCollector>();
                mLongCommandCollectorMap.put(device, collectorList);

                collectorList.add(new LongCommandCollector(device, commandID));
            }
        }
    }



    public void unregisterUuid(BluetoothDevice device, CsBleGattAttributes.CsV1CommandEnum commandID) {

        ArrayList<CsBleGattAttributes.CsV1CommandEnum> uuidCommandList = mNotificationMap.get(device);
        ArrayList<LongCommandCollector> collectorList = mLongCommandCollectorMap.get(device);

        if (uuidCommandList != null) {

            for (int cnt = 0; cnt < uuidCommandList.size(); cnt++) {

                if (uuidCommandList.get(cnt) == commandID) {

                    uuidCommandList.remove(cnt);

                }
            }
        }

        if (CsBleGattAttributes.isLongFormat(commandID)) {

            if (collectorList != null) {

                for (int cnt = 0; cnt < collectorList.size(); cnt++) {

                    if (collectorList.get(cnt).getUuid() == commandID) {

                        collectorList.remove(cnt);
                    }
                }
            }
        }
    }



    public boolean checkUuid(BluetoothDevice device, CsBleGattAttributes.CsV1CommandEnum commandID) {

        boolean ret = false;
        ArrayList<CsBleGattAttributes.CsV1CommandEnum> uuidCommandList = mNotificationMap.get(device);

        if (uuidCommandList != null) {

            for (int cnt = 0; cnt < uuidCommandList.size(); cnt++) {

                if (uuidCommandList.get(cnt) == commandID) {

                    ret = true;
                }
            }
        }

        return ret;
    }



    public LongCommandCollector getCollector(BluetoothDevice device, CsBleGattAttributes.CsV1CommandEnum commandID) {

        LongCommandCollector ret = null;

        ArrayList<LongCommandCollector> collectorList = mLongCommandCollectorMap.get(device);

        if (CsBleGattAttributes.isLongFormat(commandID)) {

            if (collectorList != null) {

                for (int cnt = 0; cnt < collectorList.size(); cnt++) {

                    if (collectorList.get(cnt).getUuid() == commandID) {

                        ret = collectorList.get(cnt);
                    }
                }
            }
        }

        return ret;
    }



    private synchronized void addNotification(Notification notification) {

        Log.d(TAG, "[CS] addNotification " + notification);

        if (notification != null) {

            mNotificationQueue.add(notification);
        }
    }



    private void processNotification(Notification notification) {

        Log.d(TAG, "[CS] processNotification mDevice = " + notification.mDevice + ", object = " + notification.mObject);

        if ((notification != null) && (notification.mObject instanceof CsBleTransceiverErrorCode)) {

            CsBleTransceiverErrorCode errorCode = (CsBleTransceiverErrorCode) notification.mObject;

            if (errorCode.equals(CsBleTransceiverErrorCode.ERROR_DISCONNECTED_FROM_GATT_SERVER)) {

                try {

                    mCsConnectivityServiceListener.onError(881);

                    Message outMsg = Message.obtain();
                    outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
                    Bundle outData = new Bundle();
                    outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_DISCONNECTED_FROM_GATT_SERVER);
                    outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, notification.mDevice);
                    outMsg.setData(outData);

                    mMessenger.send(outMsg);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

        } else if ((notification != null) && (notification.mObject instanceof BluetoothGattCharacteristic)) {

            BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) notification.mObject;
            Log.d(TAG, "Chararteristic:" + characteristic.getValue());
            CsBleGattAttributes.CsV1CommandEnum command = CsBleGattAttributes.CsV1CommandEnum.findCommandID(characteristic.getValue()[0]);
            if (checkUuid(notification.mDevice, command)) {

                try {

                    if (command == CsBleGattAttributes.CsV1CommandEnum.HWSTATUS_EVENT) {

                        int level = CsBleGattAttributeUtil.getHwStatus_BatteryLevel(characteristic);
                        int usbStorage  = CsBleGattAttributeUtil.getHwStatus_USBStatus(characteristic);
                        int adapterPlugin  = CsBleGattAttributeUtil.getHwStatus_AdapterStatus(characteristic);

                        processHwStatus(notification.mDevice, level, usbStorage, adapterPlugin, -1);

                    } else if (command == CsBleGattAttributes.CsV1CommandEnum.POWER_STATUS_EVENT) {

                        if (characteristic.getValue().length > 0) {

                            int value = (int)characteristic.getValue()[0];

                            processHwStatus(notification.mDevice, -1, -1, -1, value);
                        }

                    }// else if (command == CsBleGattAttributes.CsV1CommandEnum.REQUEST_GSP_DATE_EVENT) {
//
//                        boolean onoff = CsBleGattAttributeUtil.getRequestGpsInfoSwitch(characteristic);
//
//                        Message outMsg = Message.obtain();
//                        outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//                        Bundle outData = new Bundle();
//                        outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_REQUEST_GPS_INFO);
//                        outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, notification.mDevice);
//
//                        if (onoff) {
//
//                            outData.putSerializable(ICsConnectivityService.PARAM_REQUEST_GPS_INFO_SWITCH, SwitchOnOff.SWITCH_ON);
//
//                        } else {
//
//                            outData.putSerializable(ICsConnectivityService.PARAM_REQUEST_GPS_INFO_SWITCH, SwitchOnOff.SWITCH_OFF);
//                        }
//
//                        outMsg.setData(outData);
//
//                        mMessenger.send(outMsg);
//
//                    } else if (command == CsBleGattAttributes.CsV1CommandEnum.GET_METADATA_EVENT) {
//
//                        LongCommandCollector collector = getCollector(notification.mDevice, CsBleGattAttributes.CsV1CommandEnum.GET_METADATA_EVENT);
//
//                        Log.d(TAG, "[CS] collector = " + collector);
//
//                        if (collector != null) {
//
//                            if (collector.update(notification.mDevice, characteristic)) {
//
//                                byte[] value = collector.get();
//                                String str = "";
//                                for (int i = 0; i < value.length; i++) {
//
//                                    str = str + String.format("%02xh ", value[i]);
//                                }
//                                Log.d(TAG, "[CS] get meta data event, received = " + str);
//
//                                processMetadata(notification.mDevice, value);
//
//                                collector.reset();
//                            }
//                        }
//
//                    } else if (command == CsBleGattAttributes.CsV1CommandEnum.OPERATION_STATUS_EVENT) {
//
//                        processCameraStatus(notification.mDevice, characteristic.getValue());
//
//                    } else if (command == CsBleGattAttributes.CsV1CommandEnum.CAMERA_ERROR_EVENT) {
//
//                        processCameraError(notification.mDevice, characteristic.getValue());
//
//                    } /*else if (characteristic.getUuid().toString().equals(CsBleGattAttributes.CS_AUTO_BACKUP_GENERAL_RESULT)) {
//
//                        processAutoBackupError(notification.mDevice, characteristic.getValue());
//
//                    } */else if (command == CsBleGattAttributes.CsV1CommandEnum.WIFI_SCAN_RESULT_EVENT) {
//
//                        LongCommandCollector collector = getCollector(notification.mDevice, CsBleGattAttributes.CsV1CommandEnum.WIFI_SCAN_RESULT_EVENT);
//
//                        Log.d(TAG, "[CS] collector = " + collector);
//
//                        if (collector != null) {
//
//                            if (collector.update(notification.mDevice, characteristic)) {
//
//                                byte[] value = collector.get();
//                                String str = "";
//                                for (int i = 0; i < value.length; i++) {
//
//                                    str = str + String.format("%02xh ", value[i]);
//                                }
//                                Log.d(TAG, "[CS] wifi scan result event, received = " + str);
//
//                                processAPScanResult(notification.mDevice, value);
//
//                                collector.reset();
//                            }
//                        }
//
//                    } else if (command == CsBleGattAttributes.CsV1CommandEnum.GENERAL_PURPOSE_NOTIFY_EVENT) {
//
//                        LongCommandCollector collector = getCollector(notification.mDevice, CsBleGattAttributes.CsV1CommandEnum.GENERAL_PURPOSE_NOTIFY_EVENT);
//
//                        Log.d(TAG, "[CS] collector = " + collector);
//
//                        if (collector != null) {
//
//                            if (collector.update(notification.mDevice, characteristic)) {
//
//                                byte[] value = collector.get();
//                                String str = "";
//                                for (int i = 0; i < value.length; i++) {
//
//                                    str = str + String.format("%02xh ", value[i]);
//                                }
//                                Log.d(TAG, "[CS] general purpose notify event, received = " + str);
//
//                                processGeneralPurposeNotify(notification.mDevice, value);
//
//                                collector.reset();
//                            }
//                        }
//
//                    } else if (command == CsBleGattAttributes.CsV1CommandEnum.GET_CAMERA_MODE_REQUEST_EVENT) {
//
//                        processCameraMode(notification.mDevice, characteristic.getValue());
//
//                    } else if (command == CsBleGattAttributes.CsV1CommandEnum.LTE_CAMPING_STATUS_REQUEST_EVENT) {
//
//                        processLTECampingStatus(notification.mDevice, characteristic.getValue());
//
//                    } else {
//
//                        Log.d(TAG, "[CS] unknown command " + command);
//
//                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

        }
    }



    private void processHwStatus(BluetoothDevice device, int level, int usbStorage, int adapterPlugin, int csPower) {

        try {

            Message outMsg = Message.obtain();
            outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
            Bundle outData = new Bundle();
            outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_HW_STATUS);
            outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);

            if (level >= 1) {

                //outData.putSerializable(ICsConnectivityService.PARAM_BATTERY_LEVEL, MCUBatteryLevel.findLevel(level));
                outData.putSerializable(ICsConnectivityService.PARAM_BATTERY_LEVEL, ICsConnectivityService.HWStatusEvent.MCUBatteryLevel.findLevel(level));
            }

//            if (usbStorage == 0) {
//
//                outData.putSerializable(ICsConnectivityService.PARAM_USB_STORAGE, PlugIO.PLUG_OUT);
//
//            } else if (usbStorage == 1) {
//
//                outData.putSerializable(ICsConnectivityService.PARAM_USB_STORAGE, PlugIO.PLUG_IN);
//            }

//            if (adapterPlugin == 0) {
//
//                outData.putSerializable(ICsConnectivityService.PARAM_ADAPTER_PLUGIN, PlugIO.PLUG_OUT);
//
//            } else if (adapterPlugin == 1) {
//
//                outData.putSerializable(ICsConnectivityService.PARAM_ADAPTER_PLUGIN, PlugIO.PLUG_IN);
//            }

            if (csPower == 0) {

                outData.putSerializable(ICsConnectivityService.PARAM_CS_POWER, SwitchOnOff.SWITCH_OFF);

            } else if (csPower == 1) {

                outData.putSerializable(ICsConnectivityService.PARAM_CS_POWER, SwitchOnOff.SWITCH_ON);
            }

            outMsg.setData(outData);

            mMessenger.send(outMsg);

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

//    private final int METADATA_NO_GPS_INFO_LENGTH = 42;
//    private final int METADATA_FOLDERNAME_START_INDEX = 4;
//    private final int METADATA_FOLDERNAME_END_INDEX = 12;
//    private final int METADATA_FILENAME_START_INDEX = 13;
//    private final int METADATA_FILENAME_END_INDEX = 25;
//
//    private void processMetadata(BluetoothDevice device, byte[] metadataArray) {
//
//        int fileId;
//        String folderName = "";
//        String fileName = "";
//        int fileType;
//        Calendar fileCreateTime = Calendar.getInstance();
//        int fileSize;
//        int videoDuration;
//
//        try {
//
//            if (metadataArray.length < METADATA_NO_GPS_INFO_LENGTH) {
//
//                return;
//            }
//
//            fileId = CsBleGattAttributeUtil.byteArrayToInt(metadataArray, 0);
//
//            for (int cnt = METADATA_FOLDERNAME_START_INDEX; cnt <= METADATA_FOLDERNAME_END_INDEX; cnt++) {
//
//                if (metadataArray[cnt] != 0x00) {
//
//                    folderName = folderName + String.format("%c", metadataArray[cnt]);
//
//                } else {
//
//                    break;
//                }
//            }
//
//            if (folderName.length() <= 0) {
//
//                return;
//            }
//
//            for (int cnt = METADATA_FILENAME_START_INDEX; cnt <= METADATA_FILENAME_END_INDEX; cnt++) {
//
//                if (metadataArray[cnt] != 0x00) {
//
//                    fileName = fileName + String.format("%c", metadataArray[cnt]);
//
//                } else {
//
//                    break;
//                }
//            }
//
//            if (fileName.length() <= 0) {
//
//                return;
//            }
//
//            fileType = metadataArray[26];
//
//            int year = (metadataArray[27] & 0xff) | ((metadataArray[28] & 0xff) << 8);
//            int month = metadataArray[29];
//            int date = metadataArray[30];
//            int hour = metadataArray[31];
//            int minute = metadataArray[32];
//            int second = metadataArray[33];
//
//            Log.d(TAG, "[CS] year = " + year + ", month = " + month + ", date = " + date + ", hour = " + hour + ", minute = " + minute + ", second = " + second);
//
//            if ((year < 1970) ||
//                (month < 0) || (month > 11) ||
//                (date < 1) || (date > 31) ||
//                (hour < 0) || (hour > 23) ||
//                (minute < 0) ||    (minute > 59) ||
//                (second < 0) || (second > 59)) {
//
//                return;
//            }
//
//            fileCreateTime.clear();
//            fileCreateTime.set(year, month, date, hour, minute, second);
//
//            fileSize = CsBleGattAttributeUtil.byteArrayToInt(metadataArray, 34);
//
//            if (fileSize < 0) {
//
//                return;
//            }
//
//            videoDuration = CsBleGattAttributeUtil.byteArrayToInt(metadataArray, 38);
//
//            if (videoDuration < 0) {
//
//                return;
//            }
//
//            Message outMsg = Message.obtain();
//            outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//            Bundle outData = new Bundle();
//            outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_METADATA);
//            outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);
//            outData.putInt(ICsConnectivityService.PARAM_FILE_ID, fileId);
//            outData.putString(ICsConnectivityService.PARAM_FOLDER_NAME, folderName);
//            outData.putString(ICsConnectivityService.PARAM_FILE_NAME, fileName);
//            outData.putInt(ICsConnectivityService.PARAM_FILE_TYPE, fileType);
//            outData.putSerializable(ICsConnectivityService.PARAM_FILE_CREATE_TIME, fileCreateTime);
//            outData.putInt(ICsConnectivityService.PARAM_FILE_SIZE, fileSize);
//            outData.putInt(ICsConnectivityService.PARAM_VIDEO_DURATION, videoDuration);
//
//            outMsg.setData(outData);
//
//            mMessenger.send(outMsg);
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }



//    private void processCameraStatus(BluetoothDevice device, byte[] statusArray) {
//
//        OperationEvent opEvent;
//        int eventType;
//        int fileType;
//        int readyBit;
//        int imageRemainCount;
//        int videoRemainSecond;
//        int timelapseRemainCount;
//        int timelapseTotalCount;
//        int slowmotionRemainSecond;
//        int timelapseCurrentCount;
//
//        try {
//
//            Message outMsg = Message.obtain();
//            outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//            Bundle outData = new Bundle();
//            outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_CAMERA_STATUS);
//            outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);
//
//            eventType = statusArray[1];
//
//            if ((eventType == 0x01) || (eventType == 0x03)) {
//
//                opEvent = (eventType == 0x01) ? OperationEvent.OPEVENT_START_CAPTURING : OperationEvent.OPEVENT_START_RECORDING;
//                fileType = statusArray[2];
//
//                outData.putSerializable(ICsConnectivityService.PARAM_OPERATION_EVENT, opEvent);
//                outData.putInt(ICsConnectivityService.PARAM_FILE_TYPE, fileType);
//
//            } else if ((eventType == 0x02) || (eventType == 0x05)) {
//
//                opEvent = (eventType == 0x02) ? OperationEvent.OPEVENT_COMPLETE_CAPTURING : OperationEvent.OPEVENT_COMPLETE_RECORDING;
//                fileType = statusArray[2];
//                readyBit = statusArray[3];
//                imageRemainCount = CsBleGattAttributeUtil.byteArrayToInt(statusArray, 4);
//                videoRemainSecond = CsBleGattAttributeUtil.byteArrayToInt(statusArray, 8);
//                timelapseRemainCount = CsBleGattAttributeUtil.byteArrayToInt(statusArray, 12);
//                slowmotionRemainSecond = CsBleGattAttributeUtil.byteArrayToInt(statusArray, 16);
//
//                outData.putSerializable(ICsConnectivityService.PARAM_OPERATION_EVENT, opEvent);
//                outData.putInt(ICsConnectivityService.PARAM_FILE_TYPE, fileType);
//                outData.putInt(ICsConnectivityService.PARAM_READY_BIT, readyBit);
//                outData.putInt(ICsConnectivityService.PARAM_IMAGE_REMAIN_COUNT, imageRemainCount);
//                outData.putInt(ICsConnectivityService.PARAM_VIDEO_REMAIN_SECOND, videoRemainSecond);
//                outData.putInt(ICsConnectivityService.PARAM_TIME_LAPSE_REMAIN_COUNT, timelapseRemainCount);
//                outData.putInt(ICsConnectivityService.PARAM_SLOW_MOTION_REMAIN_SECOND, slowmotionRemainSecond);
//
//            } else if (eventType == 0x07) {
//
//                opEvent = OperationEvent.OPEVENT_TIME_LAPSE_CAPTURE_ONE;
//                timelapseCurrentCount = CsBleGattAttributeUtil.byteArrayToInt(statusArray, 2);
//                timelapseRemainCount = CsBleGattAttributeUtil.byteArrayToInt(statusArray, 6);
//                timelapseTotalCount = CsBleGattAttributeUtil.byteArrayToInt(statusArray, 10);
//
//                outData.putSerializable(ICsConnectivityService.PARAM_OPERATION_EVENT, opEvent);
//                outData.putInt(ICsConnectivityService.PARAM_TIME_LAPSE_CURRENT_COUNT, timelapseCurrentCount);
//                outData.putInt(ICsConnectivityService.PARAM_TIME_LAPSE_REMAIN_COUNT, timelapseRemainCount);
//                outData.putInt(ICsConnectivityService.PARAM_TIME_LAPSE_TOTAL_COUNT, timelapseTotalCount);
//
//            } else {
//
//                return;
//            }
//
//            outMsg.setData(outData);
//
//            mMessenger.send(outMsg);
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }



//    private void processCameraError(BluetoothDevice device, byte[] errorArray) {
//
//        Log.d(TAG, "[CS] processCameraError statusArray.length = " + errorArray.length);
//
//        int errorIndex;
//        int errorCode;
//
//        try {
//
//            Message outMsg = Message.obtain();
//            outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//            Bundle outData = new Bundle();
//            outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_CAMERA_ERROR);
//            outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);
//
//            errorIndex = CsBleGattAttributeUtil.byteArrayToInt(errorArray, 0);
//            errorCode = CsBleGattAttributeUtil.byteArrayToInt(errorArray, 4);
//
//            outData.putInt(ICsConnectivityService.PARAM_CAMERA_ERROR_INDEX, errorIndex);
//            outData.putInt(ICsConnectivityService.PARAM_CAMERA_ERROR_CODE, errorCode);
//
//            outMsg.setData(outData);
//
//            mMessenger.send(outMsg);
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }



//    private void processAutoBackupError(BluetoothDevice device, byte[] abErrorArray) {
//
//        try {
//
//            int type = abErrorArray[0];
//
//            if (type == 0) {
//
//                /// APP error code
//                Message outMsg = Message.obtain();
//                outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//                Bundle outData = new Bundle();
//
//                int errorCode = (int) CsBleGattAttributeUtil.byteArrayToShort(abErrorArray, 1);
//
//                if (errorCode == 1) {
//
//                    SwitchOnOff onoff = SwitchOnOff.SWITCH_ON;
//
//                    outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_HOTSPOT_CONTROL);
//                    outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);
//                    outData.putSerializable(ICsConnectivityService.PARAM_SWITCH_ON_OFF, onoff);
//
//                } else if (errorCode == 2) {
//
//                    SwitchOnOff onoff = SwitchOnOff.SWITCH_OFF;
//
//                    outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_HOTSPOT_CONTROL);
//                    outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);
//                    outData.putSerializable(ICsConnectivityService.PARAM_SWITCH_ON_OFF, onoff);
//
//                } else {
//
//                    outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_AUTO_BACKUP_ERROR);
//                    outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);
//                    outData.putInt(ICsConnectivityService.PARAM_AUTO_BACKUP_ERROR_TYPE, type);
//                    outData.putInt(ICsConnectivityService.PARAM_AUTO_BACKUP_ERROR_CODE, errorCode);
//                }
//
//                outMsg.setData(outData);
//                mMessenger.send(outMsg);
//
//            } else if (type == 1) {
//
//                /// Progress
//                byte [] generalResultArray = abErrorArray;
//                int errorCode = generalResultArray[1];
//
//                if (errorCode == 0x00) {
//
//                    int remainFileCount = CsBleGattAttributeUtil.byteArrayToInt(generalResultArray, 3);
//                    int totalFileCount = CsBleGattAttributeUtil.byteArrayToInt(generalResultArray, 7);
//
//                    Log.d(TAG, "[CS] remainFileCount = " + remainFileCount);
//                    Log.d(TAG, "[CS] totalFileCount = " + totalFileCount);
//
//                    Message outMsg = Message.obtain();
//                    outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//                    Bundle outData = new Bundle();
//                    outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_AUTO_BACKUP_PROGRESS);
//                    outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);
//                    outData.putInt(ICsConnectivityService.PARAM_REMAIN_FILE_COUNT, remainFileCount);
//                    outData.putInt(ICsConnectivityService.PARAM_TOTAL_FILE_COUNT, totalFileCount);
//
//                    outMsg.setData(outData);
//                    mMessenger.send(outMsg);
//                }
//
//            } else if (type == 2) {
//
//                /// CS error code
//                Message outMsg = Message.obtain();
//                outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//                Bundle outData = new Bundle();
//
//                int errorCode = (int) CsBleGattAttributeUtil.byteArrayToShort(abErrorArray, 1);
//
//                outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_AUTO_BACKUP_ERROR);
//                outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);
//                outData.putInt(ICsConnectivityService.PARAM_AUTO_BACKUP_ERROR_TYPE, type);
//                outData.putInt(ICsConnectivityService.PARAM_AUTO_BACKUP_ERROR_CODE, errorCode);
//
//                outMsg.setData(outData);
//                mMessenger.send(outMsg);
//            }
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }



    private void processAPScanResult(BluetoothDevice device, byte[] scanResultArray) {

        try {

            if (scanResultArray.length == 1 && scanResultArray[0] == (byte)0x80) {
                Log.d(TAG, "[CS] cannot find any wifi ap");

                Message outMsg = Message.obtain();
                outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
                Bundle outData = new Bundle();
                outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_AUTO_BACKUP_AP_SCAN_RESULT);
                outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);

                outData.putBoolean(ICsConnectivityService.PARAM_AP_ANY_SCAN_RESULT, false);

                outMsg.setData(outData);

                mMessenger.send(outMsg);
            } else {
                int endOfScanList = ((scanResultArray[0] & 0x80) == 0x80) ? 1 : 0;
                int indexOfScanList = scanResultArray[0] & 0x7f;
                short rssi = CsBleGattAttributeUtil.byteArrayToShort(scanResultArray, 1);
                int security = scanResultArray[3];
                int authorization = scanResultArray[4];
                String apSsid = CsBleGattAttributeUtil.byteArrayToString(scanResultArray, 6, scanResultArray[5]);

                Log.d(TAG, "[CS] endOfScanList = " + endOfScanList);
                Log.d(TAG, "[CS] indexOfScanList = " + indexOfScanList);
                Log.d(TAG, "[CS] rssi = " + rssi);
                Log.d(TAG, "[CS] security = " + security);
                Log.d(TAG, "[CS] authorization = " + authorization);
                Log.d(TAG, "[CS] apSsid = " + apSsid);

                Message outMsg = Message.obtain();
                outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
                Bundle outData = new Bundle();
                outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_AUTO_BACKUP_AP_SCAN_RESULT);
                outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);

                outData.putBoolean(ICsConnectivityService.PARAM_AP_ANY_SCAN_RESULT, true);
                outData.putInt(ICsConnectivityService.PARAM_AP_END_OF_SCAN_LIST, endOfScanList);
                outData.putInt(ICsConnectivityService.PARAM_AP_INDEX_OF_SCAN_LIST, indexOfScanList);
                outData.putShort(ICsConnectivityService.PARAM_AP_RSSI, rssi);
                outData.putInt(ICsConnectivityService.PARAM_AP_SECURITY, security);
                outData.putInt(ICsConnectivityService.PARAM_AP_AUTHORIZATION, authorization);
                outData.putString(ICsConnectivityService.PARAM_AP_SSID, apSsid);

                outMsg.setData(outData);

                mMessenger.send(outMsg);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }



//    private void processGeneralPurposeNotify(BluetoothDevice device, byte[] dataArray) {
//
//        CsGeneralPurposeCommandTask.MessageNotifyData messageNotifyData = CsGeneralPurposeCommandTask.parseMessageNotifyData(dataArray);
//        final byte appId = messageNotifyData.appId;
//        final String messageType = messageNotifyData.messageType;
//        final String message = messageNotifyData.message;
//        final byte[] messageRawData = messageNotifyData.messageRawData;
//        Log.d(TAG, "[CS] got general purpose notify: app ID=" + appId + ", message type=" + messageType + ", message=" + message);
//
//        try {
//            switch (appId) {
//            case CsGeneralPurposeCommandTask.APP_ID_AUTOBACKUP:
//                if (messageType.equals("user")) {
//                    Message outMsg = Message.obtain();
//                    outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//                    Bundle outData = new Bundle();
//                    outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_AUTO_BACKUP_ERROR2);
//
//                    outData.putString(ICsConnectivityService.PARAM_AUTO_BACKUP_ERROR2_MESSAGE, message);
//
//                    outMsg.setData(outData);
//
//                    mMessenger.send(outMsg);
//                } else {
//                    Log.d(TAG, "[CS] unknown message type:" + messageType);
//                }
//                break;
//            case CsGeneralPurposeCommandTask.APP_ID_BROADCAST:
//                switch (messageType.getBytes()[0]) {
//                case (byte)0x01: { // error code
//                    // messageRawData[0]  : error code
//                    // messageRawData[1]  : ,
//                    // messageRawData[2~] : timestamp
//                    byte errorCode = messageRawData[0];
//                    String errorTimestamp = null;
//                    try {
//                        errorTimestamp = new String(messageRawData, 2, messageRawData.length - 2);
//                    } catch (IndexOutOfBoundsException e) {
//                        Log.e(TAG, "[CS] cannot get error timestamp", e);
//                    }
//
//                    Message outMsg = Message.obtain();
//                    outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//                    Bundle outData = new Bundle();
//                    outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_BROADCAST_ERROR);
//
//                    outData.putByte(ICsConnectivityService.PARAM_BROADCAST_ERROR_CODE, errorCode);
//                    outData.putString(ICsConnectivityService.PARAM_BROADCAST_ERROR_TIMESTAMP, errorTimestamp);
//
//                    outMsg.setData(outData);
//
//                    mMessenger.send(outMsg);
//                } break;
//                case (byte)0x02: { // video url
//                    Message outMsg = Message.obtain();
//                    outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//                    Bundle outData = new Bundle();
//                    outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_BROADCAST_VIDEO_URL_RECEIVED);
//
//                    outData.putString(ICsConnectivityService.PARAM_BROADCAST_VIDEO_URL, message);
//
//                    outMsg.setData(outData);
//
//                    mMessenger.send(outMsg);
//                } break;
//                case (byte)0x03: { // live event status
//                    switch (messageRawData[0]) {
//                    case ((byte)0x1): {
//                        Message outMsg = Message.obtain();outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//                        Bundle outData = new Bundle();
//                        outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_BROADCAST_LIVE_BEGIN);
//
//                        outMsg.setData(outData);
//
//                        mMessenger.send(outMsg);
//                    } break;
//                    case ((byte)0x2): {
//                        Message outMsg = Message.obtain();
//                        outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//                        Bundle outData = new Bundle();
//                        outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_BROADCAST_LIVE_END);
//
//                        outMsg.setData(outData);
//
//                        mMessenger.send(outMsg);
//                    } break;
//                    default:
//                        Log.d(TAG, "[CS] unknown live event status:0x" + Integer.toHexString(messageRawData[0]));
//                        break;
//                    }
//                } break;
//                default:
//                    Log.d(TAG, "[CS] unknown message type:0x" + Integer.toHexString(messageType.getBytes()[0]));
//                    break;
//                }
//                break;
//            case CsGeneralPurposeCommandTask.APP_ID_SMS:
//                switch (messageType.getBytes()[0]) {
//                case (byte) 0x01:
//                    mReceiveSMSCollector.collect(messageRawData);
//                    if (mReceiveSMSCollector.isComplete()) {
//                        String dateTime = mReceiveSMSCollector.getDateTime();
//                        String phoneNumber = mReceiveSMSCollector.getPhoneNumber();
//                        String messageContent = mReceiveSMSCollector.getMessageContent();
//                        mReceiveSMSCollector.reset();
//
//                        Message outMsg = Message.obtain();
//                        outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//                        Bundle outData = new Bundle();
//                        outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_SMS_RECEIVED);
//
//                        outData.putString(ICsConnectivityService.PARAM_SMS_DATE_TIME, dateTime);
//                        outData.putString(ICsConnectivityService.PARAM_SMS_PHONE_NUMBER, phoneNumber);
//                        outData.putString(ICsConnectivityService.PARAM_SMS_MESSAGE_CONTENT, messageContent);
//
//                        outMsg.setData(outData);
//
//                        mMessenger.send(outMsg);
//                    }
//                    break;
//                default :
//                    Log.d(TAG, "[CS] unknown message type:0x" + Integer.toHexString(messageType.getBytes()[0]));
//                    break;
//                }
//                break;
//            default:
//                Log.d(TAG, "[CS] unknown app id " + appId + " for general purpose notify");
//                break;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



//    private void processCameraMode(BluetoothDevice device, byte[] dataArray) {
//        try {
//
//            Message outMsg = Message.obtain();
//            outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//            Bundle outData = new Bundle();
//            outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_CAMERA_MODE);
//            outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);
//            outData.putSerializable(ICsConnectivityService.PARAM_CAMERA_MODE, ICsConnectivityService.CameraMode.findMode(dataArray[1]));
//
//            outMsg.setData(outData);
//
//            mMessenger.send(outMsg);
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }



//    private void processLTECampingStatus(BluetoothDevice device, byte[] dataArray) {
//        try {
//
//            Message outMsg = Message.obtain();
//            outMsg.what = ICsConnectivityService.CB_LONG_TERM_EVENT_RESULT;
//            Bundle outData = new Bundle();
//            outData.putSerializable(ICsConnectivityService.PARAM_LONG_TERM_EVENT, LongTermEvent.LTEVENT_LTE_CAMPING_STATUS);
//            outData.putParcelable(ICsConnectivityService.PARAM_BLUETOOTH_DEVICE, device);
//            outData.putSerializable(ICsConnectivityService.PARAM_LTE_CAMPING_STATUS, ICsConnectivityService.LTECampingStatus.findStatus(dataArray[1]));
//
//            outMsg.setData(outData);
//
//            mMessenger.send(outMsg);
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }



    private class Notification implements Comparable<Notification> {

        public final BluetoothDevice mDevice;
        public final Object mObject;

        public Notification(BluetoothDevice device, Object object) {

            mDevice = device;
            mObject = object;
        }



        @Override
        public int compareTo(Notification another) {

            return 0;
        }
    }
}
