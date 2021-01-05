package johnfatso.laptimer.timerdbms;

import android.util.Log;

import java.util.ArrayList;

import johnfatso.laptimer.ClockTimerList;
import johnfatso.laptimer.R;

public class SimpleTimerSequenceContainer {

    private static final String LOG_TAG = "TIMER_DBMS";
    private static final String CLASS_ID = "SimpleTimerSequenceContainer";

    private Long uniqueID;
    private TimerContainerNodeInterface rootNode;
    private String containerName;

    public SimpleTimerSequenceContainer(String containerName) {
        this.uniqueID = null;
        this.rootNode = new TimerListSubtree();
        this.rootNode.setRepetition(1);
        this.rootNode.setLevel(0);
        this.containerName = containerName;
    }

    public SimpleTimerSequenceContainer() {
        this.uniqueID = null;
        this.rootNode = new TimerListSubtree();
        this.rootNode.setRepetition(1);
        this.rootNode.setLevel(0);
        this.containerName = "New Timer";
    }

    public Long getUniqueID() {
        Log.v(LOG_TAG, CLASS_ID+" | uniqueID : " + this.uniqueID);
        return uniqueID;
    }

    public void setUniqueID(Long uniqueID) {
        if (this.uniqueID == null) {
            Log.v(LOG_TAG, CLASS_ID+" | Set UniqueID : "+uniqueID);
            this.uniqueID = uniqueID;
        }
        else {
            Log.v(LOG_TAG, CLASS_ID+" |  UniqueID already exist. Request ignored");
            throw new IllegalAccessError("unique ID attempted to be changed");
        }
    }

    public TimerContainerNodeInterface getRootNode() {
        Log.v(LOG_TAG, CLASS_ID+" |  root node requested");
        return rootNode;
    }

    public void setRootNode(TimerContainerNodeInterface rootNode) {
        Log.v(LOG_TAG, CLASS_ID+" | root node added");
        this.rootNode = rootNode;
    }

    public String getContainerName() {
        Log.v(LOG_TAG, CLASS_ID+" | ContainerName: "+this.containerName);
        return containerName;
    }

    public void setContainerName(String containerName) {
        Log.v(LOG_TAG, CLASS_ID+" | set containerName: "+containerName);
        this.containerName = containerName;
    }

    public ClockTimerList getExecutableList(){
        ClockTimerList list = new ClockTimerList();
        list.addAll(this.rootNode.getExecutableTimerList());
        /*for (int i = 0 ; i < this.rootNode.getRepetition() ; i++){
            list.addAll(this.rootNode.getExecutableTimerList());
        }*/

        Log.v(LOG_TAG, CLASS_ID+" | executable list of "+list.size()+" retrieved");
        return list;
    }

    public int getSizeOfExecutableList(){
        return this.getExecutableList().size();
    }

    public int getDurationOfExecutableList(){
        int duration = 0;
        for (Long timer: this.getExecutableList()){
            duration += timer;
        }
        return duration;
    }
}
