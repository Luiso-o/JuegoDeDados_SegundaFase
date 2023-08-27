package JuegoDeDados.Mongo.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Información de una partida")
@Document(collection = "partidas")
public class PartidaEntityMongo {

    @MongoId
    private String id;
    private LocalDate fecha;
    private int victorias;
    private int derrotas;
    @DBRef
    private JugadorEntityMongo jugador;
}
