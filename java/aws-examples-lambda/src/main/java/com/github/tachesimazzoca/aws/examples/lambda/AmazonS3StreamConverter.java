package com.github.tachesimazzoca.aws.examples.lambda;

import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.IOException;
import java.io.InputStream;

public interface AmazonS3StreamConverter {
    InputStream convertToInputStream(S3ObjectInputStream input) throws IOException;
}
