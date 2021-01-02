package lnu.mida.controller;

import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.core.*;

import java.io.PrintStream;
import java.util.ArrayList;

import lnu.mida.controller.init.OverloadFileInitializer;
import lnu.mida.entity.GeneralNode;
import lnu.mida.entity.NetworkStatusManager;
import lnu.mida.entity.Service;
import lnu.mida.protocol.OverloadApplication;
import lnu.mida.protocol.OverloadComponentAssembly;

public class OverloadIdController implements Control {
	
	/**
	 * The component assambly protocol.
	 */
	private static final String COMP_PROT = "comp_prot";

	/**
	 * The application protocol.
	 */
	private static final String APPL_PROT = "appl_prot";
	
	private final int	component_assembly_pid;
	private final int	application_pid;

	public OverloadIdController(String prefix) {
		component_assembly_pid = Configuration.getPid(prefix + "." + COMP_PROT);
		application_pid = Configuration.getPid(prefix + "." + APPL_PROT);
    }

	@Override
	public boolean execute() {
		
		System.err.println("------- ID CONTROLLER -------");
	
		for (int i = 0; i < Network.size(); i++) {	
			
			GeneralNode n = (GeneralNode) Network.get(i);		
			OverloadComponentAssembly ca = (OverloadComponentAssembly) n.getProtocol(component_assembly_pid);		
			OverloadApplication appl = (OverloadApplication)  n.getProtocol(application_pid);	
			
			ca.reset();
			appl.reset();
						
		}

		if(Network.size()>0) {

			PrintStream ps_last = OverloadFileInitializer.getPs_last();
			//ps_last.print(CDState.getCycle()+"\n");
						
			System.out.println("T_one");
			NetworkStatusManager man = new NetworkStatusManager();
			man.updateTone(CDState.getCycle());
		}
		
	   Service.counterID=0;
	   GeneralNode.counterID=-1;
	   return false;
    }

}
