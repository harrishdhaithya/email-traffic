package auth;


import microsoft.exchange.webservices.data.core.EwsUtilities;
import microsoft.exchange.webservices.data.core.request.HttpWebRequest;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import java.util.HashMap;
import java.util.regex.Pattern;

/* 
 *  Source: https://gist.github.com/keshy/0ec56956e91e59e14fee7a892a7af919
 */
public final class OAuthCredentials extends ExchangeCredentials {

	private static final String BEARER_AUTH_TYPE = "Bearer";

	private static final Pattern VALID_TOKEN_PATTERN = Pattern.compile("^[A-Za-z0-9-_]+.[A-Za-z0-9-_]+.[A-Za-z0-9-_]*$");

	private final String token;

	public OAuthCredentials(String token) throws Exception {
		this(token, false);
	}

	public String getToken() {
		return token;
	}
	
	private OAuthCredentials(String token, boolean isRawToken) throws Exception {
		EwsUtilities.validateParam(token, "token");

		String rawToken;

		if (isRawToken) {
			rawToken = token;
		} else {
			int whiteSpacePosition = token.indexOf(" ");
			if (whiteSpacePosition == -1) {
				rawToken = token;
			} else {
				String authType = token.substring(0, whiteSpacePosition);
				if (BEARER_AUTH_TYPE.equalsIgnoreCase(authType)) {
					throw new IllegalArgumentException("Invalid token");
				}
				rawToken = token.substring(whiteSpacePosition + 1);
			}
			if (!VALID_TOKEN_PATTERN.matcher(rawToken).matches()) {
				throw new IllegalArgumentException("Invalid token");
			}
		}
		this.token = BEARER_AUTH_TYPE + " " + rawToken;
	}

	@Override
	public void prepareWebRequest(HttpWebRequest request) {

		if (this.token != null) {
			if (request.getHeaders() != null) {
				request.getHeaders().remove("Authorization");
			} else {
				request.setHeaders(new HashMap<String, String>());
			}
			request.getHeaders().put("Authorization", token);
		}
	}


}