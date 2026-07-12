package engine;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.UIManager;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class InteractiveMenu
{
    public InteractiveMenu(){
    };// Metodo constructor. No se necesita inicializar nada en este caso.

    public static void  startMenu() {
        /*
         * Leemos la ruta del archivo que ingresa el usuario, por consola o con un selector de archivos
         * y luego mostramos un menú interactivo con opciones para analizar el grafo.
         * */
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== MOTOR DE PROCESAMIENTO DE GRAFOS ===");

        //Llamamos al método selectFileInterface para obtener la ruta del archivo que el usuario desea cargar.
        String filePath = selectFileInterface(scanner);

        try {
            //Leemos el archivo utilizando la clase FileReaderGraph y obtenemos una lista de aristas (edges)..
            List<FileReaderGraph.Edge> edges = FileReaderGraph.readGraphFromFile(filePath);
            System.out.println("\n✓ Archivo leído correctamente. Aristas encontradas: " + edges.size());

            Graph graph = Graph.fromEdgeList(edges); // Construye el grafo a partir de las aristas
            graph.displayGraph(); // Mostrar el grafo

            boolean running = true; // Variable para controlar el bucle del menú interactivo
            while (running) {
                // Muestra las opciones disponibles
                System.out.println("\n--- OPCIONES ---");
                System.out.println("1. Calcular grado de un vértice");
                System.out.println("2. Verificar tipo de grafo (simple/multigrafo)");
                System.out.println("3. Verificar si es grafo completo");
                System.out.println("4. Verificar si el grafo es conexo");
                System.out.println("5. Mostrar componentes conexos");
                System.out.println("6. Verificar si el grafo es un arbol");
                System.out.println("7. Verificar si el grafo es plano");
                System.out.println("8. Verficar el numero cromatico del grafo");
                System.out.println("9. Verificar si el grafo tiene un camino de euler");
                System.out.println("10. Volver a mostrar el grafo");
                System.out.println("11. Salir");
                System.out.print("Elige una opción: ");

                int option = scanner.nextInt(); // Lee la opción del usuario
                scanner.nextLine(); // Consume el salto de línea después de leer el número

                switch (option) {
                    case 1: // Calcular grado
                        System.out.print("Ingresa el índice del vértice: ");
                        int vertex = scanner.nextInt();
                        scanner.nextLine(); // Consume el salto de línea limpiamos el buffer del scanner
                        try {
                            int degree = graph.calculateDegree(vertex);
                            System.out.println("Grado del vértice " + vertex + ": " + degree);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;

                    case 2: // Verificar tipo de grafo
                        System.out.println("Resultado: " + graph.getGraphType());
                        break;

                    case 3: // Verificar si es completo
                        boolean complete = graph.isCompleteGraph();
                        System.out.println("¿El grafo es completo? " + (complete ? "SÍ" : "NO"));
                        break;

                    case 4: // Verificar si es conexo
                        boolean connected = graph.isConnected();
                        if (connected) {
                            System.out.println("El grafo es CONEXO - Se puede llegar de cualquier vertice a otro ");
                        } else {
                            System.out.println("El grafo no es CONEXO - Existen vertices o grupos de vertices completamente aislados entre sí");
                        }
                        break;

                    case 5: // Mostrar componentes conexos
                        List<Set<Integer>> components = graph.findConnectedComponents();
                        System.out.println("Componentes conexos encontrados: " + components.size());
                        int compNumber = 1;
                        for (Set<Integer> component : components) {
                            System.out.println(" Componente " + compNumber + ":" + component);
                            compNumber++;
                        }
                        break;


                    case 6: // Verificar si el grafo es un árbol
                        boolean tree = graph.isTree();
                        System.out.println("¿El grafo es un árbol? " + (tree ? "SÍ" : "NO"));
                        break;

                    case 7: // Verificar si el grafo es plano
                        boolean planar = graph.isPlanar();
                        System.out.println("¿El grafo es plano? " + (planar ? "SÍ" : "NO"));
                        break;

                    case 8: // Verificar el número cromático del grafo
                        int chromaticNumber = graph.getChromaticNumber();
                        System.out.println("Número cromático del grafo: " + chromaticNumber);
                        break;

                    case 9: // Verificar si el grafo tiene un camino de Euler
                        boolean eulerianPath = graph.hasEulerPath();
                        System.out.println("¿El grafo tiene un camino de Euler? " + (eulerianPath ? "SÍ" : "NO"));
                        break;

                    case 10: // Mostrar grafo nuevamente
                        graph.displayGraph();
                        break;

                    case 11: // Salir del programa
                        running = false;
                        System.out.println(">> ¡Hasta luego!");
                        break;

                    default:
                        System.out.println(">> Opción no válida");
                }
            }

        } catch (IOException e) { //Se ejecuta si el archivo no existe.
            System.err.println(">> Error al leer el archivo: " + e.getMessage());
        }

        scanner.close(); // Cierra el scanner para liberar recursos.
    }

    // Método para manejar la selección de archivo, permitiendo al usuario ingresar una ruta o usar un explorador de archivos.
    public static String selectFileInterface(Scanner scanner) {

        try {
            // Intenta usar el mismo estilo visual del sistema operativo para el explorador de archivos.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Exception ignored) {
        } // Si falla, se usará el estilo básico de Java.

        System.out.print("\n>> Ingresa la ruta del archivo o presiona Enter para abrir la ventana de selección. " +"\n>> Escribe 'q' si quieres salir del programa.\n>> ");
        String answerUser = scanner.nextLine();
        if(answerUser.equalsIgnoreCase("q")){
                System.out.println(">> ¡Hasta luego!");
                System.exit(0);
            }

        //String path = scanner.nextLine();

        // Bucle para manejar la selección del archivo, permitiendo reintentos si el usuario ingresa una ruta inválida o cancela la exploración.
        while (true) {

            

            // Este caso es por si el usuario presiono enter sin dar ninguna ruta.
            if (answerUser.isEmpty()) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter(">> Archivo de texto", "txt"));

                //El usario selecciona un archivo y se retorna su ruta.
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    return fileChooser.getSelectedFile().getAbsolutePath();

                }
                else {
                    // Si el usuario cancela la selección, se le indicará que intente de nuevo.
                    System.out.println("\n>> Has cancelado la seleccion.");
                
                }
            }

            // Este caso es por si el usuario escribió algo o canceló la seleccion y debe reintentar.
            else {
                
                java.io.File file = new java.io.File(answerUser);

                // Si el archivo existe y es un archivo (no un directorio), se retorna la ruta.
                if (file.exists() && file.isFile()) {
                    return answerUser;
                }

                System.out.println("\n>> Error: El archivo no existe o la ruta no es valida.");
            }

            //Si el la ejecucion llega hasta este punto es porque no se pudo leer un archivo de manera exitosa asi que le pedimos al usuario reintentar.
            System.out.print(">> Intenta de nuevo! \n");
            System.out.print("\n>> Ingresa la ruta del archivo o presiona Enter para abrir la ventana de selección. " +"\n>> Escribe 'q' si quieres salir del programa.\n>> ");
            

            answerUser = scanner.nextLine();
            if(answerUser.equalsIgnoreCase("q")){
                System.out.println(">> ¡Hasta luego!");
                System.exit(0);
            }
        }
    }
}
