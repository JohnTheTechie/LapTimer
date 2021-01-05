package johnfatso.laptimer;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

import johnfatso.laptimer.timerdbms.BlobMaker;
import johnfatso.laptimer.timerdbms.SimpleTimerSequenceContainer;
import johnfatso.laptimer.timerdbms.TimerContainerNodeInterface;
import johnfatso.laptimer.timerdbms.TimerListLeaf;
import johnfatso.laptimer.timerdbms.TimerListSubtree;

public class TimerCollectionTreeUnitTest {

    SimpleTimerSequenceContainer container;
    ArrayList<Long> specList;

    @Before
    public void setUp() throws Exception {
        container = new SimpleTimerSequenceContainer("test");

        TimerListSubtree st1 = new TimerListSubtree();
        st1.setRepetition(3);
        st1.addChild((long) 40);
        st1.addChild((long) 20);

        TimerListLeaf l1 = new TimerListLeaf();
        l1.setRepetition(1);
        l1.setTimer((long) 40);

        /*st1.setRepetition(2);
        st1.addChild((long) 2);
        st1.addChild((long) 3);
        TimerListSubtree st4 = new TimerListSubtree();
        st4.setRepetition(2);
        st4.addChild((long) 1);
        st1.addChild(st4, 0);
        TimerListSubtree st2 = new TimerListSubtree();
        st2.setRepetition(3);
        st2.addChild((long) 4);
        st2.addChild((long) 5);
        st2.setRepetitionOfChildAt(1, 2);
        TimerListSubtree st3 = new TimerListSubtree();
        st3.setRepetition(1);
        st3.addChild((long) 6);
        TimerListSubtree st5 = new TimerListSubtree();
        st5.setRepetition(1);
        st3.addChild(st5);
        TimerListLeaf leaf = new TimerListLeaf();
        leaf.setTimer((long) 7);
        leaf.setRepetition(2);*/

        ArrayList<TimerContainerNodeInterface> list = new ArrayList<>();

        list.add(st1);
        list.add(l1);

        /*list.add(st1);
        list.add(st2);
        list.add(st3);
        list.add(leaf);*/

        container.getRootNode().addChildren(list);
        container.getRootNode().setRepetition(3);

        specList = new ArrayList<>();

        specList.add((long) 40);
        specList.add((long) 20);
        specList.add((long) 40);
        specList.add((long) 20);
        specList.add((long) 40);
        specList.add((long) 20);
        specList.add((long) 40);

        specList.add((long) 40);
        specList.add((long) 20);
        specList.add((long) 40);
        specList.add((long) 20);
        specList.add((long) 40);
        specList.add((long) 20);
        specList.add((long) 40);

        specList.add((long) 40);
        specList.add((long) 20);
        specList.add((long) 40);
        specList.add((long) 20);
        specList.add((long) 40);
        specList.add((long) 20);
        specList.add((long) 40);
        /*specList.add((long) 1);
        specList.add((long) 1);
        specList.add((long) 2);
        specList.add((long) 3);
        specList.add((long) 1);
        specList.add((long) 1);
        specList.add((long) 2);
        specList.add((long) 3);
        specList.add((long) 4);
        specList.add((long) 5);
        specList.add((long) 5);
        specList.add((long) 4);
        specList.add((long) 5);
        specList.add((long) 5);
        specList.add((long) 4);
        specList.add((long) 5);
        specList.add((long) 5);
        specList.add((long) 6);
        specList.add((long) 7);
        specList.add((long) 7);*/
    }

    @Test
    public void testExtendedFullTest(){
        ArrayList<Long> testList = new ArrayList<>();
        try{
            testList = container.getRootNode().getExecutableTimerList();
        }catch (Exception e){
            assertEquals("Should have passed", "But a stupid exception was thrown : "+e.getMessage());
        }
        assertEquals(specList.size(), testList.size());
        assertEquals(specList.size(), container.getExecutableList().size());
        for(int i = 0; i < testList.size() ; i++){
            System.out.print(testList.get(i) + "-");
            assertEquals(testList.get(i), specList.get(i));
        }
        assertEquals(testList, specList);

    }

    @Test
    public void jsonable_test(){
        try {
            String json = BlobMaker.timerTreeToBlob(container.getRootNode());
            System.out.println("container has "+container.getRootNode().getExecutableTimerList().size()+" timer items");
            System.out.println(json);


            TimerContainerNodeInterface node = BlobMaker.blobToTimerTree(json);
            ArrayList<Long> testlist = node.getExecutableTimerList();
            assertEquals(testlist, specList);
        }catch (Exception e){
            assertEquals("Should have passed", "But a stupid exception was thrown : "+e.getMessage());
        }

    }

    @Test
    public void treeCharacteristicsTest(){
        int height = 0;
        try {
            height = container.getRootNode().heightOfSubTree();
            System.out.println("height is read as "+height);
        }catch (Exception e){
            assertEquals("Should have passed", "But a stupid exception was thrown : "+e.getMessage());
        }
        assertEquals(0, container.getRootNode().getLevel());
        assertEquals(2, height);
        /*assertEquals(3, height);*/
    }
}
