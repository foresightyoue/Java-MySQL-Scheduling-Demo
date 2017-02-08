/* Author: Bernd Mehnert  (Github username: bhmehnert) 
 *
 * class Scheduler:
 * 
 * Methods:
 *
 *     settFKS :         Gets as input a List object whose components are objects
 *                       from our TimeFrameBound class and stores it in timeFrameSeq. 
 *                       An object of this sort just decodes the time frame information 
 *                       concerning our table MY_TABLE. 
 *
 *     computeSchedule : Delivers an array of integers which for every time frame / 
 *                       row / primary key i gives the number of the worker which
 *                       should work at that frame, where of course the number of
 *                       workers should be minimal. */

package java_mysql_scheduling_demo;

import java.util.*;

public class Scheduler {
	List<TimeFrameBound> timeFrameSeq;
	
	public void settFKS (List<TimeFrameBound> tFKS) {
		timeFrameSeq = tFKS;
	}

	public int[] computeSchedule() {
		
		// We procede as follows: 
		// First we sort timeFrameSeq by the second component, i.e. by time 
		// interval borders, putting 'ending frames' before 'starting frames' in 
		// the case of identical time interval boarders:
		
		timeFrameSeq.sort((TimeFrameBound b1, TimeFrameBound b2) -> comp(b1,b2));
		
		// Now go through that list and whenever you are at the beginning of a 
		// time frame, put either one of your available workers (stored in the
		// queue freeBD) on that frame or install a new worker if no worker is 
		// available. The workers names are 1,2,3, ...:

		Queue<Integer> freeBD = new LinkedList<Integer>();
		
		int latestD = 0;   // Stores the latest worker we had to install.

		int n = timeFrameSeq.size(); // Note that this is an even number.

		int helpVar = 0;

		int[] myOutput = new int[n/2]; // List of workers 
		                               // we choose for our time frames.
		
		for (TimeFrameBound bound : timeFrameSeq) {                                    
			helpVar = bound.idTable;
			if (bound.sign == 1) {   // I.e. we are at a starting 
				                     // point of a frame.
				if (freeBD.isEmpty()) {
					latestD += 1;
					myOutput[helpVar] = latestD;
				} 
				else {
					myOutput[helpVar] = freeBD.remove();
				}
			}
			else {
			freeBD.add(myOutput[helpVar]);  // We are about to leave a time frame
			                                // therefore its driver is available 
			                                // now. 
			}
		}
		return myOutput;
	}        

	// We will sort our timeframebounds using the following comparison function.
	// It will order our timeframebounds in such a way that whenever we have 
	// identical framebounds, we will put 'ending frames' before 'starting frames', which
	// leads to correctly keeping track of how many workers are free at the moment. 

	private int comp(TimeFrameBound b1, TimeFrameBound b2){
		int returnValue = 0;
		if(b1.tVal != b2.tVal) {
			returnValue = b1.tVal - b2.tVal;
		}
		else {
			returnValue = b1.sign - b2.sign;	
		}
	return returnValue;
	}
}                                                                                                                                                                                                                                                                                                   
