package johnfatso.laptimer;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import johnfatso.laptimer.notifier.BasicNotificationController;
import johnfatso.laptimer.notifier.NotificationControllerInterface;
import johnfatso.laptimer.notifier.TimerNotificationChannelContainer;
import johnfatso.laptimer.servicesubsystems.Converters;
import johnfatso.laptimer.servicesubsystems.TimerControllerMessageReceptor;
import johnfatso.laptimer.servicesubsystems.TimerMessageHandler;
import johnfatso.laptimer.status.StatusClockActivity;
import johnfatso.laptimer.status.StatusClockService;
import johnfatso.laptimer.notifier.NotificationType;

public class ClockService extends Service implements TimerControllerMessageReceptor {

    // log tag for logger
    private static final String LOG_TAG = "TAG_SERVICE";

    //string identifier for timer list to register in an intent
    static final String CLOCK_TIMER_LIST = "timerlist";

    //current status of the service
    StatusClockService status;

    //Clock thread instance
    Clock clock;
    //list of the timers to run
    ClockTimerList timerList;
    String timerListName;
    //current remaining time in seconds
    long currentTimer;

    //Handler for receiving the inputs from Clock thread
    Handler handler;

    NotificationControllerInterface notificationController;

    ClockActivity activity;

    //Binder for binding with activity
    private final IBinder binder = new ClockBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "ClockService | Service created | status : "+status+" | serviceID : "+this);

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "ClockService | Bind initiated | status : "+status+" | serviceID : "+this);
        if(status == StatusClockService.RUNNING_ACTIVITY_DETACHED) status = StatusClockService.RUNNING_ACTIVITY_ATTACHED;
        else if(status == StatusClockService.PAUSED_ACTIVITY_DETACHED) status = StatusClockService.PAUSED_ACTIVITY_ATTACHED;
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "ClockService | unbind initiated | status : "+status+" | serviceID : "+this);
        if(status == StatusClockService.RUNNING_ACTIVITY_ATTACHED) status = StatusClockService.RUNNING_ACTIVITY_DETACHED;
        else if(status == StatusClockService.PAUSED_ACTIVITY_ATTACHED) status = StatusClockService.PAUSED_ACTIVITY_DETACHED;
        activity = null;
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(LOG_TAG, "ClockService | service start initiated | status : "+status+" | serviceID : "+this);
        StatusClockActivity activityStatus = (StatusClockActivity) intent.getSerializableExtra(ClockActivity.STATUS);
        if(activityStatus != null){
            if (activityStatus == StatusClockActivity.RUNNING){
                status = StatusClockService.RUNNING_ACTIVITY_ATTACHED;
            }
            else if(activityStatus == StatusClockActivity.PAUSED){
                status = StatusClockService.PAUSED_ACTIVITY_ATTACHED;
            }
            else {
                status = StatusClockService.INITIALIZED;
                timerListName = intent.getStringExtra(ClockService.CLOCK_TIMER_LIST);
                timerList = TimerPersistanceContainer.getContainer().getTimerBox(timerListName).getExecutableTimerList();
                this.handler = new TimerMessageHandler(Looper.getMainLooper(), this);
                prepareForNotification();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * registers the calling activity to service
     * @param activity calling activity's ref
     */
    public void registerActivity(ClockActivity activity){
        Log.v(LOG_TAG, "ClockService | activity registered | status : "+status+" | serviceID : "+this);
        this.activity = activity;
        switch (status){
            case RUNNING_ACTIVITY_DETACHED:
                status = StatusClockService.RUNNING_ACTIVITY_ATTACHED;
                break;

            case PAUSED_ACTIVITY_DETACHED:
                status = StatusClockService.PAUSED_ACTIVITY_ATTACHED;
                break;
        }
        //once the activity is registered, setup is complete and timer is started
        Log.v(LOG_TAG, "activity registered | new service status : "+status+" | serviceID : "+this);
    }

    /**
     * updates the status of the activity whenever event occurs in service
     */
    private void updateActivityStatus(StatusClockActivity statusClockActivity){
        activity.setStatus(statusClockActivity);
    }

    /**
     * prepare the notification channel for passing notification
     */
    void prepareForNotification(){
        notificationController = new BasicNotificationController().build(getSystemService(NotificationManager.class),
                TimerNotificationChannelContainer.getInstance().getChannel(), this);
    }

    /**
     * function to pass clock control commands from activity to service
     *
     * @param control_action command constant
     */
    public void clock_control_input(ClockControlCommand control_action){

        switch (control_action){

            case START:
                if(this.status == StatusClockService.INITIALIZED ) {
                    startClock();
                }else if(status == StatusClockService.PAUSED_ACTIVITY_DETACHED || status == StatusClockService.PAUSED_ACTIVITY_ATTACHED){
                    resumeClock();
                }
                else {
                    throw new IllegalStateException("Start called on a running clock service");
                }
                this.status = StatusClockService.RUNNING_ACTIVITY_ATTACHED;
                updateActivityStatus(StatusClockActivity.RUNNING);
                break;

            case PAUSE:
                if(status == StatusClockService.RUNNING_ACTIVITY_ATTACHED || status == StatusClockService.RUNNING_ACTIVITY_DETACHED){
                    clock.stopClock();
                    clock = null;
                }else {
                    throw new IllegalStateException("Pause called on an Idle service");
                }
                this.status = StatusClockService.PAUSED_ACTIVITY_ATTACHED;
                updateActivityStatus(StatusClockActivity.PAUSED);
                break;

            case RESET:
                resetClock();
                updateActivityStatus(StatusClockActivity.REINITIALIZED);
                break;

            case STOP:
                if(this.status != StatusClockService.INITIALIZED ){
                    clock.stopClock();
                    clock = null;
                }
                this.status = StatusClockService.DESTROYED;
                stopSelf();
        }
    }

    /**
     * start an idle clock
     */
    private void startClock(){
        currentTimer = timerList.getActiveTimer();
        clock = new Clock(handler);
        clock.startClock(currentTimer);
        updateHmiComponents(currentTimer, NotificationType.NewTimerUpdate);
    }

    private void resumeClock(){
        clock = new Clock(handler);
        clock.startClock(currentTimer);
        updateHmiComponents(currentTimer, NotificationType.ExistingTimerUpdate);
    }

    /**
     * resets the series clock to start again
     */
    private void resetClock(){
        status = StatusClockService.INITIALIZED;
        timerList.resetQueue();
    }

    /**
     * decrement the current timer variable
     */
    private void processTick(){
        currentTimer--;
    }

    /**
     * checks if the timer list has reached the end
     */
    private boolean isTheSeriesCompleted(){
        return timerList.getActiveTimer() == null;
    }

    /**
     * checks condition and update the activity and the notification
     * @param timer timer duration to be posted
     */
    private void updateHmiComponents(long timer, NotificationType type){
        String time_string = Converters.timer_to_time_string(timer);
        //updateNotification(time_string, type);
        notificationController.update_notification(time_string, type);
        if(status == StatusClockService.RUNNING_ACTIVITY_ATTACHED && activity != null){
            updateActivityHmiElements();
        }
        Log.v(LOG_TAG, "update posted | status : "+status+" | serviceID : "+this);
    }

    /**
     * update the timers and indicators in the activity
     */
    private void updateActivityHmiElements(){
        activity.setMain_timer(Converters.timer_to_time_string(currentTimer));

        int remainingTimerCount = timerList.size() - timerList.getPointerPosition()-1;
        int expiredTimerCount = timerList.getPointerPosition();

        activity.setNext_counter(remainingTimerCount+"");
        activity.setPrev_counter(expiredTimerCount+"");

        if(timerList.getNextTimer()!=null)
            activity.setNext_timer(Converters.timer_to_time_string(timerList.getNextTimer()));
        else
            activity.setNext_timer("--:--");
        Log.v(LOG_TAG, "Activity updated | status : "+status);
    }

    /**
     * called when a tick is received
     */
    @Override
    public void onTick(){
        processTick();
        updateHmiComponents(currentTimer, NotificationType.ExistingTimerUpdate);
    }

    /**
     * called when the one timer is completed
     */
    @Override
    public void onClockComplete(){
        processTick();
        updateHmiComponents(currentTimer, NotificationType.CompletionUpdate);
        timerList.pop();
        clock = null;
        if(!isTheSeriesCompleted()){
            startClock();
        }else {
            updateActivityStatus(StatusClockActivity.COMPLETED);
            this.status = StatusClockService.COMPLETED;
            notificationController.update_notification("Timer Completed", NotificationType.MessageUpdate);
            resetClock();
        }
        Log.v(LOG_TAG, "service completed processed | status : "+status);
    }

    /**
     * Binder class for service binding
     */
    class ClockBinder extends Binder {
        ClockService getService(){
            Log.v(LOG_TAG, "ClockService | service retrieved | status : "+status+" | serviceID : "+ClockService.this);
            return ClockService.this;
        }
    }
}
