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
import java.util.List;

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {

    private final HomeService homeService = new HomeServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int page = 1;
        final int size = 8; // Đã đúng, mỗi trang 8 sản phẩm

        try {
            // Lấy số trang từ URL, nếu không có thì mặc định là 1
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException ignored) {
            // Giữ nguyên page = 1 nếu tham số page không hợp lệ
        }

        PageResult<ProductDTO> pageResult = homeService.getAllProducts(page, size);
        List<Setting> categories = homeService.getAllCategories();

        request.setAttribute("pageResult", pageResult);
        request.setAttribute("categories", categories);

        // ======================= DÒNG CẦN THÊM =======================
        // Đặt một thuộc tính để JSP biết đây là trang chủ
        request.setAttribute("pageType", "home");
        // =============================================================

        request.getRequestDispatcher("/views/home/home.jsp").forward(request, response);
    }
}