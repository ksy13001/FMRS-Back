package com.ksy.fmrs.exception;

public class EmptyResponseException extends SkippableSyncException {
    public EmptyResponseException(String message) {
        super(message);
    }
}
