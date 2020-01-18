/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.sql.PreparedStatement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author bastinl
 */
public class LessonSelection  {
    
    private HashMap<String, Lesson> chosenLessons;
    private int ownerID;
    
    private DataSource ds = null;
    
    private ResultSet rs = null;
    private Statement st = null;

    public LessonSelection() {
        super();
        chosenLessons = new HashMap<String, Lesson>();
    }
    
    public LessonSelection(int owner) {
        
        chosenLessons = new HashMap<String, Lesson>();
        this.ownerID = owner;

        // You don't need to make any changes to the try/catch code below
        try {
            // Obtain our environment naming context
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            // Look up our data source
            ds = (DataSource)envCtx.lookup("jdbc/LessonDatabase");
        }
            catch(Exception e) {
            System.out.println("Exception message is " + e.getMessage());
        }
        
        // Connect to the database - this is a pooled connection, so you don't need to close it afterwards
        try {

            Connection connection = ds.getConnection();

             try {

                if (connection != null) {
                  
                    // TODO get the details of any lessons currently selected by this user
                    // One way to do this: create a join query which:
                   // 1. finds rows in the 'lessons_booked' table which relate to this clientid
                   // 2. links 'lessons' to 'lessons_booked' by 'lessonid
                   // 3. selects all fields from lessons for these rows

                   Statement st = connection.createStatement();
                   PreparedStatement pstmt = connection.prepareStatement("select * from lessons_booked lb JOIN lessons l on lb.lessonid = l.lessonid WHERE lb.clientid = ?");
                   pstmt.setInt(1, owner);
                   ResultSet rs = pstmt.executeQuery();
                   while(rs.next()){
                       // (String description, Timestamp startDateTime, Timestamp endDateTime, int level, String id)
                       Lesson les = new Lesson(rs.getString("description"), rs.getTimestamp("startDateTime"), rs.getTimestamp("endDateTime"), rs.getInt("level"), rs.getString("lessonid"));
                       addLesson(les);
                   }
                      
                    
                }

            }catch(SQLException e) {

                System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
            }
        
        
            }catch(Exception e){

                System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
            }
        
    }

    /**
     * @return the items
     */
    public Set <Entry <String, Lesson>> getItems() {
        return chosenLessons.entrySet();
    }

    public void addLesson(Lesson l) {
       
        Lesson i = new Lesson(l);
        this.chosenLessons.put(l.getId(), i);
       
    }

    public Lesson getLesson(String id){
        return this.chosenLessons.get(id);
    }
    
    public int getNumChosen(){
        return this.chosenLessons.size();
    }

    public int getOwner() {
        return this.ownerID;
    }
    
    public HashMap<String, Lesson> getChosenLessons() {
        return this.chosenLessons;
    }
    
    public void updateBooking() {
       
      
        // TODO get a connection to the database as in the method above
        // TODO In the database, delete any existing lessons booked for this user in the table 'lessons_booked'
        // REMEMBER to use executeUpdate, not executeQuery
        // TODO - write and execute a query which, for each selected lesson, will insert into the correct table:
                    // the owner id into the clientid field
                    // the lesson ID into the lessonid field
            
        try {
            // Obtain our environment naming context
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            // Look up our data source
            ds = (DataSource)envCtx.lookup("jdbc/LessonDatabase");
        }
            catch(Exception e) {
            System.out.println("Exception message is " + e.getMessage());
        }
        
        // Connect to the database - this is a pooled connection, so you don't need to close it afterwards
        try {

            Connection connection = ds.getConnection();

             try {

                if (connection != null) {
                  
                    // Delete entries for client id
                    PreparedStatement pstmt = connection.prepareStatement("DELETE FROM lessons_booked WHERE clientid=?");
                    pstmt.setInt(1, this.getOwner());
                    pstmt.executeUpdate();
                   
                    // Add entries for selection
                    String query = "INSERT INTO lessons_booked (clientid, lessonid) VALUES ";
                    Object[] lessonKeys = chosenLessons.keySet().toArray();
                    for (int i=0; i<lessonKeys.length; i++) {
                        String lessonID = (String)lessonKeys[i];
                        // Temporary check to see what the current lesson ID is....
                        System.out.println("Lesson ID is : " + lessonID);
                        String entry = "( " + Integer.toString(this.getOwner()) + ",'" + lessonID +  "' ),";
                        query += entry;
                    }
                    query = query.substring(0, query.length() - 1);
                    
                    Statement st = connection.createStatement();
                    System.out.println(query);
                    st.executeUpdate(query);
                }

            }catch(SQLException e) {

                System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
            }
        
        
            }catch(Exception e){

                System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
            }
              
        
    }

}
