package com.tecgurus.filemanager.config.minio.dto;

import io.minio.ObjectStat;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MinioObject {

    private String bucketName;
    private String name;
    private Date createdTime;
    private long length;
    private String etag;
    private String contentType;
    private String path;

    public MinioObject(ObjectStat os) {
        this.bucketName = os.bucketName();
        this.name = os.name();
        this.createdTime = os.createdTime();
        this.length = os.length();
        this.etag = os.etag();
        this.contentType = os.contentType();
    }

    public MinioObject(ObjectStat os, String endpoint) {
        this.bucketName = os.bucketName();
        this.name = os.name();
        this.createdTime = os.createdTime();
        this.length = os.length();
        this.etag = os.etag();
        this.contentType = os.contentType();

        this.path = String.format("%s/%s/%s", endpoint, this.bucketName, this.name);
    }

}
