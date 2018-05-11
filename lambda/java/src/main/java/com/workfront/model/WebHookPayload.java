package com.workfront.model;

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

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public class WebHookPayload implements Serializable {

    private final String eventType;
    private final String subscriptionId;
    private final Instant eventTime;
    private final Map<String, Object> newState;
    private final Map<String, Object> oldState;

    public WebHookPayload(String subscriptionId, String eventType, Map<String, Object> newState, Map<String, Object> oldState, Instant eventTime) {
        this.eventType = eventType;
        this.subscriptionId = subscriptionId;
        this.newState = newState;
        this.oldState = oldState;
        this.eventTime = eventTime;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public Map<String, Object> getNewState() {
        return newState;
    }

    public Map<String, Object> getOldState() {
        return oldState;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    @Override
    public String toString() {
        return "{\n	\"eventType\":\"" + eventType + "\"\n, \"subscriptionId\":\"" + subscriptionId + "\"\n,	\"eventTime\":\"" + eventTime +
                "\"\n,	\"newState\":\"" + newState + "\"\n,	\"oldState\":\"" + oldState + "\"\n}";
    }

}