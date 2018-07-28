package wmr;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class StationTest {

    @org.junit.Test
    public void checkStationIsConnected() {
        ArrayList<Rail> lines = new ArrayList<Rail>();
        lines.add(new Rail("a",new Station("test2"), 5));
        lines.add(new Rail("a", new Station("test3"), 5));
        Station test = new Station("demo", lines);
        String testb = test.checkStationConnected("test2") ? "yes" : "no";
        Assert.assertSame("yes", testb);
    }
    @org.junit.Test
    public void checkStationNotConnected() {
        ArrayList<Rail> lines = new ArrayList<Rail>();
        lines.add(new Rail("a", new Station("test2"), 5));
        lines.add(new Rail("a",new Station("test3"), 5));
        Station test = new Station("demo", lines);
        String testb = test.checkStationConnected("test4") ? "yes" : "no";
        Assert.assertSame("no", testb);
    }
}
