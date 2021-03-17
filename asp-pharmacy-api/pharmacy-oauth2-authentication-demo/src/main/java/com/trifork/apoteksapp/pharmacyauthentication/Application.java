package com.trifork.apoteksapp.pharmacyauthentication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("SameParameterValue")
public class Application {
    private static final Logger log = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) throws JOSEException, IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        // Keystore configuration
        final String keyStoreFilename = "keystore.pfx";
        final String keyStorePassword = "Test1234";
        final String keyStoreType = "PKCS12";

        // Authorization server configuration
        final String tokenEndpoint = "https://oidc-test.hosted.trifork.com/auth/realms/apotek/protocol/openid-connect/token";
        final String clientId = "apotek";
        final String audience = "https://oidc-test.hosted.trifork.com/auth/realms/apotek";

        // ASP Pharmacy API configuration
        final String cpr = "9191919191";
        final String pharmacyNumber = "00101";
        final String pharmacyApiEndpoint = "https://test1.apoteksapp.dk/asp/pharmacy/api/v1/pharmacy/" + pharmacyNumber + "/getuserinfo";

        // Load certificate and private key
        KeyStore keyStore = KeyStoreUtils.loadKeyStore(keyStoreFilename, keyStorePassword, keyStoreType);
        X509Certificate certificate = KeyStoreUtils.findFirstCertificate(keyStore);
        PrivateKey privateKey = KeyStoreUtils.findFirstPrivateKey(keyStore, keyStorePassword);

        // Generate client JWT
        String clientJWT = generateClientJWT(clientId, audience, certificate, privateKey);

        // Request access token and parse JSON response as a Map
        String tokenResponse = requestToken(clientId, clientJWT, tokenEndpoint);
        Map<String, String> tokenResponseMap = new ObjectMapper().readValue(tokenResponse, new TypeReference<>() {
        });
        String accessToken = tokenResponseMap.get("access_token");
        log.info("access_token: " + accessToken);

        // Request ASP Pharmacy API
        String pharmacyApiResponse = requestPharmacyAPI(cpr, accessToken, pharmacyApiEndpoint);
        log.info("Pharmacy API response: " + pharmacyApiResponse);
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

    public static String requestToken(String clientId, String clientJWT, String tokenEndpoint) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            // Build request body
            List<NameValuePair> form = new ArrayList<>();
            form.add(new BasicNameValuePair("grant_type", "client_credentials"));
            form.add(new BasicNameValuePair("client_id", clientId));
            form.add(new BasicNameValuePair("client_assertion", clientJWT));
            form.add(new BasicNameValuePair("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"));

            // Build request method
            HttpPost httpPost = new HttpPost(tokenEndpoint);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.toString());
            httpPost.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8));

            // Do request
            CloseableHttpResponse response = httpclient.execute(httpPost);
            return verifyAndExtractResponse(response);
        }
    }

    public static String requestPharmacyAPI(String cpr, String accessToken, String endpoint) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            // Build request body
            String requestBody = "{\"cpr\": \"" + cpr + "\"}";

            // Build request method
            HttpPost httpPost = new HttpPost(endpoint);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            httpPost.setEntity(new StringEntity(requestBody, Consts.UTF_8));

            // Do request
            CloseableHttpResponse response = httpclient.execute(httpPost);
            return verifyAndExtractResponse(response);
        }
    }

    private static String verifyAndExtractResponse(CloseableHttpResponse response) throws IOException {
        HttpEntity responseEntity = response.getEntity();

        // Verify that HTTP status code is 200
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new RuntimeException("Server responded HTTP status code: " + statusCode);
        }

        // Verify that response is not empty
        if (responseEntity == null) {
            throw new RuntimeException("Response was empty");
        }

        return EntityUtils.toString(responseEntity);
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
