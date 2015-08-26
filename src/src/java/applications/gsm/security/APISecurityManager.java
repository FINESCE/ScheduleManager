package qsmart.java.applications.gsm.security;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class APISecurityManager {
	
	private static long timeout = 600000L;	
	
	/** Default Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(APISecurityManager.class);
	
	private static CustomUserDetailsService userLoader = new CustomUserDetailsService();
	
	private static final int ID_LENGHT = 10;
	
	private Map<String, ApiUser> keys = new HashMap<String, ApiUser>();
	
	public String authenticate (String userName, String password){
		if (isLogged(userName)){
			LOG.warn("Warning: User " + userName + " is already logged in the system.");
		}
		
		ApiUser user;
		
		try {
			user = (ApiUser)userLoader.loadUserByUsername(userName);
		}catch(UsernameNotFoundException e){
			return null;
		}

		try {
			
			if (!PasswordHash.validatePassword(password, user.getPassword()))
				return null;
			
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			LOG.error("Not possible to validate user.");
			e.printStackTrace();
		}
		
		byte[] b = new byte[ID_LENGHT];
		String id;
		do{
			new Random().nextBytes(b);
			id = byteToHex(b);
		}while (keys.containsKey(id));
		
		keys.put(id, user);
		
		return id;
	}
	
	public boolean logout(String authoCode){
		if (!keys.containsKey(authoCode))
			return false;
		keys.remove(authoCode);
		return true;
	}
	
	public boolean isLogged(String userName){
		for (ApiUser user : keys.values()){
			if (userName.trim().equals(user.getUsername()))
				return true;
		}
		return false;
	}
	
	public boolean isAuthorised (String authoCode){
		return isAuthorised(authoCode, null, true);
	}
	
	public boolean isAuthorised (String authoCode, String resource){
		return isAuthorised(authoCode, resource, true);
	}
	
	public boolean isAuthorised (String authoCode, String resource, boolean update){
		
//		//TODO: erase this!!!
//		if (authoCode == null)
//			return true;
		
		if (!keys.containsKey(authoCode))
			return false;
		
		ApiUser user = keys.get(authoCode);
		LOG.debug("Authorisation code asigned to user " + user.getUsername());
		
		if ((user.getTimestamp() + timeout) < System.currentTimeMillis()){
			LOG.info("Code Expired");
			keys.remove(authoCode);
			return false;
		}

		if (update)
			user.setTimestamp(System.currentTimeMillis());	
		
		if ((resource != null) && (resource.length() > 0) && (!user.isAuthorised(resource)))
			return false;
		
		return true;
	}
	
	public ApiUser getUser(String authoCode){
		return keys.get(authoCode);
	}
	
	public Collection<GrantedAuthority> getAuthorities(String authoCode){
		if (!keys.containsKey(authoCode))
			return null;
		return keys.get(authoCode).getAuthorities();
	}
	
	public Collection<GrantedAuthority> getAuthoritiesByName(String userName){
		try {
			return ((ApiUser)userLoader.loadUserByUsername(userName)).getAuthorities();
		}catch(UsernameNotFoundException e){
			return null;
		}
	}

	
	
	/**
	 * Function user for representation purposes. It transform a byte array in a string with the Hex. values of
	 * the characters.
	 * 
	 * @param input array byte to represent
	 * @return a String representation of the hexadecimal values of the input array
	 */
	public String byteToHex(byte[] input){
		StringBuilder sb = new StringBuilder();
	    for (byte b : input) {
	        sb.append(String.format("%02X", b));
	    }
	    return sb.toString();
	}

}
