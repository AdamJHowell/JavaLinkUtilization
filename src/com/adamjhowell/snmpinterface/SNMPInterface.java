package com.adamjhowell.snmpinterface;


import javafx.beans.property.SimpleStringProperty;


/**
 * Created by Adam Howell
 * on 2016-05-05.
 */
class SNMPInterface
{
	private final SimpleStringProperty ifDescr;
	private final SimpleStringProperty ifIndex;
	private int ifSpeed;
	private int ifInOctets;
	private int ifInDiscards;
	private int ifInErrors;
	private int ifOutOctets;
	private int ifOutDiscards;
	private int ifOutErrors;


	SNMPInterface( String ifDescr, String ifIndex, int ifSpeed, int ifInOctets, int ifInDiscards, int ifInErrors, int ifOutOctets, int ifOutDiscards, int ifOutErrors )
	{
		this.ifDescr = new SimpleStringProperty( ifDescr );
		this.ifIndex = new SimpleStringProperty( ifIndex );
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
		this.ifDescr = new SimpleStringProperty( ifDescr );
		this.ifIndex = new SimpleStringProperty( ifIndex );
	}
}
