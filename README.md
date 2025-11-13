📌 SGVIC – Sistema de Gestión de Vencimientos Impositivos y Contables

Entrega AP4 – Seminario de Informática
Autor: Tomás Héctor Manfredi

🧾 Descripción general del proyecto

El sistema SGVIC es una aplicación desarrollada en Java que permite gestionar vencimientos impositivos y contables de distintos clientes de un estudio.
Fue creado como proyecto integrador del Seminario, siguiendo una arquitectura en capas, con persistencia real en MySQL y una interfaz visual construida en Swing.

El objetivo principal es llevar un control organizado de:

Clientes

Obligaciones fiscales

Pagos asociados

Alertas de vencimientos (próximos y vencidos)

La aplicación permite administrar cada entidad de forma integrada, con datos almacenados en la base de datos y mostrados en la UI.

🏗️ Tecnologías utilizadas

Java 17

Swing (GUI)

JDBC para acceso a la base de datos

MySQL 8.x

NetBeans 17 como IDE

Patrón: Arquitectura en Capas (DAO – Service – UI)

Git + GitHub para control de versiones

📂 Estructura del proyecto
src/
 └── sgvic/
      ├── config/        → Conexión a la BD (DB.java, db.properties)
      ├── dao/           → Acceso a datos (ClienteDAO, ObligacionDAO, PagoDAO…)
      ├── entidades/     → Clases del dominio (Cliente, Obligacion, Pago…)
      ├── excepciones/   → DomainException, DataAccessException…
      ├── servicios/     → Lógica de negocio (ClienteService, PagoService…)
      └── ui/            → Interfaz visual (MainFrame + paneles)


Esta organización permite separar responsabilidades y mantener el código más limpio.

🧮 Principales funcionalidades
✔ Gestión de Clientes

Listar clientes desde MySQL

Alta de clientes con validaciones

Visualización en tabla con columnas autoajustadas

✔ Gestión de Obligaciones

Alta de nuevas obligaciones

Listado general

Ordenar por fecha de vencimiento

Búsqueda por período (búsqueda binaria)

Relaciones con Cliente y TipoObligacion

✔ Gestión de Pagos

Registrar un pago real en la BD

Actualizar estado de la obligación a PAGADA

Mostrar pagos por obligación

✔ Módulo de Alertas

Calcular obligaciones vencidas

Calcular próximas a vencer según DIAS_AVISO

Mostrar resultados en tabla

📘 Base de datos

Nombre de la base: sgvic

Tablas principales:

cliente

tipo_obligacion

obligacion

pago

alerta

La conexión se maneja desde DB.java y se configura vía db.properties.

Ejemplo:

url=jdbc:mysql://localhost:3306/sgvic?useSSL=false&serverTimezone=UTC
user=root
password=

▶️ Ejecución del sistema
Desde NetBeans:

Abrir el proyecto

Verificar archivo db.properties

Run → Run Project (MainFrame.java)

Desde terminal usando el JAR:
cd dist
java -jar SGVIC_Java.jar

🔧 Mejoras implementadas en AP4

Implementación completa de persistencia (INSERT, UPDATE, SELECT)

Alta de obligaciones desde UI

Registro de pagos y actualización de estado

Alertas reales basadas en fechas

Ajuste automático de columnas en tablas

Mejor manejo de excepciones a nivel de servicio

Corrección de conexión del JAR para lectura de db.properties

Limpieza del proyecto para subir a GitHub correctamente


🧑‍💻 Autor

Tomás Héctor Manfredi
Estudiante – Seminario de Programación
Proyecto Final AP4
