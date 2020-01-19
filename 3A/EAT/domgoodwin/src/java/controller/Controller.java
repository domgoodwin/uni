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
         this.getServletContext().setAttribute("availableLessons", availableLessons);
         
        
    }
    
    public void destroy() {
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
        System.out.println("Controller started");
        String action = request.getPathInfo();
        System.out.println("Action: "+action);
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
        HttpSession session = request.getSession(false);
        
        if (action.equals("/login") && users.isValid(request.getParameter("username"), request.getParameter("password")) != -1){
            Integer userID = users.isValid(request.getParameter("username"), request.getParameter("password"));
            if (userID != -1) {
                System.out.println("Valid user login: "+request.getParameter("username"));
                session = request.getSession();
                session.setAttribute("user", request.getParameter("username"));
                LessonSelection sel = new LessonSelection(userID);
                session.setAttribute("selectedLessons", sel);
                
                session.setAttribute("lessons", availableLessons.getLessons());
                dispatcher = this.getServletContext().getRequestDispatcher("/do/viewTimetable");
            }
        }else if (session == null){
            dispatcher = this.getServletContext().getRequestDispatcher("/do/viewTimetable");
        } 
        else if (session.getAttribute("user") != null) {
            if (action.equals("/logOut")) {
                session.invalidate();
                dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
                request.getSession(false);
            } else {
                LessonSelection sel = (LessonSelection)session.getAttribute("selectedLessons");
                session.setAttribute("selectedCount", sel.getNumChosen());
                if (action.equals("/viewTimetable")) {
                    session.setAttribute("selectedCount", sel.getNumChosen());
                    dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
                } 
                else if (action.equals("/viewSelection")) {
                    session.setAttribute("selectedCount", sel.getNumChosen());
                    System.out.println("Number selected: "+Integer.toString(sel.getNumChosen()));
                    dispatcher = this.getServletContext().getRequestDispatcher("/LessonSelectionView.jspx");
                } 
                else if (action.equals("/chooseLesson")) {
                    String lessonID = request.getParameter("id");
                    Lesson selectedLesson = this.availableLessons.getLesson(lessonID);

                    // Check lesson isn't already selected
                    if (! sel.getChosenLessons().containsKey(lessonID)){
                        sel.addLesson(selectedLesson);
                    }
                    dispatcher = this.getServletContext().getRequestDispatcher("/LessonSelectionView.jspx");
                }
                else if (action.equals("/finaliseBooking")){
                    System.out.println("Finalising login for " + Integer.toString(sel.getOwner()));
                    sel.updateBooking();
                    dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
                }
                else if (action.equals("/cancelSelection")){
                    sel.removeLesson(request.getParameter("id"));
                    dispatcher = this.getServletContext().getRequestDispatcher("/LessonSelectionView.jspx");
                }
                else if (action.equals("/logOut")) {
                    dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
                    session.invalidate();
                }
            }

        } else {
            session.invalidate();
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
