package johnfatso.laptimer.timerdbms;

import java.util.ArrayList;

public interface TimerContainerNodeInterface {

    /**
     * function to check if the node contains any info or another  root
     *
     * @return boolean
     */
    boolean isLeaf();

    /**
     * retrieves the level of the node in the tree
     * level of master root is 0
     *
     * @return int level of node
     */
    int getLevel();

    /**
     * sets the level of the node
     *
     * @param level int node's level
     */
    void setLevel(int level);

    /**
     * returns if the node at the index from the immediate next level is an info node or a subroot
     *
     * @param index int position in the immediate next level alone
     * @return boolean
     */
    boolean isLeafAtIndex(int index);

    /**
     * if the node is a value node, returns the info object
     * if not raise RootAccessedAsInfoException
     *
     * @return info
     */
    Long getTimer();

    /**
     * sets the timer value in seconds
     *
     * @param timerInSeconds timer object
     */
    void setTimer(Long timerInSeconds);

    /**
     * returns the repetition count for the sub tree contents or the leaf info
     *
     * @return int repetition count
     */
    int getRepetition();

    /**
     * sets the repetition count for the subtree or the leaf
     *
     * @param repetition int repetitions count
     */
    void setRepetition(int repetition);

    /**
     * changes the repetition of the child at the mentioned index to the new value
     *
     * @param index position at which to change repetition
     * @param repetition new repetition
     */
    void setRepetitionOfChildAt(int index, int repetition);

    /**
     * returns an list of child nodes from immediate next level, that belongs to this subroot
     * if not a subroot, raise InfoAccessedAsRootException
     *
     * @return Arraylist of children nodes
     */
    ArrayList<TimerContainerNodeInterface> getChildren();

    /**
     * add a child key to the list at the end
     *
     * @param key timer key object
     */
    void addChild(Long key);

    /**
     * add timer key at the specified index to the tree
     *
     * @param key timer key
     * @param index int location where to push
     */
    void addChild(Long key, int index);

    /**
     * add the subtree to the tree to the end of the level
     *
     * @param subList subtree node
     */
    void addChild(TimerContainerNodeInterface subList);

    /**
     * add the subtree to the tree at the specified location
     *
     * @param subList subtree node
     * @param index position where to push
     */
    void addChild(TimerContainerNodeInterface subList, int index);

    /**
     * add a list of containers to the end of the level
     *
     * @param listOfChildren arraylist of children
     */
    void addChildren(ArrayList<TimerContainerNodeInterface> listOfChildren);

    /**
     * add a lit of children from the location specified
     *
     * @param listOfChildren array list of children
     * @param index from which index to add
     */
    void addChildren(ArrayList<TimerContainerNodeInterface> listOfChildren, int index) ;

    /**
     * deletes the child from the index
     * throws exception if leaf
     *
     * @param index location
     */
    void deleteChildrenAt(int index) ;

    /**
     * returns a count of immediate children
     *
     * @return int
     */
    int countOfChildren();

    /**
     * returns the total number of nodes from the entire subtree
     *
     * @return int total nodes
     */
    int sizeOfTheSubTree();

    /**
     * returns the height of the subtree
     *
     * @return int height of subtree
     */
    int heightOfSubTree();

    /**
     * function retrieves an expanded and serialized list of timer list
     *
     * @return arraylist of timers
     */
    ArrayList<Long> getExecutableTimerList();
}
