package johnfatso.laptimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //definition of request codes
    final int REQUEST_CREATE_NEW_TIMERBOX = 0x01;
    final int REQUEST_MODIFY_TIMERBOX = 0x02;

     Toolbar toolbar;

     RecyclerView recyclerView;
     RecyclerView.Adapter adapter;
     RecyclerView.LayoutManager layoutManager;
     TimerPersistanceContainer timerPersistanceContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerPersistanceContainer = TimerPersistanceContainer.getContainer();

        recyclerView = findViewById(R.id.list_container_main);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TimerMainListAdapter(timerPersistanceContainer.getTimerBoxes());
        recyclerView.setAdapter(adapter);

        toolbar = findViewById(R.id.main_actionbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.addition_main:
                //if adddition pressed, modifier activity called for results
                Intent intent = new Intent(this, ModifierActivity.class);
                startActivityForResult(intent, REQUEST_CREATE_NEW_TIMERBOX);
                return true;

            case R.id.delete_main:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CREATE_NEW_TIMERBOX){
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
    void triggerTimer(String nameOfTheList){
        Intent intent=new Intent(this, ClockActivity.class);
        intent.putExtra("timerlist", nameOfTheList );
        startActivity(intent);
    }

    public class TimerMainListAdapter extends RecyclerView.Adapter<TimerMainListAdapter.CustomViewHolder>{

        ArrayList<TimerBox> list;

        public TimerMainListAdapter(ArrayList<TimerBox> list) {
            this.list = list;
            Log.v("Timer", "MainActivity | list container received | size : "+list.size());
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder{
            TextView roundsCounter;
            TextView timerCounter;
            TextView durationCounter;
            TextView label;
            ImageButton startButton, expandButton;

            public CustomViewHolder(@NonNull View itemView, TextView roundsCounter,
                                    TextView timerCounter, TextView durationCounter, TextView label,
                                    ImageButton startButton, ImageButton expandButton) {
                super(itemView);
                this.roundsCounter = roundsCounter;
                this.timerCounter = timerCounter;
                this.durationCounter = durationCounter;
                this.label = label;
                this.startButton = startButton;
                this.expandButton = expandButton;
            }
        }

        @NonNull
        @Override
        public TimerMainListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_layout, parent, false);
            TextView roundsCounter = view.findViewById(R.id.counter_rounds_list_item);
            TextView timerCounter = view.findViewById(R.id.counter_timers_list_item);
            TextView durationCounter = view.findViewById(R.id.duration_list_item);
            TextView label = view.findViewById(R.id.label_list_item);
            ImageButton startButton = view.findViewById(R.id.start_list_item);
            ImageButton expandButton = view.findViewById(R.id.exapnad_button_list_item);
            TimerMainListAdapter.CustomViewHolder VH = new CustomViewHolder(view, roundsCounter, timerCounter, durationCounter, label, startButton, expandButton);

            return VH;
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {
            String name = list.get(position).getName();
            int roundsCount = list.get(position).getRepetitions();
            int timerCount = list.get(position).getTimerList().size();
            long durationCount = list.get(position).getTotalDurationOfSingleCycle();
            Log.v("Timer", "MainActivity | box received for position : "+position
                    +" | name: "+name+" | rounds: "+roundsCount+" | timers: "
                    +timerCount+" | duration: "+durationCount);
            holder.roundsCounter.setText(roundsCount+"");
            holder.timerCounter.setText(timerCount+"");
            holder.durationCounter.setText(durationCount+"");
            holder.label.setText(name);
            holder.startButton.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
            holder.expandButton.setImageDrawable(getDrawable(android.R.drawable.arrow_up_float));

            holder.startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.this.triggerTimer(list.get(position).getName());
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
