package qsmart.java.library.util.spring;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Extension of the default {@link ReloadableResourceBundleMessageSource} which simplifies access to dnsa properties.
 *
 * @author T. Kudla <t.kudla@tarent.de>
 */
public class VppReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

    /**
     * Logger used within this class.
     */
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(VppReloadableResourceBundleMessageSource.class);

    /**
     * Property getter for a valid property key. If no key is available, then the key will be returned.
     *
     * @param property key of the property
     * @return value if available, else the key
     */
    public String getProperty(final String property) {
        return getMessage(property, null, Locale.getDefault());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultMessage(final String code) {
        return code;
    }

}
