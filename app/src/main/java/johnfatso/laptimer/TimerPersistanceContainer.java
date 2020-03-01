package johnfatso.laptimer;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class TimerPersistanceContainer {
    String LOG_TAG = "Timer";
    private ArrayList<TimerBox> timerBoxes;

    private static TimerPersistanceContainer container=null;

    private TimerPersistanceContainer() {
        timerBoxes = new ArrayList<>();
        Log.v(LOG_TAG, "TimerPersistanceContainer created");
    }

    @NonNull
    static TimerPersistanceContainer getContainer(){
        if(container == null){
                if(container == null){
                    container = new TimerPersistanceContainer();
                }
        }
        return container;
    }

    public int getSize(){
        Log.v(LOG_TAG,"size requested : size ="+timerBoxes.size());
        return timerBoxes.size();
    }

    public ArrayList<String> getTimerListOfNames(){
        ArrayList<String> nameList = new ArrayList<>();
        for (TimerBox box: this.timerBoxes){
            nameList.add(box.getName());
        }
        return nameList;
    }

    public TimerBox getTimerBox(String name){
        if(this.getSize()==0) return null;
        else {
            for (TimerBox box: timerBoxes){
                if (box.getName().equals(name)) return box;
            }
            return null;
        }
    }

    public TimerBox getTimerBox(int index){
        return timerBoxes.get(index);
    }

    public ArrayList<TimerBox> getTimerBoxes(){
        return this.timerBoxes;
    }

    public void insertTimerBox(TimerBox box){
        int index = this.indexOf(box);
        if(index == -1){
            this.insert(box);
        }
        else {
            this.getTimerBox(index).updateTimerBox(box);
        }
    }

    public void prepareDummyData(){
        Log.v(LOG_TAG,"dummy data prepared");
        if(timerBoxes.size() == 0){
            TimerBox box = new TimerBox();
            box.setName("dummy");
            box.setTimerList(ClockTimerList.prepareDummyList());
            this.insertTimerBox(box);
        }
        Log.v(LOG_TAG,"updated size of the container : "+this.timerBoxes.size());
    }

    private void insert(TimerBox box){
        this.timerBoxes.add(box);
    }

    private int indexOf(TimerBox box_ut){
        if(timerBoxes.size() == 0) return -1;
        for(TimerBox box: timerBoxes){
            if(box.getName().equals(box_ut.getName())){
                return timerBoxes.indexOf(box);
            }
        }
        return -1;
    }
}
