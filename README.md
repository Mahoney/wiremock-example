
# How to Test a CDN (Fastly) with WireMock deployed on a PAAS (Heroku)

## Prerequisites
- [WireMock deployed to Heroku](https://github.com/Mahoney/wiremock-heroku)
- [Fastly account](https://www.fastly.com/signup)
    
## Configure Fastly Service

### Set up a Fastly Alias
    alias fastly="curl \
        -H 'Fastly-Key: <api_key>' \
        -H 'Content-Type: application/x-www-form-urlencoded' \
        -H 'Accept: application/json'"
     
### Create Fastly Service
    fastly https://api.fastly.com/service \
    -d 'name=<unique_app_name>'

### Add a domain
    fastly https://api.fastly.com/service/<service_id>/version/1/domain \
    -d 'name=<unique_app_name>.global.ssl.fastly.net'

### Add WireMock backend
    fastly https://api.fastly.com/service/<service_id>/version/1/backend \
    -d 'hostname=<unique_app_name>.herokuapp.com&name=<unique_app_name>'
    
### Activate
    fastly -X PUT https://api.fastly.com/service/<service_id>/version/1/activate
    
## Configure heroku with the new domain name
    heroku domains:add <unique_app_name>.global.ssl.fastly.net
    
## Test it
    curl https://<unique_app_name>.global.ssl.fastly.net/__admin/

## Fastly things to check
    
Condition:

    req.request ~ "PURGE" && req.http.Authorization != "Bearer very_secret"
