package johnfatso.laptimer.timer;

interface ControllableTimer {
    void set_ticks_until_expiry(long ticks_until_expiry);
    void set_base_tick_duration_in_millis(long base_tick_duration_in_millis);
    void start_timer();
    long pause_timer(); // returns milliseconds until next tick
    void stop_timer();
    void reset_timer();
}
