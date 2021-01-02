package lnu.mida.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import peersim.config.Configuration;
import peersim.core.CommonState;

public class StrategyHandler {

	/**
	 * The strategy
	 */
	private static String STRATEGY = "";
	
	public StrategyHandler() {
		STRATEGY = Configuration.getString("STRATEGY", "no strat");
	}
	
	
	public Service chooseByStrategy(LinkedList<Service> candidates, GeneralNode node) {
		// random strategy
		if (STRATEGY.equals("random")) {
			return chooseByRandomStrategy(candidates);
		}
		// choose by residual life 
		if (STRATEGY.equals("residual_life")) {
			return chooseByResidualLifeStrategy(candidates);
		}
		// choose by residual life 
		if (STRATEGY.equals("rev_residual_life")) {
			return chooseByReverseResidualLifeStrategy(candidates);
		}
		
		if (STRATEGY.equals("latency_set")) {
			return chooseByLatencySetStrategy(candidates, node);
		}
						
		if (STRATEGY.equals("random_select1")) {
			return chooseByRandomSelect1Strategy(candidates);
		}
		
		if (STRATEGY.equals("random_select2")) {
			return chooseByRandomSelect2Strategy(candidates);
		}
		
		if (STRATEGY.equals("link_num")) {
			return chooseByLinkNumStrategy(candidates);
		}
		
		if (STRATEGY.equals("evolutionary_game")) {
			return chooseByEvolutionaryGameStrategy(candidates, node);
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
	
	
	// chooses a random component in a set of candidates
	private Service chooseByRandomStrategy(LinkedList<Service> candidates) {
		
		int index = CommonState.r.nextInt(candidates.size());
		//System.out.println("	index = " + index + "   servizio scelto : " + candidates.get(index).getService_id());
		return candidates.get( index);
	}
	
	
	private Service chooseByResidualLifeStrategy(LinkedList<Service> candidates) {
		double max = 0;
		Service res = null;
		for(int i=0; i<candidates.size(); i++) {
			GeneralNode n = GeneralNode.getNode(candidates.get(i).getNode_id());
			if(n.getResidualLife()>max) {
				max = n.getResidualLife();
				res = candidates.get(i);
			}
		}
		
		if(res==null)
			return chooseByRandomStrategy(candidates);
		
		return res;
	}
	
	
	private Service chooseByReverseResidualLifeStrategy(LinkedList<Service> candidates) {
		double min = Double.MAX_VALUE;
		Service res = null;
		for(int i=0; i<candidates.size(); i++) {
			GeneralNode n = GeneralNode.getNode(candidates.get(i).getNode_id());

			if(n.getResidualLife()<min) {
				min = n.getResidualLife();
				res = candidates.get(i);
			}
		}
	
		if(res==null)
			return chooseByRandomStrategy(candidates);
		
		return res;
	}
	
	
	private Service chooseByLatencySetStrategy(LinkedList<Service> candidates, GeneralNode node) {
		double max = 0;
		double max_best = 0;
		Service res = null;
		Service res_best = null;
		for(int i=0; i<candidates.size(); i++) {
			GeneralNode n = GeneralNode.getNode(candidates.get(i).getNode_id());
			if(n.getBestNode()) {
				if (n.inPeerSet(node)) {
					if(n.getResidualLife()>max_best) {
						max_best = n.getResidualLife();
						res_best = candidates.get(i);
					}
				}
			}else {
				if(n.getResidualLife()>max) {
					max = n.getResidualLife();
					res = candidates.get(i);
				}
			}
			
			
		}
		if(res==null&&res_best==null)
			return chooseByRandomStrategy(candidates);
		
		if(res_best!=null)
			return res_best;
		return res;
	}
	
	
	// RICONTROLLARE!!!!!!!!!!!!
	private Service chooseByRandomSelect1Strategy(LinkedList<Service> candidates) {
		
		// Compute the total weight of all items together.
		double totalWeight = 0.0;
		for (Service s : candidates) {
		    totalWeight += s.getWeight();
		}

		// Now choose a random item.
		int idx = 0;
		for (double r = Math.random() * totalWeight; idx < candidates.size() - 1; ++idx) {
		    r -= candidates.get(idx).getWeight();
		    if (r <= 0.0) break;
		}
		
		return candidates.get(idx);	
	}
	
	
	
	private Service chooseByRandomSelect2Strategy(LinkedList<Service> candidates) {
		
		Collections.shuffle(candidates);
		double threshold = CommonState.r.nextDouble();
		double delta = 1;
		Service s_bar = candidates.get(0);
		
		for (Service s : candidates) {
			if(s.getWeight()>threshold) {
				s_bar = s;
				return s_bar;
			
			}else {
				if(threshold-s.getWeight()<delta) {
					delta = threshold-s.getWeight();
					s_bar = s;
				}
			}
		}
		
		return s_bar;
	}
	
	
	
	// DA ESCLUDERE O RICONTROLLARE ........
	private Service chooseByLinkNumStrategy(LinkedList<Service> candidates) {
		double min = Double.MAX_VALUE;
		Service res = null;
		for(int i=0; i<candidates.size(); i++) {

			if(candidates.get(i).getLinkNum()<min) {

				min = candidates.get(i).getLinkNum();
				res = candidates.get(i);
			}
		}
		
		if(res==null)
			return chooseByRandomStrategy(candidates);
		
		return res;
	}
	
	
	
	private Service chooseByEvolutionaryGameStrategy(LinkedList<Service> candidates, GeneralNode node) {
		
		Service candidate = null;
		
		for(int i=0; i<candidates.size(); i++) {
			GeneralNode n = GeneralNode.getNode(candidates.get(i).getNode_id());
			if(n.getResidualLife()>node.getResidualLife()) {
				candidate = candidates.get(i);
				
				if(node.getResidualLife()<candidates.get(i).getPayoff()) {
					double abs = Math.abs((node.getResidualLife() - candidate.getPayoff())/candidate.getPayoff());
					if(CommonState.r.nextDouble()<abs)
						return candidate;
				}

			}
		}
			
		if(candidate==null)
			return chooseByRandomStrategy(candidates);
				
		return candidate;
	}
	
}
