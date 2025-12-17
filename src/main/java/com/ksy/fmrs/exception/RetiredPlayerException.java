package com.ksy.fmrs.exception;

public class RetiredPlayerException extends SkippableSyncException {
    public RetiredPlayerException(String message) {
        super(message);
    }
}
