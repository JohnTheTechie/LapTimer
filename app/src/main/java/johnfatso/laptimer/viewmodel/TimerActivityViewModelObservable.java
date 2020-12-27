package johnfatso.laptimer.viewmodel;

import java.util.ArrayList;

public class TimerActivityViewModelObservable implements ModelUpdateObservable<TimerActivityTimerStateContainer>{

    static private TimerActivityViewModelObservable observable = null;

    private boolean isStateChanged;
    private TimerActivityTimerStateContainer state;
    private final ArrayList<ModelUpdateObserver<TimerActivityTimerStateContainer>> listOfObservers;

    private TimerActivityViewModelObservable() {
        this.isStateChanged = false;
        this.listOfObservers = new ArrayList<>();
        this.state = new TimerActivityTimerStateContainer();
    }

    static public TimerActivityViewModelObservable getInstance(){
        if (observable == null){
            observable = new TimerActivityViewModelObservable();
        }
        return observable;
    }

    @Override
    public void setStateChanged(boolean isStateChanged) {
        this.isStateChanged = isStateChanged;
        if (this.isStateChanged){
            this.notifyObservers();
        }
        this.isStateChanged = false;
    }

    @Override
    public void setNewState(TimerActivityTimerStateContainer newState) {
        this.state = newState;
    }

    @Override
    public void register(ModelUpdateObserver<TimerActivityTimerStateContainer> observer) {
        if ( ! this.listOfObservers.contains(observer) ){
            this.listOfObservers.add(observer);
        }
    }

    @Override
    public void deregister(ModelUpdateObserver<TimerActivityTimerStateContainer> observer) {
        this.listOfObservers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(ModelUpdateObserver<TimerActivityTimerStateContainer> observer: this.listOfObservers){
            observer.onViewModelUpdated(this);
        }
    }

    @Override
    public TimerActivityTimerStateContainer getState() {
        return this.state;
    }
}
