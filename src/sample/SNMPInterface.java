package sample;


/**
 * Created by Adam Howell
 * on 2016-05-05.
 */
public class SNMPInterface
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


	void SNMPInterface( String _ifDescr, int ifIndex, int ifSpeed, int ifInOctets, int ifInDiscards, int ifInErrors, int ifOutOctets, int ifOutDiscards, int ifOutErrors )
	{
		this.ifDescr = _ifDescr;
		this.ifIndex = ifIndex;
		this.ifSpeed = ifSpeed;
		this.ifInOctets = ifInOctets;
		this.ifInDiscards = ifInDiscards;
		this.ifInErrors = ifInErrors;
		this.ifOutOctets = ifOutOctets;
		this.ifOutDiscards = ifOutDiscards;
		this.ifOutErrors = ifOutErrors;
	}
}
