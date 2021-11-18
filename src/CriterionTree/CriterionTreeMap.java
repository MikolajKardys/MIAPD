package CriterionTree;

import javax.swing.tree.TreeNode;
import java.util.HashMap;

public class CriterionTreeMap extends HashMap<TreeNode, double [][]> {
    public TreeNode getRoot(){
        TreeNode node = (TreeNode) this.keySet().toArray()[0];

        while (node.getParent() != null)
            node = node.getParent();

        return node;
    }
}
