# Motor de Procesamiento de Grafos

#### Descripción del Proyecto

Este proyecto es un motor de procesamiento de grafos desarrollado para la asignatura de **Matemáticas Discretas**. Permite leer grafos desde archivos de texto, representarlos como lista de adyacencia y realizar diversas operaciones para analizar sus propiedades.

### Importante: Ruta del archivo

El programa te pedirá que escribas la ruta del archivo manualmente o que selecciones el
archivo. Ejemplo:
**Opciones válidas:**
- `datos/grafo.txt` (si el archivo está en la carpeta `datos`)
- `C:/ruta/completa/grafo.txt` (ruta absoluta en Windows)
- `/home/usuario/grafo.txt` (ruta absoluta en Linux/Mac)
- `grafo.txt` (si el archivo está en la misma carpeta que el programa)

Si presiona la tecla "Enter" en su defecto presionando la tecla enter se abre el selector de archivos permitiendo al usuario navegar por el directorio como comunmente se hace en otras plicaciones para seleccionar el archivo deseado.

### Formato del Archivo de Entrada

El archivo debe ser un archivo de texto con extensión **.txt** y el siguiente formato:
```txt
origen,destino,peso
0,1,5
0,2,3
1,2,2
1,3,7
2,3,1
2,0,4
3,0,6
```

### Especificaciones:
- Separador: Coma (,)
- Formato: origen,destino,peso por línea
- Tipos: Todos los valores deben ser números enteros
- Comentarios: No se permiten comentarios en el archivo


#### Funcionalidades Implementadas

| # | Funcionalidad | Descripción |
|---|---------------|-------------|
| 1 | Lectura de archivos | Lee grafos desde archivos CSV con formato `origen,destino,peso` |
| 2 | Representación del grafo | Muestra el grafo como lista de adyacencia en consola |
| 3 | Grado de un vértice | Calcula el grado total (aristas entrantes + salientes) de un vértice |
| 4 | Tipo de grafo | Determina si es un grafo simple o un multigrafo |
| 5 | Grafo completo | Verifica si el grafo es completo (todos los pares de vértices conectados) |
| 6 | Grafo conexo | Verifica si el grafo es conexo usando BFS (búsqueda en anchura) |
| 7 | Componentes conexos | Identifica y muestra todos los componentes conexos del grafo |


#### Cómo compilar y ejecutar
```bash
### Compilar desde la raíz del proyecto
javac -d bin src/com/motorgrafos/*.java

### Ejecutar desde la raíz del proyecto
java -cp bin com.motorgrafos.Main

### Ejecutar en Windows (CMD) 
javac -d bin src\com\motorgrafos\*.java
java -cp bin com.motorgrafos.Main
