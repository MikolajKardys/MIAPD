package arithmetics;

import management.CriterionTreeMap;
import Jama.*;
import management.CriterionTreeNode;

import java.util.*;


public class Solver {
    public static double [] solveMultipleCriterion(CriterionTreeMap treeMap, PrioritizationMethod method) throws IllegalArgumentException {
        CriterionTreeNode root = treeMap.getRoot();

        return calculateRankingVector(treeMap, root, method);
    }

    private static void adjustIncompleteMatrixForEVM(double [][] C) {
        int n = C.length;
        int rowEmpty;
        double threshold = 1e-16;

        for(int i=0; i<n; i++) {
            rowEmpty = 0;
            for(int j=0; j<n; j++) {
                if(i != j && Math.abs(C[i][j]) <= threshold)
                    rowEmpty++;
            }
            C[i][i] = 1.0 + rowEmpty;
        }
    }

    private static double [] calculateRankingVector(CriterionTreeMap map, CriterionTreeNode node, PrioritizationMethod method) throws IllegalArgumentException{
        List<double[]> rankingVectors = new ArrayList<>();

        for(int i=0; i<node.getChildCount(); i++)
            rankingVectors.add(calculateRankingVector(map, node.getChildAt(i), method));

        double [] currentNodeRanking;
        double [][] C = map.get(node);
        if(method == PrioritizationMethod.EVM) {
            if(graphCoherence.isConnected(C))
                currentNodeRanking = solveForIncompleteEVM(C);
            else
                throw new IllegalArgumentException("PC table for " + node + " isn't connected");
        } else if(method == PrioritizationMethod.GMM)
            if(graphCoherence.isConnected(C))
                currentNodeRanking = solveIncompleteForGMM(C);
            else
                throw new IllegalArgumentException("PC table for " + node + " isn't connected");
        else
            throw new IllegalArgumentException("There is no such method");

        if(node.getChildCount() == 0) {
            return currentNodeRanking;
        } else {
            double[] resultRanking = new double[map.applesNumber()];

            for (int i=0; i<map.applesNumber(); i++) {
                resultRanking[i] = 0;
                for (int j=0; j<rankingVectors.size(); j++)
                    resultRanking[i] += rankingVectors.get(j)[i]*currentNodeRanking[j];
            }
            return resultRanking;
        }
    }

    private static double [] solveIncompleteForGMM(double [][] C) {
        int n = C.length;
        Matrix G = new Matrix(n, n);
        Matrix r = new Matrix(n, 1);
        double threshold = 1e-16;
        int s_i;

        for(int i=0; i<n; i++) {
            s_i = 0;
            for(int j=0; j<n; j++) {
                if(i != j) {
                    if (Math.abs(C[i][j]) <= threshold) {
                        s_i++;
                        G.set(i, j, 1.0);
                    } else
                        r.set(i, 0, r.get(i, 0) + Math.log(C[i][j]));
                }
            }
            G.set(i, i, n - s_i);
        }
        Matrix w_ = G.solve(r);
        double [] w = new double[n];
        double wSum = 0;

        for(int i=0; i<n; i++) {
            w[i] = Math.exp(w_.get(i, 0));
            wSum += w[i];
        }

        for(int i=0; i<n; i++)
            w[i] /= wSum;

        return w;
    }

    private static double [] solveForGMM(double [][] C) {
        int n = C.length;
        double [] w = new double[n];
        double wSum = 0;

        for(int i=0; i<n; i++) {
            w[i] = 1;
            for(int k=0; k<n; k++)
                w[i] *= C[i][k];
            w[i] = Math.pow(w[i], 1.0/n);
            wSum += w[i];
        }

        for(int i=0; i<n; i++)
            w[i] /= wSum;

        return w;
    }

    public static double [] solveForIncompleteEVM(double [][] C_) {
        adjustIncompleteMatrixForEVM(C_);
        int n = C_.length;
        double [] w = new double[n];
        double [][] CBack = Arrays.stream(C_).map(double[]::clone).toArray(double[][]::new);

        Matrix C = new Matrix(CBack);

        EigenvalueDecomposition eigenValues = C.eig();
        double [] eigenValuesImg = eigenValues.getImagEigenvalues();
        double [] eigenValuesReal = eigenValues.getRealEigenvalues();

        double lambdaMax = -1;
        double threshold = 1e-16;
        for(int i=0; i<n; i++)
            if(Math.abs(eigenValuesImg[i]) <= threshold && eigenValuesReal[i] > lambdaMax)
                lambdaMax = eigenValuesReal[i];

        Matrix B = new Matrix(n, 1);
        for(int i=0; i<n; i++) {
            C.set(i, i, C.get(i, i) - lambdaMax);
            B.set(i,0, -C.get(i,n-1));
        }

        Matrix T = C.getMatrix(0, n-1, 0, n-2);

        Matrix w_ = T.solve(B);
        double wSum = 0;
        for(int i = 0; i<n-1; i++) {
            w[i] = w_.get(i, 0);
            wSum += w[i];
        }
        wSum += 1;
        w[n-1] = 1;

        for(int i = 0; i<n; i++)
            w[i] /= wSum;

        return w;
    }
}
