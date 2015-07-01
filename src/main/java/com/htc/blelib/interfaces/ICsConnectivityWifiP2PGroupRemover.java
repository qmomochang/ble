package com.htc.blelib.interfaces;

import com.htc.blelib.v1.interfaces.ICsConnectivityService;



public interface ICsConnectivityWifiP2PGroupRemover {

    /// Interfaces
    public boolean csRemoveWifiP2pGroupInFinish();

    /// Callback
    //public static final int CB_REMOVE_WIFI_P2P_GROUP_IN_FINISH_RESULT    = ICsConnectivityService.CB_REMOVE_WIFI_P2P_GROUP_IN_FINISH_RESULT;

    /// Parameters
    public static final String PARAM_RESULT                                = ICsConnectivityService.PARAM_RESULT;
}
