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

    public void init() {
         users = new Users();
         availableLessons = new LessonTimetable();
         // TODO Attach the lesson timetable to an appropriate scope
         
        
    }
    
    public void destroy() {
        
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
        System.out.println("Controller started");
        String action = request.getPathInfo();
        System.out.println("Action: "+action);
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
        HttpSession session = request.getSession(false);
        
        String username = request.getParameter("username");
        
        if (action.equals("/login")){
            Integer userID = users.isValid(username, request.getParameter("password"));
            if (userID != -1) {
                System.out.println("Valid user login: "+username);
                session = request.getSession();
                session.setAttribute("user", username);
                session.setAttribute("selectedLessons", new LessonSelection(userID));
                session.setAttribute("lessons", availableLessons.getLessons());
                dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
            } else {
                dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
            }
        } else if (session.getAttribute("user") != null) {
            
            if (action.equals("/viewTimetable")) {
                dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
            } 
            else if (action.equals("/viewSelection")) {
                dispatcher = this.getServletContext().getRequestDispatcher("/LessonSelectionView.jspx");
            } 
            else if (action.equals("/chooseLesson")) {
                String lessonID = request.getParameter("id");
                Lesson selectedLesson = this.availableLessons.getLesson(lessonID);
                LessonSelection sel = (LessonSelection)session.getAttribute("selectedLessons");

                // Check lesson isn't already selected
                if (! sel.getChosenLessons().containsKey(lessonID)){
                    sel.addLesson(selectedLesson);
                }
                dispatcher = this.getServletContext().getRequestDispatcher("/LessonSelectionView.jspx");
            }
            else if (action.equals("/finaliseBooking")){
                LessonSelection sel = (LessonSelection)session.getAttribute("selectedLessons");
                System.out.println("Finalising login for " + Integer.toString(sel.getOwner()));
                sel.updateBooking();
                dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
            }
            else if (action.equals("/logOut")) {
                dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
                session.invalidate();
            }
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
