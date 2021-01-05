package johnfatso.laptimer.timerdbms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BlobMaker {

    static public String timerTreeToBlob(TimerContainerNodeInterface rootNode){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(TimerContainerNodeInterface.class, new SimpleTimerContainerNodeInstanceAdapter());
        Gson gson = builder.create();

        return gson.toJson(rootNode);
    }

    static public TimerContainerNodeInterface blobToTimerTree(String jsonString){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(TimerContainerNodeInterface.class, new SimpleTimerContainerNodeInstanceAdapter());
        Gson gson = builder.create();

        return gson.fromJson(jsonString, TimerListSubtree.class);
    }

}
