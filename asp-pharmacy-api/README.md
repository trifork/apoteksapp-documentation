# ASP Pharmacy API

OpenAPI definition: [asp-pharmacy-api.yml](asp-pharmacy-api.yml)

## Environments and Current Status as of 27-11-2020

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

## Security Scheme

Further documentation can be found [in this document](SECURITY-SCHEME.md).

## Test Guide

Instructions for how to test the Pharmacy API can be found [in this document](TEST-GUIDE.md).
