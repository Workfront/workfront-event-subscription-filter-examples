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

CURRENT_PROJECT_STATUS = 'CUR'


def project_plan_completion_date_changed(event_subscription_message):
    new_state = json.loads(event_subscription_message['newState'])
    old_state = json.loads(event_subscription_message['oldState'])

    if new_state is None or not new_state:
        project_plan_completion_date_was_changed = False
    elif old_state is not None:
        new_plan_completion_date = new_state['planCompletionDate']
        old_plan_completion_date = old_state['planCompletionDate']
        project_plan_completion_date_was_changed = new_plan_completion_date == old_plan_completion_date
    else:
        project_plan_completion_date_was_changed = False

    return project_plan_completion_date_was_changed


def process_message(event_subscription_message):
    aws_lambda.invoke(
        FunctionName='nextFunctionToProcessMessage',
        InvocationType='Event',
        LogType='None',
        Payload=event_subscription_message
    )


def handler(event, context):
    event_subscription_message = json.loads(event['body'])

    if event_subscription_message['status'] == CURRENT_PROJECT_STATUS and project_plan_completion_date_changed(
            event_subscription_message):
        process_message(event_subscription_message)

    return {
        'statusCode': 200
    }
