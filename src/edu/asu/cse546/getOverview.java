package edu.asu.cse546;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import com.google.appengine.labs.repackaged.org.json.*;

@SuppressWarnings("serial")
public class getOverview extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,ServletException {
		String lat = req.getParameter("lat");
		String lon = req.getParameter("lon");
		String radius = req.getParameter("radius");
		String time = req.getParameter("time");
		JSONObject j = new JSONObject();
		if (lat == null || lon ==null || radius ==null || time == null){
			
			try {
				j.put("status", "4xx");
				j.put("message","did not send anything");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			resp.getWriter().println(j.toString());
			return;
		}
		int hour = 0;
		try {
			hour = Integer.parseInt(time); 
		} catch (Exception e) {
			try {
				j.put("status","4xx");
				j.put("message","time must be int between 0 - 23");
				resp.getWriter().println(j);
				return;
			} catch (Exception ex) {return;}
		}
		
		if (hour < 0 || hour > 23) {
			try {
				j.put("status","4xx");
				j.put("message","time format not correct");
			} catch (Exception e) {
				e.printStackTrace();
			}
			resp.getWriter().println(j);
			return;
		}
		try {
			float lat_flt = Float.parseFloat(lat);
			float lon_flt = Float.parseFloat(lon);
			float radius_flt = Float.parseFloat(radius);
			int total_crime = Util.countInRadius(resp.getWriter(),lat_flt, lon_flt, radius_flt, "");
			j.put("total_crime",total_crime);
			String time_condition = "time >= " + Integer.toString((hour-1)*60) +" and time < "+Integer.toString((hour+1)*60);
			if (hour == 0)
				time_condition = "time >= " + Integer.toString((23)*60) +" or time < "+Integer.toString((1)*60);
			int crime_in_timerange = Util.countInRadius(resp.getWriter(),lat_flt, lon_flt, radius_flt, time_condition);
			j.put("crime_in_timerange",crime_in_timerange);
			HashMap<String, Integer> type_frq = new HashMap<String, Integer>();
			//resp.getWriter().println("total:" + total_crime);
			int count = 0;
			for (String t: Util.types){
				type_frq.put(t, Util.countInRadius(resp.getWriter(),lat_flt, lon_flt, radius_flt, "type = '"+t+"'"));
				///resp.getWriter().println(t+ ":"+type_frq.get(t));
			}
			j.put("crime_by_type", type_frq);
			count = 0;
			HashMap<String, Integer> type_frq_bytime = new HashMap<String, Integer>();
			for (String t: Util.types){
				type_frq_bytime.put(t, Util.countInRadius(resp.getWriter(),lat_flt, lon_flt, radius_flt, "type = '"+t+"' and "+ time_condition));
				//resp.getWriter().println(t+ ":"+type_frq_bytime.get(t));
				count += type_frq_bytime.get(t);
			}
			j.put("crime_by_type_in_timerange", type_frq_bytime);
			
			if (hour == 0)
				j.put("time_range","23:1");
			else
				j.put("time_range",hour-1 + ":" + (hour+1));
			j.put("radius", radius);
			resp.getWriter().println(j);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}