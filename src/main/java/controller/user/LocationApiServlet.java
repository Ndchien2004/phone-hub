package controller.user;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.entity.District;
import model.entity.Ward;
import service.user.LocationService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "LocationApiServlet", urlPatterns = {"/api/districts", "/api/wards"})
public class LocationApiServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final LocationService locationService = new LocationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String uri = request.getRequestURI();

        if (uri.endsWith("/api/districts")) {
            String provinceCode = request.getParameter("provinceCode");
            List<District> districts = locationService.getDistrictsByProvince(provinceCode);
            response.getWriter().write(gson.toJson(districts));
        }

        if (uri.endsWith("/api/wards")) {
            String districtCode = request.getParameter("districtCode");
            if (districtCode == null || districtCode.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"districtCode is required\"}");
                return;
            }

            List<Ward> wards = locationService.getWardsByDistrict(districtCode);
            response.getWriter().write(new Gson().toJson(wards));
        }
    }

}

