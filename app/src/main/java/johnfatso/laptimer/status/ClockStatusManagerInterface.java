package johnfatso.laptimer.status;

import johnfatso.laptimer.servicesubsystems.TimerManagerInterface;

public interface ClockStatusManagerInterface {
    void set_timer_status(StatusClockManager service_status);
    StatusClockManager get_timer_status();
    void mapTimerController(TimerManagerInterface timer);
    void destroyController();
}
