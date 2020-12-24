package johnfatso.laptimer;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import johnfatso.laptimer.notifier.BasicNotificationController;
import johnfatso.laptimer.notifier.NotificationControllerInterface;
import johnfatso.laptimer.notifier.TimerNotificationChannelContainer;
import johnfatso.laptimer.servicesubsystems.BasicTimerController;
import johnfatso.laptimer.servicesubsystems.BasicTimerStatusController;
import johnfatso.laptimer.servicesubsystems.Converters;
import johnfatso.laptimer.servicesubsystems.TimerControllerInterface;
import johnfatso.laptimer.status.ClockStatusControllerInterface;
import johnfatso.laptimer.status.StatusActivityAttachmentEnum;
import johnfatso.laptimer.status.StatusClockActivity;
import johnfatso.laptimer.status.StatusClockService;
import johnfatso.laptimer.notifier.NotificationType;
import johnfatso.laptimer.status.StatusClockServiceEnum;

public class ClockService extends Service {

    // log tag for logger
    private static final String LOG_TAG = "TAG_SERVICE";

    //string identifier for timer list to register in an intent
    static final String CLOCK_TIMER_LIST = "timerlist";

    //current status of the service
    StatusClockService status;

    //Control interfaces
    ClockStatusControllerInterface serviceStatusController;
    TimerControllerInterface timerController;
    NotificationControllerInterface notificationController;

    ClockActivity activity;

    //Binder for binding with activity
    private final IBinder binder = new ClockBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "ClockService | Service created | status : "+status+" | serviceID : "+this);
        serviceStatusController = new BasicTimerStatusController(timerController, this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "ClockService | Bind initiated | status : "+status+" | serviceID : "+this);
        serviceStatusController.setActivityAttachedStatus(true);

        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "ClockService | unbind initiated | status : "+status+" | serviceID : "+this);
        serviceStatusController.setActivityAttachedStatus(false);

        activity = null;
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(LOG_TAG, "ClockService | service start initiated | status : "+status+" | serviceID : "+this);
        timerController = new BasicTimerController(intent.getStringExtra(ClockService.CLOCK_TIMER_LIST), null, this);
        serviceStatusController.mapTimerController(timerController);
        StatusClockActivity activityStatus = (StatusClockActivity) intent.getSerializableExtra(ClockActivity.STATUS);
        serviceStatusController.processActivityStatus(activityStatus);
        if(serviceStatusController.get_timer_status() == StatusClockServiceEnum.INITIALIZED){
            notificationController = new BasicNotificationController().build(getSystemService(NotificationManager.class),
                    TimerNotificationChannelContainer.getInstance().getChannel(), this);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * registers the calling activity to service
     * @param activity calling activity's ref
     */
    public void registerActivity(ClockActivity activity){
        Log.v(LOG_TAG,
                "ClockService | activity registered | status : "+status+" | serviceID : "+this);
        this.activity = activity;
        this.serviceStatusController.setActivityAttachedStatus(true);
    }

    /**
     * updates the status of the activity whenever event occurs in service
     */
    public void updateActivityStatus(StatusClockActivity statusClockActivity){
        activity.setStatus(statusClockActivity);
    }

    /**
     * function to pass clock control commands from activity to service
     *
     * @param control_action command constant
     */
    public void clock_control_input(ClockControlCommand control_action){
        serviceStatusController.processActionCommand(control_action);
    }

    /**
     * checks condition and update the activity and the notification
     * @param timer timer duration to be posted
     */
    public void updateHmiComponents(long timer,
                                    NotificationType type,
                                    ClockTimerList timerList){
        String time_string = Converters.timer_to_time_string(timer);
        notificationController.update_notification(time_string, type);
        if(serviceStatusController.get_activity_status() ==
                StatusActivityAttachmentEnum.ACTIVITY_ATTACHED && activity != null){
            updateActivityHmiElements(timer, timerList);
        }
        Log.v(LOG_TAG, "update posted | status : "+status+" | serviceID : "+this);
    }

    /**
     * checks condition and update the activity and the notification
     * @param message message to be posted
     */
    public void updateHmiComponents(String message, NotificationType type){
        notificationController.update_notification(message, type);
        Log.v(LOG_TAG, "update posted | status : "+status+" | serviceID : "+this);
    }

    /**
     * update the timers and indicators in the activity
     */
    private void updateActivityHmiElements(long currentTimer, ClockTimerList timerList){
        activity.setMain_timer(Converters.timer_to_time_string(currentTimer));

        int remainingTimerCount = timerList.size() - timerList.getPointerPosition() - 1;
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
     * Binder class for service binding
     */
    class ClockBinder extends Binder {
        ClockService getService(){
            Log.v(LOG_TAG,
                    "ClockService | service retrieved | status : " + status +
                            " | serviceID : "+ClockService.this);
            return ClockService.this;
        }
    }
}
