package management;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class CriterionTreeMap extends HashMap<CriterionTreeNode, double [][]> implements WindowObserver{
    private final List<String> apples = new ArrayList<>();

    private List<CriterionTreeNode> getLeavesRec(CriterionTreeNode node){
        List<CriterionTreeNode> list = new ArrayList<>();
        if (node.isLeaf()){
            list.add(node);
        }
        else{
            for (int i = 0; i < node.getChildCount(); i++){
                list.addAll(getLeavesRec(node.getChildAt(i)));
            }
        }
        return list;
    }
    public List<CriterionTreeNode> getLeaves(){
        return getLeavesRec(getRoot());
    }

    public CriterionTreeNode getCriterion (String criteriaName){
        for (CriterionTreeNode node : keySet()){
            if (node.toString().equals(criteriaName))
                return node;
        }
        return null;
    }

    private void expandMatrix(CriterionTreeNode criterion){
        double [][] oldMatrix = get(criterion);
        double [][] newMatrix = new double[oldMatrix.length + 1][oldMatrix.length + 1];

        for (int i = 0; i < oldMatrix.length; i++)
            System.arraycopy(oldMatrix[i], 0, newMatrix[i], 0, oldMatrix.length);

        put(criterion, newMatrix);
    }

    public void addApple(String appleName){
        apples.add(appleName);

        List<CriterionTreeNode> leaves = getLeaves();
        if (leaves.size() == 0)
            return;

        for (CriterionTreeNode leaf : leaves){
            expandMatrix(leaf);
        }
    }

    public void addCriteria(String criteriaName, String parent){
        CriterionTreeNode newCriteria = new CriterionTreeNode(getCriterion(parent), criteriaName);

        put(newCriteria, new double[applesNumber()][applesNumber()]);

        if (newCriteria.getParent() != null){
            expandMatrix(newCriteria.getParent());
        }
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

    @Override
    public boolean isPCTablesCorrect() {
        return false;
    }

    @Override
    public double[] getRanking() {
        return new double[0];
    }


    private int getDepth(CriterionTreeNode criterion){
        if (criterion.getParent() != null){
            return 1 + getDepth(criterion.getParent());
        }
        return 0;
    }
    @Override
    public int [] getGraphIndex(String criterionName){
        CriterionTreeNode criterion = getCriterion(criterionName);

        int [] index = new int [2];
        if (criterion.getParent() == null){
            return index;
        }
        index[0] = getDepth(criterion);

        List<CriterionTreeNode> sameDepth = new ArrayList<>();
        for (CriterionTreeNode node : keySet()){
            if (getDepth(node) == index[0]){
                sameDepth.add(node);
            }
        }
        if (!(sameDepth.size() == 1)){
            int inFront = 0;
            for (CriterionTreeNode node : sameDepth){
                int myParentOrd = getGraphIndex(criterionName)[1];
                int otherParentOrd = getGraphIndex(node.getParent().toString())[1];

                if (otherParentOrd < myParentOrd){
                    inFront++;
                }
                else if (otherParentOrd == myParentOrd){
                    if (node.toString().compareTo(criterionName) < 0){
                        inFront++;
                    }
                }
            }
            index[1] = inFront;
        }

        return index;
    }

    @SuppressWarnings("unchecked")
    public void writeToFile (String fileName) {
        StringBuilder appleString = new StringBuilder();
        for (String name : apples)
            appleString.append(name).append(";");

        JSONObject appleNames = new JSONObject();
        appleNames.put("appleNames", appleString.toString());

        JSONArray nodeTree = new JSONArray();
        for (CriterionTreeNode criterion : keySet()){
            JSONObject jsonCriterion = new JSONObject();

            jsonCriterion.put("CriterionName", criterion.toString());
            jsonCriterion.put("ParentName",
                    (criterion.getParent() == null ? "" : criterion.getParent().toString())
            );

            nodeTree.add(jsonCriterion);
        }

        JSONArray arrays = new JSONArray();
        for (CriterionTreeNode criterion : keySet()){
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

    public static CriterionTreeMap readFromFile (String fileName){
        CriterionTreeMap newMap = new CriterionTreeMap();

        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(fileName)) {
            JSONArray list = (JSONArray) jsonParser.parse(reader);

            // CriterionNodes
            JSONArray nodes = (JSONArray) list.get(0);
            for (Object node : nodes){
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

                String str = (String) ((JSONObject) array).get("Array");
                String[][] arr = Arrays.stream(str.substring(2, str.length() - 2).split("\\],\\["))
                        .map(e -> Arrays.stream(e.split("\\s*,\\s*"))
                                .toArray(String[]::new)).toArray(String[][]::new);

                double[][] intArray = new double[arr.length][arr.length];
                for (int c = 0; c < arr.length; c++)
                    for (int r = 0; r < arr.length; r++)
                        intArray[c][r] = Double.parseDouble(arr[c][r]);

                newMap.put(node, intArray);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return newMap;
    }
}
