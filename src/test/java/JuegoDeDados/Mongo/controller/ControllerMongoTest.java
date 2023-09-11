package JuegoDeDados.Mongo.controller;

import JuegoDeDados.Mongo.exceptions.ListOfEmptyGamesException;
import JuegoDeDados.Mongo.model.Dto.AuthResponseMongo;
import JuegoDeDados.Mongo.model.repository.JugadorRepositoryMongo;
import JuegoDeDados.Mongo.model.repository.PartidaRepositoryMongo;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControllerMongoTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthControllerMongo authControllerMongo;
    @Autowired
    private JugadorRepositoryMongo jugadorRepositoryMongo;
    @Autowired
    private PartidaRepositoryMongo partidaRepositoryMongo;
    @Autowired
    private ControllerMongo controllerMongo;

    @BeforeEach
    public void setUp(){
        authControllerMongo.register("Luis", "luis@ejemplo.com", "123");
        authControllerMongo.register("Ana", "ana@ejemplo.com", "456");
        authControllerMongo.register("Juan", "juan@ejemplo.com", "789");

        controllerMongo.tirarDados(String.valueOf(1));
        controllerMongo.tirarDados(String.valueOf(2));
        controllerMongo.tirarDados(String.valueOf(3));

    }

    @AfterEach
    public void refresh(){
        jugadorRepositoryMongo.deleteAll();
        partidaRepositoryMongo.deleteAll();
    }

    @Test
    public void testActualizarJugador() throws Exception {
        ResponseEntity<AuthResponseMongo> loginResponse = authControllerMongo.login("luis@ejemplo.com", "123");
        // Obtener el token de la respuesta de inicio de sesión
        String authToken = Objects.requireNonNull(loginResponse.getBody()).getToken();

        mockMvc.perform(put("/jugador/{id}", 1)
                        .param("nombre", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Anónimo"));

    }

    @Test
    public void testTirarDados() throws Exception {
        ResponseEntity<AuthResponseMongo> loginResponse = authControllerMongo.login("ana@ejemplo.com", "456");
        // Obtener el token de la respuesta de inicio de sesión
        String authToken = Objects.requireNonNull(loginResponse.getBody()).getToken();

        mockMvc.perform(post("/jugador/{id}/juego", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje").value(
                        Matchers.either(Matchers.is("Ganaste :D"))
                                .or(Matchers.is("Perdiste :V"))
                ));
    }

    @Test
    public void alEliminarPartidas_deberiaRetornarNoContent() throws Exception {
        ResponseEntity<AuthResponseMongo> loginResponse = authControllerMongo.login("juan@ejemplo.com", "789");
        // Obtener el token de la respuesta de inicio de sesión
        String authToken = Objects.requireNonNull(loginResponse.getBody()).getToken();

        try {
            mockMvc.perform(delete("/jugador/{id}/partidas", 2)
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string("")); // No hay contenido en la respuesta

            // Intentar eliminar partidas nuevamente, debería lanzar una excepción
            mockMvc.perform(delete("/jugador/{id}/partidas", 2)
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.mensaje").value("UsuarioEjemplo no tiene partidas que eliminar"))
                    .andExpect(jsonPath("$.detalle").value("ListOfEmptyGamesException"));
        } catch (Exception e) {
            if (e.getCause() instanceof ListOfEmptyGamesException) {
                // La excepción fue lanzada, lo cual es lo esperado
                return;
            }
            throw e; // Lanzar cualquier otra excepción no esperada
        }
    }

    @Test
    public void alObtenerListaDeJugadores_deberiaRetornarListaProcesada() throws Exception {
        ResponseEntity<AuthResponseMongo> loginResponse = authControllerMongo.login("luis@ejemplo.com", "123");
        // Obtener el token de la respuesta de inicio de sesión
        String authToken = Objects.requireNonNull(loginResponse.getBody()).getToken();

        mockMvc.perform(get("/jugador")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray()); // La respuesta es un array (lista)
    }

    @Test
    public void testMuestraPartidasDeUnJugador() throws Exception {
        ResponseEntity<AuthResponseMongo> loginResponse = authControllerMongo.login("ana@ejemplo.com", "456");
        String authToken = Objects.requireNonNull(loginResponse.getBody()).getToken();

        mockMvc.perform(get("/jugador/{id}/partidas", 2)
                        .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

    }

    @Test
    public void testMuestraPorcentajeVictorias() throws Exception {
        ResponseEntity<AuthResponseMongo> loginResponse = authControllerMongo.login("juan@ejemplo.com", "789");
        String authToken = Objects.requireNonNull(loginResponse.getBody()).getToken();

        mockMvc.perform(get("/jugador/ranking")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.['Porcentaje de victorias globales ']").exists());
    }

    @Test
    public void testPeoresPorcentajes() throws Exception {
        ResponseEntity<AuthResponseMongo> loginResponse = authControllerMongo.login("luis@ejemplo.com", "123");
        String authToken = Objects.requireNonNull(loginResponse.getBody()).getToken();

        mockMvc.perform(get("/jugador/ranking/peores")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testMejoresPorcentajes() throws Exception {
        ResponseEntity<AuthResponseMongo> loginResponse = authControllerMongo.login("luis@ejemplo.com", "123");
        String authToken = Objects.requireNonNull(loginResponse.getBody()).getToken();

        mockMvc.perform(get("/jugador/ranking/mejores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

}