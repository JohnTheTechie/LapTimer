package johnfatso.laptimer.viewmodel;

public interface ModelUpdateObserver<Container>{
    void onViewModelUpdated(ModelUpdateObservable<Container> observable);
}
