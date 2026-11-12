# Administradora de Viviendas

Aplicación de escritorio desarrollada con JavaFX para registrar, visualizar y eliminar viviendas administradas. El sistema trabaja con casas y departamentos, calcula automáticamente sus costos de arriendo y conserva la información entre ejecuciones mediante archivos JSON.

El proyecto corresponde a la Evaluación Parcial 2 de la asignatura **DSY1102 Programación Orientada a Objetos**.

## Funcionalidades

- Registro de casas y departamentos desde una única ventana.
- Visualización de las viviendas en una tabla con tipo, código, superficie, habitaciones y costo de arriendo.
- Habilitación automática de los campos correspondientes al tipo de vivienda seleccionado.
- Eliminación de la vivienda seleccionada.
- Mensajes de confirmación y error dentro de la interfaz.
- Carga automática de los datos al iniciar la aplicación.
- Guardado automático después de registrar o eliminar una vivienda.
- Persistencia separada en `casas.json` y `departamentos.json`.
- Manejo de errores de lectura y escritura sin detener la aplicación.

## Reglas del modelo

### Departamento

- Costo base de arriendo: **$180.000**.
- Si el gasto común no está al día, se aplica un recargo del **15 %**.
- Puede tener un estacionamiento asignado.

### Casa

- Costo base de arriendo: **$250.000**.
- Si cuenta con patio, se aplica un recargo del **10 %**.

## Validaciones

Antes de registrar una vivienda, la aplicación comprueba que:

- Se haya seleccionado un tipo de vivienda.
- El código no esté vacío ni contenga solamente espacios.
- La superficie sea un número finito entre 20 y 500 m².
- La cantidad de habitaciones sea un número entero mayor que cero.
- El piso de un departamento sea un número entero mayor que cero.

Cuando una validación falla, no se registra la vivienda y los valores del formulario se mantienen para permitir su corrección.

## Tecnologías utilizadas

- Java 21
- JavaFX 21.0.6
- FXML
- Maven
- Gson 2.10.1
- JSON

## Arquitectura

La aplicación separa sus responsabilidades siguiendo MVC y los patrones DAO/Repository:

```text
src/main/java/
├── module-info.java
└── cl/duoc/viviendas/
    ├── Launcher.java                    Punto de entrada
    ├── App.java                         Inicialización de JavaFX
    ├── controller/
    │   └── MainController.java          Controlador de la interfaz
    ├── model/
    │   ├── Vivienda.java                Clase abstracta base
    │   ├── Casa.java
    │   ├── Departamento.java
    │   └── ConEstacionamiento.java
    ├── DAO/
    │   ├── ViviendaDAO.java             Lectura y escritura de JSON
    │   └── PersistenciaException.java
    └── Repository/
        └── ViviendaRepository.java      Sincronización entre vista y persistencia

src/main/resources/
└── cl/duoc/viviendas/view/
    └── main-view.fxml                   Vista principal
```

## Persistencia

Los datos se almacenan en la raíz del proyecto:

- `casas.json`: casas registradas.
- `departamentos.json`: departamentos registrados.

Si los archivos no existen, la aplicación comienza con una tabla vacía. Para proteger la información, cada guardado se realiza primero en un archivo temporal y el JSON definitivo solo se reemplaza cuando la escritura termina correctamente.

## Requisitos

- JDK 21.
- Apache Maven 3.6.3 o posterior.

Verifica las versiones instaladas con:

```bash
java -version
mvn -version
```

Ambos comandos deben indicar que Maven está utilizando Java 21.

## Compilación y ejecución

Desde la carpeta raíz del proyecto, compila con:

```bash
mvn clean package
```

Ejecuta la aplicación con:

```bash
mvn javafx:run
```

También puede ejecutarse la clase `cl.duoc.viviendas.Launcher` desde un IDE configurado con JDK 21 y soporte para proyectos Maven.

## Datos de prueba

| Tipo | Código | Superficie | Habitaciones | Datos específicos | Costo esperado |
|---|---|---:|---:|---|---:|
| Departamento | `PROP-D01` | 65.0 m² | 3 | Piso 8, gasto común pendiente, con estacionamiento | $207.000 |
| Departamento | `PROP-D02` | 48.0 m² | 2 | Piso 3, gasto común al día, sin estacionamiento | $180.000 |
| Casa | `PROP-C01` | 120.0 m² | 4 | Con patio | $275.000 |
| Casa | `PROP-C02` | 90.0 m² | 3 | Sin patio | $250.000 |

Para verificar la persistencia:

1. Registra las cuatro viviendas anteriores.
2. Cierra y vuelve a abrir la aplicación; las cuatro deben seguir en la tabla.
3. Elimina `PROP-D01`.
4. Cierra y abre nuevamente la aplicación; `PROP-D01` no debe reaparecer.

## Documentación original

El archivo [`LEEME.txt`](LEEME.txt) conserva las instrucciones entregadas con el proyecto base.
