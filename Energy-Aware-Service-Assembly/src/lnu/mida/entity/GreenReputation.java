package lnu.mida.entity;

import java.io.PrintStream;

import lnu.mida.controller.init.OverloadFileInitializer;
import peersim.cdsim.CDState;

public class GreenReputation {
	
	
	// service id for which the experience is evaluated
	private long nodeID;
	
	// window period of the learner
	private static int M = 144;
	
	private double predicted_val;
	
	private double level;
	private double previous_level;
	private double trend;
	private double previous_trend;
	
	private double[] seasonality;
	
	private int counter;
	
	public GreenReputation(long nodeID) {
		this.setNodeID(nodeID);
		//counter=0;
		predicted_val=0;
		seasonality = new double[M];
	}
	
	public void addDeclaredEnergy(double new_val) {		
		
		if(CDState.getCycle()==286)
			initializeParameters();
		
		// small values mean older values in the series are weighted more heavily
		double alpha = 0.2;
		double beta = 0.2;
		double gamma = 0.2;
		
		int h = 1;
		int t = CDState.getCycle();
		int k = (int) (h-1)/M;
		
		
		/***** ADDITIVE METHOD *****/
		
		
		// predicted value
		double new_predicted_val=0;
		for(int i=1; i<6; i++)
			new_predicted_val += level + trend*i + seasonality[(t+i-M*(k+1))%M];
			
		//double new_predicted_val = level + trend*h + seasonality[(t+h-M*(k+1))%M]; 
		
		
		// update level
		double new_level = alpha*(new_val-seasonality[(t-M)%M]) + (1-alpha)*(previous_level + previous_trend);
		
		// update trend
		double new_trend = beta*(level - previous_level) + (1 - beta) * previous_trend;
				
		// update seasonality
		seasonality[t%M] = gamma * (new_val - previous_level - previous_trend) + (1 - gamma) * seasonality[(t-M)%M];
		
		previous_level = level;
		level = new_level;
		
		previous_trend=trend;
		trend=new_trend;
		
		if(new_predicted_val<0)
			predicted_val=0;
		else
			predicted_val = new_predicted_val;
		
		
		k++;
		

		counter++;
			
		PrintStream ps_nodo2 = OverloadFileInitializer.getPs_nodo2();

		//System.out.println("    nodo " + nodeID + "      prevision : " + predicted_val);

		GeneralNode node = GeneralNode.getNode(nodeID);
		node.setPredictedG(predicted_val);
		
		//if(nodeID==0)
		//	System.out.println("    prevision = " + predicted_val + "   c = " + counter);

		//if(nodeID==10)
		//	ps_nodo2.print(predicted_val+"\n");
	}
	
	
	
	// https://robjhyndman.com/hyndsight/hw-initialization/
	
	public void initializeParameters() {
		
		GeneralNode node = GeneralNode.getNode(nodeID);

		
		// initialize level
		double[] solarHistory = node.getSolarHistory();
		
		for(int i=0; i<M; i++) {
			level+=solarHistory[i];
		}
		level = level/M;
		previous_level=level;
		
		//System.out.println("level " + level);
		
		// initialize trend
		double sum1=0;
		double sum2=0;
		
		for(int i=0; i<(2*M); i++) {
			if(i<M+1)
				sum2+=solarHistory[i];
			else
				sum1+=solarHistory[i];
		}
		trend = (sum1 - sum2)/(M*M);
		previous_trend=trend;
		
		//System.out.println("trend " + trend);

		
		// initialize seasonality -> additive method
		
		//for(int i=0; i<M; i++) {
		//	seasonality[i]=solarHistory[i]-level;
		//  if(seasonality[i]<0)
		//	seasonality[i]=0;
		//}
		
		
		
		// initialize seasonality -> multiplicative method
		
		for(int i=0; i<M; i++) {
			seasonality[i]=solarHistory[i]/level;
			//System.out.println("seasonality " + seasonality[i]);

		}
		
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
		return counter;
	}
	

	public double getPrevision() {
		return predicted_val;
	}

	public long getNodeId() {
		return nodeID;
	}

	public void setNodeID(long nodeID) {
		this.nodeID = nodeID;
	}
	

}
