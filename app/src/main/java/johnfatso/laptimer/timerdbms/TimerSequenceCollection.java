package johnfatso.laptimer.timerdbms;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class TimerSequenceCollection {
    String LOG_TAG = "Timer";
    private ArrayList<SimpleTimerSequenceContainer> collection;

    private static TimerSequenceCollection instance =null;

    private TimerSequenceCollection() {
        collection = new ArrayList<>();
        this.prepareDummyData();
        Log.v(LOG_TAG, "TimerPersistanceContainer | created");
    }

    public void setCollection(ArrayList<SimpleTimerSequenceContainer> collection){
        this.collection = collection;
    }

    @NonNull
    public static TimerSequenceCollection getContainer(){
        if(instance == null){
            instance = new TimerSequenceCollection();
        }
        return instance;
    }

    public int getSize(){
        Log.v(LOG_TAG,"TimerPersistanceContainer | size requested : size ="+ collection.size());
        return collection.size();
    }

    public ArrayList<String> getTimerListOfNames(){
        ArrayList<String> nameList = new ArrayList<>();
        for (SimpleTimerSequenceContainer box: this.collection){
            nameList.add(box.getContainerName());
        }
        return nameList;
    }

    public SimpleTimerSequenceContainer getSequenceContainer(String name){
        for (SimpleTimerSequenceContainer box: collection){
            if (box.getContainerName().equals(name)) return box;
        }
        return null;
    }

    public SimpleTimerSequenceContainer getSequenceContainer(Long UID){
        for (SimpleTimerSequenceContainer box: collection){
            if (box.getUniqueID().equals(UID)) return box;
        }
        return null;
    }

    public SimpleTimerSequenceContainer getSequenceContainer(int index){
        return collection.get(index);
    }

    public ArrayList<SimpleTimerSequenceContainer> getCollection(){
        return this.collection;
    }

    public void insertSequenceContainer(SimpleTimerSequenceContainer box){
        if(!this.collection.contains(box)){
            this.collection.add(box);
        }
    }

    public void prepareDummyData(){
        Log.v(LOG_TAG,"TimerPersistanceContainer | dummy data prepared");
        if(collection.size() == 0){
            SimpleTimerSequenceContainer container = new SimpleTimerSequenceContainer("dummy");

            ArrayList<TimerContainerNodeInterface> list = new ArrayList<>();

            TimerListLeaf leaf = new TimerListLeaf();
            leaf.setTimer((long) 5);
            leaf.setRepetition(1);
            list.add(leaf);

            leaf = new TimerListLeaf();
            leaf.setTimer((long) 10);
            leaf.setRepetition(1);
            list.add(leaf);

            TimerListSubtree subtree = new TimerListSubtree();
            subtree.addChild((long) 5);
            subtree.addChild((long) 10);
            subtree.setRepetition(1);

            list.add(subtree);

            container.getRootNode().addChildren(list);
            container.getRootNode().setRepetition(2);
            container.setUniqueID((long) 123456789);

            this.collection.add(container);
        }
        Log.v(LOG_TAG,"TimerPersistanceContainer | updated size of the container : "+this.collection.size());
    }

    private void insert(SimpleTimerSequenceContainer box){
        if(!this.collection.contains(box)){
            this.collection.add(box);
        }
        Log.v(LOG_TAG,"TimerPersistenceContainer | box inserted | new size : "+getSize());
    }

    private int indexOf(SimpleTimerSequenceContainer box_ut){
        if(collection.size() == 0) return -1;
        for(SimpleTimerSequenceContainer box: collection){
            if(box.getUniqueID().equals(box_ut.getUniqueID())){
                return collection.indexOf(box);
            }
        }
        return -1;
    }
}
