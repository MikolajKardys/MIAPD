package arithmetics;

import management.CriterionTreeMap;
import Jama.*;
import management.CriterionTreeNode;

import java.util.ArrayList;
import java.util.List;


public class Solver {
    public static double [] solveMultipleCriterion(CriterionTreeMap treeMap) {
        CriterionTreeNode root = treeMap.getRoot();

        return calculateRankingVector(treeMap, root, getAlternativeNumber(treeMap, root));
    }

    private static int getAlternativeNumber(CriterionTreeMap map, CriterionTreeNode node) {
        if(node.getChildCount() > 0)
            return getAlternativeNumber(map, node.getChildAt(0));
        return map.get(node).length;
    }

    private static double [] calculateRankingVector(CriterionTreeMap map, CriterionTreeNode node, int alternativeNumber) {
        List<double[]> rankingVectors = new ArrayList<>();

        for(int i=0; i<node.getChildCount(); i++)
            rankingVectors.add(calculateRankingVector(map, node.getChildAt(i), alternativeNumber));

        double [] currentNodeRanking = solveForEVM(map.get(node));

        if(node.getChildCount() == 0) {
            return currentNodeRanking;
        } else {
            double[] resultRanking = new double[alternativeNumber];

            for (int i=0; i<alternativeNumber; i++) {
                resultRanking[i] = 0;
                for (int j=0; j<rankingVectors.size(); j++)
                    resultRanking[i] += rankingVectors.get(j)[i]*currentNodeRanking[j];
            }

            return resultRanking;
        }
    }

    private static double [] solveForEVM(double [][] C_) {
        int numberOfAlternatives = C_.length;
        double [] w = new double[numberOfAlternatives];

        if(numberOfAlternatives == 1) {
            w[0] = 1;
            return w;
        }

        Matrix C = new Matrix(C_);

        EigenvalueDecomposition eigenValues = C.eig();
        double [] eigenValuesImg = eigenValues.getImagEigenvalues();
        double [] eigenValuesReal = eigenValues.getRealEigenvalues();

        double lambdaMax = -1;
        double threshold = 1e-16;
        for(int i=0; i<numberOfAlternatives; i++)
            if(Math.abs(eigenValuesImg[i]) <= threshold && eigenValuesReal[i] > lambdaMax)
                lambdaMax = eigenValuesReal[i];

        Matrix B = new Matrix(numberOfAlternatives, 1);
        for(int i=0; i<numberOfAlternatives; i++) {
            C.set(i, i, C.get(i, i) - lambdaMax);
            B.set(i,0, -C.get(i,numberOfAlternatives-1));
        }

        Matrix T = C.getMatrix(0, numberOfAlternatives-1, 0, numberOfAlternatives-2);

        Matrix w_ = T.solve(B);
        double wSum = 0;
        for(int i = 0; i<numberOfAlternatives-1; i++) {
            w[i] = w_.get(i, 0);
            wSum += w[i];
        }
        wSum += 1;
        w[numberOfAlternatives-1] = 1;

        for(int i = 0; i<numberOfAlternatives; i++)
            w[i] /= wSum;

        return w;
    }
}
