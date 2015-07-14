package com.github.tachesimazzoca.aws.examples.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.github.tachesimazzoca.aws.examples.lambda.util.ImageUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URLDecoder;

public class CreateImage implements RequestHandler<S3Event, String> {
    public static final int OUTPUT_WIDTH = 100;
    public static final int OUTPUT_HEIGHT = 100;
    public static final String OUTPUT_FORMAT = null;

    public static final String RESPONSE_SUCCESS = "OK";
    public static final String RESPONSE_SKIPPED = "SKIPPED";
    public static final String RESPONSE_FAILURE = "NG";

    private static final String UPLOAD_DIRECTORY = "lambda/upload/";
    private static final String OUTPUT_DIRECTORY = "lambda/images/";

    private final AmazonServiceFactory amazonServiceFactory;
    private final AmazonS3StreamConverter amazonS3StreamConverter;

    public CreateImage() {
        amazonServiceFactory = new AmazonServiceFactoryImpl();
        amazonS3StreamConverter = new AmazonS3StreamConverterImpl();
    }

    public CreateImage(
            AmazonServiceFactory amazonServiceFactory,
            AmazonS3StreamConverter amazonS3StreamConverter) {
        this.amazonServiceFactory = amazonServiceFactory;
        this.amazonS3StreamConverter = amazonS3StreamConverter;
    }

    @Override
    public String handleRequest(S3Event event, Context context) {
        try {
            S3EventNotification.S3EventNotificationRecord record = event.getRecords().get(0);
            String bucketName = record.getS3().getBucket().getName();
            String srcKey = URLDecoder.decode(
                    record.getS3().getObject().getKey(), "UTF-8");

            if (!srcKey.startsWith(UPLOAD_DIRECTORY))
                return RESPONSE_SKIPPED;

            String basename = FilenameUtils
                    .removeExtension(srcKey)
                    .substring(UPLOAD_DIRECTORY.length());
            if (basename.isEmpty()) {
                return RESPONSE_SKIPPED;
            }

            ImageFormat format;
            try {
                format = ImageFormat.fromExtension(
                        FilenameUtils.getExtension(srcKey));
            } catch (IllegalArgumentException e) {
                // Not an image file
                return RESPONSE_SKIPPED;
            }

            AmazonS3 s3Client = amazonServiceFactory.createAmazonS3Client();
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(
                    bucketName, srcKey));

            InputStream input = amazonS3StreamConverter.convertToInputStream(
                    s3Object.getObjectContent());
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageUtils.convert(input, output, OUTPUT_WIDTH, OUTPUT_HEIGHT, OUTPUT_FORMAT);

            InputStream thumbnail = new ByteArrayInputStream(output.toByteArray());
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType(format.getContentType());

            String destKey = OUTPUT_DIRECTORY + basename + format.getExtension();
            s3Client.putObject(bucketName, destKey, thumbnail, meta);

            System.out.println("format: " + format);
            System.out.println("srcKey: " + srcKey);
            System.out.println("destKey: " + destKey);

            return RESPONSE_SUCCESS;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return RESPONSE_FAILURE;

        } catch (IOException e) {
            e.printStackTrace();
            return RESPONSE_FAILURE;
        }
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

        public String getExtension() {
            return extensions[0];
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
