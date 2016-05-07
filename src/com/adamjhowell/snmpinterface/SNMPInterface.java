package com.adamjhowell.snmpinterface;


/**
 * Created by Adam Howell
 * on 2016-05-05.
 */
class SNMPInterface
{
	private String ifDescr;
	private String ifIndex;
	private int ifSpeed;
	private int ifInOctets;
	private int ifInDiscards;
	private int ifInErrors;
	private int ifOutOctets;
	private int ifOutDiscards;
	private int ifOutErrors;


	SNMPInterface( String ifDescr, String ifIndex, int ifSpeed, int ifInOctets, int ifInDiscards, int ifInErrors, int ifOutOctets, int ifOutDiscards, int ifOutErrors )
	{
		this.ifDescr = ifDescr;

		this.ifIndex = ifIndex;
		this.ifSpeed = ifSpeed;
		this.ifInOctets = ifInOctets;
		this.ifInDiscards = ifInDiscards;
		this.ifInErrors = ifInErrors;
		this.ifOutOctets = ifOutOctets;
		this.ifOutDiscards = ifOutDiscards;
		this.ifOutErrors = ifOutErrors;
	}


	SNMPInterface( String ifDescr, String ifIndex )
	{
		this.ifDescr = ifDescr;
		this.ifIndex = ifIndex;
	}


	public String getIfDescr()
	{
		return ifDescr;
	}


	public void setIfDescr( String ifDescr )
	{
		this.ifDescr = ifDescr;
	}


	public String getIfIndex()
	{
		return ifIndex;
	}


	public void setIfIndex( String ifIndex )
	{
		this.ifIndex = ifIndex;
	}


	@Override public String toString()
	{
		return "SNMPInterface: ifIndex = " + ifIndex + " ifDescr = " + ifDescr;
	}
}
