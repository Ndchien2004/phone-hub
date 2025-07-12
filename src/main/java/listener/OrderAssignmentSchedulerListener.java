package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import scheduler.OrderAssignmentScheduler;

@WebListener
public class OrderAssignmentSchedulerListener implements ServletContextListener {

    private OrderAssignmentScheduler scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing order assignment scheduler...");
        scheduler = new OrderAssignmentScheduler();
        scheduler.startScheduler();
        System.out.println("Order Assignment Scheduler Started");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            System.out.println("Order Assignment Scheduler Terminating...");
            scheduler.stopScheduler();
            System.out.println("Order Assignment Scheduler Terminated");
        }
    }
}