package lnu.mida.entity;

import peersim.config.Configuration;

public class EnergyBatteryPanelReputation implements Cloneable  {
	
	
	// service id for which the experience is evaluated
	private long nodeID;
	
	// number of time the experience is done
	private int k;
	
	// approach to challenge
	private double ee;
	
	// history weight
	private double ALPHA;
	
	
	public EnergyBatteryPanelReputation(long nodeID) {
		this.setNodeID(nodeID);
		k=0;
		ee=0;
		ALPHA = Configuration.getDouble("ALPHA",0);
	}
	
	public void addDeclaredEnergy(double declaredEnergy) {		
		double ee_new = ALPHA*declaredEnergy + ( (1-ALPHA)*ee );		
		ee = ee_new;
		k++;
		
		GeneralNode node = GeneralNode.getNode(nodeID);
		node.setEeBPEnergy(ee);
		node.setEeBPCounter(k);
		
	}
	
	@Override
	public Object clone() {	
		EnergyBatteryPanelReputation result = null;
		try {
			result = (EnergyBatteryPanelReputation) super.clone();
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

	public long getNodeId() {
		return nodeID;
	}

	public void setNodeID(long nodeID) {
		this.nodeID = nodeID;
	}

}
