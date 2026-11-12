package cl.duoc.viviendas.controller;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import cl.duoc.viviendas.DAO.PersistenciaException;
import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.Repository.ViviendaRepository;

public class MainController {

    // --- Componentes del Formulario ---
    @FXML private ComboBox<String> cbTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtSuperficie;
    @FXML private TextField txtHabitaciones;
    
    // E específicos de Departamento
    @FXML private TextField txtPiso;
    @FXML private CheckBox chkGastoComun;
    @FXML private CheckBox chkEstacionamiento;
    
    // E específicos de Casa
    @FXML private CheckBox chkPatio;

    // --- Tabla y Columnas ---
    @FXML private TableView<Vivienda> tablaViviendas;
    @FXML private TableColumn<Vivienda, String> colTipo;
    @FXML private TableColumn<Vivienda, String> colCodigo;
    @FXML private TableColumn<Vivienda, Double> colSuperficie;
    @FXML private TableColumn<Vivienda, Integer> colHabitaciones;
    @FXML private TableColumn<Vivienda, Double> colCosto;

    // --- Área de Mensajes ---
    @FXML private Label lblMensaje;

    // Repositorio para persistencia con DAO y Gson
    private ViviendaRepository repository;

    @FXML
    public void initialize() {
        repository = new ViviendaRepository();

        // 1. Configurar opciones del ComboBox de Tipo
        cbTipo.getItems().addAll("Departamento", "Casa");
        cbTipo.setOnAction(event -> actualizarCamposSegunTipo());

        // 2. Configurar las columnas de la tabla
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colSuperficie.setCellValueFactory(new PropertyValueFactory<>("superficie"));
        colHabitaciones.setCellValueFactory(new PropertyValueFactory<>("habitaciones"));
        colCosto.setCellValueFactory(cellData -> {
            Vivienda v = cellData.getValue();
            double costo = v.calcularCostoArriendo();
            return new javafx.beans.property.SimpleDoubleProperty(costo).asObject();
        });
        // Vincular la tabla antes de cargar, para que también pueda iniciar
        // vacía y mostrar un mensaje si alguno de los archivos tiene errores.
        tablaViviendas.setItems(FXCollections.observableArrayList());
        try {
            repository.cargarDatos();
            actualizarTabla();
        } catch (PersistenciaException e) {
            mostrarMensaje(e.getMessage(), true);
        }

        // Estado inicial de los campos específicos
        actualizarCamposSegunTipo();
    }

    private void actualizarCamposSegunTipo() {
        String seleccion = cbTipo.getValue();
        boolean esDepto = "Departamento".equals(seleccion);
        boolean esCasa = "Casa".equals(seleccion);

        // Habilitar / deshabilitar según el tipo elegido
        txtPiso.setDisable(!esDepto);
        chkGastoComun.setDisable(!esDepto);
        chkEstacionamiento.setDisable(!esDepto);

        chkPatio.setDisable(!esCasa);
    }

    @FXML
    private void handleRegistrar() {
        String tipo = cbTipo.getValue();
        if (tipo == null || tipo.isEmpty()) {
            mostrarMensaje("Debe seleccionar un tipo de vivienda.", true);
            return;
        }

        String codigo = txtCodigo.getText();
        if (codigo == null || codigo.trim().isEmpty()) {
            mostrarMensaje("Código: no puede ser nulo ni vacío.", true);
            return;
        }

        double superficie;
        try {
            superficie = Double.parseDouble(txtSuperficie.getText());
            if (!Double.isFinite(superficie) || superficie < 20 || superficie > 500) {
                mostrarMensaje("Superficie: debe ser un número entre 20 y 500.", true);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Superficie: ingrese un valor numérico válido.", true);
            return;
        }

        int habitaciones;
        try {
            habitaciones = Integer.parseInt(txtHabitaciones.getText());
            if (habitaciones <= 0) {
                mostrarMensaje("Habitaciones: debe ser mayor que cero.", true);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Habitaciones: ingrese un número entero válido.", true);
            return;
        }

        try {
            if ("Departamento".equals(tipo)) {
                int piso;
                try {
                    piso = Integer.parseInt(txtPiso.getText());
                    if (piso <= 0) {
                        mostrarMensaje("Piso: debe ser mayor que cero.", true);
                        return;
                    }
                } catch (NumberFormatException e) {
                    mostrarMensaje("Piso: ingrese un número entero válido.", true);
                    return;
                }

                boolean gastoComunAlDia = chkGastoComun.isSelected();
                boolean estacionamiento = chkEstacionamiento.isSelected();

                Departamento depto = new Departamento(codigo, superficie, habitaciones, piso, gastoComunAlDia);
                if (estacionamiento) {
                    depto.asignarEstacionamiento();
                }

                repository.agregarVivienda(depto);
                actualizarTabla();
                mostrarMensaje(codigo + " (Departamento) registrada correctamente.", false);

            } else {
                boolean tienePatio = chkPatio.isSelected();
                Casa casa = new Casa(codigo, superficie, habitaciones, tienePatio);

                repository.agregarVivienda(casa);
                actualizarTabla();
                mostrarMensaje(codigo + " (Casa) registrada correctamente.", false);
            }

            limpiarFormulario();

        } catch (PersistenciaException e) {
            mostrarMensaje(e.getMessage(), true);
        } catch (IllegalArgumentException e) {
            // Atrapa las validaciones internas que ya traían las clases del modelo base
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void handleEliminar() {
        Vivienda seleccionada = tablaViviendas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Debe seleccionar una vivienda de la tabla para eliminarla.", true);
            return;
        }

        try {
            repository.eliminarVivienda(seleccionada);
            actualizarTabla();
            mostrarMensaje("Vivienda " + seleccionada.getCodigo() + " eliminada correctamente.", false);
        } catch (PersistenciaException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    private void mostrarMensaje(String texto, boolean esError) {
        lblMensaje.setText(texto);
        if (esError) {
            lblMensaje.setTextFill(Color.RED);
        } else {
            lblMensaje.setTextFill(Color.GREEN);
        }
    }

    /** Adapta el modelo Java del repositorio a la colección observable de JavaFX. */
    private void actualizarTabla() {
        tablaViviendas.getItems().setAll(repository.obtenerViviendas());
    }

    private void limpiarFormulario() {
        cbTipo.setValue(null);
        txtCodigo.clear();
        txtSuperficie.clear();
        txtHabitaciones.clear();
        txtPiso.clear();
        chkGastoComun.setSelected(false);
        chkEstacionamiento.setSelected(false);
        chkPatio.setSelected(false);
        actualizarCamposSegunTipo();
    }
}
