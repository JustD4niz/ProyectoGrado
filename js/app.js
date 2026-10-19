(function () {
    "use strict";

    // Confirma acciones destructivas.
    document.addEventListener("click", function (ev) {
        var el = ev.target.closest("[data-confirmar]");
        if (el) {
            var msg = el.getAttribute("data-confirmar") || "Esta seguro de continuar?";
            if (!window.confirm(msg)) {
                ev.preventDefault();
                ev.stopPropagation();
            }
        }
    });

    // Cierre automatico de alertas.
    document.querySelectorAll(".alert").forEach(function (a) {
        setTimeout(function () {
            a.style.transition = "opacity .5s";
            a.style.opacity = "0";
            setTimeout(function () { a.style.display = "none"; }, 500);
        }, 6000);
    });

    // Muestra u oculta campos segun el tipo de solicitud.
    function refrescarCamposSolicitud() {
        var sel = document.querySelector("[data-tipo-solicitud]");
        if (!sel) { return; }
        var tipo = sel.value;
        document.querySelectorAll("[data-solo]").forEach(function (el) {
            var tipos = el.getAttribute("data-solo").split(" ");
            el.style.display = tipos.indexOf(tipo) >= 0 ? "" : "none";
        });
    }
    var ts = document.querySelector("[data-tipo-solicitud]");
    if (ts) {
        ts.addEventListener("change", refrescarCamposSolicitud);
        refrescarCamposSolicitud();
    }

    // Filtros que se envian solos al cambiar.
    document.querySelectorAll("[data-auto]").forEach(function (el) {
        el.addEventListener("change", function () {
            var form = el.closest("form");
            if (form) { form.submit(); }
        });
    });

    // Calculo de total en el formulario de rubrica.
    function calcularRubrica() {
        var campos = document.querySelectorAll("[data-rubrica]");
        if (!campos.length) { return; }
        var suma = 0;
        campos.forEach(function (c) { suma += parseFloat(c.value) || 0; });
        var salida = document.querySelector("[data-rubrica-total]");
        if (salida) { salida.textContent = (suma / campos.length).toFixed(2); }
    }
    document.querySelectorAll("[data-rubrica]").forEach(function (c) {
        c.addEventListener("input", calcularRubrica);
    });
    calcularRubrica();
})();
