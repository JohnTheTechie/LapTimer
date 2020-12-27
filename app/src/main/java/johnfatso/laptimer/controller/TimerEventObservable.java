package johnfatso.laptimer.controller;

import android.util.Log;

import java.util.ArrayList;

public class TimerEventObservable {
    private static TimerEventObservable observable = null;
    private static final String LOG_TAG = "EVENT_OBSERVABLE";
    private static final String CLASS_ID = "TimerEventObservable";

    private ClockTimerEvent event;
    private boolean isEventConsumed;
    private final ArrayList<OnTimerEventListener> listOfListeners;

    public static TimerEventObservable getObservable(){
        if (observable == null){
            observable = new TimerEventObservable();
        }
        return observable;
    }

    public void event(ClockTimerEvent event){
        Log.v(LOG_TAG, CLASS_ID + " | event " + event + " occurred");
        this.event = event;
        this.isEventConsumed = false;
        this.notifyObservers();
        this.isEventConsumed = true;
    }

    public TimerEventObservable() {
        this.event = null;
        this.isEventConsumed = true;
        this.listOfListeners = new ArrayList<>();
    }

    public void command(ClockTimerEvent input){
        this.event = input;
        this.isEventConsumed = false;
        this.notifyObservers();
        this.isEventConsumed = true;
    }

    public void register(OnTimerEventListener listener){
        if (!this.listOfListeners.contains(listener)){
            this.listOfListeners.add(listener);
        }
    }

    public void deregister(OnTimerEventListener listener){
        this.listOfListeners.remove(listener);
    }

    void notifyObservers(){
        for (OnTimerEventListener listener: listOfListeners){
            this.isEventConsumed = listener.onTimerEvent(this, this.event);
        }
    }
}
