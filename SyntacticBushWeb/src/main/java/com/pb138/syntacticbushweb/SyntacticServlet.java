/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pb138.syntacticbushweb;

import com.pb138.syntacticbush.Sentence;
import com.pb138.syntacticbush.SyntacticBush;
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
    private SyntacticBush bush = new SyntacticBush(SyntacticBush.getFirstText());
    private Sentence currentSentence = bush.firstSentence();

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

    private void showSentence(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setAttribute("currentSentence", bush.documentToString(currentSentence.createDocument()));
            request.setAttribute("indexOfCurrentSentence", bush.displayCurrentSentence(currentSentence));
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
                    currentSentence = bush.prevSentence(currentSentence);
                    response.sendRedirect(request.getContextPath() + URL_MAPPING);
                    return;
                } catch (IOException | ArrayIndexOutOfBoundsException e) {
                    showSentence(request, response);
                }
            case "/next":
                try {
                    currentSentence = bush.nextSentence(currentSentence);
                    response.sendRedirect(request.getContextPath() + URL_MAPPING);
                    return;
                } catch (IOException | ArrayIndexOutOfBoundsException e) {
                    showSentence(request, response);
                }
            case "/goto":
                int to = Integer.parseInt(request.getParameter("gotoField"));
                try{
                    currentSentence = bush.goToSentence(to);
                    response.sendRedirect(request.getContextPath() + URL_MAPPING);
                    return;
                } catch (IOException | ArrayIndexOutOfBoundsException e) {
                    showSentence(request, response);
                } 
        }
    }

}
