package johnfatso.laptimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Objects;
import java.util.zip.Inflater;

import johnfatso.laptimer.status.StatusClockActivity;

public class ClockActivity extends AppCompatActivity {

    private static final String LOG_TAG = "TAG_ACTIVITY";

    private TextView main_timer;
    private TextView prev_counter, next_counter;
    private ImageButton control_button;

    private StatusClockActivity status;

    TimerPersistanceContainer container;

    private ClockService service;
    private Intent serviceIntent;

    final String MAIN_CLOCK = "main_clock";
    final String PREV_COUNTER = "prev_counter";
    final String NEXT_COUNTER = "next_counter";
    final static public String STATUS = "status";

    RecyclerView recyclerView;
    RunningTimerListAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        status = StatusClockActivity.IDLE;
        prepareHmiElements();
        setDefaultValuesToTextHmiElements();
        if(savedInstanceState!=null){
            main_timer.setText(savedInstanceState.getString(MAIN_CLOCK));
            prev_counter.setText(savedInstanceState.getString(PREV_COUNTER));
            next_counter.setText(savedInstanceState.getString(NEXT_COUNTER));
            setStatus((StatusClockActivity) Objects.requireNonNull(savedInstanceState.getSerializable(STATUS)));
        }

        createServiceIntent();
        if(status == StatusClockActivity.IDLE || status == StatusClockActivity.COMPLETED || status == StatusClockActivity.REINITIALIZED)  {
            startService(serviceIntent);
        }
        bindToTheService();

        container = TimerPersistanceContainer.getContainer();
        adapter = new RunningTimerListAdapter(container.getTimerBox(getIntent().getStringExtra(MainActivity.CLOCK_TO_START)).getExecutableTimerList());

        recyclerView = findViewById(R.id.clock_activity_container);
        recyclerView.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(status != StatusClockActivity.COMPLETED) {
                service.clock_control_input(ClockControlCommand.STOP);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setMain_timer(String timer){
        main_timer.setText(timer);
        Log.v(LOG_TAG, "");
    }


    public void setPrev_counter(String prev_counter) {
        this.prev_counter.setText(prev_counter);
    }

    public void setNext_counter(String next_counter) {
        this.next_counter.setText(next_counter);
    }

    public void setControl_button(int control_button) {
        this.control_button.setImageDrawable(getDrawable(control_button));
    }

    public void setCurrentTimerPosition(int position){
        adapter.setCurrentPosition(position);
        adapter.notifyDataSetChanged();
        layoutManager.scrollToPosition(position);
    }

    public void setStatus(StatusClockActivity newStatus){
        switch (newStatus){
            case IDLE:
            case PAUSED:
            case REINITIALIZED:
            case COMPLETED:
                setControl_button(android.R.drawable.ic_media_play);
                break;

            case RUNNING:
                setControl_button(android.R.drawable.ic_media_pause);
                break;
        }
        this.status = newStatus;
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.v(LOG_TAG, ClockActivity.this.getLocalClassName()+" | ServiceConnected");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ClockService.ClockBinder binder = (ClockService.ClockBinder) service;
            ClockActivity.this.service = binder.getService();
            ClockActivity.this.service.registerActivity(ClockActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.v(LOG_TAG, ClockActivity.this.getLocalClassName()+" | SeriveDisconnected");
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MAIN_CLOCK, main_timer.getText().toString());
        outState.putString(PREV_COUNTER, prev_counter.getText().toString());
        outState.putString(NEXT_COUNTER, next_counter.getText().toString());
        outState.putSerializable(STATUS, status);
    }

    private void prepareHmiElements(){
        Log.v(LOG_TAG, this.getLocalClassName()+" | Prepare HMI elements called");
        main_timer = findViewById(R.id.clock_main_timer);

        prev_counter = findViewById(R.id.clock_previous_timer_count);
        next_counter = findViewById(R.id.clock_remaining_timer_count);

        control_button = findViewById(R.id.clock_control_button);
        control_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (status){
                    case IDLE:
                    case REINITIALIZED:
                    case PAUSED:
                    case COMPLETED:
                        ClockActivity.this.service.clock_control_input(ClockControlCommand.START);
                        break;

                    case RUNNING:
                        ClockActivity.this.service.clock_control_input(ClockControlCommand.PAUSE);
                }
            }
        });
    }

    private void setDefaultValuesToTextHmiElements(){
        main_timer.setText(R.string.default_clock_string);

        prev_counter.setText(R.string.default_counter);
        next_counter.setText(R.string.default_counter);

        control_button.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
    }

    private void createServiceIntent(){
        Log.v(LOG_TAG, this.getLocalClassName()+" | service called");
        serviceIntent = new Intent(this, ClockService.class);
        serviceIntent.putExtra(ClockService.CLOCK_TIMER_LIST, getIntent().getStringExtra(MainActivity.CLOCK_TO_START));
        serviceIntent.putExtra(STATUS, status);
    }

    private void bindToTheService(){
        Log.v(LOG_TAG, this.getLocalClassName()+" | bind service called");
        serviceIntent.putExtra(STATUS, status);
        if(status != StatusClockActivity.RUNNING && status != StatusClockActivity.PAUSED){
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        }
        else {
            bindService(serviceIntent, connection, 0);
        }

    }

    class RunningTimerListAdapter extends RecyclerView.Adapter<RunningTimerListAdapter.TimerViewHolder>{

        ClockTimerList list;
        int currentPosition;

        class TimerViewHolder extends RecyclerView.ViewHolder{
            boolean isCurrent;
            TextView timerText;

            public TimerViewHolder(@NonNull View itemView, TextView timerText) {
                super(itemView);
                isCurrent = false;
                this.timerText = timerText;
            }
        }

        public RunningTimerListAdapter(ClockTimerList list) {
            this.list = list;
        }

        @NonNull
        @Override
        public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_clock_activity, parent, false);
            TextView textView = view.findViewById(R.id.simple_timer_time_string_clock_activity);
            return new  TimerViewHolder(view, textView);
        }

        @Override
        public void onBindViewHolder(@NonNull TimerViewHolder holder, int position) {
            if(position != currentPosition){
                holder.itemView.setBackground(getDrawable(R.drawable.clock_simple_list_item_background));
            }
            else {
                holder.itemView.setBackground(getDrawable(R.drawable.clock_simple_list_item_highlighted));
            }

            holder.timerText.setText(convert_timer_to_time_string(list.get(position)));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        /**
         * convert timer long value into String so that it can be shown in Notification and in the activity
         * @param timer time value to be converted
         * @return Timer in string form
         */
        private String convert_timer_to_time_string(long timer){
            long minutes, seconds;
            String seconds_string;
            if(timer < 3600){
                minutes = timer/60;
                seconds = timer%60;
                if(seconds<10) seconds_string = "0"+seconds;
                else seconds_string = ""+seconds;
                return ""+minutes+":"+seconds_string;
            }
            else throw new IllegalStateException("timer exceeds an hour");
        }

        /**
         * sets the currently running timers position
         *
         * @param position new current timer position
         */
        void setCurrentPosition(int position){
            currentPosition = position;
        }
    }
}
