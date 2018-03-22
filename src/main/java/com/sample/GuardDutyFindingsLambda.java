package com.sample;


import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class GuardDutyFindingsLambda implements RequestHandler<Object, String> {
    private static ObjectMapper mapper = new ObjectMapper();
    final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();



    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String handleRequest(Object input, Context context) {
        LambdaLogger logger = context.getLogger();
        try {
            logger.log("handleRequest start");

            String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(input);
            Findings findings = mapper.readValue(jsonResult, Findings.class);

            if ("Recon:EC2/Portscan".equals(findings.detail.type)) {
                String instanceId = findings.detail.resource.instanceDetails.instanceId;
                logger.log("Terminating instance with instanceId "+ instanceId);

                StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instanceId);
                ec2.stopInstances(request);

                logger.log("Terminated instance with instanceId "+ instanceId);
            }

            logger.log("Finding is "+ findings);

            logger.log("handleRequest stop");

        } catch (IOException e) {
            e.printStackTrace();
            logger.log("Error while processing event " + input);
        }
        return "handleRequest executed";
    }


    static class Findings {
        public String id;
        public String account;
        public String time;
        public Detail detail;

        @Override
        public String toString() {
            return "Findings{" +
                    "id='" + id + '\'' +
                    ", account='" + account + '\'' +
                    ", time='" + time + '\'' +
                    ", detail=" + detail +
                    '}';
        }
    }


    static class Detail {
        public String id;
        public String arn;
        public String type;
        public Resource resource;

        @Override
        public String toString() {
            return "Detail{" +
                    "id='" + id + '\'' +
                    ", arn='" + arn + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    static class Resource {
        public InstanceDetails instanceDetails;

        @Override
        public String toString() {
            return "Resource{" +
                    "instanceDetails=" + instanceDetails +
                    '}';
        }
    }

    static class InstanceDetails {
        public String instanceId;

        @Override
        public String toString() {
            return "InstanceDetails{" +
                    "instanceId='" + instanceId + '\'' +
                    '}';
        }
    }
}
