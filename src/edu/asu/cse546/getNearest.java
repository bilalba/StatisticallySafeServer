package edu.asu.cse546;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class getNearest extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,ServletException {
		String lat = req.getParameter("lat");
		String lon = req.getParameter("lon");
		String radius = req.getParameter("radius");
		JSONObject j = new JSONObject();
		if (lat == null || lon ==null || radius ==null){
			
			try {
				j.put("status", "4xx");
				j.put("message","Missing parameters");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resp.getWriter().println(j.toString());
			return;
		}
		float lat_flt = 0;
		float lon_flt = 0;
		float radius_flt = 0;
		try {
			lat_flt = Float.parseFloat(lat);
			lon_flt = Float.parseFloat(lon);
			radius_flt = Float.parseFloat(radius);
		} catch (Exception e) {
			try {
				j.put("status", "4xx");
				j.put("message","Error parsing float");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace(resp.getWriter());
			}
			resp.getWriter().println(j.toString());
			e.printStackTrace(resp.getWriter());
			return;
			
		}
		

		Statement stmt = null;
		try {
			Util.initialize();
			stmt = Util.conn.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace(resp.getWriter());
		}
		double[] edges = Util.getEdges(lat_flt, lon_flt, radius_flt);
		String statement = "SELECT id, lat, lon, type, date, time from crimes WHERE lat BETWEEN " +
				edges[1]+ " and " + edges[0]+ " and lon BETWEEN " + edges[3] + " and " +edges[2] +"";
		ResultSet rs =null;
		try {
			rs = stmt.executeQuery(statement);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace(resp.getWriter());
		}
		try {
			JSONArray crimes = new JSONArray();
			
			while (rs.next()) {
				JSONObject js_crime = new JSONObject();
				js_crime.put("id", rs.getInt("id"));
				js_crime.put("lat", rs.getFloat("lat"));
				js_crime.put("lon", rs.getFloat("lon"));
				js_crime.put("type", rs.getString("type"));
				js_crime.put("time", rs.getInt("time"));
				crimes.put(js_crime);
			}
			j.put("crimes", crimes);
			resp.getWriter().println(j.toString());
		} catch (Exception e) {
			e.printStackTrace(resp.getWriter());
		}
	}
}