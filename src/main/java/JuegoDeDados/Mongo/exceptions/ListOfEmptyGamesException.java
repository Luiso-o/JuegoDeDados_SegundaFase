package JuegoDeDados.Mongo.exceptions;

import java.util.NoSuchElementException;

public class ListOfEmptyGamesException extends NoSuchElementException {
    public ListOfEmptyGamesException(){
        super("Lista de Partidas Vacía");
    }
}
