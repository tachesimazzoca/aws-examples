package com.github.tachesimazzoca.aws.examples.lambda;

import com.amazonaws.services.s3.AmazonS3;

public interface AmazonServiceFactory {
    AmazonS3 createAmazonS3Client();
}
