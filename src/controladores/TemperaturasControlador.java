package controladores;

import modelos.Temperatura;
import servicios.TemperaturasServicio;
import vistas.TemperaturasVista;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class TemperaturasControlador {
    private final TemperaturasVista vista;
    private List<Temperatura> temperaturas;

    public TemperaturasControlador(TemperaturasVista vista) {
        this.vista = vista;
        this.vista.setGraficarClick(evento -> graficar());
        this.vista.setCalcularEstadisticasClick(evento -> calcularEstadisticas());
        this.vista.setFechaCambiada(() -> calcularEstadisticas());
        cargarDatos();
    }

    private void cargarDatos() {
        // Si ejecutamos App.java desde la carpeta del proyecto o desde src.
        String nombreArchivo = "src/datos/Temperaturas.csv";
        if (!Files.exists(Paths.get(nombreArchivo))) {
            nombreArchivo = "datos/Temperaturas.csv";
        }
        if (!Files.exists(Paths.get(nombreArchivo))) {
            nombreArchivo = "Temperaturas.csv";
        }

        temperaturas = TemperaturasServicio.getDatos(nombreArchivo);
        vista.setTotalRegistros(temperaturas.size());
        vista.setFechasDisponibles(TemperaturasServicio.getFechasDisponibles(temperaturas));
        if (temperaturas.isEmpty()) {
            vista.mostrarMensaje("No se encontraron datos. Revisa el archivo Temperaturas.csv");
        }
    }

    private void graficar() {
        LocalDate desde = vista.getFechaDesde();
        LocalDate hasta = vista.getFechaHasta();

        if (desde.isAfter(hasta)) {
            vista.mostrarMensaje("La fecha inicial debe ser anterior a la final.");
            return;
        }

        var datosFiltrados = TemperaturasServicio.filtrar(temperaturas, desde, hasta);
        if (datosFiltrados.isEmpty()) {
            vista.mostrarMensaje("No hay temperaturas en ese rango de fechas.");
            return;
        }

        var datosGrafica = TemperaturasServicio.getDatosGrafica(datosFiltrados);
        var ciudades = TemperaturasServicio.getCiudades(datosFiltrados);
        vista.mostrarGrafica(datosGrafica, ciudades, desde, hasta);
        vista.activarPestana(0);
    }

    private void calcularEstadisticas() {
        LocalDate fecha = vista.getFecha();
        var datosFecha = TemperaturasServicio.getDatosFecha(temperaturas, fecha);

        if (datosFecha.isEmpty()) {
            vista.mostrarMensaje("No hay datos para la fecha elegida.");
            return;
        }

        // El primer dato es el menor y el último es el mayor.
        var menor = datosFecha.get(0);
        var mayor = datosFecha.get(datosFecha.size() - 1);

        vista.mostrarEstadisticas(mayor, menor, fecha);
        vista.activarPestana(1);
    }
}
