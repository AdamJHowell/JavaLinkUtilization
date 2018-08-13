package com.adamjhowell.snmpinterface.model;


/**
 * This class is a structure meant to represent all of the stats pertinent to this implementation of a SNMP interface or link.<br>
 * A SNMP interface is essentially a NIC (Network Interface Card) or network adapter.<br>
 * It may be a wired ethernet port, an 802.11 radio, a bluetooth adapter, a USB network device, virtual device, etc.<br>
 * There are other attributes to an interface that are not implemented in this program.
 * <p>
 * This class is used to transport data within this program.<br>
 * This class (short constructor) is also used to format data for display in a JavaFX TableView object (interfaceTableView).<br>
 * The ifIndex class member will go into column 1, named "Index".<br>
 * The ifDescr class member will go into column 2, named "Description".
 * <p>
 * Created by Adam Howell on 2016-05-05.
 */
public class SnmpInterface
{
	private Long ifIndex;
	private String ifDescr;
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
	 * @param ifIndex       The interface index number from the SNMP walk.
	 * @param ifDescr       The interface description from the SNMP walk.
	 * @param sysUpTime     The system uptime from the SNMP walk.
	 * @param ifSpeed       The interface speed from the SNMP walk.
	 * @param ifInOctets    The interface inbound octet count from the SNMP walk.
	 * @param ifInDiscards  The interface inbound discard count from the SNMP walk.
	 * @param ifInErrors    The interface inbound error count from the SNMP walk.
	 * @param ifOutOctets   The interface outbound octet count from the SNMP walk.
	 * @param ifOutDiscards The interface outbound discard count from the SNMP walk.
	 * @param ifOutErrors   The interface outbound error count from the SNMP walk.
	 */
	@SuppressWarnings( "squid:S00107" )
	public SnmpInterface( Long ifIndex, String ifDescr, Long sysUpTime, Long ifSpeed, Long ifInOctets, Long ifInDiscards, Long ifInErrors, Long ifOutOctets, Long ifOutDiscards, Long ifOutErrors )
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
	 * @param ifIndex The interface index number from the SNMP walk.
	 * @param ifDescr The interface description from the SNMP walk.
	 */
	public SnmpInterface( Long ifIndex, String ifDescr )
	{
		this.ifIndex = ifIndex;
		this.ifDescr = ifDescr;
	}


	public Long getIfIndex()
	{
		return ifIndex;
	}


	/**
	 * getIfDescr is required for the PropertyValueFactory to function.<br>
	 * If you delete this, you are going to have a bad time.
	 *
	 * @return the interface description.
	 */
	@SuppressWarnings( "unused" )
	public String getIfDescr()
	{
		return ifDescr;
	}


	public Long getSysUpTime()
	{
		return sysUpTime;
	}


	public Long getIfSpeed()
	{
		return ifSpeed;
	}


	public Long getIfInOctets()
	{
		return ifInOctets;
	}


	public Long getIfInDiscards()
	{
		return ifInDiscards;
	}


	public Long getIfInErrors()
	{
		return ifInErrors;
	}


	public Long getIfOutOctets()
	{
		return ifOutOctets;
	}


	public Long getIfOutDiscards()
	{
		return ifOutDiscards;
	}


	public Long getIfOutErrors()
	{
		return ifOutErrors;
	}


	/**
	 * @return A string representing the minimum required elements.
	 */
	@Override
	public String toString()
	{
		return "SNMPInterface: ifIndex = " + ifIndex + " ifDescr = " + ifDescr;
	}
}
