CREATE TABLE corredor (
usuario VARCHAR(25),
hash VARCHAR(30),
residencia VARCHAR(25),
sexo VARCHAR(10),
edad INTEGER
);

CREATE TABLE bitacora (
usuario VARCHAR(25),
hash VARCHAR(30),
distancia VARCHAR(15),
tiempo TIME,
tipo VARCHAR(15),
fecha DATE
);
