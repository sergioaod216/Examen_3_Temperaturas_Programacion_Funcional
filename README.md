# Examen Temperaturas — revisión final

Proyecto Java de nivel básico, siguiendo la estructura del ejemplo del profesor: App, modelo, servicio, controlador y vista.

## Cómo ejecutarlo en VS Code

1. Abre la carpeta **ExamenTemperaturas_Revisado** mediante File > Open Folder.
2. Abre `src/App.java` y presiona **Run** sobre el método `main`.
3. El archivo `src/datos/Temperaturas.csv` contiene 50 registros, de cinco ciudades, desde 01/01/2024 hasta 10/01/2024.

No requiere `.bat` ni librerías externas adicionales. Necesita JDK y la extensión Java para VS Code.

## Funciones del examen

- **Graficar promedios**: elige fechas desde/hasta y muestra el promedio de cada ciudad con registros en ese rango.
- **Ciudad más y menos calurosa**: elige un día y muestra ambas ciudades con su temperatura.
- Los tres calendarios solo ofrecen fechas existentes en el CSV.

## Cambios tras revisión

- Los resultados de estadísticas se actualizan automáticamente al cambiar la fecha y también al presionar el botón.
- La pestaña de estadísticas indica la fecha consultada.
- El título de la gráfica indica el rango de fechas al que pertenecen los promedios.
- Se conserva programación funcional en el servicio: `stream()`, `filter()`, `map()`, `distinct()`, `sorted()`, `collect()` y `mapToDouble().average()`.

## Cómo verificar

- Rango del 01/01/2024 al 10/01/2024: Barranquilla 28.11, Bogotá 14.35, Cali 25.90, Cartagena 28.88, Medellín 22.05 °C.
- Fecha 01/01/2024: más calurosa Cartagena 28.5 °C; menos calurosa Bogotá 14.5 °C.
- Fecha 10/01/2024: más calurosa Cartagena 29.1 °C; menos calurosa Bogotá 14.9 °C.

## Nota para la presentación

Los promedios se calculan mediante operaciones funcionales en `TemperaturasServicio`. Se usa Swing sin bibliotecas externas para construir un calendario y las barras, en lugar de `DateChooserCombo` y `JFreeChart` del proyecto de monedas del profesor. El enunciado no exige usar esas bibliotecas en particular.
