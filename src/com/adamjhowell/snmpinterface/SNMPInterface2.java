package com.adamjhowell.snmpinterface;


import javafx.beans.property.SimpleStringProperty;


/**
 * Created by Adam Howell
 * on 2016-05-05.
 */
class SNMPInterface2
{
	private final SimpleStringProperty ifIndex;
	private final SimpleStringProperty ifDescr;
/*	private int ifSpeed;
	private int ifInOctets;
	private int ifInDiscards;
	private int ifInErrors;
	private int ifOutOctets;
	private int ifOutDiscards;
	private int ifOutErrors;
*/

/*	SNMPInterface2( String ifDescr, String ifIndex, int ifSpeed, int ifInOctets, int ifInDiscards, int ifInErrors, int ifOutOctets, int ifOutDiscards, int ifOutErrors )
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
*/


	SNMPInterface2( String ifIndex, String ifDescr )
	{
		this.ifIndex = new SimpleStringProperty( ifIndex );
		this.ifDescr = new SimpleStringProperty( ifDescr );
	}


	String getIfIndex()
	{
		return ifIndex.get();
	}


	void setIfIndex( String Index )
	{
		ifIndex.set( Index );
	}


	String getIfDescr()
	{
		return ifDescr.get();
	}


	void setIfDescr( String Descr )
	{
		ifDescr.set( Descr );
	}


	@Override
	public String toString()
	{
		return "SNMPInterface2: ifIndex = " + ifIndex + " ifDescr = " + ifDescr;
	}
}
