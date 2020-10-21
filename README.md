# Servicio (Java)

Este archivo contiene la información necesaria para construir la imagen de docker e iniciar el contenedor

## Contenido de la imagen

La imagen contiene openjdk 8 y el servicio que utiliza HTTPServer nativo del lenguaje de programación.

## Instrucciones para construir la imagen y correr el contenedor

Previamente se requiere tener instalado `Docker CE`.
Abrir una terminal y dirigirse a la carpeta donde ha clonado este repositorio y ejecutar los comandos listados abajo.

### `docker-compose up --build`

Construye la imagen correspondiente.

Inicia el contenedor, de esta forma es accesible el servicio definido en `Test.py` en `localhost:8000/api`.

## Instrucciones para detener el contenedor

Salir con Ctr + C y escrbir `docker-compose down`.

## Instrucciones para cambiar el puerto de acceso a la aplicación

En el archivo `Test.java` modificar el parámetro del objeto `InetSocketAddress(8000)`
Ubicar la línea que contiene la palabra `InetSocketAddress(8000)` en el archivo `Test.java`y modificar el parámetro de dicho método (Ej. `HttpServer server = HttpServer.create(new InetSocketAddress(1080), 0);`, de esta forma sería accesible el servicio a través de `localhost:1080/api`).

## Instrucciones para modificar las rutinas que realiza nuestro servicio

Se deberá modificar el archivo `Handler.java` que está en `src`.

## Instrucciones para compilar el cliente

Se debe de estar en la carpeta cliente, luego se ejecuta:

### `javac -cp gson-2.6.2.jar: Cliente.java`

Luego para ejecutar la aplicación se ejecuta:

### `java -cp gson-2.6.2.jar: Cliente`
