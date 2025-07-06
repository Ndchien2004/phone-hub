package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.ProductDTO;
import model.entity.Setting;
import service.HomeService;
import service.impl.HomeServiceImpl;
import util.PageResult;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {

    private final HomeService homeService = new HomeServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int page = 1;
        int size = 8;

        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException ignored) {
        }

        PageResult<ProductDTO> pageResult = homeService.getAllProducts(page, size);
        List<Setting> categories = homeService.getAllCategories();

        request.setAttribute("pageResult", pageResult);
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("home.jsp").forward(request, response);
    }
}