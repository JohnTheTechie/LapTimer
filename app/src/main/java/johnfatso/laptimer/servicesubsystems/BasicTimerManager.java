package johnfatso.laptimer.servicesubsystems;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import johnfatso.laptimer.controller.ClockTimerEvent;
import johnfatso.laptimer.controller.TimerEventObservable;
import johnfatso.laptimer.timer.Clock;
import johnfatso.laptimer.ClockService;
import johnfatso.laptimer.ClockTimerList;
import johnfatso.laptimer.notifier.NotificationType;
import johnfatso.laptimer.timerdbms.TimerSequenceCollection;
import johnfatso.laptimer.viewmodel.TimerActivityTimerStateContainer;
import johnfatso.laptimer.viewmodel.TimerActivityViewModelObservable;

public class BasicTimerManager implements TimerManagerInterface, TimerControllerMessageReceptor {

    private static final String LOG_TAG = "TIMER";
    private static final String CLASS_ID = "BasicTimerManager";

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
    TimerActivityViewModelObservable modelObservable;
    TimerEventObservable eventObservable;

    public BasicTimerManager(String timerListName, Handler handler, ClockService service) {
        this.timerListName = timerListName;
        this.handler = null;
        this.service = service;
        this.modelObservable = TimerActivityViewModelObservable.getInstance();
        this.eventObservable = TimerEventObservable.getObservable();
        this.currentTimer = 0;
        Log.v(LOG_TAG, CLASS_ID + " | Timer manager created");
        this.initializeClock();

    }

    @Override
    public void initializeClock() {
        timerList = TimerSequenceCollection.getContainer().getSequenceContainer(timerListName).getExecutableList();
        this.handler = new TimerMessageHandler(Looper.getMainLooper(), this);
        this.currentTimer = timerList.getActiveTimer();
        Log.v(LOG_TAG, CLASS_ID + " | Timer manager initialized");
        this.updateState(NotificationType.None);
        this.eventObservable.event(ClockTimerEvent.CLOCK_INITIALIZED);
    }

    @Override
    public void startClock() {
        currentTimer = timerList.getActiveTimer();
        clock = new Clock(handler);
        clock.startClock(currentTimer);
        Log.v(LOG_TAG, CLASS_ID + " | Clock started");
        this.updateState(NotificationType.NewTimerUpdate);
        this.eventObservable.event(ClockTimerEvent.CLOCK_STARTED);
    }

    @Override
    public void pauseClock() {
        clock.stopClock();
        Log.v(LOG_TAG, CLASS_ID + " | clock paused");
        this.updateState(NotificationType.ExistingTimerUpdate);
        eventObservable.event(ClockTimerEvent.CLOCK_PAUSED);
        clock = null;
    }

    @Override
    public void resumeClock() {
        clock = new Clock(handler);
        clock.startClock(currentTimer);
        Log.v(LOG_TAG, CLASS_ID + " | Clock resumed");
        this.updateState(NotificationType.NewTimerUpdate);
        this.eventObservable.event(ClockTimerEvent.CLOCK_RESUMED);
    }

    @Override
    public void resetClock() {
        if (clock != null){
            clock.stopClock();
        }
        timerList.resetQueue();
        currentTimer = timerList.getActiveTimer();
        Log.v(LOG_TAG, CLASS_ID + " | clock reset");
        this.updateState(NotificationType.None);
        this.eventObservable.event(ClockTimerEvent.CLOCK_RESET_COMPLETED);
        clock = null;
    }

    @Override
    public void terminateClock() {
        clock.stopClock();
        Log.v(LOG_TAG, CLASS_ID + " | clock terminated");
        this.eventObservable.event(ClockTimerEvent.CLOCK_DESTROYED);
        clock = null;
    }

    @Override
    public void destroyTimerManager() {

    }

    /**
     * decrement the current timer variable
     */
    private void processTick(){
        currentTimer--;
        Log.v(LOG_TAG, CLASS_ID + " | current timer decremented | " + currentTimer);
    }

    @Override
    public void onTick() {
        processTick();
        Log.v(LOG_TAG, CLASS_ID + " | timer tick event occurred");
        this.updateState(NotificationType.ExistingTimerUpdate);
        this.eventObservable.event(ClockTimerEvent.CLOCK_TICK);
    }

    @Override
    public void onClockComplete() {
        processTick();
        timerList.pop();
        clock = null;
        if(!isTheSeriesCompleted()){
            Log.v(LOG_TAG, CLASS_ID + " | current timer completed");
            this.eventObservable.event(ClockTimerEvent.CLOCK_SINGLE_TIMER_COMPLETED);
            this.updateState(NotificationType.ExistingTimerUpdate);
            startClock();
        }else {
            Log.v(LOG_TAG, CLASS_ID + " | series completed | to be reset");
            this.updateStateOnComplete();
            timerList.resetQueue();
            currentTimer = timerList.getActiveTimer();
            Log.v(LOG_TAG, CLASS_ID + " | clock reset");
            this.eventObservable.event(ClockTimerEvent.CLOCK_SERIES_COMPLETED);
        }
    }

    private boolean isTheSeriesCompleted(){
        boolean isSeriesCompleted = timerList.getActiveTimer() == null;
        Log.v(LOG_TAG, CLASS_ID + " | series completion check | isCompleted : " + isSeriesCompleted);
        return isSeriesCompleted;
    }

    private void updateState(NotificationType notificationType){
        TimerActivityTimerStateContainer container = this.modelObservable.getState();
        container.setCurrentTimerIndex(this.timerList.getPointerPosition());
        container.setTotalTimerCount(this.timerList.size());
        container.setRemainingTimeInSeconds(this.currentTimer);
        container.setNextTimerInQueue(this.timerList.getNextTimer()==null?-1:this.timerList.getNextTimer());
        container.setNotificationType(notificationType);
        this.modelObservable.setNewState(container);
        this.modelObservable.setStateChanged(true);
    }

    private void updateStateOnComplete(){
        TimerActivityTimerStateContainer container = this.modelObservable.getState();
        container.setRemainingTimeInSeconds(this.currentTimer);
        container.setNextTimerInQueue(this.timerList.getNextTimer()==null?-1:this.timerList.getNextTimer());
        container.setNotificationType(NotificationType.ExistingTimerUpdate);
        this.modelObservable.setNewState(container);
        this.modelObservable.setStateChanged(true);
    }
}
