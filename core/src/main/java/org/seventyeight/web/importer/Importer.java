package org.seventyeight.web.importer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.seventyeight.web.Core;
import org.seventyeight.web.nodes.User;

public class Importer {
	
	@Option(name="-type", required=true)
	private String type;
	
	public static class UserImport {
		
		private String username;
		private String password;
		private String database;
		
		public UserImport(String database, String username, String password) {
			this.database = database;
			this.username = username;
			this.password = password;
		}
		
		public void userImport() throws SQLException, ClassNotFoundException {
			Class.forName("com.mysql.jdbc.Driver");
			String db = "jdbc:mysql://" + database;

			Connection con = DriverManager.getConnection(db, username, password);
			Statement stmt = con.createStatement();
		    
			ResultSet rs = stmt.executeQuery("SELECT * FROM users");
		    while(rs.next()) {
		    	String u = rs.getString("username");
		    	String p = rs.getString("password");
		    	String e = rs.getString("email");
		    	System.out.println("Username; " + u);
		    	
		    	//User user = User.n
		    }
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Importer i = new Importer();
		CmdLineParser parser = new CmdLineParser(i);
		try {
			parser.parseArgument(args);
			i.execute();
		} catch (CmdLineException e) {
			// handling of wrong arguments
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
		}
	}
	
	private List<Provider> providers = new ArrayList<Provider>();
	
	private Core core;
	
	public Importer() {
	}
	
	public Importer(Core core) {
		this.core = core;
	}
	
	public Importer addProvider(Provider provider) {
		this.providers.add(provider);
		return this;
	}
	
	public void execute() throws ClassNotFoundException, SQLException {
		//UserImport ui = new UserImport("mydb5.surftown.dk:3306/cwolfga_gymnerds", "cwolfga_admin", "avenger");
		UserImport ui = new UserImport("212.97.132.75:3306/cwolfga_gymnerds", "cwolfga_admin", "avenger");
		ui.userImport();
	}
}
