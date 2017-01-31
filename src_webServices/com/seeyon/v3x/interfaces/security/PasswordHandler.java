package com.seeyon.v3x.interfaces.security;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

import org.apache.ws.security.WSPasswordCallback;

/**
 * @author david.turing
 */
public class PasswordHandler implements CallbackHandler {

	private final Map passwords = new HashMap();

	@SuppressWarnings("unchecked")
	public PasswordHandler() {
		passwords.put("lixin", "123456");
		passwords.put("springuser", "springpassword");
		passwords.put("david", "springside");
		passwords.put("david.turing", "a");
		passwords.put("cavin", "cac");
	}

	public void handle(Callback[] callbacks) {
		WSPasswordCallback callback = (WSPasswordCallback) callbacks[0];
		String id = callback.getIdentifer();
		callback.setPassword((String) passwords.get(id));
	}
}
