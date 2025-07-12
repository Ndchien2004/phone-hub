package model.entity;

import java.time.LocalDateTime;

public class AssignmentLogs {
    private String logId;
    private String processId;
    private String total_orders_processed;
    private String total_orders_assigned;
    private String assignment_algorithm;
    private String execution_time_ms;
    private boolean success;
    private String error_message;
    private LocalDateTime created_at;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getTotal_orders_processed() {
        return total_orders_processed;
    }

    public void setTotal_orders_processed(String total_orders_processed) {
        this.total_orders_processed = total_orders_processed;
    }

    public String getTotal_orders_assigned() {
        return total_orders_assigned;
    }

    public void setTotal_orders_assigned(String total_orders_assigned) {
        this.total_orders_assigned = total_orders_assigned;
    }

    public String getAssignment_algorithm() {
        return assignment_algorithm;
    }

    public void setAssignment_algorithm(String assignment_algorithm) {
        this.assignment_algorithm = assignment_algorithm;
    }

    public String getExecution_time_ms() {
        return execution_time_ms;
    }

    public void setExecution_time_ms(String execution_time_ms) {
        this.execution_time_ms = execution_time_ms;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}
