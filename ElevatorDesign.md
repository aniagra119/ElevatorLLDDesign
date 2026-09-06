# Multi-Elevator System — Architectural Blueprint & Design Rationale

This document outlines the core architectural decisions, design patterns, and engineering principles established for this multi-car elevator low-level design (LLD).

---

## 1. Unified Request Payload & Dumb Routing
- **Unified Request Object**: Instead of disparate internal/external request hierarchies, a unified `Request` immutable value object encapsulates `Floor` and `RequestType`.
- **Request Types**: `RequestType` (`EXTERNAL_UP`, `EXTERNAL_DOWN`, `INTERNAL`).
- **Dumb Routing**: When a button is pressed, the request is forwarded directly to the appropriate receiver via the Command Pattern without payload manipulation.

---

## 2. Hardware UI, Command Pattern & Duplicate Prevention
- **Command Structure**:
  - `ElevatorCommand` interface declaring `execute()`.
  - Concrete commands: `ExternalRequestCommand`, `InternalRequestCommand`, `EmergencyStopCommand`, `AlarmCommand`, and `NoOpCommand`.
  - Commands depend on abstract receivers (`ExternalRequestReceiver`, `InternalRequestReceiver`, `EmergencyStopReceiver`, `AlarmReceiver`), adhering to the Dependency Inversion Principle.
- **Hardware Debounce & Duplicate Guard**:
  - `Button` encapsulates illumination state (`isIlluminated`) and debouncing logic.
  - If a button is already illuminated, duplicate presses are ignored at the hardware layer, preventing duplicate requests from flooding the dispatcher or car queues.
- **Automatic Button Shutoff**:
  - When an elevator arrives at a destination floor with doors open, the car's state update automatically turns off the corresponding cabin and hall button lamps via the Observer Pattern.
- **No Mid-Flight Stealing**:
  - Once a car commits to a request stop, the dispatcher never cancels or steals that stop mid-flight.

---

## 3. Queue Ownership & Strategy Separation
- **Strategy Owns the Data Structures**:
  - The `Elevator` does not own raw queue collections. Instead, directional sorted sets (`TreeSet<Floor>`) are encapsulated inside `ScanQueueStrategy`.
- **Two Distinct Strategy Layers**:
  1. **`DispatchStrategy`** (Owned by `ElevatorDispatcher`): Fleet-level coordination deciding *which car* handles an external hall call.
  2. **`MovementStrategy`** (Owned by `Elevator`): Car-level movement scheduling (LOOK/SCAN) deciding *how* an individual elevator sweeps its queued stops.
- **Fleet-Level Energy Policy**:
  - Energy optimization (parking idle cars, avoiding starting up idle motors) is handled at the dispatcher level (`EnergySaverDispatchStrategy`), not inside individual cars.

---

## 4. Core Elevator & Safety Sensors
- **Elevator Responsibilities**:
  - Encapsulates physical telemetry (current floor, direction, door state, load sensor, operational state).
- **Elevator State**:
  - `ElevatorState` represents operational readiness (`OPERATING`, `MAINTENANCE`, `EMERGENCY_STOP`).
- **Overload is a Dynamic Sensor Check**:
  - Overload is not a state machine state. It is an immediate boolean safety check (`weightSensor.isOverloaded()`) evaluated before closing doors.

---

## 5. Elevator Banks & Multi-Bank Isolation
- **Isolated Dispatchers per Bank**:
  - High-rise buildings group elevators into banks (e.g. Low-Rise, High-Rise, Freight). Each bank operates its own `ElevatorDispatcher` and dedicated hallway panels, eliminating monolithic global bottlenecks.

---

## 6. Displays & Telemetry (Observer Pattern)
- **Loose Coupling**:
  - `DisplayPanel` and `ElevatorPanel` observe `Elevator` telemetry changes (`update(Elevator)`).
  - Observers update floor indicators, arrows, and operational status without coupling the car to specific display hardware.