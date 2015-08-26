package qsmart.java.applications.gsm.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import qsmart.java.applications.gsm.model.ComponentDataFileWrapper;
import qsmart.java.applications.gsm.model.FINESCEComponent;
import qsmart.java.applications.gsm.model.FINESCEComponent.ComponentSide;
import qsmart.java.applications.gsm.model.FINESCEComponent.DataType;
import qsmart.java.applications.gsm.repository.ComponentRepo;
import qsmart.java.library.util.spring.VppReloadableResourceBundleMessageSource;


/**
 * Handler for importing {@link PowerPlant} and {@link PowerPlantData} from files saved on the file system using
 * spring integration and on demand calls.
 * <p/>
 * Author: M.Munoz
 */
@Component("dataHandler")
public class ComponentImportHandler {

	/** Default Logger */
	private static final Logger LOG = LoggerFactory.getLogger(ComponentImportHandler.class);

	/** Property key for virtual power plant root folder */
	private static final String PROPERTY_VPP_DATA_ROOT_FOLDER = "vpp.data.root.folder";	
	/** Property key for virtual power plant root folder */
	private static final String PROPERTY_BASE_URL = "api.base.url";
	
	/** Property key for virtual power plant root folder */
	public static final String NO_IMPORT = "noImport";
	
	/** Date formatter used during file import. */
	@SuppressWarnings("unused")
	private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
	

	/** Repository for storing {@link PowerPlant} */
	@Inject
	@Named("local")
	private ComponentRepo componentRepo;

	/** Default message source. */
	@Inject
	private VppReloadableResourceBundleMessageSource messageSource;

	/**
	 * Handles a file that was recognized by spring integration and generates a {@link FINESCEComponent} for each file.
	 *
	 * @param input
	 * 		the file to handle ( mostly found by spring integration)
	 *
	 * @return true, if the file could be imported, false else
	 */
	@ServiceActivator
	@CacheEvict(value = "fINESCEComponent", allEntries = true)
	public boolean handleFile (final File input) {
		if (!validFile(input)) {
			return false;
		}
		LOG.debug("Handling file import for file: " + input.getAbsolutePath());

		// replace any '/' at the end of the root data folder
		final String componentDataRootFolder = messageSource.getProperty(PROPERTY_VPP_DATA_ROOT_FOLDER).replaceAll("/$", "");
		final String componentBaseUrl = messageSource.getProperty(PROPERTY_BASE_URL).replaceAll("/$", "");
		final String filePath = input.getAbsolutePath().substring(componentDataRootFolder.length() + 1);
		if (filePath.contains(NO_IMPORT) || filePath.contains(".DS_Store")){
			return false;
		}
		final String[] componentMetaData = filePath.split("/");
		int len = componentMetaData.length;
		
		if (len < 4){
			return false;
		}
		final String componentSide = componentMetaData[len - 4].toLowerCase();
		final String componentName = componentMetaData[len - 3];
		final String componentType = componentMetaData[len - 2].toLowerCase();
		String day = componentMetaData[len-1];
		if (day.contains(".")){
			LOG.debug("Day = " + day);
			day = componentMetaData[len-1].substring(0, componentMetaData[len -1].length() - 4);
		}
		
		final FINESCEComponent component = new FINESCEComponent(componentName, componentBaseUrl);
		long date;
		try {
			// parse the string form the path into a date object and convert it to a unix timestamp,
			// i.e. a simple long
			date = Long.valueOf(day); //formatter.parseLocalDateTime(day).toDateTime(DateTimeZone.UTC).getMillis();
		} catch (IllegalArgumentException e) {
			LOG.error("Could not parse date of folder " + input.getAbsolutePath());
			return false;
		}

		ComponentDataFileWrapper componentDataFileWrapper = null;
		
		DataType type = null;
		ComponentSide side = null;
		
		try{
			type = DataType.valueOf(componentType);
		}catch(IllegalArgumentException e){
			LOG.error("Data type is neither 'planned' nor 'reached'" + input.getAbsolutePath());
			return false;
		}
		
		try{
			side = ComponentSide.valueOf(componentSide);
		}catch(IllegalArgumentException e){
			LOG.error("The component is neider from the 'generation' nor 'compsumtion' side" + input.getAbsolutePath());
			return false;
		}

		componentDataFileWrapper = new ComponentDataFileWrapper(input, type, date);


		componentRepo.updateOrAddComponent(component, componentDataFileWrapper, side);
		return true;
	}

	/**
	 * Handles the import of data for a given file. The file contains semi colon separated time value samples of
	 * component data
	 *
	 * @param input
	 * 		the file to import
	 *
	 * @return A String with the contents imported from the given file. Can be null if an error occur
	 */
	public String handleDataImport (final File input) {
		
		LOG.info("Importing data from file: " + input.getAbsolutePath());

		String data = null;
		try {
			data = new String(Files.readAllBytes(input.toPath()), StandardCharsets.UTF_8);
		} catch (IOException e) {
			LOG.error("Not possible to read from file " + input.getAbsolutePath());
			e.printStackTrace();
		}

		return data;
	}
	
	/**
	 * Writes the requested data to the file system
	 * 
	 * @param componentId
	 * @param side
	 * @param date
	 * @param dataType
	 * @param data
	 * 
	 * @return true if the file already existed, false otherwise
	 * @throws IOException 
	 */
	public boolean handleDataWritting(String componentId,
			ComponentSide side, long date, DataType dataType, String data) throws IOException{
		String componentDataRootFolder = messageSource.getProperty(PROPERTY_VPP_DATA_ROOT_FOLDER).replaceAll("/$", "");
		String completeRoute = String.format("%s/%s/%s/%s/%d.csv", componentDataRootFolder, side.toString(), 
				componentId, dataType.toString(), date);
		LOG.debug("Writing file " + completeRoute);
		File file = new File(completeRoute);
		return handleDataWritting(file, data);
	}
	
	
	/**
	 * Writes the requested data to the file system
	 * 
	 * @param file
	 * @param data
	 * 
	 * @return true if the file already existed, false otherwise
	 * @throws IOException 
	 */
	public boolean handleDataWritting(File file, String data) throws IOException{
		LOG.debug("Writing file " + file.getAbsolutePath());
		Boolean exist = file.exists();
		FileWriter writer = null;
		try{
			if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()){
				throw new IOException("Not possible to create the parent directories");
			}
			if (!exist && !file.createNewFile()){
				throw new IOException("Not possible to create the file");
			}
			writer = new FileWriter(file);
			writer.write(data);
		}catch (IOException e){
			LOG.error("Not possible to write to file");
		}finally{
			if (writer !=null)
				writer.close();
		}
		handleFile(file);
		return exist;
	}

	/**
	 * Helper who checks whether a given file is a valid one, i.e. not null and must exists.
	 *
	 * @param input
	 * 		the file to check
	 *
	 * @return true if
	 *         valid, false else
	 */
	private boolean validFile (final File input) {
		if (input == null) {
			LOG.error("Input file is null.");
			return false;
		}
		if (!input.exists()) {
			LOG.error("Input file does not exists: " + input.getPath());
			return false;
		}
		return true;
	}
	
}
