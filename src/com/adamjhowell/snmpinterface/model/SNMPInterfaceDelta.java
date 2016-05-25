package com.adamjhowell.snmpinterface.model;


/**
 * Created by AdamRules
 * on 2016-05-13.
 * This class is meant to represent the delta between two SNMPInterface class objects.
 * The two SNMPInterface class objects should be the same interface, but sampled at different times.
 */
public class SNMPInterfaceDelta
{
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


	public double getTimeDelta()
	{
		return timeDelta;
	}


	public void setTimeDelta( long timeDelta )
	{
		this.timeDelta = timeDelta;
	}


	public long getInOctetDelta()
	{
		return inOctetDelta;
	}


	public void setInOctetDelta( long inOctetDelta )
	{
		this.inOctetDelta = inOctetDelta;
	}


	public long getOutOctetDelta()
	{
		return outOctetDelta;
	}


	public void setOutOctetDelta( long outOctetDelta )
	{
		this.outOctetDelta = outOctetDelta;
	}


	public long getTotalDelta()
	{
		return totalDelta;
	}


	public void setTotalDelta( long totalDelta )
	{
		this.totalDelta = totalDelta;
	}


	public double getInUtilization()
	{
		return inUtilization;
	}


	public void setInUtilization( double inUtilization )
	{
		this.inUtilization = inUtilization;
	}


	public double getOutUtilization()
	{
		return outUtilization;
	}


	public void setOutUtilization( double outUtilization )
	{
		this.outUtilization = outUtilization;
	}


	public double getTotalUtilization()
	{
		return totalUtilization;
	}


	public void setTotalUtilization( double totalUtilization )
	{
		this.totalUtilization = totalUtilization;
	}


	public long getInDiscardDelta()
	{
		return inDiscardDelta;
	}


	public void setInDiscardDelta( long inDiscardDelta )
	{
		this.inDiscardDelta = inDiscardDelta;
	}


	public long getOutDiscardDelta()
	{
		return outDiscardDelta;
	}


	public void setOutDiscardDelta( long outDiscardDelta )
	{
		this.outDiscardDelta = outDiscardDelta;
	}


	public long getInErrorDelta()
	{
		return inErrorDelta;
	}


	public void setInErrorDelta( long inErrorDelta )
	{
		this.inErrorDelta = inErrorDelta;
	}


	public long getOutErrorDelta()
	{
		return outErrorDelta;
	}


	public void setOutErrorDelta( long outErrorDelta )
	{
		this.outErrorDelta = outErrorDelta;
	}
}
