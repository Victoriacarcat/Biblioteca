/** Dashboard.js
Panel principal según el rol actual. Cada panel vive en su propia sección
del JSP (data-rol) y aquí solo se llena el que corresponde.

    GET /api/estadisticas             -> números generales; el personal recibe
                                         también circulación, copias y sanciones
    GET /api/estadisticas?tipo=lector -> resumen personal del lector
    GET /api/prestamos?estado=ACTIVO  -> próximas devoluciones (personal)
    GET /api/libros?soloDisponibles=true -> libros recomendados (lector)

Lector, Bibliotecario y Administrador comparten el mismo lenguaje visual
("panel-metricas"): tarjetas KPI, gráfico semanal, categorías más pedidas
y franja de estado de las copias.
*/

// Cuántos elementos se muestran en las listas del panel
const MAX_VENCIMIENTOS = 5;
const MAX_RECOMENDADOS = 3;

// ---- Piezas comunes ----

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
    const texto = new Intl.DateTimeFormat("es-CO", {
        weekday: "long",
        day: "numeric",
        month: "long"
    }).format(new Date());
    elemento.textContent = texto.charAt(0).toUpperCase() + texto.slice(1);
}

/*
 Tarjeta KPI: {label, valor, nota, porcentaje}. "porcentaje" (0 a 100) dibuja
 la barrita; si es null, la tarjeta va sin barra.
*/
function crearTarjetaKpi(kpi) {
    const columna = document.createElement("div");
    columna.className = "tarjeta-kpi";
    const barra = kpi.porcentaje === null || kpi.porcentaje === undefined
        ? ""
        : `<div class="tarjeta-kpi-barra"><span style="width:${Math.min(100, Math.max(0, kpi.porcentaje))}%"></span></div>`;
    columna.innerHTML = `
        <div class="tarjeta-kpi-encabezado">
            <span>${escaparHtml(kpi.label)}</span>
        </div>
        <span class="tarjeta-kpi-valor">${escaparHtml(kpi.valor)}</span>
        ${barra}
        <span class="tarjeta-kpi-nota">${escaparHtml(kpi.nota)}</span>
    `;
    return columna;
}

function pintarKpis(idContenedor, listaKpis) {
    const contenedor = document.getElementById(idContenedor);
    contenedor.innerHTML = "";
    listaKpis.forEach((kpi) => contenedor.appendChild(crearTarjetaKpi(kpi)));
}

/* "3 vencidos" / "1 vencido": texto con singular y plural. */
function cantidadConTexto(cantidad, singular, plural) {
    return `${cantidad} ${cantidad === 1 ? singular : plural}`;
}

// ---- Gráfico de circulación por semana ----

function crearBarraSemana(semana, maximo) {
    const columna = document.createElement("div");
    columna.className = "barra-semana";
    const altoPrestamos = Math.max(4, Math.round((semana.prestamos / maximo) * 100));
    const altoDevoluciones = Math.max(4, Math.round((semana.devoluciones / maximo) * 100));
    // Etiqueta: el lunes de la semana como "13/10"
    const etiqueta = formatearFecha(semana.inicio).substring(0, 5);
    columna.title = `Semana del ${formatearFecha(semana.inicio)}: `
        + `${semana.prestamos} préstamos, ${semana.devoluciones} devoluciones`;
    columna.innerHTML = `
        <div class="barra-semana-grupo">
            <span class="barra-prestamos" style="height:${altoPrestamos}%"></span>
            <span class="barra-devoluciones" style="height:${altoDevoluciones}%"></span>
        </div>
        <span class="barra-semana-etiqueta">${etiqueta}</span>
    `;
    return columna;
}

function pintarGraficoSemanal(idContenedor, semanas) {
    const contenedor = document.getElementById(idContenedor);
    // Math.max(1, ...) evita dividir entre cero si todas las semanas están en 0
    const maximo = Math.max(1, ...semanas.flatMap((s) => [s.prestamos, s.devoluciones]));
    contenedor.innerHTML = "";
    semanas.forEach((semana) => contenedor.appendChild(crearBarraSemana(semana, maximo)));
}

// ---- Categorías más pedidas ----

function pintarCategoriasMasPedidas(idContenedor, categorias) {
    const contenedor = document.getElementById(idContenedor);
    contenedor.innerHTML = "";
    if (!categorias || categorias.length === 0) {
        contenedor.innerHTML = '<p class="panel-metricas-vacio">Todavía no hay préstamos registrados.</p>';
        return;
    }
    // La barra de cada categoría es relativa a la más pedida (la primera)
    const maximo = categorias[0].prestamos;
    categorias.forEach((categoria) => {
        const div = document.createElement("div");
        div.className = "item-categoria-pedida";
        div.innerHTML = `
            <div class="item-categoria-pedida-encabezado">
                <span>${escaparHtml(categoria.nombre)}</span>
                <span>${cantidadConTexto(categoria.prestamos, "préstamo", "préstamos")}</span>
            </div>
            <div class="item-categoria-pedida-barra">
                <span style="width:${Math.round((categoria.prestamos / maximo) * 100)}%"></span>
            </div>
        `;
        contenedor.appendChild(div);
    });
}

// ---- Franja de estado de las copias ----

/*
 Reparte las copias en disponibles, apartadas por una reserva aprobada y en
 préstamo (el resto). Los porcentajes se redondean, así que pueden sumar 99 o 101.
*/
function pintarEstadoCopias(idFranja, idLeyenda, estadisticas) {
    const franja = document.getElementById(idFranja);
    const leyenda = document.getElementById(idLeyenda);
    const total = estadisticas.copiasTotales;
    if (!total) {
        franja.innerHTML = "";
        leyenda.innerHTML = '<span class="panel-metricas-vacio">Todavía no hay copias registradas.</span>';
        return;
    }
    const disponibles = estadisticas.copiasDisponibles;
    const apartadas = estadisticas.copiasApartadas;
    const enPrestamo = Math.max(0, total - disponibles - apartadas);
    const segmentos = [
        { color: "var(--verde-oscuro)", etiqueta: "Disponibles", cantidad: disponibles },
        { color: "var(--dorado)", etiqueta: "En préstamo", cantidad: enPrestamo },
        { color: "#C9B792", etiqueta: "Apartadas por reserva", cantidad: apartadas }
    ];
    segmentos.forEach((s) => { s.porcentaje = Math.round((s.cantidad / total) * 100); });

    franja.innerHTML = segmentos
        .map((s) => `<div style="background:${s.color};width:${s.porcentaje}%"></div>`)
        .join("");
    leyenda.innerHTML = segmentos
        .map((s) => `<span><span class="punto-leyenda" style="background:${s.color}"></span>${s.etiqueta} ${s.cantidad} (${s.porcentaje}%)</span>`)
        .join("");
}

// ---- Próximas devoluciones ----

/* mostrarLector: en el panel del personal se ve quién tiene el libro. */
function crearItemVencimiento(prestamo, mostrarLector) {
    const div = document.createElement("div");
    const vencido = estaVencido(prestamo);
    div.className = "item-vencimiento" + (vencido ? " vencido" : "");
    const detalle = mostrarLector ? prestamo.nombreUsuario : (vencido ? "Vencido" : "Vence pronto");
    div.innerHTML = `
        <span class="item-vencimiento-texto">
            <span class="item-vencimiento-titulo">${escaparHtml(prestamo.tituloLibro)}</span>
            <span class="item-vencimiento-detalle">${escaparHtml(detalle)}</span>
        </span>
        <span class="item-vencimiento-fecha">${formatearFecha(prestamo.fechaLimite)}</span>
    `;
    return div;
}

function pintarVencimientos(idContenedor, prestamosActivos, mostrarLector) {
    const contenedor = document.getElementById(idContenedor);
    const lista = prestamosActivos
        .slice()
        .sort((a, b) => a.fechaLimite.localeCompare(b.fechaLimite))
        .slice(0, MAX_VENCIMIENTOS);

    contenedor.innerHTML = "";
    if (lista.length === 0) {
        contenedor.innerHTML = '<p class="panel-metricas-vacio">No hay préstamos activos por ahora.</p>';
        return;
    }
    lista.forEach((prestamo) => contenedor.appendChild(crearItemVencimiento(prestamo, mostrarLector)));
}

// ---- Visitante ----

function pintarPanelVisitante(estadisticas) {
    pintarMetrica("metrica-total-libros", estadisticas.totalLibros);
    pintarMetrica("metrica-total-usuarios", estadisticas.lectoresActivos);
}

// ---- Lector ----

function construirKpisLector(resumen, estadisticas) {
    return [
        // -- Personales --
        { label: "Préstamos activos", valor: resumen.prestamosActivos, porcentaje: null,
            nota: resumen.prestamosVencidos > 0
                ? cantidadConTexto(resumen.prestamosVencidos, "vencido: devuélvelo pronto", "vencidos: devuélvelos pronto")
                : "Ninguno vencido" },
        { label: "Reservas activas", valor: resumen.reservasActivas, porcentaje: null, nota: "Pendientes y aprobadas" },
        { label: "Libros leídos este año", valor: resumen.leidosEsteAnio, porcentaje: null, nota: "Préstamos devueltos" },
        { label: "Sanciones pendientes", valor: resumen.sancionesPendientes, porcentaje: null,
            nota: resumen.sancionesPendientes > 0 ? "Debes " + formatearDinero(resumen.deudaPendiente) : "Todo al día" },

        // -- Generales de la biblioteca --
        { label: "Libros en catálogo", valor: estadisticas.totalLibros, porcentaje: null, nota: "En toda la biblioteca" },
        { label: "Lectores activos", valor: estadisticas.lectoresActivos, porcentaje: null, nota: "En la comunidad" },
        { label: "Categorías", valor: estadisticas.categoriasActivas, porcentaje: null, nota: "En todo el catálogo" },
        { label: "Nuevos títulos este mes", valor: estadisticas.nuevosTitulosEsteMes, porcentaje: null, nota: "Recién agregados" }
    ];
}

/* Recomendados: los libros más recientes con copias disponibles. */
async function pintarLibrosRecomendados() {
    const contenedor = document.getElementById("lista-recomendados");
    try {
        const libros = await api.get("/libros?soloDisponibles=true");
        const recomendados = libros.sort((a, b) => b.id - a.id).slice(0, MAX_RECOMENDADOS);
        contenedor.innerHTML = "";
        if (recomendados.length === 0) {
            contenedor.innerHTML = '<p class="panel-metricas-vacio">No hay libros disponibles por ahora.</p>';
            return;
        }
        recomendados.forEach((libro) => {
            const columna = document.createElement("div");
            columna.className = "col-md-4 tarjeta-libro-contenedor";
            columna.innerHTML = `
                <a class="tarjeta-libro d-block text-decoration-none text-reset"
                   href="${contextPath}/FrontEnd/Catalogo/Detallelibro.jsp?id=${libro.id}">
                    <img class="portada-libro" src="${escaparHtml(urlPortada(libro.portadaUrl))}"
                         alt="Portada de ${escaparHtml(libro.titulo)}" onerror="portadaRota(this)">
                    <h3 class="titulo-libro">${escaparHtml(libro.titulo)}</h3>
                    <p class="autor-libro">${escaparHtml(libro.autores.join(", "))}</p>
                </a>
            `;
            contenedor.appendChild(columna);
        });
    } catch (error) {
        contenedor.innerHTML = '<p class="panel-metricas-vacio">No se pudieron cargar los recomendados.</p>';
    }
}

async function pintarPanelLector(estadisticas) {
    const resumen = await api.get("/estadisticas?tipo=lector");
    pintarFechaHoy("panel-lector-fecha");
    pintarKpis("panel-lector-kpis", construirKpisLector(resumen, estadisticas));
    pintarVencimientos("lista-vencimientos-lector", resumen.proximasDevoluciones, false);
    pintarCategoriasMasPedidas("lista-categorias-lector", estadisticas.categoriasMasPedidas);
    pintarLibrosRecomendados();
}

// ---- Personal (bibliotecario y administrador) ----

function kpiPrestamosActivos(e) {
    return { label: "Préstamos activos", valor: e.prestamosActivos, porcentaje: e.porcentajeOcupacion,
        nota: (e.prestamosVencidos > 0 ? cantidadConTexto(e.prestamosVencidos, "vencido", "vencidos") : "Ninguno vencido")
            + ` · ${e.porcentajeOcupacion}% de las copias en uso` };
}

function kpiReservasPendientes(e) {
    return { label: "Reservas pendientes", valor: e.reservasPendientes, porcentaje: null,
        nota: cantidadConTexto(e.copiasApartadas, "copia apartada", "copias apartadas") + " para retirar" };
}

function kpiSancionesPendientes(e) {
    return { label: "Sanciones pendientes", valor: e.sancionesPendientes, porcentaje: null,
        nota: e.sancionesPendientes > 0 ? formatearDinero(e.montoSancionesPendientes) + " por cobrar" : "Nada por cobrar" };
}

function kpiPrestamosHoy(e) {
    return { label: "Préstamos de hoy", valor: e.prestamosHoy, porcentaje: null, nota: "Entregados hoy" };
}

async function pintarPanelBibliotecario(e) {
    pintarFechaHoy("panel-biblio-fecha");
    pintarKpis("panel-biblio-kpis", [kpiPrestamosHoy(e), kpiPrestamosActivos(e), kpiReservasPendientes(e), kpiSancionesPendientes(e)]);
    pintarCategoriasMasPedidas("lista-categorias-biblio", e.categoriasMasPedidas);
    pintarEstadoCopias("franja-ejemplares-biblio", "leyenda-ejemplares-biblio", e);
    pintarVencimientos("lista-vencimientos-biblio", await api.get("/prestamos?estado=ACTIVO"), true);
}

/* Los tres indicadores de circulación del panel del administrador. */
function pintarIndicadoresCirculacion(e) {
    const indicador = (valor, etiqueta) => `
        <div class="indicador-circulacion">
            <span class="indicador-circulacion-valor">${valor}</span>
            <span class="indicador-circulacion-etiqueta">${etiqueta}</span>
        </div>`;
    // Decimal con coma, como se escribe en Colombia: 9.4 -> "9,4"
    const decimal = (numero) => Number(numero).toLocaleString("es-CO");
    // Mientras no haya devoluciones o lectores, se muestra "—"
    document.getElementById("indicadores-circulacion-admin").innerHTML =
        indicador(e.duracionMediaDias === null ? "—" : `${decimal(e.duracionMediaDias)} días`, "Duración media de un préstamo")
        + indicador(e.porcentajeATiempo === null ? "—" : `${e.porcentajeATiempo}%`, "Devueltos a tiempo")
        + indicador(e.prestamosPorLector === null ? "—" : decimal(e.prestamosPorLector), "Préstamos por lector activo");
}

function pintarPanelAdministrador(e) {
    pintarFechaHoy("panel-admin-fecha");
    pintarKpis("panel-admin-kpis", [
        { label: "Títulos en catálogo", valor: e.totalLibros, porcentaje: null,
            nota: cantidadConTexto(e.nuevosTitulosEsteMes, "nuevo este mes", "nuevos este mes") },
        { label: "Lectores activos", valor: e.lectoresActivos, porcentaje: null, nota: "Con cuenta activa" },
        kpiPrestamosActivos(e),
        kpiPrestamosHoy(e),
        kpiReservasPendientes(e),
        kpiSancionesPendientes(e)
    ]);
    pintarGraficoSemanal("grafico-semanal-admin", e.circulacionSemanal);
    pintarCategoriasMasPedidas("lista-categorias-admin", e.categoriasMasPedidas);
    pintarEstadoCopias("franja-coleccion-admin", "leyenda-coleccion-admin", e);
    pintarIndicadoresCirculacion(e);
}

// Espera a saber quién inició sesión (Auth.js) antes de pintar
sesionLista.then(async () => {
    // Muestra solo el panel del rol actual (Utilidades.js)
    mostrarSegunRol();
    const rol = obtenerRolActual();
    try {
        const estadisticas = await api.get("/estadisticas");
        if (rol === "visitante") {
            pintarPanelVisitante(estadisticas);
        } else if (rol === "lector") {
            await pintarPanelLector(estadisticas);
        } else if (rol === "bibliotecario") {
            await pintarPanelBibliotecario(estadisticas);
        } else {
            pintarPanelAdministrador(estadisticas);
        }
    } catch (error) {
        mostrarError("dashboard-error", "No se pudieron cargar los datos del panel: " + error.message);
    }
});
