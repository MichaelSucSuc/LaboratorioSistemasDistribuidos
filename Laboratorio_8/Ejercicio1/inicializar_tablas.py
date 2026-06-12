import psycopg2

def inicializar():
    # Inicializar Arequipa (Puerto 5434)
    try:
        conn = psycopg2.connect(dbname="almacen_arequipa", user="admin", password="admin123", host="localhost", port=5434)
        cursor = conn.cursor()
        cursor.execute("CREATE TABLE IF NOT EXISTS inventario (producto VARCHAR(50) PRIMARY KEY, stock INT);")
        cursor.execute("INSERT INTO inventario (producto, stock) VALUES ('Paracetamol', 100) ON CONFLICT (producto) DO UPDATE SET stock = 100;")
        conn.commit()
        cursor.close()
        conn.close()
        print("-> Nodo Arequipa inicializado con 100 unidades.")
    except Exception as e:
        print(f"Error inicializando Arequipa: {e}")

    # Inicializar Lima (Puerto 5433)
    try:
        conn = psycopg2.connect(dbname="almacen_lima", user="admin", password="admin123", host="localhost", port=5433)
        cursor = conn.cursor()
        cursor.execute("CREATE TABLE IF NOT EXISTS inventario (producto VARCHAR(50) PRIMARY KEY, stock INT);")
        cursor.execute("INSERT INTO inventario (producto, stock) VALUES ('Paracetamol', 50) ON CONFLICT (producto) DO UPDATE SET stock = 50;")
        conn.commit()
        cursor.close()
        conn.close()
        print("-> Nodo Lima inicializado con 50 unidades.")
    except Exception as e:
        print(f"Error inicializando Lima: {e}")

if __name__ == "__main__":
    inicializar()
