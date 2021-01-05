package johnfatso.laptimer.timerdbms;

import android.util.Log;

import java.util.ArrayList;

import johnfatso.laptimer.exceptions.LeafAccessedAsSubtreeException;

public class TimerListLeaf implements TimerContainerNodeInterface{

    private static final String LOG_TAG = "TIMER_LIST_CONTAINER";
    private static final String CLASS_ID = "TimerListLeaf";

    private Long timer_in_seconds;
    private int repetitionCount;
    private int level;

    public TimerListLeaf(int level) {
        this.level = level;
        this.repetitionCount = 1;
    }

    public TimerListLeaf() {
        this.repetitionCount = 1;
    }

    /**
     * function to check if the node contains any info or another  root
     *
     * @return boolean
     */
    @Override
    public boolean isLeaf() {
        Log.v(LOG_TAG, CLASS_ID+" | isLeaf : "+true);
        return true;
    }

    /**
     * retrieves the level of the node in the tree
     * level of master root is 0
     *
     * @return int level of node
     */
    @Override
    public int getLevel() {
        Log.v(LOG_TAG, CLASS_ID+" | Leaf | level : "+level);
        return level;
    }

    /**
     * sets the level of the node
     *
     * @param level int node's level
     */
    @Override
    public void setLevel(int level) {
        Log.v(LOG_TAG, CLASS_ID+" | Leaf | level set : "+level);
        this.level = level;
    }

    /**
     * returns if the node at the index from the immediate next level is an info node or a subroot
     *
     * @param index int position in the immediate next level alone
     * @return boolean
     */
    @Override
    public boolean isLeafAtIndex(int index){
        throw new LeafAccessedAsSubtreeException();
    }

    /**
     * if the node is a value node, returns the info object
     * if not raise RootAccessedAsInfoException
     *
     * @return info
     */
    @Override
    public Long getTimer() {
        Log.v(LOG_TAG, CLASS_ID+" | Leaf | timer restrieved : "+timer_in_seconds+" s");
        return timer_in_seconds;
    }

    /**
     * sets the timer value in seconds
     *
     * @param timerInSeconds timer object
     */
    @Override
    public void setTimer(Long timerInSeconds){
        Log.v(LOG_TAG, CLASS_ID+" | Leaf | timer set : "+timerInSeconds+" s");
        this.timer_in_seconds = timerInSeconds;
    }

    /**
     * returns the repetition count for the sub tree contents or the leaf info
     *
     * @return int repetition count
     */
    @Override
    public int getRepetition() {
        Log.v(LOG_TAG, CLASS_ID+" | Leaf | repetition retrieved : "+repetitionCount);
        return repetitionCount;
    }

    /**
     * sets the repetition count for the subtree or the leaf
     *
     * @param repetition int repetitions count
     */
    @Override
    public void setRepetition(int repetition) {
        Log.v(LOG_TAG, CLASS_ID+" | Leaf | repetition set : "+repetition);
        this.repetitionCount = repetition;
    }

    /**
     * changes the repetition of the child at the mentioned index to the new value
     *
     * @param index position at which to change repetition
     * @param repetition number of repetition
     */
    @Override
    public void setRepetitionOfChildAt(int index, int repetition)  {
        throw new LeafAccessedAsSubtreeException();
    }

    /**
     * returns an list of child nodes from immediate next level, that belongs to this subroot
     * if not a subroot, raise InfoAccessedAsRootException
     *
     * @return Arraylist of children nodes
     */
    @Override
    public ArrayList<TimerContainerNodeInterface> getChildren()  {
        throw new Error("Leaf accessed as a subtree");
    }

    /**
     * add a child key to the list at the end
     *
     * @param key timer key object
     */
    @Override
    public void addChild(Long key)  {
        throw new LeafAccessedAsSubtreeException();
    }

    /**
     * add timer key at the specified index to the tree
     *
     * @param key   timer key
     * @param index int location where to push
     */
    @Override
    public void addChild(Long key, int index)  {
        throw new LeafAccessedAsSubtreeException();
    }

    /**
     * add the subtree to the tree to the end of the level
     *
     * @param subList subtree node
     */
    @Override
    public void addChild(TimerContainerNodeInterface subList)  {
        throw new LeafAccessedAsSubtreeException();
    }

    /**
     * add the subtree to the tree at the specified location
     *
     * @param subList subtree node
     * @param index   position where to push
     */
    @Override
    public void addChild(TimerContainerNodeInterface subList, int index)  {
        throw new LeafAccessedAsSubtreeException();
    }

    /**
     * add a list of containers to the end of the level
     *
     * @param listOfChildren arraylist of children
     */
    @Override
    public void addChildren(ArrayList<TimerContainerNodeInterface> listOfChildren)  {
        throw new LeafAccessedAsSubtreeException();
    }

    /**
     * add a lit of children from the location specified
     *
     * @param listOfChildren array list of children
     * @param index          from which index to add
     */
    @Override
    public void addChildren(ArrayList<TimerContainerNodeInterface> listOfChildren, int index) {
        throw new LeafAccessedAsSubtreeException();
    }

    /**
     * returns a count of immediate children
     *
     * @return int
     */
    @Override
    public int countOfChildren() {
        return 0;
    }

    /**
     * returns the total number of nodes from the entire subtree
     *
     * @return int total nodes
     */
    @Override
    public int sizeOfTheSubTree() {
        return 0;
    }

    /**
     * returns the height of the subtree
     *
     * @return int height of subtree
     */
    @Override
    public int heightOfSubTree() {
        return 0;
    }

    /**
     * function retrieves an expanded and serialized list of timer list
     *
     * @return arraylist of timers
     */
    @Override
    public ArrayList<Long> getExecutableTimerList() {
        ArrayList<Long> list = new ArrayList<>();
        list.add(this.timer_in_seconds);
        return list;
    }

    /**
     * deletes the child from the index
     * throws exception if leaf
     *
     * @param index location
     */
    @Override
    public void deleteChildrenAt(int index)  {
        throw new LeafAccessedAsSubtreeException();
    }
}
