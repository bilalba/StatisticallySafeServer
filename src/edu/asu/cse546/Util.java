package edu.asu.cse546;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Util {
	static Connection conn = null;
	static String[] types = {"Theft","Arrest","Other","Assault", "Burglary", "Robbery", "Arson","Vandalism","Shooting"};
	static String url =  "jdbc:google:mysql://cobalt-mind-162219:us-central1:statisticallysafedb/UpdatedCrimeDB?user=root&amp;password=";
	
	
	
	// THIS CODE IS COPIED.
	//http://stackoverflow.com/questions/7764671/java-jdbc-connection-status
	public static boolean isDbConnected(Connection conn) {
	    final String CHECK_SQL_QUERY = "SELECT 1";
	    boolean isConnected = false;
	    try {
	        conn.prepareStatement(CHECK_SQL_QUERY);
	        isConnected = true;
	    } catch (Exception e) {
	    	return false;
	        // handle SQL error here!
	    }
	    return isConnected;
	}
	public static void initialize() {
		if (conn != null){
			if (isDbConnected(conn))
				return;
		}	
		try {
			 conn = DriverManager.getConnection(url);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
		
	
	public static int countInRadius(PrintWriter wr,float lat1, float lon1, float radius, String condition) {
		initialize();
		try {
			Statement stmt = conn.createStatement();
			double[] edges = getEdges(lat1, lon1, radius);
			
			String statement = "SELECT COUNT(*) as rowcount from crimes WHERE lat BETWEEN " +
					edges[1]+ " and " + edges[0]+ " and lon BETWEEN " + edges[3] + " and " +edges[2] +"";
			if (condition != "")
				statement += " and " + condition;
			ResultSet rs = stmt.executeQuery(statement);
			rs.next();
			return rs.getInt("rowcount");
		} catch(Exception e) {
			e.printStackTrace(wr);
			return -1;
		}
		
	}
	public static double[] getEdges(float lat1, float lon1, float d) {
		d /= 3958.756;
		lat1 /= 180;
		lat1 *= Math.PI;
		lon1 /= 180;
		lon1 *= Math.PI;
		double bottom_lat= Math.asin(Math.sin(lat1)*Math.cos(d)+Math.cos(lat1)*Math.sin(d)*Math.cos(Math.PI));
		double top_lat= Math.asin(Math.sin(lat1)*Math.cos(d)+Math.cos(lat1)*Math.sin(d)*Math.cos(0));
		double left_lon = ((lon1-Math.asin(Math.sin(3*Math.PI/2.0d)*Math.sin(d)/Math.cos(lat1))+Math.PI) % (2*Math.PI))-Math.PI;
		double right_lon = ((lon1-Math.asin(Math.sin(Math.PI/2.0d)*Math.sin(d)/Math.cos(lat1))+Math.PI) % (2*Math.PI))-Math.PI;
		return new double[]{top_lat*180/Math.PI,bottom_lat*180/Math.PI,left_lon*180/Math.PI, right_lon*180/Math.PI};
	}

}
