package IO;

import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;

/**
 * Created by Maarten on 2/09/2015.
 */
public class PCF8591Writer {
    private I2CDevice i2CDevice;

    public PCF8591Writer(I2CDevice i2CDevice) {
        this.i2CDevice = i2CDevice;
    }

    public void WriteValue(int value){
        try {
            value = (int)((value * 0.16) + 124);
            byte[] bytes = {0x40, (byte)value};
            i2CDevice.write(bytes, 0, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
