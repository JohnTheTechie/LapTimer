package johnfatso.laptimer.servicesubsystems;

public interface TimerManagerInterface {
    void initializeClock();
    void startClock();
    void pauseClock();
    void resumeClock();
    void resetClock();
    void terminateClock();
    void destroyTimerManager();
}
