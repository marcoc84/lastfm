package com.lastfm.view;

import com.lastfm.service.LastFmService;
import com.lastfm.service.LastFmServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class LastFmTopTracksServlet extends HttpServlet {
    private LastFmService service = new LastFmServiceImpl();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        String name = request.getParameter("name");

        String jsonResponse = service.getTopTracks(name);

        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
}
