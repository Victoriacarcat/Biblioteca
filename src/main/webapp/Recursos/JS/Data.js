// Data.js
// Datos de ejemplo (mock) para probar visualmente el catálogo de libros.
// Estos NO son datos reales ni definitivos: sirven solo para mostrar
// cómo se ubicarán las cards de libros mientras no hay backend.
//
// TODO: cuando exista el backend (Java/MySQL), estos datos se reemplazarán
// por una consulta real (por ejemplo, vía fetch a un servlet o JSP).
//
// Nota: el campo "fecha" representa el año de publicación del libro
// (corresponde al filtro "Año de publicación" del catálogo).

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
    portada: "https://via.placeholder.com/150x220?text=Portada"
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
    portada: "https://via.placeholder.com/150x220?text=Portada"
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
    portada: "https://via.placeholder.com/150x220?text=Portada"
  }
];