package qsmart.java.applications.gsm.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 * Abstract class for objects that can be returned as a rest response. 
 * 
 * @author manolomunoz
 *
 */
public abstract class RestResponseType {

	/**
	 * Formats the content of the object in the form of a Map, so it can easily be converted to a json string.
	 * 
	 * @return A map containing the relevant information of the object.
	 */
	public abstract Map<String,Object> toMap();
	
	/**
	 * Utility function to transform a collection of objects extending {@link RestResponseType} into a 
	 * collection of maps. This is accomplished by executing the {@link toMap()} function of each object
	 * in the collection.
	 * 
	 * @param list collection of objects extending {@link RestResponseType}
	 * @return list of Maps
	 */
	public static List<Map<String,Object>> fotmatList(Collection<? extends RestResponseType> list){
		List<Map<String,Object>> mList = new ArrayList<Map<String,Object>>();
		for (RestResponseType element : list){
			mList.add(element.toMap());
		}
		return mList;
	}
	
}
