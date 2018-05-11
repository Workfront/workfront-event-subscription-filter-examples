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
const PROJECT_OWNER_FILTER_HANDLER_TIMER_NAME = 'myProjectOwnerFilterHandler';
const CURRENT_PROJECT_STATUS = 'CUR';
const AWS_API_VERSION = "2015-03-31";

const AWS = require('aws-sdk');
const lambda = AWS.Lambda({apiVersion: AWS_API_VERSION});

function projectStatusIsCurrent(eventSubscriptionMessage) {
    return eventSubscriptionMessage.newState.status === CURRENT_PROJECT_STATUS;
}

function isObjectEmpty(obj) {
    return !obj || !Object.keys(obj).length;
}

function projectPlannedCompletionDateHasChanged(eventSubscriptionMessage) {
    let projectPlannedCompletionDateChanged;

    let newState = eventSubscriptionMessage.newState;
    let oldState = eventSubscriptionMessage.oldState;

    if (isObjectEmpty(newState)) {
        projectPlannedCompletionDateChanged = false;
    } else if (!isObjectEmpty(oldState)) {
        let oldPlannedCompletionDate = oldState.plannedCompletionDate;
        let newPlannedCompletionDate = newState.plannedCompletionDate;
        projectPlannedCompletionDateChanged = newPlannedCompletionDate !== oldPlannedCompletionDate;
    } else {
        projectPlannedCompletionDateChanged = false;
    }

    return projectPlannedCompletionDateChanged;
}

function endLambdaRequest(context) {
    console.timeEnd(PROJECT_OWNER_FILTER_HANDLER_TIMER_NAME);
    context.succeed({
        statusCode: 200
    });
}

function getPlanCompletionDateChangeIssueLambdaParams(eventSubscriptionMessage) {
    return {
        FunctionName: 'assignPlanCompletionDateChangeIssueToProjectOwner',
        InvocationType: 'Event',
        LogType: 'None',
        Payload: new Buffer(JSON.stringify(eventSubscriptionMessage))
    };
}

exports.myProjectOwnerFilterHandler = function (event, context) {
    console.time(PROJECT_OWNER_FILTER_HANDLER_TIMER_NAME);
    let eventSubscriptionMessage = JSON.parse(event.body);

    if (projectStatusIsCurrent(eventSubscriptionMessage) && projectPlannedCompletionDateHasChanged(eventSubscriptionMessage)) {
        // Process the message
        console.log('Processing Event Subscription message because the project plan completion has changed.');
        lambda.invoke(getPlanCompletionDateChangeIssueLambdaParams(eventSubscriptionMessage), function (err, data) {
            if (err) {
                console.error(err, err.stack);
            } else {
                console.log('data = ' + data);
            }
        });
    }
    endLambdaRequest(context);
};