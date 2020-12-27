package johnfatso.laptimer.notifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

public interface NotificationManagerInterface {
    NotificationManagerInterface build(NotificationManager manager, NotificationChannel channel, Context context);
    void update_notification(String message, NotificationType type);
    Notification getNotification(CharSequence message);
    void destroyNotificationManager();
}
