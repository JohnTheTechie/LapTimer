package johnfatso.laptimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class ModifierActivity extends AppCompatActivity {

    final String LOG_TAG = "MODIFIER";

    //Dialog for adding timer item
    Dialog timePickerDialog;
    private NumberPicker minutesPicker, secondsPicker;

    Toolbar actionBar;

    EditText name_label;
    EditText repetition_counter;
    ConstraintLayout container;
    MenuItem deleteButton;

    //object to store the values selected by the user
    TimerBox timerBox;
    //Object to store the timers
    TimerPersistanceContainer timerPersistanceContainer;

    RecyclerView recyclerView;
    TimerModifierListAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

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

        name_label = findViewById(R.id.modifier_edit_name);
        name_label.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                //remove previous unfinished r finished operations
                checkAndResetSelection();
                //if content is the default text, on clicking, text shall be removed
                if(name_label.getText().toString().equals(getResources().getString(R.string.filler_edit_text))){
                    name_label.setText("");
                }
            }
        });

        repetition_counter = findViewById(R.id.repetition_counter_modifier);
        repetition_counter.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                checkAndResetSelection();
            }
        });

        container = findViewById(R.id.modifier_container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndResetSelection();
            }
        });

        timerPersistanceContainer = TimerPersistanceContainer.getContainer();

        actionBar = findViewById(R.id.modifier_actionbar);
        setSupportActionBar(actionBar);

        Log.v(LOG_TAG, "modifier | activity started");

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
                this.checkAndResetSelection();
                timePickerDialog.show();
                return true;

            case R.id.delete_modifier:
                this.checkAndResetSelection();
                adapter.deleteSelectedContents();
                return true;

            case R.id.save_modifier:
                this.checkAndResetSelection();
                /*
                if name has been given and at least a single timer has been added, the response
                is encapsulated into TimerBox and pushed into PersistenceContainer
                 */
                if((!name_label.getText().toString().equals(getString(R.string.filler_edit_text))) && (timerBox.getTimerCount()>0)){
                    String name = name_label.getText().toString();
                    int repetition = Integer.parseInt(repetition_counter.getText().toString());
                    Intent resultIntent = new Intent(ModifierActivity.this, MainActivity.class);
                    timerBox.setName(name);
                    timerBox.setRepetitions(repetition);
                    timerPersistanceContainer.insertTimerBox(timerBox);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * the function prepares the dialog to be displayed when timer is to be added
     */
    void prepareDialog(){
        this.timePickerDialog.setTitle("Set time");
        timePickerDialog.setContentView(R.layout.time_selector_dialog);
        minutesPicker = timePickerDialog.findViewById(R.id.minute_picker);
        secondsPicker = timePickerDialog.findViewById(R.id.second_picker);

        minutesPicker.setMaxValue(60);
        minutesPicker.setMinValue(0);

        secondsPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);

        minutesPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                //if minute picker is changed to 60, second picker is to be disabled
                if(minutesPicker.getValue() == 60){
                    secondsPicker.setValue(0);
                    secondsPicker.setEnabled(false);
                }
                else {
                    secondsPicker.setEnabled(true);
                }
            }
        });

        Button affirmativeButton = timePickerDialog.findViewById(R.id.button_ok);
        affirmativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int timerValue = minutesPicker.getValue()*60+secondsPicker.getValue();
                // the dialog should be closed only if the timer value is set to non-zero. else press cancel
                if(timerValue != 0){
                    timerBox.addTimer(timerValue);
                    adapter.notifyDataSetChanged();
                    timePickerDialog.dismiss();
                }
                else {
                    Toast.makeText(ModifierActivity.this, "0s Timer is invalid!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button negativeButton = timePickerDialog.findViewById(R.id.button_cancel);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.dismiss();
            }
        });

    }

    /**
     * Function to check if any unwanted focus still remains and removes them
     */
    void checkAndResetSelection(){
        //check if any items selected in the list and resets them
        if(adapter.isItemsSelected){
            adapter.clearSelection();
            changeDeleteButtonVisibility();
        }
        //if name label is focused, the focus is removed
        if(name_label.getText().toString().length() == 0){
            name_label.setText(R.string.filler_edit_text);
            name_label.clearFocus();
        }
        //if repetition counter is set to zero or not set, reset to 1
        if(repetition_counter.getText().toString().length() == 0){
            repetition_counter.setText("1");
            name_label.clearFocus();
        }
    }

    /**
     * function to check if delete button is to be displayed
     *
     * if selectedItems list is empty, the button is removed
     * else displayed
     */
    void changeDeleteButtonVisibility(){
        int countOfSelectedItems = adapter.selectedItems.size();
        if(countOfSelectedItems > 0){
            this.deleteButton.setVisible(true);
        }
        else {
            this.deleteButton.setVisible(false);
        }
    }

    /**
     * Class to contain the timer list
     */
    public class TimerModifierListAdapter extends RecyclerView.Adapter<TimerModifierListAdapter.TimerViewHolder> {

        //flag to maintain if any items are selected for processing
        boolean isItemsSelected;
        //list to maintain position of the selected items
        ArrayList<Integer> selectedItems;

        TimerModifierListAdapter() {
            selectedItems = new ArrayList<>();
            isItemsSelected = false;
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
                        //the text color is changed to white, if the items was previously selected
                        holder.timerText.setTextColor(getResources().getColor(R.color.white_text));

                    }
                    else {
                        selectedItems.add(position);
                        //the text color is changed color, if the items was previously not selected
                        holder.timerText.setTextColor(getResources().getColor(R.color.highlighted_text));
                    }
                    //flag checked
                    isItemsSelected = selectedItems.size()>0;

                    changeDeleteButtonVisibility();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return timerBox.getTimerList().size();
        }

        //the list of selected items is reset and flag reset
        void clearSelection(){
            if(selectedItems.size()>0){
                selectedItems.clear();
                isItemsSelected = false;
                this.notifyDataSetChanged();
            }
        }

        //the items selected for processing is deleted
        void deleteSelectedContents(){
            //the selected items are sorted in descending order
            Collections.sort(adapter.selectedItems);
            Collections.reverse(adapter.selectedItems);
            //the selected items are deleted one by one
            for (int position: adapter.selectedItems){
                timerBox.deleteTimerAtPosition(position);
            }
            clearSelection();
        }


        private String convertLongToTimeStamp(long timerInSeconds){
            String result = "";
            long minutes = timerInSeconds / 60;
            long seconds = timerInSeconds % 60;

            if(minutes == 0){
                result+="00:";
            }
            else if(minutes>0 && minutes<10){
                result = result + "0" + (minutes) + ":";
            }
            else {
                result = result + (minutes) + ":";
            }

            if(seconds == 0){
                result+="00";
            }
            else if(seconds>0 && seconds<10){
                result = result + "0" + (seconds);
            }
            else {
                result = result + (seconds);
            }

            return result;
        }


    }
}

