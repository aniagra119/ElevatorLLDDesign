package com.elevator.model;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
class FloorTest {

    @Test
    void testStandardFloor() {
        Floor floor = new Floor(0, "Ground Lobby");
        assertEquals(0, floor.getFloorNumber());
        assertEquals("Ground Lobby", floor.getDisplayName());
        assertFalse(floor.isAccessRestricted());
        assertTrue(floor.canAccess("GUEST"));
        assertTrue(floor.canAccess(null));
    }

    @Test
    void testRestrictedFloor() {
        Floor penthouse = new Floor(14, "Penthouse VIP", true, Set.of("VIP", "EXECUTIVE", "SECURITY"));
        assertEquals(14, penthouse.getFloorNumber());
        assertTrue(penthouse.isAccessRestricted());
        assertTrue(penthouse.canAccess("VIP"));
        assertTrue(penthouse.canAccess("EXECUTIVE"));
        assertFalse(penthouse.canAccess("GUEST"));
        assertFalse(penthouse.canAccess(null));
    }

    @Test
    void testEqualityAndToString() {
        Floor f1 = new Floor(3, "Level 3");
        Floor f2 = new Floor(3, "3rd Floor");
        Floor f3 = new Floor(4, "Level 4");

        assertEquals(f1, f2);
        assertNotEquals(f1, f3);
        assertEquals(f1.hashCode(), f2.hashCode());
        assertTrue(f1.toString().contains("Level 3"));
    }

    @Test
    void testSingleArgConstructorAndComparable() {
        Floor floor2 = new Floor(2);
        Floor floor5 = new Floor(5);

        assertEquals(2, floor2.getFloorNumber());
        assertEquals("2", floor2.getDisplayName());
        assertTrue(floor2.compareTo(floor5) < 0);
        assertTrue(floor5.compareTo(floor2) > 0);
        assertEquals(0, floor2.compareTo(new Floor(2, "Two")));
    }

    @Test
    void testFlyweightAndNavigation() {
        Floor f3 = Floor.of(3);
        Floor f3Again = Floor.of(3);
        assertSame(f3, f3Again, "Floor.of must return identical flyweight instance from cache");

        Floor f4 = f3.next();
        assertEquals(4, f4.getFloorNumber());
        assertSame(f4, Floor.of(4), "next() must return cached Floor.of(4)");

        Floor f2 = f3.previous();
        assertEquals(2, f2.getFloorNumber());
        assertSame(f2, Floor.of(2), "previous() must return cached Floor.of(2)");
    }
}
