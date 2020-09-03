package com.zipcode.gjblog.blogservice;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.findify.s3mock.S3Mock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class S3EngineServiceTest {

    S3EngineService s3;

    @Before//Class
    public void setUp(){
        //MIT License
        S3Mock api = new S3Mock.Builder().withPort(8004).withInMemoryBackend().build();
        api.start();

        AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(
                "http://localhost:8004", "us-east-2");
        AmazonS3 amazonS3 = AmazonS3ClientBuilder
                                        .standard()
                                        .withPathStyleAccessEnabled(true)
                                        .withEndpointConfiguration(endpoint)
                                        .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                                        .build();


        //Default test items
        amazonS3.createBucket("testbucket");
        s3 = new S3EngineService(amazonS3, "testbucket");
        amazonS3.putObject("testbucket", "keyName", "contents");
    }

    @Test
    public void insertObjectIntoS3Bucket() {
        //Given
        String expected = "dGVzdDEyMw==";

        //When
        s3.insertBase64IntoS3Bucket("test", expected);
        String actual = s3.getS3ItemAsBase64("Image/test.jpg");

        //Then
        assertEquals(expected, actual);
    }

    @Test
    public void getS3ItemAsBase64() {
        //Given
        String defaultKey = "keyName";
        String expected = "Y29udGVudHM="; // "contents" converted to Base64

        //When
        String actual = s3.getS3ItemAsBase64(defaultKey);

        //Then
        assertEquals(actual, expected);
    }
}