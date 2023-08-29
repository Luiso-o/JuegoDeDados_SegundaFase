package JuegoDeDados.Mongo.controller;

import JuegoDeDados.Mongo.model.Dto.AuthResponseMongo;
import JuegoDeDados.Mongo.model.Services.AuthServiceMongo;
import JuegoDeDados.Mongo.model.Services.JugadorServicesMongo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControllerMongo {

    private final AuthServiceMongo authService;
    private final JugadorServicesMongo jugadorServicesMongo;

    @Operation(summary = "Registro de un usuario",description = "Registra tus datos para darte de alta como jugador")
    @ApiResponse(responseCode = "200", description = "Registro exitoso")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    @PostMapping(value = "register")
    public ResponseEntity<AuthResponseMongo> register(@RequestParam String nombre, @RequestParam String email, @RequestParam String password){
        return ResponseEntity.ok(jugadorServicesMongo.register(nombre, email,password));
    }

    @PostMapping(value = "login")
    @Operation(summary = "Inicio de sesión", description = "Introduce tus datos de usuario para generar un token que podrás usar para acceder a los demás endpoints")
    @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    public ResponseEntity<AuthResponseMongo> login(@RequestParam String email, String password) {
        return ResponseEntity.ok(authService.login(email, password));
    }


}