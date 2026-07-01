package com.playsawdust.glow.io.resource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.TreeSet;

public class ResourceManager {
	private TreeSet<ResourcePool> pools = new TreeSet<>(Comparator.comparingInt(ResourcePool::priority));
	
	public void addPool(ResourcePool pool) {
		pools.add(pool);
	}
	
	public void removePool(String poolNamespace) {
		pools.removeIf((pool) -> pool.origin().equals(poolNamespace));
	}
	
	public Optional<Resource> findFirst(Identifier id, PriorityOrder order) {
		NavigableSet<ResourcePool> searchSet = (order == PriorityOrder.ASCENDING) ? pools : pools.reversed();
		
		for(ResourcePool pool : searchSet) {
			Optional<Resource> rsrc = pool.find(id);
			if (rsrc.isPresent()) return rsrc;
		}
		
		return Optional.empty();
	}
	
	public List<Resource> find(Identifier id, PriorityOrder order) {
		NavigableSet<ResourcePool> searchSet = (order == PriorityOrder.ASCENDING) ? pools : pools.reversed();
		ArrayList<Resource> results = new ArrayList<>();
		
		for(ResourcePool pool : searchSet) {
			Optional<Resource> rsrc = pool.find(id);
			if (rsrc.isPresent()) results.add(rsrc.get());
		}
		return results;
	}
	
	
	
	public static enum PriorityOrder {
		ASCENDING,
		DESCENDING;
	}
}
