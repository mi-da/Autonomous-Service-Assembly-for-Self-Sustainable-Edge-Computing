package lnu.mida.controller.dynamic;

import java.util.ArrayList;

import lnu.mida.controller.OverloadReset;
import lnu.mida.entity.GeneralNode;
import lnu.mida.entity.NetworkStatusManager;
import lnu.mida.entity.Service;
import lnu.mida.protocol.OverloadApplication;
import lnu.mida.protocol.OverloadComponentAssembly;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;


/**
 * This class removes randomly chosen nodes from the network.
 */
public class OverloadComponentFailures implements Control {

	// ///////////////////////////////////////////////////////////////////////
	// Constants
	// ///////////////////////////////////////////////////////////////////////

	/**
	 * The component assambly protocol.
	 */
	private static final String COMP_PROT = "comp_prot";

	/**
	 * The application protocol.
	 */
	private static final String APPL_PROT = "appl_prot";

	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------

	/**
	 * Minimum number of services to remove
	 */
	protected static final String PAR_NUM = "num";

	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
	private final int num;
	private final String name;
	private final int component_assembly_pid;
	private final int application_pid;


	// ------------------------------------------------------------------------
	// Initialization
	// ------------------------------------------------------------------------
	public OverloadComponentFailures(String name) {
		this.name = name;
		num = Configuration.getInt(name + "." + PAR_NUM);
		component_assembly_pid = Configuration.getPid(name + "." + COMP_PROT);
		application_pid = Configuration.getPid(name + "." + APPL_PROT);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
	@Override
	public boolean execute() {


		for (int k = 0; k < num;k++) {
			if(Network.size()==0)
				return false;
			int j = CommonState.r.nextInt(Network.size());
			
			remove_links(j);
			System.out.println(" nodo morto : " + Network.get(j).getID() + "   (in OverloadComponentFailures)");

			NetworkStatusManager man = new NetworkStatusManager();
			man.printStatus();
			
			GeneralNode node = (GeneralNode) Network.remove(j);			
			OverloadComponentAssembly ca = (OverloadComponentAssembly) node.getProtocol(component_assembly_pid);
			OverloadApplication appl = (OverloadApplication) node.getProtocol(application_pid);		
			appl.reset();
		}
		
		// reset the dependencies
//		for (int i = 0; i < Network.size(); i++) {			
//			GeneralNode n = (GeneralNode) Network.get(i);		
//			OverloadComponentAssembly ca = (OverloadComponentAssembly) n.getProtocol(component_assembly_pid);		
//			OverloadApplication appl = (OverloadApplication)  n.getProtocol(application_pid);	
//			// reset the services
//			ArrayList<Service> services = ca.getServices();
//			for (Service service : services) {
//				service.reset();	
//			}
//						
//		}
		
		
		

		
		return false;				
		
	}
	
	
public void remove_links(int node_index) {
		
		// se qualche servizio ospitato dal nodo morto risolveva delle dipendenze, questi link vanno rimossi
		GeneralNode n = (GeneralNode) Network.get(node_index);		
		OverloadComponentAssembly ca = (OverloadComponentAssembly) n.getProtocol(component_assembly_pid);		
		
		ArrayList<Service> services_to_del = ca.getServices();
		for (Service service2 : services_to_del) {
			service2.reset();
		}				
		//System.out.println("		\n\n\n" );

		// si scorre tutta la rete
		for (int i = 0; i < Network.size(); i++) {
			GeneralNode node_to_check = (GeneralNode) Network.get(i);		
			
			//System.out.println("	node_to_check : " + node_to_check.getID());
			
			// si scorrono tutti i servizi
			OverloadComponentAssembly ca_to_check = (OverloadComponentAssembly) node_to_check.getProtocol(component_assembly_pid);		
			ArrayList<Service> services_to_check = ca_to_check.getServices();

			for (Service service : services_to_check) {
				Service[] listDepObj = service.getDependencies_obj();
				boolean[] listDep = service.getDependencies();
				//System.out.println("		service_to_check : " + service.getService_id());

				
				if(listDepObj==null)
					continue;
				
				// si scorre tutta la lista dei providers
				for (int j = 0; j < listDep.length; j++) {
					
					boolean dep = listDep[j];
					if (dep == true) {
						Service depObj = listDepObj[j];

						if(depObj==null)
							continue;
						
						//System.out.println("			dep of type : " + j + "    provider " + depObj.getService_id());

						// si cerca se ci sono link con il nodo morto
						for (Service s : services_to_del) {
							if(depObj==s) {
								service.unlinkDependency(s);
								//System.out.println(" ***  link RIMOSSO  tra  " + service.getService_id() + " - " + s.getService_id());
							}
						}
					}
				}
			}
			
		}
				
	}
	

}
