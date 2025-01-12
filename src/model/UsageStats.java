package model;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class UsageStats {
    private static UsageStats instance;
    private Instant startTime;
    private Duration totalTimeUsed;
    private AtomicInteger totalSearches;
    private AtomicInteger totalSubscriptions;

    private UsageStats() {
        this.startTime = Instant.now();
        this.totalTimeUsed = Duration.ZERO;
        this.totalSearches = new AtomicInteger(0);
        this.totalSubscriptions = new AtomicInteger(0);
    }

    // Obtener instancia única
    public static synchronized UsageStats getInstance() {
        if (instance == null) {
            instance = new UsageStats();
        }
        return instance;
    }

    // Métodos para registrar estadísticas
    public void incrementSearches() {
        totalSearches.incrementAndGet();
    }

    public void incrementSubscriptions() {
        totalSubscriptions.incrementAndGet();
    }

    public void endSession() {
        Duration sessionDuration = Duration.between(startTime, Instant.now());
        totalTimeUsed = totalTimeUsed.plus(sessionDuration);
        startTime = Instant.now(); // Reiniciar para una nueva sesión
    }

    // Métodos para obtener estadísticas
    public int getTotalSearches() {
        return totalSearches.get();
    }

    public int getTotalSubscriptions() {
        return totalSubscriptions.get();
    }

    public Duration getTotalTimeUsed() {
        return totalTimeUsed;
    }

    @Override
    public String toString() {
        return "Usage Stats:\n" +
                "Total Searches: " + totalSearches.get() + "\n" +
                "Total Subscriptions: " + totalSubscriptions.get() + "\n" +
                "Total Time Used: " + totalTimeUsed.toMinutes() + " minutes";
    }
}
