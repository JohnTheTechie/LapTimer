package johnfatso.laptimer;


import android.os.Handler;
import android.os.Message;

public class Clock extends Thread {

    //messages
    static final int CLOCK_MESSAGE_TICK = 0x01;
    static final int CLOCK_MESSAGE_COMPLETE = 0x02;

    //status values
    private final int CLOCK_IDLE = 0x01;
    private final int CLOCK_RUNNING = 0x02;
    private final int CLOCK_PAUSED = 0x03;

    //base interval between two ticks
    private long base_tick_duration;
    //duration to wait until triggering next tick
    private long next_tick_duration;
    //flag to control clock ticks. To be set true for clock to run
    private boolean clock_control_flag;
    //status of the clock
    private int status;
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
        this.status = CLOCK_IDLE;
        this.handler = handler;
    }

    @Override
    public void run() {
        while (this.clock_control_flag){
            try {
                sleep(this.next_tick_duration);
            }catch (Exception e) {
                //do nothing
                //TODO: handle exception
            }
            long ref_time = java.lang.System.currentTimeMillis();
            this.ticks_to_elapse--;
            if(this.ticks_to_elapse == 0){
                this.clock_control_flag = false;
                this.status = CLOCK_IDLE;
                this.onComplete();
            }
            else
                this.onTick();
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
        this.status = CLOCK_RUNNING;
        this.ticks_to_elapse = ticks;
        this.clock_control_flag = true;
        this.start();
    }

    /**
     * start the clock for specified ticks, with tick intervals of tickDuration milliseconds
     *
     * @param ticks how many ticks to be generated
     * @param tickDuration interval between two ticks
     */
    public void startClock(long ticks, long tickDuration){
        if(this.status == CLOCK_IDLE){
            this.status = CLOCK_RUNNING;
            this.ticks_to_elapse = ticks;
            this.base_tick_duration = tickDuration;
            this.next_tick_duration = this.base_tick_duration;
            this.clock_control_flag = true;
            this.start();
        }
        else {
            throw new IllegalStateException("start called on active clock");
        }
    }

    /**
     * stops and resets the clock
     */
    void stopClock(){
        this.clock_control_flag = false;
        this.status = CLOCK_IDLE;
        this.ticks_to_elapse = 0;
        this.base_tick_duration = 1000;
        this.next_tick_duration = this.base_tick_duration;
    }

    /**
     * pauses the clock. to restart call resume()
     */
    void pauseClock(){
        this.clock_control_flag = false;
        this.status = CLOCK_PAUSED;
    }

    /**
     * resumes the clock, if the clock is in PAUSED state.
     * If called on IDLE state throws IllegalStateException
     */
    void resumeClock(){
        if(this.status == CLOCK_IDLE)
            throw new IllegalStateException("resume called on idle clock");
        else if(this.status == CLOCK_PAUSED){
            this.clock_control_flag = true;
            this.status = CLOCK_RUNNING;
            this.start();
        }
    }

    /**
     * reads the status of the clock
     *
     * @return the current status
     */
    public int getStatus(){
        return this.status;
    }

    /***
     * resets the clock to initial condition
     */

    void resetClock(){
        this.base_tick_duration = 1000;
        //first clock tick will be generated after base_tick_duration
        this.next_tick_duration = this.base_tick_duration;
        this.clock_control_flag = false;
        this.status = CLOCK_IDLE;
    }

    /**
     * called when tick is generated
     */
    public void onTick(){
        Message message = this.handler.obtainMessage(Clock.CLOCK_MESSAGE_TICK);
        message.sendToTarget();
    }


    /**
     * called when clock elapsed
     */
    public void onComplete(){
        Message message = this.handler.obtainMessage(Clock.CLOCK_MESSAGE_COMPLETE);
        message.sendToTarget();
    }

}
