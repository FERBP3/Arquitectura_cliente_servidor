# Client-server arquitecture
System that implements the client-server arquitecture

## Content of the image

- openjdk 8
- HTTPServer

## Instrucions to run
Required `Docker CE`.

### `docker-compose up --build`
Start the container, so the service defined in `Test.py` is accesible in `localhost:8000/api`.

## Instructions to stop

Ctr + C and execute `docker-compose down`.

## Instructions for change the routine of the arquitecture

Change the file `src/Handler.java`

## Run the client

### `javac -cp gson-2.6.2.jar: Cliente.java`
### `java -cp gson-2.6.2.jar: Cliente`
