package com.adamjhowell.snmpinterface;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * This program will open two SNMP walk files, locate and display each interface, and allow the user to select a displayed interface.
 * When an interface is selected, the program will calculate the utilization of that interface from the values in each walk.
 * <p>
 * The OIDs in the input files are expected to be in numerical format.
 * If named identifiers are desired, please contact me, and we can discuss formatting.
 * This means each line will begin with a dotted-decimal identifier.  Some of those identifiers are listed below.
 * The lines will not contain tabs between tokens, but spaces.  Tabs have not been tested, but may work.
 * Counters are currently limited to 32 bits in size.  64-bit counters may be implemented later.
 * <p>
 * The generic formula for utilization is: ( delta-octets * 8 * 10 ) / ( delta-seconds * ifSpeed )
 * That formula will work for inbound, outbound, or bidirectional.  You only need to select the appropriate delta-octets.
 * <p>
 * More information can be found here:
 * http://www.cisco.com/c/en/us/support/docs/ip/simple-network-management-protocol-snmp/8141-calculate-bandwidth-snmp.html
 * <p>
 * Here are the pertinent OIDs for this program (some, like errors and discards, are not implemented yet):
 * SYSUPTIMEOID = ".1.3.6.1.2.1.1.3.0";			// The OID for sysUpTime (System UpTime)
 * IFINDEXOID = ".1.3.6.1.2.1.2.2.1.1.";	    	// The OID for ifIndex (Interface Index)
 * IFDESCROID = ".1.3.6.1.2.1.2.2.1.2.";	   	// The OID for ifDescr (Interface Description)
 * IFSPEEDOID = ".1.3.6.1.2.1.2.2.1.5.";	     // The OID for ifSpeed (Interface Speed)
 * IFINOCTETSOID = ".1.3.6.1.2.1.2.2.1.10.";		// The OID for ifInOctets (Interface Inbound Octet Count)
 * IFINDISCARDSOID = ".1.3.6.1.2.1.2.2.1.13."; 	// The OID for ifInDiscards (Interface Inbound Discards)
 * IFINERRORSOID = ".1.3.6.1.2.1.2.2.1.14.";		// The OID for ifInErrors (Interface Inbound Errors)
 * IFOUTOCTETSOID = ".1.3.6.1.2.1.2.2.1.16.";  	// The OID for ifOutOctets (Interface Outbound Octet Count)
 * IFOUTDISCARDSOID = ".1.3.6.1.2.1.2.2.1.19.";	// The OID for ifOutDiscards (Interface Outbound Discards)
 * IFOUTERRORSOID = ".1.3.6.1.2.1.2.2.1.20.";     // The OID for ifOutErrors (Interface Outbound Errors)
 * COUNTER32MAX = 4294967295;					// The maximum value a Counter32 can hold.
 * COUNTER64MAX = 18446744073709551615;	     	// The maximum value a Counter64 can hold.
 * <p>
 * I will commonly use 'if' in my variable names to indicate the symbol refers to a SNMP Interface.
 */


public class Main extends Application
{
	// This table (interfaceTableView) and data (interfaceData) are my attempts to populate my own table.
	private TableView< SNMPInterface > interfaceTableView = new TableView<>();
	private final ObservableList< SNMPInterface > interfaceData =
		FXCollections.observableArrayList(
			new SNMPInterface( 99, "test data" ),
			new SNMPInterface( 97, "press" ),
			new SNMPInterface( 96, "Show Interfaces" )
		);

	// This section can be modified to suit SNMP walks that use names instead of numbers.
	private final static String SYS_UPTIME_OID = ".1.3.6.1.2.1.1.3.0";               // The OID for sysUpTime (System UpTime)
	//	private final static String IF_INDEX_OID = ".1.3.6.1.2.1.2.2.1.1.";          // The OID for ifIndex (Interface Index)
	private final static String IF_DESCRIPTION_OID = ".1.3.6.1.2.1.2.2.1.2.";          // The OID for ifDescr (Interface Description)
	private final static String IF_SPEED_OID = ".1.3.6.1.2.1.2.2.1.5.";          // The OID for ifSpeed (Interface Speed)
	private final static String IF_IN_OCTETS_OID = ".1.3.6.1.2.1.2.2.1.10.";          // The OID for ifInOctets (Interface Inbound Octet Count)
	private final static String IF_IN_DISCARDS_OID = ".1.3.6.1.2.1.2.2.1.13.";     // The OID for ifInDiscards (Interface Inbound Discards)
	private final static String IF_IN_ERRORS_OID = ".1.3.6.1.2.1.2.2.1.14.";          // The OID for ifInErrors (Interface Inbound Errors)
	private final static String IF_OUT_OCTETS_OID = ".1.3.6.1.2.1.2.2.1.16.";     // The OID for ifOutOctets (Interface Outbound Octet Count)
	private final static String IF_OUT_DISCARDS_OID = ".1.3.6.1.2.1.2.2.1.19.";     // The OID for ifOutDiscards (Interface Outbound Discards)
	private final static String IF_OUT_ERRORS_OID = ".1.3.6.1.2.1.2.2.1.20.";     // The OID for ifOutErrors (Interface Outbound Errors)
	private final static long COUNTER32MAX = 4294967295L;                         // The maximum value a Counter32 can hold.

	private final static boolean DEBUG = false;


	public static void main( String[] args )
	{
		launch( args );
	}


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
		}
		catch( IOException ioe )
		{
			ioe.getLocalizedMessage();
		}
		return null;
	}


	private static ObservableList< SNMPInterface > FindInterfaces( List< String > walk1, List< String > walk2 )
	{
		ObservableList< SNMPInterface > ifListAL = FXCollections.observableArrayList();
		List< String > ifList1 = new ArrayList<>();
		List< String > ifList2 = new ArrayList<>();

		// Add every line with an interface description OID.
		ifList1.addAll( walk1.stream().filter( line -> line.contains( IF_DESCRIPTION_OID ) ).collect( Collectors.toList() ) );
		ifList2.addAll( walk2.stream().filter( line -> line.contains( IF_DESCRIPTION_OID ) ).collect( Collectors.toList() ) );

		// If the two walks have the same interface description OIDs, we can proceed.
		if( ifList1.equals( ifList2 ) )
		{
			// Populate our map.
			ifList1.stream().filter( line -> line.startsWith( IF_DESCRIPTION_OID ) ).forEach( line -> {
				// The interface index will start at position 21 and end one position before the first equal sign.
				// There may be rare cases where an OID will contain more than one equal sign.
				int ifIndex = Integer.parseInt( line.substring( 21, line.indexOf( " = " ) ) );
				// The interface description is in quotes, will start after the equal sign, and go to the end of the line.
				String ifDescr = line.substring( line.indexOf( " = " ) + 12, line.length() - 1 );

				// Create a SNMPInterface class object from those values.
				ifListAL.add( new SNMPInterface( ifIndex, ifDescr ) );
			} );

			// Return the populated container.
			return ifListAL;
		}
		else
		{
			System.out.println( "The SNMP walks appear to be from different machines.  This will prevent any calculations." );
			return null;
		}
	}


	private static SNMPInterface BuildCompleteSNMPInterface( List< String > walk, long ifIndex )
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
				tempSysUpTime = Long.parseLong( line.substring( 32 ) );
				if( DEBUG )
				{
					System.out.println( "Found a sysUpTime of " + tempSysUpTime );
				}
			}
			else if( line.startsWith( IF_DESCRIPTION_OID + ifIndex ) )
			{
				// The interface description is in quotes, will start after the equal sign, and go to the end of the line.
				tempIfDescr = line.substring( line.indexOf( " = " ) + 12, line.length() - 1 );
				if( DEBUG )
				{
					System.out.println( "Found a ifDescr of " + tempIfDescr );
				}
			}
			else if( line.startsWith( IF_SPEED_OID + ifIndex ) )
			{
				// The interface description is in quotes, will start after the equal sign, and go to the end of the line.
				tempIfSpeed = Long.parseLong( line.substring( 34 ) );
				if( DEBUG )
				{
					System.out.println( "Found a ifSpeed of " + tempIfSpeed );
				}
			}
			else if( line.startsWith( IF_IN_OCTETS_OID + ifIndex ) )
			{
				// The interface description is in quotes, will start after the equal sign, and go to the end of the line.
				tempIfInOctets = Long.parseLong( line.substring( 37 ) );
				if( DEBUG )
				{
					System.out.println( "Found a ifInOctets of " + tempIfInOctets );
				}
			}
			else if( line.startsWith( IF_IN_DISCARDS_OID + ifIndex ) )
			{
				// The interface description is in quotes, will start after the equal sign, and go to the end of the line.
				tempIfInDiscards = Long.parseLong( line.substring( 37 ) );
				if( DEBUG )
				{
					System.out.println( "Found a ifInDiscards of " + tempIfInDiscards );
				}
			}
			else if( line.startsWith( IF_IN_ERRORS_OID + ifIndex ) )
			{
				// The interface description is in quotes, will start after the equal sign, and go to the end of the line.
				tempIfInErrors = Long.parseLong( line.substring( 37 ) );
				if( DEBUG )
				{
					System.out.println( "Found a ifInErrors of " + tempIfInErrors );
				}
			}
			else if( line.startsWith( IF_OUT_OCTETS_OID + ifIndex ) )
			{
				// The interface description is in quotes, will start after the equal sign, and go to the end of the line.
				tempIfOutOctets = Long.parseLong( line.substring( 37 ) );
				if( DEBUG )
				{
					System.out.println( "Found a ifOutOctets of " + tempIfOutOctets );
				}
			}
			else if( line.startsWith( IF_OUT_DISCARDS_OID + ifIndex ) )
			{
				// The interface description is in quotes, will start after the equal sign, and go to the end of the line.
				tempIfOutDiscards = Long.parseLong( line.substring( 37 ) );
				if( DEBUG )
				{
					System.out.println( "Found a ifOutDiscards of " + tempIfOutDiscards );
				}
			}
			else if( line.startsWith( IF_OUT_ERRORS_OID + ifIndex ) )
			{
				// The interface description is in quotes, will start after the equal sign, and go to the end of the line.
				tempIfOutErrors = Long.parseLong( line.substring( 37 ) );
				if( DEBUG )
				{
					System.out.println( "Found a ifOutErrors of " + tempIfOutErrors );
				}
			}
		}
		return new SNMPInterface( ifIndex, tempIfDescr, tempSysUpTime, tempIfSpeed, tempIfInOctets, tempIfInDiscards, tempIfInErrors, tempIfOutOctets, tempIfOutDiscards, tempIfOutErrors );
	}


	private static ObservableList< String > CalculateStatistics( SNMPInterface walk1, SNMPInterface walk2 )
	{
		// The generic formula for inUtilization is: ( delta-octets * 8 * 10 ) / ( delta-seconds * ifSpeed )
		long timeDelta;
		long ifSpeed;
		long inOctetDelta;
		long outOctetDelta;
		long inDiscardDelta;
		long outDiscardDelta;
		long inErrorDelta;
		long outErrorDelta;
		Double inUtilization;
		Double outUtilization;
		Double totalUtilization;
		ObservableList< String > CalculatedStats = FXCollections.observableArrayList();
		SNMPInterfaceDelta CalculatedStatistics = new SNMPInterfaceDelta( walk1, walk2 );

		// Get the ifSpeed.  These MUST match.
		if( walk1.getIfSpeed() == walk2.getIfSpeed() )
		{
			ifSpeed = walk1.getIfSpeed();
			CalculatedStats.add( "Interface speed: " + ifSpeed );
		}
		else
		{
			CalculatedStats.add( "ifSpeed does not match!" );
			//return CalculatedStats;
			return null;
		}

		// Get the time delta.  The timestamps MUST be different for inUtilization to be meaningful.
		if( walk1.getSysUpTime() < walk2.getSysUpTime() )
		{
			// Get the number of ticks between the two walks.  There are 100 ticks per second.
			timeDelta = ( walk2.getSysUpTime() - walk1.getSysUpTime() ) / 100;
			CalculatedStatistics.setTimeDelta( ( walk2.getSysUpTime() - walk1.getSysUpTime() ) / 100 );
			CalculatedStats.add( "Time delta: " + timeDelta + " seconds." );
		}
		else
		{
			CalculatedStats.add( "SysUpTimes match: " + walk1.getSysUpTime() + ", " + walk2.getSysUpTime() );
			//return CalculatedStats;
			return null;
		}

		// Get the inOctet delta.
		inOctetDelta = walk2.getIfInOctets() - walk1.getIfInOctets();
		// If a 'counter wrap' occurred.
		if( inOctetDelta < 0 )
		{
			inOctetDelta += COUNTER32MAX;
		}
		CalculatedStats.add( "Inbound Octet delta: " + inOctetDelta );
		CalculatedStatistics.setInOctetDelta( inOctetDelta );

		// Get the outOctet delta.
		outOctetDelta = walk2.getIfOutOctets() - walk1.getIfOutOctets();
		// If a 'counter wrap' occurred.
		if( outOctetDelta < 0 )
		{
			outOctetDelta += COUNTER32MAX;
		}
		CalculatedStats.add( "Outbound Octet delta: " + outOctetDelta );
		CalculatedStatistics.setOutOctetDelta( outOctetDelta );

		// Calculate inUtilization and outUtilization.  Avoid divide-by-zero errors.
		if( timeDelta != 0 && ifSpeed != 0 )
		{
			// Calculate the inUtilization.
			inUtilization = ( double ) ( inOctetDelta * 8 * 100 ) / ( timeDelta * ifSpeed );
			CalculatedStats.add( "Inbound Utilization: " + inUtilization.toString() );
			CalculatedStatistics.setInUtilization( inUtilization );
			outUtilization = ( double ) ( outOctetDelta * 8 * 100 ) / ( timeDelta * ifSpeed );
			CalculatedStats.add( "Outbound Utilization: " + outUtilization );
			CalculatedStatistics.setOutUtilization( outUtilization );
		}
		else
		{
			// This should never be reached because I check for invalid time stamps above.
			CalculatedStats.add( "Unable to calculate inUtilization." );
			CalculatedStats.add( "Divide by zero error." );
		}
		// Calculate total utilization.
		if( timeDelta != 0 && ifSpeed != 0 )
		{
			totalUtilization = ( double ) ( ( ( inOctetDelta + outOctetDelta ) * 8 * 100 ) / ( timeDelta * ifSpeed ) / 2 );
			CalculatedStats.add( "Total delta: " + ( inOctetDelta + outOctetDelta ) );
			CalculatedStats.add( "Total Utilization: " + totalUtilization );
			CalculatedStatistics.setTotalDelta( inOctetDelta + outOctetDelta );
			CalculatedStatistics.setTotalUtilization( totalUtilization );
		}

		// Calculate inbound discard delta.
		inDiscardDelta = walk2.getIfInDiscards() - walk1.getIfInDiscards();
		// If a 'counter wrap' occurred.
		if( inDiscardDelta < 0 )
		{
			inDiscardDelta += COUNTER32MAX;
		}
		CalculatedStats.add( "Inbound discards: " + inDiscardDelta );
		CalculatedStatistics.setInDiscardDelta( inDiscardDelta );

		// Calculate outbound discard delta.
		outDiscardDelta = walk2.getIfOutDiscards() - walk1.getIfOutDiscards();
		// If a 'counter wrap' occurred.
		if( outDiscardDelta < 0 )
		{
			outDiscardDelta += COUNTER32MAX;
		}
		CalculatedStats.add( "Outbound discards: " + outDiscardDelta );
		CalculatedStatistics.setOutDiscardDelta( outDiscardDelta );

		// Calculate inbound error delta.
		inErrorDelta = walk2.getIfInErrors() - walk1.getIfInErrors();
		// If a 'counter wrap' occurred.
		if( inErrorDelta < 0 )
		{
			inErrorDelta += COUNTER32MAX;
		}
		CalculatedStats.add( "Inbound errors: " + inErrorDelta );
		CalculatedStatistics.setInErrorDelta( inErrorDelta );

		// Calculate outbound error delta.
		outErrorDelta = walk2.getIfOutErrors() - walk1.getIfOutErrors();
		// If a 'counter wrap' occurred.
		if( outErrorDelta < 0 )
		{
			outErrorDelta += COUNTER32MAX;
		}
		CalculatedStats.add( "Outbound errors: " + outErrorDelta );
		CalculatedStatistics.setOutErrorDelta( outErrorDelta );

		//return "Link inUtilization for " + walk1.getIfDescr() + "\n" + inUtilization.toString();
		return CalculatedStats;
	}


	@Override
	public void start( Stage primaryStage ) throws Exception
	{
		// Create the stage and set the window title.
		primaryStage.setTitle( "SNMP Link Utilization" );

		// Create a GridPane that will hold all of the elements.
		GridPane rootNode = new GridPane();
		rootNode.setPadding( new Insets( 15 ) );
		rootNode.setHgap( 5 );
		rootNode.setVgap( 5 );
		rootNode.setAlignment( Pos.CENTER );

		Scene primaryScene = new Scene( rootNode, 500, 600 );

		// Create and add the label and TextField for the first file.
		rootNode.add( new Label( "First walk file:" ), 0, 0 );
		TextField firstFile = new TextField();
		firstFile.setText( "walk1.txt" );
		rootNode.add( firstFile, 1, 0 );

		// Create and add a FileChooser button for the first walk.
		Button firstWalkButton = new Button( "..." );
		rootNode.add( firstWalkButton, 3, 0 );
		firstWalkButton.setOnAction( e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory( new File( System.getProperty( "user.dir" ) ) );
			fileChooser.setTitle( "Open first walk file" );
			fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter( "Text Files", "*.txt" ), new FileChooser.ExtensionFilter( "All Files", "*.*" ) );
			File selectedFile = fileChooser.showOpenDialog( primaryStage );
			if( selectedFile != null )
			{
				firstFile.setText( selectedFile.getName() );
			}
		} );

		// Create and add the label and TextField for the second file.
		rootNode.add( new Label( "Second walk file:" ), 0, 1 );
		TextField secondFile = new TextField();
		secondFile.setText( "walk2.txt" );
		rootNode.add( secondFile, 1, 1 );

		// Create and add a FileChooser button for the second walk.
		Button secondWalkButton = new Button( "..." );
		rootNode.add( secondWalkButton, 3, 1 );
		secondWalkButton.setOnAction( e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory( new File( System.getProperty( "user.dir" ) ) );
			fileChooser.setTitle( "Open second walk file" );
			fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter( "Text Files", "*.txt" ), new FileChooser.ExtensionFilter( "All Files", "*.*" ) );
			File selectedFile = fileChooser.showOpenDialog( primaryStage );
			if( selectedFile != null )
			{
				secondFile.setText( selectedFile.getName() );
			}
		} );

		Button ShowInterfaceButton = new Button( "Show Interfaces" );
		rootNode.add( ShowInterfaceButton, 0, 2 );
		GridPane.setHalignment( ShowInterfaceButton, HPos.LEFT );

		// Add my table of SNMP Interfaces.
		interfaceTableView.setEditable( false );

		// Create a column for the SNMP interface indices.
		TableColumn< SNMPInterface, String > ifIndexCol = new TableColumn<>( "Index" );
		ifIndexCol.setCellValueFactory( new PropertyValueFactory<>( "ifIndex" ) );

		// Create a column for the SNMP interface descriptions.
		TableColumn< SNMPInterface, String > ifDescrCol = new TableColumn<>( "Description" );
		ifDescrCol.setCellValueFactory( new PropertyValueFactory<>( "ifDescr" ) );
		ifDescrCol.prefWidthProperty().bind( interfaceTableView.widthProperty().multiply( 0.7 ) );

		// These next lines should populate the table with interfaceData, and add it to the stage, even if the button is not yet pressed.
		interfaceTableView.setItems( interfaceData );
		// http://stackoverflow.com/questions/21132692/java-unchecked-unchecked-generic-array-creation-for-varargs-parameter
		interfaceTableView.getColumns().setAll( ifIndexCol, ifDescrCol );
		rootNode.add( interfaceTableView, 0, 3, 4, 1 );

		// I am intentionally not adding this to rootNode yet.
		Label label = new Label( "Press the 'Show Interfaces' button above." );
		// Populate our label to let the user know they can now get more information.
		rootNode.add( label, 0, 7, 2, 1 );

		// Create a ListView to show the stats for the selected interface.
		ListView< String > ifListView = new ListView<>();
		rootNode.add( ifListView, 0, 8, 4, 1 );

		// Create an event handler for the show interface button.
		ShowInterfaceButton.setOnAction( e -> {
			// Read in each file and populate our ArrayLists.
			List< String > inAL1 = ReadFile( firstFile.getText() );
			List< String > inAL2 = ReadFile( secondFile.getText() );

			label.setText( "Click on a row above for interface details." );

			ifListView.setItems( null );

			// Check that neither ReadFile returned a null.
			if( inAL1 != null && inAL2 != null )
			{
				// Create an ObservableList of SNMPInterface objects from those files.
				ObservableList< SNMPInterface > ifContainer = FindInterfaces( inAL1, inAL2 );

				// Check that FindInterfaces did not return a null.
				if( ifContainer != null )
				{
					// Find all SNMP interfaces in those SNMP walks.
					ObservableList< SNMPInterface > ObservableIfContainer = FindInterfaces( inAL1, inAL2 );

					// Populate our ListView with content from the interfaces.
					interfaceTableView.setItems( ObservableIfContainer );
				}
				interfaceTableView.setOnMousePressed( event -> {
					if( event.isPrimaryButtonDown() )
					{
//						System.out.println( interfaceTableView.getSelectionModel().getSelectedItem() );
//						System.out.println( BuildCompleteSNMPInterface( inAL1, interfaceTableView.getSelectionModel().getSelectedItem().getIfIndex() ) );
						SNMPInterface interface1 = BuildCompleteSNMPInterface( inAL1, interfaceTableView.getSelectionModel().getSelectedItem().getIfIndex() );
//						System.out.println( BuildCompleteSNMPInterface( inAL2, interfaceTableView.getSelectionModel().getSelectedItem().getIfIndex() ) );
						SNMPInterface interface2 = BuildCompleteSNMPInterface( inAL2, interfaceTableView.getSelectionModel().getSelectedItem().getIfIndex() );

						// Populate our ListView with the return.
						ObservableList< String > CalculatedUtilization = FXCollections.observableArrayList();
						if( interface1.getSysUpTime() < interface2.getSysUpTime() )
						{
							CalculatedUtilization = CalculateStatistics( interface1, interface2 );
							//SNMPInterfaceDelta calculatedStats = CalculateStatistics( interface1, interface2 );
						}
						else if( interface1.getSysUpTime() > interface2.getSysUpTime() )
						{
							CalculatedUtilization = CalculateStatistics( interface2, interface1 );
							//SNMPInterfaceDelta calculatedStats = CalculateStatistics( interface2, interface1 );
						}
						else
						{
							CalculatedUtilization.addAll( "Unable to calculate utilization:" );
							CalculatedUtilization.addAll( "The time stamps on the two files are identical." );
						}
						//CalculatedUtilization = calculatedStats.;
						ifListView.setItems( CalculatedUtilization );
					}
				} );
			}
			else
			{
				// Create a pop-up alert to signal that a file name was invalid.
				Alert alert = new Alert( Alert.AlertType.ERROR );
				alert.setTitle( "File Error" );
				alert.setHeaderText( "Invalid file name." );
				alert.setContentText( "File does not exist." );

				alert.showAndWait();
			}
		} );

		// Set the stage with the scene.
		primaryStage.setScene( primaryScene );

		// Show the stage.
		primaryStage.show();
	}
}
