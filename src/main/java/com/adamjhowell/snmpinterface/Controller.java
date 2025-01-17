package com.adamjhowell.snmpinterface;


import com.adamjhowell.snmpinterface.model.InterfaceStats;
import com.adamjhowell.snmpinterface.model.SnmpInterface;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * This will do all of the computational work and event handling.<br>
 * The class variables can be converted from numerical (dot format) to names.
 * The class variables can be modified to suit SNMP walks that use names instead of numbers.
 * Gson will need to be added to the project classpath.
 * <p>
 * Created by Adam Howell on 2016-06-08.
 */
public class Controller
{
	/**
	 * A textual description of the entity.
	 * This value should include the full name and version identification of the system's hardware type, software operating-system, and networking software.
	 * It is mandatory that this only contain printable ASCII characters.
	 */
	private static final String SYS_DESCR = ".1.3.6.1.2.1.1.1.0";
	/**
	 * The time (in hundredths of a second) since the network management portion of the system was last re-initialized.
	 * For many manufacturers, this will be reset when the network stack is restarted.
	 */
	private static final String SYS_UPTIME_OID = ".1.3.6.1.2.1.1.3.0";
	/**
	 * An administratively-assigned name for this managed node.  By convention, this is the node's fully-qualified domain name.
	 */
	private static final String SYS_NAME = ".1.3.6.1.2.1.1.5.0";
	/**
	 * A unique value for each interface.
	 * The value for each interface must remain constant at least from one re-initialization of the entity's network management system to the next re-initialization.
	 */
	private static final String IF_INDEX_OID = ".1.3.6.1.2.1.2.2.1.1.";
	/**
	 * A textual string containing information about the interface.
	 * This string should include the name of the manufacturer, the product name and the version of the hardware interface.
	 */
	private static final String IF_DESCRIPTION_OID = ".1.3.6.1.2.1.2.2.1.2.";
	/**
	 * The nominal bandwidth of the interface.
	 * If this is set to zero or less, the utilization cannot be calculated.
	 */
	private static final String IF_SPEED_OID = ".1.3.6.1.2.1.2.2.1.5.";
	/**
	 * The total number of octets received on the interface, including framing characters.
	 * This will wrap at either a 32-bit or 64-bit value.
	 */
	private static final String IF_IN_OCTETS_OID = ".1.3.6.1.2.1.2.2.1.10.";
	/**
	 * The number of inbound packets which were chosen to be discarded even though no errors had been detected to prevent their being deliverable to a higher-layer protocol.
	 * One possible reason for discarding such a packet could be to free up buffer space.
	 * This will wrap at either a 32-bit or 64-bit value.
	 */
	private static final String IF_IN_DISCARDS_OID = ".1.3.6.1.2.1.2.2.1.13.";
	/**
	 * The number of inbound packets that contained errors preventing them from being deliverable to a higher-layer protocol.
	 * This will wrap at either a 32-bit or 64-bit value.
	 */
	private static final String IF_IN_ERRORS_OID = ".1.3.6.1.2.1.2.2.1.14.";
	/**
	 * The total number of octets transmitted out of the interface, including framing characters.
	 * This will wrap at either a 32-bit or 64-bit value.
	 */
	private static final String IF_OUT_OCTETS_OID = ".1.3.6.1.2.1.2.2.1.16.";
	/**
	 * The number of outbound packets which were chosen to be discarded even though no errors had been detected to prevent their being transmitted.
	 * One possible reason for discarding such a packet could be to free up buffer space.
	 * This will wrap at either a 32-bit or 64-bit value.
	 */
	private static final String IF_OUT_DISCARDS_OID = ".1.3.6.1.2.1.2.2.1.19.";
	/**
	 * The number of outbound packets that could not be transmitted because of errors.
	 * This will wrap at either a 32-bit or 64-bit value.
	 */
	private static final String IF_OUT_ERRORS_OID = ".1.3.6.1.2.1.2.2.1.20.";
	/**
	 * This sets the value to wrap the counters at.
	 */
	private static final long COUNTER32MAX = 4294967295L;

	/**
	 * The logging system used.
	 */
	private static final Logger errorLogger = Logger.getLogger( Main.class.getName() );
	/**
	 * Sample data for the interface table.
	 */
	private final ObservableList<SnmpInterface> interfaceObservableData = FXCollections.observableArrayList(
		new SnmpInterface( 42L, "Sample data." ),
		new SnmpInterface( 42L, "Press the..." ),
		new SnmpInterface( 42L, "...'Show Interfaces' button" ) );
	@FXML private GridPane rootNode;
	@FXML private TextField firstFile;
	@FXML private TextField secondFile;
	@FXML private Button openWalk1Button;
	@FXML private Button openWalk2Button;
	@FXML private Button showInterfacesButton;
	@FXML private Button saveButton;
	@FXML private Button exitButton;
	@FXML private Label fileLabel;
	@FXML private Label promptLabel;
	@FXML private TableView<SnmpInterface> interfaceTableView;
	@FXML private TableColumn<SnmpInterface, String> ifIndexCol;
	@FXML private TableColumn<SnmpInterface, String> ifDescCol;
	@FXML private TableView<InterfaceStats> statisticTableView;
	@FXML private TableColumn<InterfaceStats, String> statDescrCol;
	@FXML private TableColumn<InterfaceStats, String> statValueCol;


	/**
	 * This method will take a String representing a file name, and attempt to open it.<br>
	 * If the file can be opened, every line that contains uncommented text will be read into an ArrayList.<br>
	 * That ArrayList of lines will be returned to the calling method.<br>
	 * If the file cannot be opened, this method will return an empty ArrayList.<br>
	 * Created by Adam Howell on 2016-05-04.
	 *
	 * @param inFileName A String representing a file to open.
	 * @return An ArrayList containing every line from the opened file.
	 */
	private static List<String> readFile( String inFileName )
	{
		// commentString can be changed to whatever you wish to use as a comment indicator.  When this String is encountered, the rest of the line will be ignored.
		String commentString = "//";
		List<String> inAl = new ArrayList<>();

		// Attempt to open the file using "try with resources", to ensure it will close automatically.
		try( BufferedReader inBR = new BufferedReader( new FileReader( inFileName ) ) )
		{
			String line;
			int inputLineCount = 0;

			// Read lines until EOF.
			while( ( line = inBR.readLine() ) != null )
			{
				inputLineCount++;
				// Check for comments.
				if( line.contains( commentString ) )
				{
					// Grab all of the text up to the comment.
					String subString = line.substring( 0, line.indexOf( commentString ) );

					// Only add lines with content.
					if( subString.length() > 0 )
					{
						// Add the line to our ArrayList.
						inAl.add( subString );
					}
					else
					{
						errorLogger.log( Level.FINEST, "readFile() is skipping a line that has only comments at row  {0}", inputLineCount );
					}
				}
				else
				{
					// Ignore empty lines and lines that contain only whitespace.
					if( line.length() > 0 && !line.matches( "\\s+" ) )
					{
						// Add the line to our ArrayList.
						inAl.add( line.trim() );
					}
					else
					{
						errorLogger.log( Level.FINEST, "readFile is skipping a zero length line at row {0}", inputLineCount );
					}
				}
			}
		}
		catch( IOException ioe )
		{
			ioe.getMessage();
		}
		return inAl;
	} // End of ReadFile() method.


	/**
	 * This will create two ArrayLists, one from each walk, and add every line that begins with a specific OID.<br>
	 * If those ArrayLists are identical, then it will find the IF_INDEX and ifDescr for each interface,
	 * create a SnmpInterface class object from them, and return an ArrayList of those objects.<br>
	 * Created by Adam Howell on 2016-05-05.
	 *
	 * @param walk1 The first WALK to search through.
	 * @param walk2 The second WALK to search through.
	 * @return An ObservableList of discovered indexes and descriptions.
	 */
	private static ObservableList<SnmpInterface> findInterfaces( List<String> walk1, List<String> walk2 )
	{
		ObservableList<SnmpInterface> observableIfIndexIfDescList = FXCollections.observableArrayList();
		// Add every line with an interface description OID.
		List<String> ifDescList1 = walk1.stream().filter( line -> line.contains( IF_DESCRIPTION_OID ) ).collect( Collectors.toList() );
		List<String> ifDescList2 = walk2.stream().filter( line -> line.contains( IF_DESCRIPTION_OID ) ).collect( Collectors.toList() );

		// If the two walks have the same interface description OIDs, we can proceed.
		if( ifDescList1.equals( ifDescList2 ) )
		{
			// Populate our map.
			ifDescList1.stream().filter( line -> line.startsWith( IF_DESCRIPTION_OID ) ).forEach( line ->
			                                                                                      {
				                                                                                      // Catch a NumberFormatException from parseLong(), if one occurs.
				                                                                                      try
				                                                                                      {
					                                                                                      // The interface index will start at position 21 and end one position before the first equal sign.
					                                                                                      // There may be rare cases where an OID will contain more than one equal sign.
					                                                                                      Long ifIndex = Long.parseLong( line.substring( 21, line.indexOf( " = " ) ) );
					                                                                                      // The interface description is in quotes, will start after the equal sign, and go to the end of the line.
					                                                                                      String ifDescr = line.substring( line.indexOf( " = " ) + 12, line.length() - 1 );

					                                                                                      // Create a SnmpInterface class object from those values.
					                                                                                      observableIfIndexIfDescList.add( new SnmpInterface( ifIndex, ifDescr ) );
				                                                                                      }
				                                                                                      catch( NumberFormatException nfe )
				                                                                                      {
					                                                                                      errorLogger.log( Level.SEVERE, "Exception: NumberFormatException trying parseLong()!" );
					                                                                                      nfe.getMessage();
				                                                                                      }
			                                                                                      } );

			// Return the populated container.
			return observableIfIndexIfDescList;
		}
		else
		{
			errorLogger.log( Level.SEVERE, "The SNMP walks appear to be from different machines.  This will prevent any calculations." );
			return null;
		}
	} // End of findInterfaces() method.


	/**
	 * This will analyze two data containers and produce human-readable output related to the differences between those containers.<br>
	 * Created by Adam Howell on 2016-05-10.
	 *
	 * @param walk1 The output from buildCompleteSNMPInterface for the first WALK.
	 * @param walk2 The output from buildCompleteSNMPInterface for the second WALK.
	 * @return An ObservableList containing all of the statistics for interface.
	 */
	@SuppressWarnings( "squid:S3776" )
	private static ObservableList<InterfaceStats> calculateStatistics( SnmpInterface walk1, SnmpInterface walk2 )
	{
		// The generic formula for inUtilization is: ( delta-octets * 8 * 10 ) / ( delta-seconds * ifSpeed )
		ObservableList<InterfaceStats> statsAL = FXCollections.observableArrayList();
		NumberFormat nfUs = NumberFormat.getInstance( Locale.US );

		// Get the time delta.  The timestamps MUST be different for utilization to be meaningful.
		if( walk1.getSysUpTime() < walk2.getSysUpTime() )
		{
			// Get the number of ticks between the two walks.  There are 100 ticks per second.
			statsAL.add( new InterfaceStats( "Time Delta", nfUs.format( ( ( double )( walk2.getSysUpTime() - walk1.getSysUpTime() ) / 100 ) ) + " seconds" ) );
		}
		else
		{
			// We should not be able to reach this point, as checking is done in start() to avoid this situation.
			errorLogger.log( Level.SEVERE, "Invalid data, SysUpTime values match, but should not!" );
			statsAL.add( new InterfaceStats( "Invalid data:", "SysUpTime values match" ) );
			return null;
		}

		// Get the ifSpeed for each WALK.  These MUST match for any comparison to be meaningful.
		if( walk1.getIfSpeed().equals( walk2.getIfSpeed() ) )
		{
			statsAL.add( new InterfaceStats( "Interface Speed", nfUs.format( walk1.getIfSpeed() ) ) );
		}
		else
		{
			errorLogger.log( Level.SEVERE, "Invalid data, interface speeds do not match!" );
			statsAL.add( new InterfaceStats( "Interface Speeds", "Do Not Match" ) );
			return statsAL;
		}

		// Get the inOctet delta.
		long inOctetDelta = walk2.getIfInOctets() - walk1.getIfInOctets();
		// If a 'counter wrap' occurred.
		if( inOctetDelta < 0 )
		{
			inOctetDelta += COUNTER32MAX;
			errorLogger.log( Level.INFO, "Inbound octet count rolled." );
		}
		statsAL.add( new InterfaceStats( "Inbound Octet Delta", nfUs.format( inOctetDelta ) ) );

		// Get the outOctet delta.
		long outOctetDelta = walk2.getIfOutOctets() - walk1.getIfOutOctets();
		// If a 'counter wrap' occurred.
		if( outOctetDelta < 0 )
		{
			outOctetDelta += COUNTER32MAX;
			errorLogger.log( Level.INFO, "Outbound octet count rolled." );
		}
		statsAL.add( new InterfaceStats( "Outbound Octet Delta", nfUs.format( outOctetDelta ) ) );
		Long totalOctetDelta = inOctetDelta + outOctetDelta;
		statsAL.add( new InterfaceStats( "Total Delta", ( nfUs.format( totalOctetDelta ) ) ) );

		// Calculate inUtilization and outUtilization.  Avoid divide-by-zero errors.
		if( ( walk2.getSysUpTime() - walk1.getSysUpTime() ) != 0 && walk1.getIfSpeed() != 0 )
		{
			// Calculate the inUtilization.
			Double inUtilization = ( double )( inOctetDelta * 8 * 100 ) / ( ( ( double )( walk2.getSysUpTime() - walk1.getSysUpTime() ) / 100 ) * walk1.getIfSpeed() );
			// Add the inbound utilization formatted to 3 decimal places.
			statsAL.add( new InterfaceStats( "Inbound Utilization", String.format( "%.3g", inUtilization ) ) );

			// Calculate the outUtilization.
			Double outUtilization = ( double )( outOctetDelta * 8 * 100 ) / ( ( ( double )( walk2.getSysUpTime() - walk1.getSysUpTime() ) / 100 ) * walk1.getIfSpeed() );
			// Add the outbound utilization formatted to 3 decimal places.
			statsAL.add( new InterfaceStats( "Outbound Utilization", String.format( "%.3g", outUtilization ) ) );

			// Calculate the totalUtilization.
			Double totalUtilization = ( ( ( totalOctetDelta ) * 8 * 100 ) / ( ( ( double )( walk2.getSysUpTime() - walk1.getSysUpTime() ) / 100 ) * walk1.getIfSpeed() ) / 2 );
			// Add the total utilization formatted to 3 decimal places.
			statsAL.add( new InterfaceStats( "Total Utilization", String.format( "%.3g", totalUtilization ) ) );
		}
		else
		{
			if( ( ( double )( walk2.getSysUpTime() - walk1.getSysUpTime() ) / 100 ) == 0 )
			{
				// This should never be reached because I check for invalid time stamps above.
				errorLogger.log( Level.SEVERE, "Invalid data, no time has passed between walks!" );
				statsAL.add( new InterfaceStats( "Unable to calculate utilization", "no time has passed between walks" ) );
			}
			if( walk1.getIfSpeed() == 0 )
			{
				// This can only be reached if the interface speed is set to zero.
				errorLogger.log( Level.WARNING, "Invalid data, interface speed is zero!" );
				statsAL.add( new InterfaceStats( "errorLogger.log( Level.SEVERE, ", "interface speed is zero" ) );
			}
		}

		// Calculate inbound discard delta.
		long inDiscardDelta = walk2.getIfInDiscards() - walk1.getIfInDiscards();
		// If a 'counter wrap' occurred.
		if( inDiscardDelta < 0 )
		{
			inDiscardDelta += COUNTER32MAX;
			errorLogger.log( Level.INFO, "Inbound discards rolled." );
		}
		statsAL.add( new InterfaceStats( "Inbound Discards", nfUs.format( inDiscardDelta ) ) );

		// Calculate outbound discard delta.
		long outDiscardDelta = walk2.getIfOutDiscards() - walk1.getIfOutDiscards();
		// If a 'counter wrap' occurred.
		if( outDiscardDelta < 0 )
		{
			outDiscardDelta += COUNTER32MAX;
			errorLogger.log( Level.INFO, "Outbound discards rolled." );
		}
		statsAL.add( new InterfaceStats( "Outbound Discards", nfUs.format( outDiscardDelta ) ) );

		// Calculate total discard delta.
		Long totalDiscardDelta = inDiscardDelta + outDiscardDelta;
		statsAL.add( new InterfaceStats( "Total Discards", nfUs.format( totalDiscardDelta ) ) );

		// Calculate inbound error delta.
		long inErrorDelta = walk2.getIfInErrors() - walk1.getIfInErrors();
		// If a 'counter wrap' occurred.
		if( inErrorDelta < 0 )
		{
			inErrorDelta += COUNTER32MAX;
			errorLogger.log( Level.INFO, "Inbound errors rolled." );
		}
		statsAL.add( new InterfaceStats( "Inbound Errors", nfUs.format( inErrorDelta ) ) );

		// Calculate outbound error delta.
		long outErrorDelta = walk2.getIfOutErrors() - walk1.getIfOutErrors();
		// If a 'counter wrap' occurred.
		if( outErrorDelta < 0 )
		{
			outErrorDelta += COUNTER32MAX;
			errorLogger.log( Level.INFO, "Outbound errors rolled." );
		}
		statsAL.add( new InterfaceStats( "Outbound Errors", nfUs.format( outErrorDelta ) ) );

		// Calculate total error delta.
		Long totalErrorDelta = inErrorDelta + outErrorDelta;
		statsAL.add( new InterfaceStats( "Total Errors", nfUs.format( totalErrorDelta ) ) );

		return statsAL;
	} // End of calculateStatistics() method.


	/**
	 * This method will find all pertinent stats for a single SNMP Interface, and return an object containing that data.<br>
	 * The returned object will also contain the System UpTime from that WALK.<br>
	 * Created by Adam Howell on 2016-05-10.
	 *
	 * @param walk    An ArrayList containing every line from a SNMP WALK file.
	 * @param ifIndex The SNMP Interface Index to build.
	 * @return A SnmpInterface class object that represents the details for the requested interface.
	 */
	@SuppressWarnings( { "squid:S106", "squid:S3776" } )
	private static SnmpInterface buildCompleteSNMPInterface( List<String> walk, Long ifIndex )
	{
		long tempSysUpTime = 0;
		String tempIfDescr = "";
		long tempIfSpeed = 0;
		long tempIfInOctets = 0;
		long tempIfInDiscards = 0;
		long tempIfInErrors = 0;
		long tempIfOutOctets = 0;
		long tempIfOutDiscards = 0;
		long tempIfOutErrors = 0;

		for( String line : walk )
		{
			if( line.startsWith( SYS_UPTIME_OID ) )
			{
				// The sysUptime value will start at offset 32 and go to the end of the line.
				tempSysUpTime = Long.parseLong( line.substring( 32 ) );
				errorLogger.log( Level.FINE, "Found a sysUpTime of {0}", tempSysUpTime );
			}
			else if( line.startsWith( IF_DESCRIPTION_OID + ifIndex ) )
			{
				// The interface description is in quotes, will start after 'STRING:', and go to the end of the line.
				tempIfDescr = line.substring( line.indexOf( " = " ) + 12, line.length() - 1 );
				System.out.println( "Found a ifDescr of " + tempIfDescr );
				errorLogger.log( Level.FINE, "Found a ifDescr of {0}", tempIfDescr );
			}
			else if( line.startsWith( IF_SPEED_OID + ifIndex ) )
			{
				// The interface speed value will start after 'GAUGE32:', and go to the end of the line.
				tempIfSpeed = Long.parseLong( line.substring( line.indexOf( " = " ) + 12 ) );
				System.out.println( "Found a ifSpeed of " + tempIfSpeed );
				errorLogger.log( Level.FINE, "Found a ifSpeed of {0}", tempIfSpeed );
			}
			else if( line.startsWith( IF_IN_OCTETS_OID + ifIndex ) )
			{
				// The interface inbound octet count value will start after 'COUNTER32:', and go to the end of the line.
				tempIfInOctets = Long.parseLong( line.substring( line.indexOf( " = " ) + 14 ) );
				errorLogger.log( Level.FINE, "Found a ifInOctets of {0}", tempIfInOctets );
			}
			else if( line.startsWith( IF_IN_DISCARDS_OID + ifIndex ) )
			{
				// The interface inbound discard count value will start after 'COUNTER32:', and go to the end of the line.
				tempIfInDiscards = Long.parseLong( line.substring( line.indexOf( " = " ) + 14 ) );
				System.out.println( "Found a ifInDiscards of " + tempIfInDiscards );
				errorLogger.log( Level.FINE, "Found a ifInDiscards of {0}", tempIfInDiscards );
			}
			else if( line.startsWith( IF_IN_ERRORS_OID + ifIndex ) )
			{
				// The interface inbound error count value will start after 'COUNTER32:', and go to the end of the line.
				tempIfInErrors = Long.parseLong( line.substring( line.indexOf( " = " ) + 14 ) );
				errorLogger.log( Level.FINE, "Found a ifInErrors of {0}", tempIfInErrors );
			}
			else if( line.startsWith( IF_OUT_OCTETS_OID + ifIndex ) )
			{
				// The interface outbound octet count value will start after 'COUNTER32:', and go to the end of the line.
				tempIfOutOctets = Long.parseLong( line.substring( line.indexOf( " = " ) + 14 ) );
				errorLogger.log( Level.FINE, "Found a ifOutOctets of {0}", tempIfOutOctets );
			}
			else if( line.startsWith( IF_OUT_DISCARDS_OID + ifIndex ) )
			{
				// The interface outbound discard count value will start after 'COUNTER32:', and go to the end of the line.
				tempIfOutDiscards = Long.parseLong( line.substring( line.indexOf( " = " ) + 14 ) );
				errorLogger.log( Level.FINE, "Found a ifOutDiscards of {0}", tempIfOutDiscards );
			}
			else if( line.startsWith( IF_OUT_ERRORS_OID + ifIndex ) )
			{
				// The interface outbound error count value will start after 'COUNTER32:', and go to the end of the line.
				tempIfOutErrors = Long.parseLong( line.substring( line.indexOf( " = " ) + 14 ) );
				errorLogger.log( Level.FINE, "Found a ifOutErrors of {0}", tempIfOutErrors );
			}
		}
		return new SnmpInterface( ifIndex, tempIfDescr, tempSysUpTime, tempIfSpeed, tempIfInOctets, tempIfInDiscards,
		                          tempIfInErrors, tempIfOutOctets, tempIfOutDiscards, tempIfOutErrors );
	} // End of buildCompleteSNMPInterface() method.


	/**
	 * This method will create a handler for the open file buttons.
	 *
	 * @param title the title to put at the top of the FileChooser dialog window.
	 * @return the file name chosen by FileChooser.
	 */
	@FXML private String openButtonHandler( String title )
	{
		Stage primaryStage = ( Stage )rootNode.getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		// Set the FileChooser to use the PWD.
		fileChooser.setInitialDirectory( new File( System.getProperty( "user.dir" ) ) );
		// Set the title for the FileChooser window.
		fileChooser.setTitle( title );
		// Set the file selection filters available to the user.
		fileChooser.getExtensionFilters()
		           .addAll( new FileChooser.ExtensionFilter( "Text Files", "*.txt" ),
		                    new FileChooser.ExtensionFilter( "All Files", "*.*" ) );
		File selectedFile = fileChooser.showOpenDialog( primaryStage );
		if( selectedFile != null )
		{
			// Send the file name to the second TextField.
			return selectedFile.getName();
		}
		else
		{
			return null;
		}
	} // End of openButtonHandler() method.


	/**
	 * This method will create a handler for the save file button.
	 *
	 * @param calculatedUtilization The object that we want to save.
	 */
	@FXML private void saveButtonHandler( ObservableList<InterfaceStats> calculatedUtilization )
	{
		Stage primaryStage = ( Stage )rootNode.getScene().getWindow();

		// Set up a FileChooser.
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory( new File( System.getProperty( "user.dir" ) ) );
		fileChooser.setTitle( "Save stats" );
		fileChooser.getExtensionFilters()
		           .addAll( new FileChooser.ExtensionFilter( "JSON Files", "*.json" ),
		                    new FileChooser.ExtensionFilter( "All Files", "*.*" ) );
		// Implement the FileChooser as a save dialog over the stage.
		File selectedFile = fileChooser.showSaveDialog( primaryStage );

		if( selectedFile == null )
		{
			errorLogger.info( "Save file dialog was cancelled." );
		}
		else
		{
			try( FileWriter file = new FileWriter( selectedFile ) )
			{
				// Try to create a file using the name selected in FileChooser.

				// Convert calculatedUtilization to JSON and write it to file.
				file.write( new Gson().toJson( calculatedUtilization ) );

				file.flush();
			}
			catch( IOException ioe )
			{
				errorLogger.log( Level.SEVERE, "Exception: Unable to save output file!" );
				errorLogger.log( Level.SEVERE, ioe.getLocalizedMessage() );
			}
		}
	} // End of saveButtonHandler() method.


	/**
	 * This method sets up the Show Interfaces button.
	 */
	@SuppressWarnings( "squid:S3776" )
	@FXML private void showInterfaceButtonHandler()
	{
		// Set the button to disabled (again), until an interface is clicked.
		saveButton.setDisable( true );

		// Read in each file and populate our ArrayLists.
		List<String> inAL1 = readFile( firstFile.getText() );
		List<String> inAL2 = readFile( secondFile.getText() );

		promptLabel.setText( "Click on a row above for interface details." );

		statisticTableView.setItems( null );

		// Check that neither readFile returned a null.
		if( inAL1 != null && inAL2 != null )
		{
			// Create an ObservableList of SnmpInterface objects from those files.
			ObservableList<SnmpInterface> observableIfContainer = findInterfaces( inAL1, inAL2 );

			// Check that findInterfaces did not return a null.
			if( observableIfContainer != null )
			{
				// Clear the file warning label.
				fileLabel.setText( "" );

				// Populate our ListView with content from the interfaces.
				interfaceTableView.setItems( observableIfContainer );
				// Add a mouse-click event for each row in the table.
				interfaceTableView.setOnMousePressed( event ->
				                                      {
					                                      if( event.isPrimaryButtonDown() )
					                                      {
						                                      // Send the first WALK and the selected IF_INDEX to buildCompleteSNMPInterface.
						                                      SnmpInterface interface1 = buildCompleteSNMPInterface( inAL1, interfaceTableView.getSelectionModel().getSelectedItem().getIfIndex() );
						                                      // Send the second WALK and the selected IF_INDEX to buildCompleteSNMPInterface.
						                                      SnmpInterface interface2 = buildCompleteSNMPInterface( inAL2, interfaceTableView.getSelectionModel().getSelectedItem().getIfIndex() );

						                                      // Populate our ListView with the return.
						                                      ObservableList<InterfaceStats> calculatedUtilization = FXCollections.observableArrayList();
						                                      if( interface1.getSysUpTime() < interface2.getSysUpTime() )
						                                      {
							                                      calculatedUtilization = calculateStatistics( interface1, interface2 );
						                                      }
						                                      else if( interface1.getSysUpTime() > interface2.getSysUpTime() )
						                                      {
							                                      calculatedUtilization = calculateStatistics( interface2, interface1 );
						                                      }
						                                      else
						                                      {
							                                      errorLogger.log( Level.SEVERE, "Invalid data, time stamps on the two WALK files are identical!" );
							                                      errorLogger.log( Level.SEVERE, "This happened in the statisticTableView event handler." );
							                                      calculatedUtilization.addAll( new InterfaceStats( "Unable to calculate utilization", "The time stamps on the two files are identical" ) );
						                                      }
						                                      // Assign each column to a class data member.
						                                      statDescrCol.setCellValueFactory( new PropertyValueFactory<>( "description" ) );
						                                      statValueCol.setCellValueFactory( new PropertyValueFactory<>( "value" ) );

						                                      // Populate the TableView with our results.
						                                      statisticTableView.setItems( calculatedUtilization );

						                                      // Enable the save button.
						                                      saveButton.setDisable( false );
						                                      final ObservableList<InterfaceStats> finalCalculatedUtilization = calculatedUtilization;
						                                      // Save the stats to a file.
						                                      saveButton.setOnAction( clickEvent -> saveButtonHandler( finalCalculatedUtilization ) );
					                                      }
				                                      } );
			}
			else
			{
				// Warn the user that the files are not usable, and clear the TableView.
				errorLogger.log( Level.SEVERE, "Invalid data, input files are not compatible with each other!" );
				fileLabel.setText( "Walk files are not compatible!" );
				interfaceTableView.setItems( null );
			}
		}
		else
		{
			String fileName;
			if( inAL1 == null )
			{
				fileName = firstFile.getText();
			}
			else
			{
				fileName = "<unknown>";
			}
			errorLogger.log( Level.SEVERE, "The first WALK file {0} could not be opened.", fileName );
			errorLogger.log( Level.SEVERE, "Invalid file, does not exist!" );

			// Create a pop-up alert to signal that a file name was invalid.
			Alert alert = new Alert( Alert.AlertType.ERROR );
			alert.setTitle( "File Error" );
			alert.setHeaderText( "Unable to open " + firstFile.getText() );
			alert.setContentText( "File does not exist." );
			alert.showAndWait();
		}
	} // End of showInterfaceButtonHandler() method.


	/**
	 * This method will display an error dialog pop-up indicating that the button is not yet ready to use.
	 */
	@FXML private void invalidButtonAlert()
	{
		errorLogger.log( Level.SEVERE, "The save button was clicked before it was ready." );
		// Create a pop-up alert to signal that this button is not available yet.
		Alert alert = new Alert( Alert.AlertType.ERROR );
		alert.setTitle( "Invalid Button" );
		alert.setHeaderText( "This button is not ready yet." );
		alert.setContentText( "Click on an interface first." );

		alert.showAndWait();
	} // End of invalidButtonAlert() method.


	/**
	 * This method is called by the FXMLLoader when initialization is complete.
	 */
	@FXML void initialize()
	{
		assert rootNode != null : "fx:id=\"rootNode\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert firstFile != null : "fx:id=\"firstFile\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert secondFile != null : "fx:id=\"secondFile\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert openWalk1Button != null : "fx:id=\"openWalk2Button\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert openWalk2Button != null : "fx:id=\"openWalk2Button\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert showInterfacesButton != null : "fx:id=\"showInterfacesButton\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert ifIndexCol != null : "fx:id=\"ifIndexCol\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert ifDescCol != null : "fx:id=\"ifDescCol\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert fileLabel != null : "fx:id=\"fileLabel\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'RootLayout.fxml'.";

		// Assign handlers for each button.
		openWalk1Button.setOnAction( e ->
		                             {
			                             String fileName = openButtonHandler( "Open first WALK file" );
			                             if( fileName != null )
			                             {
				                             firstFile.setText( fileName );
			                             }
		                             } );
		openWalk2Button.setOnAction( e ->
		                             {
			                             String fileName = openButtonHandler( "Open second WALK file" );
			                             if( fileName != null )
			                             {
				                             secondFile.setText( fileName );
			                             }
		                             } );
		showInterfacesButton.setOnAction( event -> showInterfaceButtonHandler() );
		saveButton.setOnAction( event -> invalidButtonAlert() );
		exitButton.setOnAction( event -> Platform.exit() );

		// Make the tables uneditable.
		interfaceTableView.setEditable( false );
		statisticTableView.setEditable( false );

		// Create a column for the SNMP interface indices.  The name passed to the PropertyValueFactory needs to match the getter in the SnmpInterface class.
		ifIndexCol.setCellValueFactory( new PropertyValueFactory<>( "ifIndex" ) );
		// Create a column for the SNMP interface descriptions.  The name passed to the PropertyValueFactory needs to match the getter in the SnmpInterface class.
		ifDescCol.setCellValueFactory( new PropertyValueFactory<>( "ifDescr" ) );

		// Assign the interface table to sample data.
		interfaceTableView.setItems( interfaceObservableData );
	} // End of initialize() method.
}
