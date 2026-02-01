package com.tansiyon.tansiyon_takip.exception;

public class DuplicateRecordException extends RuntimeException {

    public DuplicateRecordException(String message) {
        super(message);
    }

    public DuplicateRecordException(String tarih, String zamanDilimi) {
        super(String.format("%s tarihli %s ölçümü zaten mevcut.", tarih, zamanDilimi));
    }
}
