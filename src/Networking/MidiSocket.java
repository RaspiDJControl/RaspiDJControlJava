package Networking;

import Models.MidiCommand;
import Models.MidiMessage;
import Models.MidiNote;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maarten on 13/06/2015.
 */
public class MidiSocket {
    private ServerSocket serverSocket;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private List<MidiSocketListener> listeners = new ArrayList<MidiSocketListener>();

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public boolean isConnected(){
        return !serverSocket.isClosed();
    }

    public MidiSocket(int port){
        try {
            serverSocket = new ServerSocket(port, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void StartListening() throws IOException {
        socket = serverSocket.accept();
        for(MidiSocketListener listener : listeners){
            listener.SocketConnected();
        }

        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        while(true){
            byte[] buffer = new byte[3];
            inputStream.read(buffer);

            String bufferString = bytesToHex(buffer);

            MidiMessage message = new MidiMessage(
                    MidiCommand.valueOf(buffer[0]),
                    MidiNote.valueOf(buffer[1]),
                    buffer[2]
            );

            for(MidiSocketListener listener : listeners){
                listener.MidiMessageReceived(message);
            }
        }
    }

    public void addMidiListener(MidiSocketListener listener){
        listeners.add(listener);
    }

    public void SendMidiMessage(MidiMessage message){
        if(!socket.isConnected()) return;
        try {
            outputStream.write(message.toByteArray());
        } catch (IOException e) {
            return;
        }
    }
}
