package com.example.weather;

import com.example.weather.model.api.SimpleDate;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testApi() throws MalformedURLException {

    }

    @Test
    public void testTime() {
        String timezone = "America/Chicago";
        long timestamp = 1558371600L;
        SimpleDate simpleDate = new SimpleDate(timezone, timestamp * 1000);
        System.out.println(simpleDate.getDayOfMonth() + " " + simpleDate.getDayOfWeek());
    }
}