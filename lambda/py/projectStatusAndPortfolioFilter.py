# Copyright 2018 Workfront
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
import json
import boto3

aws_lambda = boto3.client('lambda')

DESIRED_PORTFOLIO_ID = 'Aip6o8CEai3HN4ev1oSBFjvvv6sWdWme'
CURRENT_PROJECT_STATUS = 'CUR'


def process_message(event_subscription_message):
    aws_lambda.invoke(
        FunctionName='nextFunctionToProcessMessage',
        InvocationType='Event',
        LogType='None',
        Payload=event_subscription_message
    )


def project_status_and_portfolio_filter_handler(event, context):
    event_subscription_message = json.loads(event['body'])

    new_state = json.loads(event_subscription_message['newState'])

    if new_state['status'] == CURRENT_PROJECT_STATUS and new_state['portfolioID'] == DESIRED_PORTFOLIO_ID:
        print('Project status is current and its portfolio matched the desired portfolio.')
        process_message(event_subscription_message)

    return {
        'statusCode': 200
    }
