package lnu.mida.protocol;

import java.util.ArrayList;

import com.lajv.location.Location;

import lnu.mida.entity.EnergyReputation;
import lnu.mida.entity.GeneralNode;
import lnu.mida.entity.QOSReputation;
import lnu.mida.entity.Service;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Cleanable;
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
	}

	public void addQoSHistoryExperience(Service service, double experienced_utility, double declared_utility) {
		int index = (int) service.getService_id();
		QOSReputation reputation = getOrCreateQOSReputation(index);
		reputation.setDeclared_utility(declared_utility);
		reputation.addExperiencedUtility(experienced_utility);
	}

	public void addEnergyHistoryExperience(Service service, double declared_energy) {
		int index = (int) service.getService_id();
		EnergyReputation reputation = getOrCreateEnergyReputation(index);
		reputation.addDeclaredEnergy(declared_energy);
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
		return result;
	}

	// returns true if comp > old
	public boolean chooseByStrategy(Service comp, Service old, GeneralNode node) {

		// default composition strategy (best actual value)
		if (STRATEGY.equals("greedy")) {
			return chooseByDefaultStrategy(comp, old);
		}
		// random strategy
		if (STRATEGY.equals("random")) {
			return chooseByRandomStrategy(comp, old);
		}
		// average strategy - not used during paper
		if (STRATEGY.equals("average")) {
			return chooseByAverageStrategy(comp, old);
		}
		// future expected utility
		if (STRATEGY.equals("emergent")) {
			return chooseByFutureExpectedUtility(comp, old, node);
		}
		// approach to challenge
		if (STRATEGY.equals("shaerf")) {
			return chooseByChallengeStrategy(comp, old);
		}
		// individual energy
		if (STRATEGY.equals("local_energy")) {
			return chooseByLocalEnergyStrategy(comp, old, node);
		}
		// overall energy
		if (STRATEGY.equals("overall_energy")) {
			return chooseByOverallEnergyStrategy(comp, old);
		}
		// fair energy
		if (STRATEGY.equals("fair_energy")) {
			return chooseByFairEnergyStrategy(comp, old, node);
		}
		// quality-fair energy
		if (STRATEGY.equals("quality_fair")) {
			return chooseByQualityFairEnergyStrategy(comp, old, node);
		}

		// exception is raised if a strategy is not selected
		else {
			try {
				throw new Exception("Strategy not selected");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(0);
			return false;
		}
	}

	// returns true if comp > old
	private boolean chooseByDefaultStrategy(Service comp, Service old) {

		if (comp.getDeclaredUtility() >= old.getDeclaredUtility())
			return true;
		else
			return false;

	}

	// chooses a random component
	private boolean chooseByRandomStrategy(Service comp, Service old) {
		if (Math.random() < 0.5)
			return true;
		else
			return false;
	}

	// returns true if Avg(comp) > Avg(old)
	private boolean chooseByAverageStrategy(Service comp, Service old) {

		QOSReputation compReputation = getOrCreateQOSReputation((int) comp.getService_id());
		QOSReputation oldReputation = getOrCreateQOSReputation((int) old.getService_id());

		if (compReputation.getK() == 0)
			compReputation.setQk(comp.getCompoundUtility());

		if (oldReputation.getK() == 0)
			oldReputation.setQk(comp.getCompoundUtility());

		if (compReputation.getWindowAverage() > oldReputation.getWindowAverage())
			return true;

		else
			return false;
	}

	// future expected utility: two layer of reinforcement learning
	private boolean chooseByFutureExpectedUtility(Service comp, Service old, GeneralNode node) {

		QOSReputation quality_compReputation = getOrCreateQOSReputation((int) comp.getService_id());
		QOSReputation quality_oldReputation = getOrCreateQOSReputation((int) old.getService_id());

		double compTrust = quality_compReputation.getTk();
		double oldTrust = quality_oldReputation.getTk();

		double compFEU = compTrust * comp.getDeclaredUtility()
				+ ((1.0 - compTrust) * quality_compReputation.getWindowAverage());
		double oldFEU = oldTrust * old.getDeclaredUtility()
				+ ((1.0 - oldTrust) * quality_oldReputation.getWindowAverage());

		// if no experiences do the average
		if (quality_compReputation.getK() == 0) {
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
				return chooseByRandomStrategy(comp, old);

			compFEU = sum / n;
		}

		if (quality_oldReputation.getK() == 0) {
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
				return chooseByLocalEnergyStrategy(comp, old, node);

			oldFEU = sum / n;
		}
		
		double comp_probl1 = Math.pow(compFEU, 40);
		double old_probl1 = Math.pow(oldFEU, 40);

		double sigma = comp_probl1 + old_probl1;

		double quality_comp_probl = comp_probl1/sigma;
		double quality_old_probl = old_probl1/sigma;
		
		if(quality_comp_probl==quality_old_probl)
			return chooseByLocalEnergyStrategy(comp, old, node);
		
		else if(quality_comp_probl>quality_old_probl)
			return true;
		else return false;
	}

	// approach to challenge Shaerf
	private boolean chooseByChallengeStrategy(Service comp, Service old) {

		QOSReputation compReputation = getOrCreateQOSReputation((int) comp.getService_id());
		QOSReputation oldReputation = getOrCreateQOSReputation((int) old.getService_id());

		if (compReputation.getK() == 0 || oldReputation.getK() == 0)
			return chooseByRandomStrategy(comp, old);

		double comp_ee = compReputation.getEe();
		double old_ee = oldReputation.getEe();

		// if no experiences do the average
		if (compReputation.getK() == 0) {
			int n = 0;
			int sum = 0; // to modify
			for (QOSReputation reputation : qosReputations) {
				double ee = reputation.getEe();
				if (ee != 0) {
					sum += ee; // to modify
					n++;
				}
			}
			comp_ee = sum / n;
		}

		if (oldReputation.getK() == 0) {
			int n = 0;
			int sum = 0;
			for (QOSReputation reputation : qosReputations) {
				double ee = reputation.getEe();
				if (ee != 0) {
					sum += ee;
					n++;
				}
			}
			old_ee = sum / n;
		}

		double comp_probl1 = Math.pow(comp_ee, 3);
		double old_probl1 = Math.pow(old_ee, 3);

		double sigma = comp_probl1 + old_probl1;

		double comp_probl = comp_probl1 / sigma;
		double old_probl = old_probl1 / sigma;

		if (old_probl < comp_probl)
			return true;
		else
			return false;
	}

	// local energy strategy
	private boolean chooseByLocalEnergyStrategy(Service comp, Service old, GeneralNode node) {

		Location thisLoc = node.getLocation();

		// calc. for old
		GeneralNode nodeOld = GeneralNode.getNode(old.getNode_id());

		double energyOld = 0;

		if (node.getID() == nodeOld.getID()) {
			energyOld += comp.getL_comp();

			// per il modello energetico adottato ad ECSA L_comm non dipende dal nodo in
			// ricezione (i.e., node)
			energyOld += comp.getL_comm();
		} else {
			Location oldLoc = nodeOld.getLocation();
			double oldLatency = thisLoc.latency(oldLoc);

			energyOld += node.getConsumedIndividualCommEnergySending(1, oldLatency);
		}

		// calc. for comp
		GeneralNode nodeComp = GeneralNode.getNode(comp.getNode_id());

		double energyComp = 0;

		if (node.getID() == nodeComp.getID()) {
			energyComp += comp.getL_comp();

			// per il modello energetico adottato ad ECSA L_comm non dipende dal nodo in
			// ricezione (i.e., node)
			energyComp += comp.getL_comm();
		} else {
			Location compLoc = nodeComp.getLocation();
			double newLatency = thisLoc.latency(compLoc);

			energyComp += node.getConsumedIndividualCommEnergySending(1, newLatency);
		}

		// Choose the service causing the least local energy consumption for the node
		if (energyComp < energyOld)
			return true;
		else
			return false;
	}

	// overall energy strategy
	private boolean chooseByOverallEnergyStrategy(Service comp, Service old) {

		// at round 1 the overal energy is not known

		double energyComp = comp.getE_comp() + comp.getE_comm();
		double energyOld = old.getE_comp() + old.getE_comm();

		if (energyComp == energyOld)
			chooseByRandomStrategy(comp, old);

		if (energyComp < energyOld)
			return true;
		else
			return false;
	}

	// greedy fair energy strategy using Shaerf
	private boolean chooseByFairEnergyStrategy(Service comp, Service old, GeneralNode node) {

		EnergyReputation compReputation = getOrCreateEnergyReputation((int) comp.getService_id());
		EnergyReputation oldReputation = getOrCreateEnergyReputation((int) old.getService_id());

		double comp_ee = compReputation.getEe();
		double old_ee = oldReputation.getEe();

		// if no experiences do the average
		if (compReputation.getK() == 0) {
			int n = 0;
			double sum = 0;
			for (EnergyReputation reputation : energyReputations) {
				if (reputation.getK() > 0) {
					double ee = reputation.getEe();
					sum += ee;
					n++;
				}
			}

			if (n == 0)
		//		return chooseByLocalEnergyStrategy(comp, old, node);
				return chooseByRandomStrategy(comp, old);


			comp_ee = sum / n;
		}

		if (oldReputation.getK() == 0) {
			int n = 0;
			double sum = 0;
			for (EnergyReputation reputation : energyReputations) {
				if (reputation.getK() > 0) {
					double ee = reputation.getEe();
					sum += ee;
					n++;
				}
			}

			if (n == 0)
			//	return chooseByLocalEnergyStrategy(comp, old, node);

			old_ee = sum / n;

		}

		if (comp_ee == old_ee) {
		//	return chooseByLocalEnergyStrategy(comp, old, node);
			return chooseByRandomStrategy(comp, old);
		}

		// lower is better --> negative exponent
		double comp_probl1 = Math.pow(comp_ee, -60);
		double old_probl1 = Math.pow(old_ee, -60);

		double sigma = comp_probl1 + old_probl1;

		double comp_probl = comp_probl1 / sigma;
		double old_probl = old_probl1 / sigma;

		double random = Math.random();

		if (comp_probl > random)
			return true;
		else
			return false;
	}

	// Balance quality and energy
	private boolean chooseByQualityFairEnergyStrategy(Service comp, Service old, GeneralNode node) {


		//
		// ENERGY PART
		//

		EnergyReputation energy_compReputation = getOrCreateEnergyReputation((int) comp.getService_id());
		EnergyReputation energy_oldReputation = getOrCreateEnergyReputation((int) old.getService_id());

		double energy_comp_ee = energy_compReputation.getEe();
		double energy_old_ee = energy_oldReputation.getEe();

		// if no experiences do the average
		if (energy_compReputation.getK() == 0) {

			int n = 0;
			double sum = 0;

			for (EnergyReputation reputation : energyReputations) {
				if (reputation.getK() > 0) {
					double ee = reputation.getEe();
					sum += ee;
					n++;
				}
			}

			if (n == 0)
				return chooseByLocalEnergyStrategy(comp, old, node);

			energy_comp_ee = sum / n;
		}

		if (energy_oldReputation.getK() == 0) {
			int n = 0;
			double sum = 0;
			for (EnergyReputation reputation : energyReputations) {
				if (reputation.getK() > 0) {
					double ee = reputation.getEe();
					sum += ee;
					n++;
				}
			}

			if (n == 0)
				return chooseByRandomStrategy(comp, old);

			energy_old_ee = sum / n;
		}

		double comp_probl1 = Math.pow(energy_comp_ee, -20);
		double old_probl1 = Math.pow(energy_old_ee, -20);

		double sigma = comp_probl1 + old_probl1;

		double energy_comp_probl = comp_probl1 / sigma;
		double energy_old_probl = old_probl1 / sigma;
		
		
		//
		// QUALITY PART
		//

		QOSReputation quality_compReputation = getOrCreateQOSReputation((int) comp.getService_id());
		QOSReputation quality_oldReputation = getOrCreateQOSReputation((int) old.getService_id());

		double compTrust = quality_compReputation.getTk();
		double oldTrust = quality_oldReputation.getTk();

		double compFEU = compTrust * comp.getDeclaredUtility()
				+ ((1.0 - compTrust) * quality_compReputation.getWindowAverage());
		double oldFEU = oldTrust * old.getDeclaredUtility()
				+ ((1.0 - oldTrust) * quality_oldReputation.getWindowAverage());

		// if no experiences do the average
		if (quality_compReputation.getK() == 0) {
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
				return chooseByLocalEnergyStrategy(comp, old, node);

			compFEU = sum / n;
		}

		if (quality_oldReputation.getK() == 0) {
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
				return chooseByLocalEnergyStrategy(comp, old, node);

			oldFEU = sum / n;
		}
		
		comp_probl1 = Math.pow(compFEU, 40);
		old_probl1 = Math.pow(oldFEU, 40);

		sigma = comp_probl1 + old_probl1;

		double quality_comp_probl = comp_probl1/sigma;
		double quality_old_probl = old_probl1/sigma;

		//
		// SAW on probabilities
		//

		double w_e = 0.5;
		double w_q = 0.5;

		double saw_comp_probl1 = Math.pow(w_e * energy_comp_probl + w_q * quality_comp_probl, 20);
		double saw_old_probl1 = Math.pow(w_e * energy_old_probl + w_q * quality_old_probl, 20);
		

		sigma = saw_comp_probl1 + saw_old_probl1;

		double saw_comp_probl = saw_comp_probl1 / sigma;
		double saw_old_probl = saw_old_probl1 / sigma;


		if (saw_comp_probl == saw_old_probl) {
			return chooseByLocalEnergyStrategy(comp, old,node);
		}

		double random = Math.random();

		if (saw_comp_probl > random)
			return true;
		else
			return false;
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

	private EnergyReputation getOrCreateEnergyReputation(int serviceId) {
		for (EnergyReputation reputation : energyReputations) {
			if (reputation.getServiceID() == serviceId) {
				return reputation;
			}
		}
		EnergyReputation newReputation = new EnergyReputation(serviceId);
		energyReputations.add(newReputation);
		return newReputation;
	}

	private EnergyReputation getEnergyReputation(int serviceId) {
		for (EnergyReputation reputation : energyReputations) {
			if (reputation.getServiceID() == serviceId) {
				return reputation;
			}
		}
		return null;
	}

	public ArrayList<QOSReputation> getQoSReputations() {
		return qosReputations;
	}

	public void reset() {
		qosReputations = new ArrayList<QOSReputation>();
		energyReputations = new ArrayList<EnergyReputation>();
	}

}
