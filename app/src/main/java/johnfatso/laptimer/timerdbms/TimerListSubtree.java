package johnfatso.laptimer.timerdbms;

import android.util.Log;

import java.util.ArrayList;

import johnfatso.laptimer.exceptions.SubtreeAccessedAsLeafException;

public class TimerListSubtree implements TimerContainerNodeInterface {

    private static final String LOG_TAG = "TIMER_LIST_CONTAINER";
    private static final String CLASS_ID = "TimerListLeaf";

    private final ArrayList<TimerContainerNodeInterface> listOfChildren;
    private int repetitionCount;
    private int level;

    public TimerListSubtree(int level) {
        this.listOfChildren = new ArrayList<>();
        this.level = level;
        repetitionCount = 1;
    }

    public TimerListSubtree() {
        this.listOfChildren = new ArrayList<>();
        this.level = 0;
        this.repetitionCount = 1;
    }

    /**
     * function to check if the node contains any info or another  root
     *
     * @return boolean
     */
    @Override
    public boolean isLeaf() {
        Log.v(LOG_TAG, CLASS_ID+" | subtree | isLeaf : "+false);
        return false;
    }

    /**
     * retrieves the level of the node in the tree
     * level of master root is 0
     *
     * @return int level of node
     */
    @Override
    public int getLevel() {
        Log.v(LOG_TAG, CLASS_ID+" | subtree | level : "+level);
        return level;
    }

    /**
     * sets the level of the node
     *
     * @param level int node's level
     */
    @Override
    public void setLevel(int level) {
        this.level = level;
        for (TimerContainerNodeInterface node: this.listOfChildren){
            node.setLevel(level + 1);
        }
        Log.v(LOG_TAG, CLASS_ID+" | subtree | level set : "+level);
    }

    /**
     * returns if the node at the index from the immediate next level is an info node or a subroot
     *
     * @param index int position in the immediate next level alone
     * @return boolean
     */
    @Override
    public boolean isLeafAtIndex(int index) {
        if (index >= this.countOfChildren()){
            throw new IndexOutOfBoundsException("non existing index accessed in the subtree");
        }
        else {
            boolean isleaf = this.listOfChildren.get(index).isLeaf();
            Log.v(LOG_TAG, CLASS_ID+" | subtree | the child at "+ index + " is leaf? "+ isleaf);
            return isleaf;
        }
    }

    /**
     * if the node is a value node, returns the info object
     * if not raise RootAccessedAsInfoException
     *
     * @return info
     */
    @Override
    public Long getTimer() {
        throw new SubtreeAccessedAsLeafException();
    }

    /**
     * sets the timer value in seconds
     *
     * @param timerInSeconds timer object
     */
    @Override
    public void setTimer(Long timerInSeconds){
        throw new SubtreeAccessedAsLeafException();
    }

    /**
     * returns the repetition count for the sub tree contents or the leaf info
     *
     * @return int repetition count
     */
    @Override
    public int getRepetition() {
        Log.v(LOG_TAG, CLASS_ID+" | subtree | repetition count : "+repetitionCount);
        return repetitionCount;
    }

    /**
     * sets the repetition count for the subtree or the leaf
     *
     * @param repetition int repetitions count
     */
    @Override
    public void setRepetition(int repetition) {
        this.repetitionCount = repetition;
        Log.v(LOG_TAG, CLASS_ID+" | subtree | repetition set : " + repetition) ;
    }

    /**
     * changes the repetition of the child at the mentioned index to the new value
     *
     * @param index position at which to change repetition
     * @param repetition new repetition value
     */
    @Override
    public void setRepetitionOfChildAt(int index, int repetition) {
        this.listOfChildren.get(index).setRepetition(repetition);
        Log.v(LOG_TAG, CLASS_ID+" | subtree | repetition of child at " + index + " changed to "+repetition);
    }

    /**
     * returns an list of child nodes from immediate next level, that belongs to this subroot
     * if not a subroot, raise InfoAccessedAsRootException
     *
     * @return Arraylist of children nodes
     */
    @Override
    public ArrayList<TimerContainerNodeInterface> getChildren() {
        Log.v(LOG_TAG, CLASS_ID+" | subtree | list of children retrieved");
        return listOfChildren;
    }

    /**
     * add a child key to the list at the end
     *
     * @param key timer key object
     */
    @Override
    public void addChild(Long key) {
        TimerListLeaf leaf = new TimerListLeaf(level + 1);
        leaf.setRepetition(1);
        leaf.setTimer(key);
        Log.v(LOG_TAG, CLASS_ID+" | subtree | child added");
        this.listOfChildren.add(leaf);
    }

    /**
     * add timer key at the specified index to the tree
     *
     * @param key   timer key
     * @param index int location where to push
     */
    @Override
    public void addChild(Long key, int index) {
        TimerListLeaf leaf = new TimerListLeaf(level + 1);
        leaf.setRepetition(1);
        leaf.setTimer(key);
        Log.v(LOG_TAG, CLASS_ID+" | subtree | child added at index : "+index);
        this.listOfChildren.add(index, leaf);
    }

    /**
     * add the subtree to the tree to the end of the level
     *
     * @param subList subtree node
     */
    @Override
    public void addChild(TimerContainerNodeInterface subList) {
        subList.setLevel(this.level + 1);
        this.listOfChildren.add(subList);
        Log.v(LOG_TAG, CLASS_ID+" | subtree | subtree added");

    }

    /**
     * add the subtree to the tree at the specified location
     *
     * @param subList subtree node
     * @param index   position where to push
     */
    @Override
    public void addChild(TimerContainerNodeInterface subList, int index) {
        subList.setLevel(this.level + 1);
        this.listOfChildren.add(index, subList);
        Log.v(LOG_TAG, CLASS_ID+" | subtree | sub tree added at index "+index);
    }

    /**
     * add a list of containers to the end of the level
     *
     * @param listOfChildren arraylist of children
     */
    @Override
    public void addChildren(ArrayList<TimerContainerNodeInterface> listOfChildren) {
        for (TimerContainerNodeInterface node: listOfChildren){
            node.setLevel(this.level+1);
        }
        this.listOfChildren.addAll(listOfChildren);
        Log.v(LOG_TAG, CLASS_ID+" | subtree | list of children added");
    }

    /**
     * add a lit of children from the location specified
     *
     * @param listOfChildren array list of children
     * @param index          from which index to add
     */
    @Override
    public void addChildren(ArrayList<TimerContainerNodeInterface> listOfChildren, int index) {
        for (TimerContainerNodeInterface node: listOfChildren){
            node.setLevel(this.level + 1);
        }
        this.listOfChildren.addAll(index, listOfChildren);
        Log.v(LOG_TAG, CLASS_ID+" | subtree | list of children added at index "+index);
    }

    /**
     * returns a count of immediate children
     *
     * @return int
     */
    @Override
    public int countOfChildren() {
        int size = this.listOfChildren.size();
        Log.v(LOG_TAG, CLASS_ID+" | subtree | count of children :"+size);
        return size;
    }

    /**
     * returns the total number of nodes from the entire subtree
     *
     * @return int total nodes
     */
    @Override
    public int sizeOfTheSubTree() {
        int size = 0;
        for (TimerContainerNodeInterface node: listOfChildren){
            size += node.sizeOfTheSubTree();
        }
        size += this.listOfChildren.size();
        Log.v(LOG_TAG, CLASS_ID+" | subtree | size of subtree : "+size);
        return size;
    }

    /**
     * returns the height of the subtree
     *
     * @return int height of subtree
     */
    @Override
    public int heightOfSubTree() {
        int height = this.level;
        if (this.listOfChildren.size() > 0){
            height++;
            for (TimerContainerNodeInterface node: this.listOfChildren){
                if (!node.isLeaf()){
                    int subtreeHeight = node.heightOfSubTree();
                    if (subtreeHeight > height) height = subtreeHeight;
                }
            }
        }
        Log.v(LOG_TAG, CLASS_ID+" | subtree | height of subtree : "+height);
        return height;
    }

    /**
     * function retrieves an expanded and serialized list of timer list
     *
     * @return arraylist of timers
     */
    @Override
    public ArrayList<Long> getExecutableTimerList(){
        ArrayList<Long> templist = new ArrayList<>();

        for (TimerContainerNodeInterface node: this.listOfChildren){
            if (node.isLeaf()){
                long timer = node.getTimer();
                for (int i = 0; i < node.getRepetition() ; i ++){
                    templist.add(timer);
                }
            }
            else {
                templist = node.getExecutableTimerList();
                /*ArrayList<Long> timerlist = node.getExecutableTimerList();
                if (timerlist.size() != 0){
                    for (int i = 0; i < node.getRepetition() ; i ++){
                        templist.addAll(timerlist);
                    }
                }*/
            }
        }

        ArrayList<Long> returnableFullSizedList = new ArrayList<>();

        if (templist.size() != 0){
            for (int i = 0 ; i < this.repetitionCount ; i++) {
                returnableFullSizedList.addAll(templist);
            }
        }


        Log.v(LOG_TAG, CLASS_ID+" | subtree | executable list retrieved of length : "+returnableFullSizedList.size());
        return returnableFullSizedList;
    }

    /**
     * deletes the child from the index
     * throws exception if leaf
     *
     * @param index location
     */
    @Override
    public void deleteChildrenAt(int index) {
        this.listOfChildren.remove(index);
        Log.v(LOG_TAG, CLASS_ID+" | subtree | child at " + index + " removed");
    }
}
