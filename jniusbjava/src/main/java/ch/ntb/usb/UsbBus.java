/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

/**
 * Represents an USB bus.<br>
 * This is the root class for the representation of the libusb USB structure.
 * Zero or more devices may be connected to an USB bus.
 * 
 */
public class UsbBus {

	private UsbBus next, prev;

	private String dirname;

	private UsbDevice devices;

	private long location;

	private UsbDevice root_dev;

        /**
         * Creates a new UsbBus
         *
         * @param dirname
         * @param devices
         * @param location
         */
        public UsbBus() {
            this(null, null, 0);
        }

        /**
         * Creates a new UsbBus
         *
         * @param dirname
         * @param devices
         * @param location
         */
        public UsbBus(
            String dirname, UsbDevice devices, long location
        ) {
            this.dirname  = dirname;
            this.devices  = devices;
            this.location = location;
        }

	/**
	 * Get the first device ojects of the devices linked list.<br>
	 * 
	 * @return the first device ojects of the devices linked list or null
	 */
	public UsbDevice getDevices() {
		return devices;
	}

	/**
	 * Returns the systems String representation of the bus.<br>
	 * 
	 * @return the systems String representation of the bus
	 */
	public String getDirname() {
		return dirname;
	}

	/**
	 * Returns the next bus object.<br>
	 * 
	 * @return Returns the next bus object or null
	 */
	public UsbBus getNext() {
		return next;
	}

	/**
	 * Returns the previous bus object.<br>
	 * 
	 * @return Returns the previous bus object or null
	 */
	public UsbBus getPrev() {
		return prev;
	}

	/**
	 * Get the root device of this bus.<br>
	 * 
	 * @return the root device oject or null
	 */
	public UsbDevice getRootDev() {
		return root_dev;
	}

	/**
	 * Returns the location in the USB bus linked list.<br>
	 * 
	 * @return the location in the USB bus linked list
	 */
	public long getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return "Usb_Bus " + dirname;
	}
}