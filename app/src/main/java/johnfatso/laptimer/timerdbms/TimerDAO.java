package johnfatso.laptimer.timerdbms;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;

@Dao
public interface TimerDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TimerEntity timerEntity);

    @Query("DELETE FROM timer_master")
    void deleteAll();

    @Query("DELETE from timer_master where timer_id = :id")
    void deleteEntry(long id);

    @Query("DELETE from timer_master where timer_name = :timerName")
    void deleteEntry(String timerName);

    @Query("SELECT * FROM timer_master ORDER BY timer_id ASC")
    ArrayList<TimerEntity> getAllTimerSequences();

    @Query("SELECT * from timer_master where timer_id = :id")
    SimpleTimerSequenceContainer getTimerSequence(long id);

    @Query("SELECT * from timer_master where timer_name = :timerName")
    SimpleTimerSequenceContainer getTimerSequence(String timerName);
}
