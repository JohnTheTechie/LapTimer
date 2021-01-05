package johnfatso.laptimer.timerdbms;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "timer_master")
public class TimerEntity {

    @PrimaryKey
    @ColumnInfo (name = "timer_id", typeAffinity = 3)
    private long id;

    @NonNull
    @ColumnInfo (name = "timer_name", typeAffinity = 2)
    private String name;

    @NonNull
    @ColumnInfo (name = "timer_sequence_blob", typeAffinity = 2)
    private String timerSequenceBlob;

    public TimerEntity(long id, String name, String timerSequenceBlob) {
        this.id = id;
        this.name = name;
        this.timerSequenceBlob = timerSequenceBlob;
    }

    public TimerEntity(SimpleTimerSequenceContainer container) {
        this.id = container.getUniqueID();
        this.name = container.getContainerName();
        this.timerSequenceBlob = BlobMaker.timerTreeToBlob(container.getRootNode());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimerSequenceBlob() {
        return timerSequenceBlob;
    }

    public void setTimerSequenceBlob(String timerSequenceBlob) {
        this.timerSequenceBlob = timerSequenceBlob;
    }
}
