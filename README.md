# SGVIC – Sistema de Gestión de Vencimientos Impositivos y Contables  
Proyecto Final – AP4 – Seminario de Informática  
**Autor:** Tomás Héctor Manfredi  

---

## 📌 Descripción general
SGVIC es una aplicación desarrollada en **Java 17**, con interfaz **Swing**, que permite gestionar:

- Clientes
- Obligaciones impositivas y contables
- Pagos asociados
- Alertas de vencimientos (próximos y vencidos)

La aplicación utiliza **MySQL** para persistir datos reales y sigue una **arquitectura en capas (DAO – Service – UI)**.

---

## 🏗 Tecnologías utilizadas
- Java 17  
- Swing (GUI)  
- JDBC (acceso a MySQL)  
- MySQL 8.x  
- NetBeans 17  
- Git + GitHub  
- Patrón: Arquitectura en Capas

---

## 📂 Estructura del proyecto

src/
└── sgvic/
├── config/ → Conexión a la BD (DB.java, db.properties)
├── dao/ → Acceso a datos (ClienteDAO, PagoDAO, ObligacionDAO…)
├── entidades/ → Clases del dominio (Cliente, Obligacion, Pago…)
├── excepciones/ → DomainException, DataAccessException…
├── servicios/ → Lógica de negocio (ClienteService, PagoService…)
└── ui/ → Interfaz Swing (MainFrame + paneles)

Esta separación permite un código mantenible y escalable.

---

## 🧮 Funcionalidades principales

### ✔ Gestión de Clientes
- Listar clientes desde MySQL  
- Alta de clientes con validaciones  
- Visualización ordenada en tabla  

### ✔ Gestión de Obligaciones
- Alta de obligaciones  
- Listado general  
- Ordenar por fecha de vencimiento  
- Búsqueda por período (búsqueda binaria)  

### ✔ Gestión de Pagos
- Registrar un pago real en la BD  
- Actualizar obligación a **PAGADA**  
- Mostrar pagos por obligación  

### ✔ Módulo de Alertas
- Mostrar **obligaciones vencidas**  
- Mostrar **próximas a vencer** usando arreglo `DIAS_AVISO`  
- Visualización en tabla  

---

## 🗄 Base de datos
Base utilizada: **sgvic**

Tablas:
- `cliente`
- `tipo_obligacion`
- `obligacion`
- `pago`
- `alerta`

Conexión por `db.properties`:


---

## ▶ Ejecución del sistema

### Desde NetBeans
1. Abrir el proyecto  
2. Configurar `db.properties`  
3. Ejecutar **MainFrame.java**

### Desde terminal (JAR)
```bash
cd dist
java -jar SGVIC_Java.jar

🔧 Mejoras implementadas en AP4

Persistencia completa (INSERT / UPDATE / SELECT)

Alta de obligaciones desde la UI

Registro de pagos y actualización de estado

Alertas reales según fechas

Columnas autoajustadas para mejor lectura

Manejo de excepciones mejorado

Corrección para ejecutar desde el JAR leyendo db.properties

👨‍💻 Autor

Tomás Héctor Manfredi
Proyecto Final – AP4 – Seminario de Informática
