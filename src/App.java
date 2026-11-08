import controladores.TemperaturasControlador;
import vistas.TemperaturasVista;

public class App {
    public static void main(String[] args) {
        var vista = new TemperaturasVista();
        new TemperaturasControlador(vista);
        vista.setVisible(true);
    }
}
