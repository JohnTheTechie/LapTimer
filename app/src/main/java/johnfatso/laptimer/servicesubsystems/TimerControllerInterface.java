package johnfatso.laptimer.servicesubsystems;

public interface TimerControllerInterface {
    void initializeClock();
    void startClock();
    void pauseClock();
    void resumeClock();
    void resetClock();
    void terminateClock();
}
