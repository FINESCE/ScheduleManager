package qsmart.java.applications.gsm.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * 
 * Class containing the details of a component of the FINESCE project. This component can be either a 
 * generator or a consumer
 * 
 * @author M. Munoz
 *
 */
public class FINESCEComponent extends RestResponseType{
	
	/** Available DataType for requests */
	public enum DataType {
		measurement, calculation;
	}
	
	/** Types of components possible */
	public enum ComponentSide {
		generation,consumption;
	}
	
	private ComponentSide side;
	
	private String componentId;
	
	private String baseUrl;
	
	private Map<DataType, Map<Long, ComponentDataFileWrapper>> files;
	
	/**
	 * Constructor of a component without specifying the side (generation or consumption) it belongs to.
	 * 
	 * @param componentId the identification of the component. 
	 * @param baseUrl base url to access the component
	 */
	public FINESCEComponent(String componentId, String baseUrl){
		this.componentId = componentId;
		this.baseUrl = baseUrl;
		files = new TreeMap<DataType, Map<Long, ComponentDataFileWrapper>>();
	}
	
	/**
	 * Constructor of a component specifying the side (generation or consumption) it belongs to.
	 * 
	 * @param componentId the identification of the component. 
	 * @param baseUrl base url to access the component
	 * @param side the side in which the component is located
	 */
	public FINESCEComponent(String componentId, String baseUrl, ComponentSide side){
		this(componentId, baseUrl);
		this.setSide(side);
	}
	
	
	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * @return the side
	 */
	public ComponentSide getSide() {
		return side;
	}

	/**
	 * @param side the side to set
	 */
	public void setSide(ComponentSide side) {
		this.side = side;
	}

	/**
	 * @return the componentId
	 */
	public String getComponentId() {
		return componentId;
	}

	/**
	 * @param componentId the componentId to set
	 */
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	/**
	 * Generates a {@link ComponentDataFileWrapper} containing the specified file, and adds it to the internal 
	 * index of data files belonging to the component.  
	 * 
	 * @param type specifies if the data is a real measurement or a calculation
	 * @param day contains the day of the data in the format yyyyMMdd
	 * @param file the file to add
	 */
	public void addFile(DataType type, Long day, File file){
		addDataFile(new ComponentDataFileWrapper(file, type, day));
	}

	/**
	 * Adds a {@link ComponentDataFileWrapper} to the internal index of data files 
	 * belonging to the component. 
	 * 
	 * @param pComponentDataFileWrapper the {@link ComponentDataFileWrapper} to add
	 */
	public void addDataFile(ComponentDataFileWrapper pComponentDataFileWrapper) {
		Map<Long, ComponentDataFileWrapper> fileMap = files.get(pComponentDataFileWrapper.getDataType());
		if (fileMap == null){
			fileMap = new TreeMap<Long, ComponentDataFileWrapper>();
			files.put(pComponentDataFileWrapper.getDataType(), fileMap);
		}
		fileMap.put(pComponentDataFileWrapper.getDate(), pComponentDataFileWrapper);
	}
	
	
	
	/**
	 * 
	 * Returns a {@link ComponentDataFileWrapper} for the specified {@link DataType} and day.
	 * 
	 * @param type {@link DataType} of the data to return
	 * @param day day of the data to return, as a long in format yyyyMMdd
	 * @return the requested file if it exists, or null if it does not
	 */
	public ComponentDataFileWrapper getDataFile(DataType type, Long day){
		ComponentDataFileWrapper file = null;
		if (files.containsKey(type) && files.get(type).containsKey(day)){
			file = files.get(type).get(day);
			if (!file.stillExists()){
				files.get(type).remove(day);
				return null;
			}
		}
		return file;
	}
	
	/**
	 * Removes a data file from the internal index
	 * 
	 * @param type {@link DataType} of the data to return
	 * @param day day of the data to return, as a long in format yyyyMMdd
	 */
	public void removeDataFile(DataType type, Long day){
		if (files.containsKey(type) && files.get(type).containsKey(day)){
			files.get(type).remove(day);
		}
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new TreeMap<String, Object>();
		Set<Entry<DataType, Map<Long, ComponentDataFileWrapper>>> set = files.entrySet();
		for (Entry<DataType, Map<Long, ComponentDataFileWrapper>> entry : set){
			List <Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			Set<Entry<Long, ComponentDataFileWrapper>> set2 = entry.getValue().entrySet();
			for (Entry<Long, ComponentDataFileWrapper> entry2 : set2){
				ComponentDataFileWrapper file = entry2.getValue();
				if (!file.stillExists()){
					set2.remove(entry2);
					if (set2.isEmpty()){
						set.remove(entry);
					}
				}else{
					Map<String,Object> subMap = new TreeMap<String, Object>();
					list.add(subMap);
					String dateString = Long.toString(file.getDate());
					subMap.put("date", dateString);
					String url = String.format("%s/%s/%s/%s/%s", baseUrl , this.side.toString(), 
							this.componentId, entry.getKey().toString(), dateString);
					subMap.put("url", url);
				}
			}
			if (!list.isEmpty())
				map.put(entry.getKey().toString(), list);
		}
		return map;
	}

}
