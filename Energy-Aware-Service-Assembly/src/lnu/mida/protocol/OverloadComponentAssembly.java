package lnu.mida.protocol;

import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.*;
import peersim.cdsim.CDProtocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lnu.mida.entity.CandidateServices;
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
	private CandidateServices candidate_services;
	
	
	/**
	 * The application protocol id.
	 * 
	 */
	private final int application_pid;
	
	/** Maximum number of component types */
	private final int max_types;
	

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
		//candidate_services = new CandidateServices();
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
		
		Service candidate;
				
		// Services intereact with the services on the same node
		for (Service service : services) {			
			for (Service otherservice : services) {
				if(otherservice!=service) {
					candidate = otherservice.interact(service);
					if(candidate!=null) {
						
						// perchè???????????????
						if(candidate_services==null) {
							//resetCandidateServices();
						}
						candidate_services.addCandidateService(candidate);
						//if(node.getID()==0&&node.isUp())
						//	System.out.println("servizio aggiunto: " + candidate.getService_id() + "  type : " + candidate.getType());
						
					}
				}
			}
		}

		for (int i = 0; i < linkable.degree(); ++i) {		
			
			Node peer = linkable.getNeighbor(i);
			
			//System.out.println("node "+node.getID()+" interacts with node "+peer.getID());

			//if(node.getID()==0&&node.isUp())
			//	System.out.println("peer: " + peer.getID());

			
			if (!peer.isUp()) {
				continue;
			}
			
			OverloadComponentAssembly comp = (OverloadComponentAssembly) peer.getProtocol(protocolID);	
			
			ArrayList<Service> neighbourServices = comp.getServices();
					
			
			
			for (Service service : services) {
							
				// Interact with services on other Node
				for (Service neighbourService : neighbourServices) {
					candidate = neighbourService.interact(service);
					if(candidate!=null) {
						
						// perchè???????????????
						if(comp.getCandidateServices()==null) {
							//comp.resetCandidateServices();
						}
						comp.getCandidateServices().addCandidateService(candidate);
						GeneralNode n2 = GeneralNode.getNode(neighbourService.getNode_id());
						//if(n2.getID()==0&&n2.isUp())
							//System.out.println("servizio aggiunto: " + candidate.getService_id() + "  type : " + candidate.getType());						
					}
				}
			   
			}
			
//			System.exit(0);
			
		}
		
	}
	
	
	
	public ArrayList<Service> getServices() {
		return services;
	}

	public void setServices(ArrayList<Service> services) {
		this.services = services;
	}

	public CandidateServices getCandidateServices() {
		return candidate_services;
	}
	
	public void setCandidateServices(CandidateServices cs) {
		candidate_services = cs;
	}
	
	public void resetCandidateServices() {
		candidate_services = new CandidateServices();
		candidate_services.resetCandidateLists();
	}

	@Override
	public void onKill() {
		// TODO Auto-generated method stub		
	}

	public void reset() {
		services = new ArrayList<Service>();
	}
	
	
}
