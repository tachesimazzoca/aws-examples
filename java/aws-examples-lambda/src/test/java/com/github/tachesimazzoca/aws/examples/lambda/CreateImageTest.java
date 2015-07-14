package com.github.tachesimazzoca.aws.examples.lambda;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification.S3BucketEntity;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.event.S3EventNotification.S3ObjectEntity;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CreateImageTest {
    private File openTestFile(String path) {
        return new File(getClass().getResource("/test").getPath(), path);
    }

    private static class MockAmazonS3StreamConverter implements AmazonS3StreamConverter {
        private final File testFile;

        public MockAmazonS3StreamConverter(File testFile) {
            this.testFile = testFile;
        }

        @Override
        public InputStream convertToInputStream(S3ObjectInputStream input)
                throws IOException {
            return new FileInputStream(testFile);
        }
    }

    private static class MockAmazonServiceFactory implements AmazonServiceFactory {
        @Override
        public AmazonS3 createAmazonS3Client() {
            S3Object s3Object = mock(S3Object.class);
            when(s3Object.getObjectContent())
                    .thenReturn(null);

            AmazonS3 s3Client = mock(AmazonS3Client.class);
            when(s3Client.getObject(any(GetObjectRequest.class)))
                    .thenReturn(s3Object);
            when(s3Client.putObject(
                    anyString(), anyString(),
                    any(InputStream.class), any(ObjectMetadata.class)))
                    .thenReturn(null);

            return s3Client;
        }
    }

    private static S3Event createMockS3Event(String bucketName, String objectKey) {
        S3Event mockS3Event = mock(S3Event.class);
        List<S3EventNotificationRecord> mockRecords =
                new ArrayList<S3EventNotificationRecord>();
        S3EventNotificationRecord mockRecord = mock(S3EventNotificationRecord.class);

        S3Entity mockS3 = mock(S3Entity.class);
        S3BucketEntity mockS3Bucket = mock(S3BucketEntity.class);
        when(mockS3Bucket.getName()).thenReturn(bucketName);

        S3ObjectEntity mockS3Object = mock(S3ObjectEntity.class);
        when(mockS3Object.getKey()).thenReturn(objectKey);

        when(mockS3.getBucket()).thenReturn(mockS3Bucket);
        when(mockS3.getObject()).thenReturn(mockS3Object);

        when(mockRecord.getS3()).thenReturn(mockS3);
        mockRecords.add(mockRecord);
        when(mockS3Event.getRecords()).thenReturn(mockRecords);
        return mockS3Event;
    }

    @Test
    public void testHandleRequest() {
        CreateImage f = new CreateImage(
                new MockAmazonServiceFactory(),
                new MockAmazonS3StreamConverter(openTestFile("/peacock.jpg")));

        // Not in the directory "lambda/upload/*"
        assertEquals(CreateImage.RESPONSE_SKIPPED,
                f.handleRequest(createMockS3Event("awesome-bucket",
                        "a.jpg"), null));
        assertEquals(CreateImage.RESPONSE_SKIPPED,
                f.handleRequest(createMockS3Event("awesome-bucket",
                        "emr/upload/a.jpg"), null));

        // Not an image file
        assertEquals(CreateImage.RESPONSE_SKIPPED,
                f.handleRequest(createMockS3Event("awesome-bucket",
                        "lambda/upload/"), null));
        assertEquals(CreateImage.RESPONSE_SKIPPED,
                f.handleRequest(createMockS3Event("awesome-bucket",
                        "lambda/upload/README.txt"), null));

        // A valid image file "lambda/upload/*.(jpg|png|gif)"
        assertEquals(CreateImage.RESPONSE_SUCCESS,
                f.handleRequest(createMockS3Event("awesome-bucket",
                        "lambda/upload/a.jpg"), null));

    }
}
