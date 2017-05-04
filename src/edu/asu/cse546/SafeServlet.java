package edu.asu.cse546;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class SafeServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,ServletException {
		String instanceConnectionName="cobalt-mind-162219:us-central1:statisticallysafedb";
		//resp.setContentType("text/plain");
		//resp.getWriter().println("Hello, world");
		 String url = "jdbc:google:mysql://cobalt-mind-162219:statisticallysafedb/CrimeDB?user=root";
		String databaseName="CrimeDB";
		url =  "jdbc:google:mysql://cobalt-mind-162219:us-central1:statisticallysafedb/CrimeDB?user=root&amp;password=";
		try {
			Connection connection = DriverManager.getConnection(url);
			resp.getWriter().println("Connected");
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			resp.getWriter().println("Unable to make connection with DB");
			e.printStackTrace(resp.getWriter());
		}
			}
}

