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
const DESIRED_GROUP_ID = 'VaqTTVaB0UcbPu4n6824WIYYIV953Mg3';
const PROJECT_GROUP_FILTER_TIMER_NAME = 'myProjectGroupFilterHandler';
const AWS_API_VERSION = "2015-03-31";

const AWS = require('aws-sdk');
const lambda = new AWS.Lambda({apiVersion: AWS_API_VERSION});

function endLambdaRequest(context) {
    console.timeEnd(PROJECT_GROUP_FILTER_TIMER_NAME);
    context.succeed({
        statusCode: 200
    });
}

function forwardMessageOntoMyEndpoint(eventSubscriptionMessage, context) {
    let lambdaParams = {
        FunctionName: 'forwardMessageOntoMyEndpoint',
        InvocationType: 'Event',
        LogType: 'None',
        Payload: new Buffer(JSON.stringify(eventSubscriptionMessage))
    };

    lambda.invoke(lambdaParams, function (err, data) {
        if (err) {
            console.error(err, err.stack);
        } else {
            console.log('data = ' + data);
        }
        endLambdaRequest(context);
    });
}

exports.myProjectGroupFilterHandler = function (event, context) {
    console.time(PROJECT_GROUP_FILTER_TIMER_NAME);
    console.log('event = ' + JSON.stringify(event));
    let eventSubscriptionMessage = JSON.parse(event.body);

    console.log('ESM = ' + JSON.stringify(eventSubscriptionMessage));

    let projectGroupId = eventSubscriptionMessage.newState.groupID;

    console.log('projectGroupId = ' + projectGroupId);

    if (projectGroupId === DESIRED_GROUP_ID) {
        // Process the message
        console.log('Processing Event Subscription message matching groupId ' + DESIRED_GROUP_ID + '...');
        forwardMessageOntoMyEndpoint(eventSubscriptionMessage, context);
    } else {
        endLambdaRequest(context);
    }
};