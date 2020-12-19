package johnfatso.laptimer.servicesubsystems;

public interface TimerControllerMessageReceptor {
    void onTick();
    void onClockComplete();
}
