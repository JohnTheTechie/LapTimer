package johnfatso.laptimer.status;

import johnfatso.laptimer.ClockControlCommand;
import johnfatso.laptimer.servicesubsystems.TimerControllerInterface;

public interface ClockStatusControllerInterface {
    void processActivityStatus(StatusClockActivity activity_status);
    StatusActivityAttachmentEnum get_activity_status();
    void set_timer_status(StatusClockServiceEnum service_status);
    StatusClockServiceEnum get_timer_status();
    void setActivityAttachedStatus(boolean isAttached);
    void processActionCommand(ClockControlCommand command);
    void mapTimerController(TimerControllerInterface timer);
}
