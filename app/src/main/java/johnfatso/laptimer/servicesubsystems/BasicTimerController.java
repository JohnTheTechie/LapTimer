package johnfatso.laptimer.servicesubsystems;

import android.os.Handler;
import android.os.Looper;

import johnfatso.laptimer.status.StatusClockActivity;
import johnfatso.laptimer.timer.Clock;
import johnfatso.laptimer.ClockService;
import johnfatso.laptimer.ClockTimerList;
import johnfatso.laptimer.TimerPersistanceContainer;
import johnfatso.laptimer.notifier.NotificationType;

public class BasicTimerController implements TimerControllerInterface, TimerControllerMessageReceptor {

    //Clock thread instance
    Clock clock;
    //list of the timers to run
    ClockTimerList timerList;
    String timerListName;
    //current remaining time in seconds
    long currentTimer;
    //Handler for receiving the inputs from Clock thread
    Handler handler;
    ClockService service;

    public BasicTimerController(String timerListName, Handler handler, ClockService service) {
        this.timerListName = timerListName;
        this.handler = null;
        this.service = service;
    }

    @Override
    public void initializeClock() {
        timerList = TimerPersistanceContainer.getContainer().getTimerBox(timerListName).getExecutableTimerList();
        this.handler = new TimerMessageHandler(Looper.getMainLooper(), this);
    }

    @Override
    public void startClock() {
        currentTimer = timerList.getActiveTimer();
        clock = new Clock(handler);
        clock.startClock(currentTimer);
        service.updateHmiComponents(currentTimer, NotificationType.NewTimerUpdate, timerList);
    }

    @Override
    public void pauseClock() {
        clock.stopClock();
        clock = null;
    }

    @Override
    public void resumeClock() {
        clock = new Clock(handler);
        clock.startClock(currentTimer);
        service.updateHmiComponents(currentTimer, NotificationType.ExistingTimerUpdate, timerList);
    }

    @Override
    public void resetClock() {
        timerList.resetQueue();
    }

    @Override
    public void terminateClock() {
        clock.stopClock();
        clock = null;
    }

    /**
     * decrement the current timer variable
     */
    private void processTick(){
        currentTimer--;
    }

    @Override
    public void onTick() {
        processTick();
        service.updateHmiComponents(currentTimer, NotificationType.ExistingTimerUpdate, timerList);
    }

    @Override
    public void onClockComplete() {
        processTick();
        service.updateHmiComponents(currentTimer, NotificationType.CompletionUpdate, timerList);
        timerList.pop();
        clock = null;
        if(!isTheSeriesCompleted()){
            startClock();
        }else {
            service.updateActivityStatus(StatusClockActivity.COMPLETED);
            //serviceStatusController.set_timer_status(StatusClockServiceEnum.COMPLETED);
            // TODO: update to state controller about the completion of the clock timer
            service.updateHmiComponents("Timer Completed", NotificationType.MessageUpdate);
            resetClock();
        }
    }

    private boolean isTheSeriesCompleted(){
        return timerList.getActiveTimer() == null;
    }
}
