package controller.home;

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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {

    private final HomeService homeService = new HomeServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int page = 1;
        int size = 8;

        String keyword = request.getParameter("keyword");
        String[] categoryParams = request.getParameterValues("category");

        boolean isKeywordEmpty = (keyword == null || keyword.trim().isEmpty());
        boolean isCategoryEmpty = (categoryParams == null || categoryParams.length == 0);
        boolean isPriceEmpty = (request.getParameter("minPrice") == null || request.getParameter("minPrice").isBlank())
                && (request.getParameter("maxPrice") == null || request.getParameter("maxPrice").isBlank());

        BigDecimal min = null;
        BigDecimal max = null;

        List<Integer> categoryId = new ArrayList<>();
        if (categoryParams != null) {
            for (String id : categoryParams) {
                try {
                    categoryId.add(Integer.parseInt(id));
                } catch (NumberFormatException ignored) {}
            }
        }

        try {
            String minStr = request.getParameter("minPrice");
            if (minStr != null && !minStr.trim().isEmpty()) {
                min = new BigDecimal(minStr.trim());
            }

            String maxStr = request.getParameter("maxPrice");
            if (maxStr != null && !maxStr.trim().isEmpty()) {
                max = new BigDecimal(maxStr.trim());
            }
        } catch (NumberFormatException ignored) {
        }

        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException ignored) {
        }


        if (isKeywordEmpty && isCategoryEmpty && isPriceEmpty) {
            response.sendRedirect("/phone_hub_war_exploded");
            return;
        }

        List<Setting> categories = homeService.getAllCategories();

        PageResult<ProductDTO> pageResult = homeService.search(keyword, categoryId, min, max, page, size);
        request.setAttribute("keyword", keyword);
        request.setAttribute("pageResult", pageResult);
        request.setAttribute("categories", categories);
        request.setAttribute("minPrice", min != null ? min.toPlainString() : "");
        request.setAttribute("maxPrice", max != null ? max.toPlainString() : "");
        request.setAttribute("selectedCategories", categoryParams);
        request.getRequestDispatcher("home.jsp").forward(request, response);
    }
}