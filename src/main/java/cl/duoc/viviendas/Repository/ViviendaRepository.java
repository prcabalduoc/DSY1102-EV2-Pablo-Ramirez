package cl.duoc.viviendas.Repository;

import cl.duoc.viviendas.DAO.ViviendaDAO;
import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;

import java.util.ArrayList;
import java.util.List;

public class ViviendaRepository {
    private final ViviendaDAO dao;
    private final List<Vivienda> viviendas;

    public ViviendaRepository() {
        this.dao = new ViviendaDAO();
        this.viviendas = new ArrayList<>();
    }

    /**
     * Entrega una copia de solo lectura del estado del repositorio.
     * La conversión a colecciones propias de la interfaz gráfica corresponde
     * al controlador, no a la capa de persistencia.
     */
    public List<Vivienda> obtenerViviendas() {
        return List.copyOf(viviendas);
    }

    public void cargarDatos() {
        List<Casa> casas = dao.cargarCasas();
        List<Departamento> depto = dao.cargarDepartamentos();

        // La lista visible solo cambia si ambos archivos se leyeron correctamente.
        viviendas.clear();
        viviendas.addAll(casas);
        viviendas.addAll(depto);
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

        viviendas.add(vivienda);
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

        viviendas.remove(vivienda);
    }

    private List<Casa> obtenerCasas() {
        List<Casa> casas = new ArrayList<>();
        for (Vivienda v : viviendas) {
            if (v instanceof Casa casa) {
                casas.add(casa);
            }
        }
        return casas;
    }

    private List<Departamento> obtenerDepartamentos() {
        List<Departamento> departamentos = new ArrayList<>();
        for (Vivienda v : viviendas) {
            if (v instanceof Departamento departamento) {
                departamentos.add(departamento);
            }
        }
        return departamentos;
    }
}
