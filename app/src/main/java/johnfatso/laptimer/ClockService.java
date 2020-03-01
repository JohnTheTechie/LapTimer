package johnfatso.laptimer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.util.Timer;

import johnfatso.laptimer.status.StatusClockActivity;
import johnfatso.laptimer.status.StatusClockService;

public class ClockService extends Service {

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

    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;

    ClockActivity activity;

    //Binder for binding with activity
    private final IBinder binder = new ClockBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        this.prepareHandler();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(status == StatusClockService.RUNNING_ACTIVITY_DISCONNECTED) status = StatusClockService.RUNNING_ACTIVITY_CONNECTED;
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(status == StatusClockService.RUNNING_ACTIVITY_CONNECTED) status = StatusClockService.RUNNING_ACTIVITY_DISCONNECTED;
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timerListName = intent.getStringExtra(ClockService.CLOCK_TIMER_LIST);
        TimerPersistanceContainer.getContainer().prepareDummyData();
        timerList = TimerPersistanceContainer.getContainer().getTimerBox("dummy").getTimerList();
        //TODO: change to get the content from TimerPersistnaceContainer

        clock = new Clock(handler);

        status = StatusClockService.INITIALIZED;

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Prepare the service with intent
     */
    private void prepareService(Intent intent){

    }

    /**
     * prepare the notification channel for passing notification
     */
    void prepareForNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Timer_Notification";
            String description = "Notification for displaying timer alerts";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("timer_channel", name, importance);
            channel.setDescription(description);

            notificationManager = getSystemService(NotificationManager.class);
            if(this.notificationManager != null)
                notificationManager.createNotificationChannel(channel);
            else
                throw new IllegalStateException("notification manager is null");

            this.notificationBuilder = new NotificationCompat.Builder(this, channel.getId())
                    .setSmallIcon(android.R.drawable.alert_dark_frame)
                    .setContentText("00:00")
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            notificationManager.notify(0x1111, notificationBuilder.build());
        }
    }

    /**
     * function to pass clock control commands from activity to service
     *
     * @param control_action command constant
     */
    public void clock_control_input(ClockControlCommand control_action){
        switch (control_action){

            case START:
                if(this.status == StatusClockService.INITIALIZED){
                    startClock();
                    activity.setStatus(StatusClockActivity.RUNNING);
                }else if(status == StatusClockService.PAUSED_ACTIVITY_DETACHED || status == StatusClockService.PAUSED_ACTIVITY_ATTACHED){
                    resumeClock();
                    activity.setStatus(StatusClockActivity.RUNNING);
                }
                else {
                    throw new IllegalStateException("Start called on a running clock service");
                }
                //TODO:intimate activity
                break;

            case PAUSE:
                if(status == StatusClockService.RUNNING_ACTIVITY_CONNECTED || status == StatusClockService.RUNNING_ACTIVITY_DISCONNECTED){
                    pauseClock();
                    activity.setStatus(StatusClockActivity.PAUSED);
                }else {
                    throw new IllegalStateException("Pause called on an Idle service");
                }
                //TODO:intimate activity
                break;

            case RESET:
                resetClock();
                activity.setStatus(StatusClockActivity.REINITIALIZED);
                break;

        }
    }

    /**
     * decrement the current timer variable
     */
    private void decrementCurrentTimerAndUpdateHMI(){
        currentTimer--;
        updateHmiComponents(currentTimer);
    }

    /**
     * checks if the timer list has reached the end
     */
    private boolean isTheSeriesCompleted(){
        if(timerList.getActiveTimer()!=null)
            return true;
        else return false;
    }

    /**
     * start an idle clock
     */
    private void startClock(){
        if(activity == null) status = StatusClockService.RUNNING_ACTIVITY_DISCONNECTED;
        else status = StatusClockService.RUNNING_ACTIVITY_CONNECTED;

        prepareForNotification();

        currentTimer = timerList.getActiveTimer();
        clock.startClock(timerList.getActiveTimer());
        updateHmiComponents(currentTimer);
        Toast.makeText(this,"clock started", Toast.LENGTH_SHORT).show();
    }

    /**
     * create and start a new clock thread
     */

    private void prepareAndRunFreshClockThread(long durationForClockToRun){
        if(clock != null)
            clock.stopClock();
        currentTimer = durationForClockToRun;
        clock = new Clock(handler);
        clock.startClock(durationForClockToRun);
    }

    /**
     * resume he paused clock
     */
    private void resumeClock(){
        if(activity == null) status = StatusClockService.RUNNING_ACTIVITY_DISCONNECTED;
        else status = StatusClockService.RUNNING_ACTIVITY_CONNECTED;
        clock.resumeClock();
        updateHmiComponents(currentTimer);
    }

    /**
     * pause the running clock
     */
    private void pauseClock(){
        if(this.activity != null)
            status = StatusClockService.PAUSED_ACTIVITY_ATTACHED;
        else
            status = StatusClockService.PAUSED_ACTIVITY_DETACHED;
        clock.pauseClock();
    }

    /**
     * resets the series clock to start again
     */
    private void resetClock(){
        clock.stopClock();
        clock = new Clock(handler);
        status = StatusClockService.INITIALIZED;
        timerList.resetQueue();
    }

    /**
     * checks condition and update the activity and the notification
     * @param timer timer duration to be posted
     */
    private void updateHmiComponents(long timer){
        String time_string = convert_timer_to_time_string(timer);
        updateNotification(time_string);
        if(status == StatusClockService.RUNNING_ACTIVITY_CONNECTED && activity != null){
            Log.v(LOG_TAG, "Activity connected and update called");
            updateActivity();
        }
        Log.v(LOG_TAG, "update posted");
    }

    /**
     * change the current timer value in the notification
     * @param timer string to display in the notification
     */
    private void updateNotification(String timer){
        notificationBuilder.setContentText(timer);
        notificationManager.notify(0x1111, notificationBuilder.build());
    }

    /**
     * update the timers and indicators in the activity
     */
    private void updateActivity(){
        activity.setMain_timer(convert_timer_to_time_string(currentTimer));
        if(timerList.getNextTimer()!=null)
            activity.setNext_timer(convert_timer_to_time_string(timerList.getNextTimer()));
        else
            activity.setNext_timer("--:--");
        Log.v(LOG_TAG, "Activity updated");
    }

    /**
     * registers the calling activity to service
     * @param activity calling activity's ref
     */
    public void registerActivity(ClockActivity activity){
        this.activity = activity;
        //once the activity is registered, setup is complete and timer is started
        Log.v(LOG_TAG, "activity registered");
    }


    /**
     * define handler for receiving communication from clock thread
     */
    private void prepareHandler(){
        this.handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case Clock.CLOCK_MESSAGE_TICK:
                        ClockService.this.onTick();
                        break;

                    case Clock.CLOCK_MESSAGE_COMPLETE:
                        ClockService.this.onClockComplete();
                        break;

                }
            }
        };
    }

    /**
     * called when a tick is received
     */
    private void onTick(){
        decrementCurrentTimerAndUpdateHMI();
    }

    /**
     * called when the one timer is completed
     */
    private void onClockComplete(){
        decrementCurrentTimerAndUpdateHMI();
        clock.resetClock();
        timerList.pop();
        if(isTheSeriesCompleted()){
            prepareAndRunFreshClockThread(timerList.getActiveTimer());
        }else {
            //TODO: communicate to Activity if active
            stopSelf();
        }
        Log.v(LOG_TAG, "service completed processed");
    }

    /**
     * convert timer long value into String so that it can be shown in Notification and in the activity
     * @param timer time value to be converted
     * @return Timer in string form
     */
    private String convert_timer_to_time_string(long timer){
        long minutes, seconds;
        String seconds_string;
        if(timer < 3600){
            minutes = timer/60;
            seconds = timer%60;
            if(seconds<10) seconds_string = "0"+seconds;
            else seconds_string = ""+seconds;
            return ""+minutes+":"+seconds_string;
        }
        else throw new IllegalStateException("timer exceeds an hour");
    }

    ClockTimerList prepareDummyList(){
        ClockTimerList list = new ClockTimerList();
        list.add((long) 20);
        list.add((long) 15);
        list.add((long) 10);
        list.add((long) 5);
        return list;
    }

    /**
     * Binder class for service cbinding
     */
    public class ClockBinder extends Binder {
        ClockService getService(){
            Log.v(LOG_TAG, "Service ref recovered");
            return ClockService.this;
        }
    }
}
