package qsmart.java.applications.gsm.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import qsmart.java.applications.gsm.model.FINESCEComponent;
import qsmart.java.applications.gsm.repository.ComponentRepo;
import qsmart.java.applications.gsm.security.APISecurityManager;
import qsmart.java.applications.gsm.service.ComponentDataProcessor;

/**
 * Controller for handling rest request concerning Energy Components. Author: M. Munoz
 */

@Controller
public class Data implements ApiEndPointsAndPatameters {

	/** Default Logger */
	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(Data.class);

	/** Default char set used in JSON Response. */
	private static final String CHARSET = "charset=utf-8";
	
	/** Field for returning the Authorisation id. */
	private static final String AUTH_ID = "authorisation-id";
	
	/** Field for returning the data. */
	private static final String DATA = "data";


	/** Repository where power plants and power plant data can be fetch from. */
	@Inject
	private @Named("local") ComponentRepo componentRepo;
	
	@Inject
	private APISecurityManager securityManager;
	
	@Inject
	private ComponentDataProcessor dataProcessor;


	/**
	 * Handler for POST request with application type 'application/json'. Logs in the server, and returns a 
	 * key to use on future comunications.
	 *
	 * @return JSON utf-8 encoded array of power plants.
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = AUTHENTICATION, method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE + ";" + CHARSET)
	@ResponseBody
	public ResponseEntity<Map> authenticateUser (
			@RequestParam(value = QUERY_AUTHENTICATION_ID, required = true) String userName,
			@RequestParam(value = QUERY_USER_PASSWORD, required = true) String password){
		String code;
		if ((code = securityManager.authenticate(userName.trim(), password.trim())) == null){
			return new ResponseEntity<Map>(HttpStatus.UNAUTHORIZED);
		}
		Map<String,String> map = new TreeMap<String,String>();
		map.put(AUTH_ID, code);
		return new ResponseEntity<Map>(map,HttpStatus.OK);
	}
	
	/**
	 * Handler for GET request with application type 'application/json'. Logs out a user from the server.
	 *
	 * @return JSON utf-8 encoded array of power plants.
	 */
	@RequestMapping(value = LOGOUT, method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE + ";" + CHARSET)
	@ResponseBody
	public ResponseEntity<String> logout (
			@RequestParam(value = QUERY_AUTHORISATION_ID, required = true) String authoCode){

		if (!securityManager.logout(authoCode)){
			return new ResponseEntity<String>("User is not in the system",HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	
	/**
	 * Handler for GET request with application type 'application/json'. Lists all available Components actually
	 * available to the user.
	 *
	 * @return JSON utf-8 encoded array of power plants.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = USER_COMPONENT_LIST, method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE + ";" + CHARSET)
	@ResponseBody
	public ResponseEntity<Map<String,List>> listPowerPlants (
			@RequestParam(value = QUERY_AUTHORISATION_ID, required = false) String authoCode) {
		if (!securityManager.isAuthorised(authoCode)){
			return new ResponseEntity<Map<String,List>>(HttpStatus.UNAUTHORIZED);
		}
		LOG.info("Listing all Components.");
		Map<String,List> components;
		if (authoCode == null){
			components = componentRepo.getListsMap();
		}else{
			components = dataProcessor.addRights(componentRepo.getListsMap(),
					securityManager.getUser(authoCode));
		}
		return new ResponseEntity<Map<String,List>>(components,HttpStatus.OK);
	}
	
	/**
	 * Handler for GET request with application type 'application/json'. Returns list of available data for a Component.
	 *
	 * @param id
	 * 		the Component id
	 *
	 * @return JSON utf-8 encoded array of data
	 */
	@RequestMapping(value = SIDE_PATH + COMPONENT_ID_PATH + DATA_LIST, method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE + ";" + CHARSET)
	@ResponseBody
	public ResponseEntity<Map<String,Object>> getDataList (
			@RequestParam(value = QUERY_AUTHORISATION_ID, required = false) String authoCode,
			@PathVariable final String side,
			@PathVariable final String cId) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("loading data list for component [" + cId + "], on side [" + side + "]");
		}
		if (!securityManager.isAuthorised(authoCode , String.format("%s;%s;%s", side, cId, "r"))){
			return new ResponseEntity<Map<String,Object>>(HttpStatus.UNAUTHORIZED);
		}
		Map<String,Object> rData = new TreeMap<String,Object>();
		try {
			FINESCEComponent comp = componentRepo.getComponent(cId, FINESCEComponent.ComponentSide.valueOf(side));
			if (comp == null)
					return new ResponseEntity<Map<String,Object>>(HttpStatus.NOT_FOUND);
			rData = comp.toMap();
		}catch (Exception e){
			LOG.error("Not possible to obtain the data.");
			e.printStackTrace();
			return new ResponseEntity<Map<String,Object>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("Data to return: " + rData.size());
		}
		return new ResponseEntity<Map<String,Object>>(rData,HttpStatus.OK);
	}

	/**
	 * Handler for GET request with application type 'application/json'. Returns the data contained on an specific file
	 *
	 * @param id
	 * 		the Component id
	 *
	 * @return JSON utf-8 encoded contents of the requested file. Can be empty if it doesn't exist.
	 * 
	 */
	@RequestMapping(value = SIDE_PATH + COMPONENT_ID_PATH + TYPE_PATH + DATE_PATH, method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE + ";" + CHARSET)
	@ResponseBody
	public ResponseEntity<Map<String,String>> getConcreteData (
			@RequestParam(value = QUERY_AUTHORISATION_ID, required = false) String authoCode,
			@PathVariable final String side,
			@PathVariable final String cId,
			@PathVariable final String type,
			@PathVariable final String date) {
		if (LOG.isTraceEnabled()) {
			LOG.trace(String.format("loading for component %s, on side %s, for type %s and date %s"
					, cId, side, type, date));
		}
		if (!securityManager.isAuthorised(authoCode , String.format("%s;%s;%s", side, cId, "r"))){
			return new ResponseEntity<Map<String,String>>(HttpStatus.UNAUTHORIZED);
		}
		Map<String,String> rData = new TreeMap<String,String>();
		try {
			String data = dataProcessor.processDataToRepresent(componentRepo.getComponentDataFiltered(
					cId, FINESCEComponent.ComponentSide.valueOf(side), Long.valueOf(date), 
					FINESCEComponent.DataType.valueOf(type)));
			if (data == null)
				return new ResponseEntity<Map<String,String>>(HttpStatus.NOT_FOUND);
			LOG.debug("Processed Data: " + data);
			rData.put(DATA, data);
		}catch (Exception e){
			LOG.error("Not possible to obtain the data.");
			e.printStackTrace();
			return new ResponseEntity<Map<String,String>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("Data to return: " + rData.size());
		}
		return new ResponseEntity<Map<String,String>>(rData,HttpStatus.OK);
	}

	/**
	 * Handler for POST request with application type 'application/json'. Writes the data on the system
	 *
	 *
	 * @return Only Status code.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = SIDE_PATH + COMPONENT_ID_PATH + TYPE_PATH + DATE_PATH, method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE + ";" + CHARSET)
	@ResponseBody
	public ResponseEntity<Map<String,String>> writeData (
			@RequestParam(value = QUERY_AUTHORISATION_ID, required = false) String authoCode,
			@PathVariable final String side,
			@PathVariable final String cId,
			@PathVariable final String type,
			@PathVariable final String date,
			@RequestBody Map<String,String> body) {
		if (!securityManager.isAuthorised(authoCode , String.format("%s;%s;%s", side, cId, "w"))){
			return new ResponseEntity<Map<String,String>>(HttpStatus.UNAUTHORIZED);
		}
		Map<String,String> ret = new TreeMap<String,String>();
		try {
			if (!body.containsKey("data")){
				LOG.error("Malformed request");
				ret = new TreeMap<String,String>();
				ret.put("Error", "Malformed request. Expecting a json message with a data field");
				return new ResponseEntity<Map<String,String>>(ret,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			String data = dataProcessor.processDataToStore(body.get(DATA));
			LOG.debug("Data: " + data);
			ret = componentRepo.addComponentDataFiltered(cId, FINESCEComponent.ComponentSide.valueOf(side)
					, Long.valueOf(date), FINESCEComponent.DataType.valueOf(type), data);
		}catch (Exception e){
			LOG.error("Not possible to write the data.");
			e.printStackTrace();
			return new ResponseEntity<Map<String,String>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String,String>>(ret, HttpStatus.OK);
	}

	/**
	 * Test function to test default responses.
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/test",
			method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE + ";" + CHARSET)
	@ResponseBody
	public ResponseEntity<Map> getTestJSON () {
		Map data = new TreeMap();
		Map data2 = new TreeMap();
		data.put("test1", data2);
		data2.put("1", "2");
		data2.put("2", "3");
		data.put("hey", "you");
		List li = new ArrayList();
		li.add("ja");
		li.add("je");
		data.put("list", li.toArray());
		
		return new ResponseEntity<Map>(data, HttpStatus.OK);

	}


}
