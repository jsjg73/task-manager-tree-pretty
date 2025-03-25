package org.example;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class TaskManagerTreePretty extends JFrame {
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private JTree taskTree;
    private JTextArea descriptionArea;
    private JLabel currentTaskLabel;

    public TaskManagerTreePretty() {
        // Nimbus Look and Feel 적용
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch(Exception e) {
            System.out.println("Nimbus 적용 실패: " + e);
        }

        setTitle("작업 관리 프로그램");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout(10, 10));

        // 좌측 패널: 작업 트리
        rootNode = new DefaultMutableTreeNode("모든 작업");
        treeModel = new DefaultTreeModel(rootNode);
        taskTree = new JTree(treeModel);
        taskTree.setCellRenderer(new TaskTreeCellRenderer());
        taskTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane treeScroll = new JScrollPane(taskTree);
        treeScroll.setPreferredSize(new Dimension(300, 500));
        treeScroll.setBorder(new TitledBorder(new EtchedBorder(), "작업 목록"));
        add(treeScroll, BorderLayout.WEST);

        // 우측 패널: 세부 정보 및 컨트롤 영역
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 상단: 현재 작업 상세 정보
        currentTaskLabel = new JLabel("작업을 선택하세요.");
        currentTaskLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        rightPanel.add(currentTaskLabel, BorderLayout.NORTH);

        descriptionArea = new JTextArea(10, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(new TitledBorder(new EtchedBorder(), "작업 설명"));
        rightPanel.add(descScroll, BorderLayout.CENTER);

        // 하단: 버튼 영역
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton updateDescriptionButton = new JButton("설명 업데이트");
        updateDescriptionButton.addActionListener(e -> updateDescription());
        buttonPanel.add(updateDescriptionButton);

        JButton newTaskButton = new JButton("새 작업 시작");
        newTaskButton.addActionListener(e -> addNewRootTask());
        buttonPanel.add(newTaskButton);

        JButton subTaskButton = new JButton("하위 작업 시작");
        subTaskButton.addActionListener(e -> addSubTask());
        buttonPanel.add(subTaskButton);

        JButton finishTaskButton = new JButton("작업 완료");
        finishTaskButton.addActionListener(e -> finishTask());
        buttonPanel.add(finishTaskButton);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.CENTER);

        // 트리 선택 시 상세 정보 갱신
        taskTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) taskTree.getLastSelectedPathComponent();
            if(selectedNode != null && selectedNode != rootNode) {
                Object userObj = selectedNode.getUserObject();
                if(userObj instanceof Task){
                    Task task = (Task) userObj;
                    currentTaskLabel.setText("현재 작업: " + task.getName());
                    descriptionArea.setText(task.getDescription());
                }
            } else {
                currentTaskLabel.setText("작업을 선택하세요.");
                descriptionArea.setText("");
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 선택한 작업의 설명 업데이트
    private void updateDescription() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) taskTree.getLastSelectedPathComponent();
        if(selectedNode == null || selectedNode == rootNode) {
            JOptionPane.showMessageDialog(this, "작업을 선택하세요.");
            return;
        }
        Task task = (Task) selectedNode.getUserObject();
        task.setDescription(descriptionArea.getText());
        treeModel.nodeChanged(selectedNode);
    }

    // 새 루트 작업 추가 (기존 작업은 그대로 남음)
    private void addNewRootTask() {
        String taskName = JOptionPane.showInputDialog(this, "새 작업 이름을 입력하세요:");
        if(taskName == null || taskName.trim().isEmpty()) return;
        Task task = new Task(taskName, 0);
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(task);
        treeModel.insertNodeInto(newNode, rootNode, rootNode.getChildCount());
        taskTree.expandPath(new TreePath(rootNode.getPath()));
    }

    // 선택한 작업의 하위 작업 추가
    private void addSubTask() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) taskTree.getLastSelectedPathComponent();
        if(selectedNode == null || selectedNode == rootNode) {
            JOptionPane.showMessageDialog(this, "하위 작업을 추가할 작업을 선택하세요.");
            return;
        }
        Task parentTask = (Task) selectedNode.getUserObject();
        String taskName = JOptionPane.showInputDialog(this, "하위 작업 이름을 입력하세요:");
        if(taskName == null || taskName.trim().isEmpty()) return;
        Task subTask = new Task(taskName, parentTask.getLevel() + 1);
        DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(subTask);
        treeModel.insertNodeInto(subNode, selectedNode, selectedNode.getChildCount());
        taskTree.expandPath(new TreePath(selectedNode.getPath()));
    }

    // 선택한 작업 완료 처리 (노드 삭제 없이 완료 표시)
    private void finishTask() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) taskTree.getLastSelectedPathComponent();
        if(selectedNode == null || selectedNode == rootNode) {
            JOptionPane.showMessageDialog(this, "완료할 작업을 선택하세요.");
            return;
        }
        Task task = (Task) selectedNode.getUserObject();
        task.setFinished(true);
        JOptionPane.showMessageDialog(this, "완료한 작업: " + task.getName());
        treeModel.nodeChanged(selectedNode);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TaskManagerTreePretty());
    }


    // 커스텀 셀 렌더러로 작업 상태와 글꼴, 색상 등을 조정
    class TaskTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component comp = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if(value instanceof DefaultMutableTreeNode){
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
                Object userObj = node.getUserObject();
                if(userObj instanceof Task){
                    Task task = (Task) userObj;
                    String text = task.isFinished() ? "[완료] " : "";
                    text += task.getName();
                    setText(text);
                    if(task.isFinished()){
                        setForeground(Color.GRAY);
                        setFont(getFont().deriveFont(Font.ITALIC));
                    } else {
                        setForeground(Color.BLACK);
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                }
            }
            return comp;
        }

    }

    // Task 클래스 (작업명, 설명, 레벨, 완료 여부)
    public static class Task {
        private String name;
        private String description;
        private int level; // 루트 작업은 0, 하위 작업은 부모 level + 1
        private boolean finished;

        public Task(String name, int level) {
            this.name = name;
            this.level = level;
            this.description = "";
            this.finished = false;
        }

        public String getName() { return name; }
        public int getLevel() { return level; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isFinished() { return finished; }
        public void setFinished(boolean finished) { this.finished = finished; }

        @Override
        public String toString() {
            return name;
        }
    }
}
