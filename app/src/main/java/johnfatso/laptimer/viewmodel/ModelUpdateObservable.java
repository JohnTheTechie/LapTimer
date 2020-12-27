package johnfatso.laptimer.viewmodel;

public interface ModelUpdateObservable<Container>{
    void setStateChanged(boolean isStateChanged);
    void setNewState(Container newState);
    void register(ModelUpdateObserver<Container> observer);
    void deregister(ModelUpdateObserver<Container> observer);
    void notifyObservers();
    Container getState();
}
