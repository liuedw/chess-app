package demo;

import org.junit.*;
import static org.junit.Assert.*;

public class BoardCoordTest {
    @Test(expected=IllegalArgumentException.class)
    public void invalidStringTest1() {
        new BoardCoord("oi");
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidStringTest2() {
        new BoardCoord("e9");
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidStringTest3() {
        new BoardCoord("2k");
    }

    @Test
    public void lowerCaseTest() {
        assertEquals(new BoardCoord(4, 1), new BoardCoord("e7"));
        assertEquals(new BoardCoord(7, 0), new BoardCoord("h8"));
        assertEquals(new BoardCoord(0, 7), new BoardCoord("1a"));
        assertEquals(new BoardCoord(0, 0), new BoardCoord("8a"));
    }

    @Test
    public void upperCaseTest() {
        assertEquals(new BoardCoord(4, 1), new BoardCoord("E7"));
        assertEquals(new BoardCoord(7, 0), new BoardCoord("H8"));
        assertEquals(new BoardCoord(0, 7), new BoardCoord("1A"));
        assertEquals(new BoardCoord(0, 0), new BoardCoord("8A"));
    }
}