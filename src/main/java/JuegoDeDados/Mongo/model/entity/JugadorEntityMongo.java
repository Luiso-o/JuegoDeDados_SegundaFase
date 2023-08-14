package JuegoDeDados.Mongo.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Información de la entidad Jugador")
@Document(collection = "Jugadores")
public class JugadorEntityMongo {

    @MongoId
    private String id;
    private String nombre;
    private int porcentajeExito;

}
