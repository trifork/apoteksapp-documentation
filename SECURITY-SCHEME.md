# Pharmacy API on ASP - Security Scheme

The security scheme is based on [OAuth 2.0 Client Credentials Grant](https://tools.ietf.org/html/rfc6749#section-4.4) flow using [JWT for Client Authentication](https://tools.ietf.org/html/rfc7523#section-2.2). \
The client JWT must be signed using a [VOCES- or FOCES-certificate](https://www.nemid.nu/dk-da/om-nemid/historien_om_nemid/oces-standarden/oces-certifikatpolitikker/). \
Request and response examples of the communication with the authorization server (Trifork Identity Manager) can be found below. \
A Java-based demo client is available in this repository inside the subdirectory [/pharmacy-oauth2-authentication-demo](./pharmacy-oauth2-authentication-demo).

## Examples

Request and response examples.

### Client JWT

```
Header:
{
  "alg": "RS256",
  "jwk": { ... }
}

Payload:
{
  "sub": "apotek",
  "aud": "https://oidc-test.hosted.trifork.com/auth/realms/apotek",
  "iss": "apotek",
  "exp": 1602599819,
  "iat": 1602599759,
  "jti": "2ccb8753-01c7-462e-8266-8a562f714623"
}
```

### Token request

```
POST /auth/realms/apotek/protocol/openid-connect/token HTTP/1.1
Host: oidc-test.hosted.trifork.com
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials
client_id=apotek
client_assertion=...
client_assertion_type=urn:ietf:params:oauth:client-assertion-type:jwt-bearer
```

### Token response

```
HTTP/1.1 200 OK
Content-Type: application/json

{
  "access_token": "...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "...",
  "token_type": "bearer",
  "not-before-policy": 0,
  "session_state": "b782bf14-6085-478d-b974-a872e1462919",
  "scope": ""
}
```

### Pharmacy API request

```
POST /asp/pharmacy/api/v1/pharmacy/003/getuserinfo HTTP/1.1
Host: test1.apoteksapp.dk
Content-Type: application/json; charset=UTF-8
Authorization: Bearer ...

{"cpr": "0101010101", "pharmacyNumber": "003"}
```

### Pharmacy API response

```
HTTP/1.1 200 OK
Content-Type: application/json

{ ... }
```

