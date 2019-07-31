package com.sebastiansoch.takephoto;

import java.io.Serializable;

public class Schedule implements Serializable {
    private int period = 0;
    private TimeType timeType;

    public Schedule(int period, TimeType timeType) {
        this.period = period;
        this.timeType = timeType;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public TimeType getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeType timeType) {
        this.timeType = timeType;
    }
}
