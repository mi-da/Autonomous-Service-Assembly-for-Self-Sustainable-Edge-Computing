/*
 * Copyright (c) 2012, 2014 Moreno Marzolla
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package lnu.mida.controller;

import java.util.ArrayList;

import lnu.mida.controller.init.OverloadFileInitializer;
import lnu.mida.controller.observer.FinalUtilityObserver;
import lnu.mida.entity.*;
import lnu.mida.entity.GeneralNode;
import lnu.mida.protocol.OverloadApplication;
import lnu.mida.protocol.OverloadComponentAssembly;
import peersim.cdsim.CDState;
import peersim.config.*;
import peersim.core.*;
import java.io.PrintStream;


/**
 * I am an observer that observe
 */
public class OverloadReset implements Control {

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

	// ///////////////////////////////////////////////////////////////////////
	// Fields
	// ///////////////////////////////////////////////////////////////////////

	/**
	 * The name of this observer in the configuration file. Initialized by the
	 * constructor parameter.
	 */
	private final String name;

	private final int	component_assembly_pid;
	private final int	application_pid;
	
	// ///////////////////////////////////////////////////////////////////////
	// Constructor
	// ///////////////////////////////////////////////////////////////////////
	/**
	 * Standard constructor that reads the configuration parameters. Invoked by
	 * the simulation engine.
	 * 
	 * @param name
	 *            the configuration prefix for this class.
	 */
	public OverloadReset(String name) {
		
		this.name = name;
		component_assembly_pid = Configuration.getPid(name + "." + COMP_PROT);
		application_pid = Configuration.getPid(name + "." + APPL_PROT);		
	}

	// ///////////////////////////////////////////////////////////////////////
	// Methods
	// ///////////////////////////////////////////////////////////////////////

	@Override
	public boolean execute() {
		
		int t_all = 0;		
		int d_one = 0;

		for (int i = 0; i < Network.size(); i++) {	
			
			GeneralNode n = (GeneralNode) Network.get(i);		
			OverloadComponentAssembly ca = (OverloadComponentAssembly) n.getProtocol(component_assembly_pid);		
			
			// reset the services
			ArrayList<Service> services = ca.getServices();
			
			for (Service service : services) {
				
				service.resetLinkNum();

				Service[] listDepObj = service.getDependencies_obj();
				boolean[] listDep = service.getDependencies();
				

				for (int j = 0; j < listDep.length; j++) {
					
					boolean dep = listDep[j];
					if (dep == true) {

						Service depObj = listDepObj[j];

						if(depObj!=null) 
							depObj.addLinkNum();
					}
				}
				ca.resetCandidateServices();
			}
			
		
			if(Network.get(i).getFailState()==2) {
				d_one++;
			}else {
				t_all++;
			}
				
		}
		if(t_all == Network.size())
			FinalUtilityObserver.t_all_cycles+=6;
		if(d_one>0)
			FinalUtilityObserver.d_one_cycles+=6;
		
		
			
		ArrayList<Node> to_remove = new ArrayList<Node>();
		NetworkStatusManager man = new NetworkStatusManager();

		for (int i = 0; i < Network.size(); i++) {	
			
			GeneralNode n = (GeneralNode) Network.get(i);
			OverloadComponentAssembly ca = (OverloadComponentAssembly) n.getProtocol(component_assembly_pid);		
		
			if(n.getFailState()==0)
				n.addUpCycles(6);
			if(n.getFailState()==2)
				n.addDownCycle(6);
			
			// Nodes with no battery die
			if(n.getBattery()<0) {	
				n.setBattery(0);
				n.setR(0);
				n.addMTBF(CDState.getCycle()-n.getLastFailure());

				n.setLastFailure(CDState.getCycle());
				to_remove.add(Network.get(i));
				
			}
			
			// down nodes with battery live
			if(n.getFailState()==2 && n.getBattery()>0.1*n.getCapacity()) {
				man.remDownNode();
				n.setFailState(0);
			}
				
		}


		for(int j=0; j<to_remove.size(); j++) {
			Node to_del = to_remove.get(j);
			long id_to_del = to_del.getID();
			
			for (int i = 0; i < Network.size(); i++) {
				if(Network.get(i).getID()==id_to_del) {
					// si rimuovono tutte le dipendenze
					remove_links(i);
					man.printStatus();
					man.addDownNode();
					Network.get(i).setFailState(2);
				}
			}
		}
		
		
		return false;
	}
	
	
	
	
	
	
	
	public void remove_links(int node_index) {
		
		GeneralNode n = (GeneralNode) Network.get(node_index);		
		OverloadComponentAssembly ca = (OverloadComponentAssembly) n.getProtocol(component_assembly_pid);		
		
		ArrayList<Service> services_to_del = ca.getServices();
		for (Service service2 : services_to_del) {
			service2.reset();
		}				

		for (int i = 0; i < Network.size(); i++) {
			GeneralNode node_to_check = (GeneralNode) Network.get(i);		
						
			OverloadComponentAssembly ca_to_check = (OverloadComponentAssembly) node_to_check.getProtocol(component_assembly_pid);		
			ArrayList<Service> services_to_check = ca_to_check.getServices();

			for (Service service : services_to_check) {
				Service[] listDepObj = service.getDependencies_obj();
				boolean[] listDep = service.getDependencies();
				
				if(listDepObj==null)
					continue;
				
				for (int j = 0; j < listDep.length; j++) {
					
					boolean dep = listDep[j];
					if (dep == true) {
						Service depObj = listDepObj[j];

						if(depObj==null)
							continue;
						
						for (Service s : services_to_del) {
							if(depObj==s) 
								service.unlinkDependency(s);
						}
					}
				}
			}
			
		}
				
	}
	
	
}
