package scheduler;
import common.Log;
import floor.ElevatorRequest;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;




public class ElevatorDispatchState implements SchedulerState {
    

    @Override
    public void processRequest(Scheduler scheduler, ElevatorRequest request, int elevatorID) {
        sendRequestToElevator(request, elevatorID);

        Log.print("Scheduler: Request sent to elevator " + elevatorID + " for floor " + request.getFloorNumber() + " and direction " + request.getButtonDirection() + ".");

        // Transition back to the AwaitingRequestState
        scheduler.setState(new AwaitingRequestState(scheduler));
    }

    @Override
    public void processRequest(Scheduler scheduler, byte[] requestData) {
        // Not needed
        
    }

    public void sendRequestToElevator(ElevatorRequest request, int elevatorID){
        byte[] requestData = request.getBytes();
        byte[] elevatorIDData = ByteBuffer.allocate(4).putInt(elevatorID).array(); // 4 bytes for an int

        // Combine requestData and elevatorIDData into sendData
        byte[] sendData = new byte[requestData.length + elevatorIDData.length];

        System.arraycopy(requestData, 0, sendData, 0, requestData.length);
        System.arraycopy(elevatorIDData, 0, sendData, requestData.length, elevatorIDData.length);

        int elevatorSubsystemPort = 6000; // The port the ElevatorSubsystem is listening on
        String elevatorSubsystemHost = "localhost"; // Assuming the ElevatorSubsystem is on the same host

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress elevatorSubsystemAddress = InetAddress.getByName(elevatorSubsystemHost);

            // Send the Elevator request with the ID
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, elevatorSubsystemAddress, elevatorSubsystemPort);
            socket.send(sendPacket);

        } catch (UnknownHostException e) {
            System.err.println("UnknownHostException: " + e.getMessage());
        } catch (SocketException e) {
            System.err.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
}










































// package scheduler;

// import elevator.Elevator;
// import elevator.ElevatorInfo;
// import elevator.ElevatorSubsystem;
// import floor.ElevatorRequest.ButtonDirection;
// import floor.ElevatorRequest;
// import floor.FloorSubsystem;

// import java.io.IOException;
// import java.net.*;
// import java.nio.ByteBuffer;
// import java.time.LocalTime;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;

// import common.Log;


// public class SendingToElevatorState {
    
// }




// /**
//      * TODO: This is the processing state, probably.
//      * This is where the scheduler is selecting which elevator to send the request too
//      * @param requestData the received request in bytes format via UDP
//      */
//     public void scheduleElevatorRequest(byte[] requestData) {
//         ElevatorRequest request = parseRequestFromFloorSubsystem(requestData);
//         // Print the details of the parsed request
//         if (request != null) {
//             System.out.println("Received and parsed request: " + request.toString());
//         } else {
//             System.out.println("Failed to parse the request from received data.");
//         }
//         addToRequestQueue(request); // reusing previous method
//         String elevatorsInfo = getElevatorsInfo();
//         int elevatorID = selectElevator(request, elevatorsInfo); // currently, always 0
//         // send the request to the selected elevator on the requestPort
//         sendRequestToElevator(request, elevatorID);
//     }


//     public void sendRequestToElevator(ElevatorRequest request, int elevatorID){
//         byte[] requestData = request.getBytes();
//         byte[] elevatorIDData = ByteBuffer.allocate(4).putInt(elevatorID).array(); // 4 bytes for an int

//         // Combine requestData and elevatorIDData into sendData
//         byte[] sendData = new byte[requestData.length + elevatorIDData.length];

//         System.arraycopy(requestData, 0, sendData, 0, requestData.length);
//         System.arraycopy(elevatorIDData, 0, sendData, requestData.length, elevatorIDData.length);

//         int elevatorSubsystemPort = 6000; // The port the ElevatorSubsystem is listening on
//         String elevatorSubsystemHost = "localhost"; // Assuming the ElevatorSubsystem is on the same host

//         try (DatagramSocket socket = new DatagramSocket()) {
//             InetAddress elevatorSubsystemAddress = InetAddress.getByName(elevatorSubsystemHost);

//             // Send the Elevator request with the ID
//             DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, elevatorSubsystemAddress, elevatorSubsystemPort);
//             socket.send(sendPacket);

//         } catch (UnknownHostException e) {
//             System.err.println("UnknownHostException: " + e.getMessage());
//         } catch (SocketException e) {
//             System.err.println("SocketException: " + e.getMessage());
//         } catch (IOException e) {
//             System.err.println("IOException: " + e.getMessage());
//         }
//     }
