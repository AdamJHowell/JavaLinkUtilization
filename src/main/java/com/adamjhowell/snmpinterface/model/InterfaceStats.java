package com.adamjhowell.snmpinterface.model;


/**
 * This class is meant to hold data that is fed to a JavaFX TableView object (statisticTableView).<br>
 * The "description" class member will go into column 1, named "Description".<br>
 * The "value" class member will go into column 2, named "Value".
 * <p>
 * Created by Adam Howell on 2016-05-25.
 */
public class InterfaceStats
{
	/**
	 * Essentially a key in a Map object.
	 */
	private String description;
	/**
	 * Essentially a value in a Map object.
	 */
	private String value;


	/**
	 * Parameterized constructor.
	 *
	 * @param description A description of the value.
	 * @param value       The value/stat to display.
	 */
	public InterfaceStats( String description, String value )
	{
		this.description = description;
		this.value = value;
	}


	/**
	 * Since this class is used as an adapter, getters are required to operate properly.
	 *
	 * @return The text description to display.
	 */
	@SuppressWarnings( "unused" )
	public String getDescription()
	{
		return description;
	}


	/**
	 * Since this class is used as an adapter, getters are required to operate properly.
	 *
	 * @return The text value to display.
	 */
	@SuppressWarnings( "unused" )
	public String getValue()
	{
		return value;
	}


	/**
	 * @return A string representing the object.
	 */
	@Override
	public String toString()
	{
		return description + " " + value;
	}
}
