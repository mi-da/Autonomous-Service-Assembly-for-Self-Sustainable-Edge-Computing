package lnu.mida.entity;

import com.lajv.location.Location;

import peersim.config.*;
import peersim.core.Cleanable;
import peersim.core.CommonState;
import peersim.core.Fallible;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.Protocol;

/**
* This is the default {@link Node} class that is used to compose the
* {@link Network}.
*/
public class GeneralNode implements Node {


// ================= fields ========================================
// =================================================================

public Location location;
	

/** used to generate unique IDs */
public static long counterID = -1;

/**
* The protocols on this node.
*/
protected Protocol[] protocol = null;

/**
* The current index of this node in the node
* list of the {@link Network}. It can change any time.
* This is necessary to allow
* the implementation of efficient graph algorithms.
*/
private int index;

/**
* The fail state of the node.
*/
protected int failstate = Fallible.OK;

/**
* The ID of the node. It should be final, however it can't be final because
* clone must be able to set it.
*/
private long ID;


/** 
 * Energy related parameters
 */

// green energy generation rate of the node
private double G;



// Receiving costs 1 unit of energy = Eelect
private double Eelect = 0.02;
// Sending a message costs on average 2 times of Eelect: this leads to the definition of Eamp (average distance is 90 meters)
private double Eamp  = Eelect/Math.pow(90, 2);
// The cost of a CPU operation is equal to the maximum cost of sending a message
private double CPUCost= 0.04;

// ================ constructor and initialization =================
// =================================================================

/** Used to construct the prototype node. This class currently does not
* have specific configuration parameters and so the parameter
* <code>prefix</code> is not used. It reads the protocol components
* (components that have type {@value peersim.core.Node#PAR_PROT}) from
* the configuration.
*/
public GeneralNode(String prefix) {
	location = (Location) Configuration.getInstance(prefix + "." + "loc_impl");
	String[] names = Configuration.getNames(PAR_PROT);
	
	CommonState.setNode(this);
	ID=nextID();
	protocol = new Protocol[names.length];
	for (int i=0; i < names.length; i++) {
		CommonState.setPid(i);
		Protocol p = (Protocol) 
			Configuration.getInstance(names[i]);
		protocol[i] = p; 
	}
}


// -----------------------------------------------------------------

@Override
public Object clone() {
	
	GeneralNode result = null;
	try { result=(GeneralNode)super.clone(); }
	catch( CloneNotSupportedException e ) {} // never happens
	result.protocol = new Protocol[protocol.length];
	CommonState.setNode(result);
	result.ID=nextID();
	for(int i=0; i<protocol.length; ++i) {
		CommonState.setPid(i);
		result.protocol[i] = (Protocol)protocol[i].clone();
	}
	result.location = (Location) location.clone();
	return result;
}

// -----------------------------------------------------------------

/** returns the next unique ID */
private long nextID() {

	return counterID++;
}

// =============== public methods ==================================
// =================================================================


@Override
public void setFailState(int failState) {
	
	// after a node is dead, all operations on it are errors by definition
	if(failstate==DEAD && failState!=DEAD) throw new IllegalStateException(
		"Cannot change fail state: node is already DEAD");
	switch(failState)
	{
		case OK:
			failstate=OK;
			break;
		case DEAD:
			//protocol = null;
			index = -1;
			failstate = DEAD;
			for(int i=0;i<protocol.length;++i)
				if(protocol[i] instanceof Cleanable)
					((Cleanable)protocol[i]).onKill();
			break;
		case DOWN:
			failstate = DOWN;
			break;
		default:
			throw new IllegalArgumentException(
				"failState="+failState);
	}
}

public Location getLocation() {
	return location;
}

// -----------------------------------------------------------------

@Override
public int getFailState() { return failstate; }

// ------------------------------------------------------------------

@Override
public boolean isUp() { return failstate==OK; }

// -----------------------------------------------------------------

@Override
public Protocol getProtocol(int i) { return protocol[i]; }

//------------------------------------------------------------------

@Override
public int protocolSize() { return protocol.length; }

//------------------------------------------------------------------

@Override
public int getIndex() { return index; }

//------------------------------------------------------------------

@Override
public void setIndex(int index) { this.index = index; }
	
//------------------------------------------------------------------

/**
* Returns the ID of this node. The IDs are generated using a counter
* (i.e. they are not random).
*/
@Override
public long getID() { return ID; }

//------------------------------------------------------------------

@Override
public String toString() 
{
	StringBuffer buffer = new StringBuffer();
	buffer.append("ID: "+ID+" index: "+index+"\n");
	for(int i=0; i<protocol.length; ++i)
	{
		buffer.append("protocol["+i+"]="+protocol[i]+"\n");
	}
	return buffer.toString();
}

//------------------------------------------------------------------

/** Implemented as <code>(int)getID()</code>. */
@Override
public int hashCode() { return (int)getID(); }


public static GeneralNode getNode(long id) {
	for (int i = 0; i < Network.size(); i++) {
		GeneralNode n = (GeneralNode) Network.get(i);
		if(n.getID()==id)
			return n;
	}
	try {
		throw new Exception("No node found with id "+id);
	} catch (Exception e) {
		e.printStackTrace();
	}
	System.exit(0);
	return null;
}

public double getG() {
	return G;
}


public void setG(double g) {
	G = g;
}

// returns individual CPU energy consumption
public double getConsumedIndividualCPUEnergy(double lambda_CPU) {
	
//	System.out.println("node "+this.getID()+" lambda cpu "+lambda_CPU);
	return lambda_CPU*CPUCost;			
}

// retursn consumed individual communication energy consumption for sending
public double getConsumedIndividualCommEnergySending(double lambda, double latency) {
	
	// No communication involved for services on the same node
	if(latency==0)
		return 0;	
	
	// Sending energy =  K(E_{elect} + E_{amp} l_{S,S'}^2)
	double sendingEnergy = lambda*(Eelect+(Eamp*Math.pow(latency,2)));
	
//	System.out.println("latency "+latency);
//	System.out.println("sending energy "+sendingEnergy);
	
	return sendingEnergy;	
}

// returns consumed individual communication energy consumption for receiving
public double  getConsumedIndividualCommEnergyReceiving(double lambda) {
	// Receiving energy = K(E_{elect})
	double receivingEnergy = lambda*Eelect;
	
//	System.out.println("lambda "+lambda);
//	System.out.println("receiving energy "+receivingEnergy);

	return receivingEnergy;	
}


}


