# Java Elevator Simulation Project (SYSC-3303)

## Description
The goal of this project is to simulate an elevator system and showcase how elevators, floors, and a scheduler work together to handle elevator requests efficiently. The system uses a CSV parser to read input requests and a logging system to debug and monitor the process. The system is composed of three important components: a multithreaded controller (Scheduler), an elevator car simulator (with lights, buttons, doors, and motors), and a floor simulator (with buttons, lights, and simulated people)

## Installation
- Ensure Java OpenJDK 20.0.2 is installed on your system. 
- Clone this repository and navigate into the project directory.
- Compile the Java files using your preferred IDE that supports Java. That said, it is recommended to use IntelliJ IDEA 2023.2.5 (Community Edition) for best results. 

## Usage
- First start `ElevatorSubsystem.java`, then `Scheduler.java` and at the end `FloorSubsystem.java`
- Input requests can be modified in the CSV file specified by `input.csv` in the res folder.
- To execute all tests, right-click on the test folder and select 'Run Tests in 'test''

![iteration_3_readme_execution_order.png](iteration_3_readme_execution_order.png)


## Components
- **config**
  - `Config.java`: Defines constants used across the system, such as the time required to load/unload passengers and to open/close elevator doors, as well as the speed of the elevator. 

- **elevator**
  - `Elevator.java`: Defines the elevator's properties and actions.
  - `ElevatorSubsystem.java`:  Manages the scheduling and coordination of multiple elevators.
  - `ElevatorState.java`:  Defines the possible states an elevator can be in (e.g., idle, transporting).
  - `ElevatorIdleState.java`:  Implements the behavior of an elevator waiting for new requests.
  - `ElevatorTransportingState.java`: Implements the behavior of an elevator actively moving and handling requests.
  - `ElevatorInfo.java`:  Stores information about an elevator's current state for communication purposes.
  - `ElevatorFaultState.java`: Represents the fault state of an elevator. It clears the request queue, stops the elevator at the nearest floor, opens the doors, displays an error message, and logs relevant information when the elevator enters the fault state.


- **floor**
  - `CSVParser.java`: Parses elevator request data from a CSV file into a list of `ElevatorRequest` objects. 
  - `ElevatorRequest.java`: Defines the data structure for an elevator request, including the requested direction, floor number, and time of the request.
  - `FloorSubsystem.java`: Acts as the manager for all floor-related activities. It reads elevator requests from a CSV file using CSVParser, stores these requests, and communicates with the Scheduler to coordinate the handling of these requests. The subsystem manages an array of Floor objects representing the floors in the building.
  - `Floor.java`: Represents an individual floor within the building. It maintains the state of the floor's lamps (indicating if an up or down request has been made).

- **log**
  - `Log.java`: Provides a static method, print, for logging informational messages, which is used throughout the project to log events, operations, and errors.

- **main**
  - `Main.java`: The entry point of the application. It orchestrates the starting of all subsystems and manages their execution threads.

- **scheduler**
  - `Scheduler.java`: The main class of the scheduler. It manages the queue of requests, communicates with the ElevatorSubsystem and FloorSubsystem, and implements the state machine logic for request processing.
  - `SchedulerState.java` : Interface defining the possible states of the Scheduler (e.g., AwaitingRequestState, ProcessingRequestState, ElevatorDispatchState). Each state implements specific request handling behavior.
  - `AwaitingRequestState.java` : A concrete implementation of the SchedulerState interface, representing the state where the Scheduler is idle and waiting for new requests.
  - `ProcessingRequestState.java`: Another implementation of SchedulerState, representing the state where the Scheduler is actively analyzing a received request to determine the best elevator assignment.
  - `ElevatorDispatchState.java` : A SchedulerState implementation responsible for the state where an elevator has been selected and the Scheduler communicates the request to the ElevatorSubsystem.

- **test**
  - `CSVParserTest.java`: Tests the functionality of the CSV parser to ensure reliability.
  - `FloorSubsystemTest.java`: Tests the functionality of the floor subsystem.
  - `SchedulerTest.java`: Tests the functionality of the scheduler.
  - `TestElevator.java`: Tests the functionality of the elevator class.
  - `TestElevatorSubsystem.java`: Tests the functionality of the elevator subsystem.
  - `TestFloor.java`: Tests the functionality of the floor class.
  - `TestFaults.java`: Tests for handling various faults in the elevator system, including BAD_REQUEST, DEATH, DOOR_NOT_CLOSE, DOOR_NOT_OPEN, and timeout faults.

## Contributing - Group 7 (Lab A1)
- Ali Abdollahian (101229396) 
- Jaan Soulier  (101189819)
- Jarnail Singh (101228231)
- Laurence Lamarche-Cliche (101173070) 
- Matthew Gaudet (101193256)

## Team Contributions for Iteration 4

- Ali Abdollahian
  - Added elapsed timer for processing requests that has a limit of 1 minute. If the processing time exceeds one minute, it moves the elevator to a fault state

- Jaan Soulier
  - Added new diagrams covering the Elevator Subsystem, Floor Subsystem, Scheduler Subsystem and timing diagrams.
  
- Jarnail Singh
  - Added the Elevatorfaultstate.
  - ElevatorSubsystem looks at the request received from the scheduler and if it has a fault text such as "DEATH" or "BAD_REQUEST" or "DOOR_NOT_CLOSE", it sets the elevator that is supposed to receive this request to the FAULT state or ignores the request or passes the request.
  
- Laurence Lamarche-Cliche
  - Modified the CSV parser to parse and accept the input_faults.csv file which has an extra column specifying faults.

- Matthew Gaudet
  - Added logic to handle the DOOR_NOT_OPEN fault. If the request has this fault, the loadElevator function prints "Elevator door opening failed due to fault, retrying doors" and adds an extra second delay.
  - Added the TestFaults class with unit tests for "BAD_REQUEST", "DEATH", "DOOR_NOT_OPEN" or "DOOR_NOT_CLOSE" fault scenarios.


## How to Test Faults for Iteration 4:
- Update the file pathname in `FloorSubsystem.java` from the current value 'res/input.csv' to 'res/input_faults.csv' in line 187 of the main method.
![alt text](<Iteration 4_readme_test_faults.png>)

- Execute `TestFaults.java` in the test folder. (Make sure to close all running files before executing or the test testDeathFault() might fail due to socketException)