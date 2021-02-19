package lnu.mida.entity;

public class OverallEnergyReputation implements Cloneable  {
	
	
	// service id for which the experience is evaluated
	private long serviceID;
	
	// number of time the experience is done
	private int k;
	// window period of the learner
	private static int M;
	
	// approach to challenge
	private double ee;
	
	
	public OverallEnergyReputation(long serviceID) {
		this.setServiceID(serviceID);
		k=0;
		ee=0;
	}
	
	public void addDeclaredEnergy(double declaredEnergy) {		
		
		
		double W = 0.1 + (0.9/k);
		if(k==0)
			W=1;
		
		
		//double W = 0.3;
		
		double ee_new = W*declaredEnergy + ( (1-W)*ee );		
		ee = ee_new;
		
		k++;
		

		/*
		 * 
		if(serviceID==0)
			System.out.println("  servizio " + serviceID + "  ee  =" + ee + "    k = " + k);
		 */
	}
	
	@Override
	public Object clone() {	
		EnergyReputation result = null;
		try {
			result = (EnergyReputation) super.clone();
		} catch (CloneNotSupportedException ex) {
			System.out.println(ex.getMessage());
			assert(false);
		}
		return result;
	}

	public int getK() {
		return k;
	}
	
	public void setK(int k) {
		this.k=k;
	}

	public static int getM() {
		return M;
	}

	public static void setM(int m) {
		M = m;
	}

	public double getEe() {
		return ee;
	}

	public long getServiceId() {
		return serviceID;
	}

	public void setServiceID(long nodeID) {
		this.serviceID = nodeID;
	}

}
