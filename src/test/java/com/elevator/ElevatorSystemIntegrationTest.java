package com.elevator;

import com.elevator.enums.Direction;
import com.elevator.hardware.ElevatorPanel;
import com.elevator.hardware.HallPanel;
import com.elevator.hardware.PanelFactory;
import com.elevator.model.Elevator;
import com.elevator.model.ElevatorDispatcher;
import com.elevator.model.Floor;
import com.elevator.observer.DisplayPanel;
import com.elevator.strategy.dispatch.DirectionAwareDispatchStrategy;
import com.elevator.strategy.movement.ScanQueueStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElevatorSystemIntegrationTest {

    private ElevatorSystem system;
    private Elevator car1;
    private Elevator car2;
    private DisplayPanel display1;
    private DisplayPanel display2;
    private Map<Floor, HallPanel> hallPanels;
    private Map<String, ElevatorPanel> cabinPanels;

    @BeforeEach
    void setUp() {
        car1 = new Elevator("E1", 500, new ScanQueueStrategy());
        car2 = new Elevator("E2", 500, new ScanQueueStrategy());
        List<Elevator> elevators = List.of(car1, car2);

        display1 = new DisplayPanel("Car 1 Display");
        display2 = new DisplayPanel("Car 2 Display");
        car1.addObserver(display1);
        car2.addObserver(display2);

        ElevatorDispatcher dispatcher = new ElevatorDispatcher(elevators, new DirectionAwareDispatchStrategy());

        // Create HallPanels for floors 0 to 4
        hallPanels = new HashMap<>();
        for (int f = 0; f <= 4; f++) {
            Floor floor = Floor.of(f);
            hallPanels.put(floor, PanelFactory.createHallPanel(dispatcher, floor, Floor.of(0), Floor.of(4)));
        }

        // Create ElevatorPanels for both cars
        cabinPanels = new HashMap<>();
        cabinPanels.put("E1", PanelFactory.createElevatorPanel(car1, Floor.of(0), Floor.of(4)));
        cabinPanels.put("E2", PanelFactory.createElevatorPanel(car2, Floor.of(0), Floor.of(4)));

        system = new ElevatorSystem(dispatcher, elevators, hallPanels, cabinPanels);
    }

    @Test
    @DisplayName("End-to-End: Passenger requests car from Floor 2 UP, boards, and selects Floor 4 with button auto-shutoff")
    void testEndToEndHallAndCabinRide() {
        assertEquals(0, car1.getCurrentFloorNumber());
        assertEquals(0, car2.getCurrentFloorNumber());

        HallPanel floor2Panel = hallPanels.get(Floor.of(2));
        ElevatorPanel car1Panel = cabinPanels.get("E1");

        // 1. Hallway call: Passenger on Floor 2 presses UP
        system.requestElevator(Floor.of(2), Direction.UP);
        assertTrue(floor2Panel.getUpButton().isIlluminated(), "Floor 2 UP button must illuminate when pressed");

        // Step 1: E1 climbs to 1
        system.step();
        assertEquals(1, car1.getCurrentFloorNumber());
        assertEquals(Direction.UP, car1.getCurrentDirection());

        // Step 2: E1 arrives at 2
        system.step();
        assertEquals(2, car1.getCurrentFloorNumber());

        // Step 3: E1 serves floor 2 (door opens & closes) -> Hall button automatically
        // turns off!
        system.step();
        assertEquals(2, car1.getCurrentFloorNumber());
        assertFalse(floor2Panel.getUpButton().isIlluminated(),
                "Floor 2 UP button must turn off automatically when elevator arrives with open doors!");

        // 2. Passenger enters Car 1 and presses destination button for Floor 4
        system.selectFloor("E1", Floor.of(4));
        assertTrue(car1Panel.getButton(Floor.of(4)).isIlluminated(), "Cabin button 4 must illuminate when pressed");

        // Climbs to 3
        system.step();
        assertEquals(3, car1.getCurrentFloorNumber());

        // Arrives at 4
        system.step();
        assertEquals(4, car1.getCurrentFloorNumber());

        // Serves 4 (door opens & closes) -> Cabin button 4 automatically turns off!
        system.step();
        assertEquals(4, car1.getCurrentFloorNumber());
        assertFalse(car1Panel.getButton(Floor.of(4)).isIlluminated(),
                "Cabin button 4 must turn off automatically when elevator arrives at destination!");

        // 3. Journey complete: E1 transitions to IDLE
        system.step();
        assertEquals(Direction.IDLE, car1.getCurrentDirection());
        assertEquals(Direction.IDLE, display1.getDisplayedDirection());
    }

    @Test
    @DisplayName("Emergency stop triggered via public system facade halts the car and illuminates emergency button")
    void testEmergencyStopFromFacade() {
        ElevatorPanel car1Panel = cabinPanels.get("E1");
        assertFalse(car1Panel.getEmergencyStopButton().isIlluminated());

        system.selectFloor("E1", Floor.of(3));
        system.step(); // car starts moving

        // Emergency button pressed inside E1
        system.emergencyStop("E1");

        assertTrue(car1Panel.getEmergencyStopButton().isIlluminated(), "Emergency stop button must illuminate");
        assertTrue(car1.getDoor().isOpen(), "Door must open during emergency stop");
        assertEquals(com.elevator.enums.ElevatorState.EMERGENCY_STOP, car1.getState());
        assertEquals(com.elevator.enums.ElevatorState.EMERGENCY_STOP, display1.getDisplayedState());

        car1Panel.turnOffEmergencyStopButton();
        assertFalse(car1Panel.getEmergencyStopButton().isIlluminated());
    }

    @Test
    @DisplayName("Alarm triggered via public system facade illuminates alarm button and allows reset")
    void testActivateAlarmFromFacade() {
        ElevatorPanel car1Panel = cabinPanels.get("E1");
        assertFalse(car1Panel.getAlarmButton().isIlluminated());

        system.activateAlarm("E1");

        assertTrue(car1Panel.getAlarmButton().isIlluminated(), "Alarm button must illuminate when pressed");

        car1Panel.turnOffAlarmButton();
        assertFalse(car1Panel.getAlarmButton().isIlluminated(), "Alarm button must turn off when reset");
    }
}
