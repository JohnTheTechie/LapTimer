package johnfatso.laptimer.controller;

public interface OnControlCommandListener<ControlInputType> {
    boolean onControlCommand(EventObservable<ControlInputType> observable, ControlInputType command);
}
