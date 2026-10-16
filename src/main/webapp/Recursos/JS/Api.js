/** Api.js
Funciones para hablar con el backend (/api/...) desde cualquier vista.
Requiere que la página defina antes la constante global "contextPath"
(igual que hoy: const contextPath = "${pageContext.request.contextPath}").

Uso:
    const prestamos = await api.get("/prestamos");
    await api.put("/prestamos?id=" + id + "&accion=renovar");
    await api.post("/reservas", { idLibro: 5 });
    await api.put("/usuarios?id=" + id + "&accion=estado", { estado: "SUSPENDIDO" });
    await api.delete("/favoritos?idLibro=" + idLibro);

Si el backend responde con error, la función lanza un Error cuyo mensaje es
el que mandó el servidor (ya viene en español, listo para mostrarlo):
    try {
        await api.put("/prestamos?id=" + id + "&accion=renovar");
    } catch (error) {
        alert(error.message);       // "Ya renovaste este préstamo 2 veces..."
        // error.status = 400 (dato inválido), 401 (sin sesión), 403 (sin permiso)...
    }
*/

async function llamarApi(metodo, ruta, datos) {
    const opciones = {
        method: metodo,
        // Envía la cookie de sesión (JSESSIONID) con cada petición
        credentials: "same-origin",
        headers: { "Accept": "application/json" }
    };
    // El backend exige este encabezado en POST/PUT/DELETE (protección contra CSRF),
    // aunque no se envíe cuerpo.
    if (metodo !== "GET") {
        opciones.headers["Content-Type"] = "application/json";
        opciones.body = JSON.stringify(datos ?? {});
    }

    const respuesta = await fetch(contextPath + "/api" + ruta, opciones);

    // 204 = todo bien, sin contenido que leer
    if (respuesta.status === 204) {
        return null;
    }

    const cuerpo = await respuesta.json().catch(() => null);
    if (!respuesta.ok) {
        const error = new Error(cuerpo?.error ?? "No se pudo completar la acción.");
        error.status = respuesta.status;
        throw error;
    }
    return cuerpo;
}

const api = {
    get: (ruta) => llamarApi("GET", ruta),
    post: (ruta, datos) => llamarApi("POST", ruta, datos),
    put: (ruta, datos) => llamarApi("PUT", ruta, datos),
    delete: (ruta) => llamarApi("DELETE", ruta)
};
