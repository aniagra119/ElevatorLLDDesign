package com.elevator.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rich domain entity representing a building floor.
 * Encapsulates floor numbers, display names, and access control levels.
 * Implements the Flyweight pattern via Floor.of(...) to prevent unnecessary object allocation.
 */
public class Floor implements Comparable<Floor> {
    private static final Map<Integer, Floor> CACHE = new ConcurrentHashMap<>();

    private final int floorNumber;
    private final String displayName;
    private final boolean isAccessRestricted;
    private final Set<String> allowedAccessLevels;

    public static Floor of(int floorNumber) {
        return CACHE.computeIfAbsent(floorNumber, Floor::new);
    }

    public static Floor of(int floorNumber, String displayName) {
        return CACHE.computeIfAbsent(floorNumber, fn -> new Floor(fn, displayName));
    }

    public Floor(int floorNumber) {
        this(floorNumber, String.valueOf(floorNumber), false, Collections.emptySet());
    }

    public Floor(int floorNumber, String displayName) {
        this(floorNumber, displayName, false, Collections.emptySet());
    }

    public Floor(int floorNumber, String displayName, boolean isAccessRestricted, Set<String> allowedAccessLevels) {
        this.floorNumber = floorNumber;
        this.displayName = displayName != null ? displayName : String.valueOf(floorNumber);
        this.isAccessRestricted = isAccessRestricted;
        this.allowedAccessLevels = allowedAccessLevels != null ? new HashSet<>(allowedAccessLevels) : Collections.emptySet();
    }

    public Floor next() {
        return of(this.floorNumber + 1);
    }

    public Floor previous() {
        return of(this.floorNumber - 1);
    }

    public boolean canAccess(String accessLevel) {
        return !isAccessRestricted || (accessLevel != null && allowedAccessLevels.contains(accessLevel));
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAccessRestricted() {
        return isAccessRestricted;
    }

    public Set<String> getAllowedAccessLevels() {
        return Collections.unmodifiableSet(allowedAccessLevels);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Floor floor = (Floor) o;
        return floorNumber == floor.floorNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(floorNumber);
    }

    @Override
    public int compareTo(Floor o) {
        return Integer.compare(this.floorNumber, o.floorNumber);
    }

    @Override
    public String toString() {
        return displayName + " (" + floorNumber + ")";
    }
}
