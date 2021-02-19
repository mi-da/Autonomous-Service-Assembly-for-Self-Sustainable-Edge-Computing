package lnu.mida.controller;

import java.util.ArrayList;

import lnu.mida.entity.CandidateServices;
import lnu.mida.entity.GeneralNode;
import lnu.mida.entity.GreenReputation;
import lnu.mida.entity.LocalReputation;
import lnu.mida.entity.OverallEnergyReputation;
import lnu.mida.entity.Service;
import lnu.mida.protocol.OverloadApplication;
import lnu.mida.protocol.OverloadComponentAssembly;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;



public class LinkController implements Control {
	
	
	
	
	/**
	 * The component assambly protocol.
	 */
	private static final String COMP_PROT = "comp_prot";

	/**
	 * The application protocol.
	 */
	private static final String APPL_PROT = "appl_prot";


	private final String name;

	private final int component_assembly_pid;
	private final int application_pid;

	private int services_per_node;

	
	
	
	public LinkController(String name) {
		this.name = name;
		component_assembly_pid = Configuration.getPid(name + "." + COMP_PROT);
		application_pid = Configuration.getPid(name + "." + APPL_PROT);
		services_per_node = Configuration.getInt("SERVICES_PER_NODE", 0);
	}
	
	
	
	public boolean execute() {
			
		//System.out.println("\n--- link controller ---");
		
		int send = 0, rcv=0;
		
		for (int i = 0; i < Network.size(); i++) {
			
			if (!Network.get(i).isUp()) {
				continue;
			}
			
			GeneralNode node = (GeneralNode) Network.get(i);
			OverloadComponentAssembly ca = (OverloadComponentAssembly) node.getProtocol(component_assembly_pid);
			OverloadApplication appl = (OverloadApplication) node.getProtocol(application_pid);
			
			//GreenReputation candReputation = appl.getOrCreateGreenReputation((int) node.getID());
			//System.out.println("\n>> nodo " + node.getID() + "      prevision : " + candReputation.getPrevision());
			
			
			
			ArrayList<Service> services = ca.getServices();
			CandidateServices candidates = ca.getCandidateServices();
						
			//if(node.getBattery()==10)
				//System.out.println("\n\n\n\nListe Nodo " + node.getID() + " :   " );

			//candidates.printAllLists();

			/*
			if(node.getID()==0 && node.isUp()) {
				System.out.println("Liste Nodo 0 :   " + candidates.node_id);
				candidates.printAllLists();
			}
			if(node.getID()==1 && node.isUp()) {
				System.out.println("Liste Nodo 1 :   "+candidates.node_id);
				candidates.printAllLists();
			}
			*/

			
			for (Service service : services) {
				
				OverallEnergyReputation candReputation = appl.getOrCreateOverallEnergyReputation((int) service.getService_id());
				//System.out.println("\n>> servizio " + service.getService_id() + "      prevision : " + candReputation.getEe() + " k = " + candReputation.getK());
				service.setOverallRep(candReputation.getEe());
				service.setOverallRepCounter(candReputation.getK());
				
				LocalReputation candReputation2 = appl.getOrCreateLocalReputation((int) service.getService_id());
				//System.out.println("\n>> servizio " + service.getService_id() + "      prevision : " + candReputation.getEe() + " k = " + candReputation.getK());
				service.setLocalRep(candReputation2.getEe());
				service.setLocalRepCounter(candReputation2.getK());
				
				//if(node.getID()==1)
				//System.out.println("servizio " + service.getService_id());
				// per ogni dipendenza di ogni servizio cerco tra i possibili candidati
				boolean[] listDep = service.getDependencies();
				
				for (int j = 0; j < listDep.length; j++) {
					
					boolean dep = listDep[j];
					Service[] listDepObj = service.getDependencies_obj();

					if (dep == true) {

						if(candidates.getCandidateServices(j).isEmpty())
							continue;
						//if(node.getID()==1)
						//	candidates.printListOfType(j);
						//System.out.println("		cerco servizio di tipo: " + j);

						Service to_link = appl.chooseByStrategy(candidates.getCandidateServices(j), node);
						
						//System.out.println("		servizio scelto : " + to_link.getService_id()	+ "   type" + to_link.getType() + "\n");
						
						/*
						if(node.getID()==1 && node.isUp()) {
							if(to_link==null) {
								System.out.println("		servizio scelto : " + "  null " );
							}else {
								System.out.println("		servizio scelto : " + to_link.getService_id()	+ "  " + to_link.getType());
							}
						}*/
						
						/*
						if(node.getID()==1 && node.isUp()&&j==6) {
							System.out.println("scelto servizio " + to_link.getService_id());
						}*/
						
						
						if(to_link!=null) {						
							service.addLink(to_link);	
							//System.out.println(" ---- CREATO link tra service " + service.getService_id() + " - " + to_link.getService_id());

						}					
					}
				}
				
				
				service.updateCompoundUtility();
				
			}
			
			/*
			for (Service service : services) {
				if(service.getService_id()==10) {
					// send
					boolean[] listDep = service.getDependencies();
					Service[] listDepObj = service.getDependencies_obj();

					for (int j = 0; j < listDep.length; j++) {
						if (listDep[j] == true && listDepObj[j]!=null) {
							send++;
						}
					}
				}else {
					//rcv
					boolean[] listDep = service.getDependencies();
					Service[] listDepObj = service.getDependencies_obj();

					for (int j = 0; j < listDep.length; j++) {
						if (listDep[j] == true && listDepObj[j].getService_id()==10) {
							rcv++;
						}
					}
				}
			}*/
			
		}
		
		//System.out.println("send : " + send + "     rcv : " + rcv);

		//System.out.println("\n\n\n");
		
		return false;
	}
	

}
