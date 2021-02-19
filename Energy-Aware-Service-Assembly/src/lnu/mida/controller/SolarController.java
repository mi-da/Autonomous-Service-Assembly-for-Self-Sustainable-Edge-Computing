package lnu.mida.controller;

import peersim.config.Configuration;
import peersim.core.*;

import java.io.PrintStream;

import lnu.mida.controller.init.OverloadFileInitializer;
import lnu.mida.entity.GeneralNode;


public class SolarController implements Control {

	/**
	 * The component assambly protocol.
	 */
	private static final String COMP_PROT = "comp_prot";

	/**
	 * The application protocol.
	 */
	private static final String APPL_PROT = "appl_prot";

	private static final int STATES_NUM = 5;

	
	/**
	 * The name of this observer in the configuration file. Initialized by the
	 * constructor parameter.
	 */
	private final String name;

	private final int	component_assembly_pid;
	private final int	application_pid;
	
	private double mean_G[] = {0.000445, 0.073726, 1.542676, 2.270358, 0.743718, 0.004232};
	private int current_state;
	
	private int counter;
	
	
	double mean0=0;
	double mean1=0;
	double mean2=0;
	double mean3=0;
	double mean4=0;
	double mean5=0;
	
	int counter1=0;
	int counter2=0;
	int counter3=0;
	int counter4=0;
	int counter5=0;
	int counter6=0;
	
	//double previousG;
	
	public SolarController(String name) {
		
		this.name = name;
		component_assembly_pid = Configuration.getPid(name + "." + COMP_PROT);
		application_pid = Configuration.getPid(name + "." + APPL_PROT);
		current_state=0;
		counter=0;
	}
	
	
	public boolean execute() {
		
		counter++;
		
		// supponiamo che i round di gossip avvengano ogni 10 min
		// si cambia stato ogni 24 round 
		
		if(counter==24) { 
			counter = 0;
			
			if(current_state<STATES_NUM) {
				current_state+=1;
			}else {
				current_state=0;
			}
			
		}
		

		PrintStream ps_nodo1 = OverloadFileInitializer.getPs_nodo1();
		PrintStream ps_nodo2 = OverloadFileInitializer.getPs_nodo2();
		PrintStream ps_nodo3 = OverloadFileInitializer.getPs_nodo3();
		PrintStream ps_nodo4 = OverloadFileInitializer.getPs_nodo4();

		
		
		
			// i valori di G vengono generati casualmente seguendo il modello Roma, ma sono uguali per tutti i nodi
			
		/*
		if(current_state==0) {
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(0);
				}
			}else if(current_state==1) {
				double max = 0.75;
				double min = 0;
				
				double new_G_value = (double) min + (max - min) * CommonState.r.nextDouble();
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(new_G_value);
				}
			}else if(current_state==2) {
				double max = 2;
				double min = 0.75;
				
				double new_G_value = (double) min + (max - min) * CommonState.r.nextDouble();
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(new_G_value);
				}
			}else if(current_state==3) {
				double max = 2.5;
				double min = 2;
				
				double new_G_value = (double) min + (max - min) * CommonState.r.nextDouble();
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(new_G_value);
				}
			}else if(current_state==4) {
				double max = 2.5;
				double min = 1;
				
				double new_G_value = (double) min + (max - min) * CommonState.r.nextDouble();
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(new_G_value);
				}				
			}else {
				double max = 1;
				double min = 0;
				
				double new_G_value = (double) min + (max - min) * CommonState.r.nextDouble();
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(new_G_value);
				}
		}
		*/
		
		
		
		
		
		/****** MODELLO RANDOM WALK CON G UGUALE PER TUTTI *******/
	/*	
		if(current_state==0) {
			
			for (int i = 0; i < Network.size(); i++) {
				GeneralNode node = (GeneralNode) Network.get(i);
				node.setG(0);
			}
		}else if(current_state==1) {
			
			double max = 0.15;
			double min = -0.15;
			
			double error = (double) min + (max - min) * CommonState.r.nextDouble();
			//error=0;

			if(Math.random()>0.6) {
				//G_value = previousG + 0.01 + error;
				double G_value = previousG + 0.1 + error;

				mean1+=G_value;
				counter1++;
				if(G_value>2.5)
					G_value=2.5;
				if(G_value<0)
					G_value=0;
				
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(G_value);
				}
				
				previousG = G_value;
				
			}else{
				//G_value = previousG - 0.01 + error;
				double G_value = previousG - 0.1 + error;

				mean1+=G_value;
				counter1++;

				if(G_value<0)
					G_value=0;
				
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(G_value);
				}
				
				previousG = G_value;
			}
			
			
			
		}else if(current_state==2) {
			
			double max = 0.15;
			double min = -0.15;
			
			double error = (double) min + (max - min) * CommonState.r.nextDouble();
			//error=0;
			
			if(Math.random()>0.2) {
				double G_value = previousG + 0.15 + error;
				mean2+=G_value;
				counter2++;

				if(G_value>2.5)
					G_value=2.5 + error;
				if(G_value<0)
					G_value=0;
				
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(G_value);
				}
				
				previousG = G_value;
				
			}else{
				double G_value = previousG - 0.15 + error;
				mean2+=G_value;
				counter2++;

				if(G_value<0)
					G_value=0;
				
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(G_value);
				}
				
				previousG = G_value;
			}
			
			

		}else if(current_state==3){
			
			double max = 0.15;
			double min = -0.15;
			
			double error = (double) min + (max - min) * CommonState.r.nextDouble();
			//error=0;

			
			if(Math.random()>0.4){
				double G_value = previousG + 0.15 + error;

				//G_value = previousG + 0.06 + error;
				mean3+=G_value;
				counter3++;

				if(G_value>2.5)
					G_value=2.5 + error;
				if(G_value<0)
					G_value=0;
				
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(G_value);
				}
				
				previousG = G_value;
			}
			else {
				
				double G_value = previousG - 0.15 + error;
				//G_value = previousG - 0.06 + error;
				mean3+=G_value;
				counter3++;

				if(G_value<0)
					G_value=0;
				
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(G_value);
				}
				
				previousG = G_value;
			}
			
			
			
			
			
		}else if(current_state==4){
		
			double max = 0.15;
			double min = -0.15;
			
			double error = (double) min + (max - min) * CommonState.r.nextDouble();
			//error=0;

			
			if(Math.random()>0.8){
				double G_value = previousG + 0.15 + error;
				mean4+=G_value;
				counter4++;

				if(G_value>2.5)
					G_value=2.5 + error;
				if(G_value<0)
					G_value=0;
				
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(G_value);
				}
				
				previousG = G_value;
				
			}
			else {
				double G_value = previousG - 0.15 + error;
				mean4+=G_value;
				counter4++;

				if(G_value<0)
					G_value=0;
				
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(G_value);
				}
				
				previousG = G_value;
			}
			
			
			
			
			
		} else {

			double max = 0.15;
			double min = -0.15;
			
			double error = (double) min + (max - min) * CommonState.r.nextDouble();
			error=0;

			if(Math.random()>0.8){
				//G_value = previousG + 0.1 + error;
				double G_value = previousG + 0.1 + error;
				mean5+=G_value;
				counter5++;

				if(G_value>2.5)
					G_value=2.5;
				if(G_value<0)
					G_value=0;
				
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(G_value);
				}
				
				previousG = G_value;
				
			}
			else {
				//G_value = previousG - 0.1 + error;
				double G_value = previousG - 0.1 + error;
				mean5+=G_value;
				counter5++;

				if(G_value<0)
					G_value=0;
				
				for (int i = 0; i < Network.size(); i++) {
					GeneralNode node = (GeneralNode) Network.get(i);
					node.setG(G_value);
				}
				
				previousG = G_value;
			}
		}
		
*/
		
		
		
		
		
		
		
		
		for (int i = 0; i < Network.size(); i++) {
		
			GeneralNode node = (GeneralNode) Network.get(i);

			double previousG = node.getG();
			
			double G_value;
			
			
			// il trend è crescente
			if(current_state==0) {
				
				G_value = 0;

			}else if(current_state==1) {
				
				double max = 0.15;
				double min = -0.15;
				
				double error = (double) min + (max - min) * CommonState.r.nextDouble();
				//error=0;

				if(Math.random()>0.6) {
					//G_value = previousG + 0.01 + error;
					G_value = previousG + 0.1 + error;

					mean1+=G_value;
					counter1++;
					if(G_value>2.5)
						G_value=2.5;
					if(G_value<0)
						G_value=0;
				}else{
					//G_value = previousG - 0.01 + error;
					G_value = previousG - 0.1 + error;

					mean1+=G_value;
					counter1++;

					if(G_value<0)
						G_value=0;
				}
				
				
				
			}else if(current_state==2) {
				
				double max = 0.15;
				double min = -0.15;
				
				double error = (double) min + (max - min) * CommonState.r.nextDouble();
				//error=0;
				
				if(Math.random()>0.2) {
					G_value = previousG + 0.15 + error;
					mean2+=G_value;
					counter2++;

					if(G_value>2.5)
						G_value=2.5 + error;
					if(G_value<0)
						G_value=0;
				}else{
					G_value = previousG - 0.15 + error;
					mean2+=G_value;
					counter2++;

					if(G_value<0)
						G_value=0;
				}
				
				
				
				
			}else if(current_state==3){
				
				double max = 0.15;
				double min = -0.15;
				
				double error = (double) min + (max - min) * CommonState.r.nextDouble();
				//error=0;

				
				if(Math.random()>0.4){
					G_value = previousG + 0.15 + error;

					//G_value = previousG + 0.06 + error;
					mean3+=G_value;
					counter3++;

					if(G_value>2.5)
						G_value=2.5 + error;
					if(G_value<0)
						G_value=0;
				}
				else {
					
					G_value = previousG - 0.15 + error;
					//G_value = previousG - 0.06 + error;
					mean3+=G_value;
					counter3++;

					if(G_value<0)
						G_value=0;
				}
				
				
				
				
				
			}else if(current_state==4){
			
				double max = 0.15;
				double min = -0.15;
				
				double error = (double) min + (max - min) * CommonState.r.nextDouble();
				//error=0;

				
				if(Math.random()>0.8){
					G_value = previousG + 0.15 + error;
					mean4+=G_value;
					counter4++;

					if(G_value>2.5)
						G_value=2.5 + error;
					if(G_value<0)
						G_value=0;
				}
				else {
					G_value = previousG - 0.15 + error;
					mean4+=G_value;
					counter4++;

					if(G_value<0)
						G_value=0;
				}
				
				
				
				
				
			} else {

				double max = 0.15;
				double min = -0.15;
				
				double error = (double) min + (max - min) * CommonState.r.nextDouble();
				error=0;

				if(Math.random()>0.8){
					//G_value = previousG + 0.1 + error;
					G_value = previousG + 0.1 + error;
					mean5+=G_value;
					counter5++;

					if(G_value>2.5)
						G_value=2.5;
					if(G_value<0)
						G_value=0;
				}
				else {
					//G_value = previousG - 0.1 + error;
					G_value = previousG - 0.1 + error;
					mean5+=G_value;
					counter5++;

					if(G_value<0)
						G_value=0;
				}
			}
			
			
			node.setG(G_value);
			//if(node.getID()==0)
			//System.out.println("G " + (node.getG()));

			
			//if(node.getID()==10)
			//	ps_nodo1.print(G_value+"\n");
			
			//if(node.getID()==2)
			//	ps_nodo2.print(G_value+"\n");
			
			
			//if(node.getID()==3)
			//	ps_nodo3.print(G_value+"\n");
			
			//if(node.getID()==4)
			//	ps_nodo4.print(G_value+"\n");
				
		}
		
		//System.out.println("media 1 : " + mean1/counter1);
		//System.out.println("media 2 : " + mean2/counter2);
		//System.out.println("media 3 : " + mean3/counter3);
		//System.out.println("media 4 : " + mean4/counter4);
		//System.out.println("media 5 : " + mean5/counter5);
		
		
		return false;
	}

}
