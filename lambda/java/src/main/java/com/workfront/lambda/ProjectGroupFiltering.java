package com.workfront.lambda;

/*
Copyright 2018 Workfront

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;

import java.util.Map;

public class ProjectGroupFiltering implements RequestHandler<Map<String, Object>, String> {

    private static final String DESIRED_GROUP_ID = "VaqTTVaB0UcbPu4n6824WIYYIV953Mg3";

    private Gson jsonParser = new Gson();

    public String handleRequest(Map<String, Object> webHookPayload, Context context) {

        LambdaLogger logger = context.getLogger();
        logger.log("Event Subscription Message = " + webHookPayload);

        Map<String, Object> newState = (Map<String, Object>) webHookPayload.get("newState");

        String projectGroupId = (String) newState.get("groupID");

        logger.log("String projectGroupID is - " + projectGroupId);

        if (DESIRED_GROUP_ID.equals(projectGroupId)) {
            //process the message
            logger.log("Processing Event Subscription message matching groupId " + DESIRED_GROUP_ID + "...");

            AWSLambda client = AWSLambdaClientBuilder.standard().build();
            InvokeRequest request = new InvokeRequest()
                    .withFunctionName("forwardMessageOntoMyEndpoint")
                    .withInvocationType("Event")
                    .withLogType("Tail")
                    .withPayload(jsonParser.toJson(webHookPayload));
            InvokeResult response = client.invoke(request);
        }

        return "";
    }
}