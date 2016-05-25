package com.adamjhowell.snmpinterface.model;


import javafx.beans.property.SimpleStringProperty;


/**
 * Created by Adam Howell
 * on 2016-05-05.
 */
public class SNMPInterface
{
	private final SimpleStringProperty ifDescr;
	private long ifIndex;
	private long sysUpTime;
	private long ifSpeed;
	private long ifInOctets;
	private long ifInDiscards;
	private long ifInErrors;
	private long ifOutOctets;
	private long ifOutDiscards;
	private long ifOutErrors;


	public SNMPInterface( long ifIndex, String ifDescr, long sysUpTime, long ifSpeed, long ifInOctets, long ifInDiscards, long ifInErrors, long ifOutOctets, long ifOutDiscards, long ifOutErrors )
	{
		this.ifIndex = ifIndex;
		this.ifDescr = new SimpleStringProperty( ifDescr );
		this.sysUpTime = sysUpTime;
		this.ifSpeed = ifSpeed;
		this.ifInOctets = ifInOctets;
		this.ifInDiscards = ifInDiscards;
		this.ifInErrors = ifInErrors;
		this.ifOutOctets = ifOutOctets;
		this.ifOutDiscards = ifOutDiscards;
		this.ifOutErrors = ifOutErrors;
	}


	public SNMPInterface( long ifIndex, String ifDescr )
	{
		this.ifIndex = ifIndex;
		this.ifDescr = new SimpleStringProperty( ifDescr );
	}


	public long getSysUpTime()
	{
		return sysUpTime;
	}


	public void setSysUpTime( long sysUpTime )
	{
		this.sysUpTime = sysUpTime;
	}


	public long getIfIndex()
	{
		return ifIndex;
	}


	public void setIfIndex( long Index )
	{
		ifIndex = Index;
	}


	public String getIfDescr()
	{
		return ifDescr.get();
	}


	public void setIfDescr( String Descr )
	{
		ifDescr.set( Descr );
	}


	public long getIfSpeed()
	{
		return ifSpeed;
	}


	public void setIfSpeed( long ifSpeed )
	{
		this.ifSpeed = ifSpeed;
	}


	public long getIfInOctets()
	{
		return ifInOctets;
	}


	public void setIfInOctets( long ifInOctets )
	{
		this.ifInOctets = ifInOctets;
	}


	public long getIfInDiscards()
	{
		return ifInDiscards;
	}


	public void setIfInDiscards( long ifInDiscards )
	{
		this.ifInDiscards = ifInDiscards;
	}


	public long getIfInErrors()
	{
		return ifInErrors;
	}


	public void setIfInErrors( long ifInErrors )
	{
		this.ifInErrors = ifInErrors;
	}


	public long getIfOutOctets()
	{
		return ifOutOctets;
	}


	public void setIfOutOctets( long ifOutOctets )
	{
		this.ifOutOctets = ifOutOctets;
	}


	public long getIfOutDiscards()
	{
		return ifOutDiscards;
	}


	public void setIfOutDiscards( long ifOutDiscards )
	{
		this.ifOutDiscards = ifOutDiscards;
	}


	public long getIfOutErrors()
	{
		return ifOutErrors;
	}


	public void setIfOutErrors( long ifOutErrors )
	{
		this.ifOutErrors = ifOutErrors;
	}


	@Override
	public String toString()
	{
		return "SNMPInterface: ifIndex = " + ifIndex + " ifDescr = " + ifDescr;
	}
}
