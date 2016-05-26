package com.adamjhowell.snmpinterface.model;


import javafx.beans.property.SimpleStringProperty;


/**
 * Created by Adam Howell
 * on 2016-05-05.
 */
public class SNMPInterface
{
	private final SimpleStringProperty ifDescr;
	private Long ifIndex;
	private Long sysUpTime;
	private Long ifSpeed;
	private Long ifInOctets;
	private Long ifInDiscards;
	private Long ifInErrors;
	private Long ifOutOctets;
	private Long ifOutDiscards;
	private Long ifOutErrors;


	public SNMPInterface( Long ifIndex, String ifDescr, Long sysUpTime, Long ifSpeed, Long ifInOctets, Long ifInDiscards, Long ifInErrors, Long ifOutOctets, Long ifOutDiscards, Long ifOutErrors )
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


	public SNMPInterface( Long ifIndex, String ifDescr )
	{
		this.ifIndex = ifIndex;
		this.ifDescr = new SimpleStringProperty( ifDescr );
	}


	public Long getSysUpTime()
	{
		return sysUpTime;
	}


	public void setSysUpTime( Long sysUpTime )
	{
		this.sysUpTime = sysUpTime;
	}


	public Long getIfIndex()
	{
		return ifIndex;
	}


	public void setIfIndex( Long Index )
	{
		ifIndex = Index;
	}


	public String getIfDescr()
	{
		return ifDescr.get();
	}


	public Long getIfSpeed()
	{
		return ifSpeed;
	}


	public void setIfSpeed( Long ifSpeed )
	{
		this.ifSpeed = ifSpeed;
	}


	public Long getIfInOctets()
	{
		return ifInOctets;
	}


	public void setIfInOctets( Long ifInOctets )
	{
		this.ifInOctets = ifInOctets;
	}


	public Long getIfInDiscards()
	{
		return ifInDiscards;
	}


	public void setIfInDiscards( Long ifInDiscards )
	{
		this.ifInDiscards = ifInDiscards;
	}


	public Long getIfInErrors()
	{
		return ifInErrors;
	}


	public void setIfInErrors( Long ifInErrors )
	{
		this.ifInErrors = ifInErrors;
	}


	public Long getIfOutOctets()
	{
		return ifOutOctets;
	}


	public void setIfOutOctets( Long ifOutOctets )
	{
		this.ifOutOctets = ifOutOctets;
	}


	public Long getIfOutDiscards()
	{
		return ifOutDiscards;
	}


	public void setIfOutDiscards( Long ifOutDiscards )
	{
		this.ifOutDiscards = ifOutDiscards;
	}


	public Long getIfOutErrors()
	{
		return ifOutErrors;
	}


	public void setIfOutErrors( Long ifOutErrors )
	{
		this.ifOutErrors = ifOutErrors;
	}


	@Override
	public String toString()
	{
		return "SNMPInterface: ifIndex = " + ifIndex + " ifDescr = " + ifDescr;
	}
}
