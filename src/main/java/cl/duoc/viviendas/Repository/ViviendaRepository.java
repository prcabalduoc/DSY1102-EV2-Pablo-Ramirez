package cl.duoc.viviendas.Repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import cl.duoc.viviendas.DAO.ViviendaDAO;
import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;

import java.util.List;

public class ViviendaRepository {
    private final ViviendaDAO dao;
    private final ObservableList<Vivienda> listaViviendas;

    public ViviendaRepository() {
        this.dao = new ViviendaDAO();
        this.listaViviendas = FXCollections.observableArrayList();
    }

    public ObservableList<Vivienda> getListaViviendas() {
        return listaViviendas;
    }

    public void cargarDatos() {
        List<Casa> casas = dao.cargarCasas();
        List<Departamento> depto = dao.cargarDepartamentos();

        // La lista visible solo cambia si ambos archivos se leyeron correctamente.
        listaViviendas.clear();
        listaViviendas.addAll(casas);
        listaViviendas.addAll(depto);
    }

    public void agregarVivienda(Vivienda vivienda) {
        // Primero se guarda y luego se actualiza la tabla. Así, si falla la
        // escritura, ambas representaciones conservan su estado anterior.
        if (vivienda instanceof Casa casa) {
            List<Casa> casas = obtenerCasas();
            casas.add(casa);
            dao.guardarCasas(casas);
        } else if (vivienda instanceof Departamento departamento) {
            List<Departamento> departamentos = obtenerDepartamentos();
            departamentos.add(departamento);
            dao.guardarDepartamentos(departamentos);
        } else {
            throw new IllegalArgumentException("Tipo de vivienda no soportado.");
        }

        listaViviendas.add(vivienda);
    }

    public void eliminarVivienda(Vivienda vivienda) {
        if (vivienda instanceof Casa casa) {
            List<Casa> casas = obtenerCasas();
            casas.remove(casa);
            dao.guardarCasas(casas);
        } else if (vivienda instanceof Departamento departamento) {
            List<Departamento> departamentos = obtenerDepartamentos();
            departamentos.remove(departamento);
            dao.guardarDepartamentos(departamentos);
        } else {
            throw new IllegalArgumentException("Tipo de vivienda no soportado.");
        }

        listaViviendas.remove(vivienda);
    }

    private List<Casa> obtenerCasas() {
        List<Casa> casas = new java.util.ArrayList<>();
        for (Vivienda v : listaViviendas) {
            if (v instanceof Casa casa) {
                casas.add(casa);
            }
        }
        return casas;
    }

    private List<Departamento> obtenerDepartamentos() {
        List<Departamento> departamentos = new java.util.ArrayList<>();
        for (Vivienda v : listaViviendas) {
            if (v instanceof Departamento departamento) {
                departamentos.add(departamento);
            }
        }
        return departamentos;
    }
}
