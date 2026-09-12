WiseBooks (fase frontend)

Proyecto desarrollado para la materia de Desarrollo Web Backend. Esta entrega corresponde
únicamente a la fase de frontend: interfaces en JSP con Bootstrap, con la lógica de
sesión/roles simulada en el navegador en localStorage.

#Requisitos para ejecutar el proyecto

- NetBeans 
- JDK 26 instalado como plataforma Java sin enbargo compila desde el JDK 17 o superior 
- Apache Tomcat 11 configurado como servidor en NetBeans
- Conexión a internet  para Bootstrap 5.3.3 y las fuentes de Google Fonts.


#Cómo correr el proyecto

1. Abrir el proyecto WiseBooks-1.0-SNAPSHOT en NetBeans.
2. Verificar que el servidor asignado al proyecto sea Apache Tomcat 11
3. Ejecutar con el botón Run Project.

#Cómo probar los distintos roles
No hay login real contra base de datos: el formulario de inicio de sesión Login.jsp
incluye un selector de rol que simula con qué tipo de usuario se entra al sistema.
Correo y contraseña no se validan por lo que cualquier valor no vacío es aceptado. Los roles disponibles son:

- Visitante — usuario sin sesión iniciada (estado por defecto).
- Lector
- Bibliotecario
- Administrador

Cada rol muestra un menú y unos botones distintos en toda la aplicación (por ejemplo, en el catálogo o en el detalle de un libro), controlado por el atributo data-rol en el HTML. Para probar un rol distinto, ir a Login.jsp, seleccionarlo en el desplegable, e iniciar sesión, para volver a Visitante, usar el botón Cerrar sesión del header.


ACLARACIÓN: La carpeta FrontEnd se dejó fuera de WEB-INF intencionalmente, para que los jsp
se puedan revisar visualmente en el navegador sin necesidad de servlets todavía (esos
llegarán junto con el backend).
