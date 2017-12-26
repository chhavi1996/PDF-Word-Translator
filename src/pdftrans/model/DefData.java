package pdftrans.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.PreparedStatement;

public class DefData {


	 public String[] getDefinition(String word)
	 {
		 String[] def = new String[20];int i=0;
	      try{  
	            String query="Select definition from define where word=?";
	            Class.forName("com.mysql.jdbc.Driver");
	            Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/entries","root","mysql@123");
	           	PreparedStatement stmt =(PreparedStatement) con.prepareStatement(query);
	           	stmt.setString(1, word);
	            ResultSet rs=stmt.executeQuery();
	            
	            
	         
	            
	            while(rs.next())
	            {
	            	def[i++]=rs.getString(1);
	            	//System.out.println(def[i-1]);
	            }
	          
	            con.close();
	            
	         }
	       catch(Exception e)
	         {
	             System.out.println(e.getMessage());
	         }
	      
	      return def;
	 }
	 
	 public static void main(String[] args) {
		 	
		 System.out.println("Start");
		 DefData d=new DefData();
		
		 String [] def=d.getDefinition("interest");
		// System.out.println("Correct");
		 System.out.println("End");
	}
	        
	}
	   
	


