# ASP Pharmacy API

## OpenAPI Definition

See the YAML file: [asp-pharmacy-api.yml](asp-pharmacy-api.yml)

## Documentation Index

[Security Scheme](SECURITY-SCHEME.md): Technical details about the API authentication security scheme.\
[Test Guide](TEST-GUIDE.md): Instructions for how to test the Pharmacy API.\
[Business Concepts and Flow](BUSINESS-FLOW.md): Descriptions of the business side of the API.

## Environments

### test1

The recommended test environment for pharmacy system providers.\
The Pharmacy API is fully implemented and returns actual test data.

API Endpoint:\
https://test1.apoteksapp.dk/asp/pharmacy

Authorization server OpenID Connect Discovery endpoint:\
https://oidc-test.hosted.trifork.com/auth/realms/apotek/.well-known/openid-configuration

### prod

Production environment.

API Endpoint:\
https://prod.apoteksapp.dk/asp/pharmacy

Authorization server OpenID Connect Discovery endpoint:\
https://login.apoteksapp.dk/auth/realms/apotek/.well-known/openid-configuration

