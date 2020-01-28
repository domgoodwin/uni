/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.LessonTimetable;
import model.LessonSelection;
import model.Users;
import model.Lesson;


/**
 *
 * @author bastinl
 */
public class Controller extends HttpServlet {

   private Users users;
   private LessonTimetable availableLessons;

    // Get available lessons and store with application scope
    public void init() {
         users = new Users();
         availableLessons = new LessonTimetable();
         this.getServletContext().setAttribute("availableLessons", availableLessons);
         
        
    }
    
    // On tomcat shutdown make sure to cleanup objects
    public void destroy() {
        availableLessons.cleanUp();
        availableLessons = null;
        users.cleanUp();
        users = null;
        if (availableLessons != null){
            availableLessons.cleanUp();
        }
        availableLessons = null;
        
    }

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (availableLessons == null) {
            availableLessons = new LessonTimetable();
        }
        System.out.println("Controller started");
        String action = request.getPathInfo();
        System.out.println("Action: "+action);
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
        HttpSession session = request.getSession(false);
        
        // Login action (a)
        if (action.equals("/login")){
            Integer userID = users.isValid(request.getParameter("username"), request.getParameter("password"));
            // -1 used as invalid user
            if (userID != -1) {
                // Setup session and objects
                System.out.println("Valid user login: "+request.getParameter("username"));
                session = request.getSession();
                session.setAttribute("user", request.getParameter("username"));
                session.setAttribute("userID", userID);
                LessonSelection sel = new LessonSelection(userID);
                session.setAttribute("selectedLessons", sel);
                dispatcher = this.getServletContext().getRequestDispatcher("/do/viewTimetable");
            } else {
                // If login failed just go back to login page
                dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
            }
        // Check session exists and user set before trying any other actions
        }else if (session == null || session.getAttribute("user").equals("")){
            dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
        } 
        else if (session.getAttribute("user") != null) {
            // Log out user and invalidate session
            if (action.equals("/logOut")) {
                System.out.println("Logging user out");
                session.invalidate();
                dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
                request.getSession(false);
            } else {
                // Most actions use Lessonselection so get out here
                LessonSelection sel = (LessonSelection) session.getAttribute("selectedLessons");
                if (sel != null) {
                    Integer numChosen = sel.getNumChosen();
                    session.setAttribute("selectedCount", numChosen);
                }
                // Render base view timetable page, lessons use application scope
                if (action.equals("/viewTimetable")) {
                    dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
                } 
                // Render view selection page, uses selectedLessons from session
                else if (action.equals("/viewSelection")) {
                    System.out.println("Number selected: "+Integer.toString(sel.getNumChosen()));
                    dispatcher = this.getServletContext().getRequestDispatcher("/LessonSelectionView.jspx");
                } 
                // Add a lesson to users selected lesson object in session
                else if (action.equals("/chooseLesson")) {
                    String lessonID = request.getParameter("id");
                    Lesson selectedLesson = this.availableLessons.getLesson(lessonID);

                    // Check lesson isn't already selected
                    if (! sel.getChosenLessons().containsKey(lessonID)){
                        sel.addLesson(new Lesson(selectedLesson));
                    }
                    session.setAttribute("selectedLessons", sel);
                    dispatcher = this.getServletContext().getRequestDispatcher("/LessonSelectionView.jspx");
                }
                // Finalise booking by writing to DB
                else if (action.equals("/finaliseBooking")){
                    System.out.println("Finalising booking");
                    if (sel != null) {
                        sel.updateBooking();
                    } else {
                        System.out.println("Selection was null");
                    }
                    Integer userID = (Integer) session.getAttribute("userID");
                    LessonSelection selNew = new LessonSelection(userID);
                    session.setAttribute("selectedLessons", selNew);
                    dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
                }
                // Remove lesson from users selected session object
                else if (action.equals("/cancelSelection")){
                    sel.removeLesson(request.getParameter("id"));
                    session.setAttribute("selectedLessons", sel);
                    dispatcher = this.getServletContext().getRequestDispatcher("/LessonSelectionView.jspx");
                }
                // Invalidate session to log user out
                else if (action.equals("/logOut")) {
                    dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
                    session.invalidate();
                }
            }
        // If user isn't set and log otu request ensure user logged out anyway
        } else if (action.equals("/logOut")) {
            System.out.println("Logging user out and invalidating session");
            if (session != null){
              session.invalidate();  
            }
            dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
        }
        
        dispatcher.forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
