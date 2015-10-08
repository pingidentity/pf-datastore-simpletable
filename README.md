#pf-datastore-simpletable

### Overview

PingFederate custom datastore that uses a table of user information (configured in the datastore configuration settings) to return user information. This can be used for testing and development purposes to return different user details in a transaction.


### System Requirements / Dependencies

Requires:
 - PingFederate 7.2.x or higher
 - Apache Commons logging

 
### Installation
 
1. Compile the plugin in (refer to the [PingFederate server SDK documentation] for details on compiling PingFederate plug-ins)
2. Copy the resulting .jar file to the <pf_home>/server/default/deploy folder (on all nodes and admin instances).
3. Restart PingFederate
 
[PingFederate server SDK documentation]: http://documentation.pingidentity.com/display/PF/SDK+Developer%27s+Guide


### Configuration

Once the plug-in has been deployed and the PingFederate instance restarted, launch the PingFederate admin console:

1. Add a new datastore instance under: Server Configuration > Data Stores
2. Select "Custom" from the options
3. Name the instance and select the appropriate datastore from the "type" list
4. Refer to the inline documentation to configure the datastore


### Disclaimer

This software is open sourced by Ping Identity but not supported commercially as such. Any questions/issues should go to the Github issues tracker or discuss on the [Ping Identity developer communities] . See also the DISCLAIMER file in this directory.

[Ping Identity developer communities]: https://community.pingidentity.com/collaborate