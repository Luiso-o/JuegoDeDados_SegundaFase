package JuegoDeDados.Mongo.controller;

import JuegoDeDados.Mongo.model.repository.JugadorRepositoryMongo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerMongoTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JugadorRepositoryMongo jugadorRepositoryMongo;

    @AfterEach
    public void setUp(){
        //se limpia la base de datos de prueba una ves realizado el test
        jugadorRepositoryMongo.deleteAll();
    }

    @Test
    @Order(1)
    public void testRegisterAndLogin() throws Exception {
        //Registrar un usuario nuevo
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .param("nombre", "Pedro")
                        .param("email", "pedro@ejemplo.com")
                        .param("password", "123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        //Iniciar sesión de usuario
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .param("email", "pedro@ejemplo.com")
                        .param("password", "123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }
}
