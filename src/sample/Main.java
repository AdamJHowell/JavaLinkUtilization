package sample;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Main extends Application
{

	@Override
	public void start( Stage primaryStage ) throws Exception
	{
		primaryStage.setTitle( "SNMP Link Utilization" );

		GridPane rootNode = new GridPane();
		rootNode.setPadding( new Insets( 15 ) );
		rootNode.setHgap( 5 );
		rootNode.setVgap( 5 );
		rootNode.setAlignment( Pos.CENTER );

		Scene myScene = new Scene( rootNode, 400, 300 );

		rootNode.add( new Label( "First walk:" ), 0, 0 );
		TextField firstValue = new TextField();
		firstValue.setText( "walk1.txt" );
		rootNode.add( firstValue, 1, 0 );

		rootNode.add( new Label( "Second walk:" ), 0, 1 );
		TextField secondValue = new TextField();
		secondValue.setText( "walk2.txt" );
		rootNode.add( secondValue, 1, 1 );

		Button aButton = new Button( "Show Interfaces" );
		rootNode.add( aButton, 1, 2 );
		GridPane.setHalignment( aButton, HPos.LEFT );

		ListView<String> IfListView = new ListView<>();
		rootNode.add( IfListView, 1, 3 );

		aButton.setOnAction( e -> {
			//result.setText( ReadFile( firstValue.getText() ) );
			List< String > inAL1 = ReadFile( firstValue.getText() );
			List< String > inAL2 = ReadFile( secondValue.getText() );
			CalculateUtilization( inAL1, inAL2 );
			ObservableList<String> data = FXCollections.observableArrayList( CalculateUtilization( inAL1, inAL2 ) );
			IfListView.setItems( data );
		} );

		primaryStage.setScene( myScene );

		primaryStage.show();
	}


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


	private static List<String> CalculateUtilization( List< String > walk1, List< String > walk2 )
	{
		String IfDescriptionOID = ".1.3.6.1.2.1.2.2.1.2.";          // The OID for ifDescr.
		List< String > ifList1 = new ArrayList<>();
		List< String > ifList2 = new ArrayList<>();
		ifList1.addAll( walk1.stream().filter( line -> line.contains( IfDescriptionOID ) ).collect( Collectors.toList() ) );
		ifList1.forEach( System.out::println );
		ifList2.addAll( walk2.stream().filter( line -> line.contains( IfDescriptionOID ) ).collect( Collectors.toList() ) );
		ifList2.forEach( System.out::println );
		return ifList1;
	}
}
