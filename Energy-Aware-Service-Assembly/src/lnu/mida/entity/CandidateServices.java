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
		candidate_lists = new LinkedList[types-1];

	}
	
	
	
	// addCandidateService(Service s)
	// add service of type s to array list in index s	
	public void addCandidateService(Service s) {
		
		int index = s.getType()-1;
		if(!alreadyAdded(index, s)) {
			candidate_lists[index].add(s);
		}
	}
	
	// check if service s is already added to list of type 'type'
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
		return candidate_lists[type-1];
	}
	
	@SuppressWarnings("unchecked")
	public void resetCandidateLists() {
		//System.out.println("reset");
		candidate_lists = new LinkedList[types-1];
		initializeLists();
	}
	
	
	public void initializeLists() {
		for(int j=0; j<types-1; j++) {
			LinkedList<Service> new_list = new LinkedList<Service>();
			setCandidateList(j, new_list);
		}
	}
	
	public void setCandidateList(int index, LinkedList<Service> list) {
		candidate_lists[index] = list;
	}
	
	
	public void printListOfType(int type) {
		System.out.println("\n - List of type " + type + " - ");
		for(int i=0; i<candidate_lists[type-1].size(); i++) {
			System.out.println(((Service) candidate_lists[type-1].get(i)).getService_id()+ "   type " + ((Service) candidate_lists[type-1].get(i)).getType());
		}
	}
	
	public void printAllLists() {
		for(int j=0; j<types-1; j++) {
			System.out.println("\n - List of type " + (j+1) + " - ");
			for(int i=0; i<candidate_lists[j].size(); i++) {
				System.out.println(((Service) candidate_lists[j].get(i)).getService_id() + "   type " + ((Service) candidate_lists[j].get(i)).getType());
			}
		}
	}
	
	public long getListSize(int type) {
		return candidate_lists[type].size();
	}

}
