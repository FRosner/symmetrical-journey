# symmetrical-journey

## Description

A service that allows a user to upload an asset and then
request a time expiring URL to retrieve that asset.

## Usage

- Make sure you have valid [AWS credentials configured](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html)
- `./gradlew bootRun`
- `curl -XPOST 'localhost:8080/assets'`
- `curl -v -T README.md -H 'Content-Type: text/plain' '<presignedPutUrl>'`
- `curl -XPUT -d 'uploaded' -H 'Content-Type: text/plain' 'localhost:8080/assets/<assetId>/status'`
- `curl -XGET 'localhost:8080/assets/<assetId>?timeout=60'`
- `curl -XGET '<presignedGetUrl>'`

## Is it production ready?

The answer to this question depends on what it means to be production ready. This software **can** definitely run in production. I think it is important to understand the quality requirements and see if the software satisfies them and adjust as needed.

What you could do is to setup a meeting with the stakeholders, the dev team, the architect (if this role exists) to understand the quality requirements that you want to prioritize (e.g. using a [mini-quality attribute workshop](https://dev.to/frosnerd/quality-attributes-in-software-1ha9)). You could then use the output to derive service level objectives (SLOs).

If this service needs to go to the market quickly and we have a set of friendly customers, we might have a bigger error budget than for a feature that goes out to a global customer base with high expectations. Based on the SLOs one should certainly setup some monitoring in order to detect SLO violations and react to it with an increased prioritization of engineering work towards reliability, for example.

Sorry for the "it depends" answer :D Nevertheless, please find some bullet points for changes that would certainly improve the quality of the code.

## Possible quality improvements

- Pact / OpenAPI
- IaC + deployment automation (CFN, Terraform, build pipeline etc.)
- More integration / smoke tests
- Metrics and alarming
- Performance / load tests, regression tests
- Improve package structure and name
- Use enum for asset status
- Use TTL for storing the asset statuses?
- Stateless (by using an external DB or cache) so the service can scale without the need for internal coordination + can survive restarts
- Proper logging
- Error handling
- Proper configuration
- Splitting up the huge controller into reusable components with separation of concerns
- Use more specific AWS SDK modules to avoid importing the world
- Inject `s3Presigner` to reuse it across the application
- Content type as request parameter
- Put signature duration configurable
- Better test coverage
- Static code analysis / quality checks / linting
- Authentication and authorization (e.g. only mark your own assets as uploaded)
- Only allow status updates for existing assets (rn this is not handled)

## Remarks

- Task 2 requests that the user is able to make a POST call to the S3 URL to upload a file. This is not possible unfortunately as objects can only be uploaded via PUT.
