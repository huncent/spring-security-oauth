package org.springframework.security.oauth2.provider.token;

import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of token services that stores tokens in memory.
 *
 * @author Ryan Heaton
 */
public class InMemoryTokenStore implements TokenStore {

  protected final ConcurrentHashMap<String, OAuth2AccessToken> accessTokenStore = new ConcurrentHashMap<String, OAuth2AccessToken>();
  protected final ConcurrentHashMap<String, ExpiringOAuth2RefreshToken> refreshTokenStore = new ConcurrentHashMap<String, ExpiringOAuth2RefreshToken>();
  protected final ConcurrentHashMap<String, OAuth2Authentication> authenticationStore = new ConcurrentHashMap<String, OAuth2Authentication>();
  protected final ConcurrentHashMap<String, String> refreshTokenAssociation = new ConcurrentHashMap<String, String>();

  public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
    return this.authenticationStore.get(token.getValue());
  }

  public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
    this.accessTokenStore.put(token.getValue(), token);
    this.authenticationStore.put(token.getValue(), authentication);
    if (token.getRefreshToken() != null && token.getRefreshToken().getValue() != null) {
      this.refreshTokenAssociation.put(token.getRefreshToken().getValue(), token.getValue());
    }
  }

  public OAuth2AccessToken readAccessToken(String tokenValue) {
    return this.accessTokenStore.get(tokenValue);
  }

  public void removeAccessToken(String tokenValue) {
    this.accessTokenStore.remove(tokenValue);
    this.authenticationStore.remove(tokenValue);
  }

  public OAuth2Authentication readAuthentication(ExpiringOAuth2RefreshToken token) {
    return this.authenticationStore.get(token.getValue());
  }

  public void storeRefreshToken(ExpiringOAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
    this.refreshTokenStore.put(refreshToken.getValue(), refreshToken);
    this.authenticationStore.put(refreshToken.getValue(), authentication);
  }

  public ExpiringOAuth2RefreshToken readRefreshToken(String tokenValue) {
    return this.refreshTokenStore.get(tokenValue);
  }

  public void removeRefreshToken(String tokenValue) {
    this.refreshTokenStore.remove(tokenValue);
    this.authenticationStore.remove(tokenValue);
  }

  public void removeAccessTokenUsingRefreshToken(String refreshToken) {
    String accessToken = this.refreshTokenAssociation.remove(refreshToken);
    if (accessToken != null) {
      this.accessTokenStore.remove(accessToken);
    }
  }
}
