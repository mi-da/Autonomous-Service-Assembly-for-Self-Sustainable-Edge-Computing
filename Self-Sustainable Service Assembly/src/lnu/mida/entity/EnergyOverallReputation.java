package lnu.mida.entity;

import peersim.config.Configuration;

public class EnergyOverallReputation implements Cloneable  {
	
	
	// service id for which the experience is evaluated
	private long serviceID;

	// number of time the experience is done
	private int k;
	
	// approach to challenge
	private double ee;
	
	// history weight
	private double ALPHA;
	
	
	public EnergyOverallReputation(long serviceID) {
		this.setServiceID(serviceID);
		k=0;
		ee=0;
		ALPHA = Configuration.getDouble("ALPHA",0);
	}
	
	public void addDeclaredEnergy(double declaredEnergy) {
		
		double W=1;
		if(k>0) {
			W = ALPHA + (1-ALPHA)/k;
		}
		
		double ee_new = W*declaredEnergy + ( (1-W)*ee );		
		ee = ee_new;
		k++;
		
	}
	
	@Override
	public Object clone() {	
		EnergyOverallReputation result = null;
		try {
			result = (EnergyOverallReputation) super.clone();
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
