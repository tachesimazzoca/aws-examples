package com.github.tachesimazzoca.aws.examples.lambda;

import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.InputStream;

public class AmazonS3StreamConverterImpl implements AmazonS3StreamConverter {
    @Override
    public InputStream convertToInputStream(S3ObjectInputStream input) {
        return input;
    }
}
