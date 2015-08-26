package qsmart.java.applications.gsm.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Custom class to force the system to reload the properties file each time. This way, a new user
 * registration is now possible by simply modifying this file.
 * 
 * @author manolomunoz
 *
 */
public class CustomUserDetailsService implements UserDetailsService{


	private String sFile = "/SERVER/tomcat/apache-tomcat-7.0.54/webapps/FINESCEApi/resources/users/users.properties";
	private String hFile = "/SERVER/tomcat/apache-tomcat-7.0.54/webapps/FINESCEApi/resources/users/hashUsers.properties";
	
//	private String sFile = "/opt/servers/Tomcat/apache-tomcat-7.0.47/webapps/FINESCEApi/resources/users/users.properties";
//	private String hFile = "/opt/servers/Tomcat/apache-tomcat-7.0.47/webapps/FINESCEApi/resources/users/hashUsers.properties";
	
	/** Default Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException{
		
		Properties clearUsers= new Properties();
		File f = new File(sFile);
		long plainModified = Long.MAX_VALUE;
		if (!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				LOG.error("Not possible to create the File " + f.getAbsolutePath());
				e.printStackTrace();
			}
		}else{
			plainModified = f.lastModified();
		}
		
		Properties hashUsers= new Properties();
		File hashFile = new File(hFile);
		long hashModified = 0;
		if (!hashFile.exists()){
			try {
				hashFile.createNewFile();
				LOG.debug("File " + hashFile.getAbsolutePath() + " created");
			} catch (IOException e) {
				LOG.error("Not possible to create the File");
				e.printStackTrace();
				throw new UsernameNotFoundException("Not possible to load file");
			}
		}else{
			hashModified = hashFile.lastModified();
		}
		
		InputStream in = null;
		try {
			if (plainModified > hashModified){
				in = new FileInputStream(f);
				clearUsers.load(in);
				in.close();
				in = null;
				LOG.debug("Plain Users loaded.");
				if (clearUsers.isEmpty()){
					plainModified = Long.MAX_VALUE;
				}
			}
		} catch (IOException e) {
			LOG.error("Not possible to load data from file.",e);
			e.printStackTrace();
		}finally{
			if (in != null ){
				try {
					in.close();
				} catch (IOException e) {
					LOG.error("Error closing the InputStream",e);
					e.printStackTrace();
				}
			}
		}
		
		try{
			in = new FileInputStream(hashFile);
			hashUsers.load(in);
			in.close();
			in = null;
			LOG.debug("Hashed users loaded.");
		} catch (IOException e) {
			LOG.error("Not possible to load data from file.",e);
			e.printStackTrace();
		}finally{
			if (in != null ){
				try {
					in.close();
				} catch (IOException e) {
					LOG.error("Error closing the InputStream",e);
					e.printStackTrace();
				}
			}
		}
		
		if (plainModified > hashModified){
			for(Entry<Object, Object> entry : clearUsers.entrySet()){
				String val = (String) entry.getValue();
				String[] parts = val.split(",");
				String pass = parts[0];
				LOG.debug(String.format("User: %s", entry.getKey()));
//				LOG.debug(String.format("Clear Password: %s", pass));
				try {
					parts[0] = PasswordHash.createHash(pass);
//					LOG.debug(String.format("HashPassword: %s",parts[0]));
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					LOG.error("Not possible to hash password");
					e.printStackTrace();
				}
				val = "";
				for (String part : parts){
					val = val + part + ",";
				}
				hashUsers.setProperty((String) entry.getKey(), val);
			}
			clearUsers.clear();
			FileWriter writer = null;
			try{
				writer = new FileWriter(hashFile);
				hashUsers.store(writer, "Users with hash Passwords");
				writer.close();
				writer = new FileWriter(f);
				clearUsers.store(writer, "Write new users with clear passwords here. On the first user querry"
						+ " performed afterwards, the password will be coded and the result stored on hashUsers.properties");
				
			}catch (Exception e){
				LOG.error("Not possible to store the hash values");
				e.printStackTrace();
			}finally{
				try {
					if (writer != null)
						writer.close();
				} catch (IOException e) {
					LOG.error("Not possible to close the writer");
					e.printStackTrace();
				}
			}
		}
		
		
		if (!hashUsers.containsKey(username)){
			throw new UsernameNotFoundException("User not found.");
		}
		
		String[] parts = hashUsers.getProperty(username).split(",");
		
		String[] sRoles = Arrays.copyOfRange(parts, 1, parts.length-1);
		
		List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
		
//		TODO: Printing the password? Sure, why not! :D
		LOG.debug(String.format("Requested User: %s", username));
//		LOG.debug(String.format("Password of req.: %s", parts[0]));
		
		for (String role : sRoles){
			LOG.debug(String.format("Role: %s", role));
			roles.add(new SimpleGrantedAuthority(role));
		}

		
		return new ApiUser(username, parts[0], roles);
		
	}


	public String getsFile() {
		return sFile;
	}


	public void setsFile(String sFile) {
		this.sFile = sFile;
	}
	
	
}
