package lnu.mida.entity;

import peersim.config.Configuration;

public class EnergyLocalReputation implements Cloneable  {
	
	
	// service id for which the experience is evaluated
	private long serviceID;
	
	// number of time the experience is done
	private int k;
	
	// estimated value
	private double ee;
	
	// history weight
	private double ALPHA;
	
	
	public EnergyLocalReputation(long serviceID) {
		this.setServiceID(serviceID);
		k=0;
		ee=0;
		ALPHA = Configuration.getDouble("ALPHA",0);
	}
	
	public void addDeclaredEnergy(double declaredEnergy) {		
		
		double ee_new = ALPHA*declaredEnergy + ( (1-ALPHA)*ee );		
		ee = ee_new;
		k++;
			
	}
	
	@Override
	public Object clone() {	
		EnergyLocalReputation result = null;
		try {
			result = (EnergyLocalReputation) super.clone();
		} catch (CloneNotSupportedException ex) {
			System.out.println(ex.getMessage());
			assert(false);
		}
		return result;
	}

	public long getK() {
		return k;
	}
	
	public void setK(int k) {
		this.k=k;
	}

	public double getEe() {
		return ee;
	}

	public long getServiceID() {
		return serviceID;
	}

	public void setServiceID(long serviceID) {
		this.serviceID = serviceID;
	}


}