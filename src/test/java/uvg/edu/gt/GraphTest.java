package uvg.edu.gt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    private Graph g;

    @BeforeEach
    void setUp() {
        g = new Graph();
    }

    @Test
    void testAddVertexNoDuplicate() {
        g.addVertex("A");
        g.addVertex("A");
        assertEquals(1, g.size());
        assertEquals(1, g.getVertices().size());
    }

    @Test
    void testAddEdgeCreatesVertices() {
        g.addEdge("X", "Y", 10);
        assertTrue(g.indexOf("X") >= 0);
        assertTrue(g.indexOf("Y") >= 0);
    }

    @Test
    void testAddEdgeDirectDistance() {
        g.addEdge("A", "B", 42);
        double[][] d = g.floyd();
        int ai = g.indexOf("A"), bi = g.indexOf("B");
        assertEquals(42, d[ai][bi], 0.001);
    }

    @Test
    void testAddEdgeOverwrite() {
        g.addEdge("A", "B", 10);
        g.addEdge("A", "B", 5);
        double[][] d = g.floyd();
        int ai = g.indexOf("A"), bi = g.indexOf("B");
        assertEquals(5, d[ai][bi], 0.001);
    }

    @Test
    void testRemoveEdge() {
        g.addEdge("A", "B", 20);
        g.removeEdge("A", "B");
        double[][] d = g.floyd();
        int ai = g.indexOf("A"), bi = g.indexOf("B");
        assertTrue(d[ai][bi] >= Graph.INF);
    }

    @Test
    void testRemoveEdgeNonExistent() {
        g.addVertex("A");
        g.addVertex("B");
        assertDoesNotThrow(() -> g.removeEdge("A", "B"));
    }

    @Test
    void testFloydDirectPath() {
        g.addEdge("A", "B", 3);
        g.addEdge("B", "C", 7);
        g.addEdge("A", "C", 15);
        double[][] d = g.floyd();
        int ai = g.indexOf("A"), bi = g.indexOf("B"), ci = g.indexOf("C");
        assertEquals(3, d[ai][bi], 0.001);
        assertEquals(7, d[bi][ci], 0.001);
        // Floyd finds A→C via B (3+7=10) which beats the direct arc of 15
        assertEquals(10, d[ai][ci], 0.001);
    }

    @Test
    void testFloydIndirectPath() {
        g.addEdge("A", "B", 10);
        g.addEdge("B", "C", 5);
        double[][] d = g.floyd();
        int ai = g.indexOf("A"), ci = g.indexOf("C");
        assertEquals(15, d[ai][ci], 0.001);
    }

    @Test
    void testFloydShortcutBeatsDirect() {
        g.addEdge("A", "B", 4);
        g.addEdge("B", "C", 4);
        g.addEdge("A", "C", 100);
        double[][] d = g.floyd();
        int ai = g.indexOf("A"), ci = g.indexOf("C");
        assertEquals(8, d[ai][ci], 0.001);
    }

    @Test
    void testFloydNoPath() {
        g.addEdge("A", "B", 5);
        // no edge B -> A
        double[][] d = g.floyd();
        int bi = g.indexOf("B"), ai = g.indexOf("A");
        assertTrue(d[bi][ai] >= Graph.INF);
    }

    @Test
    void testGraphCenter() {
        // Triangle: A->B=1, B->C=1, C->A=1 — all eccentricities equal; center is whichever has min
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);
        g.addEdge("C", "A", 1);
        String center = g.graphCenter();
        // All have the same eccentricity (max incoming path = 2 for each), so any is valid
        assertTrue(List.of("A", "B", "C").contains(center));
    }

    @Test
    void testGraphCenterKnown() {
        // Build graph where D is clearly the center (low eccentricity)
        // A->D=1, B->D=1, C->D=1, D->A=10, D->B=10, D->C=10
        // eccentricity(D) = max col D = max(dist[A][D], dist[B][D], dist[C][D]) = 1
        // eccentricity(A) = max col A = dist[D][A] = 10 (others go via D)
        g.addEdge("A", "D", 1);
        g.addEdge("B", "D", 1);
        g.addEdge("C", "D", 1);
        g.addEdge("D", "A", 10);
        g.addEdge("D", "B", 10);
        g.addEdge("D", "C", 10);
        assertEquals("D", g.graphCenter());
    }

    @Test
    void testGetPath() {
        g.addEdge("A", "B", 5);
        g.addEdge("B", "C", 5);
        g.floyd();
        List<String> path = g.getPath("A", "C");
        assertEquals(List.of("A", "B", "C"), path);
    }

    @Test
    void testGetPathDirect() {
        g.addEdge("A", "B", 5);
        g.floyd();
        List<String> path = g.getPath("A", "B");
        assertEquals(List.of("A", "B"), path);
    }

    @Test
    void testGetPathNoPath() {
        g.addEdge("A", "B", 5);
        g.floyd();
        List<String> path = g.getPath("B", "A");
        assertTrue(path.isEmpty());
    }
}
