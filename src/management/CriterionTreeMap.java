package management;

import GUI.WindowObserver;
import arithmetics.PrioritizationMethod;
import arithmetics.Solver;
import arithmetics.graphCoherence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static arithmetics.Inconsistency.consistencyIndexGoldenWang;
import static arithmetics.Inconsistency.consistencyIndexSaatyHarker;

public class CriterionTreeMap extends HashMap<CriterionTreeNode, double[][]> implements WindowObserver {
    private final List<String> apples = new ArrayList<>();

    public static CriterionTreeMap getEmptyMap(){
        CriterionTreeMap map = new CriterionTreeMap();
        map.addCriteria("Criteria1", null);
        map.addApple("Choice1");
        map.put(map.getRoot(), new double[][]{{1.0}});

        return map;
    }

    public static CriterionTreeMap readFromFile(String fileName) {
        CriterionTreeMap newMap = new CriterionTreeMap();

        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(fileName)) {
            JSONArray list = (JSONArray) jsonParser.parse(reader);

            // CriterionNodes
            JSONArray nodes = (JSONArray) list.get(0);
            for (Object node : nodes) {
                newMap.addCriteria(
                        (String) ((JSONObject) node).get("CriterionName"),
                        (String) ((JSONObject) node).get("ParentName")
                );
            }

            // Apples
            JSONObject apples = (JSONObject) list.get(1);
            String[] appleNames = apples.get("appleNames").toString().split(";");
            for (String name : appleNames)
                newMap.addApple(name);

            // CriterionArrays
            JSONArray arrays = (JSONArray) list.get(2);
            for (Object array : arrays) {
                CriterionTreeNode node =
                        newMap.getCriterion((String) ((JSONObject) array).get("CriterionName"));

                String str = ((String) ((JSONObject) array).get("Array")).
                        replace("]", "").replace("[", "").replace(",", "");

                List<String> numbers = List.of(str.split(" "));
                int size = (int) (Math.sqrt(numbers.size()));

                double[][] intArray = new double[size][size];
                for (int i = 0; i < size; i++)
                    for (int j = 0; j < size; j++)
                        intArray[i][j] = Double.parseDouble(numbers.get(size * i + j));

                newMap.put(node, intArray);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return newMap;
    }

    private List<CriterionTreeNode> getLeavesRec(CriterionTreeNode node) {
        List<CriterionTreeNode> list = new ArrayList<>();
        if (node.isLeaf()) {
            list.add(node);
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                list.addAll(getLeavesRec(node.getChildAt(i)));
            }
        }
        return list;
    }

    public List<CriterionTreeNode> getLeaves() {
        return getLeavesRec(getRoot());
    }

    public CriterionTreeNode getCriterion(String criteriaName) {
        for (CriterionTreeNode node : keySet()) {
            if (node.toString().equals(criteriaName))
                return node;
        }
        return null;
    }

    private void expandMatrix(CriterionTreeNode criterion) {
        double[][] oldMatrix = get(criterion);
        double[][] newMatrix = new double[oldMatrix.length + 1][oldMatrix.length + 1];

        for (int i = 0; i < oldMatrix.length; i++) {
            System.arraycopy(oldMatrix[i], 0, newMatrix[i], 0, oldMatrix.length);
            newMatrix[i][oldMatrix.length] = 1;
        }
        for (int i = 0; i < newMatrix.length; i++)
            newMatrix[oldMatrix.length][i] = 1;

        put(criterion, newMatrix);
    }

    private void reduceMatrix(CriterionTreeNode criterion, int removeInd) {
        double[][] oldMatrix = get(criterion);
        double[][] newMatrix = new double[oldMatrix.length - 1][oldMatrix.length - 1];

        int skipX = 0;
        for (int i = 0; i < oldMatrix.length - 1; i++) {
            if (i == removeInd)
                skipX++;

            int skipY = 0;
            for (int j = 0; j < oldMatrix.length - 1; j++) {
                if (j == removeInd)
                    skipY++;

                newMatrix[i][j] = oldMatrix[i + skipX][j + skipY];
            }
        }

        put(criterion, newMatrix);
    }

    public void addApple(String appleName) {
        apples.add(appleName);

        List<CriterionTreeNode> leaves = getLeaves();
        if (leaves.size() == 0)
            return;

        for (CriterionTreeNode leaf : leaves) {
            expandMatrix(leaf);
        }
    }

    public void removeApple(String appleName) throws IllegalArgumentException {
        if (applesNumber() == 1) {
            throw new IllegalArgumentException("You must leave at least one choice");
        }
        int removeInd = apples.indexOf(appleName);

        List<CriterionTreeNode> leaves = getLeaves();
        for (CriterionTreeNode leaf : leaves) {
            reduceMatrix(leaf, removeInd);
        }

        apples.remove(appleName);
    }

    public void renameApple(String oldName, String newName) throws IllegalArgumentException {
        if (apples.contains(newName)) {
            throw new IllegalArgumentException("This name is already taken");
        }

        int index = apples.indexOf(oldName);
        apples.set(index, newName);
    }

    public void addCriteria(String criteriaName, String parent) {
        CriterionTreeNode newCriteria = new CriterionTreeNode(getCriterion(parent), criteriaName);

        double[][] newLeaf = new double[applesNumber()][applesNumber()];
        for (int i = 0; i < applesNumber(); i++)
            for (int j = 0; j < applesNumber(); j++)
                newLeaf[i][j] = 1;
        put(newCriteria, newLeaf);

        if (newCriteria.getParent() != null) {
            if (getCriterion(parent).getChildCount() == 1) {
                double[][] fields = {{1.0}};
                put(newCriteria.getParent(), fields);
            } else {
                expandMatrix(newCriteria.getParent());
            }
        }
    }

    public void removeCriteria(String criteriaName) throws IllegalArgumentException {
        CriterionTreeNode criterion = getCriterion(criteriaName);

        if (criterion.getParent() == null)
            throw new IllegalArgumentException("Error: You must leave at least one criteria");

        while (criterion.getChildCount() > 0) {
            CriterionTreeNode currChild = criterion.getChildAt(0);
            removeCriteria(currChild.toString());
        }

        int reduceInd = criterion.getParent().getChildInd(criterion);
        reduceMatrix(criterion.getParent(), reduceInd);

        criterion.delete();

        if (criterion.getParent().isLeaf()) {
            double[][] newLeaf = new double[applesNumber()][applesNumber()];
            for (int i = 0; i < applesNumber(); i++)
                for (int j = 0; j < applesNumber(); j++)
                    newLeaf[i][j] = 1;
            put(criterion.getParent(), newLeaf);
        }

        remove(criterion);
    }

    public void renameCriteria(String oldName, String newName) throws IllegalArgumentException {
        CriterionTreeNode criterion = getCriterion(oldName);

        Set<String> names = keySet().stream().map(CriterionTreeNode::toString).collect(Collectors.toSet());
        if (names.contains(newName)) {
            throw new IllegalArgumentException("This name is already taken");
        }

        double[][] matrix = remove(criterion);
        criterion.setCriterionName(newName);
        put(criterion, matrix);
    }

    @Override
    public int applesNumber() {
        return apples.size();
    }

    @Override
    public String getIthAppleName(int i) {
        return apples.get(i);
    }

    @Override
    public void setCAt(int i, int j, String criterionName, double value) {
        get(getCriterion(criterionName))[i][j] = value;
    }

    @Override
    public double getCAt(int i, int j, String criterionName) {
        return get(getCriterion(criterionName))[i][j];
    }

    @Override
    public CriterionTreeNode getRoot() {
        CriterionTreeNode node = (CriterionTreeNode) this.keySet().toArray()[0];

        while (node.getParent() != null)
            node = node.getParent();

        return node;
    }

    private boolean arePCTablesCorrectRec(CriterionTreeNode node) throws IllegalArgumentException {
        if (!graphCoherence.isConnected(this.get(node))) {
            throw new IllegalArgumentException("Insufficient data in table \"" + node.toString() + "\"");
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            arePCTablesCorrectRec(node.getChildAt(i));
        }
        return true;
    }

    @Override
    public boolean arePCTablesCorrect() throws IllegalArgumentException {
        return arePCTablesCorrectRec(getRoot());
    }

    @Override
    public double[] getRanking(PrioritizationMethod method) {
        return Solver.solveMultipleCriterion(this, method);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeToFile(String fileName) {
        StringBuilder appleString = new StringBuilder();
        for (String name : apples)
            appleString.append(name).append(";");

        JSONObject appleNames = new JSONObject();
        appleNames.put("appleNames", appleString.toString());

        JSONArray nodeTree = new JSONArray();

        int depth = 0;
        int firstDepth = depth;
        List<CriterionTreeNode> currNodes = keySet().stream().filter(e -> this.getDepth(e) == firstDepth)
                .collect(Collectors.toList());
        while (!currNodes.isEmpty()) {
            for (CriterionTreeNode criterion : currNodes) {
                JSONObject jsonCriterion = new JSONObject();

                jsonCriterion.put("CriterionName", criterion.toString());
                jsonCriterion.put("ParentName",
                        (criterion.getParent() == null ? "" : criterion.getParent().toString())
                );

                nodeTree.add(jsonCriterion);
            }

            int nextDepth = depth + 1;
            currNodes = keySet().stream().filter(e -> this.getDepth(e) == nextDepth).collect(Collectors.toList());
            depth++;
        }

        JSONArray arrays = new JSONArray();
        for (CriterionTreeNode criterion : keySet()) {
            JSONObject jsonCriterion = new JSONObject();

            jsonCriterion.put("CriterionName", criterion.toString());
            jsonCriterion.put("Array", Arrays.deepToString(get(criterion)));

            arrays.add(jsonCriterion);
        }

        //Add employees to list
        JSONArray list = new JSONArray();
        list.add(nodeTree);
        list.add(appleNames);
        list.add(arrays);

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(list.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getDepth(CriterionTreeNode criterion) {
        if (criterion.getParent() != null) {
            return 1 + getDepth(criterion.getParent());
        }
        return 0;
    }

    @Override
    public Map<CriterionTreeNode, int[]> getNodeOrder() {
        Map<CriterionTreeNode, int[]> orderMap = new HashMap<>();
        orderMap.put(getRoot(), new int[]{0, 0});

        int depth = 1;
        int finalDepth_0 = depth;
        List<CriterionTreeNode> currNodes = (new ArrayList<>(keySet())).stream()
                .filter(e -> getDepth(e) == finalDepth_0).collect(Collectors.toList());
        while (!currNodes.isEmpty()) {
            currNodes.sort((o1, o2) -> {
                int o1Parent = orderMap.get(o1.getParent())[1];
                int o2Parent = orderMap.get(o2.getParent())[1];

                if (o1Parent != o2Parent)
                    return o1Parent - o2Parent;

                if (o1.getChildCount() != o2.getChildCount())
                    return o2.getChildCount() - o1.getChildCount();

                return o1.toString().compareTo(o2.toString());
            });

            for (int i = 0; i < currNodes.size(); i++) {
                orderMap.put(currNodes.get(i), new int[]{depth, i});
            }

            depth++;
            int finalDepth_1 = depth;
            currNodes = (new ArrayList<>(keySet())).stream()
                    .filter(e -> getDepth(e) == finalDepth_1).collect(Collectors.toList());
        }

        return orderMap;
    }

    @Override
    public Map<String, Double> getIncIndex(CriterionTreeNode node) {
        Map<String, Double> indexMap = new HashMap<>();

        try {
            indexMap.put("Golden - Wang index", consistencyIndexGoldenWang(this, node));
            indexMap.put("Saaty - Harker index", consistencyIndexSaatyHarker(this, node));
        } catch (IllegalArgumentException e) {
            return null;
        }
        return indexMap;
    }


}
