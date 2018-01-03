package org.jigsaw.payment.metric;


/**
 * 持续统计。
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月13日
 */
public class Histogram {
    private long count;
    private double average;
    private Long max;
    private Long min;

    public synchronized void addValue(final long value) {
        calculateAverage(value);
        calculateMax(value);
        calculateMin(value);
        count++;
    }

    private void calculateAverage(long value) {
        average = (getTotal() + value) / (count + 1);
    }

    private void calculateMin(long value) {
        if (min != null) {
            min = Math.min(min, value);
        } else {
            min = value;
        }
    }

    private void calculateMax(long value) {
        if (max != null) {
            max = Math.max(max, value);
        } else {
            max = value;
        }
    }

    private double getTotal() {
        return average * count;
    }

    public double getAverage() {
        return average;
    }

    public long getMax() {
        return max;
    }

    public long getMin() {
        return min;
    }

}
