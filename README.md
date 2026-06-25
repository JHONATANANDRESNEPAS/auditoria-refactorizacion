# 📋 Auditoría y Refactorización de Código Java

Actividad práctica de mejora de código aplicando principios SOLID, buenas prácticas de programación y normas de calidad.

---

## 🎯 Objetivo
Transformar un código original con acoplamiento alto, responsabilidades mezcladas y código duplicado en una estructura limpia, segura, mantenible y extensible.

---

## 📝 Resumen de refactorizaciones realizadas

### 🔹 Refactorización 1: Separar conexión a base de datos
**Principios aplicados**: SRP (Responsabilidad Única), DIP (Inversión de Dependencias)
- Se crea la clase `ConexionBD.java` exclusiva para gestionar la conexión.
- Se elimina la lógica de conexión de `GestorPedidos.java`.
- Se centralizan datos de acceso y manejo de errores.

**Antes**: La clase de pedidos tenía que saber cómo conectarse a la base de datos.
**Después**: Solo solicita la conexión, sin conocer sus detalles.

---

### 🔹 Refactorización 2: Eliminar código duplicado
**Principios aplicados**: DRY, KISS, SRP
- Se unifican las validaciones de nombre y correo en un solo método `esClienteValido()`.
- Se elimina la repetición de código en los métodos `procesarPedido` y `cancelarPedido`.

**Antes**: Las mismas validaciones aparecían dos veces.
**Después**: Una sola fuente de verdad, fácil de modificar si cambian las reglas.

---

### 🔹 Refactorización 3: Mejorar cálculo de descuentos
**Principio aplicado**: OCP (Abierto/Cerrado)
- Se define la interfaz `EstrategiaDescuento`.
- Se crea una clase separada para cada tipo de descuento:
  - `DescuentoVIP.java` → 20%
  - `DescuentoFrecuente.java` → 10%
  - `DescuentoRegular.java` → 5%
  - `DescuentoNuevo.java` → 0%
- Se reemplazan los `if-else` anidados por una estructura de estrategia.

**Antes**: Para agregar un nuevo tipo de cliente había que modificar el método completo.
**Después**: Solo se agrega una nueva clase sin tocar el código existente.

---

### 🔹 Refactorización 4: Seguridad y estandarización
**Mejoras aplicadas**:
- Uso de `PreparedStatement` en lugar de concatenación de cadenas → evita **inyección SQL**.
- Formateo de valores numéricos para mayor claridad.
- Manejo adecuado de espacios en nombres para archivos de factura.
- Mensajes de error más claros y detallados.

---

## 📂 Estructura final del proyecto

auditoria-refactorizacion/├── ConexionBD.java # Gestión de conexión a base de datos├── EstrategiaDescuento.java # Interfaz para descuentos├── DescuentoVIP.java├── DescuentoFrecuente.java├── DescuentoRegular.java├── DescuentoNuevo.java└── GestorPedidos.java # Lógica principal de pedidos


---

## ✅ Beneficios obtenidos
- ✅ **Mantenibilidad**: Cambios se hacen en un solo lugar.
- ✅ **Legibilidad**: Código más claro y organizado.
- ✅ **Extensibilidad**: Se agregan nuevas funcionalidades sin romper lo que ya funciona.
- ✅ **Seguridad**: Protección contra ataques comunes.
- ✅ **Cumplimiento**: Alineado con la norma **ISO/IEC 25010** de calidad de software.

---

## 📜 Historial de cambios

b6e6d01 Refactorización 4: Mejorar seguridad con PreparedStatement, evitar inyección SQL, formatear valores
5794b4c Refactorización 3: Aplicar principio OCP con estrategia de descuentos. Eliminar if-else anidados
274db31 Refactorización 2: Eliminar validaciones duplicadas. Aplica DRY, SRP y KISS
1088853 Refactorización 1: Separar conexión BD en clase propia. Aplica SRP y DIP
9ceb205 Versión inicial: código original sin modificaciones

