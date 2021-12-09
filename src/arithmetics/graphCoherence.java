package arithmetics;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class graphCoherence {
    public static boolean isConnected(double [][] C) {
        int n = C.length;

        Stack<Integer> toVisit = new Stack<>();
        toVisit.push(0);
        Set<Integer> connected = new HashSet<>();
        connected.add(0);

        int currentNode;
        while(!toVisit.isEmpty()) {
            currentNode = toVisit.pop();
            for(int i=0; i<n; i++)
                if(currentNode != i && C[currentNode][i] > 0 && !connected.contains(i)) {
                    connected.add(i);
                    toVisit.push(i);
                }
        }
        return connected.size() == n;
    }
}
