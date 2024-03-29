package com.trifork.apoteksapp.pharmacyauthentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.message.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.*;

@SuppressWarnings("SameParameterValue")
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();

    public static void main(String[] args) throws JOSEException, IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        // Keystore configuration
        final String keyStoreFilename = "oces3_-test-_systemcertifikat.p12";
        final String keyStorePassword = "c5,PnmF8;m4I";
        final String keyStoreType = "PKCS12";

        // Authorization server configuration
        final String tokenEndpoint = "https://oidc-test.hosted.trifork.com/auth/realms/apotek/protocol/openid-connect/token";
        final String clientId = "apotek";
        final String audience = "https://oidc-test.hosted.trifork.com/auth/realms/apotek";

        // ASP Pharmacy API configuration
        final String cpr = "9191919192";
        final String pharmacyNumber = "00102";
        final String pharmacyApiEndpoint = "https://test1.apoteksapp.dk/asp/pharmacy/api/v1/pharmacy/" + pharmacyNumber + "/getuserinfo";

        // Load certificate and private key
        KeyStore keyStore = KeyStoreUtils.loadKeyStore(keyStoreFilename, keyStorePassword, keyStoreType);
        X509Certificate certificate = KeyStoreUtils.findFirstCertificate(keyStore);
        PrivateKey privateKey = KeyStoreUtils.findFirstPrivateKey(keyStore, keyStorePassword);

        // Generate client JWT
        String clientJWT = generateClientJWT(clientId, audience, certificate, privateKey);

        // Request access token and parse JSON response as a Map
        JsonNode tokenResponse = requestToken(clientId, clientJWT, tokenEndpoint);
        String accessToken = tokenResponse.get("access_token").asText();
        log.info("access_token: {}", accessToken);

        // Request ASP Pharmacy API
        JsonNode pharmacyApiResponse = requestPharmacyAPI(cpr, accessToken, pharmacyApiEndpoint);
        log.info("Pharmacy API response: \n{}", pharmacyApiResponse.toPrettyString());
    }

    private static String generateClientJWT(String clientId, String audience, X509Certificate x509Certificate, PrivateKey privateKey) throws JOSEException {
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .jwk(JWK.parse(x509Certificate))
                .build();

        Instant now = Instant.now();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(clientId)
                .subject(clientId)
                .audience(audience)
                .jwtID(UUID.randomUUID().toString())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(60)))
                .build();

        SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);
        signedJWT.sign(new RSASSASigner(privateKey));
        return signedJWT.serialize();
    }

    public static JsonNode requestToken(String clientId, String clientJWT, String tokenEndpoint) throws IOException {
        // Build request body
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("grant_type", "client_credentials"));
        form.add(new BasicNameValuePair("client_id", clientId));
        form.add(new BasicNameValuePair("client_assertion", clientJWT));
        form.add(new BasicNameValuePair("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"));

        // Build request method
        HttpPost httpPost = new HttpPost(tokenEndpoint);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.toString());
        httpPost.setEntity(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8));

        // Do request
        return executeRequest(httpPost);
    }

    public static JsonNode requestPharmacyAPI(String cpr, String accessToken, String endpoint) throws IOException {
        // Build request body
        String requestBody = "{\"cpr\": \"" + cpr + "\"}";

        // Build request method
        HttpPost httpPost = new HttpPost(endpoint);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        httpPost.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));

        // Do request
        return executeRequest(httpPost);
    }

    private static JsonNode executeRequest(HttpPost httpPost) throws IOException {
        return HTTP_CLIENT.execute(httpPost, response -> {
            if (response.getCode() >= 300) {
                log.info("Error response body: \n{}", EntityUtils.toString(response.getEntity()));
                throw new ClientProtocolException(new StatusLine(response).toString());
            }
            final HttpEntity responseEntity = response.getEntity();
            if (responseEntity == null) {
                throw new RuntimeException("Response was empty");
            }
            try (InputStream inputStream = responseEntity.getContent()) {
                return OBJECT_MAPPER.readTree(inputStream);
            }
        });
    }

    static class KeyStoreUtils {
        static KeyStore loadKeyStore(String filename, String password, String keyStoreType) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
            InputStream keystoreResource = Application.class.getClassLoader().getResourceAsStream(filename);
            if (keystoreResource == null) {
                throw new RuntimeException("Could not find keystore on classpath: " + filename);
            }
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(keystoreResource, password.toCharArray());
            return keyStore;
        }

        static X509Certificate findFirstCertificate(KeyStore keyStore) throws KeyStoreException {
            Enumeration<String> aliases = keyStore.aliases();
            if (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                return (X509Certificate) keyStore.getCertificate(alias);
            } else {
                throw new IllegalArgumentException("PKCS12 does not contain any aliases");
            }
        }

        static PrivateKey findFirstPrivateKey(KeyStore keyStore, String password) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
            Enumeration<String> aliases = keyStore.aliases();
            if (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
            } else {
                throw new IllegalArgumentException("PKCS12 does not contain any aliases");
            }
        }
    }

}
