// TaskTreeModelTest.java
import static org.junit.jupiter.api.Assertions.*;

import org.example.TaskManagerTreePretty;
import org.example.TaskManagerTreePretty.Task;
import org.junit.jupiter.api.Test;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * DefaultTreeModel과 DefaultMutableTreeNode를 이용해
 * 작업(루트 작업, 하위 작업 추가, 완료 처리 등)을 관리하는 로직을 검증하는 테스트입니다.
 */
class TaskTreeModelTest {

    @Test
    public void testAddNewRootTaskToTree() {
        // 가상의 루트 노드 생성 ("모든 작업")
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("모든 작업");
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

        // 새 루트 작업 추가
        Task task = new Task("Root Task", 0);
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(task);
        treeModel.insertNodeInto(newNode, rootNode, rootNode.getChildCount());

        // 루트의 자식 노드가 1개인지 확인
        assertEquals(1, rootNode.getChildCount(), "루트 작업이 추가되어야 합니다.");

        // 추가된 노드의 Task 정보 검증
        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) rootNode.getChildAt(0);
        Task childTask = (Task) childNode.getUserObject();
        assertEquals("Root Task", childTask.getName(), "작업 이름이 일치해야 합니다.");
        assertEquals(0, childTask.getLevel(), "루트 작업의 레벨은 0이어야 합니다.");
    }

    @Test
    public void testAddSubTaskToTree() {
        // 가상의 루트 노드 생성
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("모든 작업");
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

        // 루트 작업 추가
        Task rootTask = new Task("Root Task", 0);
        DefaultMutableTreeNode rootTaskNode = new DefaultMutableTreeNode(rootTask);
        treeModel.insertNodeInto(rootTaskNode, rootNode, rootNode.getChildCount());

        // 하위 작업 추가 (루트 작업의 자식)
        Task subTask = new Task("Sub Task", rootTask.getLevel() + 1);
        DefaultMutableTreeNode subTaskNode = new DefaultMutableTreeNode(subTask);
        treeModel.insertNodeInto(subTaskNode, rootTaskNode, rootTaskNode.getChildCount());

        // 루트 작업 노드의 자식이 1개인지 확인
        assertEquals(1, rootTaskNode.getChildCount(), "하위 작업이 추가되어야 합니다.");

        // 추가된 하위 작업의 Task 정보 검증
        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) rootTaskNode.getChildAt(0);
        Task childTask = (Task) childNode.getUserObject();
        assertEquals("Sub Task", childTask.getName(), "하위 작업의 이름이 일치해야 합니다.");
        assertEquals(1, childTask.getLevel(), "하위 작업의 레벨은 1이어야 합니다.");
    }

    @Test
    public void testFinishTaskInTree() {
        // 가상의 루트 노드 생성
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("모든 작업");
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

        // 작업 추가
        Task task = new Task("Task to Finish", 0);
        DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(task);
        treeModel.insertNodeInto(taskNode, rootNode, rootNode.getChildCount());

        // 작업 완료 처리 (노드 삭제 없이 완료 표시)
        task.setFinished(true);

        // 완료 상태가 반영되었는지 검증
        Task finishedTask = (Task) taskNode.getUserObject();
        assertTrue(finishedTask.isFinished(), "작업이 완료 상태여야 합니다.");
    }
}
