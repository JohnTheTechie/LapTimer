package johnfatso.laptimer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import johnfatso.laptimer.adapters.TimerListRecyclerViewAdapter;
import johnfatso.laptimer.comminterfaces.SuperSubTreeFragmentCommunicator;
import johnfatso.laptimer.timerdbms.TimerContainerNodeInterface;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimerCustomizationListItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerCustomizationListItemFragment extends Fragment implements SuperSubTreeFragmentCommunicator {

    private static final String LOG_TAG = "EDITOR";
    private static final String CLASS_ID = "TimerCustomizationListItemFragment";

    private TimerContainerNodeInterface node;
    private boolean isLeaf;
    private int levelOfNode;
    private SuperSubTreeFragmentCommunicator superNode;

    FrameLayout fragmentFrame;

    // contents of subtree layout
    private ConstraintLayout container;
    private ImageButton addButton;
    private ImageButton subtreeIncrementButton;
    private ImageButton subtreeDecrementButton;
    private TextView subtreeRepetitionCounterTextView;
    private ConstraintLayout subtreeDeleteContainerFrame;
    private ImageButton subtreeDeleteButton;

    // recycler views and support for subtree
    RecyclerView recyclerView;
    TimerListRecyclerViewAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    public TimerCustomizationListItemFragment() {
        // Required empty public constructor
    }

    public static TimerCustomizationListItemFragment newInstance(TimerContainerNodeInterface node, SuperSubTreeFragmentCommunicator superNode) {
        TimerCustomizationListItemFragment fragment = new TimerCustomizationListItemFragment();

        fragment.node = node;
        fragment.superNode = superNode;
        fragment.isLeaf = fragment.node.isLeaf();
        fragment.levelOfNode = node.getLevel();

        Log.v(LOG_TAG, CLASS_ID+"| fragment Instance created | node | isleaf? "+fragment.isLeaf+" | level:"+fragment.levelOfNode);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, CLASS_ID+"| fragment created | node | isleaf? "+isLeaf+" | level:"+levelOfNode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(LOG_TAG, CLASS_ID+"| fragment onCreateView starting | adding fragment to container | "+ container +" | node | isleaf? "+isLeaf+" | level:"+levelOfNode);
        View view = inflater.inflate(R.layout.fragment_timer_customization_list_item, container, false);
        Log.v(LOG_TAG, CLASS_ID+"| fragment onCreateView done | node | isleaf? "+isLeaf+" | level:"+levelOfNode);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, CLASS_ID+"| fragment onViewCreated started | node | isleaf? "+isLeaf+" | level:"+levelOfNode);
        super.onViewCreated(view, savedInstanceState);

        fragmentFrame = view.findViewById(R.id.timer_customization_list_item_base_frame);

        View frameSubtree = getLayoutInflater().inflate(R.layout.timer_subtree_layout_master_list, fragmentFrame, false);
        fragmentFrame.addView(frameSubtree);
        this.container = frameSubtree.findViewById(R.id.timer_customization_list_item_subtree);
        this.recyclerView = frameSubtree.findViewById(R.id.timer_customization_list_item_recycler);
        this.addButton = frameSubtree.findViewById(R.id.timer_customization_list_item_recycler_add_button);
        this.subtreeIncrementButton = frameSubtree.findViewById(R.id.subtree_repetition_increment_button);
        this.subtreeDecrementButton = frameSubtree.findViewById(R.id.subtree_repetition_decrement_button);
        this.subtreeRepetitionCounterTextView = frameSubtree.findViewById(R.id.subtree_repetition_count_text);
        this.subtreeDeleteContainerFrame = frameSubtree.findViewById(R.id.timer_customization_list_item_subtree_delete_button_frame);
        this.subtreeDeleteButton = frameSubtree.findViewById(R.id.subtree_delete_button);

        preSubtreeLayout();

        Log.v(LOG_TAG, CLASS_ID+"| fragment onViewCreated completed | node | isleaf? "+isLeaf+" | level:"+levelOfNode);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOG_TAG, CLASS_ID+"| fragment onStart started | node | isleaf? "+isLeaf+" | level:"+levelOfNode);
        if (!this.isLeaf){

        }
    }

    /*
    superNodeCommunicator implementation
     */

    /**
     * deletes the child nodes from the node list
     *
     * @param nodeToDelete node to delete
     */
    @Override
    public void deleteTheNode(TimerContainerNodeInterface nodeToDelete) {
        this.node.getChildren().remove(nodeToDelete);
    }

    /*
    class specific functions
     */

    void preSubtreeLayout(){

        Log.v(LOG_TAG, CLASS_ID+"| subtree is being prepared | node | isleaf? "+isLeaf+" | level:"+levelOfNode);

        this.subtreeRepetitionCounterTextView.setText(Integer.toString(this.node.getRepetition()));
        // increment decrement buttons
        this.subtreeIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int repetition_count = TimerCustomizationListItemFragment.this.node.getRepetition()+1;
                TimerCustomizationListItemFragment.this.node.setRepetition(repetition_count);
                TimerCustomizationListItemFragment.this
                        .subtreeRepetitionCounterTextView.
                        setText(Integer.toString(TimerCustomizationListItemFragment.this.node.getRepetition()));
            }
        });

        this.subtreeDecrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int repetition_count = TimerCustomizationListItemFragment.this.node.getRepetition();
                repetition_count = repetition_count>0?repetition_count-1:0;
                TimerCustomizationListItemFragment.this.node.setRepetition(repetition_count);
                TimerCustomizationListItemFragment.this
                        .subtreeRepetitionCounterTextView.
                        setText(Integer.toString(TimerCustomizationListItemFragment.this.node.getRepetition()));
            }
        });

        this.subtreeDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimerCustomizationListItemFragment.this.superNode.deleteTheNode(TimerCustomizationListItemFragment.this.node);

            }
        });

        if (this.levelOfNode == 0){
            this.subtreeDeleteContainerFrame.setVisibility(View.GONE);
        }

        this.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "add not implemented yet", Toast.LENGTH_SHORT).show();
                // TODO: implement addition
            }
        });

        this.layoutManager = new LinearLayoutManager(getContext());
        this.recyclerView.setLayoutManager(layoutManager);
        this.adapter = new TimerListRecyclerViewAdapter(this.node, this.superNode, this);
        this.recyclerView.setAdapter(this.adapter);

        Log.v(LOG_TAG, CLASS_ID+"| subtree is being completed | node | isleaf? "+isLeaf+" | level:"+levelOfNode);

    }

}
