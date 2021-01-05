package johnfatso.laptimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import johnfatso.laptimer.adapters.TimerCollectionListGridAdapter;
import johnfatso.laptimer.timerdbms.TimerSequenceCollection;

public class MainActivity extends AppCompatActivity {

    //string identifiers
    final static String CLOCK_TO_START = "timer_to_start";

    //definition of request codes
    final int REQUEST_CREATE_NEW_TIMERBOX = 0x01;
    final int REQUEST_MODIFY_TIMERBOX = 0x02;

     Toolbar toolbar;

     RecyclerView recyclerView;
     TimerCollectionListGridAdapter adapter;
     RecyclerView.LayoutManager layoutManager;
     TimerPersistenceContainer timerPersistenceContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerPersistenceContainer = TimerPersistenceContainer.getContainer();

        recyclerView = findViewById(R.id.list_container_main);

        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TimerCollectionListGridAdapter(TimerSequenceCollection.getContainer().getCollection(), this);
        recyclerView.setAdapter(adapter);

        toolbar = findViewById(R.id.main_actionbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //adapter.clearSelection();
        return super.onTouchEvent(event);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        //super.onConfigurationChanged(newConfig);
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode code identifying the request type
     * @param resultCode result sent by the called activity
     * @param data data sent by the called activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CREATE_NEW_TIMERBOX){
            if(resultCode == RESULT_OK){
                adapter.notifyDataSetChanged();
            }
        }
        else if (requestCode == REQUEST_MODIFY_TIMERBOX){
            if(resultCode == RESULT_OK){
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * function to trigger intent to start timer activity
     * @param nameOfTheList name of the timer as reference
     */
    public void triggerTimer(String nameOfTheList){
        Intent intent=new Intent(this, ClockActivity.class);
        intent.putExtra(CLOCK_TO_START, nameOfTheList );
        startActivity(intent);
    }

    public void editRequest(String nameOfContainerToEdit){
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        intent.putExtra(EditorActivity.REQUEST_ID, ModifierActivity.MODIFY_TIMER);
        intent.putExtra(EditorActivity.BOX_ID, nameOfContainerToEdit);
        startActivityForResult(intent, REQUEST_MODIFY_TIMERBOX);
    }

    public void createNewTimerRequest(){
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        intent.putExtra(EditorActivity.REQUEST_ID, ModifierActivity.NEW_TIMER);
        startActivityForResult(intent, REQUEST_MODIFY_TIMERBOX);
    }

    Activity getActivity(){
        return this;
    }

}
