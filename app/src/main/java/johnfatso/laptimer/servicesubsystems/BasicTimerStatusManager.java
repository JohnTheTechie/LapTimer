package johnfatso.laptimer.servicesubsystems;

import android.util.Log;

import johnfatso.laptimer.ClockService;
import johnfatso.laptimer.controller.ClockTimerEvent;
import johnfatso.laptimer.controller.EventObservable;
import johnfatso.laptimer.controller.OnControlCommandListener;
import johnfatso.laptimer.controller.OnTimerEventListener;
import johnfatso.laptimer.controller.TimerControlCommand;
import johnfatso.laptimer.controller.TimerControlInputObservable;
import johnfatso.laptimer.controller.TimerEventObservable;
import johnfatso.laptimer.status.ClockStatusManagerInterface;
import johnfatso.laptimer.status.StatusClockManager;
import johnfatso.laptimer.viewmodel.TimerActivityViewModelObservable;

public class BasicTimerStatusManager implements ClockStatusManagerInterface, OnControlCommandListener<TimerControlCommand>, OnTimerEventListener {

    private final String LOG_TAG = "TIMER_STATUS";
    private final String CLASS_ID = this.getClass().getName();

    private StatusClockManager timer_status;

    private TimerManagerInterface timer;
    private final ClockService service;

    public BasicTimerStatusManager(ClockService service) {
        this.timer_status = StatusClockManager.UNINITIALIZED;
        this.timer = null;
        this.service =service;
        TimerControlInputObservable.getObservable().register(this);
        Log.v(LOG_TAG, CLASS_ID + " | object created");
    }

    public BasicTimerStatusManager(TimerManagerInterface timer, ClockService service) {
        this.timer_status = StatusClockManager.UNINITIALIZED;
        this.timer = timer;
        this.service = service;
        TimerControlInputObservable.getObservable().register(this);
        TimerEventObservable.getObservable().register(this);
        Log.v(LOG_TAG, CLASS_ID + " | object created for the specified timer");
    }

    @Override
    public void set_timer_status(StatusClockManager service_status) {
        this.timer_status = service_status;
        Log.v(LOG_TAG, CLASS_ID + " | status updated | " + this.timer_status);
    }

    @Override
    public StatusClockManager get_timer_status() {
        Log.v(LOG_TAG, CLASS_ID + " | status read | " + this.timer_status);
        return timer_status;
    }

    @Override
    public boolean onControlCommand(EventObservable<TimerControlCommand> observable, TimerControlCommand command) {

        switch (command){

            case START:
                if(this.timer_status == StatusClockManager.INITIALIZED || this.timer_status == StatusClockManager.UNINITIALIZED  || this.timer_status == StatusClockManager.COMPLETED) {
                    Log.v(LOG_TAG, CLASS_ID + " | current status : " + this.timer_status + " | start clock requested");
                    timer.startClock();
                }else if(this.timer_status == StatusClockManager.PAUSED){
                    Log.v(LOG_TAG, CLASS_ID + " | current status : " + this.timer_status + " | resume clock requested");
                    timer.resumeClock();
                }
                else {

                    throw new IllegalStateException("Start called on a running clock service | current status : " + this.timer_status);
                }
                this.timer_status = StatusClockManager.RUNNING;
                break;

            case PAUSE:
                if(this.timer_status == StatusClockManager.RUNNING) {
                    timer.pauseClock();
                }else {
                    throw new IllegalStateException("Pause called on an Idle service");
                }
                this.timer_status = StatusClockManager.PAUSED;
                break;

            case RESET:
                timer.resetClock();
                this.timer_status = StatusClockManager.INITIALIZED;
                break;

            case STOP:
                if(this.timer_status != StatusClockManager.INITIALIZED || this.timer_status == StatusClockManager.UNINITIALIZED){
                    timer.terminateClock();
                }
                this.timer_status = StatusClockManager.DESTROYED;
                service.checkAndDestroy();
                service.stopSelf();
                break;
        }
        TimerActivityViewModelObservable.getInstance().getState().setTimerStatus(this.timer_status);
        TimerActivityViewModelObservable.getInstance().setStateChanged(true);
        return false;
    }

    @Override
    public boolean onTimerEvent(TimerEventObservable observable, ClockTimerEvent event) {

        boolean isStatusChanged;
        StatusClockManager oldStatus = TimerActivityViewModelObservable.getInstance().getState().getTimerStatus();

        switch (event){

            case CLOCK_INITIALIZED:
            case CLOCK_RESET_COMPLETED:
                this.timer_status = StatusClockManager.INITIALIZED;
                break;

            case CLOCK_STARTED:
            case CLOCK_RESUMED:
            case CLOCK_RESET_REQUESTED:
            case CLOCK_TICK:
            case CLOCK_SINGLE_TIMER_COMPLETED:
                this.timer_status = StatusClockManager.RUNNING;
                break;

            case CLOCK_PAUSED:
                this.timer_status = StatusClockManager.PAUSED;
                break;

            case CLOCK_SERIES_COMPLETED:
                this.timer_status = StatusClockManager.COMPLETED;
                break;

            case CLOCK_DESTROY_REQUESTED:
                break;

            case CLOCK_DESTROYED:
                this.timer_status = StatusClockManager.DESTROYED;
                break;
        }
        isStatusChanged = (oldStatus != this.timer_status);
        Log.v(LOG_TAG, CLASS_ID + " Event Recieved | isStatusChanged? " + isStatusChanged + " | status = " + this.timer_status);
        if (isStatusChanged){
            TimerActivityViewModelObservable.getInstance().getState().setTimerStatus(this.timer_status);
            TimerActivityViewModelObservable.getInstance().setStateChanged(true);
        }
        return false;
    }

    @Override
    public void mapTimerController(TimerManagerInterface timer) {
        this.timer = timer;
        Log.v(LOG_TAG, CLASS_ID + " | timer mapped");
    }

    @Override
    public void destroyController() {
        TimerControlInputObservable.getObservable().deregister(this);
        TimerEventObservable.getObservable().deregister(this);
    }
}
