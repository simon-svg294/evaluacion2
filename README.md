# Gestión de usuarios y solicitudes

Este proyecto permite guardar usuarios en MySQL y administrar solicitudes con
una lista, una cola y pilas.

## Requisitos

- Java 25
- MySQL activo
- La base de datos `gestion_usuarios`

La conexión a MySQL se configura en `src/main/resources/application.properties`.
Usa ahí el usuario y la contraseña de tu instalación. No compartas esa
contraseña.
El usuario y la contraseña también se pueden definir con las variables de
entorno `DB_USER` y `DB_PASSWORD`. Si no se definen, se usan `root` y `root`.

## Ejecutar el proyecto

Desde la carpeta del proyecto, ejecuta:

```powershell
.\mvnw.cmd spring-boot:run
```

La API queda disponible en `http://localhost:8081`.

## Usuarios

La ruta principal es `/api/usuarios`.

| Método | Ruta | Uso |
| --- | --- | --- |
| GET | `/api/usuarios` | Ver todos los usuarios |
| GET | `/api/usuarios/buscar?nombre=ana` | Buscar por nombre |
| GET | `/api/usuarios/{id}` | Buscar un usuario |
| POST | `/api/usuarios` | Crear un usuario |
| PUT | `/api/usuarios/{id}` | Actualizar un usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar un usuario |

Para crear un usuario, envía un JSON con el tipo, nombre y correo, además de
los datos propios de ese tipo:

```json
{
  "tipo": "ADMINISTRADOR",
  "nombre": "Ana",
  "correo": "ana@ejemplo.com",
  "nivelAcceso": 2
}
```

Los tipos disponibles son `ADMINISTRADOR`, `EMPLEADO` y `CLIENTE`.
Un empleado necesita `cargo` y `salario`. Un cliente puede enviar
`puntosFidelidad`; si no los envía, empiezan en cero. No se puede repetir un
correo ni cambiar el tipo de usuario al actualizar.

Los usuarios se guardan en la base de datos.
Las listas se pueden paginar con `page` y `size`, y ordenar con `sort`.
Por ejemplo, `/api/usuarios?page=0&size=10&sort=nombre,asc`.

## Solicitudes

La ruta principal es `/api/solicitudes`.

| Método | Ruta | Uso |
| --- | --- | --- |
| GET | `/api/solicitudes` | Ver todas las solicitudes |
| GET | `/api/solicitudes/buscar?texto=xxx` | Buscar por texto en la descripción |
| GET | `/api/solicitudes/ordenadas` | Ver solicitudes ordenadas por descripción |
| GET | `/api/solicitudes/{id}` | Buscar una solicitud |
| POST | `/api/solicitudes` | Registrar una solicitud |
| GET | `/api/solicitudes/cola` | Ver la cola de solicitudes pendientes |
| GET | `/api/solicitudes/reciente` | Ver la solicitud más reciente |
| GET | `/api/solicitudes/historial` | Ver el historial de acciones |
| POST | `/api/solicitudes/procesar` | Procesar la siguiente solicitud |
| POST | `/api/solicitudes/procesar-todas` | Procesar toda la cola |

Para registrar una solicitud, envía una descripción y el ID de un cliente que
ya exista:

```json
{
  "descripcion": "Solicitar soporte",
  "clienteId": 1
}
```

La descripción no puede estar vacía y el cliente debe existir. Las solicitudes
se guardan en MySQL y se pueden consultar por su ID. La lista, la cola y las
pilas del gestor siguen en memoria para administrar las solicitudes mientras
la aplicación está activa. Al iniciar, las solicitudes pendientes se cargan
desde la base de datos a la cola. El historial de operaciones sí está solo en
memoria y se borra al reiniciar el proyecto.
La lista de solicitudes también acepta `page` y `size`, por ejemplo:
`/api/solicitudes?page=0&size=10`.

En `GestorSolicitudes`, `buscarPorDescripcion` busca texto dentro de las
descripciones y `ordenarPorDescripcion` devuelve una copia de la lista ordenada
alfabéticamente.

## Pruebas

Para ejecutar las pruebas con Java 25:

```powershell
.\mvnw.cmd test
```
