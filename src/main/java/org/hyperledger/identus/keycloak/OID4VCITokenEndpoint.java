package org.hyperledger.identus.keycloak;

import jakarta.ws.rs.core.Response;
import org.hyperledger.identus.keycloak.model.NonceResponse;
import org.jboss.logging.Logger;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.TokenManager;
import org.keycloak.protocol.oidc.endpoints.AuthorizationEndpoint;
import org.keycloak.protocol.oidc.endpoints.TokenEndpoint;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.services.clientpolicy.ClientPolicyContext;

import java.util.function.Function;

public class OID4VCITokenEndpoint extends TokenEndpoint {
    private static final Logger logger = Logger.getLogger(OID4VCITokenEndpoint.class);

    private final IdentusClient identusClient;

    public OID4VCITokenEndpoint(KeycloakSession session, TokenManager tokenManager, EventBuilder event) {
        super(session, tokenManager, event);
        this.identusClient = new IdentusClient();
    }

    @Override
    public Response createTokenResponse(UserModel user, UserSessionModel userSession, ClientSessionContext clientSessionCtx,
                                        String scopeParam, boolean code, Function<TokenManager.AccessTokenResponseBuilder, ClientPolicyContext> clientPolicyContextGenerator) {
        String noteKey = AuthorizationEndpoint.LOGIN_SESSION_NOTE_ADDITIONAL_REQ_PARAMS_PREFIX + OID4VCIConstants.ISSUER_STATE;
        String issuerState = clientSessionCtx.getClientSession().getNote(noteKey);

        if (code && issuerState != null) {
            Response originalResponse = super.createTokenResponse(user, userSession, clientSessionCtx, scopeParam, true, clientPolicyContextGenerator);
            AccessTokenResponse responseEntity = (AccessTokenResponse) originalResponse.getEntity();

            String token = responseEntity.getToken();
            NonceResponse nonceResponse = identusClient.getNonce(token, issuerState);
            responseEntity.setOtherClaims(OID4VCIConstants.C_NONCE, nonceResponse.getNonce());
            responseEntity.setOtherClaims(OID4VCIConstants.C_NONCE_EXPIRE, nonceResponse.getNonceExpiresIn());
            return Response.fromResponse(originalResponse)
                    .entity(responseEntity)
                    .build();
        } else {
            return super.createTokenResponse(user, userSession, clientSessionCtx, scopeParam, false, clientPolicyContextGenerator);
        }
    }
}
