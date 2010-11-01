/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

import java.io.IOException;

public class USBException extends IOException {

    public USBException(String string) {
        super(string);
    }

    public USBException(String string, Throwable t) {
        super(string, t);
    }

    /**
     * Returns true iff the exception indicates a (bulk) endpoint has
     * stalled; these are used as error indicators in device protocols.
     *
     * For now this is just imported from jusb with a default implementation.
     */
    public boolean isStalled() {
        return false;
    };

    /**
     *
     */
    private static final long serialVersionUID = 1690857437804284710L;
}
