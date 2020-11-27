# Pharmacy API on ASP - Test Guide

Things to note when testing the Pharmacy API.

## Environments

All ASP test environments use the following external environments:

- FMK: test2
- NSP: test2

## Data Access and Authorization

When obtaining an access token, an OCES certificate must be provided, and the OCES certificate will always contain a
specific CVR number. This CVR number is used for authorization when accessing the Pharmacy API, and the CVR number must
correspond to the `pharmacyNumber` parameter supplied in the requests. Otherwise an HTTP 403 Forbidden will be returned.

This imposes a challenge when trying to test the API, as you must be in possession of a specific pharmacy's (test) OCES
certificate. To address this issue, the test environment contains a number of test pharmacies which are both available
in the app and in the Pharmacy API. This means that if you are in possession of a test certificate issued to one of the
CVR numbers in the test data, you will be able to use the corresponding pharmacy for testing. See the following section
"Test Data".

Note that an app user must have the corresponding pharmacy as its favourite pharmacy for data about that user to be
visible in the API.

## Test Data

### Pharmacies

Note that the following specified EAN-numbers are referring to real pharmacies. EAN-numbers are used when placing orders in
FMK (Fælles Medicinkort), and since FMK's test environment does not contain these test-pharmacies, we instead provide the
EAN-number of a real pharmacy to FMK.

Name: Trifork Apotek\
Pharmacy Number: 99901\
CVR: 25520041\
EAN (a real pharmacy!): 5790000170036\
Pharmacy System: PharmaNet

Name: Nets Apotek\
Pharmacy Number: 00101\
CVR: 30808460\
EAN (a real pharmacy!): 5790000170012\
Pharmacy System: PharmaNet

Name: NNIT Apotek\
Pharmacy Number: 84501\
CVR: 21093106\
EAN (a real pharmacy!): 5790000170029\
Pharmacy System: PharmaNet

Name: Cito Apotek\
Pharmacy Number: 99801\
CVR: 16724041\
EAN (a real pharmacy!): 5790000172535\
Pharmacy System: Citosys

### App Users

It is possible to use the app with any user/CPR number that are created in Dynamisk Testdata Generator (DTG) at National
Service Platform (NSP).
