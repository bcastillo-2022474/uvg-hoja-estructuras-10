"""
HT10 – Floyd-Warshall con NetworkX (opcional)
CC2003 – Algoritmos y Estructura de Datos | UVG | Semestre I 2020

Replica las funcionalidades del programa Java usando el módulo NetworkX:
  - Cargar grafo desde guategrafo.txt
  - Floyd-Warshall (todos los caminos mínimos)
  - Centro del grafo (mínima excentricidad)
  - Menú interactivo
"""

import sys
import networkx as nx

INF = float("inf")
GRAPH_FILE = "src/main/resources/guategrafo.txt"


def load_graph(path: str) -> nx.DiGraph:
    G = nx.DiGraph()
    with open(path) as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            parts = line.split()
            if len(parts) < 3:
                continue
            u, v, w = parts[0], parts[1], float(parts[2])
            G.add_edge(u, v, weight=w)
    return G


def run_floyd(G: nx.DiGraph) -> dict:
    return dict(nx.floyd_warshall(G, weight="weight"))


def graph_center(G: nx.DiGraph, dist: dict) -> str:
    nodes = list(G.nodes)
    min_ecc = INF
    center = None
    for v in nodes:
        # eccentricity(v) = max distance FROM any node TO v
        ecc = max(dist[u].get(v, INF) for u in nodes)
        if ecc < min_ecc:
            min_ecc = ecc
            center = v
    return center if center else "No hay centro definido"


def get_path(G: nx.DiGraph, u: str, v: str) -> list[str]:
    try:
        return nx.shortest_path(G, u, v, weight="weight")
    except (nx.NetworkXNoPath, nx.NodeNotFound):
        return []


def print_matrix(G: nx.DiGraph, dist: dict):
    nodes = sorted(G.nodes)
    width = 14
    print(f"{'':>{width}}", end="")
    for v in nodes:
        print(f"{v:<{width}}", end="")
    print()
    for u in nodes:
        print(f"{u:<{width}}", end="")
        for v in nodes:
            d = dist[u].get(v, INF)
            cell = "∞" if d == INF else str(int(d))
            print(f"{cell:<{width}}", end="")
        print()


def menu(G: nx.DiGraph):
    dist = run_floyd(G)

    print("\n=== Centro de Respuesta COVID-19 – Sistema de Rutas (NetworkX) ===\n")
    print("Matriz de caminos mínimos (Floyd-Warshall):")
    print_matrix(G, dist)
    print(f"\nCentro del grafo: {graph_center(G, dist)}")

    while True:
        print("\n--- Menú ---")
        print("1. Consultar ruta más corta")
        print("2. Mostrar centro del grafo")
        print("3. Modificar grafo")
        print("4. Salir")
        opt = input("Opción: ").strip()

        if opt == "1":
            u = input("Ciudad origen: ").strip()
            v = input("Ciudad destino: ").strip()
            if u not in G or v not in G:
                print("Una o ambas ciudades no existen en el grafo.")
                continue
            d = dist[u].get(v, INF)
            if d == INF:
                print(f"No existe ruta de {u} a {v}.")
            else:
                path = get_path(G, u, v)
                print(f"Distancia: {int(d)} km")
                print("Ruta: " + " → ".join(path))

        elif opt == "2":
            print(f"Centro del grafo: {graph_center(G, dist)}")

        elif opt == "3":
            print("  a. Eliminar arco")
            print("  b. Agregar arco")
            sub = input("  Sub-opción: ").strip().lower()
            if sub == "a":
                u = input("Ciudad origen: ").strip()
                v = input("Ciudad destino: ").strip()
                if G.has_edge(u, v):
                    G.remove_edge(u, v)
                    print("Arco eliminado.")
                else:
                    print("El arco no existe.")
                dist = run_floyd(G)
                print(f"Nuevo centro del grafo: {graph_center(G, dist)}")
            elif sub == "b":
                u = input("Ciudad origen: ").strip()
                v = input("Ciudad destino: ").strip()
                try:
                    w = float(input("Distancia (km): ").strip())
                    G.add_edge(u, v, weight=w)
                    print("Arco agregado.")
                    dist = run_floyd(G)
                    print(f"Nuevo centro del grafo: {graph_center(G, dist)}")
                except ValueError:
                    print("Distancia inválida.")
            else:
                print("Sub-opción no reconocida.")

        elif opt == "4":
            print("Saliendo...")
            break
        else:
            print("Opción no válida.")


if __name__ == "__main__":
    path = sys.argv[1] if len(sys.argv) > 1 else GRAPH_FILE
    G = load_graph(path)
    menu(G)
