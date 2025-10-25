# SGVIC - Sistema de Gestión de Vencimientos Impositivos y Contables  
**Proyecto en Java – Seminario de Integración Profesional (AP3)**  
Autor: **Tomás Héctor Manfredi**

---

## 🧩 Descripción general
El sistema **SGVIC** permite gestionar **clientes, obligaciones impositivas, pagos y alertas de vencimientos**.  
Fue desarrollado en **Java**, aplicando los **principios de Programación Orientada a Objetos (POO)** y una arquitectura por capas (Entidades – DAO – Servicios – UI).

Este proyecto forma parte del **Trabajo Práctico 3 (AP3)** del Seminario, donde se incorporan los pilares fundamentales del paradigma POO y las bases del desarrollo de aplicaciones con persistencia y capa visual.

---

## 🧠 Características principales
- ✅ Aplicación de los **cuatro pilares POO**:
  - **Encapsulamiento**: uso de atributos privados y métodos públicos de acceso.
  - **Abstracción**: clase abstracta `Obligacion` y la interfaz genérica `Repositorio<T>`.
  - **Herencia**: clases `ObligacionMensual` y `ObligacionAnual` extienden de `Obligacion`.
  - **Polimorfismo**: implementación distinta de `calcularInteres()` en cada subclase.
- ✅ **Arquitectura en capas**:
  - **DAO**: acceso a datos mediante JDBC.
  - **Servicios**: reglas de negocio, validaciones y algoritmos.
  - **UI (Swing)**: interfaz gráfica con pestañas para Clientes, Obligaciones, Pagos y Alertas.
- ✅ **Persistencia de datos** en MySQL (mediante la clase `DB` y archivo `db.properties`).
- ✅ **Excepciones personalizadas**: `DomainException`, `DataAccessException`, `NotFoundException`.
- ✅ **Estructuras de datos y algoritmos**:
  - Uso de `List<>` y `Deque<>` (colas para alertas).
  - Algoritmos de **ordenación por fecha de vencimiento** y **búsqueda binaria por período**.
- ✅ **Manejo de errores y validaciones** con bloques `try-catch` y `JOptionPane`.
- ✅ **Interfaz gráfica Swing**:
  - `MainFrame` con pestañas (`JTabbedPane`) que agrupan cada módulo del sistema.
  - Tablas (`JTable`) para listar registros.
  - Formularios para registrar pagos y gestionar alertas.

---

## 🧱 Estructura del proyecto
src/
└── sgvic/
├── config/ # Conexión a la base de datos
├── dao/ # Clases DAO con operaciones JDBC
├── entidades/ # Clases del dominio (Cliente, Obligacion, Pago, etc.)
├── excepciones/ # Excepciones personalizadas
├── servicios/ # Lógica de negocio (validaciones, reglas, algoritmos)
└── ui/ # Interfaz Swing (ventanas y paneles)

---

## ⚙️ Requisitos
- **Java JDK 17** o superior.  
- **NetBeans IDE** (recomendado).  
- **MySQL Server** en ejecución con la base `sgvic` creada previamente.

---

## 🚀 Ejecución
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/thmanfredi/SGVIC.git

2. Crear un archivo db.properties en la raíz del proyecto (no incluido por seguridad):
url=jdbc:mysql://localhost:3306/sgvic?useSSL=false&serverTimezone=UTC
user=root
password=TU_PASSWORD
3. Abrir el proyecto en NetBeans o IntelliJ.
4. Ejecutar la clase principal:
sgvic.ui.MainFrame
5. La aplicación abrirá una ventana con pestañas para gestionar Clientes, Obligaciones, Pagos y Alertas.
💡 Ejemplos de funcionamiento

Clientes: listar los clientes cargados en la BD.

Obligaciones: listar, ordenar por vencimiento y búsqueda binaria por período.

Pagos: registrar un pago y marcar la obligación como pagada.

Alertas: generar alertas (según días de aviso), listar pendientes y marcar como leídas.

📚 Aspectos académicos

Este desarrollo aplica:

Programación orientada a objetos.

Estructuras de control (if, for, while).

Listas y colas (ArrayList, Deque).

Algoritmos de ordenación y búsqueda.

Manejo de excepciones.

Creación de objetos mediante constructores.

Interfaz gráfica con Swing.

Conexión y manipulación de datos con JDBC.

🧑‍💻 Autor

Tomás Héctor Manfredi
Estudiante de Ingeniería
Materia: Seminario de Integración Profesional – Trabajo Práctico 3 (AP3)
Año: 2025
🏁 Estado del proyecto

✅ Entrega AP3 completa y funcional
🕒 Próximo paso: mejora visual y ampliación de funciones (alta de obligaciones) para AP4.

