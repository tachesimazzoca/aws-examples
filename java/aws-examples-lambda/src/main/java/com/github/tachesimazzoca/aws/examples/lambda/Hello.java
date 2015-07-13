package com.github.tachesimazzoca.aws.examples.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class Hello {
    public String echoHandler(String payload, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("received: " + payload);
        return payload;
    }
}
