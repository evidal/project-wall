package net.awired.visuwall.hudsonclient.domain;

import java.util.Arrays;
import java.util.Date;

import net.awired.visuwall.api.domain.TestResult;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public final class HudsonBuild {

    private boolean successful;
    private String[] commiters;
    private long duration;
    private Date startTime;
    private TestResult testResult;
    private String state;
    private int buildNumber;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String[] getCommiters() {
        return commiters;
    }

    public void setCommiters(String[] commiters) {
        this.commiters = commiters.clone();
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }


    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    @Override
    public String toString() {
        ToStringHelper toString = Objects.toStringHelper(this) //
        .add("build number", buildNumber) //
        .add("status", successful) //
        .add("commiters", Arrays.toString(commiters)) //
        .add("duration", duration) //
        .add("startTime", startTime) //
        .add("state", state); //
        if (testResult != null) {
            toString.add("test result", testResult.toString());
        }
        return toString.toString();
    }
}
