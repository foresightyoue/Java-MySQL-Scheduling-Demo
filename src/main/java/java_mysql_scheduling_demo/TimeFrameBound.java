/* Author: Bernd Mehnert  (Github username: bhmehnert) 
*  
*  This class is used by our Scheduler class. We decode the time frame information of our
*  table MY_TABLE as a List object whose components are objects of this class.
*/

package java_mysql_scheduling_demo;

public class TimeFrameBound {
	int idTable; // The primary key of a particular row is stored here. 
	int tVal;   // In that row, we take one of the boundary values of our time frame.
	int sign;   // This is -1 for the right boundary and 1 for the left.

	public TimeFrameBound(int id, int tv, int sgn){
		this.idTable = id;
		this.tVal = tv;
		this.sign = sgn;
	}
}