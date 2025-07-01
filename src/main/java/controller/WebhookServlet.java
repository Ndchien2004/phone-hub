package controller;

import com.google.gson.Gson;
import config.PayOSConfig;
import service.OrderService;
import service.impl.OrderServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.payos.PayOS;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;
import java.io.IOException;

@WebServlet("/payos-webhook")
public class WebhookServlet extends HttpServlet {
    private final PayOS payos = new PayOS(PayOSConfig.PAYOS_CLIENT_ID, PayOSConfig.PAYOS_API_KEY, PayOSConfig.PAYOS_CHECKSUM_KEY);
    private final OrderService orderService = new OrderServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Webhook webhookBody = gson.fromJson(req.getReader(), Webhook.class);
            WebhookData verifiedData = payos.verifyPaymentWebhookData(webhookBody);

            if (verifiedData != null) {
                String transactionCode = String.valueOf(verifiedData.getOrderCode());
                String status = verifiedData.getCode();

                orderService.processPaymentCallback(transactionCode, status);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("OK");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Webhook verification failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error processing webhook.");
        }
    }
}