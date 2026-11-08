package vistas;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Calendario para elegir solo las fechas que existen en el CSV.
public class SelectorFecha extends JPanel {

    private LocalDate fecha;
    private JButton btnFecha;
    private List<LocalDate> fechasDisponibles = new ArrayList<>();
    private Runnable alCambiarFecha;
    private final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public SelectorFecha(LocalDate fechaInicial) {
        fecha = fechaInicial;
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        btnFecha = new JButton(fecha.format(formato) + "  ▼");
        add(btnFecha);
        btnFecha.addActionListener(e -> abrirCalendario());
    }

    // Recibimos las fechas que encontramos al leer el CSV.
    public void setFechasDisponibles(List<LocalDate> fechas) {
        fechasDisponibles = new ArrayList<>(fechas);
        btnFecha.setEnabled(!fechasDisponibles.isEmpty());

        if (!fechasDisponibles.isEmpty() && !fechasDisponibles.contains(fecha)) {
            fecha = fechasDisponibles.get(0);
        }
        btnFecha.setText(fecha.format(formato) + "  ▼");
    }

    private void abrirCalendario() {
        if (fechasDisponibles.isEmpty()) {
            return;
        }

        Window padre = SwingUtilities.getWindowAncestor(this);
        JDialog ventana = new JDialog(padre, "Elegir fecha", Dialog.ModalityType.APPLICATION_MODAL);
        ventana.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contenido = new JPanel(new BorderLayout(5, 5));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] nombresMeses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        JComboBox<Integer> cmbAnio = new JComboBox<>();
        JComboBox<String> cmbMes = new JComboBox<>();
        List<Integer> mesesDisponibles = new ArrayList<>();

        // Mostrar solo los años que aparecen en el archivo.
        for (LocalDate dia : fechasDisponibles) {
            if (noExisteAnio(cmbAnio, dia.getYear())) {
                cmbAnio.addItem(dia.getYear());
            }
        }
        cmbAnio.setSelectedItem(fecha.getYear());
        cmbAnio.setMaximumRowCount(8);

        // Mostrar solo los meses con información del año elegido.
        llenarMeses(cmbMes, cmbAnio, mesesDisponibles, nombresMeses);

        JPanel opciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        opciones.add(cmbMes);
        opciones.add(cmbAnio);
        contenido.add(opciones, BorderLayout.NORTH);

        JPanel pnlDias = new JPanel(new GridLayout(7, 7, 2, 2));
        contenido.add(pnlDias, BorderLayout.CENTER);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> ventana.dispose());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pie.add(btnCancelar);
        contenido.add(pie, BorderLayout.SOUTH);

        cmbAnio.addActionListener(e -> {
            llenarMeses(cmbMes, cmbAnio, mesesDisponibles, nombresMeses);
            actualizarDias(pnlDias, cmbMes, cmbAnio, mesesDisponibles, ventana);
        });
        cmbMes.addActionListener(e ->
                actualizarDias(pnlDias, cmbMes, cmbAnio, mesesDisponibles, ventana));
        actualizarDias(pnlDias, cmbMes, cmbAnio, mesesDisponibles, ventana);

        ventana.setContentPane(contenido);
        ventana.setSize(365, 330);
        ventana.setResizable(false);
        ventana.setLocationRelativeTo(btnFecha);
        ventana.setVisible(true);
    }

    private boolean noExisteAnio(JComboBox<Integer> cmbAnio, int anio) {
        for (int i = 0; i < cmbAnio.getItemCount(); i++) {
            if (cmbAnio.getItemAt(i) == anio) {
                return false;
            }
        }
        return true;
    }

    private void llenarMeses(JComboBox<String> cmbMes, JComboBox<Integer> cmbAnio,
                             List<Integer> mesesDisponibles, String[] nombresMeses) {
        cmbMes.removeAllItems();
        mesesDisponibles.clear();
        if (cmbAnio.getSelectedItem() == null) {
            return;
        }

        int anio = (Integer) cmbAnio.getSelectedItem();
        for (int mes = 1; mes <= 12; mes++) {
            boolean hayDatos = false;
            for (LocalDate dia : fechasDisponibles) {
                if (dia.getYear() == anio && dia.getMonthValue() == mes) {
                    hayDatos = true;
                }
            }
            if (hayDatos) {
                mesesDisponibles.add(mes);
                cmbMes.addItem(nombresMeses[mes - 1]);
            }
        }

        if (anio == fecha.getYear() && mesesDisponibles.contains(fecha.getMonthValue())) {
            cmbMes.setSelectedIndex(mesesDisponibles.indexOf(fecha.getMonthValue()));
        }
    }

    private void actualizarDias(JPanel pnlDias, JComboBox<String> cmbMes,
                                JComboBox<Integer> cmbAnio, List<Integer> mesesDisponibles,
                                JDialog ventana) {
        pnlDias.removeAll();

        String[] nombres = {"Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do"};
        for (String nombre : nombres) {
            pnlDias.add(new JLabel(nombre, SwingConstants.CENTER));
        }

        if (cmbAnio.getSelectedItem() == null || cmbMes.getSelectedIndex() < 0
                || cmbMes.getSelectedIndex() >= mesesDisponibles.size()) {
            pnlDias.revalidate();
            pnlDias.repaint();
            return;
        }

        int anio = (Integer) cmbAnio.getSelectedItem();
        int mes = mesesDisponibles.get(cmbMes.getSelectedIndex());
        LocalDate primerDia = LocalDate.of(anio, mes, 1);

        for (int i = 1; i < primerDia.getDayOfWeek().getValue(); i++) {
            pnlDias.add(new JLabel(""));
        }

        for (int dia = 1; dia <= primerDia.lengthOfMonth(); dia++) {
            LocalDate diaCalendario = LocalDate.of(anio, mes, dia);
            JButton boton = new JButton(String.valueOf(dia));
            boton.setMargin(new Insets(3, 3, 3, 3));

            // Los días sin datos aparecen grises y no se pueden pulsar.
            boton.setEnabled(fechasDisponibles.contains(diaCalendario));

            if (fecha.equals(diaCalendario)) {
                boton.setBackground(new Color(180, 215, 245));
                boton.setOpaque(true);
            }

            boton.addActionListener(e -> {
                fecha = diaCalendario;
                btnFecha.setText(fecha.format(formato) + "  ▼");
                ventana.dispose();
                if (alCambiarFecha != null) {
                    alCambiarFecha.run();
                }
            });
            pnlDias.add(boton);
        }

        while (pnlDias.getComponentCount() < 49) {
            pnlDias.add(new JLabel(""));
        }
        pnlDias.revalidate();
        pnlDias.repaint();
    }

    // Se ejecuta cuando el usuario elige un día del calendario.
    public void setAlCambiarFecha(Runnable accion) {
        alCambiarFecha = accion;
    }

    public LocalDate getFecha() {
        return fecha;
    }
}
