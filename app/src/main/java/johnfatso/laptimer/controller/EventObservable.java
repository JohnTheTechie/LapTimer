package johnfatso.laptimer.controller;

import java.util.ArrayList;

public abstract class EventObservable< ControlInputType> {

    private ControlInputType command;
    private boolean isCommandConsumed;
    private final ArrayList<OnControlCommandListener<ControlInputType>> listOfListeners;

    public EventObservable() {
        this.command = null;
        this.isCommandConsumed = true;
        this.listOfListeners = new ArrayList<>();
    }

    public void command(ControlInputType input){
        this.command = input;
        this.isCommandConsumed = false;
        this.notifyObservers();
        this.isCommandConsumed = true;
    }

    public void register(OnControlCommandListener<ControlInputType> listener){
        if (!this.listOfListeners.contains(listener)){
            this.listOfListeners.add(listener);
        }
    }

    public void deregister(OnControlCommandListener<ControlInputType> listener){
        this.listOfListeners.remove(listener);
    }

    void notifyObservers(){
        for (OnControlCommandListener<ControlInputType> listener: listOfListeners){
            this.isCommandConsumed = listener.onControlCommand(this, this.command);
        }
    }
}
