    package com.sample;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class HelloLambda implements RequestHandler<Object, String> {

    public String handleRequest(Object input, Context context) {
        context.getLogger().log("handleRequest start");

        context.getLogger().log("Input: " + input);


        context.getLogger().log("handleRequest stop");
        return "Hello from Lambda";
    }
}
