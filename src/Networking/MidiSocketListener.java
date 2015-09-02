package Networking;

import Models.MidiMessage;

/**
 * Created by Maarten on 21/06/2015.
 */
public interface MidiSocketListener {
    void MidiMessageReceived(MidiMessage message);
    void SocketConnected();
}
