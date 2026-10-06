# SpeedFast – Semana 8 (Desarrollo Orientado a Objetos II)

Aplicación Java Swing con persistencia en MySQL mediante JDBC (CRUD de repartidores, pedidos y entregas).

## Estructura
```
src/
  model/   Repartidor, Pedido (+Comida/Encomienda/Express), Entrega, enums TipoPedido y EstadoPedido
  conexion/ ConexionBD (la de la semana 7)
  dao/     RepartidorDAO, PedidoDAO, EntregaDAO   (PreparedStatement + ResultSet)
  vista/   VentanaPrincipal, PanelRepartidores, PanelPedidos, PanelEntregas, ItemCombo, Mensajes, Recargable
  main/    Main
sql/
  speedfast_db.sql   crea la base de datos y las tablas
  datos_prueba.sql   (opcional) datos de ejemplo
```

## Cómo ejecutarlo
1. **Base de datos:** en MySQL Workbench abre y ejecuta `sql/speedfast_db.sql`.
2. **Credenciales:** en `src/conexion/ConexionBD.java` ajusta `USUARIO` y `CLAVE`.
3. **Conector JDBC:** agrega `mysql-connector-j` (JAR) al proyecto.
   - IntelliJ: File > Project Structure > Libraries > + > From Maven… > `com.mysql:mysql-connector-j:8.4.0`
   - NetBeans: clic derecho en *Libraries* > Add JAR/Folder… (o Add Library > MySQL JDBC Driver)
4. **Ejecutar:** clase `main.Main`.

## Funcionalidades
- **Repartidores:** registrar, editar, eliminar, listar en JTable.
- **Pedidos:** registrar (dirección, tipo, estado), editar, eliminar, listar con filtros opcionales por estado y tipo.
- **Entregas:** registrar asociando Pedido y Repartidor (JComboBox cargados desde la BD, muestran "id - texto" y conservan el id) con fecha y hora; editar, eliminar, listar con filtros por pedido y/o repartidor.
- Validación de campos antes de cada operación y mensajes claros (JOptionPane) para errores SQL.
- No se puede eliminar un pedido o repartidor que tenga entregas asociadas (se muestra un aviso).

## Nota sobre la base de datos
El script de esta semana crea las tablas `repartidores`, `pedidos` y `entregas` (esquema entregado en la pauta). Las tablas `repartidor`, `pedido` y `entrega` de la semana 7, si existen, no se tocan.
