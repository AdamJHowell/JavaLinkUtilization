package com.adamjhowell.snmpinterface.model;


/**
 * Created by Adam Howell on 2016-05-25.
 * This class is meant to hold data that is fed to a JavaFX TableView object (statisticTableView).
 * The "description" class member will go into column 1, named "Description".
 * The "value" class member will go into column 2, named "Value".
 */
public class InterfaceStats
{
	private String description;
	private String value;


	/**
	 * InterfaceStats parameterized constructor.
	 *
	 * @param description a description of the value.
	 * @param value       the value/stat to display.
	 */
	public InterfaceStats( String description, String value )
	{
		this.description = description;
		this.value = value;
	}


	public String getDescription()
	{
		return description;
	}


	public String getValue()
	{
		return value;
	}


	/**
	 * I wanted a cleaner toString().
	 * @return a string representing the object.
	 */
	@Override
	public String toString()
	{
		return description + " " + value;
	}
}
