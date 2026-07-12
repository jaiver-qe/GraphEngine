package engine;

import java.util.*;

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


   /**
     * Verifica si el grafo es un árbol.
     * Se evalúa la versión no dirigida del grafo:
     * - Sin ciclos (no hay caminos cerrados)
     * - Sin aristas paralelas en la misma dirección
     * - Conexo en sentido no dirigido
     * - Con exactamente n-1 aristas únicas
     *
     * @return true si el grafo es un árbol, false si no
     */
    public boolean isTree() {
        if (vertices.isEmpty()) {
            return false;
        }

        // Un árbol debe tener exactamente n-1 aristas únicas
        if (hasParallelEdgesOrSelfLoops()) {
            return false;
        }

        // Construye un mapa de vecinos para la versión no dirigida del grafo
        Map<Integer, Set<Integer>> neighbors = buildUndirectedNeighbors();
        Set<Integer> visited = new HashSet<>();
        int startVertex = vertices.iterator().next();

        // Verifica si hay ciclos en la versión no dirigida del grafo
        if (hasCycleUndirected(startVertex, -1, visited, neighbors)) {
            return false;
        }

        // Verifica si todos los vértices fueron visitados (conectividad), asegurando que no haya componentes desconectadas.
        if (visited.size() != vertices.size()) {
            return false;
        }

        // Verifica que el número de aristas únicas sea exactamente n-1
        int uniqueEdges = countUniqueUndirectedEdges(neighbors);
        return uniqueEdges == vertices.size() - 1;
    }

    // Verifica si el grafo tiene aristas paralelas o ciclos
    private boolean hasParallelEdgesOrSelfLoops() {
        Map<String, Set<String>> edgeDirections = new HashMap<>();

        // Recorre cada vértice y sus aristas para detectar ciclos y aristas paralelas
        for (Map.Entry<Integer, List<GraphEdge>> entry : adjacencyList.entrySet()) {
            int source = entry.getKey();
            // Verifica si hay un ciclo (arista que apunta a sí misma).
            for (GraphEdge e : entry.getValue()) {
                int destination = e.destination;
                if (source == destination) {
                    return true;
                }

                // Crea una llave única para cada par de vértices, independientemente del orden
                String key = source < destination ? source + "-" + destination : destination + "-" + source;
                String direction = source + "->" + destination;

                // Si ya existe una arista en la misma dirección entre estos dos vértices, es un ciclo o arista paralela
                Set<String> directions = edgeDirections.computeIfAbsent(key, k -> new HashSet<>());
                if (directions.contains(direction)) {
                    return true;
                }
                // Agrega la dirección de la arista al conjunto para este par de vértices
                directions.add(direction);
            }
        }

        return false;
    }

    // Construye un mapa de vecinos para la versión no dirigida del grafo
    private Map<Integer, Set<Integer>> buildUndirectedNeighbors() {
        Map<Integer, Set<Integer>> neighbors = new HashMap<>(); // Mapa que almacena cada vértice y su conjunto de vecinos
        for (int vertex : vertices) {
            neighbors.put(vertex, new HashSet<>()); //Inicializa un conjunto vacío para cada vértice
        }

        // Recorre cada vértice y sus aristas para llenar el mapa de vecinos
        for (Map.Entry<Integer, List<GraphEdge>> entry : adjacencyList.entrySet()) {
            int source = entry.getKey();
            for (GraphEdge e : entry.getValue()) {
                neighbors.putIfAbsent(source, new HashSet<>());
                neighbors.putIfAbsent(e.destination, new HashSet<>());
                neighbors.get(source).add(e.destination);
                neighbors.get(e.destination).add(source);
            }
        }

        // Devuelve el mapa de vecinos construido, representando la versión no dirigida del grafo
        return neighbors;
    }

    // Cuenta el número de aristas únicas en la versión no dirigida del grafo
    private int countUniqueUndirectedEdges(Map<Integer, Set<Integer>> neighbors) {
        int total = 0;
        for (Set<Integer> adj : neighbors.values()) {
            total += adj.size();
        }
        return total / 2;
    }

    // Verifica si hay ciclos en la versión no dirigida del grafo usando DFS (búsqueda en profundidad)
    private boolean hasCycleUndirected(int current, int parent, Set<Integer> visited, Map<Integer, Set<Integer>> neighbors) {
        visited.add(current);

        // Recorre todos los vecinos del vértice actual
        for (int neighbor : neighbors.getOrDefault(current, Collections.emptySet())) {
            if (!visited.contains(neighbor)) {
                if (hasCycleUndirected(neighbor, current, visited, neighbors)) { // Si se encuentra un ciclo en la recursión, retorna true
                    return true;
                }
            } else if (neighbor != parent) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica si el grafo es plano usando el grafo subyacente simple y el criterio de Kuratowski.
     * Se reducen los vértices de grado 0, 1 y 2 para detectar subdivisiones de K5 o K3,3.
     *
     * @return true si el grafo es plano, false en caso contrario
     */
    
    public boolean isPlanar() {
        if (vertices.isEmpty()) { // Un grafo vacío es plano
            return true;
        }

        // Construye un mapa de vecinos para la versión no dirigida del grafo
        Map<Integer, Set<Integer>> neighbors = buildUndirectedNeighbors();

        // Los bucles inmediatos hacen que el grafo no sea plano
        for (Map.Entry<Integer, Set<Integer>> entry : neighbors.entrySet()) {
            if (entry.getValue().contains(entry.getKey())) { // Si un vértice tiene un bucle, el grafo no es plano
                return false;
            }
        }

        // Reduce el grafo eliminando vértices de grado 0, 1 y 2
        Map<Integer, Set<Integer>> reduced = reduceGraphForPlanarity(neighbors);
        if (reduced.size() <= 4) {
            return true;
        }

        // Aplica el criterio de Kuratowski: si el grafo reducido tiene más de 3n-6 aristas, o contiene K5 o K3,3, no es plano
        int n = reduced.size();
        int m = countUniqueUndirectedEdges(reduced);
        if (m > 3 * n - 6) {
            return false;
        }

        // Verifica si el grafo reducido contiene K5 o K3,3
        if (containsK5(reduced)) {
            return false;
        }
        if (containsK33(reduced)) {
            return false;
        }

        return true;
    }

    // Reducción del grafo eliminando vértices de grado 0, 1 y 2 para facilitar la detección de subdivisiones de K5 o K3,3
    private Map<Integer, Set<Integer>> reduceGraphForPlanarity(Map<Integer, Set<Integer>> neighbors) {
        Map<Integer, Set<Integer>> reduced = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> entry : neighbors.entrySet()) { // Copia el mapa de vecinos original al mapa reducido
            reduced.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }

        // Bucle que continúa reduciendo el grafo mientras se eliminen vértices de grado 0, 1 o 2
        boolean changed = true;
        while (changed) {
            changed = false;
            Iterator<Map.Entry<Integer, Set<Integer>>> iterator = reduced.entrySet().iterator(); // Iterador para recorrer el mapa reducido de vecinos
            while (iterator.hasNext()) {
                Map.Entry<Integer, Set<Integer>> entry = iterator.next(); // Obtiene la entrada actual del mapa reducido
                int vertex = entry.getKey();
                Set<Integer> adj = entry.getValue(); // Conjunto de vecinos del vértice actual
                int degree = adj.size(); // Calcula el grado del vértice actual (número de vecinos)

                // Si el grado es 0 o 1, elimina el vértice y actualiza los vecinos
                if (degree <= 1) {
                    iterator.remove();
                    for (int neighbor : adj) {
                        Set<Integer> neighborAdj = reduced.get(neighbor);
                        if (neighborAdj != null) {
                            neighborAdj.remove(vertex); // Elimina el vértice actual de la lista de vecinos del vecino
                        }
                    }
                    changed = true; // Indica que se realizó un cambio y se debe volver a iterar para verificar si hay más vértices de grado 0 o 1
                    break;
                }

                // Si el grado es 2, elimina el vértice y conecta sus vecinos entre sí
                if (degree == 2) {
                    Iterator<Integer> neighborIterator = adj.iterator(); // Iterador para recorrer los vecinos del vértice actual
                    int firstNeighbor = neighborIterator.next();
                    int secondNeighbor = neighborIterator.next();

                    
                    iterator.remove(); // Elimina el vértice actual del mapa reducido
                    reduced.get(firstNeighbor).remove(vertex);
                    reduced.get(secondNeighbor).remove(vertex);

                    if (firstNeighbor != secondNeighbor) {
                        reduced.get(firstNeighbor).add(secondNeighbor);
                        reduced.get(secondNeighbor).add(firstNeighbor);
                    }

                    changed = true;
                    break;
                }
            }
        }

        return reduced; // Devuelve el mapa reducido de vecinos, que representa el grafo simplificado para la verificación de planitud
    }

    // Verifica si el grafo contiene una subdivisión de K5 (grafo completo de 5 vértices)
    private boolean containsK5(Map<Integer, Set<Integer>> neighbors) {
        List<Integer> vertexList = new ArrayList<>(neighbors.keySet()); // Convierte el conjunto de vértices en una lista para poder indexarlos
        int n = vertexList.size();
        for (int i = 0; i < n - 4; i++) { // Recorre todos los subconjuntos de 5 vértices posibles en el grafo reducido
            for (int j = i + 1; j < n - 3; j++) {
                for (int k = j + 1; k < n - 2; k++) {
                    for (int l = k + 1; l < n - 1; l++) {
                        for (int m = l + 1; m < n; m++) {
                            int a = vertexList.get(i); // Obtiene el primer vértice del subconjunto de 5 vértices
                            int b = vertexList.get(j);
                            int c = vertexList.get(k);
                            int d = vertexList.get(l);
                            int e = vertexList.get(m);
                            if (isCompleteSubgraph(neighbors, a, b, c, d, e)) { // Verifica si los 5 vértices forman un subgrafo completo (K5)
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    // Verifica si un conjunto de 5 vértices forma un subgrafo completo (K5)
    private boolean isCompleteSubgraph(Map<Integer, Set<Integer>> neighbors, int a, int b, int c, int d, int e) {
        int[] verticesArray = {a, b, c, d, e};// Crea un arreglo con los 5 vértices para facilitar la verificación de conexiones entre ellos
        for (int i = 0; i < verticesArray.length; i++) { // Recorre cada vértice del subconjunto de 5 vértices
            for (int j = i + 1; j < verticesArray.length; j++) {
                if (!neighbors.get(verticesArray[i]).contains(verticesArray[j])) { // Verifica si hay una conexión entre los vértices i y j; si no hay conexión, no es un subgrafo completo
                    return false;
                }
            }
        }
        return true;
    }

    // Verifica si el grafo contiene una subdivisión de K3,3 (grafo bipartito completo de 3 vértices en cada partición)
    private boolean containsK33(Map<Integer, Set<Integer>> neighbors) {
        List<Integer> vertexList = new ArrayList<>(neighbors.keySet()); // Convierte el conjunto de vértices en una lista para poder indexarlos
        int n = vertexList.size();
        for (int a = 0; a < n - 5; a++) { // Recorre todos los subconjuntos de 6 vértices posibles en el grafo reducido
            for (int b = a + 1; b < n - 4; b++) {
                for (int c = b + 1; c < n - 3; c++) {
                    for (int d = c + 1; d < n - 2; d++) {
                        for (int e = d + 1; e < n - 1; e++) {
                            for (int f = e + 1; f < n; f++) {
                                int v1 = vertexList.get(a);// Obtiene el primer vértice del subconjunto de 6 vértices
                                int v2 = vertexList.get(b);
                                int v3 = vertexList.get(c);
                                int v4 = vertexList.get(d);
                                int v5 = vertexList.get(e);
                                int v6 = vertexList.get(f);
                                if (isCompleteBipartite(neighbors, v1, v2, v3, v4, v5, v6)) {// Verifica si los 6 vértices forman un subgrafo bipartito completo (K3,3)
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    // Verifica si un conjunto de 6 vértices forma un subgrafo bipartito completo (K3,3)
    private boolean isCompleteBipartite(Map<Integer, Set<Integer>> neighbors,int a, int b, int c, int d, int e, int f) {
        int[] left = {a, b, c};// Crea un arreglo con los 3 vértices de la primera partición del subgrafo bipartito
        int[] right = {d, e, f};

        for (int i = 0; i < left.length; i++) { // Recorre cada vértice de la primera partición del subgrafo bipartito
            for (int j = i + 1; j < left.length; j++) {
                if (neighbors.get(left[i]).contains(left[j])) { // Verifica si hay una conexión entre los vértices i y j de la primera partición; si hay conexión, no es un subgrafo bipartito completo
                    return false;
                }
            }
        }

        // Recorre cada vértice de la segunda partición del subgrafo bipartito
        for (int i = 0; i < right.length; i++) {
            for (int j = i + 1; j < right.length; j++) {
                if (neighbors.get(right[i]).contains(right[j])) {
                    return false;
                }
            }
        }

        // Verifica que cada vértice de la primera partición esté conectado a todos los vértices de la segunda partición
        for (int leftVertex : left) {
            for (int rightVertex : right) {
                if (!neighbors.get(leftVertex).contains(rightVertex)) {
                    return false;
                }
            }
        }

        return true;
    }





/**
     * Calcula el número cromático del grafo usando su versión no dirigida.
     * Se busca el menor número de colores necesarios para colorear los vértices
     * de modo que no haya dos vértices adyacentes con el mismo color.
     *
     * @return Número cromático mínimo, o 0 si el grafo está vacío
     */
    public int getChromaticNumber() {
        if (vertices.isEmpty()) {
            return 0;
        }

        Map<Integer, Set<Integer>> neighbors = buildUndirectedNeighbors();
        return findChromaticNumber(neighbors);
    }

    private int findChromaticNumber(Map<Integer, Set<Integer>> neighbors) {
        List<Integer> order = new ArrayList<>(neighbors.keySet());
        order.sort((a, b) -> Integer.compare(neighbors.get(b).size(), neighbors.get(a).size()));

        int n = order.size();
        int[] colors = new int[n];

        for (int maxColors = 1; maxColors <= n; maxColors++) {
            Arrays.fill(colors, 0);
            if (colorGraph(order, neighbors, colors, 0, maxColors)) {
                return maxColors;
            }
        }

        return n;
    }

    private boolean colorGraph(List<Integer> order,
                               Map<Integer, Set<Integer>> neighbors,
                               int[] colors,
                               int index,
                               int maxColors) {
        if (index == order.size()) {
            return true;
        }

        int vertex = order.get(index);
        for (int color = 1; color <= maxColors; color++) {
            if (canAssignColor(vertex, color, order, neighbors, colors, index)) {
                colors[index] = color;
                if (colorGraph(order, neighbors, colors, index + 1, maxColors)) {
                    return true;
                }
                colors[index] = 0;
            }
        }
        return false;
    }

    private boolean canAssignColor(int vertex,
                                   int color,
                                   List<Integer> order,
                                   Map<Integer, Set<Integer>> neighbors,
                                   int[] colors,
                                   int index) {
        for (int i = 0; i < index; i++) {
            if (colors[i] == color && neighbors.get(vertex).contains(order.get(i))) {
                return false;
            }
        }
        return true;
    }


    

 /**
     * Verifica si el grafo dirigido tiene un camino de Euler.
     * Se usa el criterio para grafos dirigidos:
     * - Debe ser débilmente conexo en los vértices con aristas
     * - Todos los vértices deben tener |outDegree - inDegree| <= 1
     * - Puede haber como máximo un vértice con outDegree - inDegree = 1
     *   y uno con inDegree - outDegree = 1
     *
     * @return true si existe un camino de Euler, false en caso contrario
     */
    public boolean hasEulerianPath() {
        if (vertices.isEmpty() || countEdges() == 0) {
            return true;
        }

        Map<Integer, Integer> inDegree = new HashMap<>();
        Map<Integer, Integer> outDegree = new HashMap<>();

        for (int vertex : vertices) {
            inDegree.put(vertex, 0);
            outDegree.put(vertex, 0);
        }

        for (Map.Entry<Integer, List<GraphEdge>> entry : adjacencyList.entrySet()) {
            int source = entry.getKey();
            outDegree.put(source, outDegree.get(source) + entry.getValue().size());
            for (GraphEdge e : entry.getValue()) {
                inDegree.put(e.destination, inDegree.get(e.destination) + 1);
            }
        }

        int startCandidates = 0;
        int endCandidates = 0;
        for (int vertex : vertices) {
            int out = outDegree.get(vertex);
            int in = inDegree.get(vertex);
            int diff = out - in;

            if (Math.abs(diff) > 1) {
                return false;
            }

            if (diff == 1) {
                startCandidates++;
            } else if (diff == -1) {
                endCandidates++;
            }
        }

        if (!((startCandidates == 1 && endCandidates == 1) || (startCandidates == 0 && endCandidates == 0))) {
            return false;
        }

        return isWeaklyConnectedForEuler(inDegree, outDegree);
    }

    private boolean isWeaklyConnectedForEuler(Map<Integer, Integer> inDegree,
                                              Map<Integer, Integer> outDegree) {
        Map<Integer, Set<Integer>> neighbors = buildUndirectedNeighbors();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        int startVertex = -1;
        for (int vertex : vertices) {
            if (inDegree.get(vertex) + outDegree.get(vertex) > 0) {
                startVertex = vertex;
                break;
            }
        }

        if (startVertex == -1) {
            return true;
        }

        queue.add(startVertex);
        visited.add(startVertex);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int neighbor : neighbors.getOrDefault(current, Collections.emptySet())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        for (int vertex : vertices) {
            if (inDegree.get(vertex) + outDegree.get(vertex) > 0 && !visited.contains(vertex)) {
                return false;
            }
        }

        return true;
    }
}

