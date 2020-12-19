package johnfatso.laptimer.notifier;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

public interface NotificationControllerInterface {
    NotificationControllerInterface build(NotificationManager manager, NotificationChannel channel, Context context);
    void update_notification(String message, NotificationType type);
}
