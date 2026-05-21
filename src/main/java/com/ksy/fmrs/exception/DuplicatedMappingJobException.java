package com.ksy.fmrs.exception;

public class DuplicatedMappingJobException extends RuntimeException {
    public DuplicatedMappingJobException() {
        super("Mapping job already running");
    }
}
