/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

/**
 * Represents an USB device.<br>
 * An USB device has one device descriptor and it may have multiple
 * configuration descriptors.
 * 
 */
public class UsbDevice {

	private UsbDevice next, prev;

	private String filename;

	private UsbBus bus;

	private UsbDeviceDescriptor descriptor;

	private UsbConfigDescriptor[] config;

	private byte devnum;

	private byte num_children;

	private UsbDevice children;

	/**
	 * The address of the device structure to be passed to usb_open. This value
	 * is used only internally so we don't use getter or setter methods.
	 */
	private long devStructAddr;

        /**
         * Creates a new UsbDevice
         */
        public UsbDevice() {
        }

        /**
         * Creates a new UsbDevice
         */
        public UsbDevice(
            String filename,
            UsbDeviceDescriptor descriptor,
            UsbConfigDescriptor[] config,
            byte devnum,
            byte num_children,
            UsbDevice children) {
            
            this.filename = filename;
            this.descriptor = descriptor;
            this.config = config;
            this.devnum = devnum;
            this.num_children = num_children;
            this.children = children;
        }

	/**
	 * Returns the reference to the bus to which this device is connected.<br>
	 * 
	 * @return the reference to the bus to which this device is connected
	 */
	public UsbBus getBus() {
		return bus;
	}

	/**
	 * Returns a reference to the first child.<br>
	 * 
	 * @return a reference to the first child
	 */
	public UsbDevice getChildren() {
		return children;
	}

	/**
	 * Returns the USB config descriptors.<br>
	 * 
	 * @return the USB config descriptors
	 */
	public UsbConfigDescriptor[] getConfig() {
		return config;
	}

	/**
	 * Returns the USB device descriptor.<br>
	 * 
	 * @return the USB device descriptor
	 */
	public UsbDeviceDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * Returns the number assigned to this device.<br>
	 * 
	 * @return the number assigned to this device
	 */
	public byte getDevnum() {
		return devnum;
	}

	/**
	 * Returns the systems String representation.<br>
	 * 
	 * @return the systems String representation
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Returns the pointer to the next device.<br>
	 * 
	 * @return the pointer to the next device or null
	 */
	public UsbDevice getNext() {
		return next;
	}

	/**
	 * Returns the number of children of this device.<br>
	 * 
	 * @return the number of children of this device
	 */
	public byte getNumChildren() {
		return num_children;
	}

	/**
	 * Returns the pointer to the previous device.<br>
	 * 
	 * @return the pointer to the previous device or null
	 */
	public UsbDevice getPrev() {
		return prev;
	}

    /**
     * @param next the next to set
     */
    public void setNext(UsbDevice next) {
        this.next = next;
    }

    /**
     * @param prev the prev to set
     */
    public void setPrev(UsbDevice prev) {
        this.prev = prev;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @param bus the bus to set
     */
    public void setBus(UsbBus bus) {
        this.bus = bus;
    }

    /**
     * @param descriptor the descriptor to set
     */
    public void setDescriptor(UsbDeviceDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(UsbConfigDescriptor[] config) {
        this.config = config;
    }

    /**
     * @param devnum the devnum to set
     */
    public void setDevnum(byte devnum) {
        this.devnum = devnum;
    }

    /**
     * @param num_children the num_children to set
     */
    public void setNum_children(byte num_children) {
        this.num_children = num_children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(UsbDevice children) {
        this.children = children;
    }

    /**
     * @param devStructAddr the devStructAddr to set
     */
    public void setDevStructAddr(long devStructAddr) {
        this.devStructAddr = devStructAddr;
    }

	@Override
	public String toString() {
		return "Usb_Device " + filename;
	}
}