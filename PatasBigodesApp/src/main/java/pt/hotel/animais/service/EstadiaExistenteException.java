package pt.hotel.animais.service;

public class EstadiaExistenteException extends IllegalArgumentException {

    public EstadiaExistenteException(String message) {
        super(message);
    }
}
