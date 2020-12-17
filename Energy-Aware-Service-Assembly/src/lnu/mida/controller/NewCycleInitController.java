package lnu.mida.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.lajv.location.Location;

import lnu.mida.entity.GeneralNode;
import lnu.mida.entity.Service;
import lnu.mida.protocol.OverloadComponentAssembly;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class NewCycleInitController implements Control {

	
	/**
	 * The component assambly protocol.
	 */
	private static final String COMP_PROT = "comp_prot";

	/**
	 * The application protocol.
	 */
	private static final String APPL_PROT = "appl_prot";

	private static final String PAR_PROT = "comp_prot";
	
	
	private final String name;

	private final int component_assembly_pid;
	private final int application_pid;

		
	
	public NewCycleInitController(String name) {
		
		this.name = name;
		component_assembly_pid = Configuration.getPid(name + "." + COMP_PROT);
		application_pid = Configuration.getPid(name + "." + APPL_PROT);
	}
	
	@Override
	public boolean execute() {
		
							
	// si stabilisce il weight per ogni servizio - Random Select 1
	for (int i = 0; i < Network.size(); i++) {	
		
		GeneralNode n = (GeneralNode) Network.get(i);		
		OverloadComponentAssembly ca = (OverloadComponentAssembly) n.getProtocol(component_assembly_pid);		

		double residual_life = n.getResidualLife();

		// reset the services
		ArrayList<Service> services = ca.getServices();
		for (Service service : services) {
			double sum = 0;
			
			for (int j = 0; j < Network.size(); j++) {	
				if(Network.get(j)==n)
					continue;
				OverloadComponentAssembly ca2 = (OverloadComponentAssembly) n.getProtocol(component_assembly_pid);		
				ArrayList<Service> services2 = ca2.getServices();
				for (Service service2 : services2) {
					if(service.getType()==service2.getType())
						sum+=((GeneralNode) Network.get(j)).getResidualLife();
				}
			}
			service.setWeight(residual_life/sum);
		}
					
	}


	
	// set services payoff - Evolutionary Game Strategy
	for (int i = 0; i < Network.size(); i++) {	
		
		GeneralNode n = (GeneralNode) Network.get(i);		
		OverloadComponentAssembly ca = (OverloadComponentAssembly) n.getProtocol(component_assembly_pid);		
		
		ArrayList<Service> services = ca.getServices();
		
		for (Service service : services) {
			
			double payoff = 0;
			int counter = 0;
			
			for (int j = 0; j < Network.size(); j++) {	
				
				GeneralNode n2 = (GeneralNode) Network.get(j);		
				OverloadComponentAssembly ca2 = (OverloadComponentAssembly) n.getProtocol(component_assembly_pid);		
				
				ArrayList<Service> services2 = ca2.getServices();
				for (Service service2 : services2) {
					
					if(service2.getType()==service.getType())
						payoff += n2.getResidualLife();
						counter++;
				}

			}
			payoff = payoff/counter;
			service.setPayoff(payoff);
		}
		
	}	


			
	// il peer set viene determinato in base alla latenza di comunicazione - Latency Set Strategy
	for (int i = 0; i < Network.size(); i++) {

		GeneralNode n = (GeneralNode) Network.get(i);

		n.resetPeerSet();

		double k = 0.1 * Network.size();
		int g = (int) k;
		if(g==0 && Network.size()!=0)
			g = 1;
					
		// si determina la latenza tra il nodo n e tutti gli altri
		ArrayList<Double> original_latency_array = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> latency_array = new ArrayList<ArrayList<Double>>();
		
		for (int j = 0; j < Network.size(); ++j) {		

			Node peer = Network.get(j);		
			
			Location senderLoc = n.getLocation();
			Location receiverLoc = ((GeneralNode) peer).getLocation();
			double latency = senderLoc.latency(receiverLoc);
			original_latency_array.add(latency);
		}
		
		for (int j = 0; j < Network.size(); ++j) {
			latency_array.add(new ArrayList<Double>(Arrays.asList(original_latency_array.get(j), (double)j)));
		}

						
		Collections.sort(latency_array, new Comparator<ArrayList<Double>>() {    
	        @Override
	        public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
	            return o1.get(0).compareTo(o2.get(0));
	        }               
		});

		
		for(int l=0; l<g; l++) {
			double index = latency_array.get(l).get(1);
			//System.out.println("\n index =  " + index + "    node = " + Network.get((int) index).getID());

			n.addPeerSet(Network.get((int) index));
		}
		
		/*
		System.out.println("\n sorted array: ");
		System.out.println(prova_array);

		
		System.out.println("\n\n peerset: ");
		for(int d=0; d<n.getPeerSetSize(); d++) {
			System.out.println(n.getPeerSet().get(d).getID());
		}
		*/
	}
			 
			
			
			
	// si individuano i k nodi migliori - Latency Set Strategy
	
	ArrayList<Double> original_residual_life_array = new ArrayList<Double>();
	ArrayList<ArrayList<Double>> residual_life_array = new ArrayList<ArrayList<Double>>();

	for (int i = 0; i < Network.size(); i++) {
		
		GeneralNode n = (GeneralNode) Network.get(i);
		n.setBestNode(false);
		original_residual_life_array.add(n.getResidualLife());
	}	
	
	for (int j = 0; j < Network.size(); ++j) {
		residual_life_array.add(new ArrayList<Double>(Arrays.asList(original_residual_life_array.get(j), (double)j)));
	}
	
	Collections.sort(residual_life_array, new Comparator<ArrayList<Double>>() {    
        @Override
        public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
            return o1.get(0).compareTo(o2.get(0));
        }               
	});
	

	// si seleziona il 30% di noid "best"
	double k = 0.5 * Network.size();
	int g = (int) k;
	if(g==0&&Network.size()!=0)
		g=1;

	
	//System.out.println("\n\n new: ");
	for(int l=residual_life_array.size()-1; l>residual_life_array.size()-g-1; l--) {
		double index = residual_life_array.get(l).get(1);
		//System.out.println("\n index =  " + index + "    node = " + Network.get((int) index).getID());
		((GeneralNode) Network.get((int) index)).setBestNode(true);
		//System.out.println(((GeneralNode) Network.get((int) index)).getID());

	}
		return false;
	}
}
