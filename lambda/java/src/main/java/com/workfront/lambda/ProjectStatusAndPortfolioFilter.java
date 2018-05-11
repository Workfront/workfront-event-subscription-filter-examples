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

public class ProjectStatusAndPortfolioFilter implements RequestHandler<Map<String, Object>, String> {

    private static final String DESIRED_PORTFOLIO_ID = "Aip6o8CEai3HN4ev1oSBFjvvv6sWdWme";
    private static final String CURRENT_PROJECT_STATUS = "CUR";

    private Gson jsonParser = new Gson();

    public String handleRequest(Map<String, Object> webHookPayload, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Event Subscription Message = " + webHookPayload);

        Map<String, Object> newState = getNewStateFromWebHookPayload(webHookPayload);

        if (newProjectStatusIsCurrent(newState) && projectNowBelongsToTheDesiredPortfolio(newState)) {

            logger.log("Project status is CURRENT and project belongs to the desired portfolio");

            AWSLambda client = AWSLambdaClientBuilder.standard().build(); //this should be a class level field
            InvokeRequest request = new InvokeRequest()
                    .withFunctionName("forwardMessageOntoMyEndpoint")
                    .withInvocationType("Event")
                    .withLogType("Tail")
                    .withPayload(jsonParser.toJson(webHookPayload));
            InvokeResult response = client.invoke(request);
        }
        return "";
    }

    private static boolean newProjectStatusIsCurrent(Map<String, Object> newState) {
        return !isObjectEmpty(newState) && newState.get("status").equals(CURRENT_PROJECT_STATUS);
    }

    private static boolean isObjectEmpty(Map<String, Object> obj) {
        return obj == null || obj.keySet().isEmpty();
    }

    private static boolean projectNowBelongsToTheDesiredPortfolio(Map<String, Object> newState) {
        return (!isObjectEmpty(newState)) && DESIRED_PORTFOLIO_ID.equals(newState.get("portfolioID"));
    }

    private static Map<String, Object> getNewStateFromWebHookPayload(Map<String, Object> webHookPayload){
        return (Map<String, Object>) webHookPayload.get("newState");
    }

}
