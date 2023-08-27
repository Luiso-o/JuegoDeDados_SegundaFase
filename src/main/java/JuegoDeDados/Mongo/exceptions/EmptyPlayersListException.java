package JuegoDeDados.Mongo.exceptions;

import java.util.NoSuchElementException;

public class EmptyPlayersListException extends NoSuchElementException {
    public EmptyPlayersListException(){
        super("Lista de jugadores vacía");
    }
}
