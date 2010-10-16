/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

import java.util.ArrayList;
import java.util.Collections;

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
       return (altsetting == null) ? 0 : altsetting.length;
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

    /**
     * Determines if the given interface belongs to the given class
     *
     * @param c the class to look for
     *
     * @return true if one of the interface belongs to the given class,
     *         false otherwise
     */
    public boolean belongsToClass(byte c) {
        for (int i=0; i<getNumAlternateSetting(); ++i) {
            if (altsetting[i].getInterfaceClass() == c) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the given interface belongs to the given sub class
     *
     * @param subc the subclass to look for
     *
     * @return true if one of the interface belongs to the given sub-class,
     *         false otherwise
     */
    public boolean belongsToSubClass(byte subc) {
        for (int i=0; i<getNumAlternateSetting(); ++i) {
            if (altsetting[i].getInterfaceSubClass() == subc) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the given interface supports to the given protocol
     *
     * @param p the protocol to look for
     *
     * @return true if one of the interface supports the given protocol,
     *         false otherwise
     */
    public boolean supportsProtocol(byte p) {
        for (int i=0; i<getNumAlternateSetting(); ++i) {
            if (altsetting[i].getInterfaceProtocol() == p) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns all endpoints for all interfaces
     *
     * @return all endpoints for all interfaces
     */
    public ArrayList<UsbEndpointDescriptor> getAllEndpoints() {
        ArrayList<UsbEndpointDescriptor> ret =
            new ArrayList<UsbEndpointDescriptor>();

        if (altsetting != null) {
            for(UsbInterfaceDescriptor d: altsetting) {
                Collections.addAll(ret, d.getEndpoints());
            }
        }

        return ret;
    }
}
