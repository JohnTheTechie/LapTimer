package johnfatso.laptimer.servicesubsystems;

import johnfatso.laptimer.ClockControlCommand;
import johnfatso.laptimer.ClockService;
import johnfatso.laptimer.status.ClockStatusControllerInterface;
import johnfatso.laptimer.status.StatusActivityAttachmentEnum;
import johnfatso.laptimer.status.StatusClockActivity;
import johnfatso.laptimer.status.StatusClockServiceEnum;

public class BasicTimerStatusController implements ClockStatusControllerInterface {

    private StatusActivityAttachmentEnum activity_status;
    private StatusClockServiceEnum timer_status;

    private TimerControllerInterface timer;
    private ClockService service;

    public BasicTimerStatusController(ClockService service) {
        this.activity_status = StatusActivityAttachmentEnum.ACTIVITY_DETACHED;
        this.timer_status = StatusClockServiceEnum.UNINITIALIZED;
        this.timer = null;
        this.service =service;
    }

    public BasicTimerStatusController(TimerControllerInterface timer, ClockService service) {
        this.activity_status = StatusActivityAttachmentEnum.ACTIVITY_DETACHED;
        this.timer_status = StatusClockServiceEnum.UNINITIALIZED;
        this.timer = timer;
        this.service = service;
    }

    @Override
    public void processActivityStatus(StatusClockActivity activityStatus) {
        if(activityStatus != null){
            if (activityStatus == StatusClockActivity.RUNNING){
                //status = StatusClockService.RUNNING_ACTIVITY_ATTACHED;
                setActivityAttachedStatus(true);
                set_timer_status(StatusClockServiceEnum.RUNNING);
            }
            else if(activityStatus == StatusClockActivity.PAUSED){
                //status = StatusClockService.PAUSED_ACTIVITY_ATTACHED;
                setActivityAttachedStatus(true);
                set_timer_status(StatusClockServiceEnum.PAUSED);
            }
            else {
                timer_status = StatusClockServiceEnum.INITIALIZED;
                timer.initializeClock();
            }
        }
    }

    @Override
    public StatusActivityAttachmentEnum get_activity_status() {
        return this.activity_status;
    }

    @Override
    public void set_timer_status(StatusClockServiceEnum service_status) {
        this.timer_status = service_status;
    }

    @Override
    public StatusClockServiceEnum get_timer_status() {
        return timer_status;
    }

    @Override
    public void setActivityAttachedStatus(boolean isAttached) {
        if(isAttached){
            this.activity_status = StatusActivityAttachmentEnum.ACTIVITY_ATTACHED;
        }
        else {
            this.activity_status = StatusActivityAttachmentEnum.ACTIVITY_DETACHED;
        }
    }

    @Override
    public void processActionCommand(ClockControlCommand command) {
        switch (command){

            case START:
                if(this.timer_status == StatusClockServiceEnum.INITIALIZED ) {
                    timer.startClock();
                }else if(this.timer_status == StatusClockServiceEnum.PAUSED){
                    timer.resumeClock();
                }
                else {
                    throw new IllegalStateException("Start called on a running clock service");
                }
                this.timer_status = StatusClockServiceEnum.RUNNING;
                this.activity_status = StatusActivityAttachmentEnum.ACTIVITY_ATTACHED;
                service.updateActivityStatus(StatusClockActivity.RUNNING);
                break;

            case PAUSE:
                if(this.timer_status == StatusClockServiceEnum.RUNNING) {
                    timer.pauseClock();
                }else {
                    throw new IllegalStateException("Pause called on an Idle service");
                }
                this.timer_status = StatusClockServiceEnum.PAUSED;
                this.activity_status = StatusActivityAttachmentEnum.ACTIVITY_ATTACHED;
                service.updateActivityStatus(StatusClockActivity.PAUSED);
                break;

            case RESET:
                timer.resetClock();
                this.timer_status = StatusClockServiceEnum.INITIALIZED;
                this.activity_status = StatusActivityAttachmentEnum.ACTIVITY_ATTACHED;
                service.updateActivityStatus(StatusClockActivity.REINITIALIZED);
                break;

            case STOP:
                if(this.timer_status != StatusClockServiceEnum.INITIALIZED ){
                    /*clock.stopClock();
                    clock = null;*/
                    timer.terminateClock();
                }
                this.timer_status = StatusClockServiceEnum.DESTROYED;
                service.stopSelf();
        }
    }

    @Override
    public void mapTimerController(TimerControllerInterface timer) {
        this.timer = timer;
    }
}
