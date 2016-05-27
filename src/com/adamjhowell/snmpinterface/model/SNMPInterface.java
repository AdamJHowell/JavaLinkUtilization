package com.adamjhowell.snmpinterface.model;


/**
 * Created by Adam Howell on 2016-05-05.
 * This class is meant to represent all of the stats pertinent to a SNMP interface or link.
 * A SNMP interface is essentially a NIC (Network Interface Card) or network adapter.
 * It may be a wired ethernet port, an 802.11 radio, a bluetooth adapter, a USB network device, or similar hardware.
 * It may also be a virtual (software) network adapter.
 * <p>
 * This class is used to transport data around within this program.
 * This class (short constructor) is also used to format data for display in a JavaFX TableView object (interfaceTableView).
 * The ifIndex class member will go into column 1, named "Index".
 * The ifDescr class member will go into column 2, named "Name".
 */
public class SNMPInterface
{
	private String ifDescr;
	private Long ifIndex;
	private Long sysUpTime;
	private Long ifSpeed;
	private Long ifInOctets;
	private Long ifInDiscards;
	private Long ifInErrors;
	private Long ifOutOctets;
	private Long ifOutDiscards;
	private Long ifOutErrors;


	/**
	 * This constructor populates all class member variables with data that will be used to calculate pertinent statistics.
	 *
	 * @param ifIndex       the interface index number from the SNMP walk.
	 * @param ifDescr       the interface description from the SNMP walk.
	 * @param sysUpTime     the system uptime from the SNMP walk.
	 * @param ifSpeed       the interface speed from the SNMP walk.
	 * @param ifInOctets    the interface inbound octet count from the SNMP walk.
	 * @param ifInDiscards  the interface inbound discard count from the SNMP walk.
	 * @param ifInErrors    the interface inbound error count from the SNMP walk.
	 * @param ifOutOctets   the interface outbound octet count from the SNMP walk.
	 * @param ifOutDiscards the interface outbound discard count from the SNMP walk.
	 * @param ifOutErrors   the interface outbound error count from the SNMP walk.
	 */
	public SNMPInterface( Long ifIndex,
		String ifDescr,
		Long sysUpTime,
		Long ifSpeed,
		Long ifInOctets,
		Long ifInDiscards,
		Long ifInErrors,
		Long ifOutOctets,
		Long ifOutDiscards,
		Long ifOutErrors )
	{
		this.ifIndex = ifIndex;
		this.ifDescr = ifDescr;
		this.sysUpTime = sysUpTime;
		this.ifSpeed = ifSpeed;
		this.ifInOctets = ifInOctets;
		this.ifInDiscards = ifInDiscards;
		this.ifInErrors = ifInErrors;
		this.ifOutOctets = ifOutOctets;
		this.ifOutDiscards = ifOutDiscards;
		this.ifOutErrors = ifOutErrors;
	}


	/**
	 * This constructor is used to populate the "interfaceTableView" JavaFX object.
	 *
	 * @param ifIndex the interface index number from the SNMP walk.
	 * @param ifDescr the interface description from the SNMP walk.
	 */
	public SNMPInterface( Long ifIndex, String ifDescr )
	{
		this.ifIndex = ifIndex;
		this.ifDescr = ifDescr;
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
		return ifDescr;
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


	/**
	 * A clearer toString() than the default.
	 * @return a string representing the minimum required elements.
	 */
	@Override
	public String toString()
	{
		return "SNMPInterface: ifIndex = " + ifIndex + " ifDescr = " + ifDescr;
	}
}
