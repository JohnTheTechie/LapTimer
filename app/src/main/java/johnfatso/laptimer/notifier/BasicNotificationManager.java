package johnfatso.laptimer.notifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaDrm;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import johnfatso.laptimer.controller.ClockTimerEvent;
import johnfatso.laptimer.controller.OnTimerEventListener;
import johnfatso.laptimer.controller.TimerEventObservable;
import johnfatso.laptimer.servicesubsystems.Converters;
import johnfatso.laptimer.viewmodel.ModelUpdateObservable;
import johnfatso.laptimer.viewmodel.ModelUpdateObserver;
import johnfatso.laptimer.viewmodel.TimerActivityTimerStateContainer;
import johnfatso.laptimer.viewmodel.TimerActivityViewModelObservable;

public class BasicNotificationManager implements NotificationManagerInterface, ModelUpdateObserver<TimerActivityTimerStateContainer>, OnTimerEventListener {

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private Notification notification;

    @Override
    public NotificationManagerInterface build(NotificationManager manager, NotificationChannel channel, Context context ) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            this.notificationManager = manager;

            if(this.notificationManager != null)
                notificationManager.createNotificationChannel(channel);
            else
                throw new IllegalStateException("notification manager is null");

            this.notificationBuilder = new NotificationCompat.Builder(context, channel.getId())
                    .setSmallIcon(android.R.drawable.alert_dark_frame)
                    .setContentText("00:00")
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            this.notification = this.notificationBuilder.build();
        }

        TimerActivityViewModelObservable.getInstance().register(this);
        TimerEventObservable.getObservable().register(this);

        return this;
    }

    @Override
    public void onViewModelUpdated(ModelUpdateObservable<TimerActivityTimerStateContainer> observable) {
        TimerActivityTimerStateContainer state = observable.getState();
        this.update_notification(Converters.timer_to_time_string(state.getRemainingTimeInSeconds()), state.getNotificationType());
    }

    @Override
    public boolean onTimerEvent(TimerEventObservable observable, ClockTimerEvent event) {
        switch (event){
            case CLOCK_SERIES_COMPLETED:
                this.update_notification("Timer Completed!", NotificationType.MessageUpdate);
                break;

            case CLOCK_DESTROYED:
                this.update_notification("Timer aborted", NotificationType.MessageUpdate);
                break;
        }

        return false;
    }

    @Override
    public void update_notification(String message, NotificationType type) {
        Log.v("NOTIFICATIONS", "Notification | type : "+type+" | message : "+message);
        notificationBuilder.setContentText(message);

        //check if chime needed
        notificationBuilder.setOnlyAlertOnce(type == NotificationType.ExistingTimerUpdate);

        notificationManager.notify(0x1111, notificationBuilder.build());
        notificationBuilder.setOnlyAlertOnce(true);
    }

    @Override
    public Notification getNotification(CharSequence message) {
        return notificationBuilder.setContentText(message).build();
    }

    @Override
    public void destroyNotificationManager() {
        TimerActivityViewModelObservable.getInstance().deregister(this);
        TimerEventObservable.getObservable().deregister(this);
    }
}
