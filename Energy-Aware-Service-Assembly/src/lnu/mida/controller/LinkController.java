package lnu.mida.controller;

import java.util.ArrayList;

import lnu.mida.entity.GeneralNode;
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
	
		System.out.println("\n\n----- link controller ---- \n ");
		
		for (int i = 0; i < Network.size(); i++) {

			if (!Network.get(i).isUp()) {
				continue;
			}
			
			GeneralNode node = (GeneralNode) Network.get(i);
			OverloadComponentAssembly ca = (OverloadComponentAssembly) node.getProtocol(component_assembly_pid);
			OverloadApplication appl = (OverloadApplication) node.getProtocol(application_pid);
			
			ca.printCandidates();
			
			ArrayList<Service> services = ca.getServices();
			ArrayList<ArrayList<Service>> candidates = ca.getCandidates();
			
			for (Service service : services) {
				System.out.println("-- considero Service : " + service.getService_id());
				
				// per ogni dipendenza di ogni servizio cerco tra i possibili candidati
				Service[] listDepObj = service.getDependencies_obj();
				boolean[] listDep = service.getDependencies();
				
				for (int j = 0; j < listDep.length; j++) {
					
					boolean dep = listDep[j];
					if (dep == true) {
						//System.out.println("	dep di tipo : " + j);
						// si selezionano i candidati di tipo j
						ArrayList<Service> candidates_of_type = new ArrayList<>();
						int index = (int) service.getService_id()%services_per_node;

						//System.out.println("		candidates_of_type : " );
						for(int k=0; k<candidates.get(index).size();k++) {
							if(candidates.get(index).get(k).getType()==j) {
								candidates_of_type.add(candidates.get(index).get(k));
								//System.out.println("				" + candidates.get(count).get(k).getService_id());
							}
						}
						
						System.out.println("\n candidates of type : " + j);
						for(int l=0; l<candidates_of_type.size(); l++) {
							System.out.println(candidates_of_type.get(l).getService_id());
						}
						
						Service to_link = appl.chooseByStrategy2(candidates_of_type);
						if(to_link==null)
							return false;
						System.out.println("				\nServizio scelto : " + to_link.getService_id() + "\n\n");

						Service depObj = listDepObj[j];
						
						if(depObj==null) {
							service.linkDependency(to_link);
						}else {
							service.unlinkDependency(depObj);
							service.linkDependency(to_link);
						}
					}
				}
			}
		}
		
		
		
		return false;
	}
	

}
