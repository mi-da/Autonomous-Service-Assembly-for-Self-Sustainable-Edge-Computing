package lnu.mida.controller;
import lnu.mida.entity.GeneralNode;

import java.io.PrintStream;

import lnu.mida.controller.init.OverloadFileInitializer;
import lnu.mida.controller.observer.FinalUtilityObserver;
import lnu.mida.protocol.OverloadComponentAssembly;
import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.core.*;
import peersim.util.*;


public class StatController implements Control {

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
	
	
	public StatController(String name) {
		this.name = name;
		component_assembly_pid = Configuration.getPid(name + "." + COMP_PROT);
		application_pid = Configuration.getPid(name + "." + APPL_PROT);
	}
	
	@Override
	public boolean execute() {
		
	
		int cycle_number = CDState.getCycle();
		int total_cycles = Configuration.getInt("simulation.cycles",1);

		
		// print data from last cycle
		if(cycle_number==total_cycles-6) {
			
			/*
			for (int i = 0; i < Network.size(); i++) {
				
				System.out.println("\n MTBF list del nodo " + Network.get(i).getID());
				((GeneralNode) Network.get(i)).printMTBF();
			}*/
			PrintStream ps_first = OverloadFileInitializer.getPs_first();
			PrintStream ps_last = OverloadFileInitializer.getPs_last();
			
			
			double mean = totalMTBFMean();
			//System.out.println(" media totale : " + mean);
			
			ps_first.print(mean+"\n");
			
			int total_counter_up = 0;
			int total_counter_down = 0;
			
			for (int i = 0; i < Network.size(); i++) {
				total_counter_up+=((GeneralNode) Network.get(i)).getUpCycles();
				total_counter_down+=((GeneralNode) Network.get(i)).getDownCycles();				
			}
			
			total_counter_up = total_counter_up/Network.size();
			total_counter_down = total_counter_down/Network.size();
			
			FinalUtilityObserver.total_up+=total_counter_up;
			FinalUtilityObserver.total_down+=total_counter_down;
		} 
		
		return false;
	}
	
	
	public double totalMTBFMean() {
		
		int counter = 0;
		double mean = 0;
		
		for (int i = 0; i < Network.size(); i++) {
			for(int j=0; j<((GeneralNode) Network.get(i)).getMTBF().size(); j++) {
				mean += ((GeneralNode) Network.get(i)).getMTBF().get(j);
				counter++;
			}
		}
		mean = mean/counter;
		return mean;
	}
	
	public double nodeMTBFMean(int id) {
		
		int counter = 0;
		double mean = 0;
		
		for(int i=0; i<((GeneralNode) Network.get(id)).getMTBF().size(); i++) {
			mean += ((GeneralNode) Network.get(id)).getMTBF().get(i);
			counter++;
		}
		mean = mean/counter;
		return mean;
	}
}
