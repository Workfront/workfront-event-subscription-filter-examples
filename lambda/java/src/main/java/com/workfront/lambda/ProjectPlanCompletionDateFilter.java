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

public class ProjectPlanCompletionDateFilter implements RequestHandler<Map<String, Object>, String> {

    private static final String CURRENT_PROJECT_STATUS = "CUR";
    private static final String PLANNED_COMPLETION_DATE = "plannedCompletionDate";

    private Gson jsonParser = new Gson();

    public String handleRequest(Map<String, Object> webHookPayload, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Event Subscription Message = " + webHookPayload);

        if (projectStatusIsCurrent(webHookPayload) && projectPlannedCompletionDateHasChanged(webHookPayload)) {
            logger.log("Processing Event Subscription message because project plan completion has changed.");

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

    private static boolean projectStatusIsCurrent(Map<String, Object> webHookPayload) {
        Map<String, Object> newState = getWebHookPayloadNewState(webHookPayload);
        return newState != null && CURRENT_PROJECT_STATUS.equals(newState.get("status"));
    }

    private static boolean projectPlannedCompletionDateHasChanged(Map<String, Object> webHookPayload) {
        boolean projectPlannedCompletionDateChanged = false;

        Map<String, Object> newState = getWebHookPayloadNewState(webHookPayload);
        Map<String, Object> oldState = getWebHookPayloadOldState(webHookPayload);
        
        if (!isObjectEmpty(newState) && !isObjectEmpty(oldState)){
            Object oldPlannedCompletionDate = oldState.get(PLANNED_COMPLETION_DATE);

            Object newPlannedCompletionDate = newState.get(PLANNED_COMPLETION_DATE);

            projectPlannedCompletionDateChanged = newPlannedCompletionDate != null && !newPlannedCompletionDate.equals(oldPlannedCompletionDate);
        }
        return projectPlannedCompletionDateChanged;
    }

    private static boolean isObjectEmpty(Map<String, Object> obj) {
        return obj == null || obj.keySet().isEmpty();
    }

    private static Map<String, Object> getWebHookPayloadNewState(Map<String, Object> webHookPayload){
        return (Map<String, Object>) webHookPayload.get("newState");
    }

    private static Map<String, Object> getWebHookPayloadOldState(Map<String, Object> webHookPayload){
        return (Map<String, Object>) webHookPayload.get("oldState");
    }
}
