package sample;


import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Main extends Application
{

	@Override
	public void start( Stage myStage ) throws Exception
	{
		//List< String > inAL1 = new ArrayList<>();
		//List< String > inAL2 = new ArrayList<>();

		myStage.setTitle( "SNMP Link Utilization" );

		GridPane rootNode = new GridPane();
		rootNode.setPadding( new Insets( 15 ) );
		rootNode.setHgap( 5 );
		rootNode.setVgap( 5 );
		rootNode.setAlignment( Pos.CENTER );

		Scene myScene = new Scene( rootNode, 300, 200 );

		rootNode.add( new Label( "First walk:" ), 0, 0 );
		TextField firstValue = new TextField();
		rootNode.add( firstValue, 1, 0 );

		rootNode.add( new Label( "Second walk:" ), 0, 1 );
		TextField secondValue = new TextField();
		rootNode.add( secondValue, 1, 1 );

		Button aButton = new Button( "Calculate" );
		rootNode.add( aButton, 1, 2 );
		GridPane.setHalignment( aButton, HPos.LEFT );

		TextField result = new TextField();
		result.setEditable( false );
		rootNode.add( result, 1, 3 );

		aButton.setOnAction( e -> {
/*
			Integer value1 = Integer.valueOf( firstValue.getText() );
			Integer value2 = Integer.valueOf( secondValue.getText() );
			Integer r = value1 + value2;
			result.setText( r.toString() );
*/
			//result.setText( ReadFile( firstValue.getText() ) );
			List<String> inAL1 = ReadFile( firstValue.getText() );
			List<String> inAL2 = ReadFile( secondValue.getText() );
			CalculateUtilization( inAL1, inAL2 );
		} );

		myStage.setScene( myScene );

		myStage.show();
	}


	public static void main( String[] args )
	{
		launch( args );
	}


	private static List<String> ReadFile( String inFileName )
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
				// Output every element in our ArrayList.
				inAL.forEach( System.out::println );
				return inAL;
			}
		}
		catch( IOException ioe )
		{
			ioe.getLocalizedMessage();
		}
		return null;
	}


	private static double CalculateUtilization( List<String> walk1, List<String> walk2 )
	{
		return 0.0;
	}
}
