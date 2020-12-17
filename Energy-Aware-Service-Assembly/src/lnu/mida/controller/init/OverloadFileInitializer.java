package lnu.mida.controller.init;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import lnu.mida.controller.observer.FinalUtilityObserver;
import peersim.config.*;
import peersim.core.*;
import peersim.util.IncrementalStats;


public class OverloadFileInitializer implements Control {

	// ///////////////////////////////////////////////////////////////////////
	// Fields
	// ///////////////////////////////////////////////////////////////////////

	/**
	 * The name of this observer in the configuration file. Initialized by the
	 * constructor parameter.
	 */
	private final String name;

    //  final file --> collects the average
	public String filename_final;
	public static FileOutputStream	fos_final;
	public static PrintStream ps_final;
	
	//  collects first/last end time
	public String filename_first;
	public static FileOutputStream	fos_first;
	public static PrintStream ps_first;
	
	public String filename_last;
	public static FileOutputStream	fos_last;
	public static PrintStream ps_last;
	
	public static  int experiment_number=0;

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
	public OverloadFileInitializer(String name) {

		this.name = name;
		experiment_number++;
		int total_exps = Configuration.getInt("simulation.experiments",1);
		
		int cycles = Configuration.getInt("simulation.cycles",1);
		int comp_step = Configuration.getInt("COMPOSITION_STEPS",1);

		// the first experiment initializes the final data structure
		if(experiment_number==1) {

			// Quality
			FinalUtilityObserver.quality = new ArrayList<>();
			FinalUtilityObserver.quality_jain = new ArrayList<>();
			// Energy
			FinalUtilityObserver.energy = new ArrayList<>();
			FinalUtilityObserver.energy_jain = new ArrayList<>();
			// Network			
			FinalUtilityObserver.networkSize = new ArrayList<>();
			// Availability			
			FinalUtilityObserver.availability = new ArrayList<>();


			for(int i=0;i<((cycles/comp_step));i++) {
				// Quality
				FinalUtilityObserver.quality.add(new IncrementalStats());
				FinalUtilityObserver.quality_jain.add(new IncrementalStats());
				// Energy
				FinalUtilityObserver.energy.add(new IncrementalStats());
				FinalUtilityObserver.energy_jain.add(new IncrementalStats());
				// Network
				FinalUtilityObserver.networkSize.add(new IncrementalStats());
				// Availability
				FinalUtilityObserver.availability.add(new IncrementalStats());
			}
			
			filename_first = "first_"+Configuration.getString("STRATEGY","no strat")+"_"+System.currentTimeMillis()+".txt";
			
			try {
				fos_first = new FileOutputStream(filename_first);
				ps_first = new PrintStream(fos_first);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			filename_last = "last_"+Configuration.getString("STRATEGY","no strat")+"_"+System.currentTimeMillis()+".txt";
			
			try {
				fos_last = new FileOutputStream(filename_last);
				ps_last = new PrintStream(fos_last);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
		// the last experiment initializes the final file
		if(experiment_number==total_exps) {
			
			filename_final = "exp_assembly_"+Configuration.getString("STRATEGY","no strat")+"_"+System.currentTimeMillis()+".txt";
			
			try {
				fos_final = new FileOutputStream(filename_final);
				ps_final = new PrintStream(fos_final);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	// ///////////////////////////////////////////////////////////////////////
	// Methods
	// ///////////////////////////////////////////////////////////////////////

	@Override
	public boolean execute() {
		return false;
	}

	public String getFilename_final() {
		return filename_final;
	}

	public static FileOutputStream getFos_final() {
		return fos_final;
	}

	public static PrintStream getPs_final() {
		return ps_final;
	}

	public static int getExperiment_number() {
		return experiment_number;
	}
	
	public static PrintStream getPs_first() {
		return ps_first;
	}
	
	public static PrintStream getPs_last() {
		return ps_last;
	}
}
