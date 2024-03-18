package scheduler;
import common.Log;
import floor.ElevatorRequest;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * This state sends the request and chosen elevatorID to the ElevatorSubsystem
 */
public class ElevatorDispatchState implements SchedulerState {

    /**
     * Send the request and transition to AwaitingRequestState
     * @param scheduler the scheduler
     * @param request the request to send
     * @param elevatorID the chosen elevator
     */
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

    /**
     * Send the elevatorRequest and elevatorID to the ElevatorSubsystem via UDP
     * @param request the request
     * @param elevatorID the chosen elevatorID
     */
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

