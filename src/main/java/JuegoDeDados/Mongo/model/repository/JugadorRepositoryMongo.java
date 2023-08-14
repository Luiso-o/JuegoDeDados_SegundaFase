package JuegoDeDados.Mongo.model.repository;

import JuegoDeDados.Mongo.model.entity.JugadorEntityMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JugadorRepositoryMongo extends MongoRepository<JugadorEntityMongo, String> {
}
