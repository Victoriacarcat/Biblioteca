const libros = [
  {
    id: 1,
    titulo: "Cien años de soledad",
    autor: "Gabriel García Márquez",
    categoria: "Novela",
    genero: "Realismo mágico",
    formato: "Físico",
    fecha: 1967,
    disponible: true,
    portada: contextPath + "/Recursos/Imagenes/portada-cienAñosdeSoledad.jpg"
  },
  {
    id: 2,
    titulo: "1984",
    autor: "George Orwell",
    categoria: "Novela",
    genero: "Distopía",
    formato: "Digital",
    fecha: 1949,
    disponible: false,
    portada: contextPath + "/Recursos/Imagenes/portada-1984.jpg"
  },
  {
    id: 3,
    titulo: "Breve historia del tiempo",
    autor: "Stephen Hawking",
    categoria: "Ciencia",
    genero: "Divulgación científica",
    formato: "Físico",
    fecha: 1988,
    disponible: true,
    portada: contextPath + "/Recursos/Imagenes/PORTADA-breveHistoriaDelTiempo.jpg"
  }
];

// Busca la portada de un libro por título. La usan las vistas que solo
// guardan el título como texto (prestamos/reservas/sanciones/historial
// no tienen libroId) y necesitan mostrar la miniatura junto al nombre.
// Si no encuentra el libro (no debería pasar con los datos de ejemplo,
// pero el arreglo "libros" podría cambiar) devuelve null.
function obtenerPortadaPorTitulo(tituloLibro) {
    const libro = libros.find((l) => l.titulo === tituloLibro);
    return libro ? libro.portada : null;
}

// Préstamos de ejemplo. Los usa MisPrestamos.js (vista Lector), Prestamos.js
// (vista Bibliotecario/Administrador) y Devoluciones.js.
//
// TODO: cuando exista el backend, vendrán de una consulta real filtrada
// por el usuario autenticado (para la vista Lector) o sin filtrar (staff).
const prestamos = [
  {
    id: 1,
    libro: "Cien años de soledad",
    usuario: "ana.gomez@correo.com",
    fechaPrestamo: "2026-08-20",
    fechaLimite: "2026-09-03",
    estado: "Activo"
  },
  {
    id: 2,
    libro: "1984",
    usuario: "carlos.ruiz@correo.com",
    fechaPrestamo: "2026-08-15",
    fechaLimite: "2026-08-29",
    estado: "Vencido"
  },
  {
    id: 3,
    libro: "Breve historia del tiempo",
    usuario: "ana.gomez@correo.com",
    fechaPrestamo: "2026-09-01",
    fechaLimite: "2026-09-15",
    estado: "Activo"
  }
];

// Reservas de ejemplo. Las usa Reservas.js (vista Lector y vista
// Bibliotecario/Administrador, en la misma página).
const reservas = [
  {
    id: 1,
    libro: "1984",
    usuario: "ana.gomez@correo.com",
    fecha: "2026-09-05",
    estado: "Pendiente"
  },
  {
    id: 2,
    libro: "Cien años de soledad",
    usuario: "carlos.ruiz@correo.com",
    fecha: "2026-09-08",
    estado: "Aprobada"
  },
  {
    id: 3,
    libro: "Breve historia del tiempo",
    usuario: "maria.diaz@correo.com",
    fecha: "2026-08-30",
    estado: "Vencida"
  }
];

// Sanciones/multas de ejemplo. Las usa Sanciones.js (vista Lector y vista
// Bibliotecario/Administrador, en la misma página).
const sanciones = [
  {
    id: 1,
    usuario: "carlos.ruiz@correo.com",
    motivo: "Devolución tardía",
    fecha: "2026-08-30",
    estado: "Pendiente",
    valor: 5000
  },
  {
    id: 2,
    usuario: "maria.diaz@correo.com",
    motivo: "Libro dañado",
    fecha: "2026-07-12",
    estado: "Pagada",
    valor: 15000
  }
];

// Historial de préstamos de ejemplo (principalmente para el Lector).
// Lo usa Historial.js.
const historial = [
  {
    id: 1,
    libro: "1984",
    fechaPrestamo: "2026-06-01",
    fechaDevolucion: "2026-06-14",
    estado: "Devuelto a tiempo"
  },
  {
    id: 2,
    libro: "Cien años de soledad",
    fechaPrestamo: "2026-05-10",
    fechaDevolucion: "2026-05-25",
    estado: "Devuelto con retraso"
  }
];

// Usuarios registrados de ejemplo. Los usa Usuarios.js (vista exclusiva
// de Administrador). El campo "correo" es la clave que los relaciona con
// "prestamos" y "sanciones" (mismo patrón que usan esos arreglos).
//
// TODO: cuando exista el backend, vendrán de una consulta real a la
// tabla de usuarios (con contraseña hasheada, etc.: nada de eso vive
// en el frontend).
const usuarios = [
  { id: 1, nombre: "Ana Gómez", correo: "ana.gomez@correo.com", rol: "lector", estado: "Activo", fechaRegistro: "2025-11-03" },
  { id: 2, nombre: "Carlos Ruiz", correo: "carlos.ruiz@correo.com", rol: "lector", estado: "Activo", fechaRegistro: "2025-12-18" },
  { id: 3, nombre: "María Díaz", correo: "maria.diaz@correo.com", rol: "lector", estado: "Suspendido", fechaRegistro: "2026-01-22" },
  { id: 4, nombre: "Julián Torres", correo: "julian.torres@correo.com", rol: "bibliotecario", estado: "Activo", fechaRegistro: "2025-06-10" },
  { id: 5, nombre: "Laura Fernández", correo: "laura.fernandez@correo.com", rol: "administrador", estado: "Activo", fechaRegistro: "2025-02-01" }
];

// Ejemplares (copias físicas/digitales) de cada libro. Los usa
// Inventario.js (vista Bibliotecario/Administrador). El campo "libroId"
// los relaciona con "libros".
//
// TODO: cuando exista el backend, "libros[].disponible" se calculará a
// partir de estos ejemplares en vez de ser un booleano fijo.
const ejemplares = [
  { id: 1, libroId: 1, codigo: "COL-001", estado: "Disponible" },
  { id: 2, libroId: 1, codigo: "COL-002", estado: "Prestado" },
  { id: 3, libroId: 2, codigo: "OR-001", estado: "Prestado" },
  { id: 4, libroId: 3, codigo: "HAW-001", estado: "Disponible" },
  { id: 5, libroId: 3, codigo: "HAW-002", estado: "Dañado" }
];

// Estadísticas generales de la biblioteca. Las usa Dashboard.js para
// pintar las métricas de las 4 vistas del panel (Visitante, Lector,
// Bibliotecario, Administrador).
//
// TODO: cuando exista el backend, estos números saldrán de consultas
// reales (COUNT sobre usuarios, préstamos, reservas, etc.).
const estadisticasBiblioteca = {
  totalLibros: libros.length,
  totalUsuarios: 128,
  prestamosActivos: 42,
  reservasActivas: 15,
  usuariosConMora: 6,
  prestamosDelDia: 9,
  devolucionesPendientes: 4,
  reservasPendientes: 3
};