package com.adamjhowell.snmpinterface;


import javafx.beans.property.SimpleStringProperty;


/**
 * Created by Adam Howell
 * on 2016-05-05.
 */
public class SNMPInterface
{
	private final SimpleStringProperty ifIndex;
	private final SimpleStringProperty ifDescr;
	private int ifSpeed;
	private int ifInOctets;
	private int ifInDiscards;
	private int ifInErrors;
	private int ifOutOctets;
	private int ifOutDiscards;
	private int ifOutErrors;


	SNMPInterface( String ifDescr, String ifIndex, int ifSpeed, int ifInOctets, int ifInDiscards, int ifInErrors, int ifOutOctets, int ifOutDiscards, int ifOutErrors )
	{
		this.ifIndex = new SimpleStringProperty( ifIndex );
		this.ifDescr = new SimpleStringProperty( ifDescr );
		this.ifSpeed = ifSpeed;
		this.ifInOctets = ifInOctets;
		this.ifInDiscards = ifInDiscards;
		this.ifInErrors = ifInErrors;
		this.ifOutOctets = ifOutOctets;
		this.ifOutDiscards = ifOutDiscards;
		this.ifOutErrors = ifOutErrors;
	}


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
