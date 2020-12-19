package johnfatso.laptimer.status;

public enum StatusClockService {
    INITIALIZED,
    RUNNING_ACTIVITY_ATTACHED,
    RUNNING_ACTIVITY_DETACHED,
    PAUSED_ACTIVITY_ATTACHED,
    PAUSED_ACTIVITY_DETACHED,
    COMPLETED,
    DESTROYED;

    StatusClockService(){

    }
}
