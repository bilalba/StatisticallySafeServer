package edu.asu.cse546;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.labs.repackaged.org.json.JSONArray;

@SuppressWarnings("serial")
public class getTimeData extends HttpServlet {
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
				e1.printStackTrace();
			}
			
			
			resp.getWriter().println(j.toString());
			e.printStackTrace();
			return;
			
		}
		int[] total_freq = new int[24];
		for (int i = 0; i < 24; i++) {
			total_freq[i] = Util.countInRadius(resp.getWriter(),lat_flt, lon_flt, radius_flt, "time >= " +i*60+ " and time < "+(i+1)*60);
			System.out.println("Hour:" + i+ " freq:"+ total_freq[i]);
		}
		int[][] type_freq = new int[Util.types.length][24];
		for (int i = 0; i < Util.types.length; i++) {
			String t = Util.types[i];
			System.out.println(t);
			for (int jx = 0; jx < 24; jx++) {
				type_freq[i][jx] = Util.countInRadius(resp.getWriter(),lat_flt, lon_flt, radius_flt, "type ='"+ t +"' and time >= " +jx*60+ " and time < "+(jx+1)*60);
				System.out.println("Hour:" + j+ " freq:"+ type_freq[i][jx]);
			}
		}
		try {
			j.put("total", total_freq);
			j.put("types", Util.types);
			j.put("radius", radius);
			for (int i = 0; i < Util.types.length; i++) {
				j.put(Util.types[i], type_freq[i]);
			}
		} catch (Exception e) {}
		resp.getWriter().println(j);
	}
}

//public static String getTimeData(String lat, String lon, String radius) {
//	float lat_flt = 0;
//	float lon_flt = 0;
//	float radius_flt = 0;
//	try {
//		lat_flt = Float.parseFloat(lat);
//		lon_flt = Float.parseFloat(lon);
//		radius_flt = Float.parseFloat(radius);
//	} catch (Exception e) {
//		e.printStackTrace();
//	}
//	int[] total_freq = new int[24];
//	for (int i = 0; i < 24; i++) {
//		total_freq[i] = countInRadius(lat_flt, lon_flt, radius_flt, "time BETWEEN " +i*60+ " and "+(i+1)*60);
//		System.out.println("Hour:" + i+ " freq:"+ total_freq[i]);
//	}
//	int[][] type_freq = new int[types.length][24];
//	for (int i = 0; i < types.length; i++) {
//		String t = types[i];
//		System.out.println(t);
//		for (int j = 0; j < 24; j++) {
//			type_freq[i][j] = countInRadius(lat_flt, lon_flt, radius_flt, "type ='"+ t +"' and time BETWEEN " +j*60+ " and "+(j+1)*60);
//			System.out.println("Hour:" + j+ " freq:"+ type_freq[i][j]);
//		}
//	}
//	return "";
//}
