# PLAN DE DESARROLLO DE SOFTWARE: SISTEMA MODULAR DE GESTIÓN DE SUSCRIPCIONES Y COBROS ("STREAMFLOW CORE")

---

### FASE 1: ESPECIFICACIÓN DEL PROBLEMA Y REGLAS DE NEGOCIO

#### 1.1. Contexto del Problema

Una empresa proveedora de servicios digitales requiere un núcleo transaccional en Java para administrar suscripciones mensuales de servicios en la nube (Streaming y Almacenamiento Digital). El sistema debe gestionar clientes, calcular cargos periódicos considerando descuentos o consumos extra, aplicar cancelaciones y procesar cobros directos. Para garantizar un estándar de calidad profesional, el desarrollo debe estructurarse mediante pruebas unitarias automatizadas y desacoplamiento orientado a objetos.

#### 1.2. Reglas del Dominio

1. **Identidad del Cliente:** Todo cliente posee un identificador alfanumérico, un nombre completo y un saldo disponible para pagos recurrentes.


2. **Ciclo de Cobro y Facturación:**
* Las suscripciones poseen una tarifa mensual base.


* Los servicios de streaming aplican una tarifa fija mensual sujeta a recargos si el usuario añade perfiles adicionales.


* Los planes familiares de streaming heredan la lógica base pero bonifican automáticamente un perfil adicional sin costo.


* Los servicios de almacenamiento cobran una tarifa según los gigabytes ($GB$) consumidos por encima del límite gratuito acordado.




3. **Restricciones Financieras e Invariantes de Estado:**
* No se permite registrar clientes con nombres vacíos ni montos de saldo negativos.


* Al procesar un pago o débito, si el saldo del cliente es menor que el monto facturado, la operación debe ser rechazada categóricamente.


* Una suscripción dada de baja no puede ser cobrada nuevamente ni modificada en su estado.





---

### FASE 2: ESPECIFICACIÓN ARQUITECTÓNICA DEL MODELO (ESTRUCTURA Y FIRMAS)

```
Estructura de paquetes requerida:
  edu.upv.streamflow.modelo
  edu.upv.streamflow.excepciones
  edu.upv.streamflow.interfaces

```

#### 2.1. Paquete de Excepciones (`edu.upv.streamflow.excepciones`)

* **Excepción Estándar de la Biblioteca Java (No marcada / Unchecked):**
* Se utilizará `IllegalArgumentException` ante argumentos de entrada nulos, vacíos o fuera de rango físico admisible.




* **`TransaccionRechazadaException` (Excepción Propia / Marcada - *Checked*):**
* **Herencia:** Hereda directamente de `java.lang.Exception`.


* **Propósito:** Notificar anomalías de negocio que el flujo invocador está obligado a capturar o propagar (por ejemplo: saldo insuficiente para renovar).


* **Constructores requeridos:** Constructor que reciba mensaje descriptivo (`String`) y constructor que reciba mensaje y causa (`String`, `Throwable`).




* **`SuscripcionInactivaException` (Excepción Propia / No Marcada - *Unchecked*):**
* **Herencia:** Hereda directamente de `java.lang.RuntimeException`.


* **Propósito:** Señalar violaciones graves en el ciclo de vida del objeto (intentar facturar o reactivar un servicio previamente cancelado).


* **Constructores requeridos:** Constructor que reciba mensaje descriptivo (`String`).





#### 2.2. Paquete de Interfaces (`edu.upv.streamflow.interfaces`)

* **Interfaz `Facturable`:**
* Define la abstracción del comportamiento financiero.


* Firma: `double calcularMontoFacturable()`.


* Firma: `void procesarCobro(Cliente cliente) throws TransaccionRechazadaException`.




* **Interfaz `Cancelable`:**
* Firma: `void cancelarServicio()`.


* Firma: `boolean estaActivo()`.





#### 2.3. Paquete del Modelo de Dominio (`edu.upv.streamflow.modelo`)

##### A. Clase Concreta: `Cliente`

* **Encapsulamiento de Atributos:**
* `private String idCliente`

* `private String nombre`

* `private double saldoDisponible`



* **Constructores y Métodos:**
* `public Cliente(String idCliente, String nombre, double saldoInicial)`

* Métodos de acceso (*Getters*) para todos los atributos.


* `public void abonarSaldo(double monto)`

* `public void debitarSaldo(double monto) throws TransaccionRechazadaException`




##### B. Clase Abstracta: `Suscripcion` (Herencia Abstracta)

* **Implementación:** Implementa `Facturable` y `Cancelable`.


* **Encapsulamiento de Atributos:**
* `protected String codigoContrato`

* `protected double tarifaBase`

* `protected boolean activo`



* **Constructores y Métodos:**
* `public Suscripcion(String codigoContrato, double tarifaBase)`

* Getters para atributos de solo lectura.


* Implementación concreta del método `cancelarServicio()` y `estaActivo()`.


* Implementación base de `procesarCobro(Cliente cliente)` delegando el cálculo al método abstracto.


* Método Abstracto: `public abstract double calcularMontoFacturable()`.





##### C. Clase Concreta: `SuscripcionStreaming` (Especialización de Clase Abstracta)

* **Herencia:** Hereda de `Suscripcion`.


* **Encapsulamiento de Atributos:**
* `protected int perfilesAdicionales`

* `protected double costoPorPerfilAdicional`



* **Constructores y Métodos:**
* `public SuscripcionStreaming(String codigoContrato, double tarifaBase, int perfilesAdicionales, double costoPorPerfilAdicional)`

* `public void agregarPerfil()`

* Sobrescritura polimórfica: `public double calcularMontoFacturable()`.





##### D. Clase Concreta: `SuscripcionStreamingFamiliar` (Herencia Concreta)

* **Herencia:** Hereda directamente de la clase concreta `SuscripcionStreaming`.


* **Regla de Especialización:** Bonifica 1 perfil adicional (el primer perfil adicional no genera costo).


* **Constructores y Métodos:**
* `public SuscripcionStreamingFamiliar(String codigoContrato, double tarifaBase, int perfilesAdicionales, double costoPorPerfilAdicional)`

* Sobrescritura polimórfica: `public double calcularMontoFacturable()`.





##### E. Clase Concreta: `SuscripcionAlmacenamiento` (Especialización de Clase Abstracta)

* **Herencia:** Hereda de `Suscripcion`.


* **Encapsulamiento de Atributos:**
* `private int gigabytesConsumidos`

* `private int limiteGratuitoGB`

* `private double costoPorGBExtra`



* **Constructores y Métodos:**
* `public SuscripcionAlmacenamiento(String codigoContrato, double tarifaBase, int limiteGratuitoGB, double costoPorGBExtra)`

* `public void registrarConsumo(int totalGB)`

* Sobrescritura polimórfica: `public double calcularMontoFacturable()`.





---

### FASE 3: ESTRUCTURA DEL PROYECTO MAVEN Y CONVENCIONES

* **Estructura Física del Proyecto:** El código productivo debe ubicarse en `src/main/java/` y la totalidad de clases de prueba en `src/test/java/`.


* **Dependencia del Gestor de Pruebas:** Archivo `pom.xml` configurado para Java 21 y JUnit en su versión 4.13.2 con alcance de prueba (`<scope>test</scope>`).


* **Patrón de Nomenclatura:** Todas las clases de prueba deben finalizar estrictamente con el sufijo `Test` (ej. `ClienteTest.java`) para asegurar su descubrimiento por el plugin surefire de Maven.



---

### FASE 4: BATERÍA ESTANDARIZADA DE PRUEBAS UNITARIAS (JUNIT)

Cada prueba unitaria debe implementarse siguiendo el patrón **AAA (Arrange, Act, Assert)**. Se listan las suites de prueba obligatorias por cada unidad de compilación:

#### 4.1. Suite: `ClienteTest`

*Ubicación:* `src/test/java/edu/upv/streamflow/modelo/ClienteTest.java`

| Método de Prueba | Tipo de Prueba | Estímulo y Condiciones (Arrange & Act) | Aseveración / Resultado Esperado (Assert) |
| --- | --- | --- | --- |
| `deberiaInstanciarClienteValido` | Happy Path | `new Cliente("CLI-1", "Ana Gomez", 1500.0)`<br> | Comprobar que los métodos getter devuelven los valores exactos.

 |
| `deberiaLanzarExcepcionPorNombreInvalido` | Negative Path | Invocar constructor con nombre nulo o cadena en blanco

 | Se espera `IllegalArgumentException`.

 |
| `deberiaLanzarExcepcionPorSaldoInicialNegativo` | Invariante / Borde | Invocar constructor con `saldoInicial = -1.0`<br> | Se espera `IllegalArgumentException`.

 |
| `deberiaAbonarSaldoCorrectamente` | Happy Path | Saldo actual de $1000.0$, invocar `abonarSaldo(500.0)`<br> | Comprobar que `getSaldoDisponible()` es igual a $1500.0$.

 |
| `deberiaLanzarExcepcionAlAbonarMontoNegativo` | Edge Case | Invocar `abonarSaldo(-50.0)`<br> | Se espera `IllegalArgumentException`.

 |
| `deberiaDebitarSaldoExitosamente` | Happy Path | Saldo actual de $800.0$, invocar `debitarSaldo(300.0)`<br> | Comprobar que el saldo restante es $500.0$.

 |
| `deberiaLanzarExcepcionPropiaAlDebitarMontoMayorAlSaldo` | Regla Negocio | Saldo actual de $200.0$, invocar `debitarSaldo(500.0)`<br> | Se espera `TransaccionRechazadaException`.

 |

#### 4.2. Suite: `SuscripcionStreamingTest`

*Ubicación:* `src/test/java/edu/upv/streamflow/modelo/SuscripcionStreamingTest.java`

| Método de Prueba | Tipo de Prueba | Estímulo y Condiciones (Arrange & Act) | Aseveración / Resultado Esperado (Assert) |
| --- | --- | --- | --- |
| `deberiaCalcularTarifaSinPerfilesExtra` | Happy Path | Tarifa base $150.0$, $0$ perfiles adicionales

 | `calcularMontoFacturable()` es igual a $150.0$.

 |
| `deberiaCalcularTarifaConPerfilesExtra` | Happy Path | Tarifa base $150.0$, $2$ perfiles extra a $50.0$ c/u

 | `calcularMontoFacturable()` es igual a $250.0$.

 |
| `deberiaIncrementarCostoAlAgregarPerfil` | Comportamiento | Objeto existente con 1 perfil adicional, invocar `agregarPerfil()`<br> | Nuevo cálculo refleja el costo del nuevo perfil agregado.

 |
| `deberiaLanzarExcepcionSiPerfilesInicialesEsNegativo` | Edge Case | Constructor con `-2` perfiles adicionales

 | Se espera `IllegalArgumentException`.

 |

#### 4.3. Suite: `SuscripcionStreamingFamiliarTest`

*Ubicación:* `src/test/java/edu/upv/streamflow/modelo/SuscripcionStreamingFamiliarTest.java`

| Método de Prueba | Tipo de Prueba | Estímulo y Condiciones (Arrange & Act) | Aseveración / Resultado Esperado (Assert) |
| --- | --- | --- | --- |
| `deberiaBonificarUnicoPerfilAdicional` | Regla Negocio | Tarifa base $200.0$, $1$ perfil extra a $60.0$ c/u

 | `calcularMontoFacturable()` retorna exactamente $200.0$.

 |
| `deberiaCobrarPerfilesExcedentesDespuesDeBonificar` | Regla Negocio | Tarifa base $200.0$, $3$ perfiles extra a $60.0$ c/u

 | `calcularMontoFacturable()` retorna $320.0$ (cobra solo 2).

 |
| `deberiaMantenerTarifaBaseSiNoHayPerfilesExtra` | Edge Case | Tarifa base $200.0$, $0$ perfiles extra

 | `calcularMontoFacturable()` retorna $200.0$.

 |

#### 4.4. Suite: `SuscripcionAlmacenamientoTest`

*Ubicación:* `src/test/java/edu/upv/streamflow/modelo/SuscripcionAlmacenamientoTest.java`

| Método de Prueba | Tipo de Prueba | Estímulo y Condiciones (Arrange & Act) | Aseveración / Resultado Esperado (Assert) |
| --- | --- | --- | --- |
| `deberiaCobrarTarifaBaseDentroDelLimiteGratuito` | Happy Path | Base $100.0$, límite $50$ GB, consumo registrado de $30$ GB

 | `calcularMontoFacturable()` es igual a $100.0$.

 |
| `deberiaCobrarTarifaBaseEnElLimiteExacto` | Boundary Case | Base $100.0$, límite $50$ GB, consumo de exactamente $50$ GB

 | `calcularMontoFacturable()` es igual a $100.0$.

 |
| `deberiaCalcularExcedenteDeAlmacenamiento` | Regla Negocio | Base $100.0$, límite $50$ GB, consumo de $70$ GB ($20$ GB extra a $5.0$/GB)

 | `calcularMontoFacturable()` es igual a $200.0$.

 |
| `deberiaLanzarExcepcionAlRegistrarConsumoNegativo` | Negative Path | Invocar `registrarConsumo(-15)`<br> | Se espera `IllegalArgumentException`.

 |

#### 4.5. Suite: `CicloCobroPolimorficoTest` (Integración de Interfaces y Excepciones)

*Ubicación:* `src/test/java/edu/upv/streamflow/modelo/CicloCobroPolimorficoTest.java`

| Método de Prueba | Tipo de Prueba | Estímulo y Condiciones (Arrange & Act) | Aseveración / Resultado Esperado (Assert) |
| --- | --- | --- | --- |
| `deberiaProcesarCobroPolimorficoEnSuscripcionStreaming` | Polimorfismo / Happy Path | Instancia de `SuscripcionStreaming` tratada mediante la referencia de interfaz `Facturable`. Cliente con $1000.0$. Invocar `procesarCobro(cliente)`.

 | Saldo del cliente disminuye según el cálculo polimórfico.

 |
| `deberiaFallarCobroPorSaldoInsuficiente` | Excepción Marcada | Cliente con $50.0$, suscripción activa cuyo costo es $250.0$. Invocar `procesarCobro(cliente)`.

 | Se captura `TransaccionRechazadaException` y el saldo del cliente queda en $50.0$ (intacto).

 |
| `deberiaLanzarExcepcionNoMarcadaAlCobrarServicioCancelado` | Excepción No Marcada | Suscripción previamente cancelada mediante `cancelarServicio()`. Invocar `procesarCobro(cliente)`.

 | Se lanza `SuscripcionInactivaException`.

 |
| `deberiaPermitirCancelarServicioActivo` | Ciclo de Vida | Suscripción activa. Invocar `cancelarServicio()`.

 | Comprobar que `estaActivo()` pasa de `true` a `false`.

 |


