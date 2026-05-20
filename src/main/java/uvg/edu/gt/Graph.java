package uvg.edu.gt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Graph {

    public static final double INF = Double.MAX_VALUE / 2;

    private final List<String> vertices = new ArrayList<>();
    private double[][] adj;
    private int n = 0;

    private double[][] dist;
    private int[][] next;

    public Graph() {
        adj = new double[16][16];
        initAdj();
    }

    private void initAdj() {
        for (int i = 0; i < adj.length; i++)
            for (int j = 0; j < adj[i].length; j++)
                adj[i][j] = (i == j) ? 0 : INF;
    }

    public void addVertex(String v) {
        if (indexOf(v) >= 0) return;
        if (n == adj.length) grow();
        vertices.add(v);
        // row and column already INF from grow/init; set diagonal
        adj[n][n] = 0;
        n++;
        dist = null; // invalidate cache
    }

    public void addEdge(String u, String v, double w) {
        addVertex(u);
        addVertex(v);
        int ui = indexOf(u), vi = indexOf(v);
        adj[ui][vi] = w;
        dist = null;
    }

    public void removeEdge(String u, String v) {
        int ui = indexOf(u), vi = indexOf(v);
        if (ui < 0 || vi < 0) return;
        adj[ui][vi] = INF;
        dist = null;
    }

    public double[][] floyd() {
        dist = new double[n][n];
        next = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                dist[i][j] = adj[i][j];
                next[i][j] = (adj[i][j] < INF && i != j) ? j : -1;
            }

        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (dist[i][k] < INF && dist[k][j] < INF
                            && dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }

        // return a defensive copy
        double[][] copy = new double[n][n];
        for (int i = 0; i < n; i++)
            copy[i] = dist[i].clone();
        return copy;
    }

    public List<String> getPath(String u, String v) {
        if (dist == null) floyd();
        int ui = indexOf(u), vi = indexOf(v);
        if (ui < 0 || vi < 0 || next[ui][vi] == -1) return Collections.emptyList();

        List<String> path = new ArrayList<>();
        int cur = ui;
        path.add(vertices.get(cur));
        while (cur != vi) {
            cur = next[cur][vi];
            path.add(vertices.get(cur));
        }
        return path;
    }

    public String graphCenter() {
        if (dist == null) floyd();
        double minEcc = INF;
        int centerIdx = -1;

        for (int j = 0; j < n; j++) {
            double ecc = 0;
            for (int i = 0; i < n; i++)
                if (dist[i][j] > ecc) ecc = dist[i][j];
            if (ecc < minEcc) {
                minEcc = ecc;
                centerIdx = j;
            }
        }

        return (centerIdx < 0 || minEcc >= INF) ? "No hay centro definido" : vertices.get(centerIdx);
    }

    public void printMatrix() {
        int width = 14;
        System.out.printf("%-" + width + "s", "");
        for (int j = 0; j < n; j++)
            System.out.printf("%-" + width + "s", vertices.get(j));
        System.out.println();

        for (int i = 0; i < n; i++) {
            System.out.printf("%-" + width + "s", vertices.get(i));
            for (int j = 0; j < n; j++) {
                String cell = (adj[i][j] >= INF) ? "∞" : String.valueOf((int) adj[i][j]);
                System.out.printf("%-" + width + "s", cell);
            }
            System.out.println();
        }
    }

    public int indexOf(String v) {
        return vertices.indexOf(v);
    }

    public List<String> getVertices() {
        return Collections.unmodifiableList(vertices);
    }

    public int size() {
        return n;
    }

    private void grow() {
        int newCap = adj.length * 2;
        double[][] newAdj = new double[newCap][newCap];
        for (int i = 0; i < newCap; i++)
            for (int j = 0; j < newCap; j++)
                newAdj[i][j] = (i == j) ? 0 : INF;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                newAdj[i][j] = adj[i][j];
        adj = newAdj;
    }
}
