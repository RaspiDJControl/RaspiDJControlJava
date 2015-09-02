package Models;

/**
 * Created by Maarten on 16/06/2015.
 */
public enum MidiNote {
    JOG_WHEEL(0x01),
    PFL_1(0x02),
    PFL_2(0x03),
    PLAY(0x04),
    PAUSE(0x05),
    SYNC(0x06),
    CROSSFADER(0x07),
    GAIN(0x08),
    TEMPO(0x09),
    MID_1(0x10),
    MID_2(0x11),
    BASS_1(0x12),
    BASS_2(0x13),
    LED_PFL_1(0x15),
    LED_PFL_2(0x16),
    LED_PLAY(0x17),
    LOOP_IN(0x18),
    LOOP_OUT(0x19),
    LEFT(0x20),
    RIGHT(0x21),
    PITCH_RESET(0x22),
    EFFECT(0x23),
    UNUSED(0x00);

    private byte value;

    MidiNote(int value) {
        this.value = (byte)value;
    }

    public byte getValue() {
        return value;
    }

    public static MidiNote valueOf(byte b)
    {
        for(MidiNote num : MidiNote.values())
            if(b == num.getValue()) return num;

        return UNUSED;
    }
}
