package servicios;

import modelos.Temperatura;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemperaturasServicio {

    // Leer el archivo CSV, como en el ejemplo de monedas.
    public static List<Temperatura> getDatos(String nombreArchivo) {
        var formatoFecha = DateTimeFormatter.ofPattern("d/M/yyyy");
        try (Stream<String> lineasArchivo = Files.lines(Paths.get(nombreArchivo))) {
            return lineasArchivo.skip(1)
                    .map(linea -> linea.split(","))
                    .map(textos -> new Temperatura(textos[0],
                            LocalDate.parse(textos[1], formatoFecha),
                            Double.parseDouble(textos[2])
                    ))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    // Fechas del archivo, sin repetir y en orden.
    public static List<LocalDate> getFechasDisponibles(List<Temperatura> datos) {
        return datos.stream()
                .map(Temperatura::getFecha)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Obtener los nombres de las ciudades sin repetir.
    public static List<String> getCiudades(List<Temperatura> datos) {
        return datos.stream()
                .map(Temperatura::getCiudad)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Filtrar los registros por el rango de fechas elegido.
    public static List<Temperatura> filtrar(List<Temperatura> datos,
                                           LocalDate desde, LocalDate hasta) {
        return datos.stream()
                .filter(dato -> !dato.getFecha().isBefore(desde) &&
                        !dato.getFecha().isAfter(hasta))
                .collect(Collectors.toList());
    }

    // Calcular el promedio de cada ciudad con registros en el rango.
    public static Map<String, Double> getDatosGrafica(List<Temperatura> datos) {
        return getCiudades(datos).stream()
                .collect(Collectors.toMap(
                        ciudad -> ciudad,
                        ciudad -> datos.stream()
                                .filter(dato -> dato.getCiudad().equals(ciudad))
                                .mapToDouble(Temperatura::getTemperatura)
                                .average().orElse(0)
                ));
    }

    // Buscar los registros del día, ordenados de menor a mayor temperatura.
    public static List<Temperatura> getDatosFecha(List<Temperatura> datos,
                                                   LocalDate fecha) {
        return datos.stream()
                .filter(dato -> dato.getFecha().equals(fecha))
                .sorted(Comparator.comparingDouble(Temperatura::getTemperatura))
                .collect(Collectors.toList());
    }
}
