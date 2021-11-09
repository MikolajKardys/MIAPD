package arithmetics;

import Jama.*;


public class Solver {
    public static double [] solveForEVM(double [][] C_) {
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
