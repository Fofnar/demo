package com.fof.demo.exception;

public class UserAlreadyExistsException extends RuntimeException {

    //Message d'erreurs personnalisé
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}