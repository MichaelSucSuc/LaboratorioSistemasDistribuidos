import psycopg2
import time
import sys

# -------------------------------------------------------------------
# CONFIGURACION DE CONEXIONES A LAS BASES DE DATOS
# Estos diccionarios contienen los parametros para conectarse a cada nodo
# -------------------------------------------------------------------

# Configuracion para conectarse al nodo de Arequipa (base de datos origen)
# El puerto 5432 es el estandar de PostgreSQL y mapea al contenedor arequipa_db
AREGUIPA_CONFIG = {
    "dbname": "almacen_arequipa",      # Nombre de la base de datos
    "user": "admin",                    # Usuario creado en docker-compose.yml
    "password": "admin123",             # Contrasenia definida en docker-compose.yml
    "host": "localhost",                # Los contenedores exponen puertos en localhost
    "port": 5432                        # Puerto externo del contenedor arequipa_db
}

# Configuracion para conectarse al nodo de Lima (base de datos destino)
# El puerto 5433 es diferente para evitar conflicto con el nodo Arequipa
LIMA_CONFIG = {
    "dbname": "almacen_lima",          # Nombre de la base de datos
    "user": "admin",                    # Mismo usuario que en Arequipa
    "password": "admin123",             # Misma contrasenia
    "host": "localhost",                # Mismo host
    "port": 5433                        # Puerto externo del contenedor lima_db
}


# -------------------------------------------------------------------
# FUNCIONES AUXILIARES PARA OPERACIONES CON LA BASE DE DATOS
# -------------------------------------------------------------------

def verificar_stock_arequipa(conn, cantidad_necesaria):
    """
    Verifica si el nodo Arequipa tiene suficiente stock para la transferencia.
    
    Parametros:
        conn: Conexion activa a la base de datos de Arequipa
        cantidad_necesaria: Cantidad de unidades que se desea transferir
    
    Retorna:
        True si hay stock suficiente, False en caso contrario
    """
    # Crear un cursor para ejecutar consultas SQL
    cursor = conn.cursor()
    
    # Consultar el stock actual del producto Paracetamol
    cursor.execute("SELECT stock FROM inventario WHERE producto = 'Paracetamol'")
    
    # Obtener el valor (fetchone devuelve una tupla, [0] toma el primer elemento)
    stock = cursor.fetchone()[0]
    
    # Cerrar el cursor para liberar recursos
    cursor.close()
    
    # Retornar True si hay suficiente stock, False si no
    return stock >= cantidad_necesaria


def descontar_stock_arequipa(conn, cantidad):
    """
    Descuenta la cantidad especificada del stock en Arequipa.
    NOTA: Esta operacion se ejecuta dentro de una transaccion y no es permanente
          hasta que se ejecute commit().
    
    Parametros:
        conn: Conexion activa a la base de datos de Arequipa
        cantidad: Numero de unidades a descontar
    """
    cursor = conn.cursor()
    
    # Consulta SQL que resta la cantidad al stock actual
    # El signo %s es un placeholder que psycopg2 reemplaza de forma segura
    cursor.execute(
        "UPDATE inventario SET stock = stock - %s WHERE producto = 'Paracetamol'",
        (cantidad,)
    )
    
    cursor.close()


def simular_caida_lima():
    """
    Simula una falla en el nodo Lima.
    Esta funcion lanza una excepcion para representar que el nodo destino
    dejo de responder durante la transaccion (por ejemplo, por caida de red,
    timeout o fallo del servidor).
    
    En un escenario real, esto podria ser un timeout de conexion, un error de red,
    o la deteccion de que el nodo remoto no responde.
    """
    print("=== SIMULANDO CAIDA DEL NODO LIMA ===")
    print("El nodo Lima no respondera al commit. Esto simula:")
    print("  - Caida del servidor de base de datos")
    print("  - Problema de conectividad de red")
    print("  - Timeout en la comunicacion")
    
    # Pequena pausa para simular el tiempo de espera
    time.sleep(2)
    
    # Lanzar una excepcion para interrumpir la transaccion
    # Esto representa que la operacion no pudo completarse
    raise Exception("Nodo Lima no responde (timeout o caida simulada)")


# -------------------------------------------------------------------
# FUNCION PRINCIPAL QUE EJECUTA LA TRANSACCION DISTRIBUIDA CON FALLO
# -------------------------------------------------------------------

def ejecutar_transaccion_con_fallo():
    """
    Ejecuta una transaccion distribuida entre Arequipa y Lima.
    Esta funcion simula el siguiente escenario:
    1. Se inicia una transaccion que abarca ambos nodos
    2. Se descuenta stock en Arequipa
    3. Antes de poder actualizar Lima, ocurre una falla
    4. Se ejecuta ROLLBACK para mantener la consistencia
    
    Este escenario demuestra la importancia de la atomicidad en transacciones
    distribuidas: o ambas operaciones se completan, o ninguna.
    """
    
    # Inicializar las conexiones como None para poder cerrarlas en caso de error
    conn_arequipa = None
    conn_lima = None
    
    try:
        # -------------------------------------------------------------------
        # PASO 1: Establecer conexiones con ambos nodos
        # -------------------------------------------------------------------
        print("Conectando a la base de datos de Arequipa (puerto 5432)...")
        conn_arequipa = psycopg2.connect(**AREGUIPA_CONFIG)
        # Desactivar autocommit: ahora cada operacion es parte de una transaccion
        # que debemos confirmar manualmente con commit() o cancelar con rollback()
        conn_arequipa.autocommit = False
        print("  - Conexion establecida. Modo transaccional activado.")
        
        print("Conectando a la base de datos de Lima (puerto 5433)...")
        conn_lima = psycopg2.connect(**LIMA_CONFIG)
        conn_lima.autocommit = False
        print("  - Conexion establecida. Modo transaccional activado.")
        
        cantidad = 20
        
        # -------------------------------------------------------------------
        # PASO 2: Verificar que haya stock suficiente en el origen
        # -------------------------------------------------------------------
        print(f"Verificando stock disponible en Arequipa (necesario: {cantidad} unidades)...")
        if not verificar_stock_arequipa(conn_arequipa, cantidad):
            print("ERROR: Stock insuficiente en Arequipa. Transaccion cancelada.")
            return
        print("  - Stock verificado correctamente.")
        
        # -------------------------------------------------------------------
        # PASO 3: Iniciar la transaccion distribuida
        # -------------------------------------------------------------------
        # En una base de datos distribuida, el coordinador (este script)
        # comienza una transaccion que abarca todos los participantes.
        # Aqui usamos autocommit=False, que implica que todas las operaciones
        # siguientes forman parte de una transaccion hasta commit() o rollback().
        print("Iniciando transaccion distribuida (abarcando Arequipa y Lima)...")
        
        # -------------------------------------------------------------------
        # PASO 4: Descontar stock en el nodo origen (Arequipa)
        # -------------------------------------------------------------------
        print(f"Descontando {cantidad} unidades del stock de Arequipa...")
        descontar_stock_arequipa(conn_arequipa, cantidad)
        
        # Verificar temporalmente el stock en Arequipa (aun sin commit)
        # Esto demuestra que el cambio existe dentro de la transaccion actual
        cursor = conn_arequipa.cursor()
        cursor.execute("SELECT stock FROM inventario WHERE producto = 'Paracetamol'")
        stock_arequipa_temp = cursor.fetchone()[0]
        cursor.close()
        print(f"  - Stock actual en Arequipa (dentro de la transaccion): {stock_arequipa_temp}")
        print("  (Este cambio aun no es permanente - falta el commit)")
        
        # -------------------------------------------------------------------
        # PASO 5: Simular falla antes de actualizar el destino
        # -------------------------------------------------------------------
        # En este punto, ocurre la falla. El nodo Lima deja de responder.
        # Esto impide completar la segunda parte de la transaccion.
        print("Intentando actualizar el stock en Lima...")
        simular_caida_lima()  # Esta funcion lanza una excepcion
        
        # -------------------------------------------------------------------
        # EL CODIGO A PARTIR DE AQUI NUNCA SE EJECUTA DEBIDO A LA FALLA
        # -------------------------------------------------------------------
        # Las siguientes lineas representan lo que deberia hacerse en una
        # transaccion exitosa, pero nunca llegan a ejecutarse.
        
        cursor_lima = conn_lima.cursor()
        cursor_lima.execute(
            "UPDATE inventario SET stock = stock + %s WHERE producto = 'Paracetamol'",
            (cantidad,)
        )
        cursor_lima.close()
        
        # Si todo hubiera salido bien, se ejecutaria commit en ambos nodos
        conn_arequipa.commit()
        conn_lima.commit()
        print("Transaccion completada exitosamente (esto no se muestra por la falla)")
        
    except Exception as e:
        # -------------------------------------------------------------------
        # MANEJO DE LA FALLA: EJECUTAR ROLLBACK EN AMBOS NODOS
        # -------------------------------------------------------------------
        # Cuando ocurre una falla (como la caida de Lima), el coordinador
        # debe garantizar la atomicidad ejecutando rollback en todos los
        # nodos participantes para deshacer cualquier cambio parcial.
        print(f"\n--- FALLO DETECTADO: {e} ---")
        print("Ejecutando ROLLBACK en ambos nodos para mantener la consistencia...")
        
        # Rollback en Arequipa: deshace el descuento de stock
        if conn_arequipa:
            conn_arequipa.rollback()
            print("  - Rollback completado en Arequipa (descuento revertido)")
        
        # Rollback en Lima: aunque no hubo cambios, garantiza consistencia
        if conn_lima:
            conn_lima.rollback()
            print("  - Rollback completado en Lima")
        
        print("\nResultado esperado: Ambos nodos mantienen sus valores originales")
        print("  Arequipa: 100 unidades (se revirtio el descuento)")
        print("  Lima: 50 unidades (nunca se modifico)")
        
    finally:
        # -------------------------------------------------------------------
        # LIMPIEZA: Cerrar las conexiones a las bases de datos
        # -------------------------------------------------------------------
        # Este bloque se ejecuta siempre, haya ocurrido error o no.
        # Es importante cerrar las conexiones para liberar recursos.
        if conn_arequipa:
            conn_arequipa.close()
            print("Conexion a Arequipa cerrada.")
        if conn_lima:
            conn_lima.close()
            print("Conexion a Lima cerrada.")


def verificar_estado_final():
    """
    Verifica que los stocks finales sean los correctos despues del fallo.
    Esta funcion consulta directamente ambas bases de datos para confirmar
    que el rollback funciono correctamente y los datos volvieron a su estado
    original (Arequipa=100, Lima=50).
    """
    print("\n" + "="*60)
    print("VERIFICANDO ESTADO FINAL DE LOS NODOS")
    print("="*60)
    
    # Consultar stock en Arequipa
    conn = psycopg2.connect(**AREGUIPA_CONFIG)
    cursor = conn.cursor()
    cursor.execute("SELECT stock FROM inventario WHERE producto = 'Paracetamol'")
    stock_arequipa = cursor.fetchone()[0]
    cursor.close()
    conn.close()
    
    # Consultar stock en Lima
    conn = psycopg2.connect(**LIMA_CONFIG)
    cursor = conn.cursor()
    cursor.execute("SELECT stock FROM inventario WHERE producto = 'Paracetamol'")
    stock_lima = cursor.fetchone()[0]
    cursor.close()
    conn.close()
    
    # Mostrar resultados
    print(f"  Nodo Arequipa - Stock final: {stock_arequipa} (original: 100)")
    print(f"  Nodo Lima - Stock final: {stock_lima} (original: 50)")
    
    # Validar si el resultado es correcto
    if stock_arequipa == 100 and stock_lima == 50:
        print("\n[RESULTADO CORRECTO] Los stocks se mantuvieron consistentes.")
        print("  El ROLLBACK evito la inconsistencia que habria ocurrido si")
        print("  solo se hubiera completado el descuento en Arequipa.")
        print("\n  Demostracion de la propiedad ACID de ATOMICIDAD:")
        print("  - La transaccion se revierte por completo ante cualquier fallo.")
        print("  - No quedan cambios parciales en ningun nodo.")
    else:
        print("\n[RESULTADO INCORRECTO] Los stocks son inconsistentes.")
        print("  Esto indica un problema en el manejo del rollback.")


# -------------------------------------------------------------------
# PUNTO DE ENTRADA PRINCIPAL DEL SCRIPT
# -------------------------------------------------------------------

if __name__ == "__main__":
    print("="*60)
    print("EJERCICIO 2 - SIMULACION DE FALLO EN TRANSACCION DISTRIBUIDA")
    print("="*60)
    print("\nEscenario:")
    print("  - Transferir 20 unidades de Paracetamol desde Arequipa hacia Lima")
    print("  - Se descuenta el stock en Arequipa")
    print("  - Antes de actualizar Lima, el nodo Lima falla")
    print("  - Se debe ejecutar ROLLBACK para mantener la consistencia")
    print("-"*60)
    
    # Ejecutar la transaccion con fallo
    ejecutar_transaccion_con_fallo()
    
    # Verificar que los datos finales sean correctos
    verificar_estado_final()
    
    print("\n" + "="*60)
    print("FIN DEL EJERCICIO 2")
    print("="*60)