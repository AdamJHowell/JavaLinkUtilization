package com.adamjhowell.snmpinterface;


import com.adamjhowell.snmpinterface.model.InterfaceStats;
import com.adamjhowell.snmpinterface.model.SNMPInterface;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


/**
 * Created by Adam Howell on 2016-06-08.
 * This will do all of the computational work and event handling.
 */
public class Controller
{
	// This section can be modified to suit SNMP walks that use names instead of numbers.
//	private final static String SYS_DESCR = ".1.3.6.1.2.1.1.1.0";
	private final static String SYS_UPTIME_OID = ".1.3.6.1.2.1.1.3.0";
	//	private final static String SYS_NAME = ".1.3.6.1.2.1.1.5.0";
//	private final static String IF_INDEX_OID = ".1.3.6.1.2.1.2.2.1.1.";
	private final static String IF_DESCRIPTION_OID = ".1.3.6.1.2.1.2.2.1.2.";
	private final static String IF_SPEED_OID = ".1.3.6.1.2.1.2.2.1.5.";
	private final static String IF_IN_OCTETS_OID = ".1.3.6.1.2.1.2.2.1.10.";
	private final static String IF_IN_DISCARDS_OID = ".1.3.6.1.2.1.2.2.1.13.";
	private final static String IF_IN_ERRORS_OID = ".1.3.6.1.2.1.2.2.1.14.";
	private final static String IF_OUT_OCTETS_OID = ".1.3.6.1.2.1.2.2.1.16.";
	private final static String IF_OUT_DISCARDS_OID = ".1.3.6.1.2.1.2.2.1.19.";
	private final static String IF_OUT_ERRORS_OID = ".1.3.6.1.2.1.2.2.1.20.";
	private final static long COUNTER32MAX = 4294967295L;
	private final static boolean DEBUG = false;

	// Logging
	private static final Logger errorLogger = LoggerFactory.getLogger( Main.class );
	private static List< String > WALK;
	private static Long IF_INDEX;
	// Data for the table.
	private final ObservableList< SNMPInterface > interfaceData = FXCollections.observableArrayList(
		new SNMPInterface( 42L, "Test data." ),
		new SNMPInterface( 42L, "Press the..." ),
		new SNMPInterface( 42L, "'Show Interfaces' button" ) );
	@FXML private GridPane rootNode;
	@FXML private TextField firstFile;
	@FXML private TextField secondFile;
	@FXML private Button openWalk1Button;
	@FXML private Button openWalk2Button;
	@FXML private Button showInterfacesButton;
	@FXML private TableView< SNMPInterface > interfaceTableView;
	@FXML private TableColumn< SNMPInterface, String > ifIndexCol;
	@FXML private TableColumn< SNMPInterface, String > ifDescCol;
	@FXML private TableView< InterfaceStats > statisticTableView;
	@FXML private TableColumn< InterfaceStats, String > statDescrCol;
	@FXML private TableColumn< InterfaceStats, String > statValueCol;
	@FXML private Label fileLabel;
	@FXML private Button saveButton;
	@FXML private Label promptLabel;
	@FXML private Button exitButton;


	/**
	 * ReadFile
	 * Created by Adam Howell on 2016-05-04.
	 * This method will take a String representing a file name, and attempt to open it.
	 * If the file can be opened, every line within that file will be read into an ArrayList.
	 * That ArrayList of lines will be returned to the calling method.
	 * If the file cannot be opened, this method will return 'null'.
	 *
	 * @param inFileName a String representing a file to open.
	 * @return an ArrayList containing every line from the opened file.
	 */
	private static List< String > ReadFile( String inFileName )
	{
		String line;
		List< String > inAL = new ArrayList<>();
		try
		{
			// Create a file handle with the provided filename.
			File inFileHandle = new File( inFileName );
			// Check that the file opened.
			if( inFileHandle.exists() )
			{
				// Create a BufferedReader from that file handle.
				BufferedReader inBR = new BufferedReader( new FileReader( inFileHandle ) );
				// Read in each line from the file.
				while( ( line = inBR.readLine() ) != null )
				{
					// Populate the ArrayList with each line we read in.
					inAL.add( line );
				}
				return inAL;
			}
			else
			{
				errorLogger.error( "File error: input file " + inFileName + " does not exist!" );
			}
		}
		catch( IOException ioe )
		{
			errorLogger.error( "Exception: IOError trying to read the input file: " + inFileName );
			ioe.getLocalizedMessage();
		}
		return null;
	} // End of ReadFile() method.


	/**
	 * FindInterfaces
	 * Created by Adam Howell on 2016-05-05.
	 * This will create two ArrayLists and add every line that begins with a specific OID.
	 * If those ArrayLists are identical, then it will find the IF_INDEX and ifDescr for each interface,
	 * create a SNMPInterface class object from them, and return an ArrayList of those objects.
	 *
	 * @param walk1 the first WALK to search through.
	 * @param walk2 the second WALK to search through.
	 * @return an ObservableList of discovered interfaces.
	 */
	private static ObservableList< SNMPInterface > FindInterfaces( List< String > walk1, List< String > walk2 )
	{
		ObservableList< SNMPInterface > ifListAL = FXCollections.observableArrayList();
		List< String > ifList1 = new ArrayList<>();
		List< String > ifList2 = new ArrayList<>();

		// Add every line with an interface description OID.
		ifList1.addAll(
			walk1.stream().filter( line -> line.contains( IF_DESCRIPTION_OID ) ).collect( Collectors.toList() ) );
		ifList2.addAll(
			walk2.stream().filter( line -> line.contains( IF_DESCRIPTION_OID ) ).collect( Collectors.toList() ) );

		// If the two walks have the same interface description OIDs, we can proceed.
		if( ifList1.equals( ifList2 ) )
		{
			// Populate our map.
			ifList1.stream().filter( line -> line.startsWith( IF_DESCRIPTION_OID ) ).forEach( line -> {
				// Catch a NumberFormatException from parseLong(), if one occurs.
				try
				{
					// The interface index will start at position 21 and end one position before the first equal sign.
					// There may be rare cases where an OID will contain more than one equal sign.
					Long ifIndex = Long.parseLong( line.substring( 21, line.indexOf( " = " ) ) );
					// The interface description is in quotes, will start after the equal sign, and go to the end of the line.
					String ifDescr = line.substring( line.indexOf( " = " ) + 12, line.length() - 1 );

					// Create a SNMPInterface class object from those values.
					ifListAL.add( new SNMPInterface( ifIndex, ifDescr ) );
				}
				catch( NumberFormatException nfe )
				{
					errorLogger.error( "Exception: NumberFormatException trying parseLong()!" );
					nfe.getMessage();
					nfe.printStackTrace();
				}
			} );

			// Return the populated container.
			return ifListAL;
		}
		else
		{
			errorLogger.error(
				"The SNMP walks appear to be from different machines.  This will prevent any calculations." );
			return null;
		}
	} // End of FindInterfaces() method.


	/**
	 * CalculateStatistics
	 * Created by Adam Howell on 2016-05-10.
	 * This will analyze two data containers and produce human-readable output related to
	 * the differences between those containers.
	 *
	 * @param walk1 the output from BuildCompleteSNMPInterface for the first WALK.
	 * @param walk2 the output from BuildCompleteSNMPInterface for the second WALK.
	 * @return an ObservableList containing all of the statistics for interface.
	 */
	private static ObservableList< InterfaceStats > CalculateStatistics( SNMPInterface walk1, SNMPInterface walk2 )
	{
		// The generic formula for inUtilization is: ( delta-octets * 8 * 10 ) / ( delta-seconds * ifSpeed )
		Long inOctetDelta;
		Long outOctetDelta;
		Long totalOctetDelta;
		Long inDiscardDelta;
		Long outDiscardDelta;
		Long totalDiscardDelta;
		Long inErrorDelta;
		Long outErrorDelta;
		Long totalErrorDelta;
		Double inUtilization;
		Double outUtilization;
		Double totalUtilization;
		ObservableList< InterfaceStats > statsAL = FXCollections.observableArrayList();
		NumberFormat nf_us = NumberFormat.getInstance( Locale.US );

		// Get the time delta.  The timestamps MUST be different for utilization to be meaningful.
		if( walk1.getSysUpTime() < walk2.getSysUpTime() )
		{
			// Get the number of ticks between the two walks.  There are 100 ticks per second.
			statsAL.add( new InterfaceStats( "Time Delta",
				nf_us.format( ( ( double ) ( walk2.getSysUpTime() - walk1.getSysUpTime() ) / 100 ) ) +
					" seconds" ) );
		}
		else
		{
			// We should not be able to reach this point, as checking is done in start() to avoid this situation.
			errorLogger.error( "Invalid data, SysUpTime values match, but should not!" );
			statsAL.add( new InterfaceStats( "Invalid data:", "SysUpTime values match" ) );
			//return CalculatedStats;
			return null;
		}

		// Get the ifSpeed for each WALK.  These MUST match.
		if( walk1.getIfSpeed().equals( walk2.getIfSpeed() ) )
		{
			//statsAL.add( new InterfaceStats( "Interface Speed", walk1.getIfSpeed().toString() ) );
			statsAL.add( new InterfaceStats( "Interface Speed", nf_us.format( walk1.getIfSpeed() ) ) );
		}
		else
		{
			errorLogger.error( "Invalid data, interface speeds do not match!" );
			statsAL.add( new InterfaceStats( "Interface Speeds", "Do Not Match" ) );
			return statsAL;
		}

		// Get the inOctet delta.
		inOctetDelta = walk2.getIfInOctets() - walk1.getIfInOctets();
		// If a 'counter wrap' occurred.
		if( inOctetDelta < 0 )
		{
			inOctetDelta += COUNTER32MAX;
		}
		statsAL.add( new InterfaceStats( "Inbound Octet Delta", nf_us.format( inOctetDelta ) ) );

		// Get the outOctet delta.
		outOctetDelta = walk2.getIfOutOctets() - walk1.getIfOutOctets();
		// If a 'counter wrap' occurred.
		if( outOctetDelta < 0 )
		{
			outOctetDelta += COUNTER32MAX;
		}
		statsAL.add( new InterfaceStats( "Outbound Octet Delta", nf_us.format( outOctetDelta ) ) );
		totalOctetDelta = inOctetDelta + outOctetDelta;
		statsAL.add( new InterfaceStats( "Total Delta", ( nf_us.format( totalOctetDelta ) ) ) );

		// Calculate inUtilization and outUtilization.  Avoid divide-by-zero errors.
		if( ( walk2.getSysUpTime() - walk1.getSysUpTime() ) != 0 && walk1.getIfSpeed() != 0 )
		{
			// Calculate the inUtilization.
			inUtilization =
				( double ) ( inOctetDelta * 8 * 100 ) /
					( ( ( double ) ( walk2.getSysUpTime() - walk1.getSysUpTime() ) / 100 ) * walk1.getIfSpeed() );
			// Format the double to 3 decimal places.
			Double inTruncatedDouble =
				new BigDecimal( inUtilization ).setScale( 3, BigDecimal.ROUND_HALF_UP ).doubleValue();
			statsAL.add( new InterfaceStats( "Inbound Utilization", nf_us.format( inTruncatedDouble ) ) );

			// Calculate the outUtilization.
			outUtilization =
				( double ) ( outOctetDelta * 8 * 100 ) /
					( ( ( double ) ( walk2.getSysUpTime() - walk1.getSysUpTime() ) / 100 ) * walk1.getIfSpeed() );
			// Format the double to 3 decimal places.
			Double outTruncatedDouble =
				new BigDecimal( outUtilization ).setScale( 3, BigDecimal.ROUND_HALF_UP ).doubleValue();
			statsAL.add( new InterfaceStats( "Outbound Utilization", nf_us.format( outTruncatedDouble ) ) );

			// Calculate the totalUtilization.
			totalUtilization = ( ( ( totalOctetDelta ) * 8 * 100 ) /
				( ( ( double ) ( walk2.getSysUpTime() - walk1.getSysUpTime() ) / 100 ) * walk1.getIfSpeed() ) /
				2 );
			// Format the double to 3 decimal places.
			Double totalTruncatedDouble =
				new BigDecimal( totalUtilization ).setScale( 3, BigDecimal.ROUND_HALF_UP ).doubleValue();
			statsAL.add( new InterfaceStats( "Total Utilization", nf_us.format( totalTruncatedDouble ) ) );
		}
		else
		{
			if( ( ( double ) ( walk2.getSysUpTime() - walk1.getSysUpTime() ) / 100 ) == 0 )
			{
				// This should never be reached because I check for invalid time stamps above.
				errorLogger.error( "Invalid data, no time has passed between walks!" );
				statsAL.add(
					new InterfaceStats( "Unable to calculate utilization", "no time has passed between walks" ) );
			}
			if( walk1.getIfSpeed() == 0 )
			{
				// This can only be reached if the interface speed is set to zero.
				errorLogger.warn( "Invalid data, interface speed is zero!" );
				statsAL.add( new InterfaceStats( "Unable to calculate utilization", "interface speed is zero" ) );
			}
		}

		// Calculate inbound discard delta.
		inDiscardDelta = walk2.getIfInDiscards() - walk1.getIfInDiscards();
		// If a 'counter wrap' occurred.
		if( inDiscardDelta < 0 )
		{
			inDiscardDelta += COUNTER32MAX;
		}
		statsAL.add( new InterfaceStats( "Inbound Discards", nf_us.format( inDiscardDelta ) ) );

		// Calculate outbound discard delta.
		outDiscardDelta = walk2.getIfOutDiscards() - walk1.getIfOutDiscards();
		// If a 'counter wrap' occurred.
		if( outDiscardDelta < 0 )
		{
			outDiscardDelta += COUNTER32MAX;
		}
		statsAL.add( new InterfaceStats( "Outbound Discards", nf_us.format( outDiscardDelta ) ) );

		// Calculate total discard delta.
		totalDiscardDelta = inDiscardDelta + outDiscardDelta;
		statsAL.add( new InterfaceStats( "Total Discards", nf_us.format( totalDiscardDelta ) ) );

		// Calculate inbound error delta.
		inErrorDelta = walk2.getIfInErrors() - walk1.getIfInErrors();
		// If a 'counter wrap' occurred.
		if( inErrorDelta < 0 )
		{
			inErrorDelta += COUNTER32MAX;
		}
		statsAL.add( new InterfaceStats( "Inbound Errors", nf_us.format( inErrorDelta ) ) );

		// Calculate outbound error delta.
		outErrorDelta = walk2.getIfOutErrors() - walk1.getIfOutErrors();
		// If a 'counter wrap' occurred.
		if( outErrorDelta < 0 )
		{
			outErrorDelta += COUNTER32MAX;
		}
		statsAL.add( new InterfaceStats( "Outbound Errors", nf_us.format( outErrorDelta ) ) );

		// Calculate total error delta.
		totalErrorDelta = inErrorDelta + outErrorDelta;
		statsAL.add( new InterfaceStats( "Total Errors", nf_us.format( totalErrorDelta ) ) );

		//return "Link inUtilization for " + walk1.getIfDescr() + "\n" + inUtilization.toString();
		return statsAL;
	} // End of CalculateStatistics() method.


	/**
	 * BuildCompleteSNMPInterface
	 * Created by Adam Howell on 2016-05-10.
	 * This method will find all pertinent stats for a single SNMP Interface, and return an object containing that data.
	 * The returned object will also contain the System UpTime from that WALK.
	 *
	 * @param walk    an ArrayList containing every line from a SNMP WALK file.
	 * @param ifIndex the SNMP Interface Index to build.
	 * @return a SNMPInterface class object that represents the details for the requested interface.
	 */
	private static SNMPInterface BuildCompleteSNMPInterface( List< String > walk, Long ifIndex )
	{
		WALK = walk;
		IF_INDEX = ifIndex;
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
				if( DEBUG )
				{
					System.out.println( "Found a sysUpTime of " + tempSysUpTime );
				}
			}
			else if( line.startsWith( IF_DESCRIPTION_OID + ifIndex ) )
			{
				// The interface description is in quotes, will start after 'STRING:', and go to the end of the line.
				tempIfDescr = line.substring( line.indexOf( " = " ) + 12, line.length() - 1 );
				if( DEBUG )
				{
					System.out.println( "Found a ifDescr of " + tempIfDescr );
				}
			}
			else if( line.startsWith( IF_SPEED_OID + ifIndex ) )
			{
				// The interface speed value will start after 'GAUGE32:', and go to the end of the line.
				tempIfSpeed = Long.parseLong( line.substring( line.indexOf( " = " ) + 12 ) );
				if( DEBUG )
				{
					System.out.println( "Found a ifSpeed of " + tempIfSpeed );
				}
			}
			else if( line.startsWith( IF_IN_OCTETS_OID + ifIndex ) )
			{
				// The interface inbound octet count value will start after 'COUNTER32:', and go to the end of the line.
				tempIfInOctets = Long.parseLong( line.substring( line.indexOf( " = " ) + 14 ) );
				if( DEBUG )
				{
					System.out.println( "Found a ifInOctets of " + tempIfInOctets );
				}
			}
			else if( line.startsWith( IF_IN_DISCARDS_OID + ifIndex ) )
			{
				// The interface inbound discard count value will start after 'COUNTER32:', and go to the end of the line.
				tempIfInDiscards = Long.parseLong( line.substring( line.indexOf( " = " ) + 14 ) );
				if( DEBUG )
				{
					System.out.println( "Found a ifInDiscards of " + tempIfInDiscards );
				}
			}
			else if( line.startsWith( IF_IN_ERRORS_OID + ifIndex ) )
			{
				// The interface inbound error count value will start after 'COUNTER32:', and go to the end of the line.
				tempIfInErrors = Long.parseLong( line.substring( line.indexOf( " = " ) + 14 ) );
				if( DEBUG )
				{
					System.out.println( "Found a ifInErrors of " + tempIfInErrors );
				}
			}
			else if( line.startsWith( IF_OUT_OCTETS_OID + ifIndex ) )
			{
				// The interface outbound octet count value will start after 'COUNTER32:', and go to the end of the line.
				tempIfOutOctets = Long.parseLong( line.substring( line.indexOf( " = " ) + 14 ) );
				if( DEBUG )
				{
					System.out.println( "Found a ifOutOctets of " + tempIfOutOctets );
				}
			}
			else if( line.startsWith( IF_OUT_DISCARDS_OID + ifIndex ) )
			{
				// The interface outbound discard count value will start after 'COUNTER32:', and go to the end of the line.
				tempIfOutDiscards = Long.parseLong( line.substring( line.indexOf( " = " ) + 14 ) );
				if( DEBUG )
				{
					System.out.println( "Found a ifOutDiscards of " + tempIfOutDiscards );
				}
			}
			else if( line.startsWith( IF_OUT_ERRORS_OID + ifIndex ) )
			{
				// The interface outbound error count value will start after 'COUNTER32:', and go to the end of the line.
				tempIfOutErrors = Long.parseLong( line.substring( line.indexOf( " = " ) + 14 ) );
				if( DEBUG )
				{
					System.out.println( "Found a ifOutErrors of " + tempIfOutErrors );
				}
			}
		}
		return new SNMPInterface( ifIndex, tempIfDescr, tempSysUpTime, tempIfSpeed, tempIfInOctets, tempIfInDiscards,
			tempIfInErrors, tempIfOutOctets, tempIfOutDiscards, tempIfOutErrors );
	} // End of BuildCompleteSNMPInterface() method.


	@FXML private void exitButtonHandler()
	{
		Platform.exit();
	} // End of handleExitButtonAction() method.


	@FXML private void openFirstWalk()
	{
		// Create a handler for the button that launches FileChooser.
		openWalk1Button.setOnAction( e -> {
			String fileName = OpenButtonHandler( "Open first WALK file" );
			if( fileName != null )
			{
				firstFile.setText( fileName );
			}
		} );
	}


	@FXML private void openSecondWalk()
	{
		// Create a handler for the button that launches FileChooser.
		openWalk2Button.setOnAction( e -> {
			String fileName = OpenButtonHandler( "Open second WALK file" );
			if( fileName != null )
			{
				secondFile.setText( fileName );
			}
		} );
	}


	/**
	 * OpenButtonHandler
	 * This method will create a handler for the open file buttons.
	 *
	 * @param title the title to put at the top of the FileChooser dialog window.
	 * @return the file name chosen by FileChooser.
	 */
	@FXML private String OpenButtonHandler( String title )
	{
		Stage primaryStage = ( Stage ) rootNode.getScene().getWindow();
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
	} // End of OpenButtonHandler() method.


	/**
	 * SaveButtonHandler
	 * This method will create a handler for the save file button.
	 *
	 * @param CalculatedUtilization the object that we want to save.
	 */
	@FXML private void SaveButtonHandler( ObservableList< InterfaceStats > CalculatedUtilization )
	{
		Stage primaryStage = ( Stage ) rootNode.getScene().getWindow();

		// Set up a FileChooser.
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory( new File( System.getProperty( "user.dir" ) ) );
		fileChooser.setTitle( "Save stats" );
		fileChooser.getExtensionFilters()
			.addAll( new FileChooser.ExtensionFilter( "JSON Files", "*.json" ),
				new FileChooser.ExtensionFilter( "All Files", "*.*" ) );
		// Implement the FileChooser as a save dialog over the stage.
		File selectedFile = fileChooser.showSaveDialog( primaryStage );

		if( selectedFile != null )
		{
			try
			{
				// Try to create a file using the name selected in FileChooser.
				FileWriter file = new FileWriter( selectedFile );

				// Convert CalculatedUtilization to JSON and write it to file.
				file.write( new Gson().toJson( CalculatedUtilization ) );

				file.flush();
				file.close();
			}
			catch( IOException ioe )
			{
				errorLogger.error( "Exception: Unable to save output file!" );
				ioe.printStackTrace();
			}
		}
		else
		{
			errorLogger.info( "Save file dialog was cancelled." );
		}
	} // End of SaveButtonHandler() method.


	@FXML private void ShowInterfaceButtonHandler()
	{
		// Set the button to disabled (again), until an interface is clicked.
		saveButton.setDisable( true );

		// Read in each file and populate our ArrayLists.
		List< String > inAL1 = ReadFile( firstFile.getText() );
		List< String > inAL2 = ReadFile( secondFile.getText() );

		promptLabel.setText( "Click on a row above for interface details." );

		statisticTableView.setItems( null );

		// Check that neither ReadFile returned a null.
		if( inAL1 != null && inAL2 != null )
		{
			// Create an ObservableList of SNMPInterface objects from those files.
			ObservableList< SNMPInterface > ObservableIfContainer = FindInterfaces( inAL1, inAL2 );

			// Check that FindInterfaces did not return a null.
			if( ObservableIfContainer != null )
			{
				// Clear the file warning label.
				fileLabel.setText( "" );

				// Populate our ListView with content from the interfaces.
				interfaceTableView.setItems( ObservableIfContainer );
				// Add a mouse-click event for each row in the table.
				interfaceTableView.setOnMousePressed( event -> {
					if( event.isPrimaryButtonDown() )
					{
						// Send the first WALK and the selected IF_INDEX to BuildCompleteSNMPInterface.
						SNMPInterface
							interface1 =
							BuildCompleteSNMPInterface( inAL1,
								interfaceTableView.getSelectionModel().getSelectedItem().getIfIndex() );
						// Send the second WALK and the selected IF_INDEX to BuildCompleteSNMPInterface.
						SNMPInterface
							interface2 =
							BuildCompleteSNMPInterface( inAL2,
								interfaceTableView.getSelectionModel().getSelectedItem().getIfIndex() );

						// Populate our ListView with the return.
						ObservableList< InterfaceStats >
							CalculatedUtilization =
							FXCollections.observableArrayList();
						if( interface1.getSysUpTime() < interface2.getSysUpTime() )
						{
							CalculatedUtilization = CalculateStatistics( interface1, interface2 );
						}
						else if( interface1.getSysUpTime() > interface2.getSysUpTime() )
						{
							CalculatedUtilization = CalculateStatistics( interface2, interface1 );
						}
						else
						{
							errorLogger.error(
								"Invalid data, time stamps on the two WALK files are identical!" );
							errorLogger.error( "This happened in the statisticTableView event handler." );
							CalculatedUtilization.addAll( new InterfaceStats( "Unable to calculate utilization",
								"The time stamps on the two files are identical" ) );
						}
						// Assign each column to a class data member.
						statDescrCol.setCellValueFactory( new PropertyValueFactory<>( "description" ) );
						statValueCol.setCellValueFactory( new PropertyValueFactory<>( "value" ) );

						// Populate the TableView with our results.
						statisticTableView.setItems( CalculatedUtilization );

						// Enable the save button.
						saveButton.setDisable( false );
						final ObservableList< InterfaceStats > finalCalculatedUtilization = CalculatedUtilization;
						// Save the stats to a file.
						saveButton.setOnAction( clickEvent -> SaveButtonHandler( finalCalculatedUtilization ) );
					}
				} );
			}
			else
			{
				// Warn the user that the files are not usable, and clear the TableView.
				errorLogger.error( "Invalid data, input files are not compatible with each other!" );
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
//			else if( inAL2 == null )
//			{
//				fileName = secondFile.getText();
//			}
			else
			{
				fileName = "<unknown>";
			}
			errorLogger.error( "The first WALK file " + fileName + " could not be opened." );

			errorLogger.error( "Invalid data, selected file does not exist!" );
			// Create a pop-up alert to signal that a file name was invalid.
			Alert alert = new Alert( Alert.AlertType.ERROR );
			alert.setTitle( "File Error" );
			alert.setHeaderText( "Unable to open " + firstFile.getText() );
			alert.setContentText( "File does not exist." );

			alert.showAndWait();
		}
	} // End of ShowInterfaceButtonHandler() method.


	/**
	 * InvalidButtonAlert
	 * This method will display an error dialog pop-up indicating that the button is not yet ready to use.
	 */
	@FXML private void InvalidButtonAlert()
	{
		errorLogger.error( "The save button was clicked before it was ready." );
		// Create a pop-up alert to signal that this button is not available yet.
		Alert alert = new Alert( Alert.AlertType.ERROR );
		alert.setTitle( "Invalid Button" );
		alert.setHeaderText( "This button is not ready yet." );
		alert.setContentText( "Click on an interface first." );

		alert.showAndWait();
	} // End of InvalidButtonAlert() method.


	// This method is called by the FXMLLoader when initialization is complete
	@FXML void initialize()
	{
		assert rootNode != null : "fx:id=\"rootNode\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert firstFile != null : "fx:id=\"firstFile\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert secondFile != null : "fx:id=\"secondFile\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert openWalk1Button != null :
			"fx:id=\"openWalk2Button\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert openWalk2Button != null :
			"fx:id=\"openWalk2Button\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert showInterfacesButton != null :
			"fx:id=\"showInterfacesButton\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert ifIndexCol != null : "fx:id=\"ifIndexCol\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert ifDescCol != null : "fx:id=\"ifDescCol\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert fileLabel != null : "fx:id=\"fileLabel\" was not injected: check your FXML file 'RootLayout.fxml'.";
		assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'RootLayout.fxml'.";

		// initialize your logic here: all @FXML variables will have been injected
		openWalk1Button.setOnAction( event -> openFirstWalk() );
		openWalk2Button.setOnAction( event -> openSecondWalk() );
		showInterfacesButton.setOnAction( event -> ShowInterfaceButtonHandler() );
		saveButton.setOnAction( event -> InvalidButtonAlert() );
		exitButton.setOnAction( event -> exitButtonHandler() );

		// Make the tables unmodifiable.
		interfaceTableView.setEditable( false );
		statisticTableView.setEditable( false );

		// Create a column for the SNMP interface indices.
		ifIndexCol.setCellValueFactory( new PropertyValueFactory<>( "IF_INDEX" ) );
		// Create a column for the SNMP interface descriptions.
		ifDescCol.setCellValueFactory( new PropertyValueFactory<>( "ifDescr" ) );

		interfaceTableView.setItems( interfaceData );
	} // End of initialize() method.
}
