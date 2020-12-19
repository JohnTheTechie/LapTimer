package johnfatso.laptimer.constants;

public enum ClockMessage {
    CLOCK_MESSAGE_TICK(0x01),
    CLOCK_MESSAGE_COMPLETE(0x02);

    public final int id;

    ClockMessage(int id) {
        this.id = id;
    }
}
