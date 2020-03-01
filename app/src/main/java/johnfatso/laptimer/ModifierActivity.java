package johnfatso.laptimer;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Trace;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class ModifierActivity extends AppCompatActivity {

    Dialog timePickerDialog;
    private NumberPicker minutesPicker, secondsPicker;

    Toolbar actionBar;

    EditText name_label;
    EditText repetition_counter;

    TimerBox timerBox;

    RecyclerView recyclerView;
    TimerModifierListAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    MenuItem deleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier);

        timerBox = new TimerBox();
        timePickerDialog = new Dialog(ModifierActivity.this);
        prepareDialog();

        recyclerView = findViewById(R.id.timer_modifier_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TimerModifierListAdapter();
        recyclerView.setAdapter(adapter);

        actionBar = findViewById(R.id.modifier_actionbar);
        setSupportActionBar(actionBar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.modifier_actionbar, menu);

        deleteButton = menu.findItem(R.id.delete_modifier);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.addition_modifier:
                Toast.makeText(this, "dialog triggered", Toast.LENGTH_SHORT).show();
                timePickerDialog.show();
                if(adapter.isItemsSelected) adapter.notifyDataSetChanged();
                return true;

            case R.id.delete_modifier:
                Collections.sort(adapter.selectedItems);
                Collections.reverse(adapter.selectedItems);
                for (int position: adapter.selectedItems){
                    timerBox.deleteTimerAtPosition(position);
                }
                adapter.selectedItems.clear();
                adapter.notifyDataSetChanged();
                return true;

            case R.id.save_modifier:
                if(adapter.isItemsSelected) adapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void prepareDialog(){

        LayoutInflater inflater = getLayoutInflater();

        this.timePickerDialog.setTitle("Set time");
        timePickerDialog.setContentView(R.layout.time_selector_dialog);
        minutesPicker = timePickerDialog.findViewById(R.id.minute_picker);
        secondsPicker = timePickerDialog.findViewById(R.id.second_picker);

        minutesPicker.setMaxValue(99);
        minutesPicker.setMinValue(0);

        secondsPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);

        Button affirmativeButton = timePickerDialog.findViewById(R.id.button_ok);
        affirmativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ModifierActivity.this, "OK pressed", Toast.LENGTH_SHORT).show();
                timerBox.addTimer(minutesPicker.getValue()*60+secondsPicker.getValue());

                adapter.notifyDataSetChanged();
                timePickerDialog.dismiss();
            }
        });

        Button negativeButton = timePickerDialog.findViewById(R.id.button_cancel);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ModifierActivity.this, "Cancel pressed", Toast.LENGTH_SHORT).show();
                timePickerDialog.dismiss();
            }
        });

    }

    void changeDeleteButtonVisibility(int countOfSelectedItems){
        if(countOfSelectedItems > 0){
            this.deleteButton.setVisible(true);
        }
        else {
            this.deleteButton.setVisible(false);
        }
    }

    public class TimerModifierListAdapter extends RecyclerView.Adapter<TimerModifierListAdapter.TimerViewHolder> {

        public boolean isItemsSelected;

        ArrayList<Integer> selectedItems;

        TimerModifierListAdapter() {
            selectedItems = new ArrayList<>();
        }

        class TimerViewHolder extends RecyclerView.ViewHolder{

            TextView timerText;
            boolean isHolderSelected;

            TimerViewHolder(@NonNull View itemView, TextView timerText) {
                super(itemView);
                this.timerText = timerText;
                isHolderSelected = false;
            }
        }

        @NonNull
        @Override
        public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_list, parent, false);
            TextView textView = view.findViewById(R.id.name_timer_main);
            return new TimerViewHolder(view, textView);
        }


        @Override
        public void onBindViewHolder(@NonNull final TimerViewHolder holder, final int position) {
            holder.timerText.setText(convertLongToTimeStamp(timerBox.getTimerList().get(position)));
            holder.timerText.setTextColor(getResources().getColor(R.color.white_text));
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(selectedItems.contains(position)){
                        selectedItems.remove(selectedItems.indexOf(position));
                        holder.timerText.setTextColor(getResources().getColor(R.color.white_text));

                    }
                    else {
                        selectedItems.add(position);
                        holder.timerText.setTextColor(getResources().getColor(R.color.highlighted_text));
                    }

                    isItemsSelected = selectedItems.size()>0;

                    changeDeleteButtonVisibility(selectedItems.size());
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return timerBox.getTimerList().size();
        }


        private String convertLongToTimeStamp(long timerInSeconds){
            String result = "";
            long minutes = timerInSeconds / 60;
            long seconds = timerInSeconds % 60;

            if(minutes == 0){
                result+="00:";
            }
            else if(minutes>0 && minutes<10){
                result = result + "0" + Long.toString(minutes) + ":";
            }
            else {
                result = result + Long.toString(minutes) + ":";
            }

            if(seconds == 0){
                result+="00";
            }
            else if(seconds>0 && seconds<10){
                result = result + "0" + Long.toString(seconds);
            }
            else {
                result = result + Long.toString(seconds);
            }

            return result;
        }


    }
}

