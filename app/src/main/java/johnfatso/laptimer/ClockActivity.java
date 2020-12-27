package johnfatso.laptimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import johnfatso.laptimer.controller.TimerControlCommand;
import johnfatso.laptimer.controller.TimerControlInputObservable;
import johnfatso.laptimer.servicesubsystems.Converters;
import johnfatso.laptimer.status.StatusClockManager;
import johnfatso.laptimer.viewmodel.ModelUpdateObservable;
import johnfatso.laptimer.viewmodel.ModelUpdateObserver;
import johnfatso.laptimer.viewmodel.TimerActivityTimerStateContainer;
import johnfatso.laptimer.viewmodel.TimerActivityViewModelObservable;

public class ClockActivity extends AppCompatActivity implements ModelUpdateObserver<TimerActivityTimerStateContainer> {

    private static final String LOG_TAG = "TAG_ACTIVITY";
    private static final String CLASS_ID = "ClockActivity";

    private TextView main_timer, next_timer;
    private TextView prev_counter, next_counter;
    private ImageButton control_button;

    private StatusClockManager status;

    private ClockService service;
    private Intent serviceIntent;

    final String MAIN_CLOCK = "main_clock";
    final String NEXT_CLOCK = "next_clock";
    final String PREV_COUNTER = "prev_counter";
    final String NEXT_COUNTER = "next_counter";
    final static public String STATUS = "status";

    /*
    Activity life cycle functions
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        status = StatusClockManager.UNINITIALIZED;
        prepareHmiElements();
        //setDefaultValuesToTextHmiElements();
        if(savedInstanceState!=null){
            /*main_timer.setText(savedInstanceState.getString(MAIN_CLOCK));
            next_timer.setText(savedInstanceState.getString(NEXT_CLOCK));
            prev_counter.setText(savedInstanceState.getString(PREV_COUNTER));
            next_counter.setText(savedInstanceState.getString(NEXT_COUNTER));
            setStatus((StatusClockManager) Objects.requireNonNull(savedInstanceState.getSerializable(STATUS)));*/
            onViewModelUpdated(TimerActivityViewModelObservable.getInstance());
        }

        createServiceIntent();
        if(status == StatusClockManager.UNINITIALIZED || status == StatusClockManager.COMPLETED || status == StatusClockManager.INITIALIZED)  {
            startService(serviceIntent);
        }
        bindToTheService();
        TimerActivityViewModelObservable.getInstance().register(this);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        Log.v(LOG_TAG, CLASS_ID + "activity is restarted");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.v(LOG_TAG, CLASS_ID + "activity is started");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.v(LOG_TAG, CLASS_ID + "activity is resuming");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.v(LOG_TAG, CLASS_ID + "Activity in Pause state");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.v(LOG_TAG, CLASS_ID + "Activity in stopped state");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimerActivityViewModelObservable.getInstance().deregister(this);
        unbindService(connection);
        Log.v(LOG_TAG, CLASS_ID + "activity is destroyed");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MAIN_CLOCK, main_timer.getText().toString());
        outState.putString(NEXT_CLOCK, next_timer.getText().toString());
        outState.putString(PREV_COUNTER, prev_counter.getText().toString());
        outState.putString(NEXT_COUNTER, next_counter.getText().toString());
        outState.putSerializable(STATUS, status);
        outState.putBoolean("RECREATED", true);
    }

    /*
    Service specific functions
     */

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.v(LOG_TAG, ClockActivity.this.getLocalClassName()+" | ServiceConnected");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ClockService.ClockBinder binder = (ClockService.ClockBinder) service;
            ClockActivity.this.service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.v(LOG_TAG, ClockActivity.this.getLocalClassName()+" | SeriveDisconnected");
        }
    };

    private void createServiceIntent(){
        Log.v(LOG_TAG, this.getLocalClassName()+" | service called");
        serviceIntent = new Intent(this, ClockService.class);
        serviceIntent.putExtra(ClockService.CLOCK_TIMER_LIST, getIntent().getStringExtra(MainActivity.CLOCK_TO_START));
        serviceIntent.putExtra(STATUS, status);
    }

    private void bindToTheService(){
        Log.v(LOG_TAG, this.getLocalClassName()+" | bind service called");
        serviceIntent.putExtra(STATUS, status);
        if(status != StatusClockManager.RUNNING && status != StatusClockManager.PAUSED){
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        }
        else {
            bindService(serviceIntent, connection, 0);
        }
    }

    /*
    Event listener functions
     */

    @Override
    public void onViewModelUpdated(ModelUpdateObservable<TimerActivityTimerStateContainer> observable) {
        TimerActivityTimerStateContainer state = observable.getState();
        long nextTimer = state.getNextTimerInQueue();
        if (nextTimer != -1){
            this.setNext_timer(Converters.timer_to_time_string(nextTimer));
        }
        else {
            this.setNext_timer("--:--");
        }
        this.setMain_timer(Converters.timer_to_time_string(state.getRemainingTimeInSeconds()));
        this.setPrev_counter(Integer.toString(state.getCurrentTimerIndex()));
        this.setNext_counter(Integer.toString(state.getTotalTimerCount() - state.getCurrentTimerIndex() - 1));
        this.setStatus(state.getTimerStatus());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(status != StatusClockManager.COMPLETED) {
                TimerControlInputObservable.getObservable().command(TimerControlCommand.STOP);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
    HMI component control functions
     */

    public void setMain_timer(String timer){
        main_timer.setText(timer);
        Log.v(LOG_TAG, "");
    }

    public void setNext_timer(String timer){
        next_timer.setText(timer);
    }

    public void setPrev_counter(String prev_counter) {
        this.prev_counter.setText(prev_counter);
    }

    public void setNext_counter(String next_counter) {
        this.next_counter.setText(next_counter);
    }

    public void setControl_button(int control_button) {
        this.control_button.setImageDrawable(ContextCompat.getDrawable(this, control_button));
    }

    public void setStatus(StatusClockManager newStatus){
        switch (newStatus){
            case INITIALIZED:
            case UNINITIALIZED:
            case PAUSED:
                setControl_button(android.R.drawable.ic_media_play);
                break;

            case RUNNING:
                setControl_button(android.R.drawable.ic_media_pause);
                break;

            case COMPLETED:
            case DESTROYED:
                setControl_button(android.R.drawable.ic_menu_revert);
                break;
        }
        this.status = newStatus;
        Log.v(LOG_TAG,CLASS_ID + " | status update recieved | " + newStatus );
    }

    private void prepareHmiElements(){
        Log.v(LOG_TAG, this.getLocalClassName()+" | Prepare HMI elements called");
        main_timer = findViewById(R.id.clock_main_timer);
        next_timer = findViewById(R.id.clock_next_timer_clock);

        prev_counter = findViewById(R.id.clock_previous_timer_count);
        next_counter = findViewById(R.id.clock_remaining_timer_count);

        control_button = findViewById(R.id.clock_control_button);
        control_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (status){
                    case INITIALIZED:
                    case UNINITIALIZED:
                    case PAUSED:
                        TimerControlInputObservable.getObservable().command(TimerControlCommand.START);
                        break;

                    case RUNNING:
                        TimerControlInputObservable.getObservable().command(TimerControlCommand.PAUSE);
                        break;

                    case COMPLETED:
                    case DESTROYED:
                        TimerControlInputObservable.getObservable().command(TimerControlCommand.RESET);
                        break;
                }
            }
        });
    }

    private void setDefaultValuesToTextHmiElements(){
        main_timer.setText(R.string.default_clock_string);
        next_timer.setText(R.string.default_clock_string);

        prev_counter.setText(R.string.default_counter);
        next_counter.setText(R.string.default_counter);

        control_button.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_media_play));
    }
}
