package johnfatso.laptimer.servicesubsystems;

public class Converters {

    /**
     * convert timer long value into String so that it can be shown in Notification and in the activity
     * @param timer time value to be converted in seconds
     * @return Timer in string form XX:XX
     */
    public static String timer_to_time_string(long timer){
        long minutes, seconds;
        String seconds_string;
        String minutes_string;
        if(timer < 3600){
            minutes = timer/60;
            seconds = timer%60;
            if(minutes<10) minutes_string = "0"+minutes;
            else minutes_string = "" + minutes;
            if(seconds<10) seconds_string = "0"+seconds;
            else seconds_string = ""+seconds;
            return ""+minutes_string+":"+seconds_string;
        }
        else throw new IllegalStateException("timer exceeds an hour");
    }
}
