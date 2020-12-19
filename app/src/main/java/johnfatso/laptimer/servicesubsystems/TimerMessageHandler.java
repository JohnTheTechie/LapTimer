package johnfatso.laptimer.servicesubsystems;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import johnfatso.laptimer.constants.ClockMessage;

public class TimerMessageHandler extends Handler {

    TimerControllerMessageReceptor receptor;

    public TimerMessageHandler(@NonNull Looper looper, TimerControllerMessageReceptor receptor) {
        super(looper);
        this.receptor = receptor;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {

        if (msg.what == ClockMessage.CLOCK_MESSAGE_TICK.id){
            receptor.onTick();
        }
        else if(msg.what == ClockMessage.CLOCK_MESSAGE_COMPLETE.id){
            receptor.onClockComplete();
        }
    }

}
