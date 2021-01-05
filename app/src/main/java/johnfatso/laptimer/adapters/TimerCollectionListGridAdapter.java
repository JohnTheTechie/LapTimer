package johnfatso.laptimer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import johnfatso.laptimer.MainActivity;
import johnfatso.laptimer.R;
import johnfatso.laptimer.servicesubsystems.Converters;
import johnfatso.laptimer.timerdbms.SimpleTimerSequenceContainer;

public class TimerCollectionListGridAdapter extends RecyclerView.Adapter<TimerCollectionListGridAdapter.GridViewHolder> {

    // types of list item views
    private static final int SEQUENCE_CONTAINER = 0x01;
    private static final int ADD_CONTAINER = 0x02;

    // collection of timer sequences
    private final ArrayList<SimpleTimerSequenceContainer> collection;
    MainActivity activity;

    public TimerCollectionListGridAdapter(ArrayList<SimpleTimerSequenceContainer> collection, MainActivity activity) {
        this.collection = collection;
        this.activity = activity;
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {

        SimpleTimerSequenceContainer node;

        ConstraintLayout container;
        TextView timerName;
        TextView timerDuration;
        ImageButton editButton;
        ImageButton deleteButton;
        ImageButton addButton;

        int viewType;

        public GridViewHolder(@NonNull View itemView, TextView timerName, TextView timerDuration, ImageButton editButton, ImageButton deleteButton, ConstraintLayout container, int viewType) {
            super(itemView);
            this.timerName = timerName;
            this.timerDuration = timerDuration;
            this.editButton = editButton;
            this.deleteButton = deleteButton;
            this.container = container;
            this.viewType = viewType;
        }

        public GridViewHolder(@NonNull View itemView, ImageButton addButton, int viewType) {
            super(itemView);
            this.addButton = addButton;
            this.viewType = viewType;
        }
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case ADD_CONTAINER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.main_activity_grid_add_new_layout, parent, false);
                ImageButton addButton = view.findViewById(R.id.main_activity_grid_add_new_Button);
                return new GridViewHolder(view, addButton, viewType);

            case SEQUENCE_CONTAINER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.main_activity_grid_list_layout, parent, false);
                TextView nameText = view.findViewById(R.id.main_grid_title);
                TextView durationText = view.findViewById(R.id.main_grid_timer_duration);
                ImageButton editButton = view.findViewById(R.id.main_grid_edit_button);
                ImageButton deleteButton = view.findViewById(R.id.main_grid_delete_button);
                ConstraintLayout container = view.findViewById(R.id.main_grid_container_frame);
                return new GridViewHolder(view, nameText, durationText, editButton, deleteButton, container, viewType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final GridViewHolder holder, final int position) {

        switch (holder.viewType){
            case ADD_CONTAINER:
                holder.addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TimerCollectionListGridAdapter.this.activity.createNewTimerRequest();
                    }
                });
                break;

            case SEQUENCE_CONTAINER:
                holder.node = this.collection.get(position);

                holder.timerName.setText(holder.node.getContainerName());

                holder.timerDuration.setText(Converters.timer_to_time_string(holder.node.getDurationOfExecutableList()));

                switch ((int)(holder.node.getUniqueID() % 4)){
                    case 0:
                        holder.container.setBackground(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.grid_list_item_background_blue,null));
                        break;

                    case 1:
                        holder.container.setBackground(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.grid_list_item_background_green,null));
                        break;

                    case 2:
                        holder.container.setBackground(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.grid_list_item_background_red,null));
                        break;

                    case 3:
                        holder.container.setBackground(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.grid_list_item_background_orange,null));
                        break;
                }

                holder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TimerCollectionListGridAdapter.this.activity.editRequest(holder.node.getContainerName());
                        Toast.makeText(activity.getApplicationContext(), "edit triggered", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(activity.getApplicationContext(), "delete not yet implemented", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TimerCollectionListGridAdapter.this.activity.triggerTimer(holder.node.getContainerName());
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return collection.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == collection.size()){
            return ADD_CONTAINER;
        }

        else {
            return SEQUENCE_CONTAINER;
        }
    }
}
