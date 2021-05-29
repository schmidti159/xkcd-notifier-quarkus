# xkcd-notifier-quarkus

This is a small project to learn quarkus and aws lambda.

It will check the [xkcd.com](https://xkcd.com) feed and send a mail to the passed payload 
if there is new comic for today.

## Status
Work in Progress

## Possible extensions
- Read mail addresses dynamically
- Add functions to subscribe/unsubscribe with new mail addresses
- Build and deploy automatically with GitHub actions

## Packaging and running the application

See the official [Quarkus guide for Amazon Lambda](https://quarkus.io/guides/amazon-lambda)

Package as native image and push to aws
```shell script
./mvnw clean package -Pnative -Dquarkus.native.container-build=true
target/manage.sh native update
```

## AWS Lambda Config
Following environment variables must be configured to be able to send mails:
- MAIL_HOST
- MAIL_PORT
- MAIL_USER
- MAIL_PASSWORD

Payload to call this function with:
```json
{
  "mailAddresses": [
    "foo@example.com",
    "bar@example.com"
  ]
}
```