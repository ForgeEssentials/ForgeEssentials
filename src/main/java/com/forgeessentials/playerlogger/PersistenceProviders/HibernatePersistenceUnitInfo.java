package com.forgeessentials.playerlogger.PersistenceProviders;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;

public class HibernatePersistenceUnitInfo implements PersistenceUnitInfo {
    
    public static String JPA_VERSION = "2.2";
    private String persistenceUnitName;
    private List<String> managedClassNames;
    private Properties properties;
    
    public HibernatePersistenceUnitInfo(
      String persistenceUnitName, List<String> managedClassNames, Properties properties) {
        this.persistenceUnitName = persistenceUnitName;
        this.managedClassNames = managedClassNames;
        this.properties = properties;
    }

	@Override
	public void addTransformer(ClassTransformer arg0) {	
	}

	@Override
	public boolean excludeUnlistedClasses() {
		return false;
	}

	@Override
	public ClassLoader getClassLoader() {
		return null;
	}

	@Override
	public List<URL> getJarFileUrls() {
		return Collections.emptyList();
	}

	@Override
	public javax.sql.DataSource getJtaDataSource() {
		return null;
	}

	@Override
	public List<String> getManagedClassNames() {
		return managedClassNames;
	}

	@Override
	public List<String> getMappingFileNames() {
		return null;
	}

	@Override
	public ClassLoader getNewTempClassLoader() {
		return null;
	}

	@Override
	public javax.sql.DataSource getNonJtaDataSource() {
		return null;
	}

	@Override
	public String getPersistenceProviderClassName() {
		return "org.hibernate.jpa.HibernatePersistenceProvider";
	}

	@Override
	public String getPersistenceUnitName() {
		return persistenceUnitName;
	}

	@Override
	public URL getPersistenceUnitRootUrl() {
		return null;
	}

	@Override
	public String getPersistenceXMLSchemaVersion() {
		return null;
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	@Override
	public SharedCacheMode getSharedCacheMode() {
		return null;
	}

	@Override
	public PersistenceUnitTransactionType getTransactionType() {
		return PersistenceUnitTransactionType.RESOURCE_LOCAL;
	}

	@Override
	public ValidationMode getValidationMode() {
		return null;
	}
}
