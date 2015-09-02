package Models;

/**
 * Created by Maarten on 13/06/2015.
 */
public class MidiMessage {
    private MidiCommand midiCommand;
    private MidiNote note;
    private byte value;

    public MidiMessage(MidiCommand command, MidiNote note, byte value) {
        this.midiCommand = command;
        this.note = note;
        this.value = value;
    }

    public MidiMessage(Models.MidiCommand command, MidiNote note) {
        this.midiCommand = command;
        this.note = note;
        this.value = 127;
    }

    public MidiCommand getMidiCommand() {
        return midiCommand;
    }

    public void setMidiCommand(MidiCommand command) {
        this.midiCommand = command;
    }

    public MidiNote getNote() {
        return note;
    }

    public void setNote(MidiNote note) {
        this.note = note;
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    public byte[] toByteArray(){
        byte[] result = new byte[3];
        result[0] = midiCommand.getValue();
        result[1] = note.getValue();
        result[2] = value;
        return result;
    }

    @Override
    public String toString() {
        return "MidiMessage{" +
                "midiCommand=" + midiCommand +
                ", note=" + note +
                ", value=" + value +
                '}';
    }
}
