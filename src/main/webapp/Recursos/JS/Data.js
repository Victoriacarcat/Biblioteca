//data generada para simular datos 

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

// Datos del panel de administración (KPIs con variación, circulación
// semanal, categorías más pedidas, estado de la colección y alertas de mora).
// Reutilizan los números de "estadisticasBiblioteca" donde corresponde.
//
// TODO: cuando exista el backend, esto sale de consultas agregadas
// (COUNT/SUM sobre préstamos, devoluciones, reservas y sanciones por
// rango de fechas).
const kpisAdministrador = [
  { label: "Títulos en catálogo", valor: estadisticasBiblioteca.totalLibros, delta: "+18", positivo: true, porcentaje: 86, nota: "3 altas esta semana", escalable: false },
  { label: "Usuarios activos", valor: estadisticasBiblioteca.totalUsuarios, delta: "+6%", positivo: true, porcentaje: 64, nota: "92 con préstamo vigente", escalable: false },
  { label: "Préstamos activos", valor: estadisticasBiblioteca.prestamosActivos, delta: "+9", positivo: true, porcentaje: 58, nota: "Promedio de 11 días", escalable: true },
  { label: "Reservas pendientes", valor: estadisticasBiblioteca.reservasActivas, delta: "−3", positivo: false, porcentaje: 32, nota: "4 listas para retirar", escalable: true }
];

const circulacionSemanal = [
  { semana: "S1", prestamos: 46, devoluciones: 38 },
  { semana: "S2", prestamos: 58, devoluciones: 44 },
  { semana: "S3", prestamos: 41, devoluciones: 52 },
  { semana: "S4", prestamos: 70, devoluciones: 48 },
  { semana: "S5", prestamos: 62, devoluciones: 66 },
  { semana: "S6", prestamos: 84, devoluciones: 58 },
  { semana: "S7", prestamos: 76, devoluciones: 72 },
  { semana: "S8", prestamos: 94, devoluciones: 68 }
];

const categoriasMasPedidas = [
  { nombre: "Novela", prestamos: 312, porcentaje: 92 },
  { nombre: "Ciencia y Tecnología", prestamos: 204, porcentaje: 62 },
  { nombre: "Historia", prestamos: 166, porcentaje: 50 },
  { nombre: "Infantil y Juvenil", prestamos: 131, porcentaje: 39 },
  { nombre: "Poesía", prestamos: 74, porcentaje: 22 }
];

const estadoColeccion = {
  disponiblesPorcentaje: 62,
  enPrestamoPorcentaje: 24,
  reservadosPorcentaje: 9,
  enReparacionPorcentaje: 5
};

const indicadoresCirculacion = {
  duracionMediaDias: "11 días",
  devueltosATiempoPorcentaje: "94%",
  librosPorSocio: "3,2"
};

// Datos del panel del Bibliotecario: mismos componentes visuales que el
// panel de Administrador (KPIs, categorías), pero acotados a la operación
// diaria en vez de a la biblioteca completa.
const kpisBibliotecario = [
  { label: "Préstamos del día", valor: estadisticasBiblioteca.prestamosDelDia, delta: "+2", positivo: true, porcentaje: 60, nota: "Respecto de ayer" },
  { label: "Devoluciones pendientes", valor: estadisticasBiblioteca.devolucionesPendientes, delta: "−1", positivo: true, porcentaje: 35, nota: "Para hoy" },
  { label: "Reservas pendientes", valor: estadisticasBiblioteca.reservasPendientes, delta: "+1", positivo: false, porcentaje: 25, nota: "Listas para retirar" },
  { label: "Usuarios con mora", valor: estadisticasBiblioteca.usuariosConMora, delta: "+1", positivo: false, porcentaje: 45, nota: "Requieren contacto" }
];

// Métricas personales adicionales del panel del Lector. Hardcodeadas por
// ahora: la app todavía no tiene login real ni un historial de lectura
// propio para calcular "leídos este año" o una racha de días.
//
// TODO: cuando exista el backend, "leidosEsteAnio" y "rachaDias" van a
// salir de "historial" filtrado por el usuario autenticado, y
// "sancionesActivas" de "sanciones" filtrado igual (estado "Pendiente").
const misMetricasLector = {
  leidosEsteAnio: 14,
  metaAnual: 30,
  rachaDias: 12,
  sancionesActivas: 0
};

// Contexto general adicional para el panel del Lector (además de los
// números que ya vienen de "estadisticasBiblioteca"). Hardcodeado.
//
// TODO: cuando exista el backend, "categoriasDisponibles" sale de un
// COUNT DISTINCT sobre libros.categoria y "nuevosTitulosEsteMes" de altas
// de catálogo filtradas por fecha.
const contextoGeneralBiblioteca = {
  categoriasDisponibles: 8,
  nuevosTitulosEsteMes: 6
};