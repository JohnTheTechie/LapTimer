package johnfatso.laptimer.adapters;

import android.os.TestLooperManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import johnfatso.laptimer.R;
import johnfatso.laptimer.TimerCustomizationListItemFragment;
import johnfatso.laptimer.comminterfaces.SuperSubTreeFragmentCommunicator;
import johnfatso.laptimer.servicesubsystems.Converters;
import johnfatso.laptimer.timerdbms.TimerContainerNodeInterface;

public class TimerListRecyclerViewAdapter extends RecyclerView.Adapter<TimerListRecyclerViewAdapter.TimerViewHolder> {

    private static final String LOG_TAG = "EDITOR";
    private static final String CLASS_ID = "TimerListRecyclerViewAdapter";

    private static final int CONTENT_VIEW_TYPE_LEAF = 0x01;
    private static final int CONTENT_VIEW_TYPE_SUBTREE = 0x02;

    private final TimerContainerNodeInterface node;
    private final SuperSubTreeFragmentCommunicator superNode;
    private final TimerCustomizationListItemFragment fragment;

    public TimerListRecyclerViewAdapter(TimerContainerNodeInterface node, SuperSubTreeFragmentCommunicator superNode, TimerCustomizationListItemFragment fragment) {
        this.node = node;
        this.superNode = superNode;
        this.fragment = fragment;
        Log.v(LOG_TAG, CLASS_ID + " | adapter instance created for node level : "+this.node.getLevel());
    }

    static class TimerViewHolder extends RecyclerView.ViewHolder{

        TimerContainerNodeInterface nodeInTheHolder;
        int viewType;

        // leaf views
        TextView leafTimerTextView;
        TextView leafRepetitionTextView;
        ImageButton leafIncrementButton;
        ImageButton leafDecrementButton;
        ImageButton leafDeleteButton;

        // subtree views
        ConstraintLayout subtreeContainer;
        RecyclerView subtreeRecyclerView;
        TextView subtreeRepetitionTextView;
        ImageButton subtreeIncrementButton;
        ImageButton subtreeDecrementButton;
        ImageButton subtreeDeleteButton;
        ImageButton subtreeAddButton;

        public TimerViewHolder(@NonNull View itemView,
                               TextView leafTimerTextView,
                               TextView leafRepetitionTextView,
                               ImageButton leafIncrementButton,
                               ImageButton leafDecrementButton,
                               ImageButton leafDeleteButton) {
            super(itemView);
            this.nodeInTheHolder = nodeInTheHolder;
            this.leafTimerTextView = leafTimerTextView;
            this.leafRepetitionTextView = leafRepetitionTextView;
            this.leafIncrementButton = leafIncrementButton;
            this.leafDecrementButton = leafDecrementButton;
            this.leafDeleteButton = leafDeleteButton;
            this.viewType = TimerListRecyclerViewAdapter.CONTENT_VIEW_TYPE_LEAF;
            Log.v(LOG_TAG, CLASS_ID+" | ViewHolder created | viewType : "+viewType);
        }

        public TimerViewHolder(@NonNull View itemView,
                               RecyclerView subtreeRecyclerView,
                               TextView subtreeRepetitionTextView,
                               ImageButton subtreeIncrementButton,
                               ImageButton subtreeDecrementButton,
                               ImageButton subtreeDeleteButton,
                               ImageButton subtreeAddButton,
                               ConstraintLayout subtreeContainer) {
            super(itemView);
            this.nodeInTheHolder = nodeInTheHolder;
            this.subtreeRecyclerView = subtreeRecyclerView;
            this.subtreeRepetitionTextView = subtreeRepetitionTextView;
            this.subtreeIncrementButton = subtreeIncrementButton;
            this.subtreeDecrementButton = subtreeDecrementButton;
            this.subtreeDeleteButton = subtreeDeleteButton;
            this.subtreeAddButton = subtreeAddButton;
            this.subtreeContainer = subtreeContainer;
            this.viewType = TimerListRecyclerViewAdapter.CONTENT_VIEW_TYPE_SUBTREE;
            Log.v(LOG_TAG, CLASS_ID+" | ViewHolder created | viewType : "+viewType);
        }
    }

    @NonNull
    @Override
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case CONTENT_VIEW_TYPE_LEAF:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.timer_leaf_layout, parent, false);

                TextView timerText = view.findViewById(R.id.leaf_timer_text_view);
                TextView repetitionText = view.findViewById(R.id.leaf_repetition_count_text);
                ImageButton increButton = view.findViewById(R.id.leaf_repetition_increment_button);
                ImageButton decreButton = view.findViewById(R.id.leaf_repetition_decrement_button);
                ImageButton deleteButton = view.findViewById(R.id.leaf_delete_button);
                Log.v(LOG_TAG, CLASS_ID + " | onCreateViewHolder completed | for level:"+node.getLevel()+1);
                return new TimerViewHolder(view, timerText, repetitionText, increButton, decreButton, deleteButton);

            case CONTENT_VIEW_TYPE_SUBTREE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.timer_subtree_layout, parent, false);

                RecyclerView recyclerView = view.findViewById(R.id.timer_customization_list_item_recycler);
                TextView subtreeRepetionText = view.findViewById(R.id.subtree_repetition_count_text);
                ImageButton subtreeIncreButton = view.findViewById(R.id.subtree_repetition_increment_button);
                ImageButton subtreeDecreButton = view.findViewById(R.id.subtree_repetition_decrement_button);
                ImageButton subtreeDeleteButton = view.findViewById(R.id.subtree_delete_button);
                ImageButton subtreeAddButton = view.findViewById(R.id.timer_customization_list_item_recycler_add_button);
                ConstraintLayout subtreeContainer = view.findViewById(R.id.timer_customization_list_item_subtree);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(parent.getContext());
                recyclerView.setLayoutManager(layoutManager);

                Log.v(LOG_TAG, CLASS_ID + " | onCreateViewHolder completed | for level:"+node.getLevel()+1);
                return new TimerViewHolder(view, recyclerView, subtreeRepetionText, subtreeIncreButton, subtreeDecreButton, subtreeDeleteButton,subtreeAddButton, subtreeContainer);
        }

        throw new Error("incompatible viewtype");
    }

    @Override
    public void onBindViewHolder(@NonNull final TimerViewHolder holder, int position) {
        Log.v(LOG_TAG, CLASS_ID + " | onBindViewHolder started | for level:"+node.getLevel()+1);
        holder.nodeInTheHolder = this.node.getChildren().get(position);

        if (holder.viewType == CONTENT_VIEW_TYPE_LEAF) {

            holder.leafTimerTextView.setText(Converters.timer_to_time_string(holder.nodeInTheHolder.getTimer()));
            holder.leafRepetitionTextView.setText(Integer.toString(holder.nodeInTheHolder.getRepetition()));

            holder.leafIncrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int newRep = holder.nodeInTheHolder.getRepetition() + 1;
                    holder.nodeInTheHolder.setRepetition(newRep);
                    holder.leafRepetitionTextView.setText(Integer.toString(holder.nodeInTheHolder.getRepetition()));
                }
            });

            holder.leafDecrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int oldRep = holder.nodeInTheHolder.getRepetition();
                    int newRep = oldRep > 0 ? oldRep - 1 : 0;
                    holder.nodeInTheHolder.setRepetition(newRep);
                    holder.leafRepetitionTextView.setText(Integer.toString(holder.nodeInTheHolder.getRepetition()));
                }
            });

            holder.leafDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimerListRecyclerViewAdapter.this.node.getChildren().remove(holder.nodeInTheHolder);
                    notifyDataSetChanged();
                }
            });

        }

        else if (holder.viewType == CONTENT_VIEW_TYPE_SUBTREE){

            TimerListRecyclerViewAdapter adapter = new TimerListRecyclerViewAdapter(holder.nodeInTheHolder, null, fragment);
            holder.subtreeRecyclerView.setAdapter(adapter);

            switch (holder.nodeInTheHolder.getLevel()){
                case 1:
                    holder.subtreeContainer.setBackground(ResourcesCompat.getDrawable(fragment.getResources(), R.drawable.list_container_coded_bg_blue, null));
                    break;

                case 2:
                    holder.subtreeContainer.setBackground(ResourcesCompat.getDrawable(fragment.getResources(), R.drawable.list_container_coded_bg_green, null));
                    break;
            }

            holder.subtreeRepetitionTextView.setText(Integer.toString(holder.nodeInTheHolder.getRepetition()));

            holder.subtreeIncrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int newRep = holder.nodeInTheHolder.getRepetition() + 1;
                    holder.nodeInTheHolder.setRepetition(newRep);
                    holder.subtreeRepetitionTextView.setText(Integer.toString(holder.nodeInTheHolder.getRepetition()));
                }
            });

            holder.subtreeDecrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int oldRep = holder.nodeInTheHolder.getRepetition();
                    int newRep = oldRep > 0 ? oldRep - 1 : 0;
                    holder.nodeInTheHolder.setRepetition(newRep);
                    holder.subtreeRepetitionTextView.setText(Integer.toString(holder.nodeInTheHolder.getRepetition()));
                }
            });

            holder.subtreeDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimerListRecyclerViewAdapter.this.node.getChildren().remove(holder.nodeInTheHolder);
                    notifyDataSetChanged();
                }
            });

            holder.subtreeAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(fragment.getContext(), "add button not yet implemented", Toast.LENGTH_SHORT).show();
                }
            });

        }

        Log.v(LOG_TAG, CLASS_ID + " | onBindViewHolder completed | for level:"+node.getLevel()+1);
    }

    @Override
    public int getItemCount() {
        return node.getChildren().size();
    }

    @Override
    public int getItemViewType(int position) {

        int type = 0;

        if (this.node.getChildren().get(position).isLeaf()){
            type = CONTENT_VIEW_TYPE_LEAF;
        }
        else {
            type = CONTENT_VIEW_TYPE_SUBTREE;
        }

        return type;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull final TimerViewHolder holder) {
        Log.v(LOG_TAG, CLASS_ID + " | onViewAttachedToWindow started | for level:"+node.getLevel()+1);



        /*TimerCustomizationListItemFragment newFragment = TimerCustomizationListItemFragment.newInstance(holder.nodeInTheHolder, fragment);
        FragmentTransaction transaction = fragment.getChildFragmentManager().beginTransaction();
        transaction.add(holder.frame.getId(), newFragment);
        transaction.commit();*/

        super.onViewAttachedToWindow(holder);
        Log.v(LOG_TAG, CLASS_ID + " | onViewAttachedToWindow completed | for level:"+node.getLevel()+1);

    }


}
