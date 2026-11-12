package cl.duoc.viviendas.DAO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ViviendaDAO {
    private static final Path ARCHIVO_CASAS = Path.of("casas.json");
    private static final Path ARCHIVO_DEPARTAMENTOS = Path.of("departamentos.json");
    private final Gson gson;

    public ViviendaDAO() {
        // Gson configurado con formato legible (opcional pero ayuda a depurar)
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    // --- GUARDAR Y CARGAR CASAS ---
    public void guardarCasas(List<Casa> casas) {
        guardarJson(casas, ARCHIVO_CASAS, "casas");
    }

    public List<Casa> cargarCasas() {
        if (Files.notExists(ARCHIVO_CASAS)) {
            return new ArrayList<>();
        }

        try (Reader reader = Files.newBufferedReader(ARCHIVO_CASAS, StandardCharsets.UTF_8)) {
            Type tipoLista = new TypeToken<ArrayList<Casa>>() {}.getType();
            List<Casa> cargadas = gson.fromJson(reader, tipoLista);
            return cargadas != null ? cargadas : new ArrayList<>();
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(
                    "Error al leer casas.json: " + detalleError(e), e);
        }
    }

    // --- GUARDAR Y CARGAR DEPARTAMENTOS ---
    public void guardarDepartamentos(List<Departamento> departamentos) {
        guardarJson(departamentos, ARCHIVO_DEPARTAMENTOS, "departamentos");
    }

    public List<Departamento> cargarDepartamentos() {
        if (Files.notExists(ARCHIVO_DEPARTAMENTOS)) {
            return new ArrayList<>();
        }

        try (Reader reader = Files.newBufferedReader(ARCHIVO_DEPARTAMENTOS, StandardCharsets.UTF_8)) {
            Type tipoLista = new TypeToken<ArrayList<Departamento>>() {}.getType();
            List<Departamento> cargadas = gson.fromJson(reader, tipoLista);
            return cargadas != null ? cargadas : new ArrayList<>();
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(
                    "Error al leer departamentos.json: " + detalleError(e), e);
        }
    }

    /**
     * Escribe primero en un archivo temporal y solo reemplaza el JSON cuando
     * toda la serialización terminó correctamente.
     */
    private void guardarJson(Object datos, Path archivo, String descripcion) {
        Path destino = archivo.toAbsolutePath();
        Path temporal = null;

        try {
            temporal = Files.createTempFile(destino.getParent(),
                    destino.getFileName().toString(), ".tmp");

            try (Writer writer = Files.newBufferedWriter(temporal, StandardCharsets.UTF_8)) {
                gson.toJson(datos, writer);
            }

            try {
                Files.move(temporal, destino,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | JsonParseException e) {
            if (temporal != null) {
                try {
                    Files.deleteIfExists(temporal);
                } catch (IOException ignored) {
                    // Se conserva el error original, que es el relevante para el usuario.
                }
            }
            throw new PersistenciaException(
                    "Error al guardar " + descripcion + ": " + detalleError(e), e);
        }
    }

    private String detalleError(Exception e) {
        return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
    }
}
