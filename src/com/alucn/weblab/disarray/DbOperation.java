package com.alucn.weblab.disarray;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.alucn.casemanager.server.common.util.ParamUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DbOperation {


    private static JSONArray GetCaseInf(Set<String> Second_Date,String CaseDependedInfos,String DailyCase)
    {
    	//get case information
    	Connection connection2=null;
    	PreparedStatement pStatement2=null;
    	ResultSet result2=null;
        
        JSONArray jsonArray=new JSONArray();
       // String DbpathCaseIf=CaseDependedInfos; //database path of case information
        Set<String> Base_Date=new HashSet<String>();
        JSONObject jsonObject=GetDtfInf(Base_Date,DailyCase);
        Set<String> set=jsonObject.keySet();
      try
      {
    	 Class.forName("org.sqlite.JDBC");
    	// connection2=DriverManager.getConnection("jdbc:sqlite:"+DbpathCaseIf);
    	 connection2=DriverManager.getConnection("jdbc:sqlite:"+CaseDependedInfos);
    	 pStatement2=connection2.prepareStatement("select * from CaseDepends where case_name LIKE (?)");
        
    	 for(String key:set)
    	 {
    		 String str=key;
    		 //System.out.println(key);
    		 pStatement2.setString(1, str);
    		 result2=pStatement2.executeQuery();
    		 while(result2.next())
    		 {
    			 JSONObject jsoncase=new JSONObject();
    			 String casename=result2.getString("case_name");
    			 String spa=result2.getString("SPA");
    			 String db=result2.getString("DB");
    			 String base_date=jsonObject.getString(key);
    			 String secondDate=result2.getString("SecData");  //this date has not support
    			 jsoncase.put("second_date", secondDate);
    			 if(!secondDate.equals(""))
    			 {
    	          Second_Date.add(secondDate);
    			 }
    			 jsoncase.put("base_date", base_date);
    			 jsoncase.put("case_name", casename);
    			 JSONArray spArray=new JSONArray();
    			 String[] spaname=spa.split(",");
    			 List<String> list2=Arrays.asList(spaname);
    			 for(String s:list2)
    			 {
    				 spArray.add(s);
    			 }
    			 jsoncase.put("SPA", spArray);
    			 
    			 JSONArray dbArray=new JSONArray();
    			 String[] dbname=db.split(",");
    			 List<String> list3=Arrays.asList(dbname);
    			 for(String s2:list3)
    			 {
    				 dbArray.add(s2);
    			 }
    			 jsoncase.put("RTDB", dbArray);
    			 jsonArray.add(jsoncase);
    		 
    		 }
    	 }
    	 
      }
      catch(SQLException e4)
      {
    	  e4.printStackTrace();
          System.out.println("sql has problems");
      }
      catch(Exception e5)
      {
    	  e5.printStackTrace();
    	//  System.out.println("can not find the case information in DB");
      }
      
      finally
      {
    	  try
    	  {
    	    if(pStatement2!=null)
    	   {
    	 	  pStatement2.close();
    	   }
    	    if(connection2!=null)
    	   {
    		  connection2.close();
    	   }
          } 
    	  catch(SQLException e6)
    	  {
    		  e6.printStackTrace();
    	  }
    	  
      }
      return jsonArray;
    }
    
    private static JSONObject GetDtfInf(Set<String> Base_Date,String DailyCase )
    {
    	SimpleDateFormat  simple=new SimpleDateFormat("yyyy-MM-dd");
    	Date date=new Date();
    	String datestring=simple.format(date);	
    	
    	Connection connection=null;
    	Statement state=null;
    	Connection connection2=null;
        Statement state2=null;
    	//String Dbpath="C:\\Sqlite\\DailyCase.db";  //database path
    	//String Dbpath=DailyCase;
    	//ArrayList<String> caseName=new ArrayList<String>();
    	JSONObject caseName=new JSONObject();
    	//JSONArray caseSearch=new JSONArray();
    	try
    	{
    	 Class.forName("org.sqlite.JDBC");
    	 //connection=DriverManager.getConnection("jdbc:sqlite:"+Dbpath);
    	 String CaseInfoDB=ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
    	 connection=DriverManager.getConnection("jdbc:sqlite:"+CaseInfoDB);
    	 state = connection.createStatement();
    	 String query_sql = "select case_name from DistributedCaseTbl;";
    	 ResultSet result2=state.executeQuery(query_sql);
    	 JSONArray case_name_list = new JSONArray();
         while(result2.next())
         {
             String Case_name=result2.getString("case_name");
             case_name_list.add(Case_name);
         }
    	 
    	 connection2=DriverManager.getConnection("jdbc:sqlite:"+DailyCase);
    	 state2 = connection2.createStatement();
    	 query_sql = "select * from DailyCase where mate = 'N' and lab_number = '1' and special_data = 'N' and case_status = 'I' and submit_date<(" 
    	             + datestring + ") ORDER BY lab_number, base_data, customer, feature_number, special_data, case_name ASC;";
    	 
    	 if(case_name_list.size() > 0)
    	 {
    	     query_sql += "and case_name not in (";
    	     for(int i =0; i < case_name_list.size(); i++)
    	     {
    	         query_sql += "'" + case_name_list.getString(i) + "',";
    	     }
    	     query_sql = query_sql.substring(0,query_sql.length()-1) + ")";
    	 }
    	 query_sql += ";";
         
    	 ResultSet result=state2.executeQuery(query_sql);
    	 while(result.next())
    	 {
    		 String Case_name=result.getString("case_name");
    		 String Case_baseDB=result.getString("base_data");
    		 Base_Date.add(Case_baseDB);
    		 caseName.put(Case_name, Case_baseDB);
    		 
    	 }
    	 
    	 
    	}
    	catch(SQLException e1)
    	{
    		e1.printStackTrace();
            System.out.println("sql has problems");
    	}
    	catch(Exception e2)
    	{
    		e2.printStackTrace();
    		
    	}
    	finally
    	{
    		try
    		{
    			if(state!=null)
    			{
    				state.close();
    			}
    			if(connection!=null)
    			{
    				connection.close();
    			}
    			if(state2!=null)
                {
                    state2.close();
                }
                if(connection2!=null)
                {
                    connection2.close();
                }
    		}
    		catch(SQLException e3)
    		{
    			
    			e3.printStackTrace();
    		}
    	 
    	}	
    	return caseName;
    
    }
    
    private static JSONObject SortedCase(JSONArray jsonArray,Set<String>Base_Date,Set<String>Second_Date )
    {
    	JSONObject lastResult=new JSONObject();
    	JSONArray lastArray=new JSONArray();
    
    	for(String tempbasedate:Base_Date)
    	{
    
    	JSONArray Array=new JSONArray();
    	for(int n=0;n<jsonArray.size();n++)
    	   {
    		JSONObject jsonObject3=jsonArray.getJSONObject(n);
    		String basedt=jsonObject3.getString("base_date");
    		String secondt=jsonObject3.getString("second_date");
    		if(tempbasedate.equals(basedt) && "".equals(secondt) )
    		 {
    //			jsonObject3.remove("SPA");
    //			jsonObject3.remove("RTDB");
    //			jsonObject3.remove("case_name");
    
    			Array.add(jsonObject3);
    		 }
    	   }
    	  if(Array.size()>0)
    	  {
    	  lastArray.add(Array);
    	  }
    	 }
    	
    
        for(String baseDateName:Base_Date)
        {
        	for(String secondDateName:Second_Date)
        	{
        		JSONArray Array=new JSONArray();
        	//	JSONArray obJsonArray=new JSONArray();
        		for(int i=0;i<jsonArray.size();i++)
        		{
        			JSONObject object2=jsonArray.getJSONObject(i);
        			if(baseDateName.equals(object2.get("base_date")) && secondDateName.equals(object2.get("second_date")))
        			{
    //    				object2.remove("SPA");
    //    				object2.remove("RTDB");
    //    				object2.remove("case_name");
        			
        				Array.add(object2);
        			}
        		}
        		if(Array.size()>0)
        		{
        		  lastArray.add(Array);
        		}
        	}
        }
       for(int i=0;i<lastArray.size();i++)
       {
    	  JSONArray tmp=lastArray.getJSONArray(i);
    	  for(int j=0;j<tmp.size();j++)
    	  {
    		  JSONObject tem=tmp.getJSONObject(j);
    		  tem.remove("base_date");
    		  tem.remove("second_date");
    	  }
    	 
       }
        lastResult.put("case_list", lastArray);
        return lastResult;  
    }
     
    public static String GetCaseInf() throws IOException
    {
    	  
    	  String DailyCase=ParamUtil.getUnableDynamicRefreshedConfigVal("DailyCaseDB");
    	  String CaseDependedInfos=ParamUtil.getUnableDynamicRefreshedConfigVal("CaseDependsDB");
    
    	//  System.out.println(DailyCase+" "+CaseDependedInfos);
    	  //System.out.println("C:\\Sqlite\\DailyCase.db");
    
    	  //dbOperation dbOperation=new dbOperation();
    	  Set<String> Base_Date=new HashSet<String>();
    	  JSONObject totalcase=GetDtfInf(Base_Date,DailyCase);
    	  Set<String> Second_Date=new HashSet<String>();
    	  JSONArray jsonArray=GetCaseInf(Second_Date,CaseDependedInfos,DailyCase);
    	  JSONObject result=SortedCase(jsonArray, Base_Date, Second_Date);
    	//  System.out.println(result.toString());
    	  return result.toString();
    }

    public static void DeleteDistributedCase(JSONArray UnNeedServers)
    {
        String CaseInfoDB=ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
        Connection connection=null;
        Statement stat = null;
        try
        {
             Class.forName("org.sqlite.JDBC");
             //connection=DriverManager.getConnection("jdbc:sqlite:"+Dbpath);
             connection=DriverManager.getConnection("jdbc:sqlite:"+CaseInfoDB);
             stat = connection.createStatement();
             String UpdateSql = "Delete from DistributedCaseTbl where lab_ip in (";
             for(int i = 0; i < UnNeedServers.size(); i++)
             {
                 UpdateSql += "'" + UnNeedServers.getString(i) + "', ";
             }
             UpdateSql = UpdateSql.substring(0, UpdateSql.length()-2) + ");";
             
             int Result = stat.executeUpdate( UpdateSql);
         
        }
        catch(SQLException e1)
        {
            e1.printStackTrace();
            System.out.println("sql has problems");
        }
        catch(Exception e2)
        {
            e2.printStackTrace();
            
        }
        finally
        {
            try
            {
                if(stat!=null)
                {
                    stat.close();
                }
                if(connection!=null)
                {
                    connection.close();
                }
            }
            catch(SQLException e3)
            {
                
                e3.printStackTrace();
            }
         
        }
    }
    
    public static void UpdateDistributedCase(JSONArray CaseList, String lab_ip)
    {
        String CaseInfoDB=ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
        Connection connection=null;
        Statement stat = null;
        try
        {
             Class.forName("org.sqlite.JDBC");
             connection=DriverManager.getConnection("jdbc:sqlite:"+CaseInfoDB);
             PreparedStatement prep = connection.prepareStatement(
                     "replace into DistributedCaseTbl(case_name, lab_ip) values (?, ?);");
             
             for(int k = 0; k < CaseList.size(); k++)
             {
                prep.setString(1, CaseList.getString(k));
                prep.setString(2, lab_ip);
                prep.addBatch();
             }
             
             connection.setAutoCommit(false);
             int []num = prep.executeBatch();
             connection.setAutoCommit(true);
         
        }
        catch(SQLException e1)
        {
            e1.printStackTrace();
            System.out.println("sql has problems");
        }
        catch(Exception e2)
        {
            e2.printStackTrace();
            
        }
        finally
        {
            try
            {
                if(stat!=null)
                {
                    stat.close();
                }
                if(connection!=null)
                {
                    connection.close();
                }
            }
            catch(SQLException e3)
            {
                
                e3.printStackTrace();
            }
         
        }
    }

}
