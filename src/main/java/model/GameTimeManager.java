package model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameTimeManager {
    private ScheduledExecutorService scheduler;
    private long gameStartTime;
    private final float TIME_SCALE = 10.0f;
    private int gameDayLength = 8 * 60 * 60 * 10;
    private int startHour = 19;
    private int endHour = 3;
    private int currentDay = 1;
    private TimeUpdateListener updateListener;
    private DayEndListener dayEndListener;
    private SpecialHourListener specialHourListener;

    public interface TimeUpdateListener {
        void onTimeUpdate(String timeString, int currentHour, boolean isNight);
    }

    public interface DayEndListener {
        void onDayEnd();
    }

    public interface SpecialHourListener {
        void onSpecialHour(int hour);
    }

    public GameTimeManager() {
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void setUpdateListener(TimeUpdateListener listener) {
        this.updateListener = listener;
    }

    public void setDayEndListener(DayEndListener listener) {
        this.dayEndListener = listener;
    }

    public void setSpecialHourListener(SpecialHourListener listener) {
        this.specialHourListener = listener;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void startTimer() {
        gameStartTime = System.currentTimeMillis();
        scheduler.scheduleAtFixedRate(() -> {
            updateGameTime();
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    public void stopTimer() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private void updateGameTime() {
        long realTimeElapsed = System.currentTimeMillis() - gameStartTime;
        long gameTimeElapsed = (long) (realTimeElapsed * TIME_SCALE);

        long totalTenthSeconds = gameTimeElapsed;
        long totalSeconds = totalTenthSeconds / 10;
        long minutes = (totalSeconds / 60) % 60;
        long hours = (startHour + totalSeconds / 3600) % 24;

        String timeString = String.format("%02d:%02d", hours, minutes);
        boolean isNight = hours >= 19 || hours < 7;

        if (hours == 0 && minutes == 0 && specialHourListener != null) {
            specialHourListener.onSpecialHour(0);
        }

        if (hours == endHour && minutes == 0) {
            if (dayEndListener != null) {
                dayEndListener.onDayEnd();
                currentDay++;
            }
            stopTimer();
            return;
        }

        if (updateListener != null) {
            updateListener.onTimeUpdate(timeString, (int) hours, isNight);
        }
    }

    public void restartTimer() {
        stopTimer();
        this.scheduler = Executors.newScheduledThreadPool(1);
        gameStartTime = System.currentTimeMillis();
        startTimer();
    }

    public boolean isTimerRunning() {
        return scheduler != null && !scheduler.isShutdown();
    }

    public float getTimeProgress() {
        long realTimeElapsed = System.currentTimeMillis() - gameStartTime;
        long gameTimeElapsed = (long) (realTimeElapsed * TIME_SCALE);
        return (float) gameTimeElapsed / gameDayLength;
    }

    public String getRemainingTime() {
        long realTimeElapsed = System.currentTimeMillis() - gameStartTime;
        long gameTimeElapsed = (long) (realTimeElapsed * TIME_SCALE);
        long remaining = gameDayLength - gameTimeElapsed;

        long remainingTenthSeconds = remaining;
        long remainingSeconds = remainingTenthSeconds / 10;
        long minutes = (remainingSeconds / 60) % 60;
        long hours = remainingSeconds / 3600;

        return String.format("%02d:%02d", hours, minutes);
    }

    public void fastForwardToNearEnd() {
        long almostFullDay = gameDayLength - (1 * 60 * 10);
        gameStartTime = System.currentTimeMillis() - (long) (almostFullDay / TIME_SCALE);
    }
}