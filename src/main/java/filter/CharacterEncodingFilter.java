package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Servlet Filter này đảm bảo rằng tất cả các request và response
 * đều được xử lý với encoding UTF-8.
 * Nó sẽ chặn tất cả các request đến ứng dụng ("/*").
 */
@WebFilter("/*")
public class CharacterEncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Không cần làm gì khi khởi tạo
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Ép buộc encoding cho request
        request.setCharacterEncoding("UTF-8");

        // Ép buộc encoding cho response
        response.setCharacterEncoding("UTF-8");

        // In ra để debug (bạn có thể xóa sau này)
        // System.out.println("CharacterEncodingFilter: Đã set encoding là UTF-8 cho request và response.");

        // Cho phép request tiếp tục đi đến servlet tiếp theo trong chuỗi
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Không cần làm gì khi hủy
    }
}