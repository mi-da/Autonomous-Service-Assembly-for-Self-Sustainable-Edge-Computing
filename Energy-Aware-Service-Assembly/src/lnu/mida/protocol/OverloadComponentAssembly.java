package lnu.mida.protocol;

import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.*;
import peersim.cdsim.CDProtocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import lnu.mida.entity.GeneralNode;
import lnu.mida.entity.Service;
import lnu.mida.entityl.transferfunction.TransferFunction;
import lnu.mida.entityl.transferfunction.UnityTransferFunction;

import java.util.Iterator;

/**
 * This class implements the P2P-based component assembly protocol. Each node
 * represents a single component of a given type my_type. Each component has
 * zero or more dependencies, where each dependency is the type of one required
 * component. In this model a component may have at most one dependency for each
 * type t. Note that dependency loops are not handled; therefore, it is
 * essential that there are no loops in the dependency structure. To avoid loops
 * a component of type i can only have dependencies of type >i
 */
public class OverloadComponentAssembly implements CDProtocol, Cleanable {

	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------

	/**
	 * The maximum number of service types (default: 10)
	 */
	private static final String PAR_TYPES = "types";
	

	/**
	 * The cache size (default: 10)
	 */
	private static final String PAR_CACHESIZE = "cache_size";

	/**
	 * The application protocol.
	 */
	private static final String APPL_PROT = "appl_prot";

	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
	
	/** 
	 * set of services offered by the node
	 */
	private ArrayList<Service> services;
	
	
	/** 
	 * set of services discovered by the node
	 */
	private ArrayList<ArrayList<Service>> candidate_services;
	
	
	/**
	 * The application protocol id.
	 * 
	 */
	private final int application_pid;
	
	/** Maximum number of component types */
	private final int max_types;
	
	private int services_per_node;

	/**
	 * Initialize this object by reading configuration parameters.
	 * 
	 * @param prefix
	 *            the configuration prefix for this class.
	 */
	public OverloadComponentAssembly(String prefix) {	
		max_types = Configuration.getInt(prefix + "." + PAR_TYPES, 10);
		application_pid = Configuration.getPid(prefix + "." + "appl_prot");		
		services = new ArrayList<Service>();
		candidate_services = new ArrayList<ArrayList<Service>>();
		services_per_node = Configuration.getInt("SERVICES_PER_NODE", 0);
	}

	/**
	 * Makes a copy of this object. Needs to be explicitly defined, since we
	 * have array members.
	 */
	@Override
	public Object clone() {
		OverloadComponentAssembly result = null;
		try {
			result = (OverloadComponentAssembly) super.clone();
		} catch (CloneNotSupportedException ex) {
			System.out.println(ex.getMessage());
			assert (false);
		}
		return result;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	
	/**
	 * Returns the maximum number of component types
	 */
	public int getTypes() {
		return max_types;
	};


	
	@Override
	public void nextCycle(Node node, int protocolID) {
		
		int linkableID = FastConfig.getLinkable(protocolID);
		Linkable linkable = (Linkable) node.getProtocol(linkableID);
		
		Service candidate_service; 

		// Services intereact with the services on the same node
		for (Service service : services) {
			for (Service otherservice : services) {
				if(otherservice!=service) {
					candidate_service = otherservice.interact(service);
					if(candidate_service!=null) {
						int index = (int) otherservice.getService_id()%services_per_node;
						if(!alreadyAdded(index, candidate_service))
							candidate_services.get(index).add(candidate_service);
					}
				}
			}
		}
				

		for (int i = 0; i < linkable.degree(); ++i) {		
			
			Node peer = linkable.getNeighbor(i);
			
			if (!peer.isUp()) {
				continue;
			}

			OverloadComponentAssembly comp = (OverloadComponentAssembly) peer.getProtocol(protocolID);			
			ArrayList<Service> neighbourServices = comp.getServices();
					
			for (Service service : services) {
			
				// Interact with services on other Nodes
				for (Service neighbourService : neighbourServices) {

					candidate_service = neighbourService.interact(service);
					if(candidate_service!=null) {
						int index = (int) neighbourService.getService_id()%services_per_node;
						if(!comp.alreadyAdded(index, candidate_service))
							comp.candidate_services.get(index).add(candidate_service);
					}
				}	   
			}	
		}
	}
	
	
	public boolean alreadyAdded(int index, Service s) {

		for(int i=0; i<candidate_services.get(index).size(); i++) {
			Service to_check = candidate_services.get(index).get(i);
			if(to_check==s)
				return true;
		}
		return false;
	}
	
	
	public ArrayList<Service> getServices() {
		return services;
	}

	public void setServices(ArrayList<Service> services) {
		this.services = services;
	}


	@Override
	public void onKill() {
		// TODO Auto-generated method stub		
	}

	public void reset() {
		services = new ArrayList<Service>();
	}

	public ArrayList<ArrayList<Service>> getCandidates() {
		return candidate_services;
	}
	
	public void setCandidates(ArrayList<ArrayList<Service>> cand) {
		candidate_services = cand;
	}
	
	
	public void resetCandidatesList() {
		ArrayList<ArrayList<Service>> cand = new ArrayList<ArrayList<Service>>();
	
		for(Service service : services) {
			cand.add(new ArrayList<Service>());
		}
		setCandidates(cand);
	}
}
