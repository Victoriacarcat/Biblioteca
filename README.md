WiseBooks

Sistema de biblioteca desarrollado para la materia de Desarrollo Web Backend.
Frontend en JSP + Bootstrap que consume una API JSON propia (/api/...) hecha con
Servlets, JPA (Hibernate) y MariaDB. El inventario se maneja por stock de cada
libro (copias totales y disponibles), no por ejemplares individuales.

#Requisitos

- NetBeans con Apache Tomcat 11 configurado como servidor.
- JDK 17 o superior.
- XAMPP con MariaDB encendido (puerto 3306, usuario root sin contraseña;
  se puede cambiar en src/main/resources/META-INF/persistence.xml).
- Conexión a internet para Bootstrap 5.3.3 y las fuentes de Google Fonts.

#Preparar la base de datos (una sola vez)

1. Crear la base y sus tablas con uno de estos dos caminos:
   - Ejecutar schema-generado.sql en phpMyAdmin, o
   - Crear la base vacía "wisebooks" (utf8mb4) y ejecutar util/PruebaTablas
     desde NetBeans (clic derecho > Run File): crea o actualiza las tablas
     a partir de las entidades.
2. Crear el primer administrador: abrir util/CrearAdministrador, cambiar los
   datos (sobre todo la contraseña) y ejecutarlo con Run File.

Después, ese administrador crea desde la pantalla Usuarios las demás cuentas
(lectores, bibliotecarios u otros administradores). CrearAdministrador solo
vuelve a hacer falta si ningún administrador puede entrar. No se insertan
usuarios a mano con SQL porque la contraseña se guarda cifrada con BCrypt.

#Cómo correr el proyecto

1. Abrir el proyecto en NetBeans (aparece como WiseBooks).
2. Verificar que el servidor asignado sea Apache Tomcat 11.
3. Ejecutar con Run Project. La aplicación queda en /WiseBooks.

#Roles

- Visitante: consulta el catálogo y el detalle de los libros.
- Lector: se registra desde "Crear cuenta". Reserva, renueva sus préstamos y
  consulta sus préstamos, historial, reservas, sanciones y favoritos.
- Bibliotecario: lo crea el administrador. Aprueba y entrega reservas,
  registra préstamos, devoluciones, pérdidas y sanciones, y gestiona las
  copias del inventario.
- Administrador: todo lo del bibliotecario, más crear, editar y desactivar
  libros, anular préstamos y sanciones, crear cuentas de cualquier rol y
  cambiar el rol (lector/bibliotecario) y el estado de los usuarios. El rol y
  el estado de otro administrador no se cambian desde la aplicación.

Los permisos los aplica el backend en cada petición. El atributo data-rol del
HTML solo decide qué botones y menús se ven.

#Estructura

- src/main/java/.../controller: servlets de la API (/api/...).
- src/main/java/.../service: reglas de negocio, permisos y transacciones.
- src/main/java/.../dao: consultas JPA.
- src/main/java/.../entity y dto: tablas y datos que viajan en JSON.
- src/main/webapp/FrontEnd: páginas JSP.
- src/main/webapp/Recursos/JS: un archivo por página, más los compartidos
  (Api.js, Auth.js, Utilidades.js, Header.js, CeldasTabla.js, BuscadorLectores.js).
