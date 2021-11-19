package management;

import java.util.*;

public class CriterionTreeNode {
    private final CriterionTreeNode parent;
    private final List<CriterionTreeNode> children;

    private final String criterionName;

    public CriterionTreeNode(CriterionTreeNode parent, String criterionName) {
        this.parent = parent;
        this.criterionName = criterionName;

        if(parent != null)
            this.parent.children.add(this);

        this.children = new ArrayList<>();
    }

    @Override
    public String toString() {
        return criterionName;
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
        return that.criterionName.equals(this.criterionName);
    }

    public CriterionTreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    public int getChildCount() {
        return children.size();
    }

    public CriterionTreeNode getParent() {
        return parent;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }
}
