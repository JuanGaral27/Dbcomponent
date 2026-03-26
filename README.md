# DbComponent 

## Características

Arquitectura Genérica: Utiliza tipos genéricos <T extends IAdapter> para permitir diferentes implementaciones de conexión.

Carga de Consultas Dinámica: Soporta la lectura de archivos .properties y .json de forma nativa (sin dependencias externas pesadas).

Gestión de Transacciones: Incluye una clase Transaction que implementa AutoCloseable, permitiendo el uso de bloques try-with-resources.

Seguridad: Implementa PreparedStatement para prevenir ataques de Inyección SQL.

Minimalista: Diseñado para ser integrado rápidamente en proyectos de escritorio o backend.

## 🛠️ Estructura del Proyecto
El componente se divide en tres pilares fundamentales:

IAdapter: Interfaz que define el contrato de conexión.

DbComponent: El núcleo que orquesta la carga de archivos y la ejecución de queries.

Transaction: Manejador de operaciones atómicas (commit/rollback).

### Compilar

javac -d bin -cp "lib/postgresql.jar" src/Main.java src/db/*.java

### Ejecutar

java -cp "bin;lib/postgresql.jar" Main
