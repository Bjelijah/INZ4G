package com.inz.bean;

import com.inz.player.ICam;
import com.inz.player.TurnMgr;

/**
 * Created by howell on 2016/12/6.
 */

public class CamFactory {
    public static ICam mp=null;
    public static ICam buildCam(PlayType type){
        switch (type){
            case ECAM:
//                mp = new ECamMgr();
                break;
            case TURN:
                mp = new TurnMgr();
                break;
            case HW5198:
//                mp = new ApCamMgr();
                break;
            default:
                break;
        }
        return mp;
    }


}
