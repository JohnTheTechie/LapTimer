package johnfatso.laptimer.notifier;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class BasicNotificationController implements NotificationControllerInterface{

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    @Override
    public NotificationControllerInterface build(NotificationManager manager, NotificationChannel channel, Context context ) {
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
        }

        return this;
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
}
