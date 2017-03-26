package com.lastfm.view;

import com.lastfm.service.LastFmService;
import com.lastfm.service.LastFmServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class LastFmArtistsServlet extends HttpServlet {
    private LastFmService service = new LastFmServiceImpl();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.getRequestDispatcher("artists.jsp").forward(request, response);

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        String country = request.getParameter("country");
        String page = request.getParameter("page");

        String jsonResponse = service.getPopularArtists(country, page);

        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
}
