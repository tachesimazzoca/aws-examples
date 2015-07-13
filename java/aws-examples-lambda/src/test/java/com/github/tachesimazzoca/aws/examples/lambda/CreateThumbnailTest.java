package com.github.tachesimazzoca.aws.examples.lambda;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3BucketEntity;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.event.S3EventNotification.S3ObjectEntity;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateThumbnailTest {
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
        CreateThumbnail f = new CreateThumbnail();

        // A valid image file "lambda/images/*.(jpg|png|gif)"
        assertEquals(CreateThumbnail.RESPONSE_SUCCESS,
                f.handleRequest(createMockS3Event("awesome-bucket",
                        "lambda/images/a.jpg"), null));

        // Not in the directory "lambda/images/*"
        assertEquals(CreateThumbnail.RESPONSE_SKIPPED,
                f.handleRequest(createMockS3Event("awesome-bucket",
                        "a.jpg"), null));
        assertEquals(CreateThumbnail.RESPONSE_SKIPPED,
                f.handleRequest(createMockS3Event("awesome-bucket",
                        "emr/images/a.jpg"), null));

        // Not an image file (by extension)
        assertEquals(CreateThumbnail.RESPONSE_SKIPPED,
                f.handleRequest(createMockS3Event("awesome-bucket",
                        "emr/images/README.txt"), null));
    }
}
