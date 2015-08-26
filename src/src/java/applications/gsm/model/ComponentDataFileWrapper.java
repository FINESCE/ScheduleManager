package qsmart.java.applications.gsm.model;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * POJO for a file containing power plant data for a specific power plant.
 * <p/>
 * Author: M. Munoz
 */
public class ComponentDataFileWrapper {
	
	/** Default Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(ComponentDataFileWrapper.class);

	/** The file containing data. */
	private File file;

	/** The date as a long of the format yyyyMMdd of this file, i.e. which day this data represents. */
	private long date;

	/** The {@link PowerPlantData.PowerPlantDataType} this file represents. */
	private FINESCEComponent.DataType dataType;
	
	/** The {@link String} containing the url to the data when reading from a remote File System. */
	private String reading;
	
	/** Long representing the time in milliseconds when the remote file was modified */
	private long lastModified = 0;

	/**
	 * Standard constructor.
	 *
	 * @param file
	 * 		the file containing the data
	 * @param dataType
	 * 		the {@link FINESCEComponent.DataType} of the data
	 * @param date
	 * 		the date as a long of the format yyyyMMdd of the day of this data file
	 */
	public ComponentDataFileWrapper (final File file, final FINESCEComponent.DataType dataType, final long date) {
		this.file = file;
		this.date = date;
		this.dataType = dataType;
	}
	
	/**
	 * Standard constructor for remote imports.
	 *
	 * @param file
	 * 		the file containing the data
	 * @param dataType
	 * 		the {@link PowerPlantData.PowerPlantDataType} of the data
	 * @param unixTimestamp
	 * 		the unix unixTimestamp of the day of this data file
	 */
	public ComponentDataFileWrapper (String file, final FINESCEComponent.DataType dataType, final long date) {
		this.setReading(file);
		this.file = new File(file);
		this.date = date;
		this.dataType = dataType;
	}

	/**
	 * Getter
	 *
	 * @return the file containing the data
	 */
	public File getFile () {
		return file;
	}

	/**
	 * Getter
	 *
	 * @return the date of the day this data file represents
	 */
	public long getDate () {
		if (LOG.isTraceEnabled()) {
			LOG.trace("date: " + date);
		}
		return date;
	}

	/**
	 * Getter
	 *
	 * @return the {@link PowerPlantData.PowerPlantDataType} this file represents
	 */
	public FINESCEComponent.DataType getDataType () {
		return dataType;
	}

	public String getReading() {
		return reading;
	}

	public void setReading(String reading) {
		this.reading = reading;
	}

	/**
	 * @return the lastModified time in milliseconds
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified time in milliseconds to set
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	

	/**
	 * Tests if the file wrapped by this object still exists.
	 * 
	 * @return true if the contained file exists, false otherwise
	 */
	public boolean stillExists() {
		return file.exists();
	}

	@Override
	public boolean equals (final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ComponentDataFileWrapper)) {
			return false;
		}

		final ComponentDataFileWrapper that = (ComponentDataFileWrapper) o;

		if (date != that.date) {
			return false;
		}
		if (dataType != that.dataType) {
			return false;
		}
		if (file != null ? !file.equals(that.file) : that.file != null) {
			return false;
		}
		
		if (lastModified != that.getLastModified()){
			return false;
		}
		
		if (reading != null ? !reading.equals(that.reading) : that.reading != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode () {
		int result = file != null ? file.hashCode() : 0;
		result = 31 * result + (int) (date ^ (date >>> 32));
		result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
		return result;
	}

	@Override
	public String toString () {
		return "PowerPlantDataFileWrapper{" +
				"file=" + file +
				", date=" + date +
				", dataType=" + dataType +
				'}';
	}
}
