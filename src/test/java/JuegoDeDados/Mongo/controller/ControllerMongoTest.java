package JuegoDeDados.Mongo.controller;

import JuegoDeDados.Mongo.model.Services.PartidaServiceMongo;
import JuegoDeDados.Mongo.model.entity.JugadorEntityMongo;
import JuegoDeDados.Mongo.model.entity.PartidaEntityMongo;
import JuegoDeDados.Mongo.model.repository.JugadorRepositoryMongo;
import JuegoDeDados.Mongo.model.repository.PartidaRepositoryMongo;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;

import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")//config a otra base de datos de prueba (mongoDB)
class ControllerMongoTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JugadorRepositoryMongo jugadorRepositoryMongo;
    @Autowired
    private PartidaRepositoryMongo partidaRepositoryMongo;
    @Autowired
    private PartidaServiceMongo partidaServiceMongo;

    @Test
    @DirtiesContext
    public void cuandoSeCreaUnUsuario_ElResultadoSeraOk() throws Exception {
        mockMvc.perform(post("/jugador")
                        .param("nombre", "Marcos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("Marcos"));
    }

    @Test
    @DirtiesContext
    public void testActualizarNombreDeUnUsuario() throws Exception {
        JugadorEntityMongo jugador = new JugadorEntityMongo("1","Jose",0);
        jugadorRepositoryMongo.save(jugador);

        mockMvc.perform(put("/jugador/{id}", "1")
                        .param("nombre","")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("Anónimo"));
    }

    @Test
    @DirtiesContext
    public void testTirarDados() throws Exception {
        JugadorEntityMongo jugador = new JugadorEntityMongo("1","Maria",0);
        jugadorRepositoryMongo.save(jugador);

        mockMvc.perform(post("/jugador/{id}/juego", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.mensaje").value(
                        Matchers.either(Matchers.is("Ganaste :D"))
                                .or(Matchers.is("Perdiste :V"))
                ));
    }

    @Test
    @DirtiesContext
    public void testEliminarPartidasDeUnJugador() throws Exception {
        JugadorEntityMongo jugador = new JugadorEntityMongo("1", "Juan", 0);
        jugadorRepositoryMongo.save(jugador);

        PartidaEntityMongo partida1 = PartidaEntityMongo.builder()
                .fecha(LocalDate.now())
                .victorias(1)
                .derrotas(0)
                .jugador(jugador)
                .build();

        PartidaEntityMongo partida2 = PartidaEntityMongo.builder()
                .fecha(LocalDate.now())
                .victorias(0)
                .derrotas(1)
                .jugador(jugador)
                .build();
        partidaRepositoryMongo.saveAll(Arrays.asList(partida1, partida2));

        mockMvc.perform(delete("/jugador/{id}/partidas", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Partidas eliminadas con éxito"));

    }

    @Test
    @DirtiesContext
    public void testObtenerListaJugadoresConPorcentajeMedioExito() throws Exception {

        jugadorRepositoryMongo.saveAll(Arrays.asList(new JugadorEntityMongo("1","Mario",20)
                ,new JugadorEntityMongo("2","Mercedes",60),
                new JugadorEntityMongo("3","Pedro",30)));

        mockMvc.perform(get("/jugador"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nombre").value("Mario"))
                .andExpect(jsonPath("$[0].porcentajeExito").value(20))
                .andExpect(jsonPath("$[1].nombre").value("Mercedes"))
                .andExpect(jsonPath("$[1].porcentajeExito").value(60))
                .andExpect(jsonPath("$[2].nombre").value("Pedro"))
                .andExpect(jsonPath("$[2].porcentajeExito").value(30));

    }

    @Test
    @DirtiesContext
    public void testMuestraPartidasJugador() throws Exception {
        JugadorEntityMongo jugador = new JugadorEntityMongo("1", "Mario", 50);
        jugadorRepositoryMongo.save(jugador);

        PartidaEntityMongo partida1 = PartidaEntityMongo.builder()
                .fecha(LocalDate.now())
                .victorias(1)
                .derrotas(0)
                .jugador(jugador)
                .build();

        PartidaEntityMongo partida2 = PartidaEntityMongo.builder()
                .fecha(LocalDate.now())
                .victorias(0)
                .derrotas(1)
                .jugador(jugador)
                .build();
        partidaRepositoryMongo.saveAll(Arrays.asList(partida1, partida2));

        // Realizar la solicitud GET y verificar la respuesta
        mockMvc.perform(get("/jugador/{id}/partidas", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DirtiesContext
    public void testMuestraPorcentajeVictorias() throws Exception {
        jugadorRepositoryMongo.save(new JugadorEntityMongo("1", "Mario", 20));
        jugadorRepositoryMongo.save(new JugadorEntityMongo("2", "Mercedes", 60));
        jugadorRepositoryMongo.save(new JugadorEntityMongo("3", "Pedro", 30));

        mockMvc.perform(get("/jugador/ranking"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.['Porcentaje de victorias globales ']").value("36%"));

    }

    @Test
    @DirtiesContext
    public void testPeoresJugadores() throws Exception {
        JugadorEntityMongo jugador1 = new JugadorEntityMongo("1", "Mario", 20);
        JugadorEntityMongo jugador2 = new JugadorEntityMongo("2", "Mercedes", 30);
        jugadorRepositoryMongo.saveAll(Arrays.asList(jugador1, jugador2));

        ResultActions resultActions = mockMvc.perform(get("/jugador/ranking/peores")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DirtiesContext
    public void testMejoresJugadores() throws Exception {
        JugadorEntityMongo jugador1 = new JugadorEntityMongo("1", "Mario", 20);
        JugadorEntityMongo jugador2 = new JugadorEntityMongo("2", "Mercedes", 30);
        jugadorRepositoryMongo.saveAll(Arrays.asList(jugador1, jugador2));

        ResultActions resultActions = mockMvc.perform(get("/jugador/ranking/mejores")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }


}