package IO;

import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maarten on 4/07/2015.
 */
public class PCF8591Reader implements Runnable {
    private List<PCF8591Listener> listeners = new ArrayList();
    private int[] previousValues = new int[4];
    private int[] values = new int[4];
    private I2CDevice i2CDevice;

    public PCF8591Reader(I2CDevice PCF8591Device) {
        this.i2CDevice = PCF8591Device;
        values = new int[]{128, 128, 128, 128};
        previousValues = new int[]{0, 0, 0, 0};
    }

    public void addListener(PCF8591Listener listener){
        listeners.add(listener);
    }

    @Override
    public void run() {
        while(true){
            for(byte i=0x00; i<0x04; i++){
                try {
                    i2CDevice.write((byte) (i + 0x40)); // first 4 bits = 0100 ? Keep output enabled, last 4 bits (i): input channel
                    i2CDevice.read();

                    previousValues[i] = values[i];
                    values[i] = 255 - i2CDevice.read(); // invert readings (0 - 255 ? 255 - 0)

                    if(previousValues[i] != values[i])
                        for(PCF8591Listener listener : listeners)
                            listener.ValueChanged(i, values[i]);

                    Thread.sleep(15);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}