package service;

import model.dao.impl.CartDAOImpl;
import model.dao.impl.CartItemDAOImpl;
import model.dao.impl.ProductDAOImpl;
import model.dto.CartDTO;
import model.dto.CartItemDTO;
import model.entity.Cart;
import model.entity.CartItem;
import model.entity.Product;

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
}
