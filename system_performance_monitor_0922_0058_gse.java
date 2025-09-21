// 代码生成时间: 2025-09-22 00:58:54
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.Context;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import static io.javalin.ApiBuilder.get;

/**
 * SystemPerformanceMonitor is a Javalin-based application that provides
 * system performance monitoring features.
 */
public class SystemPerformanceMonitor {

    private static final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

    /**
     * Starts the Javalin server with the configured routes.
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        // Define the route to get system performance data
        app.routes(() -> {
            ApiBuilder.get("/performance", SystemPerformanceMonitor::performanceCheck);
        });
    }

    /**
     * Handles the GET request to the /performance endpoint.
     * @param ctx The Javalin context.
     */
    public static void performanceCheck(Context ctx) {
        try {
            // Gather system performance metrics
            double cpuLoad = osBean.getSystemCpuLoad();
            long totalMemory = Runtime.getRuntime().totalMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long usedMemory = totalMemory - freeMemory;

            // Create a response object with the system performance metrics
            SystemPerformance performance = new SystemPerformance(
                    cpuLoad,
                    totalMemory,
                    freeMemory,
                    usedMemory
            );

            // Return the response as JSON
            ctx.json(performance);
        } catch (Exception e) {
            // Handle any errors that occur during the performance check
            ctx.status(500).result("Error checking system performance: " + e.getMessage());
        }
    }

    /**
     * A simple POJO to hold system performance data.
     */
    public static class SystemPerformance {
        private double cpuLoad;
        private long totalMemory;
        private long freeMemory;
        private long usedMemory;

        public SystemPerformance(double cpuLoad, long totalMemory, long freeMemory, long usedMemory) {
            this.cpuLoad = cpuLoad;
            this.totalMemory = totalMemory;
            this.freeMemory = freeMemory;
            this.usedMemory = usedMemory;
        }

        // Getters and setters for each field
        public double getCpuLoad() { return cpuLoad; }
        public void setCpuLoad(double cpuLoad) { this.cpuLoad = cpuLoad; }
        public long getTotalMemory() { return totalMemory; }
        public void setTotalMemory(long totalMemory) { this.totalMemory = totalMemory; }
        public long getFreeMemory() { return freeMemory; }
        public void setFreeMemory(long freeMemory) { this.freeMemory = freeMemory; }
        public long getUsedMemory() { return usedMemory; }
        public void setUsedMemory(long usedMemory) { this.usedMemory = usedMemory; }
    }
}
