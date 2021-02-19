package lnu.mida.protocol;

import java.util.ArrayList;
import java.util.LinkedList;

import com.lajv.location.Location;

import lnu.mida.entity.BatteryReputation;
import lnu.mida.entity.EnergyReputation;
import lnu.mida.entity.GeneralNode;
import lnu.mida.entity.GreenReputation;
import lnu.mida.entity.LocalReputation;
import lnu.mida.entity.OverallEnergyReputation;
import lnu.mida.entity.QOSReputation;
import lnu.mida.entity.Service;
import peersim.cdsim.CDProtocol;
import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.core.Cleanable;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

public class OverloadApplication implements CDProtocol, Cleanable {

	// ///////////////////////////////////////////////////////////////////////
	// Constants
	// ///////////////////////////////////////////////////////////////////////

	/**
	 * The component assambly protocol.
	 */
	private static final String COMP_PROT = "comp_prot";

	/**
	 * The strategy
	 */
	private static String STRATEGY = "";

	// ///////////////////////////////////////////////////////////////////////
	// Fields
	// ///////////////////////////////////////////////////////////////////////

	/**
	 * The component assembly protocol id.
	 */
	private final int component_assembly_pid;

	/** The learner's container */
	private ArrayList<QOSReputation> qosReputations;
	private ArrayList<EnergyReputation> energyReputations;
	private ArrayList<LocalReputation> localReputations;
	private ArrayList<OverallEnergyReputation> overallEnergyReputations;
	private ArrayList<BatteryReputation> batteryReputations;
	private ArrayList<GreenReputation> greenReputations;

	/**
	 * Initialize this object by reading configuration parameters.
	 * 
	 * @param prefix the configuration prefix for this class.
	 */
	public OverloadApplication(String prefix) {
		super();
		STRATEGY = Configuration.getString("STRATEGY", "no strat");
		component_assembly_pid = Configuration.getPid(prefix + "." + COMP_PROT);
		qosReputations = new ArrayList<QOSReputation>();
		energyReputations = new ArrayList<EnergyReputation>();
		batteryReputations = new ArrayList<BatteryReputation>();
		greenReputations = new ArrayList<GreenReputation>();
		overallEnergyReputations = new ArrayList<OverallEnergyReputation>();
	}

	public void addQoSHistoryExperience(Service service, double experienced_utility, double declared_utility) {
		int index = (int) service.getService_id();
		QOSReputation reputation = getOrCreateQOSReputation(index);
		reputation.setDeclared_utility(declared_utility);
		reputation.addExperiencedUtility(experienced_utility);
	}

	public void addEnergyHistoryExperience(GeneralNode generalNode, double nodeBalance) {
		int index = (int) generalNode.getID();
		EnergyReputation reputation = getOrCreateEnergyReputation(index);
		reputation.addDeclaredEnergy(nodeBalance);
	}
	
	public void addLocalHistoryExperience(Service service, double consumption) {
		int index = (int) service.getService_id();
		LocalReputation reputation = getOrCreateLocalReputation(index);
		reputation.addDeclaredEnergy(consumption);
	}

	public void addBatteryHistoryExperience(GeneralNode generalNode, double level) {
		int index = (int) generalNode.getID();
		BatteryReputation reputation = getOrCreateBatteryReputation(index);
		reputation.addDeclaredEnergy(level);
	}
	
	public void addGreenHistoryExperience(GeneralNode generalNode, double level) {
		int index = (int) generalNode.getID();
		GreenReputation reputation = getOrCreateGreenReputation(index);
		reputation.addDeclaredEnergy(level);
	}
	
	public void addOverallEnergyHistoryExperience(Service service, double energy) {
		int index = (int) service.getService_id();
		OverallEnergyReputation reputation = getOrCreateOverallEnergyReputation(index);
		reputation.addDeclaredEnergy(energy);
	}
	
//	public ArrayList<QOSReputation> getHistories() {
//		return qosReputations;
//	}

	/**
	 * Makes a copy of this object. Needs to be explicitly defined, since we have
	 * array members.
	 */
	@Override
	public Object clone() {
		OverloadApplication result = null;
		try {
			result = (OverloadApplication) super.clone();
		} catch (CloneNotSupportedException ex) {
			System.out.println(ex.getMessage());
			assert (false);
		}
		result.qosReputations = new ArrayList<QOSReputation>();
		result.energyReputations = new ArrayList<EnergyReputation>();
		result.localReputations = new ArrayList<LocalReputation>();
		return result;
	}

	// returns true if comp > old
	public Service chooseByStrategy(LinkedList<Service> candidates, GeneralNode node) {

		// default composition strategy (best actual value)
		if (STRATEGY.equals("greedy")) {
			return chooseByDefaultStrategy(candidates);
		}
		// random strategy
		if (STRATEGY.equals("random")) {
			return chooseByRandomStrategy(candidates);
		}
		if (STRATEGY.equals("weighted_random")) {
			return chooseByWeightedRandomStrategy(candidates);
		}
		// average strategy - not used during paper
		if (STRATEGY.equals("average")) {
			return chooseByAverageStrategy(candidates);
		}
		// future expected utility
		if (STRATEGY.equals("emergent")) {
			return chooseByFutureExpectedUtility(candidates, node);
		}
		// approach to challenge
		if (STRATEGY.equals("shaerf")) {
			return chooseByChallengeStrategy(candidates);
		}
		// individual energy
		if (STRATEGY.equals("local_energy")) {
			return chooseByLocalEnergyStrategy(candidates, node);
		}
		// overall energy
		if (STRATEGY.equals("overall_energy")) {
			return chooseByOverallEnergyStrategy(candidates);
		}
		// overall energy
		if (STRATEGY.equals("overall_learning")) {
			return chooseByOverallLearningStrategy(candidates, node);
		}
		// fair energy
		if (STRATEGY.equals("fair_energy")) {
			return chooseByFairEnergyStrategy(candidates, node);
		}
		// quality-fair energy
		if (STRATEGY.equals("quality_fair")) {
			return chooseByQualityFairEnergyStrategy(candidates, node);
		}
		
		if (STRATEGY.equals("energy_latency")) {
			return chooseByEnergyAvailabilityLatencyStrategy(candidates, node);
		}

		if (STRATEGY.equals("max_availability_latency")) {
			return chooseByMaxAvailabilityLatencyStrategy(candidates, node);
		}
		
		if (STRATEGY.equals("local_rep")) {
			return chooseByLocalRepStrategy(candidates, node);
		}
		
		if (STRATEGY.equals("maxb")) {
			return chooseByMaxBStrategy(candidates);
		}
		
		if (STRATEGY.equals("maxbl")) {
			return chooseByMaxBLearningStrategy(candidates);
		}
		
		if (STRATEGY.equals("maxbal")) {
			return chooseByMaxBalanceStrategy(candidates);
		}
		
		if (STRATEGY.equals("green_learning")) {
			return chooseByGreenLearningStrategy(candidates);
		}
		if (STRATEGY.equals("residual_life_latency")) {
			return chooseByResidualLifeLatencyStrategy(candidates,node);
		}
		
		// exception is raised if a strategy is not selected
		else {
			try {
				throw new Exception("Strategy not selected");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(0);
			return null;
		}
	}

	
	
	// returns true if comp > old
	private Service chooseByDefaultStrategy(LinkedList<Service> candidates) {

		double max = 0;
		Service res = null;
		
		// declared utility è sempre 1 ........... ?????
		for(int i=0; i<candidates.size(); i++) {
			if(candidates.get(i).getDeclaredUtility()>max) {
				max = candidates.get(i).getDeclaredUtility();
				res = candidates.get(i);
			}
		}
		
		return res;
	}
	
	
	

	// chooses a random component
	public Service chooseByRandomStrategy(LinkedList<Service> candidates) {
		
		int index = CommonState.r.nextInt(candidates.size());
		return candidates.get( index);
	}
	
	
	private Service chooseByWeightedRandomStrategy(LinkedList<Service> candidates) {
		
		if(CDState.getCycle()<7)
			return chooseByRandomStrategy(candidates);
		
		/*
		// si individuano i servizi con peso infinito
		LinkedList<Service> infinite_life_candidates = new LinkedList<Service>();
		for(int i=0; i<candidates.size(); i++) {
			if(candidates.get(i).getWeight()==Double.POSITIVE_INFINITY)
				infinite_life_candidates.add(candidates.get(i));
		}
		
		//si sceglie in modo random tra quelli con vita residua infinita
		if(infinite_life_candidates.size()>0) {
			//System.out.println("random");
			//return chooseByRandomStrategy(infinite_life_candidates);
		}
		*/		
				
		double sum=0;
		double finite_sum=0;
		
		ArrayList<Double> array = new ArrayList<Double>();
		
		for(int i=0; i<candidates.size(); i++) {

			//System.out.println(" " + candidates.get(i).getWeight());
			if(candidates.get(i).getWeight()<Double.POSITIVE_INFINITY) {
				finite_sum+=candidates.get(i).getWeight();
			}
		}	
		
		for(int i=0; i<candidates.size(); i++) {

			//System.out.println(" " + candidates.get(i).getWeight());
			if(candidates.get(i).getWeight()<Double.POSITIVE_INFINITY) {
				sum+=candidates.get(i).getWeight();
				array.add(sum);	
			}else {
				sum+=finite_sum;
				array.add(sum);	
			}
		}
		
		
		double max = 0;
		double min = sum;
		
		double random_num = (double) min + (max - min) * CommonState.r.nextDouble();

		int index=0;
		
		for(int i=0; i<array.size(); i++) {
			if(array.get(i)>random_num) {
				index = i;
				break;
			}
		}	
		
		return candidates.get(index);
	}

	
	
	
	
	// returns true if Avg(comp) > Avg(old)
	private Service chooseByAverageStrategy(LinkedList<Service> candidates) {

		if(CDState.getCycle()<7)
			return chooseByRandomStrategy(candidates);
		
		
		double max = 0;
		Service res = null;
		
		for(int i=0; i<candidates.size(); i++) {
						
			QOSReputation candReputation = getOrCreateQOSReputation((int) candidates.get(i).getService_id());

			if (candReputation.getK() == 0)
				candReputation.setQk(candidates.get(i).getCompoundUtility());
			
			//System.out.println(candReputation.getK());
			if(candReputation.getWindowAverage()>max) {
				max = candReputation.getWindowAverage();
				res = candidates.get(i);
			}
		}
		
		return res;
		
	}
	
	

	// future expected utility: two layer of reinforcement learning
	private Service chooseByFutureExpectedUtility(LinkedList<Service> candidates, GeneralNode node) {

		if(CDState.getCycle()<7)
			return chooseByRandomStrategy(candidates);
		

		Double[] probl_array = new Double[candidates.size()];

		double sigma = 0;

		
		for(int i=0; i<candidates.size(); i++) {
		
			QOSReputation quality_candidateReputation = getOrCreateQOSReputation((int) candidates.get(i).getService_id());

			double candidateTrust = quality_candidateReputation.getTk();

			double candidateFEU = candidateTrust * candidates.get(i).getDeclaredUtility()
					+ ((1.0 - candidateTrust) * quality_candidateReputation.getWindowAverage());
			
			
			ArrayList<QOSReputation> qosReputations = getQoSReputations();


			// if no experiences do the average
			if (quality_candidateReputation.getK() == 0) {

				int n = 0;
				double sum = 0;
				for (QOSReputation reputation : qosReputations) {
					if (reputation.getK() > 0) {
						double qk = reputation.getQk();
						sum += qk;
						n++;
					}
				}

				if (n == 0)
					return chooseByLocalEnergyStrategy(candidates, node);

				candidateFEU = sum / n;
			}

			
			double cand_probl = Math.pow(candidateFEU, 40);
			probl_array[i] = cand_probl;
			sigma += probl_array[i];

		}
				
		
		for(int i=0; i<probl_array.length; i++) {
			probl_array[i] = probl_array[i] / sigma;
		}
		
		
		double max=0;
		Service res = null;
		
		for(int i=0; i<probl_array.length; i++) {
			//System.out.println(probl_array[i]);
			if(probl_array[i]>max) {
				max=probl_array[i];
				res=candidates.get(i);
			}
		}

		
		return res;
		
	}
	
	
	

	// approach to challenge Shaerf
	private Service chooseByChallengeStrategy(LinkedList<Service> candidates) {

		if(CDState.getCycle()<7)
			return chooseByRandomStrategy(candidates);
		
		Double[] probl_array = new Double[candidates.size()];

		double sigma = 0;

		for(int i=0; i<candidates.size(); i++) {
			
			QOSReputation candReputation = getOrCreateQOSReputation((int) candidates.get(i).getService_id());
			
			double cand_ee = candReputation.getEe();
			ArrayList<QOSReputation> qosReputations = getQoSReputations();

			//if(candidates.get(i).getService_id()==0)
			//	System.out.println("[print in Overload App.]" + cand_ee + "    k " + candReputation.getK());
			
			// if no experiences do the average
			if (candReputation.getK() == 0) {
				int n = 0;
				int sum = 0; // to modify
				for (QOSReputation reputation : qosReputations) {
					double ee = reputation.getEe();
					if (ee != 0) {
						sum += ee; // to modify
						n++;
					}
				}
				cand_ee = sum / n;
			}
			
			//System.out.println(cand_ee);
			
			double cand_probl = Math.pow(cand_ee, 3);
			probl_array[i] = cand_probl;
			sigma += probl_array[i];
		}
		
		//System.out.println("\n");

		
		for(int i=0; i<probl_array.length; i++) {
			probl_array[i] = probl_array[i] / sigma;
		}
		
		
		double max=0;
		Service res = null;
		
		for(int i=0; i<probl_array.length; i++) {
			
			if(probl_array[i]>max) {
				max=probl_array[i];
				res=candidates.get(i);
			}
		}
		

		return res;
		

	}

	// local energy strategy
	private Service chooseByLocalEnergyStrategy(LinkedList<Service> candidates, GeneralNode node) {

		if(CDState.getCycle()<7)
			return chooseByRandomStrategy(candidates);
		
		Location thisLoc = node.getLocation();

		double min=Double.MAX_VALUE;
		Service res = null;
		
		
		for(int i=0; i<candidates.size(); i++) {
			
			GeneralNode other_node = GeneralNode.getNode(candidates.get(i).getNode_id());
			double energy = 0;

			if (node.getID() == other_node.getID()) {
				
				energy += candidates.get(i).getL_comp();

				// per il modello energetico adottato ad ECSA L_comm non dipende dal nodo in
				// ricezione (i.e., node)
				energy += candidates.get(i).getL_comm();
			} else {
				
				Location other_loc = other_node.getLocation();
				double other_latency = thisLoc.latency(other_loc);

				energy += node.getConsumedIndividualCommEnergySending(1, other_latency);
			}
			
			if(energy<min) {
				min=energy;
				res=candidates.get(i);
			}
		}
		

		return res;
		
	}
	
	

	// overall energy strategy
	private Service chooseByOverallEnergyStrategy(LinkedList<Service> candidates) {

		// at round 1 the overall energy is not known
		if(CDState.getCycle()<7)
			return chooseByRandomStrategy(candidates);
				
		double min=Double.MAX_VALUE;
		Service res = null;
		
		for(int i=0; i<candidates.size(); i++) {

			double energy = candidates.get(i).getE_comp() + candidates.get(i).getE_comm();
			
			if(energy<min) {
				min=energy;
				res=candidates.get(i);
			}	
		}
		return res;
	}

	
	
	
	
	private Service chooseByOverallLearningStrategy(LinkedList<Service> candidates, GeneralNode node) {

		if(CDState.getCycle()<7)
			return chooseByRandomStrategy(candidates);
		
		
		Double[] probl_array = new Double[candidates.size()];

		double sigma = 0;

		// tiny translation to avoid 0 as a base of the exponent	
		double translation = Math.pow(10,-5);

		for(int i=0; i<candidates.size(); i++) {
			
			//OverallEnergyReputation candReputation = getOrCreateOverallEnergyReputation((int) candidates.get(i).getService_id());
			//double cand_ee = candReputation.getEe();
			double cand_ee = candidates.get(i).getOverallRep();
			int k = candidates.get(i).getOverallRepCounter();
			
			// if no experiences do the average
			if (k == 0) {
				int n = 0;
				double sum = 0;

				
				for(int j=0; j<Network.size(); j++) {
					GeneralNode othernode = (GeneralNode) Network.get(i);
					OverloadComponentAssembly ca = (OverloadComponentAssembly) othernode.getProtocol(component_assembly_pid);
					
					ArrayList<Service> services = ca.getServices();

					for (Service service : services) {
						if (service.getOverallRepCounter() > 0) {
							double ee = service.getOverallRep();
							sum += ee;
							n++;
						}
					}
				}
				

				if (n == 0)
					return chooseByLocalEnergyStrategy(candidates, node);
					//return chooseByRandomStrategy(comp, old);

				cand_ee = sum / n;

			}
			
			cand_ee-=translation;
			
			double cand_probl = Math.pow(cand_ee, 3);
			probl_array[i] = cand_probl;
			sigma+=cand_probl;
			
		}

	
		for(int i=0; i<probl_array.length; i++) {
			probl_array[i] = probl_array[i] / sigma;
		}
		

		double max=0;
		Service res = null;
		
		for(int i=0; i<probl_array.length; i++) {
			
			if(probl_array[i]>max) {
				max=probl_array[i];
				res=candidates.get(i);
			}
		}
		
		return res;
	
	}
	
	
	
	
	
	
	
	// greedy fair energy strategy (using Shaerf) - select the node with the "best" energy balance - problemi con G - R perchè negativo
	private Service chooseByFairEnergyStrategy(LinkedList<Service> candidates, GeneralNode node) {

		if(CDState.getCycle()<7)
			return chooseByRandomStrategy(candidates);
		
		
		Double[] probl_array = new Double[candidates.size()];

		double sigma = 0;

		// tiny translation to avoid 0 as a base of the exponent	
		double translation = Math.pow(10,-5);

		for(int i=0; i<candidates.size(); i++) {
			
			EnergyReputation candReputation = getOrCreateEnergyReputation((int) candidates.get(i).getNode_id());
			double cand_ee = candReputation.getEe();

			GeneralNode nnode = GeneralNode.getNode(candidates.get(i).getNode_id());
			double cand_ee2 = nnode.getEeEnergy();
			int k = nnode.getEeCounter();

			if(candidates.get(i).getNode_id()==0) {
				System.out.println("\n        [print in Overload App.]" + cand_ee + "   k " + candReputation.getK());
				System.out.println("        [print in Overload App. ***]" + cand_ee2 + "   k " + k);
			}
			
			// if no experiences do the average
			if (candReputation.getK() == 0) {
				int n = 0;
				int sum = 0; // to modify
				for (EnergyReputation reputation : energyReputations) {
					double ee = reputation.getEe();
					if (ee != 0) {
						sum += ee; // to modify
						n++;
					}
				}
				
				if (n == 0)
					return chooseByLocalEnergyStrategy(candidates, node);

				cand_ee = sum / n;
			}

			cand_ee-=translation;

			// energy balance -> higher is better --> negative exponent
			double cand_probl = Math.pow(cand_ee, -3);
			probl_array[i] = cand_probl;
			sigma+=cand_probl;
			
		}
	
		for(int i=0; i<probl_array.length; i++) {
			probl_array[i] = probl_array[i] / sigma;
		}
		

		double max=0;
		Service res = null;
		
		for(int i=0; i<probl_array.length; i++) {
			if(probl_array[i]>max) {
				max=probl_array[i];
				res=candidates.get(i);
			}
		}
		return res;
	
	}
	
	

	// Balance quality and energy
	private Service chooseByQualityFairEnergyStrategy(LinkedList<Service> candidates, GeneralNode node) {

		if(CDState.getCycle()<7)
			return chooseByRandomStrategy(candidates);
		
		
		//
		// ENERGY PART
		//

		Double[] eprobl_array = new Double[candidates.size()];

		double sigma = 0;

		
		for(int i=0; i<candidates.size(); i++) {
			
			EnergyReputation energy_candReputation = getOrCreateEnergyReputation((int) candidates.get(i).getNode_id());
			
			if (energy_candReputation.getK() == 0)
				return chooseByRandomStrategy(candidates);
			
			double energy_cand_ee = energy_candReputation.getEe();
			
			ArrayList<QOSReputation> qosReputations = getQoSReputations();

			
			// if no experiences do the average
			if (energy_candReputation.getK() == 0) {
				int n = 0;
				int sum = 0; // to modify
				for (EnergyReputation reputation : energyReputations) {
					double ee = reputation.getEe();
					if (ee != 0) {
						sum += ee; // to modify
						n++;
					}
				}
				
				if (n == 0)
					return chooseByLocalEnergyStrategy(candidates, node);

				energy_cand_ee = sum / n;
			}
			
			// tiny translation to avoid 0 as a base of the exponent	
			double translation = Math.pow(10,-5);
			
			energy_cand_ee-=translation;
			
			double cand_probl = Math.pow(energy_cand_ee, -5);
			eprobl_array[i] = cand_probl;
			sigma += eprobl_array[i];

		}

		
		for(int i=0; i<eprobl_array.length; i++) {
			eprobl_array[i] = eprobl_array[i] / sigma;
		}
		
		

		

		//
		// QUALITY PART
		//

		Double[] qprobl_array = new Double[candidates.size()];
		double trust;
		double FEU;

		sigma = 0;

		for(int i=0; i<candidates.size(); i++) {
			
			QOSReputation quality_candReputation = getOrCreateQOSReputation((int) candidates.get(i).getService_id());

			trust = quality_candReputation.getTk();
			FEU= trust * candidates.get(i).getDeclaredUtility()+ ((1.0 - trust) * quality_candReputation.getWindowAverage());
		
			
			// if no experiences do the average
			if (quality_candReputation.getK() == 0) {
				int n = 0;
				double sum = 0;
				for (QOSReputation reputation : qosReputations) {
					if (reputation.getK() > 0) {
						double qk = reputation.getQk();
						sum += qk;
						n++;
					}
				}

				if (n == 0)
					//return chooseByRandomStrategy(comp, old);
					return chooseByLocalEnergyStrategy(candidates, node);

				FEU= sum / n;
			}
			
			qprobl_array[i] = Math.pow(FEU, 20);
			sigma += qprobl_array[i];

		}


		
		for(int i=0; i<qprobl_array.length; i++) {
			qprobl_array[i] = qprobl_array[i] / sigma;
		}
		
		

		//
		// SAW on probabilities
		//

		
		double w_q = 0.6;
		double w_e = 0.4;

		Double[] saw_probl_array = new Double[candidates.size()];

		sigma =0; 

		for(int i=0; i<candidates.size(); i++) {
			saw_probl_array[i] = Math.pow(w_e * eprobl_array[i] + w_q * qprobl_array[i], 20);
			sigma += saw_probl_array[i];
		}

	
		for(int i=0; i<saw_probl_array.length; i++) {
			saw_probl_array[i] = saw_probl_array[i] / sigma;
		}


		
		// random pesata sulle probabilità

		
		double sum=0;
		
		ArrayList<Double> array = new ArrayList<Double>();
		
		for(int i=0; i<saw_probl_array.length; i++) {

			sum+=saw_probl_array[i];
			array.add(sum);
		}	
		
		
		double max = 0;
		double min = sum;
		
		double random_num = (double) min + (max - min) * CommonState.r.nextDouble();

		int index=0;
		
		for(int i=0; i<array.size(); i++) {
			if(array.get(i)>random_num) {
				index = i;
				break;
			}
		}
		
		
		return candidates.get(index);
		

	}
	
	
	/*
	 si individuano tra i candidati i k servizi “migliori”, ovvero i k servizi allocati 
	 sui nodi con energia disponibile maggiore. Tra questi si seleziona il servizio 
	 allocato sul nodo più vicino (in termini di latenza di comunicazione) al nodo del 
	 servizio richiedente.
	 */
	
	private Service chooseByEnergyAvailabilityLatencyStrategy(LinkedList<Service> candidates, GeneralNode node) {
		
		if(CDState.getCycle()<7)
			return chooseByRandomStrategy(candidates);
		
		
		double mean_energy=0;
		for(int i=0; i<candidates.size(); i++) {
			GeneralNode n = GeneralNode.getNode(candidates.get(i).getNode_id());
			mean_energy += n.getG()+n.getBattery();
		}
		mean_energy=mean_energy/candidates.size();
		
		
		LinkedList<Service> best_candidates = new LinkedList<Service>();
		for(int i=0; i<candidates.size(); i++) {
			GeneralNode n = GeneralNode.getNode(candidates.get(i).getNode_id());
			double energy = n.getG()+n.getBattery();
			if(energy>mean_energy)
				best_candidates.add(candidates.get(i));
		}
		
		
		double min=Double.MAX_VALUE;
		Service res = null;

		for(int i=0; i<best_candidates.size(); i++) {
			
			GeneralNode receiverNode = GeneralNode.getNode(candidates.get(i).getNode_id());

			Location senderLoc = node.getLocation();
			Location receiverLoc = receiverNode.getLocation();
			double latency = senderLoc.latency(receiverLoc);
			
			if(latency<min) {
				min=latency;
				res=best_candidates.get(i);
			}
			
		}

		if(res==null)
			return chooseByRandomStrategy(candidates);
		
		return res;
	
	}
	

	
private Service chooseByMaxAvailabilityLatencyStrategy(LinkedList<Service> candidates, GeneralNode node) {
		
		if(CDState.getCycle()<7)
			return chooseByRandomStrategy(candidates);
		
		
		double mean_availability=0;
		for(int i=0; i<candidates.size(); i++) {
			GeneralNode n = GeneralNode.getNode(candidates.get(i).getNode_id());
			mean_availability += n.getAvailability();
		}
		mean_availability=mean_availability/candidates.size();
		
		
		LinkedList<Service> best_candidates = new LinkedList<Service>();
		for(int i=0; i<candidates.size(); i++) {
			GeneralNode n = GeneralNode.getNode(candidates.get(i).getNode_id());
			double availability = n.getAvailability();
			if(availability>mean_availability)
				best_candidates.add(candidates.get(i));
		}
		
		
		double min=Double.MAX_VALUE;
		Service res = null;

		for(int i=0; i<best_candidates.size(); i++) {
			
			GeneralNode receiverNode = GeneralNode.getNode(candidates.get(i).getNode_id());

			Location senderLoc = node.getLocation();
			Location receiverLoc = receiverNode.getLocation();
			double latency = senderLoc.latency(receiverLoc);
			
			if(latency<min) {
				min=latency;
				res=best_candidates.get(i);
			}
			
		}

		if(res==null)
			return chooseByRandomStrategy(candidates);
		
		return res;
	
	}







	//approach to challenge Shaerf
	private Service chooseByLocalRepStrategy(LinkedList<Service> candidates, GeneralNode node) {

		if(CDState.getCycle()<7)
			return chooseByRandomStrategy(candidates);
		
		Double[] probl_array = new Double[candidates.size()];

		double sigma = 0;

		for(int i=0; i<candidates.size(); i++) {
						
			double cand_ee = candidates.get(i).getLocalRep();

			
			
			//System.out.println("servizio " + candidates.get(i).getService_id() + "  ee  =" + cand_ee + "    k = " + candReputation.getK());
			 
			
			int k = candidates.get(i).getLocalRepCounter();
			// if no experiences do the average
			if (k == 0) {
				int n = 0;
				double sum = 0;

				
				for(int j=0; j<Network.size(); j++) {
					GeneralNode othernode = (GeneralNode) Network.get(i);
					OverloadComponentAssembly ca = (OverloadComponentAssembly) othernode.getProtocol(component_assembly_pid);
					
					ArrayList<Service> services = ca.getServices();

					for (Service service : services) {
						if (service.getLocalRepCounter() > 0) {
							double ee = service.getLocalRep();
							sum += ee;
							n++;
						}
					}
				}
				

				if (n == 0)
					return chooseByLocalEnergyStrategy(candidates, node);
					//return chooseByRandomStrategy(comp, old);

				cand_ee = sum / n;

			}
			
			//System.out.println(cand_ee);
			
			double cand_probl = Math.pow(cand_ee, 3);
			probl_array[i] = cand_probl;
			sigma += probl_array[i];
		}
		
		//System.out.println("\n");
		if (sigma == 0)
			return chooseByLocalEnergyStrategy(candidates, node);

		
		for(int i=0; i<probl_array.length; i++) {
			probl_array[i] = probl_array[i] / sigma;
		}
		
		
		double max=0;
		Service res = null;
		
		for(int i=0; i<probl_array.length; i++) {
			//System.out.println(probl_array[i]);

			if(probl_array[i]>max) {

				max=probl_array[i];
				res=candidates.get(i);
			}
		}
		//System.out.println(res.getService_id());


		return res;
	}
	
	
	
	private Service chooseByMaxBStrategy(LinkedList<Service> candidates) {
		
		double max=0;
		Service res = null;
		
		for(int i=0; i<candidates.size(); i++) {
			
			GeneralNode depNode = GeneralNode.getNode(candidates.get(i).getNode_id());

			if(depNode.getBattery()>max) {
				max=depNode.getBattery();
				res=candidates.get(i);
			}
		}
		
		return res;
	}
	

	
	private Service chooseByMaxBalanceStrategy(LinkedList<Service> candidates) {
		
		double max=0;
		Service res = null;
		
		for(int i=0; i<candidates.size(); i++) {
			
			GeneralNode depNode = GeneralNode.getNode(candidates.get(i).getNode_id());
			double balance=depNode.getG()-depNode.getR();
					
			if(balance>max) {
				max=balance;
				res=candidates.get(i);
			}
		}
		
		return res;
	}


	
	private Service chooseByMaxBLearningStrategy(LinkedList<Service> candidates) {
		
		if(CDState.getCycle()<7)
			return chooseByRandomStrategy(candidates);
		
		Double[] probl_array = new Double[candidates.size()];

		double sigma = 0;

		for(int i=0; i<candidates.size(); i++) {
			
			BatteryReputation candReputation = getOrCreateBatteryReputation((int) candidates.get(i).getNode_id());
			
			double cand_ee = candReputation.getEe();

			/*
			if(candidates.get(i).getNode_id()==0)
				System.out.println("ee = " + cand_ee + "   k = " + candReputation.getK());
			*/
			
			/*
			if(candidates.get(i).getService_id()==0)
				System.out.println("servizio " + candidates.get(i).getService_id() + "  ee  =" + cand_ee + "    k = " + candReputation.getK());
			 */
			// if no experiences do the average
			if (candReputation.getK() == 0) {
				int n = 0;
				int sum = 0; // to modify
				for (BatteryReputation reputation : batteryReputations) {
					double ee = reputation.getEe();
					if (ee != 0) {
						sum += ee; // to modify
						n++;
					}
				}
				cand_ee = sum / n;
			}
			
			//System.out.println(cand_ee);
			
			double cand_probl = Math.pow(cand_ee, 3);
			probl_array[i] = cand_probl;
			sigma += probl_array[i];
		}
		
		//System.out.println("\n");

		
		for(int i=0; i<probl_array.length; i++) {
			probl_array[i] = probl_array[i] / sigma;
		}
		
		
		double max=0;
		Service res = null;
		
		for(int i=0; i<probl_array.length; i++) {
			if(probl_array[i]>max) {
				max=probl_array[i];
				res=candidates.get(i);
			}
		}
		

		return res;
	}
	
	
	
	
	
	private Service chooseByGreenLearningStrategy(LinkedList<Service> candidates) {
		
		if(CDState.getCycle()<287)
			return chooseByRandomStrategy(candidates);
		
		Double[] probl_array = new Double[candidates.size()];

		double sigma = 0;

		
		for(int i=0; i<candidates.size(); i++) {
			
			GeneralNode node = GeneralNode.getNode(candidates.get(i).getNode_id());

			//GreenReputation candReputation = getOrCreateGreenReputation((int) candidates.get(i).getNode_id());
			
			//double prevision = candReputation.getPrevision();
			double prevision = node.getPredictedG();

			//System.out.println("nodo " + candidates.get(i).getNode_id() + "      prevision : " + prevision);

			
			//System.out.println(prevision);

			//if(candidates.get(i).getNode_id()==0)
			//	System.out.println("prevision = " + prevision + "   k = " + candReputation.getK());
			
			
			/*
			if(candidates.get(i).getService_id()==0)
				System.out.println("servizio " + candidates.get(i).getService_id() + "  ee  =" + cand_ee + "    k = " + candReputation.getK());
			 */

			
			//System.out.println(cand_ee);
			
			double cand_probl = Math.pow(prevision, 3);
			probl_array[i] = cand_probl;
			sigma += probl_array[i];
		}
		
		//System.out.println("\n");

		//System.out.println(" sigma " + sigma);

		int counter=0;
		for(int i=0; i<probl_array.length; i++) {
			//System.out.println("probl_array[i] " +  probl_array[i] + "   sigma " + sigma + "    res = " + probl_array[i] / sigma);
			if(probl_array[i]==0)
				counter++;
			probl_array[i] = probl_array[i] / sigma;

		}
		
		if(counter==candidates.size())
			return chooseByRandomStrategy(candidates);

		
		double max=0;
		Service res = null;
		
		for(int i=0; i<probl_array.length; i++) {
			if(probl_array[i]>max) {
				max=probl_array[i];
				res=candidates.get(i);
			}
		}		
		
		//System.exit(0);

		return res;
	}
	
	
	
	
	
	
	
	
	
	
	/*
	 si individuano tra i candidati i k servizi “migliori”, ovvero i k servizi allocati 
	 sui nodi con vita residua maggiore. Tra questi si seleziona il servizio 
	 allocato sul nodo più vicino (in termini di latenza di comunicazione) al nodo del 
	 servizio richiedente.
	 */
	
	private Service chooseByResidualLifeLatencyStrategy(LinkedList<Service> candidates, GeneralNode node) {
		
		if(CDState.getCycle()<7)
			return chooseByRandomStrategy(candidates);
		
		
		ArrayList<Service> infinite_life_services = new ArrayList<Service>();
		Service res = null;

		
		for(int i=0; i<candidates.size(); i++) {
			GeneralNode n = GeneralNode.getNode(candidates.get(i).getNode_id());
			if(n.getResidualLife()==Double.POSITIVE_INFINITY)
				infinite_life_services.add(candidates.get(i));
		}
		
		if(infinite_life_services.size()==0) {
			double mean_energy=0;
			for(int i=0; i<candidates.size(); i++) {
				GeneralNode n = GeneralNode.getNode(candidates.get(i).getNode_id());
				mean_energy += n.getG()+n.getBattery();
			}
			mean_energy=mean_energy/candidates.size();
			
			
			LinkedList<Service> best_candidates = new LinkedList<Service>();
			for(int i=0; i<candidates.size(); i++) {
				GeneralNode n = GeneralNode.getNode(candidates.get(i).getNode_id());
				double energy = n.getG()+n.getBattery();
				if(energy>mean_energy)
					best_candidates.add(candidates.get(i));
			}
			
			
			double min=Double.MAX_VALUE;

			for(int i=0; i<best_candidates.size(); i++) {
				
				GeneralNode receiverNode = GeneralNode.getNode(candidates.get(i).getNode_id());

				Location senderLoc = node.getLocation();
				Location receiverLoc = receiverNode.getLocation();
				double latency = senderLoc.latency(receiverLoc);
				
				if(latency<min) {
					min=latency;
					res=best_candidates.get(i);
				}
				
			}
		}else {
			double min=Double.MAX_VALUE;

			for(int i=0; i<infinite_life_services.size(); i++) {
				
				GeneralNode receiverNode = GeneralNode.getNode(candidates.get(i).getNode_id());

				Location senderLoc = node.getLocation();
				Location receiverLoc = receiverNode.getLocation();
				double latency = senderLoc.latency(receiverLoc);
				
				if(latency<min) {
					min=latency;
					res=infinite_life_services.get(i);
				}
				
			}
		}
		
		

		if(res==null)
			return chooseByRandomStrategy(candidates);
		
		return res;
	
	}
	
	
	
	
	
	
	

	@Override
	public void onKill() {
		// TODO Auto-generated method stub
	}

	@Override
	public void nextCycle(Node node, int protocolID) {
	}

	private QOSReputation getOrCreateQOSReputation(int serviceId) {
		for (QOSReputation reputation : qosReputations) {
			if (reputation.getServiceID() == serviceId) {
				return reputation;
			}
		}
		QOSReputation newReputation = new QOSReputation(serviceId);
		qosReputations.add(newReputation);
		return newReputation;
	}

	private EnergyReputation getOrCreateEnergyReputation(int nodeID) {
		for (EnergyReputation reputation : energyReputations) {
			if (reputation.getNodeId() == nodeID) {
				return reputation;
			}
		}
		EnergyReputation newReputation = new EnergyReputation(nodeID);
		energyReputations.add(newReputation);
		return newReputation;
	}

	public LocalReputation getOrCreateLocalReputation(int serviceId) {
		for (LocalReputation reputation : localReputations) {
			if (reputation.getServiceId() == serviceId) {
				return reputation;
			}
		}
		LocalReputation newReputation = new LocalReputation(serviceId);
		localReputations.add(newReputation);
		return newReputation;
	}
	
	
	private BatteryReputation getOrCreateBatteryReputation(int nodeID) {
		for (BatteryReputation reputation : batteryReputations) {
			if (reputation.getNodeId() == nodeID) {
				return reputation;
			}
		}
		BatteryReputation newReputation = new BatteryReputation(nodeID);
		batteryReputations.add(newReputation);
		return newReputation;
	}
	
	
	public GreenReputation getOrCreateGreenReputation(int nodeID) {
		for (GreenReputation reputation : greenReputations) {
			if (reputation.getNodeId() == nodeID) {
				return reputation;
			}
		}
		GreenReputation newReputation = new GreenReputation(nodeID);
		greenReputations.add(newReputation);
		return newReputation;
	}
	
	
	public OverallEnergyReputation getOrCreateOverallEnergyReputation(int serviceId) {
		for (OverallEnergyReputation reputation : overallEnergyReputations) {
			if (reputation.getServiceId() == serviceId) {
				return reputation;
			}
		}
		OverallEnergyReputation newReputation = new OverallEnergyReputation(serviceId);
		overallEnergyReputations.add(newReputation);
		return newReputation;
	}
	
	
	/*
	private QOSReputation getOrCreateQOSReputation(int serviceId) {
		
		//System.out.println(qosReputations.size());
		
		return qosReputations.get(serviceId);
	}

	private EnergyReputation getOrCreateEnergyReputation(int nodeID) {
		return energyReputations.get(nodeID);
	}
*/
	public ArrayList<QOSReputation> getQoSReputations() {
		return qosReputations;
	}

	public ArrayList<EnergyReputation> getEnergyReputations() {
		return energyReputations;
	}
	
	public ArrayList<LocalReputation> getLocalReputations() {
		return localReputations;
	}

	public ArrayList<BatteryReputation> getBatteryReputations() {
		return batteryReputations;
	}
	
	public ArrayList<GreenReputation> getGreenReputations() {
		return greenReputations;
	}
	
	public ArrayList<OverallEnergyReputation> getOverallEnergyReputations() {
		return overallEnergyReputations;
	}
	
	public void reset() {
		qosReputations = new ArrayList<QOSReputation>();
		energyReputations = new ArrayList<EnergyReputation>();
		localReputations = new ArrayList<LocalReputation>();
		batteryReputations = new ArrayList<BatteryReputation>();
		greenReputations = new ArrayList<GreenReputation>();
		overallEnergyReputations = new ArrayList<OverallEnergyReputation>();

	}

}
