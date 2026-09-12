/** Dashboard.js
  panel principal según el rol actual. Cada vista del panel vive
 en su propia de los datos de roles con Data dentro del JSP y aquí solo se
 muestra la que corresponde al rol activo (mismo patrón que
 Header.js/Auth.js: visitante, lector, bibliotecario o administrador).

 Lector, Bibliotecario y Administrador comparten el mismo lenguaje visual
 ("panel-metricas": tarjetas KPI, gráfico semanal, categorías más pedidas,
 franja de estado y tarjeta de alertas). Los números y listas salen de
 Data.js (mock): no hay backend todavía, así que las métricas no son
 reales.

// Cuando exista el backend, estos datos vendrán de consultas reales
// (préstamos/reservas/sanciones del usuario autenticado, conteos y
agregados globales para bibliotecario/administrador, etc.).*/ 

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

function pintarFechaHoy(idElemento) {
    const elemento = document.getElementById(idElemento);
    if (!elemento) {
        return;
    }
    const texto = new Intl.DateTimeFormat("es-AR", {
        weekday: "long",
        day: "numeric",
        month: "long"
    }).format(new Date());
    elemento.textContent = texto.charAt(0).toUpperCase() + texto.slice(1);
}

//Visitante: info general de la biblioteca
function pintarMetricasVisitante() {
    pintarMetrica("metrica-total-libros", estadisticasBiblioteca.totalLibros);
    pintarMetrica("metrica-total-usuarios", estadisticasBiblioteca.totalUsuarios);
}

//se crean las tarjetas con las metricas compartidas
function crearTarjetaKpi(kpi) {
    const columna = document.createElement("div");
    columna.className = "tarjeta-kpi";
    const deltaHtml = kpi.delta
        ? `<span class="tarjeta-kpi-delta ${kpi.positivo ? "positivo" : "negativo"}">${kpi.delta}</span>`
        : "";
    columna.innerHTML = `
        <div class="tarjeta-kpi-encabezado">
            <span>${kpi.label}</span>
            ${deltaHtml}
        </div>
        <span class="tarjeta-kpi-valor">${kpi.valor}</span>
        <div class="tarjeta-kpi-barra"><span style="width:${kpi.porcentaje}%"></span></div>
        <span class="tarjeta-kpi-nota">${kpi.nota}</span>
    `;
    return columna;
}

function pintarKpis(idContenedor, listaKpis) {
    const contenedor = document.getElementById(idContenedor);
    if (!contenedor) {
        return;
    }
    contenedor.innerHTML = "";
    listaKpis.forEach((kpi) => contenedor.appendChild(crearTarjetaKpi(kpi)));
}

function crearBarraSemana(item, maximo) {
    const columna = document.createElement("div");
    columna.className = "barra-semana";
    const altoPrestamos = Math.max(4, Math.round((item.prestamos / maximo) * 100));
    const altoDevoluciones = Math.max(4, Math.round((item.devoluciones / maximo) * 100));
    columna.innerHTML = `
        <div class="barra-semana-grupo">
            <span class="barra-prestamos" style="height:${altoPrestamos}%"></span>
            <span class="barra-devoluciones" style="height:${altoDevoluciones}%"></span>
        </div>
        <span class="barra-semana-etiqueta">${item.semana}</span>
    `;
    return columna;
}

function pintarGraficoSemanal(idContenedor) {
    const contenedor = document.getElementById(idContenedor);
    if (!contenedor) {
        return;
    }
    const maximo = Math.max(...circulacionSemanal.flatMap((s) => [s.prestamos, s.devoluciones]));
    contenedor.innerHTML = "";
    circulacionSemanal.forEach((item) => contenedor.appendChild(crearBarraSemana(item, maximo)));
}

function crearItemCategoriaPedida(item) {
    const div = document.createElement("div");
    div.className = "item-categoria-pedida";
    div.innerHTML = `
        <div class="item-categoria-pedida-encabezado">
            <span>${item.nombre}</span>
            <span>${item.prestamos} préstamos</span>
        </div>
        <div class="item-categoria-pedida-barra"><span style="width:${item.porcentaje}%"></span></div>
    `;
    return div;
}

function pintarCategoriasMasPedidas(idContenedor) {
    const contenedor = document.getElementById(idContenedor);
    if (!contenedor) {
        return;
    }
    contenedor.innerHTML = "";
    categoriasMasPedidas.forEach((item) => contenedor.appendChild(crearItemCategoriaPedida(item)));
}

// Grafica apilada de estado (se usa tanto para "Estado general de la colección" utilizada en admin y bibliotecario
function pintarFranjaEstado(idFranja, idLeyenda, segmentos) {
    const franja = document.getElementById(idFranja);
    const leyenda = document.getElementById(idLeyenda);
    if (!franja || !leyenda) {
        return;
    }
    franja.innerHTML = segmentos
        .map((s) => `<div style="background:${s.color};width:${s.porcentaje}%"></div>`)
        .join("");
    leyenda.innerHTML = segmentos
        .map((s) => `<span><span class="punto-leyenda" style="background:${s.color}"></span>${s.etiqueta} ${s.porcentaje}%</span>`)
        .join("");
}

function formatearFechaCorta(fechaIso) {
    const fecha = new Date(fechaIso + "T00:00:00");
    return new Intl.DateTimeFormat("es-AR", { day: "numeric", month: "short" }).format(fecha);
}

/** Lista de vencimientos a partir de "prestamos" (Data.js): la usan tanto 
el panel del Lector ("mis" préstamos) como el del Bibliotecario (todos
 los préstamos activos). Como todavía no hay backend/login real, el
 arreglo "prestamos" se trata como si fuera del usuario actual, mismo
 criterio que ya usa MisPrestamos.js.*/
function crearItemVencimiento(prestamo, mostrarUsuario) {
    const div = document.createElement("div");
    const vencido = prestamo.estado === "Vencido";
    div.className = "item-vencimiento" + (vencido ? " vencido" : "");
    const detalle = mostrarUsuario ? prestamo.usuario : (vencido ? "Vencido" : "Vence pronto");
    div.innerHTML = `
        <span class="item-vencimiento-texto">
            <span class="item-vencimiento-titulo">${prestamo.libro}</span>
            <span class="item-vencimiento-detalle">${detalle}</span>
        </span>
        <span class="item-vencimiento-fecha">${formatearFechaCorta(prestamo.fechaLimite)}</span>
    `;
    return div;
}

function pintarVencimientos(idContenedor, mostrarUsuario) {
    const contenedor = document.getElementById(idContenedor);
    if (!contenedor) {
        return;
    }
    const activos = prestamos
        .filter((p) => p.estado === "Activo" || p.estado === "Vencido")
        .slice()
        .sort((a, b) => a.fechaLimite.localeCompare(b.fechaLimite));

    contenedor.innerHTML = "";
    if (!activos.length) {
        contenedor.innerHTML = '<p class="panel-metricas-vacio">No hay préstamos activos por ahora.</p>';
        return;
    }
    activos.forEach((prestamo) => contenedor.appendChild(crearItemVencimiento(prestamo, mostrarUsuario)));
}

//Lector Las 3 primeras se calculan de "prestamos"/"reservas" mediante la info de Data.js. 
function construirKpisLector() { 
    const misPrestamosActivos = prestamos.filter((p) => p.estado === "Activo").length;
    const pctLectura = Math.min(100, Math.round((misMetricasLector.leidosEsteAnio / misMetricasLector.metaAnual) * 100));

    return [
        // -- Personales --
        { label: "Mis préstamos", valor: prestamos.length, delta: "", positivo: true, porcentaje: Math.min(100, prestamos.length * 20), nota: "Activos e históricos" },
        { label: "Mis reservas", valor: reservas.length, delta: "", positivo: true, porcentaje: Math.min(100, reservas.length * 20), nota: "Pendientes y aprobadas" },
        { label: "Próximas devoluciones", valor: misPrestamosActivos, delta: "", positivo: true, porcentaje: Math.min(100, misPrestamosActivos * 25), nota: "Con préstamo activo" },
        { label: "Libros leídos este año", valor: misMetricasLector.leidosEsteAnio, delta: "", positivo: true, porcentaje: pctLectura, nota: `Meta anual: ${misMetricasLector.metaAnual}` },
        { label: "Racha de lectura", valor: `${misMetricasLector.rachaDias} días`, delta: "", positivo: true, porcentaje: Math.min(100, misMetricasLector.rachaDias * 5), nota: "Sin cortar la racha" },
        { label: "Sanciones activas", valor: misMetricasLector.sancionesActivas, delta: "", positivo: misMetricasLector.sancionesActivas === 0, porcentaje: misMetricasLector.sancionesActivas === 0 ? 0 : 60, nota: misMetricasLector.sancionesActivas === 0 ? "Todo al día" : "Requieren tu atención" },

        // -- Generales de la biblioteca --
        { label: "Libros en catálogo", valor: kpisAdministrador[0].valor, delta: kpisAdministrador[0].delta, positivo: true, porcentaje: kpisAdministrador[0].porcentaje, nota: "En toda la biblioteca" },
        { label: "Usuarios de la comunidad", valor: kpisAdministrador[1].valor, delta: kpisAdministrador[1].delta, positivo: true, porcentaje: kpisAdministrador[1].porcentaje, nota: "Lectores activos" },
        { label: "Categorías disponibles", valor: contextoGeneralBiblioteca.categoriasDisponibles, delta: "", positivo: true, porcentaje: 100, nota: "En todo el catálogo" },
        { label: "Nuevos títulos este mes", valor: contextoGeneralBiblioteca.nuevosTitulosEsteMes, delta: "+" + contextoGeneralBiblioteca.nuevosTitulosEsteMes, positivo: true, porcentaje: Math.min(100, contextoGeneralBiblioteca.nuevosTitulosEsteMes * 10), nota: "Recién agregados" }
    ];
}

function pintarLibrosRecomendados() {
    const listaRecomendados = document.getElementById("lista-recomendados");
    if (!listaRecomendados) {
        return;
    }
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

function pintarPanelLector() {
    pintarFechaHoy("panel-lector-fecha");
    pintarKpis("panel-lector-kpis", construirKpisLector());
    pintarVencimientos("lista-vencimientos-lector", false);
    pintarCategoriasMasPedidas("lista-categorias-lector");
    pintarLibrosRecomendados();
}

//Bibliotecario
function pintarEstadoEjemplaresBibliotecario() {
    const total = ejemplares.length;
    if (!total) {
        return;
    }
    const contar = (estado) => ejemplares.filter((e) => e.estado === estado).length;
    const disponibles = Math.round((contar("Disponible") / total) * 100);
    const prestados = Math.round((contar("Prestado") / total) * 100);
    const danados = Math.max(0, 100 - disponibles - prestados);

    pintarFranjaEstado("franja-ejemplares-biblio", "leyenda-ejemplares-biblio", [
        { color: "var(--verde-oscuro)", etiqueta: "Disponibles", porcentaje: disponibles },
        { color: "var(--dorado)", etiqueta: "Prestados", porcentaje: prestados },
        { color: "var(--error-borde)", etiqueta: "Dañados", porcentaje: danados }
    ]);
}

function pintarPanelBibliotecario() {
    pintarFechaHoy("panel-biblio-fecha");
    pintarKpis("panel-biblio-kpis", kpisBibliotecario);
    pintarVencimientos("lista-vencimientos-biblio", true);
    pintarCategoriasMasPedidas("lista-categorias-biblio");
    pintarEstadoEjemplaresBibliotecario();
}

//Administrador
function pintarEstadoColeccionAdministrador() {
    pintarFranjaEstado("franja-coleccion-admin", "leyenda-coleccion-admin", [
        { color: "var(--verde-oscuro)", etiqueta: "Disponibles", porcentaje: estadoColeccion.disponiblesPorcentaje },
        { color: "var(--dorado)", etiqueta: "En préstamo", porcentaje: estadoColeccion.enPrestamoPorcentaje },
        { color: "#C9B792", etiqueta: "Reservados", porcentaje: estadoColeccion.reservadosPorcentaje },
        { color: "#E0D9C6", etiqueta: "En reparación", porcentaje: estadoColeccion.enReparacionPorcentaje }
    ]);

    const indicadores = document.getElementById("indicadores-circulacion-admin");
    if (!indicadores) {
        return;
    }
    indicadores.innerHTML = `
        <div class="indicador-circulacion">
            <span class="indicador-circulacion-valor">${indicadoresCirculacion.duracionMediaDias}</span>
            <span class="indicador-circulacion-etiqueta">Duración media</span>
        </div>
        <div class="indicador-circulacion">
            <span class="indicador-circulacion-valor">${indicadoresCirculacion.devueltosATiempoPorcentaje}</span>
            <span class="indicador-circulacion-etiqueta">Devueltos a tiempo</span>
        </div>
        <div class="indicador-circulacion">
            <span class="indicador-circulacion-valor">${indicadoresCirculacion.librosPorSocio}</span>
            <span class="indicador-circulacion-etiqueta">Libros por socio</span>
        </div>
    `;
}

// El toggle "7 días / 30 días" solo reescala, a modo ilustrativo, las dos métricas que tienen sentido como "actividad del período" (préstamos activos y reservas pendientes). El resto (títulos en catálogo, usuario activos) no depende del rango elegido.

function aplicarRangoAdministrador(dias) {
    const factor = dias === 7 ? 0.28 : 1;
    const kpisEscalados = kpisAdministrador.map((kpi) => {
        if (!kpi.escalable) {
            return kpi;
        }
        return Object.assign({}, kpi, { valor: Math.max(1, Math.round(kpi.valor * factor)) });
    });
    pintarKpis("panel-admin-kpis", kpisEscalados);
}

function inicializarRangoAdministrador() {
    const contenedor = document.getElementById("panel-admin-rango");
    if (!contenedor) {
        return;
    }
    const botones = contenedor.querySelectorAll("button");
    botones.forEach((boton) => {
        boton.addEventListener("click", () => {
            botones.forEach((b) => b.classList.remove("is-activo"));
            boton.classList.add("is-activo");
            aplicarRangoAdministrador(Number(boton.dataset.rango));
        });
    });
}

function pintarPanelAdministrador() {
    pintarFechaHoy("panel-admin-fecha");
    aplicarRangoAdministrador(30);
    inicializarRangoAdministrador();
    pintarGraficoSemanal("grafico-semanal-admin");
    pintarCategoriasMasPedidas("lista-categorias-admin");
    pintarEstadoColeccionAdministrador();
}

document.addEventListener("DOMContentLoaded", () => {
    mostrarSeccionDelRolActual();
    pintarMetricasVisitante();
    pintarPanelLector();
    pintarPanelBibliotecario();
    pintarPanelAdministrador();
});
