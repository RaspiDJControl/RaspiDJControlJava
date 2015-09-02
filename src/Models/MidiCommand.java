package Models;

/**
 * Created by Maarten on 13/06/2015.
 */
public enum MidiCommand {
    NOTE_OFF(0x80),
    NOTE_ON(0x90),
    CC_MSG(0xB0),
    UNUSED(0x00);

    private byte value;

    MidiCommand(int value) {
        this.value = (byte)value;
    }

    public byte getValue() {
        return value;
    }

    public static MidiCommand valueOf(byte b)
    {
        for(MidiCommand num : MidiCommand.values())
            if(b == num.getValue()) return num;

        return UNUSED;
    }
}
