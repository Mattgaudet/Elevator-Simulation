package test;
import floor.ElevatorRequest;
import floor.Floor;
import floor.FloorSubsystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scheduler.Scheduler;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static org.junit.Assert.assertEquals;

public class TestUDPCommunications {

    @Test
    public void testUdpCommunication() throws Exception {
        int testServerPort = 12346;
        DatagramSocket serverSocket = new DatagramSocket(testServerPort);
        DatagramSocket clientSocket = new DatagramSocket();

        byte[] sendData = "Test Message".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), testServerPort);

        clientSocket.send(sendPacket); // Send a test message

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        serverSocket.receive(receivePacket); // Receive the message

        String receivedMessage = new String(receivePacket.getData()).trim();

        assertEquals("Test Message", receivedMessage);

        serverSocket.close();
        clientSocket.close();
    }

    @Test
    public void testUdpFromFloorToScheduler() throws Exception {
        int testSchedulerPort = 5000;
        DatagramSocket serverSocket = new DatagramSocket(testSchedulerPort);
        DatagramSocket clientSocket = new DatagramSocket();

        //ElevatorRequest testEr = new ElevatorRequest("14:15:05.000; 2; Up; 4; BAD_REQUEST".getBytes());
        byte[] sendData = "14:15:05.000; 2; Up; 4; BAD_REQUEST".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), testSchedulerPort);

        clientSocket.send(sendPacket); // Send a test message

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        serverSocket.receive(receivePacket); // Receive the message

        String receivedMessage = new String(receivePacket.getData()).trim();

        assertEquals("14:15:05.000; 2; Up; 4; BAD_REQUEST", receivedMessage);

        serverSocket.close();
        clientSocket.close();
    }
}
