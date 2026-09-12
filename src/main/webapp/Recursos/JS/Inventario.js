/** Inventario.js
 Vista de Bibliotecario/Administrador: gestiona los ejemplares (copias
 físicas/digitales) de cada libro (arreglo "ejemplares" de Data.js).
 Sin backend todavía: agregar/dar de baja/eliminar solo modifican el
 arreglo en memoria (se pierde al recargar la página).

 Reglas de negocio aplicadas aquí:
   1. Un ejemplar "Prestado" no se puede dar de baja ni eliminar: hay
      que esperar a que se registre la devolución (Devoluciones.js).
   2. "Dar de baja" lo puede hacer Bibliotecario o Administrador; el
      ejemplar queda con estado "De baja" pero no desaparece del
      inventario (trazabilidad).
   3. Eliminar el registro por completo es exclusivo del Administrador
      (botón oculto para Bibliotecario, mismo patrón data-rol de
      Catalogo.js/Header.js).
   4. El estado "Disponible" no se asigna a mano desde esta pantalla:
      lo controla el flujo real de préstamos/reservas/devoluciones.

 TODO: cuando exista el backend, "libros[].disponible" se calculará a
partir de estos ejemplares en vez de ser un booleano fijo.*/ 

function obtenerLibroPorId(id) {
    return libros.find((libro) => libro.id === id);
}

function tituloDelEjemplar(ejemplar) {
    const libro = obtenerLibroPorId(ejemplar.libroId);
    return libro ? libro.titulo : "Libro eliminado";
}

function celdaLibroDelEjemplar(ejemplar) {
    const libro = obtenerLibroPorId(ejemplar.libroId);
    if (!libro) {
        return "Libro eliminado";
    }
    return `
        <span class="celda-con-icono">
            <img class="miniatura-libro" src="${libro.portada}" alt="">
            ${libro.titulo}
        </span>
    `;
}


function claseEstadoEjemplar(estado) {
    if (estado === "Disponible") {
        return "estado-positivo";
    }
    if (estado === "Dañado") {
        return "estado-negativo";
    }
    if (estado === "Reservado") {
        return "estado-alerta";
    }
    return "estado-neutro";
}

function mostrarErrorInventario(mensaje) {
    const caja = document.getElementById("inventario-error");
    caja.textContent = mensaje;
    caja.hidden = false;
}

function ocultarErrorInventario() {
    document.getElementById("inventario-error").hidden = true;
}

function pintarMetricaInventario(idElemento, valor) {
    const elemento = document.getElementById(idElemento);
    if (elemento) {
        elemento.textContent = valor;
    }
}

function pintarMetricasInventario() {
    pintarMetricaInventario("metrica-total-ejemplares", ejemplares.length);
    pintarMetricaInventario(
        "metrica-ejemplares-disponibles",
        ejemplares.filter((ejemplar) => ejemplar.estado === "Disponible").length
    );
    pintarMetricaInventario(
        "metrica-ejemplares-prestados",
        ejemplares.filter((ejemplar) => ejemplar.estado === "Prestado").length
    );
    pintarMetricaInventario(
        "metrica-ejemplares-fuera-servicio",
        ejemplares.filter((ejemplar) => ejemplar.estado === "Dañado" || ejemplar.estado === "De baja").length
    );
}

function crearFilaEjemplar(ejemplar) {
    const fila = document.createElement("tr");
    const estaPrestado = ejemplar.estado === "Prestado";
    const yaDeBaja = ejemplar.estado === "De baja";

    fila.innerHTML = `
        <td>${ejemplar.codigo}</td>
        <td>${celdaLibroDelEjemplar(ejemplar)}</td>
        <td><span class="estado-ejemplar ${claseEstadoEjemplar(ejemplar.estado)}">${ejemplar.estado}</span></td>
        <td>
            <button type="button" class="btn btn-outline-secondary btn-sm boton-dar-de-baja" ${(estaPrestado || yaDeBaja) ? "disabled" : ""}>
                Dar de baja
            </button>
            <button type="button" class="btn btn-outline-danger btn-sm boton-eliminar-ejemplar" data-rol="administrador" hidden ${estaPrestado ? "disabled" : ""}>
                Eliminar
            </button>
        </td>
    `;

    if (estaPrestado) {
        fila.title = "Este ejemplar está prestado: espera a que se registre la devolución.";
    }

    fila.querySelector(".boton-dar-de-baja").addEventListener("click", () => {
        if (ejemplar.estado === "Prestado") {
            mostrarErrorInventario(`No se puede dar de baja "${ejemplar.codigo}": está prestado.`);
            return;
        }
        ocultarErrorInventario();
        ejemplar.estado = "De baja";
        filtrarEjemplares();
        pintarMetricasInventario();
    });

    fila.querySelector(".boton-eliminar-ejemplar").addEventListener("click", () => {
        if (ejemplar.estado === "Prestado") {
            mostrarErrorInventario(`No se puede eliminar "${ejemplar.codigo}": está prestado.`);
            return;
        }
        ocultarErrorInventario();
        const indice = ejemplares.indexOf(ejemplar);
        if (indice !== -1) {
            ejemplares.splice(indice, 1);
        }
        filtrarEjemplares();
        pintarMetricasInventario();
    });

    // Muestra el botón "Eliminar" solo para Administrador (mismo patrón
    // data-rol que usan Header.js y Catalogo.js).
    const rolActual = obtenerRolActual();
    fila.querySelectorAll("[data-rol]").forEach((elemento) => {
        const rolesPermitidos = elemento.getAttribute("data-rol").split(",");
        if (rolesPermitidos.includes(rolActual)) {
            elemento.hidden = false;
        }
    });

    return fila;
}

function pintarEjemplares(listaEjemplares) {
    const cuerpo = document.getElementById("cuerpo-inventario");
    cuerpo.innerHTML = "";
    listaEjemplares.forEach((ejemplar) => {
        cuerpo.appendChild(crearFilaEjemplar(ejemplar));
    });
}

function filtrarEjemplares() {
    const texto = document.getElementById("buscar-ejemplar").value.trim().toLowerCase();
    const estadoElegido = document.getElementById("filtro-estado-ejemplar").value;

    const filtrados = ejemplares.filter((ejemplar) => {
        const coincideTexto =
            ejemplar.codigo.toLowerCase().includes(texto) ||
            tituloDelEjemplar(ejemplar).toLowerCase().includes(texto);
        const coincideEstado = !estadoElegido || ejemplar.estado === estadoElegido;
        return coincideTexto && coincideEstado;
    });

    pintarEjemplares(filtrados);
}

function llenarSelectDeLibros() {
    const select = document.getElementById("ejemplar-libro");
    libros.forEach((libro) => {
        const opcion = document.createElement("option");
        opcion.value = libro.id;
        opcion.textContent = libro.titulo;
        select.appendChild(opcion);
    });
}

function manejarAltaDeEjemplar(evento) {
    evento.preventDefault();

    const libroId = Number(document.getElementById("ejemplar-libro").value);
    const codigo = document.getElementById("ejemplar-codigo").value.trim();

    if (!libroId || !codigo) {
        mostrarErrorInventario("Selecciona un libro e ingresa un código de inventario.");
        return;
    }

    const codigoRepetido = ejemplares.some(
        (ejemplar) => ejemplar.codigo.toLowerCase() === codigo.toLowerCase()
    );
    if (codigoRepetido) {
        mostrarErrorInventario(`Ya existe un ejemplar con el código "${codigo}".`);
        return;
    }

    ocultarErrorInventario();

    const nuevoId = Math.max(0, ...ejemplares.map((ejemplar) => ejemplar.id)) + 1;
    ejemplares.push({ id: nuevoId, libroId: libroId, codigo: codigo, estado: "Disponible" });

    document.getElementById("form-agregar-ejemplar").reset();
    filtrarEjemplares();
    pintarMetricasInventario();
}

document.addEventListener("DOMContentLoaded", () => {
    llenarSelectDeLibros();
    pintarMetricasInventario();
    pintarEjemplares(ejemplares);

    document.getElementById("buscar-ejemplar").addEventListener("input", filtrarEjemplares);
    document.getElementById("filtro-estado-ejemplar").addEventListener("change", filtrarEjemplares);
    document.getElementById("form-agregar-ejemplar").addEventListener("submit", manejarAltaDeEjemplar);
});
