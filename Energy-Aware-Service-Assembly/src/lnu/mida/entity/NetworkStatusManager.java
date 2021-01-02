package lnu.mida.entity;


import java.io.PrintStream;
import java.util.ArrayList;

import lnu.mida.controller.init.OverloadFileInitializer;
import lnu.mida.entity.GeneralNode;
import lnu.mida.entity.Service;
import lnu.mida.protocol.OverloadComponentAssembly;
import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.util.IncrementalStats;

public class NetworkStatusManager {


	
	private int original_network_size;
	
	public static ArrayList<IncrementalStats> T_all;	
	public static ArrayList<IncrementalStats> T_one;	
	public static ArrayList<IncrementalStats> D_all;	
	public static ArrayList<IncrementalStats> D_one;	
	
	
	private int down_nodes;
	
	public NetworkStatusManager() {

		original_network_size = Configuration.getInt("NETWORK_SIZE",1);
	}
	
	
	public void printStatus() {
	
		//System.out.println("********* dead nodes = " + dead_nodes + "     original = " +original_network_size +  " ********** ");

		
		PrintStream ps_first = OverloadFileInitializer.getPs_first();
		PrintStream ps_last = OverloadFileInitializer.getPs_last();
			
		if(Network.size()-down_nodes==original_network_size) {
			//System.out.println("T_all");
			updateTall(CDState.getCycle());
			//ps_first.print(CDState.getCycle()+"\n");
			//System.out.println(" new E[T_all] : " + T_all);
		}
		if(Network.size()-down_nodes==1) {
			//System.out.println("T_one");
			updateTone(CDState.getCycle());
			//ps_last.print(CDState.getCycle()+"\n");
			//System.out.println(" new E[T_one] : " + T_one);
		}
				
	}
	
	
	public void updateTall(int val) {
		IncrementalStats to_add = new IncrementalStats();
		to_add.add(val);
		T_all.add(to_add);
	}
	
	public void updateTone(int val) {
		IncrementalStats to_add = new IncrementalStats();
		to_add.add(val);
		T_one.add(to_add);
	}
	
	public void updateDall(int val) {
		IncrementalStats to_add = new IncrementalStats();
		to_add.add(val);
		D_all.add(to_add);
	}
	
	public void updateDone(int val) {
		IncrementalStats to_add = new IncrementalStats();
		to_add.add(val);
		D_one.add(to_add);
	}
	
	
	public void addDownNode() {
		down_nodes++;
	}
	
	public void remDownNode() {
		down_nodes--;
	}
}
