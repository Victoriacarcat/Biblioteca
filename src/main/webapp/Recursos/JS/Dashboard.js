// Dashboard.js
// Pinta el panel principal según el rol actual. Cada vista del panel vive
// en su propia <section data-rol="..."> dentro del JSP y aquí solo se
// muestra la que corresponde al rol activo (mismo patrón que
// Header.js/Auth.js: visitante, lector, bibliotecario o administrador).
//
// Los números y listas salen de Data.js (mock): no hay backend todavía,
// así que las métricas no son reales.
//
// TODO: cuando exista el backend, estos datos vendrán de consultas reales
// (préstamos/reservas/sanciones del usuario autenticado, conteos globales
// para bibliotecario/administrador, etc.).

function mostrarSeccionDelRolActual() {
    const rolActual = obtenerRolActual();

    document.querySelectorAll("[data-rol]").forEach((elemento) => {
        const rolesPermitidos = elemento.getAttribute("data-rol").split(",");
        elemento.hidden = !rolesPermitidos.includes(rolActual);
    });
}

function pintarMetrica(idElemento, valor) {
    const elemento = document.getElementById(idElemento);
    if (elemento) {
        elemento.textContent = valor;
    }
}

// ---- Visitante: info general de la biblioteca ----
function pintarMetricasVisitante() {
    pintarMetrica("metrica-total-libros", estadisticasBiblioteca.totalLibros);
    pintarMetrica("metrica-total-usuarios", estadisticasBiblioteca.totalUsuarios);
}

// ---- Lector: mis préstamos/reservas + recomendados ----
function pintarMetricasLector() {
    pintarMetrica("metrica-mis-prestamos", prestamos.length);
    pintarMetrica("metrica-mis-reservas", reservas.length);
    pintarMetrica(
        "metrica-proximas-devoluciones",
        prestamos.filter((prestamo) => prestamo.estado === "Activo").length
    );

    // Tarjetas visuales con la portada (mismo componente que usan
    // Catalogo.js/Favoritos.js), no una lista de solo texto.
    const listaRecomendados = document.getElementById("lista-recomendados");
    if (listaRecomendados) {
        listaRecomendados.innerHTML = "";
        libros.forEach((libro) => {
            const columna = document.createElement("div");
            columna.className = "col-md-4 tarjeta-libro-contenedor";
            columna.innerHTML = `
                <div class="tarjeta-libro">
                    <img class="portada-libro" src="${libro.portada}" alt="Portada de ${libro.titulo}">
                    <h3 class="titulo-libro">${libro.titulo}</h3>
                    <p class="autor-libro">${libro.autor}</p>
                </div>
            `;
            listaRecomendados.appendChild(columna);
        });
    }
}

// ---- Bibliotecario: operación diaria ----
function pintarMetricasBibliotecario() {
    pintarMetrica("metrica-prestamos-dia", estadisticasBiblioteca.prestamosDelDia);
    pintarMetrica("metrica-devoluciones-pendientes", estadisticasBiblioteca.devolucionesPendientes);
    pintarMetrica("metrica-reservas-pendientes", estadisticasBiblioteca.reservasPendientes);
    pintarMetrica("metrica-usuarios-mora", estadisticasBiblioteca.usuariosConMora);
}

// ---- Administrador: visión global + gráficos ----
function pintarMetricasAdministrador() {
    pintarMetrica("metrica-total-libros-admin", estadisticasBiblioteca.totalLibros);
    pintarMetrica("metrica-total-usuarios-admin", estadisticasBiblioteca.totalUsuarios);
    pintarMetrica("metrica-prestamos-activos-admin", estadisticasBiblioteca.prestamosActivos);
    pintarMetrica("metrica-reservas-admin", estadisticasBiblioteca.reservasActivas);
}

function pintarGraficoAdministrador() {
    // "Gráfico" simple con barras de Bootstrap (.progress): un solo color
    // (el mismo azul de los botones primarios), el largo de la barra
    // representa la magnitud de cada métrica frente a un máximo de
    // referencia. No es una librería de gráficos: es solo para visualizar
    // el mockup mientras no hay backend.
    const metricas = [
        { id: "grafico-libros", valor: estadisticasBiblioteca.totalLibros, maximo: 50 },
        { id: "grafico-prestamos-activos", valor: estadisticasBiblioteca.prestamosActivos, maximo: 50 },
        { id: "grafico-reservas", valor: estadisticasBiblioteca.reservasActivas, maximo: 50 },
        { id: "grafico-usuarios-mora", valor: estadisticasBiblioteca.usuariosConMora, maximo: 50 }
    ];

    metricas.forEach((metrica) => {
        const barra = document.getElementById(metrica.id);
        if (!barra) {
            return;
        }
        const porcentaje = Math.min(100, Math.round((metrica.valor / metrica.maximo) * 100));
        barra.style.width = porcentaje + "%";
        barra.setAttribute("aria-valuenow", metrica.valor);
        barra.textContent = metrica.valor;
    });
}

document.addEventListener("DOMContentLoaded", () => {
    mostrarSeccionDelRolActual();
    pintarMetricasVisitante();
    pintarMetricasLector();
    pintarMetricasBibliotecario();
    pintarMetricasAdministrador();
    pintarGraficoAdministrador();
});
