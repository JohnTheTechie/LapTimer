package johnfatso.laptimer.timer;

interface TimerRunnable {
    void set_timer_and_duration(long ticks_until_expiry, long duration_of_the_ticket_in_millis);
    void run_timer_runnable();
    long stop_timer_runnable(); // return the remaining millis until next tick
}
