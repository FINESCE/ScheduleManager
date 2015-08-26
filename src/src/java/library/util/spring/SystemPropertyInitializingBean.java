package qsmart.java.library.util.spring;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Bean for automatically initializing System properties from within a Spring context.
 *
 * @author T. Kudla <t.kudla@tarent.de>
 *
 */
@SuppressWarnings("rawtypes")
public class SystemPropertyInitializingBean implements InitializingBean, ResourceLoaderAware {

    /**
     * Logger used within this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SystemPropertyInitializingBean.class);

    /**
     * System properties to be set.
     */
    private Map<String,String> systemProperties;

    /**
     * {@link ResourceLoader} which will be used to get path to files that will be set as system property.
     */
    private ResourceLoader resourceLoader;

    @Override
    public void afterPropertiesSet() {
        if (systemProperties == null || systemProperties.isEmpty()) {
            LOG.debug("No system properties defined for initialization.");
            return;
        }
        Iterator i = systemProperties.keySet().iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            String value = (String) systemProperties.get(key);
            try {
                // using resourceLoader to get a resource, which will be
                // automatically resolved by file, classpath, application
                // context path, than checking if it exists, if not, this system
                // property is not a resource path
                Resource resource = resourceLoader.getResource(value);
                if (resource.exists()) {
                    value = resource.getFile().getAbsolutePath();
                } else {
                    value = (String) systemProperties.get(key);
                }
            } catch (IOException e) {
                LOG.error("An error occurred while trying to resolve resource for system property. Key: " + key
                        + " with value" + value);
            }

            System.setProperty(key, value);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting system property: " + key + "to value: " + value);
            }
        }
    }

    /**
     *
     * @param newSystemProperties
     *            Map of system properties to set
     */
    @SuppressWarnings("unchecked")
	public void setSystemProperties(final Map newSystemProperties) {
        this.systemProperties = newSystemProperties;
    }

    /**
     * @param newResourceLoader
     *            setter for {@link ResourceLoader}, which will be used to get path to property files
     */
    @Override
    public void setResourceLoader(final ResourceLoader newResourceLoader) {
        this.resourceLoader = newResourceLoader;
    }

}
