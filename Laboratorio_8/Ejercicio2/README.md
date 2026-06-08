# Ejercicio 2 - Simulación de Fallo en Transacción Distribuida

## Requisitos

- Windows 10/11
- Docker Desktop instalado y funcionando
- Python 3.12 o superior
- Conexión a internet (solo para la primera ejecución)

## Instalación

1. Clonar o descargar este repositorio

2. Abrir una terminal en la carpeta `Ejercicio2`

3. Instalar la librería de Python:
   ```bash
   pip install psycopg2-binary
   ```

## Ejecución

**Paso 1:** Levantar los contenedores
```bash
docker compose up -d
```

**Paso 2:** Ejecutar el script
```bash
python simulacion_fallo.py
```

**Paso 3:** Al finalizar, detener los contenedores
```bash
docker compose down
```