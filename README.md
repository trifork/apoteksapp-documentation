# Apoteksapp Public Documentation

This repository contains the public documentation for **Apotekernes Service Platform (ASP)**.

## Pharmacy API on ASP
 
OpenAPI definition: [asp-provided-api.yml](asp-provided-api.yml) (view version currently deployed on [ASP test1](https://test1.apoteksapp.dk/asp/pharmacy/swagger-ui/))

### Environments and Current Status as of 10-11-2020

#### test1

Endpoint: https://test1.apoteksapp.dk/asp/pharmacy

Currently the only test environment.\
The Pharmacy API now returns actual data (as opposed to mock data).

#### test2

Endpoint: Will be available soon.

A more stable test environment dedicated to testing the Pharmacy API.

#### prod

Endpoint: Will be available soon.

Production environment.

### Security Scheme

Further documentation can be found [in this document](./SECURITY-SCHEME.md).

### Test Guide

Instructions for how to test the Pharmacy API can be found [in this document](./TEST-GUIDE.md). 

## API on pharmacy systems

Suggested OpenAPI definition for pharmacy systems: [pharmacy-provided-api.yml](pharmacy-provided-api.yml)

