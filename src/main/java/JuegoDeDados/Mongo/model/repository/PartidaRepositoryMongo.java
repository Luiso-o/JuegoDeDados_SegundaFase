package JuegoDeDados.Mongo.model.repository;

import JuegoDeDados.Mongo.model.entity.JugadorEntityMongo;
import JuegoDeDados.Mongo.model.entity.PartidaEntityMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidaRepositoryMongo extends MongoRepository<PartidaEntityMongo, String> {
    /**
     * Busca y devuelve una lista de partidas asociadas a un jugador específico.
     *
     * @param jugador El jugador para el que se desea buscar las partidas.
     * @return Una lista de objetos PartidaEntityJpa relacionados con el jugador.
     */
    List<PartidaEntityMongo> findByJugador(JugadorEntityMongo jugador);
}
