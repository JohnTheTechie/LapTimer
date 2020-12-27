package johnfatso.laptimer.controller;

public class TimerControlInputObservable extends EventObservable<TimerControlCommand> {

    private static TimerControlInputObservable observable = null;

    public static TimerControlInputObservable getObservable(){
        if (observable == null){
            observable = new TimerControlInputObservable();
        }
        return observable;
    }

}
