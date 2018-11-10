package com.tecgurus.filemanager.config.minio.http;

import io.minio.errors.*;
import io.minio.messages.Bucket;

import com.tecgurus.filemanager.config.minio.service.MinioTemplate;
import com.tecgurus.filemanager.config.minio.dto.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.util.*;

@RestController
@RequestMapping("/api-filemanager/${minio.endpoint.name:/minio}")
@ConditionalOnProperty(name = "minio.endpoint.enable", havingValue = "true")
public class MinioEndpoint {

    @Autowired
    private MinioTemplate template;

    /**
     *
     * Bucket Endpoints
     */
    @PostMapping("/bucket/{bucketName}")
    public Bucket createBucket(@PathVariable String bucketName) throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, InvalidPortException, ErrorResponseException, InternalException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InvalidEndpointException, RegionConflictException {

        template.createBucket(bucketName);
        return template.getBucket(bucketName).get();

    }

    @GetMapping("/bucket")
    public List<Bucket> getBuckets() throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidPortException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InvalidEndpointException, InternalException {
        return template.getAllBuckets();
    }

    @GetMapping("/bucket/{bucketName}")
    public Bucket getBucket(@PathVariable String bucketName) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidPortException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InvalidEndpointException, InternalException {
        return template.getBucket(bucketName).orElseThrow(() -> new IllegalArgumentException("Bucket Name not found!"));
    }

    @DeleteMapping("/bucket/{bucketName}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteBucket(@PathVariable String bucketName) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidPortException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InvalidEndpointException, InternalException {

        template.removeBucket(bucketName);
    }

    /**
     *
     * Object Endpoints
     */

    @PostMapping("/object/{bucketName}")
    public MinioObject createObject(@RequestBody MultipartFile object, @PathVariable String bucketName) throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, InvalidPortException, ErrorResponseException, InternalException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InvalidEndpointException, RegionConflictException, InvalidArgumentException {
        String name = object.getOriginalFilename();
        template.saveObject(bucketName, name, object.getInputStream(), object.getSize(), object.getContentType());
        return new MinioObject(template.getObjectInfo(bucketName, name));

    }

    @PostMapping("/object/{bucketName}/{objectName}")
    public MinioObject createObject(@RequestBody MultipartFile multipartFile, @PathVariable String bucketName, @PathVariable String objectName) throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, InvalidPortException, ErrorResponseException, InternalException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InvalidEndpointException, RegionConflictException, InvalidArgumentException {

        template.saveObject(bucketName, objectName, multipartFile.getInputStream(), multipartFile.getSize(), multipartFile.getContentType());

        String name = multipartFile.getOriginalFilename();

        return new MinioObject(template.getObjectInfo(bucketName, name), template.getEndpoint());
    }

    @GetMapping("/object/{bucketName}/{objectName}")
    public  List<MinioItem> filterObject(@PathVariable String bucketName, @PathVariable String objectName) throws InvalidPortException, InvalidEndpointException {

        return template.getAllObjectsByPrefix(bucketName, objectName, true);

    }

    @GetMapping("/object/{bucketName}/{objectName}/{expires}")
    public Map<String, Object> getObject( @PathVariable String bucketName, @PathVariable String objectName, @PathVariable Integer expires) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidPortException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InvalidEndpointException, InternalException, InvalidExpiresRangeException {
        Map<String,Object> responseBody = new HashMap<>();
        // Put Object info
        responseBody.put("bucket" , bucketName);
        responseBody.put("object" , objectName);
        responseBody.put("url" , template.getObjectURL(bucketName, objectName, expires));
        responseBody.put("expires" ,  expires);
        return  responseBody;
    }

    @DeleteMapping("/object/{bucketName}/{objectName}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteObject(@PathVariable String bucketName, @PathVariable String objectName) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidPortException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InvalidEndpointException, InternalException, InvalidArgumentException {

        template.removeObject(bucketName, objectName);
    }


}
