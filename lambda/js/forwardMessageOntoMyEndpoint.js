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
const https = require('https');

const MY_ENDPOINT_TIMER_NAME = 'myEndpointHandler';

function forwardMessageOntoMyEndpoint(eventSubscriptionMessage, context) {
    console.log('Starting request...');
    let request = https.request(generateMyEndpointHttpsOptions(), function (response) {
        response.on('data', function (data) {
            console.log('data = ' + data);
            endLambdaRequest(context);
        });
    });

    request.on('error', function (e) {
        console.error(e);
        endLambdaRequest(context);
    });

    request.write(JSON.stringify(eventSubscriptionMessage));
    console.log('Ending request...');
    request.end();
}

function generateMyEndpointHttpsOptions() {
    return {
        hostname: '6c8zs0gxu6.execute-api.us-east-2.amazonaws.com',
        port: 443,
        path: '/dev/simplebin/1842556bb9ed47e5a5a9f0ff24530aa5',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    };
}

function endLambdaRequest(context) {
    console.timeEnd(MY_ENDPOINT_TIMER_NAME);
    context.succeed({
        statusCode: 200
    });
}

exports.myEndpointHandler = function (event, context) {
    console.time(MY_ENDPOINT_TIMER_NAME);
    console.log('event = ' + event);
    forwardMessageOntoMyEndpoint(event, context);
};