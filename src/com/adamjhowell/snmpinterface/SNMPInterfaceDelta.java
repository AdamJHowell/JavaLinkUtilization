package com.adamjhowell.snmpinterface;


/**
 * Created by AdamRules
 * on 2016-05-13.
 * This class is meant to represent the delta between two SNMPInterface class objects.
 * The two SNMPInterface class objects should be the same interface, but sampled at different times.
 */
public class SNMPInterfaceDelta
{
	private SNMPInterface walk1;
	private SNMPInterface walk2;
	private double timeDelta;
	private long inOctetDelta;
	private long outOctetDelta;
	private long totalDelta;
	private double inUtilization;
	private double outUtilization;
	private double totalUtilization;
	private long inDiscardDelta;
	private long outDiscardDelta;
	private long inErrorDelta;
	private long outErrorDelta;


	public void setTimeDelta( long timeDelta )
	{
		this.timeDelta = timeDelta;
	}


	public void setInOctetDelta( long inOctetDelta )
	{
		this.inOctetDelta = inOctetDelta;
	}


	public void setOutOctetDelta( long outOctetDelta )
	{
		this.outOctetDelta = outOctetDelta;
	}


	public void setTotalDelta( long totalDelta )
	{
		this.totalDelta = totalDelta;
	}


	public void setInUtilization( double inUtilization )
	{
		this.inUtilization = inUtilization;
	}


	public void setOutUtilization( double outUtilization )
	{
		this.outUtilization = outUtilization;
	}


	public void setTotalUtilization( double totalUtilization )
	{
		this.totalUtilization = totalUtilization;
	}


	public void setInDiscardDelta( long inDiscardDelta )
	{
		this.inDiscardDelta = inDiscardDelta;
	}


	public void setOutDiscardDelta( long outDiscardDelta )
	{
		this.outDiscardDelta = outDiscardDelta;
	}


	public void setInErrorDelta( long inErrorDelta )
	{
		this.inErrorDelta = inErrorDelta;
	}


	public void setOutErrorDelta( long outErrorDelta )
	{
		this.outErrorDelta = outErrorDelta;
	}


	public double getTimeDelta()
	{
		return timeDelta;
	}


	public long getInOctetDelta()
	{
		return inOctetDelta;
	}


	public long getOutOctetDelta()
	{
		return outOctetDelta;
	}


	public long getTotalDelta()
	{
		return totalDelta;
	}


	public double getInUtilization()
	{
		return inUtilization;
	}


	public double getOutUtilization()
	{
		return outUtilization;
	}


	public double getTotalUtilization()
	{
		return totalUtilization;
	}


	public long getInDiscardDelta()
	{
		return inDiscardDelta;
	}


	public long getOutDiscardDelta()
	{
		return outDiscardDelta;
	}


	public long getInErrorDelta()
	{
		return inErrorDelta;
	}


	public long getOutErrorDelta()
	{
		return outErrorDelta;
	}


	SNMPInterfaceDelta( SNMPInterface walk1, SNMPInterface walk2 )
	{
		this.walk1 = walk1;
		this.walk2 = walk2;
	}
}
