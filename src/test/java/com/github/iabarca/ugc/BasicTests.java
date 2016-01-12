
package com.github.iabarca.ugc;

import junit.framework.Assert;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import java.util.Locale;

public class BasicTests {

    @Test
    public void jodaTest() {
        LocalDateTime date;
        date = LocalDateTime.parse("2014-02-11T07:05:43.236Z",
                DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        Assert.assertEquals(2014, date.getYear());
        Assert.assertEquals(2, date.getMonthOfYear());
        Assert.assertEquals(11, date.getDayOfMonth());
        Assert.assertEquals(7, date.getHourOfDay());
        Assert.assertEquals(5, date.getMinuteOfHour());
        Assert.assertEquals(43, date.getSecondOfMinute());
        Assert.assertEquals(236, date.getMillisOfSecond());
        date = LocalDateTime.parse("February, 17 2014 00:00:00",
                DateTimeFormat.forPattern("MMM, dd yyyy HH:mm:ss").withLocale(Locale.ENGLISH));
        Assert.assertEquals(2014, date.getYear());
        Assert.assertEquals(2, date.getMonthOfYear());
        Assert.assertEquals(17, date.getDayOfMonth());
        Assert.assertEquals(0, date.getHourOfDay());
        Assert.assertEquals(0, date.getMinuteOfHour());
        Assert.assertEquals(0, date.getSecondOfMinute());
        Assert.assertEquals(0, date.getMillisOfSecond());
    }

}
