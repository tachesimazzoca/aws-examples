package com.github.tachesimazzoca.aws.examples.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification;
import org.apache.commons.io.FilenameUtils;

public class CreateThumbnail implements RequestHandler<S3Event, String> {
    public static final String RESPONSE_SUCCESS = "OK";
    public static final String RESPONSE_SKIPPED = "SKIPPED";
    public static final String RESPONSE_FAILURE = "NG";

    private static final String BASE_DIRECTORY = "lambda/images/";

    @Override
    public String handleRequest(S3Event event, Context context) {
        S3EventNotification.S3EventNotificationRecord record = event.getRecords().get(0);
        String bucketName = record.getS3().getBucket().getName();
        String objectKey = record.getS3().getObject().getKey();
        if (!objectKey.startsWith(BASE_DIRECTORY))
            return RESPONSE_SKIPPED;

        ImageFormat format;
        try {
            format = ImageFormat.fromExtension(
                    FilenameUtils.getExtension(objectKey));
        } catch (IllegalArgumentException e) {
            // Not an image file
            return RESPONSE_SKIPPED;
        }

        System.out.println("objectKey: " + objectKey);
        System.out.println("format: " + format);
        return RESPONSE_SUCCESS;
    }

    public enum ImageFormat {
        JPEG("jpeg", "image/jpeg", "jpg"),
        PNG("png", "image/png", "png"),
        GIF("gif", "image/gif", "gif");

        private final String formatName;
        private final String contentType;
        private final String[] extensions;

        private ImageFormat(String formatName,
                            String contentType,
                            String... extensions) {
            this.formatName = formatName;
            this.contentType = contentType;
            this.extensions = extensions;
        }

        public String getFormatName() {
            return formatName;
        }

        public String getContentType() {
            return contentType;
        }

        public static ImageFormat fromExtension(String extension)
                throws IllegalArgumentException {
            for (ImageFormat v : ImageFormat.values()) {
                for (int i = 0; i < v.extensions.length; i++) {
                    if (v.extensions[i].equals(extension))
                        return v;
                }
            }
            throw new IllegalArgumentException("unknown extension: " + extension);
        }
    }
}
