package JuegoDeDados.Mongo.model.Services;

import JuegoDeDados.Mongo.exceptions.EmptyPlayersListException;
import JuegoDeDados.Mongo.exceptions.ListOfEmptyGamesException;
import JuegoDeDados.Mongo.exceptions.PlayerNotFoundException;
import JuegoDeDados.Mongo.model.Dto.JugadorDtoMongo;
import JuegoDeDados.Mongo.model.entity.JugadorEntityMongo;
import JuegoDeDados.Mongo.model.entity.PartidaEntityMongo;
import JuegoDeDados.Mongo.model.repository.JugadorRepositoryMongo;
import JuegoDeDados.Mongo.model.repository.PartidaRepositoryMongo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Service
public class JugadorServicesMongo {

    private JugadorRepositoryMongo jugadorRepositoryMongo;

    private PartidaRepositoryMongo partidaRepositoryMongo;

    /**
     * Crea un nuevo jugador en el sistema.
     * <p>
     * Este método permite la creación de un nuevo jugador en el sistema con el nombre proporcionado.
     * Si el nombre es nulo o está en blanco, se asignará el nombre "Anónimo" al jugador creado.
     *
     * @param nombre El nombre del jugador. Puede ser nulo o en blanco.
     * @return Un objeto JugadorDtoMongo que representa al jugador recién creado.
     */
    public JugadorDtoMongo crearJugador(String nombre){
        JugadorEntityMongo jugador = JugadorEntityMongo.builder()
                .id(asignarId())
                .nombre(filtraNombre(nombre))
                .build();
        JugadorEntityMongo jugadorCreado = jugadorRepositoryMongo.save(jugador);
        return pasarEntidadADto(jugadorCreado);
    }

    /**
     * Busca y devuelve un jugador por su ID.
     *
     * @param id El ID del jugador que se desea buscar.
     * @return El objeto JugadorEntityMongo que representa al jugador con el ID especificado.
     * @throws NoSuchElementException Si la lista de jugadores está vacía.
     * @throws RuntimeException Si no se encuentra un jugador con el ID proporcionado.
     */
    public JugadorEntityMongo buscarJugadorPorId(String id) {
        List<JugadorEntityMongo> misJugadores = jugadorRepositoryMongo.findAll();

        if (misJugadores.isEmpty()) {
            throw new EmptyPlayersListException();
        }

        return jugadorRepositoryMongo.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));
    }

    /**
     * Actualiza el nombre de un jugador en la base de datos y devuelve la información actualizada en forma de DTO.
     *
     * @param jugador El objeto de entidad de jugador que se desea actualizar.
     * @param nombre El nuevo nombre que se desea asignar al jugador.
     * @return Un objeto DTO que representa al jugador actualizado.
     */
    public JugadorDtoMongo actualizarNombreJugador(JugadorEntityMongo jugador, String nombre){
        String nombreFinal = filtraNombre(nombre);
        jugador.setNombre(nombreFinal);
        jugadorRepositoryMongo.save(jugador);
        return pasarEntidadADto(jugador);
    }

    /**
     * Actualiza el porcentaje de éxito de un jugador y guarda los datos actualizados en la base de datos.
     *
     * @param jugador El objeto JugadorEntityMongo que representa al jugador cuyo porcentaje de éxito se actualizará.
     */
    @Transactional
    public void actualizarPorcentajeExitoJugador(JugadorEntityMongo jugador){
        int porcentajeExitoActualizado = calculaPorcentajeExitoDeUnJugador(jugador);
        jugador.setPorcentajeExito(porcentajeExitoActualizado);
        jugadorRepositoryMongo.save(jugador);
    }

    /**
     * Retorna una lista de todos los jugadores en forma de DTO.
     *
     * @return Una lista de objetos JugadorDtoMongo que representan a todos los jugadores.
     * @throws NotFoundException Si la lista de jugadores está vacía.
     */
    public List<JugadorDtoMongo> listaJugadores() {
        List<JugadorEntityMongo> jugadores = jugadorRepositoryMongo.findAll();
        if (jugadores.isEmpty()) {
            throw new EmptyPlayersListException();
        }
        return jugadores.stream().map(this::pasarEntidadADto)
                .collect(Collectors.toList());
    }

    /**
     * Calcula y devuelve el porcentaje global de victorias entre todos los jugadores.
     *
     * @return El porcentaje global de victorias.
     */
    public int calculaPorcentajeVictoriasGlobales(){
        List<JugadorEntityMongo> jugadores = jugadorRepositoryMongo.findAll();
        if(jugadores.isEmpty()){return 0;}
        int porcentajeExitoGlobal = jugadores.stream()
                .mapToInt(JugadorEntityMongo::getPorcentajeExito)
                .sum();
        if(porcentajeExitoGlobal == 0){return 0;}
        return porcentajeExitoGlobal/jugadores.size();
    }

    /**
     * Obtiene una lista de los peores jugadores basados en su porcentaje de éxito.
     *
     * @return Una lista de objetos JugadorDtoMongo que representan a los peores jugadores.
     * @throws EmptyPlayersListException Si no se encuentran jugadores en la base de datos.
     */
    public List<JugadorDtoMongo> peoresJugadores(){
        List<JugadorEntityMongo> todosLosJugadores = jugadorRepositoryMongo.findAll();
        List<JugadorDtoMongo> peoresJugadores = new ArrayList<>();
        int porcentajeMasBajo = 100; //Partimos con el porcentaje más alto

        if (todosLosJugadores.isEmpty()) {
            throw  new EmptyPlayersListException();
        }

        for (JugadorEntityMongo jugador : todosLosJugadores) {
            int miPorcentajeDeExito = jugador.getPorcentajeExito();

            if(miPorcentajeDeExito < porcentajeMasBajo){
                // Si encontramos un jugador con un porcentaje más bajo, limpiamos la lista anterior
                peoresJugadores.clear();
                porcentajeMasBajo = miPorcentajeDeExito;
            }
            if (miPorcentajeDeExito == porcentajeMasBajo) {
                JugadorDtoMongo jugadorDto = pasarEntidadADto(jugador);
                peoresJugadores.add(jugadorDto);
            }
        }
        return peoresJugadores;
    }

    /**
     * Obtiene una lista de los jugadores con el mismo porcentaje más alto de éxito.
     *
     * @return Una lista de objetos JugadorDtoMongo que representan a los jugadores con el mismo porcentaje más alto.
     * @throws EmptyPlayersListException Si no se encuentran jugadores en la base de datos.
     */
    public List<JugadorDtoMongo> mejoresJugadores()  {
        List<JugadorEntityMongo> todosLosJugadores = jugadorRepositoryMongo.findAll();
        List<JugadorDtoMongo> mejoresJugadores = new ArrayList<>();
        int porcentajeMasAlto = 0; // Partimos con el porcentaje más bajo

        if (todosLosJugadores.isEmpty()) {
            throw new EmptyPlayersListException();
        }

        for (JugadorEntityMongo jugador : todosLosJugadores) {
            int miPorcentajeDeExito = jugador.getPorcentajeExito();

            if (miPorcentajeDeExito > porcentajeMasAlto) {
                // Si encontramos un jugador con un porcentaje más alto, limpiamos la lista anterior
                mejoresJugadores.clear();
                porcentajeMasAlto = miPorcentajeDeExito;
            }
            if (miPorcentajeDeExito == porcentajeMasAlto) {
                JugadorDtoMongo jugadorDto = pasarEntidadADto(jugador);
                mejoresJugadores.add(jugadorDto);
            }
        }

        return mejoresJugadores;
    }

    //Metodos Privados----------------------------------------------------------------------->>

    /**
     * Calcula el porcentaje de éxito de un jugador basado en la cantidad de victorias en sus partidas.
     * <p>
     * Este método calcula el porcentaje de éxito de un jugador identificado por su ID. El porcentaje se calcula
     * dividiendo la suma de las victorias en las partidas del jugador por la cantidad total de partidas jugadas.
     * El resultado se multiplica por 100 para obtener el porcentaje.
     *
     * @param jugador La entidad del jugador para el que se va a calcular el porcentaje de éxito.
     * @return El porcentaje de éxito del jugador en sus partidas. Si no hay partidas registradas, devuelve 0.
     * @throws ListOfEmptyGamesException Si no se encuentra el jugador con el ID proporcionado.
     */
    private int calculaPorcentajeExitoDeUnJugador(JugadorEntityMongo jugador){

        List<PartidaEntityMongo> misPartidas = partidaRepositoryMongo.findByJugador(jugador);

        if (misPartidas == null) {
            throw new PlayerNotFoundException(jugador.getId());
        }

        if (misPartidas.isEmpty()) {
            throw new ListOfEmptyGamesException();
        }

        int victorias = misPartidas.stream().mapToInt(PartidaEntityMongo::getVictorias).sum();
        int cantidadPartidas = misPartidas.size();

        return (victorias * 100) / cantidadPartidas;
    }

    /**

     * Convierte una entidad JugadorEntityMongo en un DTO JugadorDtoMongo.
     * <p>
     * Esta función realiza la conversión de una entidad JugadorEntityMongo a un DTO JugadorDtoMongo,
     * asignando las propiedades relevantes de la entidad al DTO resultante.
     *
     * @param jugador La entidad JugadorEntityMongo que se va a convertir.
     * @return Un objeto JugadorDtoMongo que representa la entidad convertida.
     */
    private JugadorDtoMongo pasarEntidadADto(JugadorEntityMongo jugador) {
        return JugadorDtoMongo.builder()
                .id(jugador.getId())
                .nombre(jugador.getNombre())

                .porcentajeExito(jugador.getPorcentajeExito())
                .build();
    }

    /**
     * Filtra y normaliza un nombre, retornando "Anónimo" si el nombre es nulo, vacío o contiene solo espacios en blanco.
     *
     * @param cadena La cadena de texto a filtrar y normalizar.
     * @return El nombre filtrado o "Anónimo" si la cadena es nula, vacía o contiene solo espacios en blanco.
     */
    private String filtraNombre(String cadena){
        if (cadena != null && !cadena.isBlank()) {
            // Eliminar espacios en blanco y números de la cadena
            String nombreFiltrado = cadena.replaceAll("\\s+", "").replaceAll("\\d+", "");

            return nombreFiltrado.isEmpty() ? "Anónimo" : nombreFiltrado;
        } else {
            return "Anónimo";
        }
    }

    /**
     * Obtiene el siguiente ID para un jugador en la base de datos.
     *
     * @return El siguiente ID disponible como cadena.
     */
    private String asignarId() {
        List<JugadorEntityMongo> jugadores = jugadorRepositoryMongo.findAll();

        if (jugadores.isEmpty()) {
            return "1";
        }

        String maxId = jugadores.stream()
                .map(JugadorEntityMongo::getId)
                .max(Comparator.naturalOrder())
                .orElse("0");

        int nextId = Integer.parseInt(maxId) + 1;
        return String.valueOf(nextId);
    }


}
