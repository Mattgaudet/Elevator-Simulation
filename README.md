# Java Elevator Simulation Project (SYSC-3303)

## Description
The goal of this project is to simulate an elevator system and showcase how elevators, floors, and a scheduler work together to handle elevator requests efficiently. The system uses a CSV parser to read input requests and a logging system to debug and monitor the process. The system is composed of three important components: a multithreaded controller (Scheduler), an elevator car simulator (with lights, buttons, doors, and motors), and a floor simulator (with buttons, lights, and simulated people)

## Installation
- Ensure Java OpenJDK 20.0.2 is installed on your system. 
- Clone this repository and navigate into the project directory.
- Compile the Java files using your preferred IDE that supports Java. That said, it is recommended to use IntelliJ IDEA 2023.2.5 (Community Edition) for best results. 

## Usage
- Run `Main.java` to start the simulation.
- Input requests can be modified in the CSV file specified by `input.txt` in the res folder.

## Components
- **config**
  - `Config.java`: Defines constants used across the system, such as the time required to load/unload passengers and to open/close elevator doors, as well as the speed of the elevator. 

- **elevator**
  - `Elevator.java`: Defines the elevator's properties and actions.
  - `ElevatorSubsystem.java`: Acts as the control system for one or more elevators. It interfaces with the Scheduler to receive elevator requests and dispatches elevators to fulfill these requests. The subsystem manages the state and operation of each elevator, ensuring requests are processed efficiently.

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
  - `Scheduler.java`: Implements the logic required to queue and dispatch elevator requests to the ElevatorSubsystem and then communicates the outcome of these requests back to the originating FloorSubsystem. This class effectively bridges the gap between the request generation (floors) and request fulfillment (elevators) components of the simulation.

- **test**
  - `CSVParserTest.java`: Tests the functionality of the CSV parser to ensure reliability.
  - `FloorSubsystemTest.java`: Tests the functionality of the floor subsystem.
  - `SchedulerTest.java`: Tests the functionality of the scheduler.
  - `TestElevator.java`: Tests the functionality of the elevator class.
  - `TestElevatorSubsystem.java`: Tests the functionality of the elevator subsystem.
  - `TestFloor.java`: Tests the functionality of the floor class.

## Contributing - Group 7 (Lab A1)
- Ali Abdollahian (101229396) 
- Jaan Soulier  (101189819)
- Jarnail Singh (101228231)
- Laurence Lamarche-Cliche (101173070) 
- Matthew Gaudet (101193256)

## Team Contributions for Iteration 1

- Ali Abdollahian
  - UML Class Diagram: Created the UML class diagram to outline the project's structure and relationships between classes.
  - Testing: Added several tests to evaluate the functionality of different classes, including FloorSubsystem, Scheduler, and ElevatorSubsystem, ensuring robust testing coverage for the project's components

- Jaan Soulier
  - Javadocs: Responsible for creating Javadocs-style comments for the project, ensuring that all classes, methods, and functionalities are well-documented.
  - Added Log.java, which is used for logging informational messages throughout the project. 

- Jarnail Singh
  - README.txt: Added the README.md file, providing an overview and instructions for the project.
  - Code Implementation: Added the project's starter code and implemented the logic for back-and-forth request ping between FloorSubsystem, Scheduler, and ElevatorSubsystem.
    
- Laurence Lamarche-Cliche
  - CSV Reader Integration: Added and Merged the CSV reader functionality into the existing FloorSubsystem package.
  - Added Command line file path option to specify which txt file should provide the input data to the Elevator System
  - Testing: Created and placed tests in the src/test directory, ensuring complete testing coverage for the project's components.

- Matthew Gaudet
  - UML Sequence Diagram: Created the UML sequence diagram to visualize the sequence of operations based on the project requirements.
  - Added logic that enables the system to exit properly once all requests are completed.


