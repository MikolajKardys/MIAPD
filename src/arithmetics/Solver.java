package arithmetics;

import management.CriterionTreeMap;
import Jama.*;
import management.CriterionTreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Pair;

public class Solver {
    public static double [] solveMultipleCriterion(CriterionTreeMap treeMap) {
        CriterionTreeNode root = treeMap.getRoot();

        return calculateRankingVector(treeMap, root);
    }


    //ISH - inconsistency index Salo Hamalainen
    public static double ambiguityIndex(double [][] C) {
        int n = C.length;
        Pair<Double, Double> [][] R = new Pair [n][n];

        for(int i=0; i<n; i++)
            for(int j=0; j<n; j++)
                R[i][j] = new Pair<>(r_min(C, n, i, j), r_max(C, n, i, j));

        double ISH = 0;
        for(int i=0; i<n-1; i++)
            for(int j=i+1; j<n; j++)
                ISH += (R[i][j].getValue1() - R[i][j].getValue0())/((1+R[i][j].getValue1())*(1+R[i][j].getValue0()));

        ISH /= (n*(n-1)*0.5);

        return ISH;
    }

    private static double r_min(double [][] C, int n, int i, int j) {
        double r = C[i][0]*C[0][j];
        for(int k=1; k<n; k++)
            if(C[i][k]*C[k][j] < r)
                r = C[i][k]*C[k][j];

        return r;
    }

    private static double r_max(double [][] C, int n, int i, int j) {
        double r = C[i][0]*C[0][j];
        for(int k=1; k<n; k++)
            if(C[i][k]*C[k][j] > r)
                r = C[i][k]*C[k][j];

        return r;
    }


    //index of determinants is used as inconsistency index
    public static double indexOfDeterminants(double [][] C) {
        int n = C.length;
        double CIStar = 0.0;

        for(int i=0; i<n; i++)
            for(int j=i; j<n; j++)
                for(int k=j; k<n; k++)
                    CIStar += detT(C, i, j, k);

        CIStar /= (n*(n-1)*(n-2)/6.0);

        return CIStar;
    }

    private static double detT(double [][] C, int i, int j, int k) {
        return C[i][j]*C[k][j]/C[i][k] + C[i][k]/(C[i][j]*C[k][j]) - 2.0;
    }


    //0 <= K(C) <= 1
    //K - Koczkodaj inconsistency index
    //it can show which part of C matrix cause the biggest inconsistency
    public static double KoczkodajIndex(double [][] C) {
        int n = C.length;
        double K = 0.0, tmpK;

        for(int i=0; i<n; i++)
            for(int j=i+1; j<n; j++)
                for(int k=j+1; k<n; k++) {
                    tmpK = K(C, i, j, k);
                    if (K < tmpK)
                        K = tmpK;
                }

        return K;
    }

    private static double K(double [][] C, int i, int j, int k) {
        return Math.min(Math.abs(1-C[i][k]*C[k][j]/C[i][j]), Math.abs(1-C[i][j]/(C[i][k]*C[k][j])));
    }
    

    private static double [] calculateRankingVector(CriterionTreeMap map, CriterionTreeNode node) {
        List<double[]> rankingVectors = new ArrayList<>();

        for(int i=0; i<node.getChildCount(); i++)
            rankingVectors.add(calculateRankingVector(map, node.getChildAt(i)));

        double [] currentNodeRanking = solveForEVM(map.get(node));

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

    private static double [] solveForEVM(double [][] C_) {
        int numberOfAlternatives = C_.length;
        double [] w = new double[numberOfAlternatives];

        if(numberOfAlternatives == 1) {
            w[0] = 1;
            return w;
        }

        double [][] CBack = Arrays.stream(C_).map(double[]::clone).toArray(double[][]::new);

        Matrix C = new Matrix(CBack);

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
