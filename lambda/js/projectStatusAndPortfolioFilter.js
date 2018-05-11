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
const DESIRED_PORTFOLIO_ID = 'Aip6o8CEai3HN4ev1oSBFjvvv6sWdWme';
const CURRENT_PROJECT_STATUS = 'CUR';
const AWS_API_VERSION = "2015-03-31";
const PROJECT_OWNER_FILTER_TIMER_NAME = 'myProjectOwnerFilterHandler';

const AWS = require('aws-sdk');
const lambda = AWS.Lambda({apiVersion: AWS_API_VERSION});

function projectStatusIsCurrent(eventSubscriptionMessage) {
    return eventSubscriptionMessage.newState.status === CURRENT_PROJECT_STATUS;
}

function isObjectEmpty(obj) {
    return !obj || !Object.keys(obj).length;
}

function projectBelongsToTheDesiredPortfolio(eventSubscriptionMessage) {
    let newState = eventSubscriptionMessage.newState;
    return (!isObjectEmpty(newState)) && DESIRED_PORTFOLIO_ID === newState.portfolioID;
}

function endLambdaRequest(context) {
    console.timeEnd(PROJECT_OWNER_FILTER_TIMER_NAME);
    context.succeed({
        statusCode: 200
    });
}

function getOwnerFilterLambdaParams(eventSubscriptionMessage) {
    return {
        FunctionName: 'ownerFilter',
        InvocationType: 'Event',
        LogType: 'None',
        Payload: new Buffer(JSON.stringify(eventSubscriptionMessage))
    };
}

exports.myProjectOwnerFilterHandler = function (event, context) {
    console.time(PROJECT_OWNER_FILTER_TIMER_NAME);
    let eventSubscriptionMessage = JSON.parse(event.body);

    console.log('Event Body = ' + event.body);

    if (projectStatusIsCurrent(eventSubscriptionMessage) && projectBelongsToTheDesiredPortfolio(eventSubscriptionMessage)) {
        console.log('Project status is CURRENT and project belongs to the desired portfolio');
        lambda.invoke(getOwnerFilterLambdaParams(eventSubscriptionMessage), function (err, data) {
            if (err) {
                console.error(err, err.stack);
            } else {
                console.log('data = ' + data);
            }
        });
    }
    endLambdaRequest(context);
};