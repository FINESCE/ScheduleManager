package qsmart.java.library.util.spring;

import static java.lang.System.currentTimeMillis;

import java.io.File;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.stereotype.Component;

/**
 * Spring integration filter for allowing files to be process which are at least some time old,
 * and are not actually being written to the file system. This should prevent loading power plant data from files
 * which are not completely written.
 * 
 * Author: T. Kudla <t.kudla@tarent.de>
 * Date: 6/13/13
 * Time: 10:36 AM
 */
@Component
public class LastModifiedAcceptOnceFileListFilter<F> extends AcceptOnceFileListFilter<F> {

	/** Default Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(LastModifiedAcceptOnceFileListFilter.class);

	/** Property key for the delay. */
	private static final String PROPERTY_PROCESS_FILE_AFTER_SECONDS = "vpp.process.file.after.seconds";

	/** Default Message Source. */
	@Inject
	private VppReloadableResourceBundleMessageSource messageSource;

	/**
	 * Checks weather this file is a least some time on the file system available and had not be changed for some time.
	 *
	 * @param file
	 * 		the file to check
	 *
	 * @return true, if the files last modified value ist at least some time old, false else
	 */

	@Override
	public boolean accept (final F file) {
		LOG.trace("Checking file for import: " + file.toString());
		if (file instanceof File) {
			final File castedFile = (File) file;
			try {
				final long delay = Long.parseLong(messageSource.getProperty(PROPERTY_PROCESS_FILE_AFTER_SECONDS));
				if ((currentTimeMillis() - delay) > castedFile.lastModified()) {
					return super.accept(file);
				}
			} catch (NumberFormatException e) {
				LOG.info("Could not parse property " + PROPERTY_PROCESS_FILE_AFTER_SECONDS + ", " +
						"allowing file [" + ((File) file).getPath() + "] to be parsed.", e);
				return super.accept(file);
			}
		}
		return false;
	}
}
