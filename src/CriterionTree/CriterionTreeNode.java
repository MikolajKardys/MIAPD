package CriterionTree;

import javax.swing.tree.TreeNode;
import java.util.*;

public class CriterionTreeNode implements TreeNode {
    private final CriterionTreeNode parent;
    private final List<CriterionTreeNode> children;

    private final String criterionName;

    public CriterionTreeNode(CriterionTreeNode parent, String criterionName) {
        this.parent = parent;
        this.criterionName = criterionName;

        if(parent != null)
            this.parent.children.add(this);

        children = new ArrayList<>();
    }

    @Override
    public int hashCode() {
        return criterionName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CriterionTreeNode that = (CriterionTreeNode)o;
        //maybe here should be tested C matrix
        return that.criterionName.equals(this.criterionName);
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        for(int i=0; i<children.size(); i++)
            if(children.get(i).equals(node))
                return i;
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return children.size() == 0;
    }

    @Override
    public Enumeration<? extends TreeNode> children() {
        return Collections.enumeration(children);
    }
}
