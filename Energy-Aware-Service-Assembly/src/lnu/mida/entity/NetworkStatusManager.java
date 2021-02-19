package lnu.mida.entity;


import java.io.PrintStream;
import java.util.ArrayList;

import lnu.mida.controller.init.OverloadFileInitializer;
import lnu.mida.controller.observer.FinalUtilityObserver;
import lnu.mida.entity.GeneralNode;
import lnu.mida.entity.Service;
import lnu.mida.protocol.OverloadComponentAssembly;
import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;

public class NetworkStatusManager {


	
	private int original_network_size;
	
	public NetworkStatusManager() {

		original_network_size = Configuration.getInt("NETWORK_SIZE",1);
	}
	
	
	public void printStatus() {
	
		//System.out.println("********* dead nodes = " + dead_nodes + "     original = " +original_network_size +  " ********** ");

		
		
	}
	
	
}
