# ASP Pharmacy API - OAuth 2.0 Authentication Demo Client

This directory contains a demo client that fetches an access token from Trifork Identity Manager.\
The client then uses this access token to make an API request at the ASP Pharmacy API.\
The client logs the output of both requests to console.

## Howto

### 1. Build

The code requires at least JDK 17 and must be built using Maven (wrapper is included).

```
$ ./mvnw clean package
```

### 2. Run

The packaged JAR-file is ready to be run.

```
$ java -jar target/pharmacy-oauth2-authentication-demo-*.jar
```


## Configuration

If the cert ever runs out make a new one on https://testportal.test-devtest4-nemlog-in.dk/BO
NOTE: The certificate must be issued to a cvr with a matching pharmacy. Currently the demo client is configured to use
the test certificate with CVR 91693788, which corresponds to the test pharmacy 00102.
The user for organisation 91693788 in Ehvervsstyrelsen: Tapio2040