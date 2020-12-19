package johnfatso.laptimer.notifier;

import android.app.NotificationChannel;
import android.app.NotificationManager;

public class TimerNotificationChannelContainer {

    private static final TimerNotificationChannelContainer ourInstance = new TimerNotificationChannelContainer();
    private final NotificationChannel channel;

    public static TimerNotificationChannelContainer getInstance() {
        return ourInstance;
    }

    private TimerNotificationChannelContainer() {
        String channel_id = "timer_channel";
        CharSequence channel_name = "Timer_Notification";
        int notification_importance = NotificationManager.IMPORTANCE_HIGH;
        String description = "Notification for displaying timer alerts";

        channel = new NotificationChannel(channel_id, channel_name, notification_importance);
        channel.setDescription(description);
    }

    public NotificationChannel getChannel(){
        return this.channel;
    }
}
