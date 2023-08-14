package JuegoDeDados.Mongo.controller;

import JuegoDeDados.Mongo.model.Dto.JugadorDtoMongo;
import JuegoDeDados.Mongo.model.Dto.PartidaDtoMongo;
import JuegoDeDados.Mongo.model.Services.JugadorServicesMongo;
import JuegoDeDados.Mongo.model.Services.PartidaServiceMongo;
import JuegoDeDados.Mongo.model.entity.JugadorEntityMongo;
import com.mongodb.lang.Nullable;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Builder
@RequestMapping("Jugador")
@OpenAPIDefinition(info = @Info(title = "Juego de Dados API",version = "6.0",description = "API para gestionar jugadores y partidas en el juego de dados"))
public class ControllerMongo {

    @Autowired
    private JugadorServicesMongo jugadorServicesMongo;

    @Autowired
    private PartidaServiceMongo partidaServiceMongo;

    @Operation(summary = "Crea un nuevo jugador", description = "devuelve un objeto jugador,recibirá un parametro de tipo String, si no recibe nada devolverá un Anónimo")
    @ApiResponse(responseCode = "200", description = "Nuevo Jugador Guardado con éxito")
    @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    @ApiResponse(responseCode = "500", description = "Error interno, Revise response status 500")
    @PostMapping
    public ResponseEntity<JugadorDtoMongo> crearNuevoUsuario(@Nullable @RequestParam
             @Pattern(regexp = "^[a-zA-Z]*$",message = "El nombre debe contener solo letras") String nombre)
    {
        JugadorDtoMongo jugadorNuevo = jugadorServicesMongo.crearJugador(nombre);
        return ResponseEntity.ok(jugadorNuevo);
    }

    @Operation(summary = "Actualiza el nombre de un Jugador", description = "Actualizará el nombre del jugador correspondiente al id introducido")
    @ApiResponse(responseCode = "200", description = "Nombre de jugador actualizado con éxito")
    @ApiResponse(responseCode = "500", description = "Error interno, Revise response status 500")
    @PutMapping("/{id}")
    public ResponseEntity<JugadorDtoMongo> actualizarJugador
            (@PathVariable String id,
             @Nullable
             @RequestParam
             @Pattern(regexp = "^[a-zA-Z]*$",message = "El nombre debe contener solo letras") String nombre)
    {
        JugadorEntityMongo jugadorEntidad = jugadorServicesMongo.buscarJugadorPorId(id);
        JugadorDtoMongo jugador = jugadorServicesMongo.actualizarNombreJugador(jugadorEntidad, nombre);
        return ResponseEntity.ok(jugador);
    }

    @Operation(summary = "Juega una partida", description = "Lanza los dados y devuelve los resultados de la partida")
    @ApiResponse(responseCode = "200", description = "Partida realizada y guardada con éxito")
    @ApiResponse(responseCode = "500", description = "Error interno, Revise response status 500")
    @PostMapping("/{id}/juego")
    public ResponseEntity<PartidaDtoMongo> tirarDados(@PathVariable String id){
        JugadorEntityMongo jugador = jugadorServicesMongo.buscarJugadorPorId(id);
        PartidaDtoMongo nuevaPartida = partidaServiceMongo.crearPartida(jugador);
        jugadorServicesMongo.actualizarPorcentajeExitoJugador(jugador);
        return ResponseEntity.ok(nuevaPartida);
    }

    @Operation(summary = "Elimina las partidas de un Jugador", description = "recibe el id de un jugador y elimina sus partidas")
    @ApiResponse(responseCode = "204", description = "Partidas eliminadas con éxito")
    @ApiResponse(responseCode = "500", description = "Error interno, Revise response status 500")
    @DeleteMapping("/{id}/partidas")
    public ResponseEntity<Void> eliminarPartidasDeUnJugador(@PathVariable String id) {
        JugadorEntityMongo jugador = jugadorServicesMongo.buscarJugadorPorId(id);
        partidaServiceMongo.eliminarPartidasDeJugador(jugador);
        jugadorServicesMongo.actualizarPorcentajeExitoJugador(jugador);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ver lista de jugadores",description = "Devuelve la lista de los jugadores con su porcentaje éxito")
    @ApiResponse(responseCode = "200", description = "Lista procesada con éxito")
    @ApiResponse(responseCode = "404", description = "Lista no encontrada")
    @ApiResponse(responseCode = "500", description = "Error interno, Revise response status 500")
    @GetMapping()
    public List<JugadorDtoMongo> obtenerListaJugadoresConPorcentajeMedioExito() {
        List<JugadorDtoMongo> jugadores = jugadorServicesMongo.listaJugadores();
        return ResponseEntity.ok(jugadores).getBody();
    }

    @Operation(summary = "Busca Partidas de un jugador", description = "Encontrará las partidas de un jugador por su id")
    @ApiResponse(responseCode = "200", description = "Partidas encontradas con éxito")
    @ApiResponse(responseCode = "500", description = "Error interno, Revise response status 500")
    @GetMapping("/{id}/partidas")
    public ResponseEntity<List<PartidaDtoMongo>> muestraPartidasJugador(@PathVariable String id){
        JugadorEntityMongo jugador = jugadorServicesMongo.buscarJugadorPorId(id);
        List<PartidaDtoMongo> partidas = partidaServiceMongo.encuentraPartidasJugador(jugador);
        return ResponseEntity.ok(partidas);
    }

    @Operation(summary = "Ranking de victorias",description = "Muestra el porcentaje total de victorias de todos los jugadores")
    @ApiResponse(responseCode = "200", description = "Porcentaje realizado con éxito")
    @ApiResponse(responseCode = "500", description = "Error interno, Revise response status 500")
    @GetMapping("/ranking")
    public ResponseEntity<Map<String, Object>> muestraPorcentajeVictorias(){
        int porcentaje = jugadorServicesMongo.calculaPorcentajeVictoriasGlobales();
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("Porcentaje de victorias globales ", porcentaje + "%");
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Revisa peores jugadores",description = "Muestra los jugadores con el porcentaje más bajo de victorias")
    @ApiResponse(responseCode = "200", description = "Lista encontrada con éxito")
    @ApiResponse(responseCode = "500", description = "Error interno, Revise response status 500")
    @GetMapping("/ranking/peores")
    public ResponseEntity<List<JugadorDtoMongo>> peoresPorcentajes() {
        List<JugadorDtoMongo> peoresJugadores = jugadorServicesMongo.peoresJugadores();
        return ResponseEntity.ok(peoresJugadores);
    }

    @Operation(summary = "Revisa mejores jugadores",description = "Muestra los jugadores con el porcentaje más alto de victorias")
    @ApiResponse(responseCode = "200", description = "Lista encontrada con éxito")
    @ApiResponse(responseCode = "500", description = "Error interno, Revise response status 500")
    @GetMapping("/ranking/mejores")
    public ResponseEntity<List<JugadorDtoMongo>> mejoresPorcentajes() {
        List<JugadorDtoMongo> peoresJugadores = jugadorServicesMongo.mejoresJugadores();
        return ResponseEntity.ok(peoresJugadores);
    }


}