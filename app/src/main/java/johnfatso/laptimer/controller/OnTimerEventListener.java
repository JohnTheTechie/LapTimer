package johnfatso.laptimer.controller;

public interface OnTimerEventListener{
    boolean onTimerEvent(TimerEventObservable observable, ClockTimerEvent event);
}
