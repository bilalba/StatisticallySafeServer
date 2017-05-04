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

@SuppressWarnings("serial")
public class getOverview extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,ServletException {
		String lat = req.getParameter("lat");
		String lon = req.getParameter("lon");
		String radius = req.getParameter("radius");
		String time = req.getParameter("time");
		if (lat == null || lon ==null || radius ==null || time == null){
			resp.getWriter().println("You did not send anything");
			return;
		}
		try {
			float lat_flt = Float.parseFloat(lat);
			float lon_flt = Float.parseFloat(lon);
			float radius_flt = Float.parseFloat(radius);
			int total_crime = Util.countInRadius(resp.getWriter(),lat_flt, lon_flt, radius_flt, "");
			String time_condition = "time BETWEEN " + Integer.toString(13*60) +" and "+Integer.toString(15*60);
			int crime_in_timerange = Util.countInRadius(resp.getWriter(),lat_flt, lon_flt, radius_flt, time_condition);
			HashMap<String, Integer> type_frq = new HashMap<String, Integer>();
			resp.getWriter().println("total:" + total_crime);
			int count = 0;
			for (String t: Util.types){
				type_frq.put(t, Util.countInRadius(resp.getWriter(),lat_flt, lon_flt, radius_flt, "type = '"+t+"'"));
				resp.getWriter().println(t+ ":"+type_frq.get(t));
			}
			count = 0;
			HashMap<String, Integer> type_frq_bytime = new HashMap<String, Integer>();
			for (String t: Util.types){
				type_frq_bytime.put(t, Util.countInRadius(resp.getWriter(),lat_flt, lon_flt, radius_flt, "type = '"+t+"' and "+ time_condition));
				resp.getWriter().println(t+ ":"+type_frq_bytime.get(t));
				count += type_frq_bytime.get(t);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}