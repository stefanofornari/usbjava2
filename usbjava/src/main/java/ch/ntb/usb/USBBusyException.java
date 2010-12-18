/*
 * Java libusb 2 wrapper
 * Copyright (c) 2010 Stefano Fornari
 *
 * http://code.google.com/p/usbjava
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

public class USBBusyException extends USBException {

    public USBBusyException(String string) {
        super(string);
    }
}
