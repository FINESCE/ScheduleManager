package qsmart.java.applications.gsm.security;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import qsmart.java.applications.gsm.model.FINESCEComponent;

public class ApiUser extends User {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 18997023950273030L;
	
	/** Default Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(ApiUser.class);
	
	private static final String ADMIN_NAME = "ADMIN";
	
	private Map<FINESCEComponent.ComponentSide,Map<String,String>> rights;
	
	private boolean admin = false;
	
	private Long timestamp;

	public ApiUser(String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		setTimestamp(System.currentTimeMillis());
		
		for (GrantedAuthority auth : authorities){
			if (auth.getAuthority().equals(ADMIN_NAME)){
				admin = true;
			}else{
				String[] parts = auth.getAuthority().split(";");
				if (parts.length == 3){
					try{
						FINESCEComponent.ComponentSide side = FINESCEComponent.ComponentSide.valueOf(parts[0]);
						if (rights == null)
							rights = new TreeMap<FINESCEComponent.ComponentSide,Map<String,String>>();
						if (!rights.containsKey(FINESCEComponent.ComponentSide.valueOf(parts[0])))
							rights.put(side, new TreeMap<String,String>());
						
						if (rights.get(side).containsKey(parts[1]) && 
								!rights.get(side).get(parts[1]).contains(parts[2])){
							rights.get(side).put(parts[1],rights.get(side).get(parts[1]).concat(parts[2]));
						}else if (!rights.get(side).containsKey(parts[1])){
							rights.get(side).put(parts[1], parts[2]);
						}
							
					}catch (Exception e){
						LOG.error("Malformed authority " + auth.getAuthority());
						e.printStackTrace();
					}					
				}else{
					LOG.error("Malformed authority " + auth.getAuthority());
				}
			}
		}
	}

	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	public boolean isAuthorised(FINESCEComponent.ComponentSide side, String name, String rw){
		try{
			if (rights.get(side).get(name).contains(rw))
				return true;
		}catch (Exception e){}
		
		return false;
	}
	
	public boolean isAuthorised(String auth){
		
		if (admin)
			return true;
		
		String[] parts = auth.split(";");
		if (parts.length == 3){
			try{
				FINESCEComponent.ComponentSide side = FINESCEComponent.ComponentSide.valueOf(parts[0]);
				return isAuthorised(side,parts[1],parts[2]);
			}catch (Exception e){
				LOG.error("Malformed authority " + auth);
				e.printStackTrace();
			}					
		}else{
			LOG.error("Malformed authority " + auth);
		}
		return false;
	}
	
	public String getRw(FINESCEComponent.ComponentSide side, String name){
		if (admin){
			LOG.debug("User is ADMIN");
			return "rw";
		}
		String ret = "";
		try{
			ret = rights.get(side).get(name);
		}catch (Exception e){}
		return ret;
	}
	

}
