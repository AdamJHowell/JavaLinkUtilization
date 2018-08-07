package com.adamjhowell.snmpinterface;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


/**
 * This program will open two SNMP walk files, locate and display each interface, and allow the user to select a displayed interface.<br>
 * When an interface is selected, the program will calculate the utilization of that interface from the values in each walk.
 * <p>
 * You will need the GSON library in your classpath to build this project.
 * <p>
 * The OIDs in the input files are expected to be in numerical (dot notation) format.<br>
 * However, it would not take much work to convert to ASN.1 or OID-IRI notation.<br>
 * If named identifiers are desired, please contact me, and we can discuss formatting.<br>
 * This means each line will begin with a dotted-decimal identifier.  Some of those identifiers are listed below.<br>
 * The lines will not contain tabs between tokens, but spaces.  Tabs have not been tested, but may work.<br>
 * Counters are currently limited to 32 bits in size.  64-bit counters may be implemented later.
 * <p>
 * The generic formula for utilization is: ( delta-octets * 8 * 10 ) / ( delta-seconds * ifSpeed )<br>
 * That formula will work for inbound, outbound, or bidirectional.  You only need to select the appropriate delta-octets.
 * <p>
 * More information can be found here:
 * <a href="http://www.cisco.com/c/en/us/support/docs/ip/simple-network-management-protocol-snmp/8141-calculate-bandwidth-snmp.html">Cisco bandwidth calculation</a>
 * <p>
 * Here are the pertinent OIDs for this program:<br>
 * SYSUPTIMEOID = ".1.3.6.1.2.1.1.3.0";			// The OID for sysUpTime (System UpTime)<br>
 * IFINDEXOID = ".1.3.6.1.2.1.2.2.1.1.";	    	// The OID for ifIndex (Interface Index)<br>
 * IFDESCROID = ".1.3.6.1.2.1.2.2.1.2.";	   	// The OID for ifDescr (Interface Description)<br>
 * IFSPEEDOID = ".1.3.6.1.2.1.2.2.1.5.";	     // The OID for ifSpeed (Interface Speed)<br>
 * IFINOCTETSOID = ".1.3.6.1.2.1.2.2.1.10.";		// The OID for ifInOctets (Interface Inbound Octet Count)<br>
 * IFINDISCARDSOID = ".1.3.6.1.2.1.2.2.1.13."; 	// The OID for ifInDiscards (Interface Inbound Discards)<br>
 * IFINERRORSOID = ".1.3.6.1.2.1.2.2.1.14.";		// The OID for ifInErrors (Interface Inbound Errors)<br>
 * IFOUTOCTETSOID = ".1.3.6.1.2.1.2.2.1.16.";  	// The OID for ifOutOctets (Interface Outbound Octet Count)<br>
 * IFOUTDISCARDSOID = ".1.3.6.1.2.1.2.2.1.19.";	// The OID for ifOutDiscards (Interface Outbound Discards)<br>
 * IFOUTERRORSOID = ".1.3.6.1.2.1.2.2.1.20.";     // The OID for ifOutErrors (Interface Outbound Errors)<br>
 * COUNTER32MAX = 4294967295;					// The maximum value a Counter32 can hold.<br>
 * COUNTER64MAX = 18446744073709551615;	     	// The maximum value a Counter64 can hold.<br>
 * <p>
 * Many of my variable names will begin with 'if'.  In the SNMP world, 'if' typically represents an interface (NIC).<br>
 * SNMP OIDs research links:<br>
 * <a href="https://snmp.cloudapps.cisco.com/Support/SNMP/do/BrowseOID.do">Cisco OID browser</a><br>
 * <a href="http://www.oid-info.com/">OID Info browser</a><br>
 * <a href="http://www.alvestrand.no/objectid/top.html">Alvestrand Data OID browser</a><br>
 */


public class Main extends Application
{

	public static void main( String[] args )
	{
		launch( args );
	} // End of main() method.


	/**
	 * This sets the stage and the scene for the program.<br>
	 * Created by Adam Howell on 2016-05-10.
	 *
	 * @param primaryStage the stage on which all JavaFX elements will be placed.
	 * @throws Exception if FXML file is not found.
	 */
	@Override
	public void start( Stage primaryStage ) throws Exception
	{
		// Load the FXML file containing all of the UI elements.
		Parent rootNode = new FXMLLoader( getClass().getResource( "../../../view/RootLayout.fxml" ) ).load();

		// Create the stage and set the window title.
		primaryStage.setTitle( "SNMP Link Utilization" );

		// Set the scene using root, with the specified width and height.
		primaryStage.setScene( new Scene( rootNode, 500, 600 ) );

		// Set the icon for a non-Maven build: "file:resources/images/nic.png"
		// Set the icon for a Maven build.
		primaryStage.getIcons().add( new Image( "images/nic.png" ) );

		primaryStage.show();
	} // End of start() method.
}
