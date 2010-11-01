/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ntb.usb;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author ste
 */
public class BulkInputStream extends InputStream {

    private int    address;
    private Device dev    ;

    BulkInputStream(Device dev, int address) {
        this.dev     = dev    ;
        this.address = address;
    }

    @Override
    public int read()
            throws IOException {
        byte[] temp = new byte[1];

        dev.readBulk(address, temp, temp.length, Device.DEFAULT_TIMEOUT, false);

        return 0xff & temp[0];
    }

    @Override
    public int read(byte buf[], int off, int len)
            throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException();
        }

        byte[] temp = new byte[len];
        int ret = dev.readBulk(address, temp, len, Device.DEFAULT_TIMEOUT, false);
        System.arraycopy(temp, 0, buf, off, temp.length);
        return ret;
    }
}
