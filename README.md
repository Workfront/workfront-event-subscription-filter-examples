# Workfront Event Subscription Filter Examples

## Understanding these Examples
These examples are meant for those attempting to filter their Event Subscription messages.
There are three examples all built in Java, Python, and Node.js. 
The examples illustrate three filtering scenarios for filtering Project resources based on their:

- Group ID
- Completion Date
- Status and Portfolio ID

These examples are written with the intent of deploying them as AWS Lambdas, although it is possible, and quite simple, to convert them to simple Java, Python, or Node.js native implementations. 

These examples cover the following use-cases which highlight some potential things to do with your messages once they've been filtered. 

- Deliver the message to a hosted endpoint you own
- Invoke another AWS Lambda for further filtering or processing

For a full-detail article on these examples please visit the [Filtering Event Subscription Messages](https://support.workfront.com/hc/en-us/articles/360003166274-Filtering-Event-Subscription-Messages) help article. 

## Disclaimer
These examples are **not** meant to be used as-is. 
They are meant to serve as guidance for those building more elaborate and complex Event Subscription message filters.
The examples will compile and run but they are just that, examples, and as such are not production ready implementations.  