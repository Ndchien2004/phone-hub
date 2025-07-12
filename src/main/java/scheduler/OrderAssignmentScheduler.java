package scheduler;

import service.OrderAssignmentService;
import service.impl.OrderAssignmentServiceImpl;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OrderAssignmentScheduler {

    private final ScheduledExecutorService scheduler;
    private final OrderAssignmentService assignmentService;

    public OrderAssignmentScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.assignmentService = new OrderAssignmentServiceImpl();
    }

    public void startScheduler() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("Start the automatic order allocation process: " + java.time.LocalDateTime.now());
                assignmentService.processOrderAssignments();
            } catch (Exception e) {
                System.err.println("Scheduler error: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, 30, TimeUnit.MINUTES);
    }

    public void stopScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}