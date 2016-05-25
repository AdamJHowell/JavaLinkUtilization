package com.adamjhowell.snmpinterface.model;


/**
 * Created by Adam Howell
 * on 2016-05-25.
 */
public class InterfaceStats
{
	private String description;
	private String value;


	public InterfaceStats( String description, String value )
	{
		this.description = description;
		this.value = value;
	}


	public String getDescription()
	{
		return description;
	}


	public void setDescription( String description )
	{
		this.description = description;
	}


	public String getValue()
	{
		return value;
	}


	public void setValue( String value )
	{
		this.value = value;
	}
}
