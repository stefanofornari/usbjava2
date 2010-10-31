/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

public class USBTimeoutException extends USBException {

    public USBTimeoutException(String string) {
        super(string);
    }

    /**
     * Returns true iff the exception indicates a (bulk) endpoint has
     * stalled; these are used as error indicators in device protocols.
     *
     * For now this is just imported from jusb with a default implementation.
     */
    @Override
    public boolean isStalled() {
        return true;
    }

    ;
    /**
     *
     */
    private static final long serialVersionUID = -1065328371159778249L;
}
