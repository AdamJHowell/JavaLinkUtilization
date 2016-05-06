package com.adamjhowell.snmpinterface;


/**
 * Created by Adam Howell
 * on 2016-05-05.
 */
class SNMPInterface
{
	private String ifDescr;
	private int ifIndex;
	private int ifSpeed;
	private int ifInOctets;
	private int ifInDiscards;
	private int ifInErrors;
	private int ifOutOctets;
	private int ifOutDiscards;
	private int ifOutErrors;


	SNMPInterface( String ifDescr, int ifIndex, int ifSpeed, int ifInOctets, int ifInDiscards, int ifInErrors, int ifOutOctets, int ifOutDiscards, int ifOutErrors )
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


	SNMPInterface( String ifDescr, int ifIndex )
	{
		this.ifDescr = ifDescr;
		this.ifIndex = ifIndex;
	}
}
