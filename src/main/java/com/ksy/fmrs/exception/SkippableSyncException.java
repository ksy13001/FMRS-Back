package com.ksy.fmrs.exception;

public abstract class SkippableSyncException extends RuntimeException {
    public SkippableSyncException(String message) {
        super(message);
    }
}
