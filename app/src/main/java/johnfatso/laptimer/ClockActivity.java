package johnfatso.laptimer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import johnfatso.laptimer.status.StatusClockActivity;

public class ClockActivity extends AppCompatActivity {

    private static final String LOG_TAG = "TAG_ACTIVITY";

    private TextView main_timer, next_timer;
    private TextView prev_counter, next_counter;
    private ImageButton control_button;

    private StatusClockActivity status;

    private ActionBar actionBar;

    private ClockService service;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        prepareHmiElements();
        setDefaultValuesToTextHmiElements();
        status = StatusClockActivity.IDLE;

        createAndStartService();
        bindToTheService();
    }

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
        this.control_button.setImageDrawable(getDrawable(control_button));
    }

    public void setStatus(StatusClockActivity newStatus){
        switch (newStatus){
            case IDLE:
                control_button.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                break;

            case PAUSED:
                control_button.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                break;

            case RUNNING:
                control_button.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
                break;

            case REINITIALIZED:
                control_button.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                break;
        }
        this.status = newStatus;
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ClockService.ClockBinder binder = (ClockService.ClockBinder) service;
            ClockActivity.this.service = binder.getService();
            ClockActivity.this.service.registerActivity(ClockActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

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
        service.stopService(serviceIntent);
    }

    private void prepareHmiElements(){
        main_timer = findViewById(R.id.clock_main_timer);
        next_timer = findViewById(R.id.clock_next_timer_clock);

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
                        ClockActivity.this.service.clock_control_input(ClockControlCommand.START);
                        break;

                    case RUNNING:
                        ClockActivity.this.service.clock_control_input(ClockControlCommand.PAUSE);
                }
            }
        });

        actionBar = getSupportActionBar();
    }

    private void setDefaultValuesToTextHmiElements(){
        main_timer.setText(R.string.default_clock_string);
        next_timer.setText(R.string.default_clock_string);

        prev_counter.setText(R.string.default_counter);
        next_counter.setText(R.string.default_counter);

        control_button.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
    }

    private void createAndStartService(){
        serviceIntent = new Intent(this, ClockService.class);
        serviceIntent.putExtra(ClockService.CLOCK_TIMER_LIST, (ClockTimerList) getIntent().getParcelableExtra("timerlist"));
        startService(serviceIntent);
    }

    private void bindToTheService(){
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }
}
