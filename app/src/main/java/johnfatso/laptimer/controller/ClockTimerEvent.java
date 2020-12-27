package johnfatso.laptimer.controller;

public enum ClockTimerEvent {
    CLOCK_INITIALIZED,
    CLOCK_STARTED,
    CLOCK_PAUSED,
    CLOCK_RESUMED,
    CLOCK_RESET_REQUESTED,
    CLOCK_RESET_COMPLETED,
    CLOCK_TICK,
    CLOCK_SINGLE_TIMER_COMPLETED,
    CLOCK_SERIES_COMPLETED,
    CLOCK_DESTROY_REQUESTED,
    CLOCK_DESTROYED
}
