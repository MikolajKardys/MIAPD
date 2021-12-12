package arithmetics;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import management.CriterionTreeMap;
import management.CriterionTreeNode;

import java.util.Arrays;

public class Inconsistency {
    //Incomplete PC matrixes
    public static double consistencyIndexSaatyHarker(CriterionTreeMap map, CriterionTreeNode node)
            throws IllegalArgumentException {
        double [][] C = map.get(node);
        if(graphCoherence.isConnected(C)) {
            int n = C.length;
            Matrix B = new Matrix(n, n);

            double threshold = 1e-16;
            int s_i;

            for (int i = 0; i < n; i++) {
                s_i = 0;
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        if (Math.abs(C[i][j]) <= threshold)
                            s_i++;
                        else
                            B.set(i, j, C[i][j]);
                    }
                }
                B.set(i, i, 1 + s_i);
            }

            EigenvalueDecomposition eigenValues = B.eig();
            double[] eigenValuesImg = eigenValues.getImagEigenvalues();
            double[] eigenValuesReal = eigenValues.getRealEigenvalues();

            double lambdaMax = -1;
            for (int i = 0; i < n; i++)
                if (Math.abs(eigenValuesImg[i]) <= threshold && eigenValuesReal[i] > lambdaMax)
                    lambdaMax = eigenValuesReal[i];

            return (lambdaMax - n) / (n - 1);
        } else
            throw new IllegalArgumentException("PC table for " + node + " isn't connected");
    }

    public static double consistencyIndexGoldenWang(CriterionTreeMap map, CriterionTreeNode node)
            throws IllegalArgumentException {
        double [][] COriginal = map.get(node);
        double [][] C = Arrays.stream(COriginal).map(double[]::clone).toArray(double[][]::new);

        if(graphCoherence.isConnected(C)) {
            int n = C.length;
            double [] wBackup = Solver.solveForIncompleteEVM(C);
            double [] w = Arrays.copyOf(wBackup, n);

            double threshold = 1e-16;
            double colSum, wSum, GW = 0.0;

            for(int j=0; j<n; j++) {
                colSum = 0.0;
                wSum = 0.0;
                for(int i=0; i<n; i++) {
                    if(Math.abs(C[i][j]) <= threshold)
                        w[i] = 0.0;
                    else
                        colSum += C[i][j];
                    wSum += w[i];
                }

                for(int i=0; i<n; i++) {
                    C[i][j] /= colSum;
                    w[i] /= wSum;
                    GW += Math.abs(C[i][j]-w[i]);
                }
                w = Arrays.copyOf(wBackup, n);
            }

            GW /= n;
            return GW;
        } else
            throw new IllegalArgumentException("PC table for " + node + " isn't connected");
    }

    /*
    //Complete PC matrixes
    //ISH - inconsistency index Salo Hamalainen
    public static double ambiguityIndex(double [][] C) {
        int n = C.length;
        Pair<Double, Double>[][] R = new Pair [n][n];

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
    */
}
