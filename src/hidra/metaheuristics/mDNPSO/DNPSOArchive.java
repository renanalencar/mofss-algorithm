package hidra.metaheuristics.mDNPSO;

import hidra.core.population.HIDRAArchive;
import hidra.jmetal.core.Solution;
import jmetal.util.archive.Archive;

public class DNPSOArchive extends HIDRAArchive{

	public DNPSOArchive(int maxSize, int numberOfObjectives) {
		super(maxSize, numberOfObjectives);		
	}
	
}
