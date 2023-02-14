package oracle.communications.inventory.c2a.impl;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;

public class ExtensionUtils {
	private static Log log = LogFactory.getLog(ExtensionUtils.class);
	private static Map<String, Properties> registries = new HashMap<String, Properties>();

	/**
	 * Instantiate an implementation of an extension that is identified by a
	 * registry appropriate for handling an entity that has a given
	 * specification.
	 * 
	 * @param registryName
	 *            the name of an implementation class registry corresponds to an
	 *            extension point and an interface
	 * @param specName
	 *            the specification name of the entity, which will be handled by
	 *            an appropriate implementation class
	 * @return a new instance of the implementation class
	 * @throws ClassNotFoundException
	 *             the registry identifies an implementation class that does not
	 *             exist
	 * @throws InstantiationException
	 *             the implementation class cannot be instantiated
	 * @throws IllegalAccessException
	 *             no permission to instantiate the implementation class
	 */
	public static <InterfaceType> InterfaceType getImplementation(
	        String registryName, String specName)
	        throws ClassNotFoundException, InstantiationException,
	        IllegalAccessException {
		Properties registry = null;
		synchronized (registries) {
			registries.get(registryName);
			if (registry == null) {
				registry = loadRegistry(registryName);
			}
		}
		String implementationClass = registry.getProperty(specName);
		if (implementationClass == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
        Class<? extends InterfaceType> klass = (Class<? extends InterfaceType>) Class
		        .forName(implementationClass);
		return klass.newInstance();
	}

	protected static Properties loadRegistry(String registryDirName) {
		Properties registry = new Properties();
		registries.put(registryDirName, registry);
		ClassLoader classLoader = Thread.currentThread()
		        .getContextClassLoader();
		URL dirURL = classLoader.getResource(registryDirName);
		try {
			if (dirURL == null) {
				throw new FileNotFoundException(registryDirName);
			}
			if (!dirURL.getProtocol().equals("file")) {
				throw new URISyntaxException(dirURL.toString(),
				        "expecting file protocol");
			}
			File dirFile = new File(dirURL.toURI());
			for (String child : dirFile.list()) {
				InputStream inStream =  null;
				try {
					File childFile = new File(dirFile, child);
					inStream = new FileInputStream(childFile);
					registry.load(inStream);
				} catch (IOException e) {
					log.warn("c2a.registryLoadError", e, registryDirName, child);
				} finally {
					if (inStream != null) {
						inStream.close();
					}				
				}
			}
		} catch (Exception e) {
			log.error("c2a.registryNotFound", e, registryDirName);
		}
		return registry;
	}
}
