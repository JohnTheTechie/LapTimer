package johnfatso.laptimer.timerdbms;

import android.app.Application;

import java.util.ArrayList;

public class TimerRepository {
    private final TimerDAO timerDAO;

    TimerRepository(Application application) {
        TimerRoomDatabase db = TimerRoomDatabase.getInstance(application);
        timerDAO = db.timerDAO();
    }

    ArrayList<SimpleTimerSequenceContainer> getAllTimers() {
        ArrayList<SimpleTimerSequenceContainer> collection = new ArrayList<>();
        final ArrayList<TimerEntity> container = new ArrayList<>();

        TimerRoomDatabase.dbExecutor.execute(() -> {
            container.addAll(timerDAO.getAllTimerSequences());
        });

        for (TimerEntity timerEntity: container){
            SimpleTimerSequenceContainer timerSequence = new SimpleTimerSequenceContainer();

            timerSequence.setUniqueID(timerEntity.getId());
            timerSequence.setContainerName(timerEntity.getName());
            timerSequence.setRootNode(BlobMaker.blobToTimerTree(timerEntity.getTimerSequenceBlob()));

            collection.add(timerSequence);
        }

        return collection;
    }

    void insert(SimpleTimerSequenceContainer container) {

        TimerEntity timerEntity = new TimerEntity(container);

        TimerRoomDatabase.dbExecutor.execute(() -> {
            timerDAO.insert(timerEntity);
        });
    }

    void insert(ArrayList<SimpleTimerSequenceContainer> containers){

        TimerRoomDatabase.dbExecutor.execute(() -> {
            for (SimpleTimerSequenceContainer container: containers){
                timerDAO.insert(new TimerEntity(container));
            }
        });
    }

    void deleteAll(){
        TimerRoomDatabase.dbExecutor.execute(timerDAO::deleteAll);
    }

    void delete(String name){
        TimerRoomDatabase.dbExecutor.execute(() -> {
            timerDAO.deleteEntry(name);
        });
    }

    void delete(long id){
        TimerRoomDatabase.dbExecutor.execute(() -> {
            timerDAO.deleteEntry(id);
        });
    }

    SimpleTimerSequenceContainer getTimerSequence(long id){
        final SimpleTimerSequenceContainer[] container = {new SimpleTimerSequenceContainer()};

        TimerRoomDatabase.dbExecutor.execute(() -> {
            container[0] = timerDAO.getTimerSequence(id);
        });

        return container[0];
    }

    SimpleTimerSequenceContainer getTimerSequence(String name){
        final SimpleTimerSequenceContainer[] container = {new SimpleTimerSequenceContainer()};

        TimerRoomDatabase.dbExecutor.execute(() -> {
            container[0] = timerDAO.getTimerSequence(name);
        });

        return container[0];
    }

}
