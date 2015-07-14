package com.github.tachesimazzoca.aws.examples.lambda;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class AmazonServiceFactoryImpl implements AmazonServiceFactory {
    @Override
    public AmazonS3 createAmazonS3Client() {
        return new AmazonS3Client();
    }
}
