/* Author: Bernd Mehnert  (Github username: bhmehnert) 
* 
*  class App:
*    Contains the main entry point.
*  
*  Description:  
*   
*  From the MySQL-table MY_TABLE (within the database MY_DB), which by our assumption contains 
*  in each row a 'time frame', i.e. the table has two columns with the names T1 and T2 containing 
*  integers t1 < t2, we read out those frames, store them in a certain List object
*  and hand this to our Scheduler class. This computes us a minimal number of workers
*  1,2,3, .... which are placed at these frames  (i.e. for every primary key in MY_TABLE,
*  we have a worker id (which may occur more than once)) such that for no two overlapping frames,
*  we have placed the same worker there. The results, i.e. the schedule for our workers, 
*  are stored in RESULT_TABLE. (If MY_TABLE/ MY_DB do not exist, we will create them automatically.)   
*/

package java_mysql_scheduling_demo;

import java.io.*;
import java.util.*;
import java.sql.*;

class App {
  
  // JDBC driver name and database URL
  
  static final String DB_URL = "jdbc:mysql://localhost:3306";
  	
  // The Database 

  static final String USER = "username";
  static final String PASS = "password";
  static final String MY_DB = "myDB";  // Will be created if it does 
                                       // not exist.
  	
  // Data concerning the table where read read out the time intervals
  // in which our tasks have to be done.

  static final String MY_TABLE = "myTable";
  static final String PRIMARY_ID = "id"; 
  static final String T1 = "t1";
  static final String T2 = "t2";

  // If in our MY_DB the table MY_TABLE does not exist, 
  // it will be created, where T1,T2 above are between 
  // TMIN and TMAX and we will create NR_ENTRIES many rows.

  static final int TMIN = 0;
  static final int TMAX = 10000;
  static int NR_ENTRIES = 10000;  // Note that this is no constant.
                                  // If MY_TABLE exists, NR_ENTRIES
                                  // will be determined and changed 
                                  // accordingly.
	
	// Name of the table where the scheduling results will be stored.
	
  static final String RESULT_TABLE = "resultTable";

  public static void main(String[] args) {
    
	Connection connection = null;
	Statement statement = null;
	DatabaseMetaData metadata = null;
	ResultSet result = null;

	try {

			// Open a connection. The JDBC driver is automatically loaded 
			// in our setting . 
  		
		 	System.out.println("Connecting to MySQL-server ...");
		  	connection = DriverManager.getConnection(DB_URL, USER, PASS); 
			System.out.println("Done.");
  			
			// Connect to the database MY_DB if it exists, otherwise create it
			// beforehand.

			System.out.println("Creating database "+MY_DB+ 
					" if it does not exist and using it as default/current database ...");

			statement = connection.createStatement();
  			
		 	String sql = "CREATE DATABASE IF NOT EXISTS "+MY_DB;
			statement.executeUpdate(sql);
			sql = "USE "+MY_DB;
			statement.executeUpdate(sql);
			System.out.println("Done.");

			// If it not exists, create a table with the name MY_TABLE. 
			// This table will have NR_ENTRIES many tuples, each 
			// tuple consisting of a random pair of integers t1,t2 of integers
			// with TMIN <= t1 < t2 <= TMAX.

			metadata = connection.getMetaData();
			result = metadata.getTables(MY_DB, null, MY_TABLE, null);
			boolean createTable = !result.next();
 			
		 	if (createTable) {
				System.out.print("A table with the name "+MY_TABLE+" is not present. ");
				System.out.print("We will create one with "+NR_ENTRIES+" tuples, each tuple containing a pair t1,t_2 ");
				System.out.print("of integers with "+TMIN+" <= "+"t1"+" < "+"t2"+" <= "+TMAX+", press ENTER to continue ...");
				System.in.read();

				System.out.println("Creating table "+MY_TABLE+" ...");
				sql = "CREATE TABLE "+MY_TABLE+
								"("+PRIMARY_ID+" INT NOT NULL, " +
								" "+T1+" INT NOT NULL, " +
								" "+T2+" INT NOT NULL, " +
								" PRIMARY KEY ( "+PRIMARY_ID+" ))";
		  		statement.executeUpdate(sql);
				System.out.println("Done.");
  				
				System.out.println("Inserting the tuples ...");
				Random randGenerator = new Random();
				int t1 = 0;
				int t2 = 0;

				// Since we have lots of data to write, we create a large string
				// and write the data in our table using only one statement. 
				// This is much faster than writing one row at a time.

				sql = "INSERT INTO "+ MY_TABLE + " (" +
						PRIMARY_ID+","+T1+","+T2+") VALUES";
				for (int i = 0; i < NR_ENTRIES; i++) {
					t1 = randGenerator.nextInt(TMAX-TMIN) + TMIN;
					t2 = randGenerator.nextInt(TMAX-t1) + t1 + 1;
					sql = sql +
						  "(" + Integer.toString(i) + " ," +
						     Integer.toString(t1) + " ," +
					         Integer.toString(t2) + ")";
					if (i < NR_ENTRIES - 1) {
						sql = sql + ",";                   
					}
				}
				statement.executeUpdate(sql);
				System.out.println("Done.");
			}
			  else {
			  	System.out.println("The table "+MY_TABLE+" already exists. Finding out the number of rows ... ");
        
			 	// The number of rows.

				sql = "SELECT COUNT(*) FROM "+MY_TABLE;
			 	result = statement.executeQuery(sql);

			 	if (result.next()) {
				 	NR_ENTRIES = result.getInt("COUNT(*)");
				 	System.out.println("The number is "+ Integer.toString(NR_ENTRIES)+".");
			 	}
			}

		 	// Now we read the time data out of our MY_TABLE and use
			// our scheduler. We compute a schedule and store it in the table
			// RESULT_TABLE. Let us first count how many rows our table has:

			System.out.println("Reading out the time frames t1,t2 from our table " 
				+ MY_TABLE + 
				" and prepareing the data for further computations with our scheduler ...");

			// In the following list, we store what we read out. 

			List<TimeFrameBound> tFKS = new ArrayList<TimeFrameBound>();
 			
			// Let us read out the data.

			sql = "SELECT "+PRIMARY_ID+","+T1+","+T2+" FROM "+MY_TABLE;
			result = statement.executeQuery(sql);

			while (result.next()){	
				int primary = result.getInt(PRIMARY_ID);
				tFKS.add(new TimeFrameBound(primary, result.getInt(T1),1));
				tFKS.add(new TimeFrameBound(primary, result.getInt(T2),-1));
		 	}
 			
			System.out.println("Done.");

			// Now we apply our own scheduler and store the results.

			System.out.println("Computing a schedule ...");
 
		 	Scheduler myScheduler = new Scheduler();
			myScheduler.settFKS(tFKS);
			int[] computation = myScheduler.computeSchedule();

			System.out.println("Done.");

			System.out.println("Creating table "+RESULT_TABLE+","+ 
				" dropping the old version of it if it exists,"+
				" and storing scheduling results ...");

			sql = "DROP TABLE IF EXISTS "+RESULT_TABLE;
			statement.executeUpdate(sql);

			sql = "CREATE TABLE "+RESULT_TABLE+
					"(resultId INT NOT NULL, " +
					" workerId INT NOT NULL, " +
					" PRIMARY KEY ( resultId ))";
			statement.executeUpdate(sql);

  			
			sql = "INSERT INTO "+ RESULT_TABLE + " (" +
						"resultId, workerId) VALUES";
  			
			for (int i = 0; i < NR_ENTRIES; i++) {
			 	sql = sql +
					 "(" + Integer.toString(i) + " ," +
				  Integer.toString(computation[i]) + ")"; 
				if (i < NR_ENTRIES - 1) {
					sql = sql + ",";                   
				}
			}                  
			statement.executeUpdate(sql);	
			System.out.println("Done.");	

			// As a final demonstration: We compute how many workers were necessary
			// and for a particular time frame the worker we plan should work at
			// at that frame:

			System.out.print("We have tasks distributed over "+
				Integer.toString(NR_ENTRIES)+
				" time frames. Computing the number of workers we need for those ... ");

			sql = "SELECT MAX(workerId) AS workerId FROM "+ RESULT_TABLE;
			result = statement.executeQuery(sql);

			if (result.next()) {
				System.out.println("The number is "+
  					Integer.toString(result.getInt("workerId"))+".");
		 	}

			// End of of our demo.
		}
		catch(SQLException se){

			// Deal with errors from the JDBC.
  			
			se.printStackTrace();
		} 
		catch(IOException ioe){
		 	ioe.printStackTrace();
  		} 
		finally{

			//close resources.
  		
				try{
					if(statement != null)
					connection.close();
			}
			catch(SQLException se){
			}// do nothing
			try{
				if(connection != null)
					connection.close();
			}
			catch(SQLException se){
				se.printStackTrace();
			} //end finally try
		}
	}
}
