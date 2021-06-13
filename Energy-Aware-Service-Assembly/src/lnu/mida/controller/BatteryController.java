package lnu.mida.controller;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.*;
import lnu.mida.entity.GeneralNode;
import lnu.mida.protocol.OverloadApplication;


public class BatteryController implements Control {

	/**
	 * The component assambly protocol.
	 */
	private static final String COMP_PROT = "comp_prot";

	/**
	 * The application protocol.
	 */
	private static final String APPL_PROT = "appl_prot";
	
	
	/**
	 * The name of this observer in the configuration file. Initialized by the
	 * constructor parameter.
	 */
	private final String name;

	private final int	component_assembly_pid;
	private final int	application_pid;
		
	public BatteryController(String name) {
		
		this.name = name;
		component_assembly_pid = Configuration.getPid(name + "." + COMP_PROT);
		application_pid = Configuration.getPid(name + "." + APPL_PROT);
	}
	
	
	@Override
	public boolean execute() {
		
		for (int i = 0; i < Network.size(); i++) {
			
			GeneralNode node = (GeneralNode) Network.get(i);

			node.setBattery(node.getBattery() - node.getR());
			node.setResidualLife(node.getBattery()/node.getR());
		
		}

		return false;
	}
	

}
