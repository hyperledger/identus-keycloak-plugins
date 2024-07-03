package org.hyperledger.identus.keycloak;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hyperledger.identus.keycloak.model.NonceRequest;
import org.hyperledger.identus.keycloak.model.NonceResponse;
import org.jboss.logging.Logger;
import org.keycloak.util.JsonSerialization;

import java.io.IOException;
import java.util.function.Supplier;

public class IdentusClient {

    private static final Logger logger = Logger.getLogger(IdentusClient.class);

    private final String identusUrl = System.getenv("IDENTUS_URL");

    private final Supplier<CloseableHttpClient> httpClient = IdentusClient::newCloseableHttpClient;

    public IdentusClient() {
        if (this.identusUrl == null) {
            logger.warn("The URL of the Identus Cloud Agent client is null. The IDENTUS_URL environment variable is not set. The token response will not contain a nonce.");
        }
    }

    public Boolean isIdentusUrlSet() {
        return this.identusUrl != null;
    }

    public static CloseableHttpClient newCloseableHttpClient() {
        return HttpClientBuilder.create().build();
    }

    public NonceResponse getNonce(String token, String issuerState) {
        try (CloseableHttpClient client = httpClient.get()) {
            HttpPost post = new HttpPost(identusUrl + "/oid4vci/nonces");
            post.setHeader("Authorization", "Bearer " + token);
            post.setEntity(new StringEntity(JsonSerialization.writeValueAsString(new NonceRequest(issuerState)), ContentType.APPLICATION_JSON));
            return NonceResponse.fromResponse(client.execute(post));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
