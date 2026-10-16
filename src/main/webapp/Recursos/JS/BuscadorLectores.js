/** BuscadorLectores.js
Buscador de lectores para los formularios del personal (registrar un
préstamo en el mostrador, registrar una sanción).

    GET /api/usuarios?tipo=lectores&texto=ana
        -> lectores cuyo nombre, correo o documento contienen el texto
           (mínimo 3 letras, máximo 20 resultados). Solo personal.

En el JSP hacen falta tres elementos: un <input> para escribir, un botón
"Buscar" y un <select> donde quedan los resultados. El lector elegido es
el value del <select> (su id), o "" si no hay ninguno.

Depende de Api.js y Utilidades.js.
*/

/* Texto de cada opción: "Ana Gómez · ana@correo.com · CC 1021387416 (Suspendido)". */
function textoOpcionLector(lector) {
    let texto = `${lector.nombreCompleto} · ${lector.correo} · ${lector.tipoDocumento} ${lector.documento}`;
    if (lector.estado !== "ACTIVO") {
        texto += ` (${textoEstado("usuario", lector.estado)})`;
    }
    return texto;
}

/* Deja el <select> solo con un mensaje (sin lector elegible). */
function vaciarSelectLectores(select, mensaje) {
    select.innerHTML = "";
    const opcion = document.createElement("option");
    opcion.value = "";
    opcion.textContent = mensaje;
    select.appendChild(opcion);
}

/*
 Conecta el buscador: al pulsar el botón (o Enter en el campo) pide los
 lectores y llena el <select>. Los errores se muestran en idCajaError.
*/
function conectarBuscadorLectores(idInput, idBoton, idSelect, idCajaError) {
    const input = document.getElementById(idInput);
    const boton = document.getElementById(idBoton);
    const select = document.getElementById(idSelect);
    vaciarSelectLectores(select, "Busca un lector para elegirlo");

    async function buscar() {
        ocultarError(idCajaError);
        boton.disabled = true;
        try {
            const lectores = await api.get("/usuarios?tipo=lectores&texto=" + encodeURIComponent(input.value.trim()));
            if (lectores.length === 0) {
                vaciarSelectLectores(select, "No se encontró ningún lector");
            } else {
                select.innerHTML = "";
                lectores.forEach((lector) => {
                    const opcion = document.createElement("option");
                    opcion.value = lector.id;
                    opcion.textContent = textoOpcionLector(lector);
                    select.appendChild(opcion);
                });
            }
        } catch (error) {
            vaciarSelectLectores(select, "Busca un lector para elegirlo");
            mostrarError(idCajaError, error.message);
        }
        boton.disabled = false;
    }

    boton.addEventListener("click", buscar);
    input.addEventListener("keydown", (evento) => {
        if (evento.key === "Enter") {
            evento.preventDefault(); // dentro de un <form>, Enter no debe enviarlo
            buscar();
        }
    });
}
