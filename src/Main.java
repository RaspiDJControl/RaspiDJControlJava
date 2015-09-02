import IO.*;
import Models.MidiCommand;
import Models.MidiMessage;
import Models.MidiNote;
import Networking.MidiSocket;
import Networking.MidiSocketListener;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * Created by Maarten on 13/06/2015.
 */
public class Main {
    static GpioController gpio;
    static GpioPinDigitalOutput led_pfl_1, led_pfl_2;
    static GpioPinDigitalInput rot_clk, rot_data, btn_pause, btn_play, btn_sync, btn_loop_in, btn_loop_out, btn_left, btn_right, btn_pitchreset, btn_effect, btn_pfl1, btn_pfl2;
    static I2CDevice PCF8591_1, PCF8591_2;
    static PCF8591Reader reader_1, reader_2;
    static PCF8591Writer writer_1;
    static MidiSocket socket;

    public static void main(String[] args) throws Exception {
        gpio = GpioFactory.getInstance();
        I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
        PCF8591_1 = bus.getDevice(0x48);
        PCF8591_2 = bus.getDevice(0x4f);

        led_pfl_1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_15, "LED_PFL_1", PinState.LOW);
        led_pfl_1.setShutdownOptions(true, PinState.LOW);
        led_pfl_2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_16, "LED_PFL_2", PinState.LOW);
        led_pfl_2.setShutdownOptions(true, PinState.LOW);

        rot_clk = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06, PinPullResistance.OFF);
        rot_clk.setDebounce(0);
        rot_data = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.OFF);
        rot_data.setDebounce(1);

        btn_play = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN);
        btn_pause = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN);
        btn_sync = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
        btn_pfl1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, PinPullResistance.PULL_DOWN);
        btn_pfl2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
        btn_loop_in = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_DOWN);
        btn_loop_out = gpio.provisionDigitalInputPin(RaspiPin.GPIO_10, PinPullResistance.PULL_DOWN);
        btn_left = gpio.provisionDigitalInputPin(RaspiPin.GPIO_12, PinPullResistance.PULL_DOWN);
        btn_right = gpio.provisionDigitalInputPin(RaspiPin.GPIO_11, PinPullResistance.PULL_DOWN);
        btn_pitchreset = gpio.provisionDigitalInputPin(RaspiPin.GPIO_13, PinPullResistance.PULL_DOWN);
        btn_effect = gpio.provisionDigitalInputPin(RaspiPin.GPIO_14, PinPullResistance.PULL_DOWN);

        btn_pause.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                OnButtonPressed(MidiNote.PAUSE, event);
            }
        });
        btn_play.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                OnButtonPressed(MidiNote.PLAY, event);
            }
        });
        btn_sync.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                OnButtonPressed(MidiNote.SYNC, event);
            }
        });
        rot_clk.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if(event.getState().isLow())
                    OnRotaryEvent(event);
            }
        });
        btn_pfl1.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                OnButtonPressed(MidiNote.PFL_1, event);
            }
        });
        btn_pfl2.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                OnButtonPressed(MidiNote.PFL_2, event);
            }
        });
        btn_loop_in.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                OnButtonPressed(MidiNote.LOOP_IN, event);
            }
        });
        btn_loop_out.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                OnButtonPressed(MidiNote.LOOP_OUT, event);
            }
        });
        btn_left.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                OnButtonPressed(MidiNote.LEFT, event);
            }
        });
        btn_right.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                OnButtonPressed(MidiNote.RIGHT, event);
            }
        });
        btn_pitchreset.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                OnButtonPressed(MidiNote.PITCH_RESET, event);
            }
        });
        btn_effect.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                OnButtonPressed(MidiNote.EFFECT, event);
            }
        });

        reader_1 = new PCF8591Reader(PCF8591_1);
        reader_1.addListener(new PCF8591Listener() {
            @Override
            public void ValueChanged(int channel, int value) {
                switch(channel){
                    case 0:
                        socket.SendMidiMessage(new MidiMessage(
                                MidiCommand.CC_MSG,
                                MidiNote.BASS_1,
                                (byte)(value)
                        ));
                        break;
                    case 1:
                        socket.SendMidiMessage(new MidiMessage(
                                MidiCommand.CC_MSG,
                                MidiNote.CROSSFADER,
                                (byte)(value)
                        ));
                        break;
                    case 2:
                        socket.SendMidiMessage(new MidiMessage(
                                    MidiCommand.CC_MSG,
                                    MidiNote.TEMPO,
                                    (byte)(value)
                        ));
                        break;
                }
            }
        });

        reader_2 = new PCF8591Reader(PCF8591_2);
        reader_2.addListener(new PCF8591Listener() {
            @Override
            public void ValueChanged(int channel, int value) {
                switch(channel){
                    case 0:
                        socket.SendMidiMessage(new MidiMessage(
                                MidiCommand.CC_MSG,
                                MidiNote.MID_1,
                                (byte)(value)
                        ));
                        break;
                    case 1:
                        socket.SendMidiMessage(new MidiMessage(
                                MidiCommand.CC_MSG,
                                MidiNote.BASS_2,
                                (byte)(value)
                        ));
                        break;
                    case 2:
                        socket.SendMidiMessage(new MidiMessage(
                                MidiCommand.CC_MSG,
                                MidiNote.MID_2,
                                (byte)(value)
                        ));
                        break;
                    case 3:
                        socket.SendMidiMessage(new MidiMessage(
                                MidiCommand.CC_MSG,
                                MidiNote.GAIN,
                                (byte)(value)
                        ));
                        break;
                }
            }
        });

        writer_1 = new PCF8591Writer(PCF8591_1);

        System.out.print("Waiting for connection... ");
        socket = new MidiSocket(5008);
        socket.addMidiListener(new MidiSocketListener() {
            @Override
            public void MidiMessageReceived(MidiMessage message) {
                OnMidiMessageReceived(message);
            }

            @Override
            public void SocketConnected() {
                System.out.println("Connected!");
                new Thread(reader_1).start();
                new Thread(reader_2).start();
            }
        });


        socket.StartListening();

    }

    public static void OnMidiMessageReceived(MidiMessage message){
        System.out.println("Received: " + message.toString());

        switch(message.getNote()){
            case LED_PFL_1:
                led_pfl_1.setState(message.getValue() != 0);
                break;
            case LED_PFL_2:
                led_pfl_2.setState(message.getValue() != 0);
                break;
            case LED_PLAY:
               writer_1.WriteValue(message.getValue());
                break;
        }
    }

    public static void OnButtonPressed(MidiNote note, GpioPinDigitalStateChangeEvent e){
        socket.SendMidiMessage(new MidiMessage(
                e.getState().isHigh() ? MidiCommand.NOTE_ON : MidiCommand.NOTE_OFF,
                note
        ));
    }

    public static void OnRotaryEvent(GpioPinDigitalStateChangeEvent e){
        socket.SendMidiMessage(new MidiMessage(
                MidiCommand.CC_MSG,
                MidiNote.JOG_WHEEL,
                (byte)(0x40 + (rot_data.getState().isLow() ? 2 : -2))
        ));
    }
}