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
    "dbname": "almacen_arequipa",
    "user": "admin",
    "password": "admin123",
    "host": "localhost",
    "port": 5434                        # <-- Asegúrate de que diga 5434
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


# -------------------------------------------------------------------
# FUNCION PRINCIPAL QUE EJECUTA LA TRANSACCION DISTRIBUIDA EXITOSA
# -------------------------------------------------------------------

def ejecutar_transaccion_exitosa():
    """
    Ejecuta una transaccion distribuida exitosa entre Arequipa y Lima.
    Esta funcion simula el siguiente escenario (Ejercicio 1):
    1. Se inicia una transaccion que abarca ambos nodos de forma coordinada.
    2. Se verifica el stock disponible en el origen.
    3. Se descuenta stock en Arequipa.
    4. Se incrementa stock en Lima de manera correcta.
    5. Se confirma la transaccion (COMMIT) en ambos nodos para asegurar la consistencia.
    """
    
    # Inicializar las conexiones como None para poder cerrarlas en caso de error
    conn_arequipa = None
    conn_lima = None
    
    try:
        # -------------------------------------------------------------------
        # PASO 1 / ACTIVIDAD 2: Establecer conexiones con ambos nodos
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
        # PASO 2 / ACTIVIDAD 1: Verificar que haya stock suficiente en el origen
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
        print("Iniciando transaccion distribuida (abarcando Arequipa y Lima)...")
        
        # -------------------------------------------------------------------
        # PASO 4 / ACTIVIDAD 3: Descontar stock en el nodo origen (Arequipa)
        # -------------------------------------------------------------------
        print(f"Descontando {cantidad} unidades del stock de Arequipa...")
        descontar_stock_arequipa(conn_arequipa, cantidad)
        
        # Verificar temporalmente el stock en Arequipa (aun sin commit)
        cursor = conn_arequipa.cursor()
        cursor.execute("SELECT stock FROM inventario WHERE producto = 'Paracetamol'")
        stock_arequipa_temp = cursor.fetchone()[0]
        cursor.close()
        print(f"  - Stock actual en Arequipa (dentro de la transaccion): {stock_arequipa_temp}")
        print("  (Este cambio aun no es permanente - falta el commit)")
        
        # -------------------------------------------------------------------
        # PASO 5 / ACTIVIDAD 4: Actualizar el inventario en el nodo destino (Lima)
        # -------------------------------------------------------------------
        print(f"Aumentando {cantidad} unidades al stock de Lima...")
        
        cursor_lima = conn_lima.cursor()
        cursor_lima.execute(
            "UPDATE inventario SET stock = stock + %s WHERE producto = 'Paracetamol'",
            (cantidad,)
        )
        cursor_lima.close()
        
        # Verificar temporalmente el stock en Lima (aun sin commit)
        cursor = conn_lima.cursor()
        cursor.execute("SELECT stock FROM inventario WHERE producto = 'Paracetamol'")
        stock_lima_temp = cursor.fetchone()[0]
        cursor.close()
        print(f"  - Stock actual en Lima (dentro de la transaccion): {stock_lima_temp}")
        print("  (Este cambio aun no es permanente - falta el commit)")
        
        # -------------------------------------------------------------------
        # PASO 6 / ACTIVIDAD 5: Confirmar cambios (COMMIT) en ambos nodos
        # -------------------------------------------------------------------
        print("\nConfirmando transaccion distribuida (COMMIT)...")
        conn_arequipa.commit()
        conn_lima.commit()
        print("Transaccion completada exitosamente de forma duradera.")
        
    except Exception as e:
        # -------------------------------------------------------------------
        # MANEJO DE FALLAS INESPERADAS (ROLLBACK DE SEGURIDAD)
        # -------------------------------------------------------------------
        print(f"\n--- FALLO INESPERADO DETECTADO: {e} ---")
        print("Ejecutando ROLLBACK en ambos nodos por seguridad...")
        
        if conn_arequipa:
            conn_arequipa.rollback()
            print("  - Rollback completado en Arequipa")
        if conn_lima:
            conn_lima.rollback()
            print("  - Rollback completado en Lima")
        
    finally:
        # -------------------------------------------------------------------
        # LIMPIEZA: Cerrar las conexiones a las bases de datos
        # -------------------------------------------------------------------
        if conn_arequipa:
            conn_arequipa.close()
            print("Conexion a Arequipa cerrada.")
        if conn_lima:
            conn_lima.close()
            print("Conexion a Lima cerrada.")


def verificar_estado_final():
    """
    Verifica que los stocks finales sean los correctos despues de la transferencia.
    Esta funcion consulta directamente ambas bases de datos para confirmar
    que el flujo feliz funciono correctamente y los datos se consolidaron
    (Arequipa=80, Lima=70).
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
    print(f"  Nodo Arequipa - Stock final: {stock_arequipa} (Esperado: 80)")
    print(f"  Nodo Lima - Stock final: {stock_lima} (Esperado: 70)")
    
    # Validar si el resultado es correcto
    if stock_arequipa == 80 and stock_lima == 70:
        print("\n[RESULTADO CORRECTO] Los stocks se actualizaron correctamente.")
        print("  El COMMIT consolido los cambios parciales en ambos nodos.")
        print("\n  Demostracion de una Transaccion Distribuida Exitosa:")
        print("  - Ambas operaciones locales se guardaron permanentemente.")
        print("  - Se preserva la consistencia global del sistema de inventarios.")
    else:
        print("\n[RESULTADO INCORRECTO] Los stocks no coinciden con la tabla de la tarea.")


# -------------------------------------------------------------------
# PUNTO DE ENTRADA PRINCIPAL DEL SCRIPT
# -------------------------------------------------------------------

if __name__ == "__main__":
    print("="*60)
    print("EJERCICIO 1 - TRANSACCION DISTRIBUIDA EXITOSA (CONSOLIDACION)")
    print("="*60)
    print("\nEscenario:")
    print("  - Transferir 20 unidades de Paracetamol desde Arequipa hacia Lima")
    print("  - Se descuenta el stock en Arequipa (100 -> 80)")
    print("  - Se incrementa el stock en Lima (50 -> 70)")
    print("  - Ambos nodos guardan los cambios de forma exitosa")
    print("-"*60)
    
    # Ejecutar la transaccion exitosa
    ejecutar_transaccion_exitosa()
    
    # Verificar que los datos finales sean correctos conforme a la tabla de la tarea
    verificar_estado_final()
    
    print("\n" + "="*60)
    print("FIN DEL EJERCICIO 1")
    print("="*60)
