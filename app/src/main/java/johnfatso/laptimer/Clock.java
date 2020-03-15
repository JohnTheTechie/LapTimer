package johnfatso.laptimer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class defines a thread for running a controllable stopwatch
 */
public class Clock extends Thread {

    final private String LOG_TAG = "CLOCK_THREAD";

    //messages
    static final int CLOCK_MESSAGE_TICK = 0x01;
    static final int CLOCK_MESSAGE_COMPLETE = 0x02;

    //base interval between two ticks
    private long base_tick_duration;
    //duration to wait until triggering next tick
    private long next_tick_duration;
    //flag to control clock ticks. To be set true for clock to run
    //if the clock is set to false when running, the operation shall seize
    private boolean clock_control_flag;
    //handler defined by the parent thread
    private Handler handler;

    //number of ticks until clock completion
    private long ticks_to_elapse;

    /**
     * Creates and initiates the clock object
     */
    Clock(Handler handler) {
        this.base_tick_duration = 1000;
        //first clock tick will be generated after base_tick_duration
        this.next_tick_duration = this.base_tick_duration;
        this.clock_control_flag = false;
        this.handler = handler;
        Log.v(LOG_TAG,this.getName()+" | object created");
    }

    @Override
    public void run() {
        //infinite loop runs until the flag is set to false
        while (this.clock_control_flag){
            Log.v(LOG_TAG,this.getName()+" | flag value:"+clock_control_flag);
            try {
                sleep(this.next_tick_duration);
            }catch (Exception e) {
                //do nothing
            }
            //time noted before doing operations
            long ref_time = java.lang.System.currentTimeMillis();
            //counter decremented
            this.ticks_to_elapse--;
            Log.v(LOG_TAG,this.getName()+" | remaining ticks: "+this.ticks_to_elapse);
            //if the tick have been exhausted, the clock is completed
            //hence the flag is unset and completion signal sent
            if(this.ticks_to_elapse <= 0){
                this.clock_control_flag = false;
                this.onComplete();
            }
            else
                this.onTick();
            //after the operations, time is noted and adjusted time to sleep is set
            long remaining_time = this.base_tick_duration - (java.lang.System.currentTimeMillis() - ref_time);
            this.next_tick_duration = remaining_time<0?0:remaining_time;
        }
    }

    /**
     * start the clock for specified ticks, with freq = 1 Hz
     *
     * @param ticks how many ticks to be generated
     */
    void startClock(long ticks){
        this.ticks_to_elapse = ticks;
        this.clock_control_flag = true;
        this.start();
        Log.v(LOG_TAG,this.getName()+" | start triggered");
    }

    /**
     * stops and resets the clock
     */
    void stopClock(){
        this.clock_control_flag = false;
        this.ticks_to_elapse = 0;
        this.base_tick_duration = 1000;
        this.next_tick_duration = this.base_tick_duration;
        handler = null;
        Log.v(LOG_TAG,this.getName()+" | stop triggered | flag = "+clock_control_flag);
    }

    /**
     * called when tick is generated
     */
    private void onTick(){
        sendMessageToClient(Clock.CLOCK_MESSAGE_TICK);
        Log.v(LOG_TAG,this.getName()+" | tick triggering");
    }

    /**
     * called when clock elapsed
     */
    private void onComplete(){
        sendMessageToClient(Clock.CLOCK_MESSAGE_COMPLETE);
        Log.v(LOG_TAG,this.getName()+" | tick triggering as current timer obj completed");
    }


    private void sendMessageToClient(int messageValue){
        if(handler != null){
            Message message = this.handler.obtainMessage(messageValue);
            message.sendToTarget();
        }
    }

}
