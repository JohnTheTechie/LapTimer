package johnfatso.laptimer.timerdbms;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {TimerEntity.class}, version = 1, exportSchema = false)
public abstract class TimerRoomDatabase extends RoomDatabase {
    public abstract TimerDAO timerDAO();

    private static volatile TimerRoomDatabase INSTANCE;
    static final ExecutorService dbExecutor = Executors.newFixedThreadPool(4);

    static TimerRoomDatabase getInstance(final Context context){
        if(INSTANCE == null){
            synchronized (TimerRoomDatabase.class) {
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TimerRoomDatabase.class, "timer_db")
                            .createFromAsset("asset/timer_table.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
