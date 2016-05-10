package com.adamjhowell.snmpinterface;


import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
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
			new SNMPInterface( "99", "test" ),
			new SNMPInterface( "98", "data" ),
			new SNMPInterface( "97", "press" ),
			new SNMPInterface( "96", "button" )
		);

	private final static boolean DEBUG = true;


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
		String IfDescriptionOID = ".1.3.6.1.2.1.2.2.1.2.";
//		ObservableMap< Integer, String > ifListMap = new HashMap<>();
		//List< SNMPInterface2 > ifListAL = new ArrayList<>();
		ObservableList< SNMPInterface > ifListAL = FXCollections.observableArrayList();
		List< String > ifList1 = new ArrayList<>();
		List< String > ifList2 = new ArrayList<>();

		// Add every line with an interface description OID.
		ifList1.addAll( walk1.stream().filter( line -> line.contains( IfDescriptionOID ) ).collect( Collectors.toList() ) );
		ifList2.addAll( walk2.stream().filter( line -> line.contains( IfDescriptionOID ) ).collect( Collectors.toList() ) );

		// If the two walks have the same interface description OIDs, we can proceed.
		if( ifList1.equals( ifList2 ) )
		{
			// Populate our map.
			ifList1.stream().filter( line -> line.startsWith( IfDescriptionOID ) ).forEach( line -> {
				// The interface index will start at position 21 and end one position before the first equal sign.
				// There may be rare cases where an OID will contain more than one equal sign.
				String ifIndex = line.substring( 21, line.indexOf( " = " ) );
				// The interface description will start after the equal sign, and go to the end of the line.
				String ifDescr = line.substring( line.indexOf( " = " ) + 11 );

				// Create a SNMPInterface2 class object from those values.
				ifListAL.add( new SNMPInterface( ifIndex, ifDescr ) );
//				ifListMap.put( ifIndex, ifDescr );
			} );

			// Return the populated container.
//			return ifListMap;
			return ifListAL;
		}
		else
		{
			System.out.println( "The SNMP walks appear to be from different machines.  This will prevent any calculations." );
			return null;
		}
	}


	@Override
	public void start( Stage primaryStage ) throws Exception
	{
		primaryStage.setTitle( "SNMP Link Utilization" );

		GridPane rootNode = new GridPane();
		rootNode.setPadding( new Insets( 15 ) );
		rootNode.setHgap( 5 );
		rootNode.setVgap( 5 );
		rootNode.setAlignment( Pos.CENTER );

		Scene primaryScene = new Scene( rootNode, 500, 600 );

		rootNode.add( new Label( "First walk file:" ), 0, 0 );
		TextField firstValue = new TextField();
		firstValue.setText( "walk1.txt" );
		rootNode.add( firstValue, 1, 0 );

		rootNode.add( new Label( "Second walk file:" ), 0, 1 );
		TextField secondValue = new TextField();
		secondValue.setText( "walk2.txt" );
		rootNode.add( secondValue, 1, 1 );

		Button ShowInterfaceButton = new Button( "Show Interfaces" );
		rootNode.add( ShowInterfaceButton, 0, 2 );
		GridPane.setHalignment( ShowInterfaceButton, HPos.LEFT );

//		ListView< String > ifListView = new ListView<>();
//		rootNode.add( ifListView, 0, 3 );

		// Add my table of SNMP Interfaces.
		interfaceTableView.setEditable( true );

		// Create a column for the SNMP interface indices.
		TableColumn< SNMPInterface, String > ifIndexCol = new TableColumn<>( "Index" );
		ifIndexCol.setCellValueFactory( new PropertyValueFactory<>( "ifIndex" ) );

		// Create a column for the SNMP interface descriptions.
		TableColumn< SNMPInterface, String > ifDescrCol = new TableColumn<>( "Description" );
		ifDescrCol.setCellValueFactory( new PropertyValueFactory<>( "ifDescr" ) );
		ifDescrCol.prefWidthProperty().bind( interfaceTableView.widthProperty().multiply( 0.7 ) );

		// These next lines should populate the table with interfaceData, and add it to the stage, even if the button is not yet pressed.
		interfaceTableView.setItems( interfaceData );
		interfaceTableView.getColumns().setAll( ifIndexCol, ifDescrCol );
		rootNode.add( interfaceTableView, 0, 7, 2, 1 );

		if( DEBUG )
		{
			// Test code to see what is in 'interfaceData'.
			for( SNMPInterface test : interfaceData )
			{
				System.out.println( "Static data: " + test.toString() );
			}
		}

		// Create an event handler for the show interface button.
		ShowInterfaceButton.setOnAction( e -> {
			// Read in each file and populate our ArrayLists.
			List< String > inAL1 = ReadFile( firstValue.getText() );
			List< String > inAL2 = ReadFile( secondValue.getText() );

			// Check that neither ReadFile returned a null.
			if( inAL1 != null && inAL2 != null )
			{
				// Create an ObservableList of SNMPInterface objects from those files.
				ObservableList< SNMPInterface > ifContainer = FindInterfaces( inAL1, inAL2 );

				// Check that FindInterfaces did not return a null.
				if( ifContainer != null )
				{
					if( DEBUG )
					{
						// Test code to see what is in 'ifContainer'.
						for( SNMPInterface test : ifContainer )
						{
							System.out.println( "File data: " + test.toString() );
						}
					}

					// Find all SNMP interfaces in those SNMP walks.
					ObservableList< SNMPInterface > ObservableIfContainer = FindInterfaces( inAL1, inAL2 );

					// Populate our ListView with content from the interfaces.
					interfaceTableView.setItems( ObservableIfContainer );
				}
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


	public static class SNMPInterface
	{
		private final SimpleStringProperty ifIndex;
		private final SimpleStringProperty ifDescr;


		SNMPInterface( String ifIndex, String ifDescr )
		{
			this.ifIndex = new SimpleStringProperty( ifIndex );
			this.ifDescr = new SimpleStringProperty( ifDescr );
		}


		public String getIfIndex()
		{
			return ifIndex.get();
		}


		public void setIfIndex( String Index )
		{
			ifIndex.set( Index );
		}


		public String getIfDescr()
		{
			return ifDescr.get();
		}


		public void setIfDescr( String Descr )
		{
			ifDescr.set( Descr );
		}


		@Override
		public String toString()
		{
			return "SNMPInterface: ifIndex = " + ifIndex + " ifDescr = " + ifDescr;
		}
	}
}
