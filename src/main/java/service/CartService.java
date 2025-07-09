package service;

import model.dao.impl.CartDAOImpl;
import model.dao.impl.CartItemDAOImpl;
import model.dao.impl.ProductDAOImpl;
import model.dto.CartDTO;
import model.dto.CartItemDTO;
import model.entity.Cart;
import model.entity.CartItem;
import model.entity.Product;
import model.entity.User;

import java.util.List;
import java.util.Optional;

public class CartService {

    private final CartDAOImpl cartDAO =  new CartDAOImpl();
    private final CartItemDAOImpl cartItemDAO =  new CartItemDAOImpl();
    private final ProductDAOImpl productDAO = new ProductDAOImpl();

    public CartDTO getCartByUserIdOrCartId(Integer userId, Integer cartId) {
        Cart cart = null;

        if (userId != null) {
            cart = cartDAO.findByUserId(userId);
        } else if (cartId != null) {
            cart = cartDAO.findById(cartId);
        }

        if (cart == null) return null;

        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(item -> {
                    Product p = item.getProduct();
                    CartItemDTO dto = new CartItemDTO();

                    dto.setCartItemId(item.getCartItemId());
                    dto.setProductId(item.getProductId());
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice());

                    dto.setProductName(p.getProductName());
                    dto.setImage(p.getImageUrl());
                    dto.setColor(p.getColor());
                    dto.setMemory(p.getMemory());
                    dto.setPriceSale(p.getPriceSale());
                    dto.setPriceOrigin(p.getPriceOrigin());

                    return dto;
                }).toList();

        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getCartId());
        dto.setUserId(cart.getUserId());
        dto.setTotalMoney(cart.getTotalMoney());
        dto.setItems(itemDTOs);

        return dto;
    }

    public int getTotalQuantityInCart(int cartId) {
        Cart cart = cartDAO.findById(cartId);
        if (cart == null) return 0;
        return cart.getTotalQuantity(); // dùng method vừa tạo trong Cart
    }

    public int createCart(Integer userId) {
        return cartDAO.createCart(userId);
    }

    public void addProductToCart(int cartId, int productId, int quantity, long price) {
        cartDAO.addProductToCart(cartId, productId, quantity, price);
    }

    public void updateCartItemQuantity(int cartItemId, int quantity) {
        CartItem item = cartItemDAO.findById(cartItemId);
        if (item == null) {
            throw new IllegalArgumentException("Không tìm thấy cart item với ID: " + cartItemId);
        }

        Optional<Product> optionalProduct = productDAO.findById(item.getProductId());
        if (optionalProduct.isEmpty()) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại hoặc đã bị xóa");
        }

        Product product = optionalProduct.get();
        if (quantity > product.getQuantity()) {
            throw new IllegalStateException("Không đủ hàng trong kho. Hiện chỉ còn: " + product.getQuantity());
        }

        cartItemDAO.updateQuantityById(cartItemId, quantity);
        cartDAO.updateCartTotal(item.getCartId());
    }

    public void removeCartItem(int cartItemId) {
        // Lấy cart item trước khi xóa để biết cartId
        CartItem item = cartItemDAO.findById(cartItemId);
        if (item == null) {
            throw new IllegalArgumentException("Không tìm thấy cart item với ID: " + cartItemId);
        }
        
        int cartId = item.getCartId();
        
        // Xóa cart item
        cartItemDAO.deleteById(cartItemId);
        
        // Cập nhật lại tổng tiền của cart
        cartDAO.updateCartTotal(cartId);
    }

    public void deleteCart(int cartId) {
        cartItemDAO.deleteByCartId(cartId);
        cartDAO.deleteCartById(cartId);
    }


    // ====================================================================================
// === HÃY THÊM PHƯƠNG THỨC MỚI NÀY VÀO TRONG FILE CartService.java CỦA BẠN ===
// ====================================================================================

    /**
     * Phương thức cấp cao để thêm sản phẩm vào giỏ hàng.
     * Tự động xử lý việc tìm hoặc tạo giỏ hàng mới.
     *
     * @param user             Đối tượng User nếu đã đăng nhập (có thể là null)
     * @param cartIdFromSession ID của giỏ hàng từ session (có thể là null)
     * @param productId        ID của sản phẩm cần thêm
     * @param quantity         Số lượng cần thêm
     * @return CartDTO đã được cập nhật
     */
    public CartDTO addToCart(User user, Integer cartIdFromSession, int productId, int quantity) {
        // 1. Xác định userId
        Integer userId = (user != null) ? user.getUserId() : null;

        // 2. Tìm hoặc Tạo Giỏ hàng
        Cart cart = null;
        // Ưu tiên tìm giỏ hàng theo user ID nếu đã đăng nhập
        if (userId != null) {
            cart = cartDAO.findByUserId(userId);
        }
        // Nếu không có hoặc là khách, tìm theo cartId trong session
        if (cart == null && cartIdFromSession != null) {
            cart = cartDAO.findById(cartIdFromSession);
        }
        // Nếu vẫn không có, tạo một giỏ hàng mới
        if (cart == null) {
            int newCartId = cartDAO.createCart(userId);
            cart = cartDAO.findById(newCartId);
        }

        // Nếu sau tất cả các bước vẫn không có giỏ hàng, đây là lỗi nghiêm trọng
        if (cart == null) {
            throw new RuntimeException("Không thể tìm hoặc tạo giỏ hàng.");
        }

        // 3. Lấy giá sản phẩm từ database để đảm bảo an toàn
        Product product = productDAO.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        // 4. Thêm sản phẩm vào giỏ hàng (sử dụng phương thức cấp thấp đã có)
        // Lưu ý: phương thức addProductToCart của bạn cần giá kiểu long
        cartDAO.addProductToCart(cart.getCartId(), productId, quantity, (long) product.getPriceSale());

        // 5. Trả về CartDTO đã được làm mới để servlet cập nhật session
        return getCartByUserIdOrCartId(userId, cart.getCartId());
    }
}
