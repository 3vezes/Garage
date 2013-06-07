package com.ericrgon.model;


import com.google.api.client.repackaged.com.google.common.base.Objects;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Object to contain an individual log entry.
 * Created by ericrgon on 6/6/13.
 */
public class LogEntry {

    private String name;
    private Date time;
    private String state;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("E hh:mm a", Locale.US);

    public LogEntry(String name, Date time, String state) {
        this.name = name;
        this.time = time;
        this.state = state;
    }

    public String getName() {
        return "Eric";
    }

    public String getTime() {
        return SIMPLE_DATE_FORMAT.format(time);
    }

    public String getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogEntry that = (LogEntry) o;

        return Objects.equal(this.name, that.name) &&
                Objects.equal(this.time,that.time) &&
                Objects.equal(this.state,that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name,time,state);
    }
}
