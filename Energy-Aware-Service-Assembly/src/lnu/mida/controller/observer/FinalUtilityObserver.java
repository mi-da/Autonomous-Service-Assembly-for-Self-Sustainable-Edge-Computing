package lnu.mida.controller.observer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lnu.mida.controller.init.OverloadFileInitializer;
import peersim.config.Configuration;
import peersim.core.*;
import peersim.util.*;

/**
 * I am an observer that prints, at each timestep, the minimum, average and
 * maximum utility of all fully resolved services.
 */
public class FinalUtilityObserver implements Control {

	// ///////////////////////////////////////////////////////////////////////
	// Fields
	// ///////////////////////////////////////////////////////////////////////

	/**
	 * The name of this observer in the configuration file. Initialized by the
	 * constructor parameter.
	 */
	private final String name;

	// Quality
	public static ArrayList<IncrementalStats> quality;	
	public static ArrayList<IncrementalStats> quality_jain;
	
	
	// Energy	
	public static ArrayList<IncrementalStats> energy;
	public static ArrayList<IncrementalStats> energy_jain;
	
	// Network	
	public static ArrayList<IncrementalStats> networkSize;
	public static ArrayList<IncrementalStats> networkUpSize;
	
	// Availability	
	public static ArrayList<IncrementalStats> availability;
	public static ArrayList<IncrementalStats> availability_s;
	public static ArrayList<IncrementalStats> availability_ud;
	

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
	public FinalUtilityObserver(String name) {
		this.name = name;
	}

	// ///////////////////////////////////////////////////////////////////////
	// Methods
	// ///////////////////////////////////////////////////////////////////////

	@Override
	public boolean execute() {
		
		int exp_number = OverloadFileInitializer.experiment_number;
		int total_exps = Configuration.getInt("simulation.experiments",1);

		
		// print data from last experiment
		if(exp_number==total_exps) {
			

			PrintStream ps = OverloadFileInitializer.getPs_final();
			
			int n = 1;
			for (IncrementalStats incrementalStats : quality.subList(1, quality.size() ) ) {
					
				int index = quality.indexOf(incrementalStats);
				
				// Quality
				double finalQuality = incrementalStats.getAverage();
				IncrementalStats quality_jain_is = quality_jain.get(index);		
				double finalQualityFairness = quality_jain_is.getAverage();
				
				// Energy
				IncrementalStats energy_is = energy.get(index);	
				IncrementalStats energy_jain_is = energy_jain.get(index);	
				
				double finalEnergy = energy_is.getAverage();
				double finalEnergyFairness = energy_jain_is.getAverage();
				
				// Network
				IncrementalStats nodesAlive_is = networkSize.get(index);	
				double nodesAlive = nodesAlive_is.getAverage();
				
				IncrementalStats nodesUp_is = networkUpSize.get(index);	
				double nodesUp = nodesUp_is.getAverage();
				
				// Availability
				IncrementalStats avail_is = availability.get(index);	
				double availability = avail_is.getAverage();
				
				IncrementalStats avail_s_is = availability_s.get(index);	
				double availability_s = avail_s_is.getAverage();
				
				IncrementalStats avail_ud_is = availability_ud.get(index);	
				double availability_ud = avail_ud_is.getAverage();

				//System.out.println(finalQuality);
				ps.print(n+" ");
				ps.print(finalQuality+" ");
				ps.print(finalQualityFairness+" ");
				ps.print(finalEnergy+" ");
				ps.print(finalEnergyFairness+" ");
				ps.print(nodesAlive+" ");
				ps.print(nodesUp+" ");
				ps.print(availability+" ");
				ps.print(availability_s+" ");
				ps.print(availability_ud+"\n");
				
				n+=1; // learning step

			}						
		}
		
		//System.out.println("\n\n mean Fairness = " + fairness/counter_fairness);

		
		return false;
	}	
	
}