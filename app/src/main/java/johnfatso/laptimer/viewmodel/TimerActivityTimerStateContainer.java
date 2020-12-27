package johnfatso.laptimer.viewmodel;

import johnfatso.laptimer.notifier.NotificationType;
import johnfatso.laptimer.status.StatusClockManager;

public class TimerActivityTimerStateContainer {
    private long remainingTimeInSeconds;
    private long nextTimerInQueue;
    private int currentTimerIndex;
    private int totalTimerCount;
    private StatusClockManager timerStatus;
    private NotificationType notificationType;

    public TimerActivityTimerStateContainer() {
        this.remainingTimeInSeconds = 0;
        this.nextTimerInQueue = -1;
        this.currentTimerIndex = 0;
        this.totalTimerCount = 0;
        this.timerStatus = StatusClockManager.UNINITIALIZED;
        this.notificationType = NotificationType.None;
    }

    public long getRemainingTimeInSeconds() {
        return remainingTimeInSeconds;
    }

    public TimerActivityTimerStateContainer setRemainingTimeInSeconds(long remainingTimeInSeconds) {
        this.remainingTimeInSeconds = remainingTimeInSeconds;
        return this;
    }

    public long getNextTimerInQueue() {
        return nextTimerInQueue;
    }

    public TimerActivityTimerStateContainer setNextTimerInQueue(long nextTimerInQueue) {
        this.nextTimerInQueue = nextTimerInQueue;
        return this;
    }

    public int getCurrentTimerIndex() {
        return currentTimerIndex;
    }

    public TimerActivityTimerStateContainer setCurrentTimerIndex(int currentTimerIndex) {
        this.currentTimerIndex = currentTimerIndex;
        return this;
    }

    public int getTotalTimerCount() {
        return totalTimerCount;
    }

    public TimerActivityTimerStateContainer setTotalTimerCount(int totalTimerCount) {
        this.totalTimerCount = totalTimerCount;
        return this;
    }

    public StatusClockManager getTimerStatus() {
        return timerStatus;
    }

    public TimerActivityTimerStateContainer setTimerStatus(StatusClockManager timerStatus) {
        this.timerStatus = timerStatus;
        return this;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public TimerActivityTimerStateContainer setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
        return this;
    }

    public TimerActivityTimerStateContainer reset(){
        long remainingTimeInSeconds = 0;
        long nextTimerInQueue = -1;
        int currentTimerIndex = 0;
        int totalTimerCount = 0;
        StatusClockManager timerStatus = StatusClockManager.DESTROYED;
        return this;
    }
}
