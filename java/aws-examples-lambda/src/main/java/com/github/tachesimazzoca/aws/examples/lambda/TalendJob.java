package com.github.tachesimazzoca.aws.examples.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import sandbox.a_basic_job_0_1.A_Basic_Job;

public class TalendJob implements RequestHandler<S3Event, String> {
    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        String[] args = new String[]{};
        A_Basic_Job job = new A_Basic_Job();
        return String.format("exitCode: %d", job.runJobInTOS(args));
    }
}
