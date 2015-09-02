package IO;

import java.awt.event.ActionListener;

/**
 * Created by Maarten on 4/07/2015.
 */
public interface PCF8591Listener {
    void ValueChanged(int channel, int value);
}
