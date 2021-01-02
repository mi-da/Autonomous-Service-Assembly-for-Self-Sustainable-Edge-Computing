package lnu.mida.entity;

import java.util.LinkedList;

import peersim.config.Configuration;

// Hosts lists of candidate services "divided" per type

// Array of linked lists
//List 0 --> Services type 0 etc..
public class CandidateServices {

	private int types;
	
	private LinkedList<Service>[] candidate_lists;
	
	public int node_id;
	
	@SuppressWarnings("unchecked")
	public CandidateServices() {
	
		types = Configuration.getInt("TYPES", 0);
		candidate_lists = new LinkedList[types];

	}
	
	
	
// addCandidateService(Service s)
// add service of type s to array list in index s
	
	public void addCandidateService(Service s) {
		
		int index = s.getType();
		if(!alreadyAdded(index, s)) {
			//System.out.println("	aggiungo il servizio " + s.getService_id() + " alla lista di tipo " + index + " del nodo " + node_id);
			candidate_lists[index].add(s);
		}
	}
	
	public boolean alreadyAdded(int type, Service s) {

		for(int i=0; i<candidate_lists[type].size(); i++) {
			Service to_check = (Service) candidate_lists[type].get(i);
			if(to_check==s)
				return true;
		}
		return false;
	}
	
	
	
// getAllCandidateServices returns array of lists
	public LinkedList<Service>[] getAllCandidateServices() {
		return candidate_lists;
	}
	
// getCandidateServices(int type) returns linked list at index type
	public LinkedList<Service> getCandidateServices(int type) {
		return candidate_lists[type];
	}
	
	
	public void initializeLists() {
		candidate_lists = new LinkedList[types];
		for(int j=0; j<types; j++) {
			LinkedList<Service> new_list = new LinkedList<Service>();
			candidate_lists[j] = new_list;
		}
	}
	
	
	public void printListOfType(int type) {
		System.out.println("\n - List of type " + type + " - ");
		for(int i=0; i<candidate_lists[type].size(); i++) {
			System.out.println(((Service) candidate_lists[type].get(i)).getService_id()+ "   type " + ((Service) candidate_lists[type].get(i)).getType());
		}
	}
	
	public void printAllLists() {
		for(int j=0; j<types; j++) {
			System.out.println("\n - List of type " + (j) + " - ");
			for(int i=0; i<candidate_lists[j].size(); i++) {
				System.out.println(((Service) candidate_lists[j].get(i)).getService_id() + "   type " + ((Service) candidate_lists[j].get(i)).getType());
			}
		}
	}
	
	public long getListSize(int type) {
		return candidate_lists[type].size();
	}
	
	public void clearLists() {
		for(int j=0; j<types; j++) {
			candidate_lists[j].clear();
		}
	}

}