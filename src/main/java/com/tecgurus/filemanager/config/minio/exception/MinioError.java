package com.tecgurus.filemanager.config.minio.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class MinioError {

    private HttpStatus status;
    private String message;

    public MinioError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
