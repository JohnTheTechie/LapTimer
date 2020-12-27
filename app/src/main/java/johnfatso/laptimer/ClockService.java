package johnfatso.laptimer;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import johnfatso.laptimer.notifier.BasicNotificationManager;
import johnfatso.laptimer.notifier.NotificationManagerInterface;
import johnfatso.laptimer.notifier.TimerNotificationChannelContainer;
import johnfatso.laptimer.servicesubsystems.BasicTimerManager;
import johnfatso.laptimer.servicesubsystems.BasicTimerStatusManager;
import johnfatso.laptimer.servicesubsystems.TimerManagerInterface;
import johnfatso.laptimer.status.ClockStatusManagerInterface;
import johnfatso.laptimer.status.StatusClockManager;

public class ClockService extends Service {

    // log tag for logger
    private static final String LOG_TAG = "TAG_SERVICE";
    private static final String CLASS_ID = "ClockService";

    //string identifier for timer list to register in an intent
    static final String CLOCK_TIMER_LIST = "timerlist";

    //current status of the service
    StatusClockManager status;

    //activity active?
    boolean isActivityActive;

    //Control interfaces
    ClockStatusManagerInterface serviceStatusManager;
    TimerManagerInterface timerManager;
    NotificationManagerInterface notificationManager;

    String timerNameID;

    //Binder for binding with activity
    private final IBinder binder = new ClockBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "ClockService | Service created | status : "+status+" | serviceID : "+this);
        serviceStatusManager = new BasicTimerStatusManager(timerManager, this);
        timerNameID = null;
        this.isActivityActive = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "ClockService | Bind initiated | status : "+status+" | serviceID : "+this);
        this.isActivityActive = true;
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "ClockService | unbind initiated | status : "+status+" | serviceID : "+this);
        this.isActivityActive = false;
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(LOG_TAG, "ClockService | service start initiated | status : "+status+" | serviceID : "+this);
        String timerRequested = intent.getStringExtra(ClockService.CLOCK_TIMER_LIST);
        if (isNewTimerRequested(timerRequested)){
            if (this.timerNameID != null){
                this.destroyExpiredControllers();
                serviceStatusManager = new BasicTimerStatusManager(timerManager, this);
            }
            timerManager = new BasicTimerManager(timerRequested, null, this);
            serviceStatusManager.mapTimerController(timerManager);
            notificationManager = new BasicNotificationManager().build(getSystemService(NotificationManager.class),
                    TimerNotificationChannelContainer.getInstance().getChannel(), this);
        }
        if (this.timerNameID == null) {
            startForeground(1254, notificationManager.getNotification("Timer prepared! Get prepared to start!"));
        }
        this.timerNameID = timerRequested;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.v(LOG_TAG, CLASS_ID + "service destroyed");
        super.onDestroy();
    }

    private boolean isNewTimerRequested(String timerName){
        return !timerName.equals(this.timerNameID);
    }

    private void destroyExpiredControllers(){
        serviceStatusManager.destroyController();
        notificationManager.destroyNotificationManager();
        timerManager.destroyTimerManager();
    }

    public void checkAndDestroy(){
        if(!this.isActivityActive) {
            destroyExpiredControllers();
            Log.v(LOG_TAG, CLASS_ID + "service destroy requested");
            this.stopForeground(true);
            this.stopSelf();
        }
    }

    /**
     * Binder class for service binding
     */
    class ClockBinder extends Binder {
        ClockService getService(){
            Log.v(LOG_TAG, "ClockService | service retrieved | status : " + status + " | serviceID : "+ClockService.this);
            return ClockService.this;
        }
    }
}
