package com.inz.utils;

/**
 * Created by Administrator on 2017/6/27.
 */

public interface IConfig {
    final String S_IP = "121.42.228.77";
    final int S_PORT_NORMAL = 8800;
    final int S_PORT_SSL = 8850;
    final boolean IS_SSL = true;
    final String T_IP = "121.42.228.77";
    final int T_PORT_SSL = 8862;
    final int T_PORT_NORMAL = 8812;



    final boolean IS_TEST = false;
    boolean RECORDE_FILE_TEST = true;
    String TEST_ACCOUNT = "howell";
    String TEST_PASSWORD = "123456";
    String TEST_S_IP = "192.168.21.240";
    String TEST_TURN_IP = "192.168.21.240";

    final int RECORD_SUB = 0;//回放用主码流

//    mDeviceId=00310101031111111000001000000000  mChannel=8~~~~~~~~~~~~~~~~~~~~~~

}
