package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReaderGraph {

    /**
     * Clase interna que representa una arista (edge) del grafo
     * Almacena origen, destino y peso de una conexión
     */
    public static class Edge {
        public int source; // Vértice de origen
        public int target; // Vértice de destino
        public int weight; // Peso de la arista

        public Edge(int source, int target, int weight) {
            this.source = source;
            this.target = target;
            this.weight = weight;
        }
    }

    /**
     * Lee un archivo de texto y convierte su contenido en una lista de aristas
     * Formato esperado por línea: origen,destino,peso
     * Ejemplo: 0,1,5
     *
     * @param filePath Ruta del archivo a leer
     * @return Lista de objetos Edge con todas las aristas del archivo
     * @throws IOException Si hay error al leer el archivo o formato incorrecto
     */
    public static List<Edge> readGraphFromFile(String filePath) throws IOException {
        List<Edge> edges = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Archivo no encontrado: " + filePath);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty()) continue;

                String[] parts = line.split(",");

                if (parts.length != 3) {
                    throw new IOException("Línea " + lineNumber + ": se esperaban 3 valores (origen,destino,peso)");
                }

                try {
                    int source = Integer.parseInt(parts[0].trim());
                    int target = Integer.parseInt(parts[1].trim());
                    int weight = Integer.parseInt(parts[2].trim());

                    edges.add(new Edge(source, target, weight));
                } catch (NumberFormatException e) {
                    throw new IOException("Línea " + lineNumber + ": valores no numéricos");
                }
            }
        }

        return edges;
    }
}
