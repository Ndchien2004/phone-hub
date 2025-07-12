package model.entity;

import java.time.LocalDateTime;

public class Sales {
    private String salesId;
    private String userId;
    private String employeeCode;
    private String department;
    private LocalDateTime hireDate;
    private String status;
    private String maxOrdersPerDay;
    private String currentAssignedOrders;
    private String totalCompletedOrders;
    private Double performanceRating;
    private boolean is_deleted;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public String getSalesId() { return salesId; }

    public void setSalesId(String salesId) { this.salesId = salesId; }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getEmployeeCode() { return employeeCode; }

    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getDepartment() { return department; }

    public void setDepartment(String department) { this.department = department; }

    public LocalDateTime getHireDate() { return hireDate; }

    public void setHireDate(LocalDateTime hireDate) { this.hireDate = hireDate; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getMaxOrdersPerDay() { return maxOrdersPerDay; }

    public void setMaxOrdersPerDay(String maxOrdersPerDay) { this.maxOrdersPerDay = maxOrdersPerDay; }

    public String getCurrentAssignedOrders() { return currentAssignedOrders; }

    public void setCurrentAssignedOrders(String currentAssignedOrders) { this.currentAssignedOrders = currentAssignedOrders; }

    public String getTotalCompletedOrders() { return totalCompletedOrders; }

    public void setTotalCompletedOrders(String totalCompletedOrders) { this.totalCompletedOrders = totalCompletedOrders; }

    public Double getPerformanceRating() { return performanceRating; }

    public void setPerformanceRating(Double performanceRating) { this.performanceRating = performanceRating; }

    public boolean isIs_deleted() { return is_deleted; }

    public void setIs_deleted(boolean is_deleted) { this.is_deleted = is_deleted; }

    public LocalDateTime getCreated_at() { return created_at; }

    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    public LocalDateTime getUpdated_at() { return updated_at; }

    public void setUpdated_at(LocalDateTime updated_at) { this.updated_at = updated_at; }
}
