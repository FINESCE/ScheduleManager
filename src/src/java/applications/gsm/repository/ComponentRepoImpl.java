package qsmart.java.applications.gsm.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import qsmart.java.applications.gsm.model.ComponentDataFileWrapper;
import qsmart.java.applications.gsm.model.FINESCEComponent;
import qsmart.java.applications.gsm.model.FINESCEComponent.ComponentSide;
import qsmart.java.applications.gsm.model.FINESCEComponent.DataType;
import qsmart.java.applications.gsm.service.ComponentImportHandler;
import qsmart.java.library.util.spring.VppReloadableResourceBundleMessageSource;

/**
 * Implementation of {@link PowerPlantRepo} for a local repository.
 * 
 * User: M. Munoz
 * Modified by: M.Munoz
 * Date: 5/21/13 Time: 4:29 PM
 */
@Component
@Named("local")
public class ComponentRepoImpl implements ComponentRepo {

	/** Default Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(ComponentRepoImpl.class);
	
	/** Property key for virtual power plant root folder */
	private static final String PROPERTY_BASE_URL = "api.base.url";

	/** {@link ComponentImportHandler} for importing on demand. */
	@Inject
	private ComponentImportHandler importHandler;
	
	/** Default message source. */
	@Inject
	private VppReloadableResourceBundleMessageSource messageSource;

	/** Map of available {@link FINESCEComponent} */
	private Map<FINESCEComponent.ComponentSide, Map<String, FINESCEComponent>> components =
			new TreeMap<FINESCEComponent.ComponentSide, Map<String, FINESCEComponent>>();
	


	@Override
	public Collection<FINESCEComponent> getComponents() {
		List<FINESCEComponent> list = new ArrayList<FINESCEComponent>();
		for (Map<String, FINESCEComponent> side : components.values()){
			list.addAll(side.values());
		}
		return list;
	}

	@Override
	public FINESCEComponent getComponent(String componentId, FINESCEComponent.ComponentSide side) {
		if (components.containsKey(side) && components.get(side).containsKey(componentId)){
			return components.get(side).get(componentId);
		}
		return null;
	}

	@Override
	public String getComponentDataFiltered(String componentId,
			ComponentSide side, long date, DataType dataType) {
		String data = "";
		FINESCEComponent component = getComponent(componentId, side);
		if (component != null){
			ComponentDataFileWrapper fw = component.getDataFile(dataType, date);
			if (fw == null){
				LOG.debug(String.format("No data returned for component %s on side %s for type %s and day %d", componentId, 
						side.toString(), dataType.toString(), date));
				return null;
			}
			data = importHandler.handleDataImport(fw.getFile());
			if (data == null){
				LOG.debug(String.format("No data returned for component %s on side %s for type %s and day %d", componentId, 
						side.toString(), dataType.toString(), date));
				return null;
			}
		}else{
			LOG.debug(String.format("No data returned for component %s on side %s for type %s and day %d", componentId, 
					side.toString(), dataType.toString(), date));
			return null;
		}
		LOG.debug(String.format("Data returned for component %s on side %s for type %s and day %d: %s", componentId, 
				side.toString(), dataType.toString(), date, data));
		return data;
	}

	@Override
	public void updateOrAddComponent(FINESCEComponent pFINESCEComponent,
			ComponentDataFileWrapper pComponentDataFileWrapper,
			ComponentSide side) {
		Map<String, FINESCEComponent> mapSide;
		if (!components.containsKey(side)){
			mapSide = new TreeMap<String, FINESCEComponent>();
			components.put(side, new TreeMap<String, FINESCEComponent>());
		}else{
			mapSide = components.get(side);
		}
		LOG.trace("Updating component. Size before: " + mapSide.size());
		String cID = pFINESCEComponent.getComponentId();
		if (!mapSide.containsKey(cID)) {
			final String componentBaseUrl = messageSource.getProperty(PROPERTY_BASE_URL).replaceAll("/$", "");
			mapSide.put(cID, new FINESCEComponent(cID, componentBaseUrl, side));
		}
		mapSide.get(cID).addDataFile(pComponentDataFileWrapper);
		
		LOG.trace("Updating component. Size after: " + mapSide.size());
		
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("rawtypes")
	@Override
	public Map<String,List> getListsMap() {
		Map<String,List> rData = new TreeMap<String,List>();
		for (Entry<FINESCEComponent.ComponentSide, Map<String, FINESCEComponent>> sides : components.entrySet()){
			FINESCEComponent.ComponentSide side = sides.getKey();
			List<Map> list = new ArrayList<Map>();
			for (Entry<String, FINESCEComponent> cEntries : sides.getValue().entrySet()){
				Map<String,Object> subMap = new TreeMap<String, Object>();
				String cID = cEntries.getKey();
				FINESCEComponent comp = cEntries.getValue();
				subMap.put("component", cID);
				subMap.put("url", String.format("%s/%s/%s", comp.getBaseUrl(), side.toString(), cID));
//				subMap.put("access", "rw");
				list.add(subMap);
			}
			rData.put(side.toString(), list);
		}
		return rData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map addComponentDataFiltered(String componentId,
			ComponentSide side, long date, DataType dataType, String data) throws IOException {
		FINESCEComponent component;
		Map<String,String> subMap = new TreeMap<String, String>();
		String url = String.format("%s/%s/%s/%s/%d", messageSource.getProperty(PROPERTY_BASE_URL) , 
				side.toString(), componentId, dataType.toString(), date);
		subMap.put("url", url);
		if ((component = getComponent(componentId, side)) != null){
			ComponentDataFileWrapper file = component.getDataFile(dataType, date);
			if (file != null){
				importHandler.handleDataWritting(file.getFile(), data);
				return subMap;
			}
		}
		importHandler.handleDataWritting(componentId, side, date, dataType, data);
		return subMap;
	}

}
