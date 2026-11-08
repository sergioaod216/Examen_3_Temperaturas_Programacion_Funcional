package vistas;

import modelos.Temperatura;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class TemperaturasVista extends JFrame {
    private SelectorFecha dccDesde, dccHasta, dccFecha;
    private JButton btnGraficar, btnCalcularEstadisticas;
    private JPanel pnlGrafica, pnlEstadisticas;
    private JTabbedPane tpTemperaturas;
    private JLabel lblTotal, lblFechasDisponibles;


    public TemperaturasVista() {
        setTitle("Temperaturas de las ciudades");
        setSize(850, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel pnlOpciones = new JPanel(new GridLayout(3, 1, 5, 5));
        pnlOpciones.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JPanel pnlFechas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFechas.add(new JLabel("Desde:"));
        dccDesde = new SelectorFecha(LocalDate.of(2024, 1, 1));
        pnlFechas.add(dccDesde);
        pnlFechas.add(new JLabel("Hasta:"));
        dccHasta = new SelectorFecha(LocalDate.of(2024, 1, 10));
        pnlFechas.add(dccHasta);
        btnGraficar = new JButton("Graficar promedios");
        pnlFechas.add(btnGraficar);
        pnlOpciones.add(pnlFechas);

        JPanel pnlFechaUnica = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFechaUnica.add(new JLabel("Fecha:"));
        dccFecha = new SelectorFecha(LocalDate.of(2024, 1, 1));
        pnlFechaUnica.add(dccFecha);
        btnCalcularEstadisticas = new JButton("Ciudad más y menos calurosa");
        pnlFechaUnica.add(btnCalcularEstadisticas);
        pnlOpciones.add(pnlFechaUnica);

        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTotal = new JLabel("Archivo: Temperaturas.csv");
        pnlInfo.add(lblTotal);
        lblFechasDisponibles = new JLabel("    Datos disponibles: cargando...");
        pnlInfo.add(lblFechasDisponibles);
        pnlOpciones.add(pnlInfo);

        pnlGrafica = new JPanel(new BorderLayout());
        pnlEstadisticas = new JPanel(new BorderLayout(10, 10));
        tpTemperaturas = new JTabbedPane();
        tpTemperaturas.addTab("Gráfica de barras", pnlGrafica);
        tpTemperaturas.addTab("Estadísticas", pnlEstadisticas);

        getContentPane().add(pnlOpciones, BorderLayout.NORTH);
        getContentPane().add(tpTemperaturas, BorderLayout.CENTER);
    }

    // Obtener las fechas elegidas en los calendarios.
    public LocalDate getFechaDesde() {
        return dccDesde.getFecha();
    }

    public LocalDate getFechaHasta() {
        return dccHasta.getFecha();
    }

    public LocalDate getFecha() {
        return dccFecha.getFecha();
    }

    public void setGraficarClick(ActionListener evento) {
        btnGraficar.addActionListener(evento);
    }

    public void setCalcularEstadisticasClick(ActionListener evento) {
        btnCalcularEstadisticas.addActionListener(evento);
    }

    // Avisar al controlador cuando se cambia la fecha de estadísticas.
    public void setFechaCambiada(Runnable accion) {
        dccFecha.setAlCambiarFecha(accion);
    }

    // Activar solo las fechas que vienen del archivo CSV.
    public void setFechasDisponibles(List<LocalDate> fechas) {
        dccDesde.setFechasDisponibles(fechas);
        dccHasta.setFechasDisponibles(fechas);
        dccFecha.setFechasDisponibles(fechas);

        if (fechas.isEmpty()) {
            lblFechasDisponibles.setText("    No hay fechas disponibles");
        } else {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            lblFechasDisponibles.setText("    Datos disponibles: "
                    + fechas.get(0).format(formato) + " al "
                    + fechas.get(fechas.size() - 1).format(formato));
        }
    }

    public void setTotalRegistros(int total) {
        lblTotal.setText("Archivo: Temperaturas.csv (" + total + " registros)");
    }

    public void activarPestana(int posicion) {
        tpTemperaturas.setSelectedIndex(posicion);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    // Barras verticales creadas con componentes de Swing.
    public void mostrarGrafica(Map<String, Double> promedios, List<String> ciudades,
                               LocalDate desde, LocalDate hasta) {
        pnlGrafica.removeAll();

        JPanel barras = new JPanel(new GridLayout(1, ciudades.size(), 20, 0));
        barras.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        for (String ciudad : ciudades) {
            double promedio = promedios.get(ciudad);

            JPanel columna = new JPanel(new BorderLayout(0, 8));
            JLabel lblValor = new JLabel(String.format("%.2f °C", promedio), SwingConstants.CENTER);
            JLabel lblCiudad = new JLabel(ciudad, SwingConstants.CENTER);

            JProgressBar barra = new JProgressBar(SwingConstants.VERTICAL, 0, 350);
            barra.setValue((int) Math.round(promedio * 10));
            barra.setForeground(new Color(65, 135, 190));
            barra.setBackground(new Color(235, 240, 245));
            barra.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            columna.add(lblValor, BorderLayout.NORTH);
            columna.add(barra, BorderLayout.CENTER);
            columna.add(lblCiudad, BorderLayout.SOUTH);
            barras.add(columna);
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String titulo = "Promedios del " + desde.format(formato) + " al " + hasta.format(formato);
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel lblEscala = new JLabel("Escala de las barras: 0 a 35 °C", SwingConstants.CENTER);

        pnlGrafica.add(lblTitulo, BorderLayout.NORTH);
        pnlGrafica.add(barras, BorderLayout.CENTER);
        pnlGrafica.add(lblEscala, BorderLayout.SOUTH);
        pnlGrafica.revalidate();
        pnlGrafica.repaint();
    }

    public void mostrarEstadisticas(Temperatura mayor, Temperatura menor, LocalDate fecha) {
        pnlEstadisticas.removeAll();
        String dia = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        JLabel lblFecha = new JLabel("Resultados del " + dia, SwingConstants.CENTER);
        lblFecha.setFont(new Font("SansSerif", Font.BOLD, 17));

        JPanel resultados = new JPanel(new GridLayout(2, 1, 10, 10));
        JLabel lblMayor = new JLabel("Más calurosa: " + mayor.getCiudad() + " (" + mayor.getTemperatura() + " °C)", SwingConstants.CENTER);
        JLabel lblMenor = new JLabel("Menos calurosa: " + menor.getCiudad() + " (" + menor.getTemperatura() + " °C)", SwingConstants.CENTER);
        lblMayor.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblMenor.setFont(new Font("SansSerif", Font.BOLD, 20));
        resultados.add(lblMayor);
        resultados.add(lblMenor);

        pnlEstadisticas.add(lblFecha, BorderLayout.NORTH);
        pnlEstadisticas.add(resultados, BorderLayout.CENTER);
        pnlEstadisticas.revalidate();
        pnlEstadisticas.repaint();
    }
}
