package org.databox.di.airport.domain;

/**
 *  Class to hold delay info
 *  @author Jason Melbourne
 */
public class DelayStatus {
    private String reason;
    private String closureBegin;
    private String endTime;
    private String minDelay;
    private String avgDelay;
    private String maxDelay;
    private String closureEnd;
    private String trend;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getClosureBegin() {
        return closureBegin;
    }

    public void setClosureBegin(String closureBegin) {
        this.closureBegin = closureBegin;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMinDelay() {
        return minDelay;
    }

    public void setMinDelay(String minDelay) {
        this.minDelay = minDelay;
    }

    public String getAvgDelay() {
        return avgDelay;
    }

    public void setAvgDelay(String avgDelay) {
        this.avgDelay = avgDelay;
    }

    public String getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay(String maxDelay) {
        this.maxDelay = maxDelay;
    }

    public String getClosureEnd() {
        return closureEnd;
    }

    public void setClosureEnd(String closureEnd) {
        this.closureEnd = closureEnd;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }
}
