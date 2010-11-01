/*
 * Java libusb wrapper
 * Copyright (c) 2010 Stefano Fornari
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

import java.io.IOException;
import java.io.OutputStream;

public class BulkOutputStream extends OutputStream {

    private int    address;
    private Device dev    ;

    BulkOutputStream(Device dev, int address) {
        this.dev     = dev    ;
        this.address = address;
    }

    @Override
    public void write(int value)
            throws IOException {
        byte temp[] = new byte[]{(byte) value};
        dev.writeBulk(address, temp, temp.length, Device.DEFAULT_TIMEOUT, false);
    }

    @Override
    public void write(byte buf[], int off, int len)
            throws IOException {
        if (off == 0 && len == buf.length) {
            dev.writeBulk(address, buf, buf.length, Device.DEFAULT_TIMEOUT, false);
        } else {
            // extra copy forced by RMI
            byte temp[] = new byte[len];
            System.arraycopy(buf, off, temp, 0, len);
            dev.writeBulk(address, temp, temp.length, Device.DEFAULT_TIMEOUT, false);
        }
    }
}
