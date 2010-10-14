/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

/**
 * Represents an USB interface.<br>
 * An interface is a group of alternate settings of a configuration.<br>
 * 
 */
public class UsbInterface {

    /**
     * Maximal number of alternate settings
     */
    public static final int USB_MAXALTSETTING = 128; /* Hard limit */

    private UsbInterfaceDescriptor[] altsetting;
    private int num_altsetting;

    @Override
    public String toString() {
        return "Usb_Interface num_altsetting: 0x"
                + Integer.toHexString(num_altsetting);
    }

    /**
     * Retuns an array of interface descriptors.<br>
     *
     * @return an array of interface descriptors
     */
    public UsbInterfaceDescriptor[] getAlternateSetting() {
        return altsetting;
    }

    /**
     * Returns the number of alternate settings.<br>
     *
     * @return the number of alternate settings
     */
    public int getNumAlternateSetting() {
        return num_altsetting;
    }

    /**
     * @param altsetting the altsetting to set
     */
    public void setAlternateSetting(UsbInterfaceDescriptor[] altsetting) {
        this.altsetting = altsetting;
    }

    /**
     * @param num_altsetting the num_altsetting to set
     */
    public void setNumAlternateSetting(int num_altsetting) {
        this.num_altsetting = num_altsetting;
    }
}
