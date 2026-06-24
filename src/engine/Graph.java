package engine;

import java.util.*;
import java.util.LinkedList;
import java.util.Queue;

public class Graph {
    private Map<Integer, List<GraphEdge>> adjacencyList;
    private Set<Integer> vertices;

    /**
     * Clase interna que representa una arista desde un vértice hacia otro
     * Solo guarda destino y peso (el origen se sabe por el mapa que la contiene)
     */
    public static class GraphEdge {
        public int destination; // Vértice de destino
        public int weight; // Peso de la arista

        // Método constructor para inicializar los campos de la arista.
        public GraphEdge(int destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }

        @Override // Indica que este método sobrescribe el método toString de la clase Object.
        public String toString() {
            return " -> " + destination + " (peso:" + weight + ")";
        }
    }

    // Constructor: inicializa las estructuras vacías
    public Graph() {
        adjacencyList = new HashMap<>(); // Mapa vacío
        vertices = new HashSet<>(); // Conjunto vacío
    }

    /**
     * Agrega una arista al grafo
     * Permite múltiples aristas entre los mismos vértices (multigrafo)
     *
     * @param source      Vértice origen
     * @param destination Vértice destino
     * @param weight      Peso de la arista
     */
    public void addEdge(int source, int destination, int weight) {
        adjacencyList.putIfAbsent(source, new ArrayList<>()); // si el vértice no existe, crea una lista vacía
        adjacencyList.get(source).add(new GraphEdge(destination, weight)); // Agrega la nueva arista a la lista del vértice origen
        // Registra ambos vértices en el conjunto de vértices
        vertices.add(source);
        vertices.add(destination);
    }

    /**
     * Muestra el grafo en formato de lista de adyacencia
     * Imprime cada vértice seguido de sus aristas salientes
     */
    public void displayGraph() {
        System.out.println("=== REPRESENTACIÓN DEL GRAFO ===");
        System.out.println("Vértices: " + vertices);
        System.out.println("\nLista de adyacencia:");

        if (adjacencyList.isEmpty()) {
            System.out.println("  (grafo vacío)");
            return;
        }

        // Ordena los vértices numéricamente para una presentación ordenada
        List<Integer> sortedVertices = new ArrayList<>(vertices);
        Collections.sort(sortedVertices);

        // Recorre cada vértice y muestra sus aristas
        for (int vertex : sortedVertices) {
            System.out.print("Vértice " + vertex + ":");
            List<GraphEdge> edges = adjacencyList.getOrDefault(vertex, new ArrayList<>());
            if (edges.isEmpty()) {
                System.out.println(" (sin aristas salientes)");
            } else {
                for (GraphEdge e : edges) {
                    System.out.print(e);
                }
                System.out.println();
            }
        }

        System.out.println("\nTotal de aristas: " + countEdges());
    }

    /**
     * Cuenta el número total de aristas en el grafo
     * Suma el tamaño de todas las listas de adyacencia
     *
     * @return Número total de aristas
     */
    private int countEdges() {
        int total = 0;
        for (List<GraphEdge> edges : adjacencyList.values()) {
            total += edges.size(); // Suma el tamaño de cada lista
        }
        return total;
    }

    /**
     * Calcula el grado de un vértice
     * Grado = aristas salientes + aristas entrantes
     *
     * @param vertex Índice del vértice
     * @return Grado total del vértice
     * @throws IllegalArgumentException Si el vértice no existe
     */
    public int calculateDegree(int vertex) {
        if (!vertices.contains(vertex)) { // Verifica que el vértice exista
            throw new IllegalArgumentException("El vértice " + vertex + " no existe en el grafo");
        }

        int outDegree = adjacencyList.getOrDefault(vertex, new ArrayList<>()).size();

        int inDegree = 0;
        for (Map.Entry<Integer, List<GraphEdge>> entry : adjacencyList.entrySet()) {
            for (GraphEdge e : entry.getValue()) {
                if (e.destination == vertex) {
                    inDegree++;
                }
            }
        }

        return outDegree + inDegree;
    }

    /**
     * Determina si el grafo es simple o multigrafo
     * Grafo simple: no hay aristas paralelas (mismo origen y destino con mismo peso)
     *
     * @return String indicando "Grafo simple" o "Multigrafo"
     */
    public String getGraphType() {
        for (Map.Entry<Integer, List<GraphEdge>> entry : adjacencyList.entrySet()) {
            int source = entry.getKey();
            List<GraphEdge> edges = entry.getValue();

            Set<String> uniqueEdges = new HashSet<>();
            for (GraphEdge e : edges) {
                String key = source + "->" + e.destination + "(" + e.weight + ")";
                if (uniqueEdges.contains(key)) {
                    return "Multigrafo (se detectaron aristas paralelas o múltiples)";
                }
                uniqueEdges.add(key);
            }
        }

        return "Grafo simple (sin aristas paralelas)";
    }

    /**
     * Verifica si el grafo es completo
     * Grafo completo: todos los pares de vértices están conectados en ambos sentidos
     * Para n vértices debe tener n*(n-1)/2 aristas (en grafo no dirigido)
     *
     * @return true si es completo, false si no
     */
    public boolean isCompleteGraph() {
        int n = vertices.size();
        if (n <= 1) return false;

        int expectedEdges = n * (n - 1) / 2;
        int actualEdges = countEdges();

        if (actualEdges != expectedEdges) {
            return false;
        }

        // Verifica que CADA PAR de vértices esté conectado en ambos sentidos
        List<Integer> vertexList = new ArrayList<>(vertices);
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int u = vertexList.get(i);
                int v = vertexList.get(j);

                if (!hasEdge(u, v) || !hasEdge(v, u)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Verifica si existe una arista entre dos vértices
     *
     * @param source      Vértice origen
     * @param destination Vértice destino
     * @return true si existe al menos una arista de source a destination
     */
    private boolean hasEdge(int source, int destination) {
        List<GraphEdge> edges = adjacencyList.get(source);
        if (edges == null) return false;

        for (GraphEdge e : edges) {
            if (e.destination == destination) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si el grafo es conexo usando BFS (búsqueda en anchura)
     * Un grafo es conexo si desde cualquier vértice se pueden alcanzar todos los demás
     *
     * @return true si el grafo es conexo, false si no
     */
    public boolean isConnected() {
        // Un grafo vacío o con un solo vértice es conexo
        if (vertices.size() <= 1) {
            return true;
        }

        // Conjunto para guardar los vértices ya visitados
        Set<Integer> visited = new HashSet<>();

        // Cola para el BFS (búsqueda en anchura)
        Queue<Integer> queue = new LinkedList<>();

        // Empezamos desde el primer vértice
        int startVertex = vertices.iterator().next();
        queue.add(startVertex);
        visited.add(startVertex);

        // BFS: mientras haya vértices en la cola
        while (!queue.isEmpty()) {
            int current = queue.poll();

            // Recorremos todos los vecinos del vértice actual
            List<GraphEdge> edges = adjacencyList.getOrDefault(current, new ArrayList<>());
            for (GraphEdge edge : edges) {
                if (!visited.contains(edge.destination)) {
                    visited.add(edge.destination);
                    queue.add(edge.destination);
                }
            }
        }

        // Si visitamos todos los vértices, el grafo es conexo
        return visited.size() == vertices.size();
    }

    /**
     * Encuentra todos los componentes conexos del grafo
     *
     * @return Lista de conjuntos, donde cada conjunto es un componente conexo
     */
    public List<Set<Integer>> findConnectedComponents() {
        List<Set<Integer>> components = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        for (int vertex : vertices) {
            if (!visited.contains(vertex)) {
                // Nuevo componente encontrado
                Set<Integer> component = new HashSet<>();
                Queue<Integer> queue = new LinkedList<>();

                queue.add(vertex);
                component.add(vertex);

                while (!queue.isEmpty()) {
                    int current = queue.poll();
                    List<GraphEdge> edges = adjacencyList.getOrDefault(current, new ArrayList<>());
                    for (GraphEdge edge : edges) {
                        if (!component.contains(edge.destination)) {
                            component.add(edge.destination);
                            queue.add(edge.destination);
                        }
                    }
                }

                components.add(component);
                visited.addAll(component);
            }
        }

        return components;
    }

    /**
     * Metodo de fábrica: construye un grafo a partir de una lista de aristas
     *
     * @param edges Lista de aristas (del FileReaderGraph)
     * @return Nuevo grafo con todas las aristas agregadas
     */
    public static Graph fromEdgeList(List<FileReaderGraph.Edge> edges) {
        Graph graph = new Graph();
        for (FileReaderGraph.Edge e : edges) {
            graph.addEdge(e.source, e.target, e.weight);
        }
        return graph;
    }
}
