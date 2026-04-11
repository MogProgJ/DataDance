package structlab.gui.visual.tree;

import javafx.scene.layout.Pane;

import java.util.List;

/**
 * Reusable canvas for rendering tree structures using {@link TreeNode}
 * and {@link TreeEdge} primitives with hierarchical (layered) layout.
 *
 * <p>Computes node positions for a complete binary tree layout and draws
 * curved edges between parent and child nodes.  This foundation supports
 * heap tree rendering now and can be extended for general tree/graph
 * layouts in the future.</p>
 *
 * <h3>Layout algorithm</h3>
 * Uses a top-down level-order placement:
 * <ul>
 *   <li>Level 0 (root): centered</li>
 *   <li>Level k: 2^k evenly spaced nodes</li>
 *   <li>Edges connect parent at index {@code i} to children at {@code 2i+1} and {@code 2i+2}</li>
 * </ul>
 */
public class TreeCanvas extends Pane {

    private static final double NODE_SIZE = 40;
    private static final double LEVEL_HEIGHT = 56;
    private static final double H_PADDING = 12;
    private static final double V_PADDING = 10;

    public TreeCanvas() {
        getStyleClass().add("tree-canvas");
        setMinHeight(50);
    }

    /**
     * Renders a binary heap as a tree with edges connecting parents to children.
     *
     * @param elements    heap array in level-order (index 0 = root)
     * @param rootEmphasized whether to emphasize the root node
     */
    public void renderHeapTree(List<String> elements, boolean rootEmphasized) {
        getChildren().clear();
        if (elements == null || elements.isEmpty()) return;

        int n = elements.size();
        int levels = (int) (Math.floor(Math.log(n) / Math.log(2))) + 1;

        // Calculate width: bottom level has up to 2^(levels-1) nodes
        int maxLeafCount = 1 << (levels - 1);
        double totalWidth = maxLeafCount * (NODE_SIZE + H_PADDING) + H_PADDING;
        double totalHeight = levels * LEVEL_HEIGHT + V_PADDING;

        setPrefSize(totalWidth, totalHeight);
        setMinWidth(totalWidth);

        // Calculate positions for every node
        double[] nodeX = new double[n];
        double[] nodeY = new double[n];

        for (int level = 0; level < levels; level++) {
            int levelStart = (1 << level) - 1;
            int levelCap = 1 << level;
            double levelWidth = totalWidth;
            double spacing = levelWidth / levelCap;

            for (int j = 0; j < levelCap && (levelStart + j) < n; j++) {
                int idx = levelStart + j;
                nodeX[idx] = spacing * j + spacing / 2;
                nodeY[idx] = V_PADDING + level * LEVEL_HEIGHT + NODE_SIZE / 2;
            }
        }

        // Draw edges first (behind nodes)
        for (int i = 0; i < n; i++) {
            int leftChild = 2 * i + 1;
            int rightChild = 2 * i + 2;

            if (leftChild < n) {
                boolean isRootEdge = (i == 0);
                TreeEdge edge = new TreeEdge(
                        nodeX[i], nodeY[i] + NODE_SIZE / 2 - 4,
                        nodeX[leftChild], nodeY[leftChild] - NODE_SIZE / 2 + 4,
                        isRootEdge);
                getChildren().add(edge);
            }
            if (rightChild < n) {
                boolean isRootEdge = (i == 0);
                TreeEdge edge = new TreeEdge(
                        nodeX[i], nodeY[i] + NODE_SIZE / 2 - 4,
                        nodeX[rightChild], nodeY[rightChild] - NODE_SIZE / 2 + 4,
                        isRootEdge);
                getChildren().add(edge);
            }
        }

        // Draw nodes on top
        for (int i = 0; i < n; i++) {
            boolean isRoot = (i == 0);
            TreeNode node = new TreeNode(elements.get(i), isRoot && rootEmphasized);
            node.setLayoutX(nodeX[i] - NODE_SIZE / 2);
            node.setLayoutY(nodeY[i] - NODE_SIZE / 2);
            getChildren().add(node);
        }
    }
}
