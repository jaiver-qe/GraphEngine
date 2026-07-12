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
        if (tieneAristaParalelaCiclo()) {
            return false;
        }

        // Construye un mapa de vecinos para la versión no dirigida del grafo
        Map<Integer, Set<Integer>> vecinos = vecinosNoDirigidos();
        Set<Integer> visitado = new HashSet<>();
        int verticeInicio = vertices.iterator().next();

        // Verifica si hay ciclos en la versión no dirigida del grafo
        if (tieneCiclo(verticeInicio, -1, visitado, vecinos)) {
            return false;
        }

        // Verifica si todos los vértices fueron visitados (conectividad), asegurando que no haya componentes desconectadas.
        if (visitado.size() != vertices.size()) {
            return false;
        }

        // Verifica que el número de aristas únicas sea exactamente n-1
        int unicaArista = contarAristasNoDirigidas(vecinos);
        return unicaArista == vertices.size() - 1;
    }

    // Verifica si el grafo tiene aristas paralelas o ciclos
    private boolean tieneAristaParalelaCiclo() {
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
    private Map<Integer, Set<Integer>> vecinosNoDirigidos() {
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
    private int contarAristasNoDirigidas(Map<Integer, Set<Integer>> neighbors) {
        int total = 0;
        for (Set<Integer> adj : neighbors.values()) {
            total += adj.size();
        }
        return total / 2;
    }

    // Verifica si hay ciclos en la versión no dirigida del grafo usando DFS (búsqueda en profundidad)
    private boolean tieneCiclo(int actual, int pariente, Set<Integer> visitado, Map<Integer, Set<Integer>> vecinos) {
        visitado.add(actual);

        // Recorre todos los vecinos del vértice actual
        for (int vecino : vecinos.getOrDefault(actual, Collections.emptySet())) {
            if (!visitado.contains(vecino)) {
                if (tieneCiclo(vecino, actual, visitado, vecinos)) { // Si se encuentra un ciclo en la recursión, retorna true
                    return true;
                }
            } else if (vecino != pariente) {
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
        Map<Integer, Set<Integer>> vecinos = vecinosNoDirigidos();

        // Los bucles inmediatos hacen que el grafo no sea plano
        for (Map.Entry<Integer, Set<Integer>> entry : vecinos.entrySet()) {
            if (entry.getValue().contains(entry.getKey())) { // Si un vértice tiene un bucle, el grafo no es plano
                return false;
            }
        }

        // Reduce el grafo eliminando vértices de grado 0, 1 y 2
        Map<Integer, Set<Integer>> reducido = gradoReducido(vecinos);
        if (reducido.size() <= 4) {
            return true;
        }

        // Aplica el criterio de Kuratowski: si el grafo reducido tiene más de 3n-6 aristas, o contiene K5 o K3,3, no es plano
        int n = reducido.size();
        int m = contarAristasNoDirigidas(reducido);
        if (m > 3 * n - 6) {
            return false;
        }

        // Verifica si el grafo reducido contiene K5 o K3,3
        if (containsK5(reducido)) {
            return false;
        }
        if (containsK33(reducido)) {
            return false;
        }

        return true;
    }

    // Reducción del grafo eliminando vértices de grado 0, 1 y 2 para facilitar la detección de subdivisiones de K5 o K3,3
    private Map<Integer, Set<Integer>> gradoReducido(Map<Integer, Set<Integer>> vecinos) {
        Map<Integer, Set<Integer>> reducido = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> entry : vecinos.entrySet()) { // Copia el mapa de vecinos original al mapa reducido
            reducido.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }

        // Bucle que continúa reduciendo el grafo mientras se eliminen vértices de grado 0, 1 o 2
        boolean changed = true;
        while (changed) {
            changed = false;
            Iterator<Map.Entry<Integer, Set<Integer>>> iterator = reducido.entrySet().iterator(); // Iterador para recorrer el mapa reducido de vecinos
            while (iterator.hasNext()) {
                Map.Entry<Integer, Set<Integer>> entry = iterator.next(); // Obtiene la entrada actual del mapa reducido
                int vertex = entry.getKey();
                Set<Integer> adj = entry.getValue(); // Conjunto de vecinos del vértice actual
                int degree = adj.size(); // Calcula el grado del vértice actual (número de vecinos)

                // Si el grado es 0 o 1, elimina el vértice y actualiza los vecinos
                if (degree <= 1) {
                    iterator.remove();
                    for (int neighbor : adj) {
                        Set<Integer> neighborAdj = reducido.get(neighbor);
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
                    reducido.get(firstNeighbor).remove(vertex);
                    reducido.get(secondNeighbor).remove(vertex);

                    if (firstNeighbor != secondNeighbor) {
                        reducido.get(firstNeighbor).add(secondNeighbor);
                        reducido.get(secondNeighbor).add(firstNeighbor);
                    }

                    changed = true;
                    break;
                }
            }
        }

        return reducido; // Devuelve el mapa reducido de vecinos, que representa el grafo simplificado para la verificación de planitud
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
        if (vertices.isEmpty()) { // Un grafo vacío no requiere colores
            return 0;
        }

        // Construye un mapa de vecinos para la versión no dirigida del grafo
        Map<Integer, Set<Integer>> vecinos = vecinosNoDirigidos();
        return encontrarNumeroCromatico(vecinos);
    }

    // Método auxiliar que encuentra el número cromático mínimo mediante backtracking (que es una técnica de búsqueda exhaustiva)
    private int encontrarNumeroCromatico(Map<Integer, Set<Integer>> vecinos) {
        List<Integer> orden = new ArrayList<>(vecinos.keySet()); // Crea una lista de vértices para determinar el orden de coloreado
        orden.sort((a, b) -> Integer.compare(vecinos.get(b).size(), vecinos.get(a).size())); // Ordena los vértices en orden descendente según su grado (número de vecinos) para optimizar el proceso de coloreado

        // Intenta colorear el grafo con un número creciente de colores, comenzando desde 1 hasta n (número de vértices)
        int cantidad = orden.size();
        int[] colores = new int[cantidad];

        // Intenta colorear el grafo con un número creciente de colores, comenzando desde 1 hasta n (número de vértices)
        for (int colorMaximo = 1; colorMaximo <= cantidad; colorMaximo++) {
            Arrays.fill(colores, 0); // Reinicia los colores asignados a 0 (sin color) antes de cada intento
            if (colorGrafo(orden, vecinos, colores, 0, colorMaximo)) { // Si se puede colorear con colorMaximo colores, retorna ese número como el número cromático mínimo
                return colorMaximo;
            }
        }

        return cantidad; // En el peor de los casos, cada vértice necesita un color diferente
    }

    // Método recursivo que intenta colorear el grafo usando backtracking
    private boolean colorGrafo(List<Integer> orden, Map<Integer, Set<Integer>> vecinos,int[] colores,int index,int coloresMaximos) {
        if (index == orden.size()) { // Si se han coloreado todos los vértices, retorna true indicando que se logró un coloreado válido
            return true;
        }

        // Obtiene el vértice actual a colorear según el orden determinado
        int vertex = orden.get(index);
        for (int color = 1; color <= coloresMaximos; color++) {
            if (puedoAsignarColor(vertex, color, orden, vecinos, colores, index)) { // Verifica si se puede asignar el color actual al vértice sin violar las restricciones de coloreado
                colores[index] = color;
                if (colorGrafo(orden, vecinos, colores, index + 1, coloresMaximos)) {
                    return true;
                }
                colores[index] = 0; // Si no se puede colorear el resto del grafo con este color, se deshace la asignación y se prueba con el siguiente color
            }
        }
        return false;
    }

    // Método auxiliar que verifica si se puede asignar un color a un vértice sin violar las restricciones de coloreado
    private boolean puedoAsignarColor(int vertex,int color,List<Integer> orden,Map<Integer, Set<Integer>> vecinos,int[] colores,int index) {
        for (int i = 0; i < index; i++) { // Recorre los vértices ya coloreados para verificar si alguno de ellos es vecino del vértice actual y tiene el mismo color
            if (colores[i] == color && vecinos.get(vertex).contains(orden.get(i))) {
                return false;
            }
        }
        // Si no se encontró ningún vecino con el mismo color, se puede asignar el color al vértice actual
        return true;
    }


    

 /**
     * Verifica si el grafo dirigido tiene un camino de Euler.
     * Se usa el criterio para grafos dirigidos:
     * - Debe ser débilmente conexo en los vértices con aristas
     * - Todos los vértices deben tener |gradoSalida - GradoEntrada| <= 1
     * - Puede haber como máximo un vértice con gradoSalida - GradoEntrada = 1
     *   y uno con GradoEntrada - gradoSalida = 1
     *
     * @return true si existe un camino de Euler, false en caso contrario
     */
    public boolean hasEulerPath() {
        if (vertices.isEmpty() || countEdges() == 0) { // Un grafo vacío o sin aristas tiene un camino de Euler trivial
            return true;
        }

        // Calcula los grados de entrada y salida para cada vértice
        Map<Integer, Integer> gradoEntrada = new HashMap<>();
        Map<Integer, Integer> gradoSalida = new HashMap<>();

        // Inicializa los grados de entrada y salida en 0 para todos los vértices
        for (int vertex : vertices) {
            gradoEntrada.put(vertex, 0);
            gradoSalida.put(vertex, 0);
        }

        // Recorre la lista de adyacencia para calcular los grados de entrada y salida
        for (Map.Entry<Integer, List<GraphEdge>> entrada : adjacencyList.entrySet()) {
            int origen = entrada.getKey();
            gradoSalida.put(origen, gradoSalida.get(origen) + entrada.getValue().size());
            for (GraphEdge e : entrada.getValue()) {
                gradoEntrada.put(e.destination, gradoEntrada.get(e.destination) + 1);
            }
        }

        // Verifica las condiciones para la existencia de un camino de Euler
        int candidatoInicial = 0;
        int candidatoFinal = 0;
        for (int vertex : vertices) {
            int salida = gradoSalida.get(vertex); // Grado de salida del vértice
            int entrada = gradoEntrada.get(vertex);
            int diferencia = salida - entrada;

            // Si la diferencia entre el grado de salida y el grado de entrada es mayor que 1, no puede haber un camino de Euler
            if (Math.abs(diferencia) > 1) {
                return false;
            }

            // Cuenta los candidatos para el inicio y fin del camino de Euler
            if (diferencia == 1) {
                candidatoInicial++;
            } else if (diferencia == -1) {
                candidatoFinal++;
            }
        }

        // Verifica que haya como máximo un candidato para el inicio y uno para el fin del camino de Euler
        if (!((candidatoInicial == 1 && candidatoFinal == 1) || (candidatoInicial == 0 && candidatoFinal == 0))) {
            return false;
        }

        // Verifica si el grafo es débilmente conexo considerando solo los vértices con aristas
        return grafoEsDebilmenteConexo(gradoEntrada, gradoSalida);
    }

    // Método auxiliar que verifica si el grafo es débilmente conexo considerando solo los vértices con aristas
    private boolean grafoEsDebilmenteConexo(Map<Integer, Integer> gradoEntrada,Map<Integer, Integer> gradoSalida) {
        Map<Integer, Set<Integer>> vecinos = vecinosNoDirigidos(); // Construye un mapa de vecinos para la versión no dirigida del grafo
        Set<Integer> visitado = new HashSet<>(); // Conjunto para almacenar los vértices visitados durante la búsqueda en anchura (BFS)
        Queue<Integer> cola = new LinkedList<>(); // Cola para la búsqueda en anchura (BFS)

        // Encuentra un vértice de inicio que tenga al menos una arista (grado de entrada o salida mayor que 0)
        int verticeInicio = -1;
        for (int vertex : vertices) {
            if (gradoEntrada.get(vertex) + gradoSalida.get(vertex) > 0) {
                verticeInicio = vertex; // Se encontró un vértice de inicio válido, se rompe el bucle
                break;
            }
        }

        // Si no se encontró ningún vértice con aristas, el grafo es débilmente conexo por definición
        if (verticeInicio == -1) {
            return true;
        }

        // Realiza una búsqueda en anchura (BFS) para recorrer todos los vértices alcanzables desde el vértice de inicio
        cola.add(verticeInicio);
        visitado.add(verticeInicio);

        // Mientras haya vértices en la cola, se procesan sus vecinos y se agregan a la cola si no han sido visitados
        while (!cola.isEmpty()) {
            int verticeActual = cola.poll();
            for (int vecino : vecinos.getOrDefault(verticeActual, Collections.emptySet())) {
                if (!visitado.contains(vecino)) { // Si el vecino no ha sido visitado, se marca como visitado y se agrega a la cola para su procesamiento
                    visitado.add(vecino);
                    cola.add(vecino);
                }
            }
        }

        // Verifica si todos los vértices con aristas han sido visitados
        for (int vertex : vertices) {
            if (gradoEntrada.get(vertex) + gradoSalida.get(vertex) > 0 && !visitado.contains(vertex)) { // Si hay un vértice con aristas que no fue visitado, el grafo no es débilmente conexo
                return false;
            }
        }

        return true;
    }
}

