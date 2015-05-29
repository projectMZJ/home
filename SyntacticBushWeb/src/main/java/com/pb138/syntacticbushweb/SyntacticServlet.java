/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pb138.syntacticbushweb;

import com.pb138.syntacticbush.Evaluation;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Game
 */
@WebServlet(SyntacticServlet.URL_MAPPING + "/*")
public class SyntacticServlet extends HttpServlet {

    private static final String LIST_JSP = "/list.jsp";
    public static final String URL_MAPPING = "/bush";
    private String currentImage = "";
    private WebBush webBush = new WebBush();
    private Evaluation eval;
    private String data;
    

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        showSentence(request, response);
    }
    
    
    /**
     * Shows parameters, used in get method
     * @param request
     * @param response 
     */
    private void showSentence(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setAttribute("currentSentence", currentImage);
            request.setAttribute("indexOfCurrentSentence", webBush.displayingSentence(eval, data));
            request.setAttribute("data", data);
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }

    }
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String action = request.getPathInfo();
        switch (action) {
            case "/prev":
                try {
                    eval = webBush.prevSentence(data, eval);
                    currentImage = webBush.documentToString(eval.createDocument());
                    response.sendRedirect(request.getContextPath() + URL_MAPPING);
                    return;
                } catch (IOException | ArrayIndexOutOfBoundsException | NullPointerException ex) {
                    showSentence(request, response);
                }
            case "/next":
                try {
                    eval = webBush.nextSentence(data, eval);
                    currentImage = webBush.documentToString(eval.createDocument());
                    response.sendRedirect(request.getContextPath() + URL_MAPPING);
                    return;
                } catch (IOException | ArrayIndexOutOfBoundsException | NullPointerException ex) {
                    showSentence(request, response);
                }
            case "/goto":
                
                try{
                    int to = Integer.parseInt(request.getParameter("gotoField"));
                    eval = webBush.goToSentence(data, to);
                    currentImage = webBush.documentToString(eval.createDocument());
                    response.sendRedirect(request.getContextPath() + URL_MAPPING);
                    return;
                } catch (IOException | ArrayIndexOutOfBoundsException | NumberFormatException ex) {
                    showSentence(request, response);
                }
            case "/select":
                
                try{
                    data = (String)request.getParameter("selection");
                    eval = webBush.firstEvaluation(data);
                    currentImage = webBush.documentToString(eval.createDocument());
                    response.sendRedirect(request.getContextPath() + URL_MAPPING);
                    return;
                }
                catch (Exception ex){
                    showSentence(request, response);
                }
        }
    }

}
