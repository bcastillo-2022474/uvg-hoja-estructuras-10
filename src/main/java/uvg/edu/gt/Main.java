package uvg.edu.gt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Graph g = new Graph();
        loadFromFile(g, "guategrafo.txt");

        System.out.println("=== Centro de Respuesta COVID-19 – Sistema de Rutas ===\n");
        System.out.println("Matriz de adyacencia inicial:");
        g.printMatrix();

        runFloydAndShowCenter(g);
        menu(g);
    }

    private static void loadFromFile(Graph g, String resource) {
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(resource)) {
            if (is == null) {
                System.err.println("No se encontró el archivo: " + resource);
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\s+");
                if (parts.length < 3) continue;
                double km;
                try {
                    km = Double.parseDouble(parts[2]);
                } catch (NumberFormatException e) {
                    continue;
                }
                g.addEdge(parts[0], parts[1], km);
            }
        } catch (Exception e) {
            System.err.println("Error leyendo archivo: " + e.getMessage());
        }
    }

    private static void runFloydAndShowCenter(Graph g) {
        g.floyd();
        System.out.println("\nCentro del grafo: " + g.graphCenter());
    }

    private static void menu(Graph g) {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Menú ---");
            System.out.println("1. Consultar ruta más corta");
            System.out.println("2. Mostrar centro del grafo");
            System.out.println("3. Modificar grafo");
            System.out.println("4. Salir");
            System.out.print("Opción: ");

            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1" -> {
                    System.out.print("Ciudad origen: ");
                    String from = sc.nextLine().trim();
                    System.out.print("Ciudad destino: ");
                    String to = sc.nextLine().trim();

                    if (g.indexOf(from) < 0 || g.indexOf(to) < 0) {
                        System.out.println("Una o ambas ciudades no existen en el grafo.");
                        break;
                    }

                    double[][] dist = g.floyd();
                    int fi = g.indexOf(from), ti = g.indexOf(to);
                    double cost = dist[fi][ti];

                    if (cost >= Graph.INF) {
                        System.out.println("No existe ruta de " + from + " a " + to + ".");
                    } else {
                        List<String> path = g.getPath(from, to);
                        System.out.println("Distancia: " + (int) cost + " km");
                        System.out.println("Ruta: " + String.join(" → ", path));
                    }
                }
                case "2" -> System.out.println("Centro del grafo: " + g.graphCenter());
                case "3" -> {
                    System.out.println("  a. Eliminar arco");
                    System.out.println("  b. Agregar arco");
                    System.out.print("  Sub-opción: ");
                    String sub = sc.nextLine().trim().toLowerCase();
                    if (sub.equals("a")) {
                        System.out.print("Ciudad origen: ");
                        String u = sc.nextLine().trim();
                        System.out.print("Ciudad destino: ");
                        String v = sc.nextLine().trim();
                        g.removeEdge(u, v);
                        System.out.println("Arco eliminado (si existía).");
                        runFloydAndShowCenter(g);
                    } else if (sub.equals("b")) {
                        System.out.print("Ciudad origen: ");
                        String u = sc.nextLine().trim();
                        System.out.print("Ciudad destino: ");
                        String v = sc.nextLine().trim();
                        System.out.print("Distancia (km): ");
                        try {
                            double w = Double.parseDouble(sc.nextLine().trim());
                            g.addEdge(u, v, w);
                            System.out.println("Arco agregado.");
                            runFloydAndShowCenter(g);
                        } catch (NumberFormatException e) {
                            System.out.println("Distancia inválida.");
                        }
                    } else {
                        System.out.println("Sub-opción no reconocida.");
                    }
                }
                case "4" -> running = false;
                default -> System.out.println("Opción no válida.");
            }
        }
        System.out.println("Saliendo...");
    }
}
