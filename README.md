# JuegoDeDado_SegundaFase @MongoDB @Gradle @Swuagger

El juego de dados se juega con dos dados. En caso de que el resultado de la suma de ambos dados sea menor o igual a 7, la partida será ganada, si no es perdida. Un jugador/a puede ver un listado de todas las tiradas que ha realizado y el porcentaje de éxito.

Para poder jugar al juego y realizar una tirada, un usuario debe registrarse con un nombre no repetido. Al crearse, se le asigna un identificador numérico único y una fecha de registro. Si el usuario así lo desea, puedes no añadir ningún nombre y se llamará “ANÓNIMO”. Puede haber más de un jugador “ANÓNIMO”.
Cada jugador puede ver un listado de todas las tiradas que ha hecho, con el valor de cada dado y si se ha ganado o no la partida. Además, puede saber su porcentaje de éxito por todas las tiradas que ha realizado.

No se puede eliminar una partida en concreto, pero sí se puede eliminar todo el listado de tiradas por un jugador/a.

El software debe permitir listar a todos los jugadores/as que hay en el sistema, el porcentaje de éxito de cada jugador/a y el porcentaje de éxito medio de todos los jugadores/as en el sistema.

El software debe respetar los principales patrones de diseño.

NOTAS

Tienes que tener en cuenta los siguientes detalles de construcción:

URL's:
POST: /players: crea un jugador/a.
PUT /players: modifica el nombre del jugador/a.
POST /players/{id}/games/ : un jugador/a específico realiza un tirón de los dados.
DELETE /players/{id}/games: elimina las tiradas del jugador/a.
GET /players/: devuelve el listado de todos los jugadores/as del sistema con su porcentaje medio de éxitos.
GET /players/{id}/games: devuelve el listado de jugadas por un jugador/a.
GET /players/ranking: devuelve el ranking medio de todos los jugadores/as del sistema. Es decir, el porcentaje medio de logros.
GET /players/ranking/loser: devuelve al jugador/a con peor porcentaje de éxito.
GET /players/ranking/winner: devuelve al jugador con peor porcentaje de éxito.

fase 2 : Cambia todo lo que necesites y utiliza MongoDB para persistir sus datos.

Captura Swagger http://localhost:8080/swagger-ui/index.html#/
![Captura3](https://github.com/Luiso-o/JuegoDeDados_TerceraFase_ApiRest/assets/128043647/82597d76-c988-4dea-976a-e552c36240c3)

Captura Base de datos jugadores
![Captura1](https://github.com/Luiso-o/JuegoDeDados_TerceraFase_ApiRest/assets/128043647/ba8befc8-3590-408a-b0da-0e6a9bd1f151)

Captura base de datos Partidas
![Captura2](https://github.com/Luiso-o/JuegoDeDados_TerceraFase_ApiRest/assets/128043647/e1df5e9b-7190-449c-8667-f1a7f1d47146)

