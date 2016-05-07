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
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
	private final ObservableList< Person > data = FXCollections.observableArrayList( new Person( "Jacob", "Smith", "jacob.smith@example.com" ), new Person( "Isabella", "Johnson", "isabella.johnson@example.com" ), new Person( "Ethan", "Williams", "ethan.williams@example.com" ), new Person( "Emma", "Jones", "emma.jones@example.com" ), new Person( "Michael", "Brown", "michael.brown@example.com" ) );
	private TableView< Person > table = new TableView<>();
/*
	private final ObservableList< SNMPInterface > data2 =
		FXCollections.observableArrayList(
			new SNMPInterface( "1", "lo" ),
			new SNMPInterface( "2", "eth1" ),
			new SNMPInterface( "3", "eth2" ),
			new SNMPInterface( "4", "bond0" )
		);
*/


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
		//List< SNMPInterface > ifListAL = new ArrayList<>();
		ObservableList< SNMPInterface > ifListAL = FXCollections.observableArrayList();
		List< String > ifList1 = new ArrayList<>();
		List< String > ifList2 = new ArrayList<>();

		// Add every line with an interface description OID.
		ifList1.addAll( walk1.stream().filter( line -> line.contains( IfDescriptionOID ) ).collect( Collectors.toList() ) );
		ifList2.addAll( walk2.stream().filter( line -> line.contains( IfDescriptionOID ) ).collect( Collectors.toList() ) );

		// If the two walks have the same interface description OIDs.
		if( ifList1.equals( ifList2 ) )
		{
			// Populate our map.
			ifList1.stream().filter( line -> line.startsWith( IfDescriptionOID ) ).forEach( line -> {
				// The interface index will start at position 21 and end one position before the first equal sign.
				// There may be rare cases where an OID will contain more than one equal sign.
				String ifIndex = line.substring( 21, line.indexOf( " = " ) );
				// The interface description will start after the equal sign, and go to the end of the line.
				String ifDescr = line.substring( line.indexOf( " = " ) + 11 );

				// Create a SNMPInterface class object from those values.
				ifListAL.add( new SNMPInterface( ifDescr, ifIndex ) );
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


	@Override public void start( Stage primaryStage ) throws Exception
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

		Button aButton = new Button( "Show Interfaces" );
		rootNode.add( aButton, 0, 2 );
		GridPane.setHalignment( aButton, HPos.LEFT );

//		ListView< String > ifListView = new ListView<>();
//		rootNode.add( ifListView, 0, 3 );

		table.setEditable( true );

		TableColumn firstNameCol = new TableColumn( "First Name" );
		firstNameCol.setMinWidth( 100 );
		firstNameCol.setCellValueFactory( new PropertyValueFactory< Person, String >( "firstName" ) );

		TableColumn lastNameCol = new TableColumn( "Last Name" );
		lastNameCol.setMinWidth( 100 );
		lastNameCol.setCellValueFactory( new PropertyValueFactory< Person, String >( "lastName" ) );

		TableColumn emailCol = new TableColumn( "Email" );
		emailCol.setMinWidth( 200 );
		emailCol.setCellValueFactory( new PropertyValueFactory< Person, String >( "email" ) );

		table.setItems( data );
		table.getColumns().addAll( firstNameCol, lastNameCol, emailCol );
		rootNode.add( table, 0, 3, 2, 1 );


		TableColumn< Map, String > firstDataColumn = new TableColumn<>( "Class A" );
		TableColumn< Map, String > secondDataColumn = new TableColumn<>( "Class B" );

		firstDataColumn.setCellValueFactory( new MapValueFactory( "A" ) );
		firstDataColumn.setMinWidth( 130 );
		secondDataColumn.setCellValueFactory( new MapValueFactory( "B" ) );
		secondDataColumn.setMinWidth( 130 );

		//TableView< SNMPInterface > ifTableView = new TableView<>();
		TableView< SNMPInterface > ifTableView = new TableView<>();
		ifTableView.setEditable( false );
		TableColumn ifIndexCol = new TableColumn( "Index" );
		TableColumn ifDescrCol = new TableColumn( "Description" );
		ifDescrCol.prefWidthProperty().bind( ifTableView.widthProperty().multiply( 0.7 ) );
//		ifTableView.getColumns().addAll( ifIndexCol, ifDescrCol );
		rootNode.add( ifTableView, 0, 7, 2, 1 );

		aButton.setOnAction( e -> {
			// Read in each file and populate our ArrayLists.
			List< String > inAL1 = ReadFile( firstValue.getText() );
			List< String > inAL2 = ReadFile( secondValue.getText() );

			if( inAL1 != null && inAL2 != null )
			{
				// Find all SNMP interfaces in those SNMP walks.
				//ObservableList< List > interfaceContainer = FXCollections.observableArrayList( FindInterfaces( inAL1, inAL2 ) );
				List< SNMPInterface > ifContainer = FindInterfaces( inAL1, inAL2 );
//				Map< Integer, String > ifContainer = FindInterfaces( inAL1, inAL2 );

				for( SNMPInterface test : ifContainer )
				{
					System.out.println( test.toString() );
				}

				if( ifContainer != null )
				{
					ObservableList< SNMPInterface > ObservableIfContainer = FindInterfaces( inAL1, inAL2 );
//					ObservableMap< Integer, String > ObservableIfContainer = FXCollections.observableMap( ifContainer );

					// Populate our ListView with content from the interfaces.
//		          	ifListView.setItems( ifMap );
//					ifIndexCol.setCellValueFactory( new PropertyValueFactory< SNMPInterface, String >( "ifIndex" ) );
//					ifDescrCol.setCellValueFactory( new PropertyValueFactory< SNMPInterface, String >( "ifDescr" ) );

					ifTableView.getColumns().addAll( ifIndexCol, ifDescrCol );
					ifTableView.setItems( ObservableIfContainer );
				}
			}
			else
			{
				Alert alert = new Alert( Alert.AlertType.ERROR );
				alert.setTitle( "File Error" );
				alert.setHeaderText( "Invalid file name." );
				alert.setContentText( "File does not exist." );

				alert.showAndWait();
			}
		} );

		primaryStage.setScene( primaryScene );

		primaryStage.show();
	}


	public static class Person
	{

		private final SimpleStringProperty firstName;
		private final SimpleStringProperty lastName;
		private final SimpleStringProperty email;


		private Person( String fName, String lName, String email )
		{
			this.firstName = new SimpleStringProperty( fName );
			this.lastName = new SimpleStringProperty( lName );
			this.email = new SimpleStringProperty( email );
		}


		public String getFirstName()
		{
			return firstName.get();
		}


		public void setFirstName( String fName )
		{
			firstName.set( fName );
		}


		public String getLastName()
		{
			return lastName.get();
		}


		public void setLastName( String fName )
		{
			lastName.set( fName );
		}


		public String getEmail()
		{
			return email.get();
		}


		public void setEmail( String fName )
		{
			email.set( fName );
		}
	}
}
