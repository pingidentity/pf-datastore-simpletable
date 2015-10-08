package com.pingidentity.datastore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sourceid.saml20.adapter.attribute.AttributeValue;
import org.sourceid.saml20.adapter.conf.Configuration;
import org.sourceid.saml20.adapter.conf.Row;
import org.sourceid.saml20.adapter.conf.SimpleFieldList;
import org.sourceid.saml20.adapter.conf.Table;
import org.sourceid.saml20.adapter.gui.AdapterConfigurationGuiDescriptor;
import org.sourceid.saml20.adapter.gui.TextFieldDescriptor;
import org.sourceid.saml20.adapter.gui.TableDescriptor;

import com.pingidentity.sources.CustomDataSourceDriver;
import com.pingidentity.sources.CustomDataSourceDriverDescriptor;
import com.pingidentity.sources.SourceDescriptor;
import com.pingidentity.sources.gui.FilterFieldsGuiDescriptor;


public class SimpleTableDataStore implements CustomDataSourceDriver
{
	private static final String DATA_SOURCE_NAME = "Simple Table Data Store";
	private static final String DATA_SOURCE_CONFIG_DESC = "Configuration settings for the simple user lookup table data store.";

    private static final String CONFIG_USERENTRY_TABLE_NAME = "Sample Data";
    private static final String CONFIG_USERENTRY_TABLE_DESC = "List of simple data entries.";

    private static final String CONFIG_USERENTRY_USERNAME_NAME = "Username";
	private static final String CONFIG_USERENTRY_USERNAME_DESC = "Username or ID value.";   
        
    private static final String CONFIG_USERENTRY_FIRSTNAME_NAME = "FirstName";
    private static final String CONFIG_USERENTRY_FIRSTNAME_DESC = "First name";
                
    private static final String CONFIG_USERENTRY_LASTNAME_NAME = "LastName";
    private static final String CONFIG_USERENTRY_LASTNAME_DESC = "Last name";
                        
    private static final String CONFIG_USERENTRY_EMAIL_NAME = "Email";
    private static final String CONFIG_USERENTRY_EMAIL_DESC = "Email address";
                                 
    private static final String CONFIG_USERENTRY_PHONE_NAME = "Phone";
    private static final String CONFIG_USERENTRY_PHONE_DESC = "Telephone number";

    private static final String CONFIG_USERENTRY_ROLE_NAME = "Role";
    private static final String CONFIG_USERENTRY_ROLE_DESC = "Access role";

    private static final String CONFIG_FILTER_NAME = "Username to lookup";
    private static final String CONFIG_FILTER_DESC = "Username to lookup in table (ie ${Username} value from HTML Form Adapter)";
    
    private Log log = LogFactory.getLog(this.getClass());

    private final CustomDataSourceDriverDescriptor descriptor;

    private static Map<String, UserEntry> userEntries = new HashMap<String, UserEntry>();
	

    public SimpleTableDataStore()
    {
        // create the configuration descriptor for our custom data store
        AdapterConfigurationGuiDescriptor dataStoreConfigGuiDesc = new AdapterConfigurationGuiDescriptor(DATA_SOURCE_CONFIG_DESC);

        // Create the table to hold the user entries
        TableDescriptor userentryTable = new TableDescriptor(CONFIG_USERENTRY_TABLE_NAME, CONFIG_USERENTRY_TABLE_DESC);
		TextFieldDescriptor userentryUserName = new TextFieldDescriptor(CONFIG_USERENTRY_USERNAME_NAME, CONFIG_USERENTRY_USERNAME_DESC);
		TextFieldDescriptor userentryFirstName = new TextFieldDescriptor(CONFIG_USERENTRY_FIRSTNAME_NAME, CONFIG_USERENTRY_FIRSTNAME_DESC);
		TextFieldDescriptor userentryLastName = new TextFieldDescriptor(CONFIG_USERENTRY_LASTNAME_NAME, CONFIG_USERENTRY_LASTNAME_DESC);
		TextFieldDescriptor userentryEmail = new TextFieldDescriptor(CONFIG_USERENTRY_EMAIL_NAME, CONFIG_USERENTRY_EMAIL_DESC);
		TextFieldDescriptor userentryPhone = new TextFieldDescriptor(CONFIG_USERENTRY_PHONE_NAME, CONFIG_USERENTRY_PHONE_DESC);
		TextFieldDescriptor userentryRole = new TextFieldDescriptor(CONFIG_USERENTRY_ROLE_NAME, CONFIG_USERENTRY_ROLE_DESC);
		userentryTable.addRowField(userentryUserName);
		userentryTable.addRowField(userentryFirstName);
		userentryTable.addRowField(userentryLastName);
		userentryTable.addRowField(userentryEmail);
		userentryTable.addRowField(userentryPhone);
		userentryTable.addRowField(userentryRole);
		dataStoreConfigGuiDesc.addTable(userentryTable);
        
        // Add the configuration field for the LDAP Filter
        FilterFieldsGuiDescriptor filterFieldsDescriptor = new FilterFieldsGuiDescriptor();
        filterFieldsDescriptor.addField(new TextFieldDescriptor(CONFIG_FILTER_NAME, CONFIG_FILTER_DESC));
        
        descriptor = new CustomDataSourceDriverDescriptor(this, DATA_SOURCE_NAME, dataStoreConfigGuiDesc, filterFieldsDescriptor);
    }

    @Override
    public SourceDescriptor getSourceDescriptor()
    {
        return descriptor;
    }

	@Override
    public void configure(Configuration configuration)
    {
    	log.debug("---[ Configuring Simple Table Data Store ]------");
	    Table userEntryTable = configuration.getTable(CONFIG_USERENTRY_TABLE_NAME);
	    List<Row> userentryRows = userEntryTable.getRows();
	    
	    if (userentryRows != null) {
	    	for (Row userEntryRow : userentryRows) {
	    		String Username = userEntryRow.getFieldValue(CONFIG_USERENTRY_USERNAME_NAME);
	    		String FirstName = userEntryRow.getFieldValue(CONFIG_USERENTRY_FIRSTNAME_NAME);
	    		String LastName = userEntryRow.getFieldValue(CONFIG_USERENTRY_LASTNAME_NAME);
	    		String Email = userEntryRow.getFieldValue(CONFIG_USERENTRY_EMAIL_NAME);
	    		String Phone = userEntryRow.getFieldValue(CONFIG_USERENTRY_PHONE_NAME);
	    		String Role = userEntryRow.getFieldValue(CONFIG_USERENTRY_ROLE_NAME);
	    		
	    		if (!userEntries.containsKey(Username)) {
		    		userEntries.put(Username, new UserEntry(Username, FirstName, LastName, Email, Phone, Role));
	    		}
	    	}
	    }
	    
    }

    @Override
    public boolean testConnection()
    {
		// test connectivity - not applicable to this data source
		return true;
    }

    @Override
    public Map<String, Object> retrieveValues(Collection<String> attributeNamesToFill, SimpleFieldList filterConfiguration)
    {
    	log.debug("---[ Retrieving Values ]------");

		String username = filterConfiguration.getFieldValue(CONFIG_FILTER_NAME);

		if (userEntries.containsKey(username)) {
			UserEntry returnEntry = userEntries.get(username);
			return returnEntry.getUserObject();
		} else {
			return new HashMap<String, Object>();
		}
    }

    @Override
    public List<String> getAvailableFields()
    {
        return new ArrayList<String>(Arrays.asList(CONFIG_USERENTRY_USERNAME_NAME, CONFIG_USERENTRY_FIRSTNAME_NAME, CONFIG_USERENTRY_LASTNAME_NAME, CONFIG_USERENTRY_EMAIL_NAME, CONFIG_USERENTRY_PHONE_NAME, CONFIG_USERENTRY_ROLE_NAME)) ;
    }
    
    private class UserEntry {
    
    	public String Username;
    	public String FirstName;
    	public String LastName;
    	public String Email;
    	public String Phone;
    	public String Role;
    
    	public UserEntry(String inUsername, String inFirstName, String inLastName, String inEmail, String inPhone, String inRole) {
    		this.Username = inUsername;
    		this.FirstName = inFirstName;
    		this.LastName = inLastName;
    		this.Email = inEmail;
    		this.Phone = inPhone;
    		this.Role = inRole;
    	}
    
    	public Map<String, Object> getUserObject() {

            Map<String, Object> returnObject = new HashMap<String, Object>();
            
            returnObject.put(CONFIG_USERENTRY_USERNAME_NAME, new AttributeValue(Username));
            returnObject.put(CONFIG_USERENTRY_FIRSTNAME_NAME, new AttributeValue(FirstName));
            returnObject.put(CONFIG_USERENTRY_LASTNAME_NAME, new AttributeValue(LastName));
            returnObject.put(CONFIG_USERENTRY_EMAIL_NAME, new AttributeValue(Email));
            returnObject.put(CONFIG_USERENTRY_PHONE_NAME, new AttributeValue(Phone));
            returnObject.put(CONFIG_USERENTRY_ROLE_NAME, new AttributeValue(Role));

            return returnObject;
    	}
    }
}
