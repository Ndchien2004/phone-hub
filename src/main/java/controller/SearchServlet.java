package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.ProductDTO;
import service.HomeService;
import service.impl.HomeServiceImpl;
import util.PageResult;

import java.io.IOException;

@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {

    private final HomeService homeService = new HomeServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        int page = 1;
        int size = 8;

        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException ignored) {
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            response.sendRedirect("HomeServlet?page=" + page);
            return;
        }

        PageResult<ProductDTO> pageResult = homeService.searchByName(keyword, page, size);
        request.setAttribute("pageResult", pageResult);
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("home.jsp").forward(request, response);
    }
}