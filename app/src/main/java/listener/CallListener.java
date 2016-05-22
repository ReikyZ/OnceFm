package listener;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by Reiky on 2016/5/21.
 */
public class CallListener extends PhoneStateListener {

    boolean beforeCall;

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE: // 挂机状态 继续播放
                if (beforeCall) {
//                    MainActivity.intentPlaySong.putExtra("MSG", PlaySong.MSG_CONTINUE);
//                    startService(MainActivity.intentPlaySong);
//                    Log.i("Test Listener", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> on over call");
//                    isPlaying = true;
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:    //通话状态
            case TelephonyManager.CALL_STATE_RINGING:    //响铃状态  暂停播放
//                if (isPlaying) {
//                    MainActivity.intentPlaySong.putExtra("MSG", PlaySong.MSG_PAUSE);
//                    Log.i("Test Listener", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> on call");
//                    startService(MainActivity.intentPlaySong);
//                    isPlaying = false;
//                    beforeCall = true;
//                } else {
//                    beforeCall = false;
//                }
                break;
            default:
                break;
        }
    }
}