/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import ch.ntb.usb.logger.LogUtil;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class manages all USB devices and defines some USB specific constants.<br>
 * 
 */
public class USB {

    // Standard requests (USB spec 9.4)
    /**
     * This request returns status for the specified recipient (USB spec 9.4.5).
     *
     * @see ch.ntb.usb.Device#controlMsg(int, int, int, int, byte[], int, int,
     *      boolean)
     */
    public static final int REQ_GET_STATUS = 0x00;
    /**
     * This request is used to clear or disable a specific feature (USB spec
     * 9.4.1).
     *
     * @see ch.ntb.usb.Device#controlMsg(int, int, int, int, byte[], int, int,
     *      boolean)
     */
    public static final int REQ_CLEAR_FEATURE = 0x01;
    // 0x02 is reserved
    /**
     * This request is used to set or enable a specific feature (USB spec
     * 9.4.9).
     *
     * @see ch.ntb.usb.Device#controlMsg(int, int, int, int, byte[], int, int,
     *      boolean)
     */
    public static final int REQ_SET_FEATURE = 0x03;
    // 0x04 is reserved
    /**
     * This request sets the device address for all future device accesses (USB
     * spec 9.4.6).
     *
     * @see ch.ntb.usb.Device#controlMsg(int, int, int, int, byte[], int, int,
     *      boolean)
     */
    public static final int REQ_SET_ADDRESS = 0x05;
    /**
     * This request returns the specified descriptor if the descriptor exists
     * (USB spec 9.4.3).
     *
     * @see ch.ntb.usb.Device#controlMsg(int, int, int, int, byte[], int, int,
     *      boolean)
     */
    public static final int REQ_GET_DESCRIPTOR = 0x06;
    /**
     * This request is optional and may be used to update existing descriptors
     * or new descriptors may be added (USB spec 9.4.8).
     *
     * @see ch.ntb.usb.Device#controlMsg(int, int, int, int, byte[], int, int,
     *      boolean)
     */
    public static final int REQ_SET_DESCRIPTOR = 0x07;
    /**
     * This request returns the current device configuration value (USB spec
     * 9.4.2).
     *
     * @see ch.ntb.usb.Device#controlMsg(int, int, int, int, byte[], int, int,
     *      boolean)
     */
    public static final int REQ_GET_CONFIGURATION = 0x08;
    /**
     * This request sets the device configuration (USB spec 9.4.7).
     *
     * @see ch.ntb.usb.Device#controlMsg(int, int, int, int, byte[], int, int,
     *      boolean)
     */
    public static final int REQ_SET_CONFIGURATION = 0x09;
    /**
     * This request returns the selected alternate setting for the specified
     * interface (USB spec 9.4.4).
     *
     * @see ch.ntb.usb.Device#controlMsg(int, int, int, int, byte[], int, int,
     *      boolean)
     */
    public static final int REQ_GET_INTERFACE = 0x0A;
    /**
     * This request allows the host to select an alternate setting for the
     * specified interface (USB spec 9.4.10).
     *
     * @see ch.ntb.usb.Device#controlMsg(int, int, int, int, byte[], int, int,
     *      boolean)
     */
    public static final int REQ_SET_INTERFACE = 0x0B;
    /**
     * This request is used to set and then report an endpoint�s synchronization
     * frame (USB spec 9.4.11).
     *
     * @see ch.ntb.usb.Device#controlMsg(int, int, int, int, byte[], int, int,
     *      boolean)
     */
    public static final int REQ_SYNCH_FRAME = 0x0C;
    // data transfer direction (USB spec 9.3)
    /**
     * Identifies the direction of data transfer in the second phase of the
     * control transfer.<br>
     * The state of the Direction bit is ignored if the wLength field is zero,
     * signifying there is no Data stage.<br>
     * Specifies bit 7 of bmRequestType.
     *
     * @see ch.ntb.usb.Device#controlMsg(int, int, int, int, byte[], int, int,
     *      boolean)
     */
    public static final int REQ_TYPE_DIR_HOST_TO_DEVICE = (0x00 << 7),
            REQ_TYPE_DIR_DEVICE_TO_HOST = (0x01 << 7);
    // request types (USB spec 9.3)
    /**
     * Specifies the type of the request.<br>
     * Specifies bits 6..5 of bmRequestType.
     *
     * @see ch.ntb.usb.Device#controlMsg(int, int, int, int, byte[], int, int,
     *      boolean)
     */
    public static final int REQ_TYPE_TYPE_STANDARD = (0x00 << 5),
            REQ_TYPE_TYPE_CLASS = (0x01 << 5),
            REQ_TYPE_TYPE_VENDOR = (0x02 << 5),
            REQ_TYPE_TYPE_RESERVED = (0x03 << 5);
    // request recipient (USB spec 9.3)
    /**
     * Specifies the intended recipient of the request.<br>
     * Requests may be directed to the device, an interface on the device, or a
     * specific endpoint on a device. When an interface or endpoint is
     * specified, the wIndex field identifies the interface or endpoint.<br>
     * Specifies bits 4..0 of bmRequestType.
     *
     * @see ch.ntb.usb.Device#controlMsg(int, int, int, int, byte[], int, int,
     *      boolean)
     */
    public static final int REQ_TYPE_RECIP_DEVICE = 0x00,
            REQ_TYPE_RECIP_INTERFACE = 0x01, REQ_TYPE_RECIP_ENDPOINT = 0x02,
            REQ_TYPE_RECIP_OTHER = 0x03;
    /**
     * The maximum packet size of a bulk transfer when operating in highspeed
     * (480 MB/s) mode.
     */
    public static int HIGHSPEED_MAX_BULK_PACKET_SIZE = 512;
    /**
     * The maximum packet size of a bulk transfer when operating in fullspeed
     * (12 MB/s) mode.
     */
    public static int FULLSPEED_MAX_BULK_PACKET_SIZE = 64;
    private static final Logger logger = LogUtil.getLogger("ch.ntb.usb");
    private static LinkedList<Device> devices = new LinkedList<Device>();
    private static boolean initUSBDone = false;

    /**
     * Create a new device an register it in a device list. If the device is
     * already registered, a reference to it will be returned.<br>
     * After resetting or re-attaching a device the busName and filename may
     * change. You can unregister the current device instance (see
     * {@link #unregisterDevice(Device)}) and get a new instance with the
     * updated bus and filename.
     *
     * @param idVendor
     *            the vendor id of the USB device
     * @param idProduct
     *            the product id of the USB device
     * @param busName
     *            optional name of the bus which can be used to distinguish
     *            multiple devices with the same vendor and product id.<br>
     *            see {@link UsbBus#getDirname()}
     * @param filename
     *            optional filename which can be used to distinguish multiple
     *            devices with the same vendor and product id.<br>
     *            see {@link UsbDevice#getFilename()}
     * @return a newly created device or an already registered device
     */
    public static Device getDevice(short idVendor, short idProduct,
            String busName, String filename) {

        // check if this device is already registered
        Device dev = getRegisteredDevice(idVendor, idProduct, busName, filename);
        if (dev != null) {
            logger.info("return already registered device: " + dev);
            return dev;
        }
        dev = new Device(idVendor, idProduct, busName, filename);
        logger.info("create new device: " + dev);
        devices.add(dev);
        return dev;
    }

    /**
     * See {@link #getUsbDevice(short, short, String, String)}. The parameter
     * <code>filename</code> and <code>busName</code>is set to null.
     *
     * @param idVendor
     * @param idProduct
     * @return a newly created device or an already registered device
     */
    public static Device getDevice(short idVendor, short idProduct) {
        return getDevice(idVendor, idProduct, null, null);
    }

    /**
     * Unregister a registered device.
     *
     * @param dev
     *            the device to unregister
     * @return true if the device has been removed, else false
     */
    public static boolean unregisterDevice(Device dev) {
        return devices.remove(dev);
    }

    /**
     * Get an already registered device or null if the device does not exist.<br>
     * To uniquely identify a device bus and filename should be set. If only one
     * of those is set the first device matching the criteria is returned.
     *
     * @param idVendor
     *            the vendor id of the USB device
     * @param idProduct
     *            the product id of the USB device
     * @param busName
     *            the name of the bus which can be used to distinguish multiple
     *            devices with the same vendor and product id.<br>
     *            see {@link UsbBus#getDirname()}
     * @param filename
     *            an optional filename which can be used to distinguish multiple
     *            devices with the same vendor and product id. see
     *            {@link UsbDevice#getFilename()}
     *
     * @return the device or null
     */
    private static Device getRegisteredDevice(short idVendor, short idProduct,
            String busName, String filename) {
        for (Iterator<Device> iter = devices.iterator(); iter.hasNext();) {
            Device dev = iter.next();
            // bus and filename
            if (busName != null && filename != null) {
                if (busName.compareTo(dev.getBusName() == null ? "" : dev.getBusName()) == 0
                        && filename.compareTo(dev.getFilename() == null ? ""
                        : dev.getFilename()) == 0
                        && dev.getVendorId() == idVendor
                        && dev.getProductId() == idProduct) {
                    return dev;
                }
            } else if (filename != null) {
                if (filename.compareTo(dev.getFilename() == null ? "" : dev.getFilename()) == 0
                        && dev.getVendorId() == idVendor
                        && dev.getProductId() == idProduct) {
                    return dev;
                }
            } else if (busName != null) {
                if (busName.compareTo(dev.getBusName() == null ? "" : dev.getBusName()) == 0
                        && dev.getVendorId() == idVendor
                        && dev.getProductId() == idProduct) {
                    return dev;
                }
            } else if (dev.getVendorId() == idVendor
                    && dev.getProductId() == idProduct) {
                return dev;
            }
        }
        return null;
    }

    /**
     * Returns the root {@link UsbBus} element.
     *
     * @return the root {@link UsbBus} element
     * @throws USBException
     */
    public static UsbBus getBus() throws USBException {
        if (!initUSBDone) {
            init();
        }
        LibusbJava.usb_find_busses();
        LibusbJava.usb_find_devices();

        UsbBus bus = LibusbJava.usb_get_busses();
        if (bus == null) {
            throw new USBException("LibusbJava.usb_get_busses(): "
                    + LibusbJava.usb_strerror());
        }

        //
        // At this point the UsbBus data structure is the one created
        // by the native code. I do not want to add any additional logic
        // to the native layer, therefore, bus is then processed below.
        // Client of this library should use USB instead of LibusbJava.
        //
        processBus(bus);

        return bus;
    }

    /**
     * Explicitly calls {@link LibusbJava#usb_init()}. Note that you don't need
     * to call this procedure as it is called implicitly when creating a new
     * device with {@link USB#getUsbDevice(short, short, String, String)}.
     */
    public static void init() {
        LibusbJava.usb_init();
        initUSBDone = true;
    }

    /**
     * Creates and returns an output stream for the given device and on a given
     * endpoint.
     * 
     * @param dev the device to stream to
     * @param ep the endpoint to use is must be bulk and not input
     * 
     * @return the output stream
     *
     * @throws IllegalArgumentException if the endpoint is not of appropriate type
     */
    public static OutputStream getOutputStream(Device dev, UsbEndpointDescriptor ep) {
        if (!ep.isTypeBulk() || ep.isInput())
	    throw new IllegalArgumentException ();
	return new BulkOutputStream (dev, ep.getEndpointAddress());
    }

    /**
     * Creates and returns an input stream for the given device and on a given
     * endpoint.
     *
     * @param dev the device to stream to
     * @param ep the endpoint to use is must be bulk and input
     *
     * @return the output stream
     *
     * @throws IllegalArgumentException if the endpoint is not of appropriate type
     */
    public static InputStream getInputStream(Device dev, UsbEndpointDescriptor ep) {
        if (!ep.isTypeBulk() || !ep.isInput())
	    throw new IllegalArgumentException ();
	return new BulkInputStream (dev, ep.getEndpointAddress());
    }

    // ----------------------------------------------------- Private methods
    private static void processBus(UsbBus bus) {
        doubleLinkInterfacesAndDescriptors(bus);
    }

    private static void doubleLinkInterfacesAndDescriptors(UsbBus bus) {
        while (bus != null) {
            UsbDevice device = bus.getDevices();

            while (device != null) {
                UsbConfigDescriptor[] descriptors = device.getConfig();

                if (descriptors == null) {
                    continue;
                }

                for (UsbConfigDescriptor descriptor : descriptors) {
                    UsbInterface[] interfaces = descriptor.getInterfaces();
                    if (interfaces == null) {
                        continue;
                    }
                    
                    for (UsbInterface i: interfaces) {
                        UsbInterfaceDescriptor[] intDescs = i.getAlternateSetting();

                        if (intDescs != null) {
                            continue;
                        }
                        for (UsbInterfaceDescriptor j: intDescs) {
                            j.setUsbInterface(i);
                        }
                    }
                }

                device = device.getNext();
            }

            bus = bus.getNext();
        }
    }
}
