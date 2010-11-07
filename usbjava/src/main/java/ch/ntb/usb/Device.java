/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

import java.util.logging.Level;
import java.util.logging.Logger;

import ch.ntb.usb.logger.LogUtil;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represents an USB device.<br>
 * To get an instance of an USB device use <code>USB.getUsbDevice(...)</code>.
 * 
 */
public class Device {

    public static final int DEFAULT_TIMEOUT = 2000;

    private static final Logger logger = LogUtil.getLogger("ch.ntb.usb");
    private int maxPacketSize;
    /**
     * Mandatory identification values for the device.
     */
    private int vendorId, productId;
    /**
     * Optional identification value for the device (e.g. if there are multiple
     * devices with the same vendor and product id).
     */
    private String filename;
    /**
     * Optional identification value for the device (e.g. if there are multiple
     * devices with the same vendor and product id).
     */
    private String busName;
    private int dev_configuration, dev_interface, dev_altinterface;
    private long usbDevHandle;
    private boolean resetOnFirstOpen, resetDone;
    private int resetTimeout = DEFAULT_TIMEOUT;
    private UsbDevice dev;

    protected Device(short idVendor, short idProduct) {
        resetOnFirstOpen = false;
        resetDone = false;
        maxPacketSize = -1;
        this.vendorId = idVendor;
        this.productId = idProduct;
        this.filename = null;
    }

    protected Device(short idVendor, short idProduct, String busName,
            String filename) {
        resetOnFirstOpen = false;
        resetDone = false;
        maxPacketSize = -1;
        this.vendorId = idVendor;
        this.productId = idProduct;
        this.busName = busName;
        this.filename = filename;
    }

    private void updateMaxPacketSize(UsbDevice device) throws USBException {
        setMaxPacketSize(-1);
        UsbConfigDescriptor[] confDesc = device.getConfig();
        for (int i = 0; i < confDesc.length; i++) {
            UsbInterface[] int_ = confDesc[i].getInterfaces();
            for (int j = 0; j < int_.length; j++) {
                UsbInterfaceDescriptor[] intDesc = int_[j].getAlternateSetting();
                for (int k = 0; k < intDesc.length; k++) {
                    UsbEndpointDescriptor[] epDesc = intDesc[k].getEndpoints();
                    for (int l = 0; l < epDesc.length; l++) {
                        setMaxPacketSize(Math.max(epDesc[l].getMaxPacketSize(), maxPacketSize));
                    }
                }
            }
        }
        if (maxPacketSize <= 0) {
            throw new USBException(
                    "No USB endpoints found. Check the device configuration");
        }
    }

    /**
     * Initializes the device. The parameters <code>idVendor</code> and
     * <code>idProduct</code> are mandatory. The parameter <code>filename</code>
     * is optional.
     */
    private UsbDevice initDevice(int idVendorParam, int idProductParam,
            String busName, String filename) throws USBException {
        UsbBus bus = USB.getBus();

        UsbDevice device = null;
        // search for device
        while (bus != null) {
            device = bus.getDevices();
            while (device != null) {
                UsbDeviceDescriptor devDesc = device.getDescriptor();
                if (busName != null && filename != null) {
                    if (busName.compareTo(bus.getDirname()) == 0
                            && filename.compareTo(device.getFilename()) == 0
                            && devDesc.getVendorId() == vendorId
                            && devDesc.getProductId() == productId) {
                        logger.info("Device found. bus: " + bus.getDirname()
                                + ", filename: " + device.getFilename());
                        updateMaxPacketSize(device);
                        return device;
                    }
                } else if (filename != null) {
                    if (filename.compareTo(device.getFilename()) == 0
                            && devDesc.getVendorId() == vendorId
                            && devDesc.getProductId() == productId) {
                        logger.info("Device found. bus: " + bus.getDirname()
                                + ", filename: " + device.getFilename());
                        updateMaxPacketSize(device);
                        return device;
                    }
                } else if (busName != null) {
                    if (busName.compareTo(bus.getDirname()) == 0
                            && devDesc.getVendorId() == vendorId
                            && devDesc.getProductId() == productId) {
                        logger.info("Device found. bus: " + bus.getDirname()
                                + ", filename: " + device.getFilename());
                        updateMaxPacketSize(device);
                        return device;
                    }
                } else if (devDesc.getVendorId() == vendorId
                        && devDesc.getProductId() == productId) {
                    logger.info("Device found. bus: " + bus.getDirname()
                            + ", filename: " + device.getFilename());
                    updateMaxPacketSize(device);
                    return device;
                }
                device = device.getNext();
            }
            bus = bus.getNext();
        }
        return null;
    }

    /**
     * Updates the device and descriptor information from the bus.<br>
     * The descriptors can be read with {@link #getDeviceDescriptor()} and
     * {@link #getConfigDescriptors()}.
     *
     * @throws USBException
     */
    public void updateDescriptors() throws USBException {
        setUsbDevice(initDevice(vendorId, productId, busName, filename));
    }

    /**
     * Returns the device descriptor associated with this device.<br>
     * The descriptor is updated by calling {@link #updateDescriptors()} or
     * {@link #open(int, int, int)}.
     *
     * @return the device descriptor associated with this device or
     *         <code>null</code>
     */
    public UsbDeviceDescriptor getDeviceDescriptor() {
        if (dev == null) {
            return null;
        }
        return dev.getDescriptor();
    }

    /**
     * Returns the configuration descriptors associated with this device.<br>
     * The descriptors are updated by calling {@link #updateDescriptors()} or
     * {@link #open(int, int, int)}.
     *
     * @return the configuration descriptors associated with this device or
     *         <code>null</code>
     */
    public UsbConfigDescriptor[] getConfigDescriptors() {
        if (dev == null) {
            return null;
        }
        return dev.getConfig();
    }

    /**
     * @param maxPacketSize the maxPacketSize to set
     */
    public void setMaxPacketSize(int maxPacketSize) {
        this.maxPacketSize = maxPacketSize;
    }

    /**
     * @param idVendor the idVendor to set
     */
    public void setVendorId(int idVendor) {
        this.vendorId = idVendor;
    }

    /**
     * @param idProduct the idProduct to set
     */
    public void setProductId(int idProduct) {
        this.productId = idProduct;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @param busName the busName to set
     */
    public void setBusName(String busName) {
        this.busName = busName;
    }

    /**
     * @param usbDevHandle the usbDevHandle to set
     */
    public void setUsbDevHandle(long usbDevHandle) {
        this.usbDevHandle = usbDevHandle;
    }

    /**
     * @param resetOnFirstOpen the resetOnFirstOpen to set
     */
    public void setResetOnFirstOpen(boolean resetOnFirstOpen) {
        this.resetOnFirstOpen = resetOnFirstOpen;
    }

    /**
     * @param resetDone the resetDone to set
     */
    public void setResetDone(boolean resetDone) {
        this.resetDone = resetDone;
    }

    /**
     * @param resetTimeout the resetTimeout to set
     */
    public void setResetTimeout(int resetTimeout) {
        this.resetTimeout = resetTimeout;
    }

    /**
     * @param dev the dev to set
     */
    public void setUsbDevice(UsbDevice dev) {
        this.dev = dev;
    }

    /**
     * Opens the device and claims the specified configuration, interface and
     * altinterface.<br>
     * First the bus is enumerated. If the device is found its descriptors are
     * read and the <code>maxPacketSize</code> value is updated. If no endpoints
     * are found in the descriptors an exception is thrown.
     *
     * @param configuration
     *            the configuration, see
     *            {@link UsbConfigDescriptor#getConfigurationValue()}
     * @param interface_
     *            the interface, see
     *            {@link UsbInterfaceDescriptor#getInterfaceNumber()}
     * @param altinterface
     *            the alternate interface, see
     *            {@link UsbInterfaceDescriptor#getAlternateSetting()}. If no
     *            alternate interface must be set <i>-1</i> can be used.
     * @throws USBException
     */
    public void open(int configuration, int interface_, int altinterface)
            throws USBException {
        this.setConfiguration(configuration);
        this.setInterface(interface_);
        this.setAltinterface(altinterface);

        if (usbDevHandle != 0) {
            throw new USBException("device opened, close or reset first");
        }

        setUsbDevice(initDevice(vendorId, productId, busName, filename));

        if (dev != null) {
            long res = LibusbJava.usb_open(dev);
            if (res == 0) {
                throw new USBException("LibusbJava.usb_open: "
                        + LibusbJava.usb_strerror());
            }
            setUsbDevHandle(res);
        }

        if (dev == null || usbDevHandle == 0) {
            throw new USBException("USB device with " + toString()
                    + " not found on USB");
        }
        claim_interface(usbDevHandle, configuration, interface_, altinterface);
        if (resetOnFirstOpen & !resetDone) {
            logger.info("reset on first open");
            setResetDone(true);
            reset();
            try {
                Thread.sleep(resetTimeout);
            } catch (InterruptedException e) {
                //
            }
            open(configuration, interface_, altinterface);
        }
    }

    /**
     * Release the claimed interface and close the opened device.<br>
     *
     * @throws USBException
     */
    public void close() throws USBException {
        if (usbDevHandle == 0) {
            throw new USBException("invalid device handle");
        }
        release_interface(usbDevHandle, dev_interface);
        if (LibusbJava.usb_close(usbDevHandle) < 0) {
            setUsbDevHandle(0);
            throw new USBException("LibusbJava.usb_close: "
                    + LibusbJava.usb_strerror());
        }
        setUsbDevHandle(0);
        setMaxPacketSize(-1);
        logger.info("device closed");
    }

    /**
     * Sends an USB reset to the device. The device handle will no longer be
     * valid. To use the device again, {@link #open(int, int, int)} must be
     * called.<br>
     * Note that the device is re-attached to the USB which may cause the bus
     * and filename to be changed. If the bus and filename parameters are used
     * in {@link USB#getUsbDevice(short, short, String, String)} unregister the
     * device using {@link USB#unregisterDevice(Device)}, re-enumerate the bus
     * and create a new device instance. If that is not done the device may not
     * be found.
     *
     * @throws USBException
     */
    public void reset() throws USBException {
        if (usbDevHandle == 0) {
            throw new USBException("invalid device handle");
        }
        release_interface(usbDevHandle, dev_interface);
        if (LibusbJava.usb_reset(usbDevHandle) < 0) {
            setUsbDevHandle(0);
            throw new USBException("LibusbJava.usb_reset: "
                    + LibusbJava.usb_strerror());
        }
        setUsbDevHandle(0);
        logger.info("device reset");
    }

    /**
     * Write data to the device using a bulk transfer.<br>
     *
     * @param out_ep_address
     *            endpoint address to write to
     * @param data
     *            data to write to this endpoint
     * @param size
     *            size of the data
     * @param timeout
     *            amount of time in ms the device will try to send the data
     *            until a timeout exception is thrown
     * @param reopenOnTimeout
     *            if set to true, the device will try to open the connection and
     *            send the data again before a timeout exception is thrown
     * @return the actual number of bytes written
     * @throws USBException
     */
    public int writeBulk(int out_ep_address, byte[] data, int size,
            int timeout, boolean reopenOnTimeout) throws USBException {
        if (usbDevHandle == 0) {
            throw new USBException("invalid device handle");
        }
        if (data == null) {
            throw new USBException("data must not be null");
        }
        if (size <= 0 || size > data.length) {
            throw new ArrayIndexOutOfBoundsException("invalid size: " + size);
        }
        int lenWritten = LibusbJava.usb_bulk_write(usbDevHandle,
                out_ep_address, data, size, timeout);
        if (lenWritten < 0) {
            if (lenWritten == LibusbJava.ERROR_TIMEDOUT) {
                // try to reopen the device and send the data again
                if (reopenOnTimeout) {
                    logger.info("try to reopen");
                    reset();
                    open(dev_configuration, dev_interface, dev_altinterface);
                    return writeBulk(out_ep_address, data, size, timeout, false);
                }
                throw new USBTimeoutException("LibusbJava.usb_bulk_write: "
                        + LibusbJava.usb_strerror());
            }
            throw new USBException("LibusbJava.usb_bulk_write: "
                    + LibusbJava.usb_strerror());
        }

        logger.info("length written: " + lenWritten);
        if (logger.isLoggable(Level.FINEST)) {
            StringBuffer sb = new StringBuffer("bulkwrite, ep 0x"
                    + Integer.toHexString(out_ep_address) + ": " + lenWritten
                    + " Bytes sent: ");
            for (int i = 0; i < lenWritten; i++) {
                sb.append("0x" + String.format("%1$02X", data[i]) + " ");
            }
            logger.info(sb.toString());
        }
        return lenWritten;
    }

    /**
     * Read data from the device using a bulk transfer.<br>
     *
     * @param in_ep_address
     *            endpoint address to read from
     * @param data
     *            data buffer for the data to be read
     * @param size
     *            the maximum requested data size
     * @param timeout
     *            amount of time in ms the device will try to receive data until
     *            a timeout exception is thrown
     * @param reopenOnTimeout
     *            if set to true, the device will try to open the connection and
     *            receive the data again before a timeout exception is thrown
     * @return the actual number of bytes read
     * @throws USBException
     */
    public int readBulk(int in_ep_address, byte[] data, int size, int timeout,
            boolean reopenOnTimeout) throws USBException {
        if (usbDevHandle == 0) {
            throw new USBException("invalid device handle");
        }
        if (data == null) {
            throw new USBException("data must not be null");
        }
        if (size <= 0 || size > data.length) {
            throw new ArrayIndexOutOfBoundsException("invalid size: " + size);
        }
        int lenRead = LibusbJava.usb_bulk_read(usbDevHandle, in_ep_address,
                data, size, timeout);
        if (lenRead < 0) {
            if (lenRead == LibusbJava.ERROR_TIMEDOUT) {
                // try to reopen the device and send the data again
                if (reopenOnTimeout) {
                    logger.info("try to reopen");
                    reset();
                    open(dev_configuration, dev_interface, dev_altinterface);
                    return readBulk(in_ep_address, data, size, timeout, false);
                }
                throw new USBTimeoutException("LibusbJava.usb_bulk_read: "
                        + LibusbJava.usb_strerror());
            }
            throw new USBException("LibusbJava.usb_bulk_read: "
                    + LibusbJava.usb_strerror());
        }

        logger.info("length read: " + lenRead);
        if (logger.isLoggable(Level.FINEST)) {
            StringBuffer sb = new StringBuffer("bulkread, ep 0x"
                    + Integer.toHexString(in_ep_address) + ": " + lenRead
                    + " Bytes received: ");
            for (int i = 0; i < lenRead; i++) {
                sb.append("0x" + String.format("%1$02X", data[i]) + " ");
            }
            logger.info(sb.toString());
        }
        return lenRead;
    }

    /**
     * Write data to the device using a interrupt transfer.<br>
     *
     * @param out_ep_address
     *            endpoint address to write to
     * @param data
     *            data to write to this endpoint
     * @param size
     *            size of the data
     * @param timeout
     *            amount of time in ms the device will try to send the data
     *            until a timeout exception is thrown
     * @param reopenOnTimeout
     *            if set to true, the device will try to open the connection and
     *            send the data again before a timeout exception is thrown
     * @return the actual number of bytes written
     * @throws USBException
     */
    public int writeInterrupt(int out_ep_address, byte[] data, int size,
            int timeout, boolean reopenOnTimeout) throws USBException {
        if (usbDevHandle == 0) {
            throw new USBException("invalid device handle");
        }
        if (data == null) {
            throw new USBException("data must not be null");
        }
        if (size <= 0 || size > data.length) {
            throw new ArrayIndexOutOfBoundsException("invalid size: " + size);
        }
        int lenWritten = LibusbJava.usb_interrupt_write(usbDevHandle,
                out_ep_address, data, size, timeout);
        if (lenWritten < 0) {
            if (lenWritten == LibusbJava.ERROR_TIMEDOUT) {
                // try to reopen the device and send the data again
                if (reopenOnTimeout) {
                    logger.info("try to reopen");
                    reset();
                    open(dev_configuration, dev_interface, dev_altinterface);
                    return writeInterrupt(out_ep_address, data, size, timeout,
                            false);
                }
                throw new USBTimeoutException(
                        "LibusbJava.usb_interrupt_write: "
                        + LibusbJava.usb_strerror());
            }
            throw new USBException("LibusbJava.usb_interrupt_write: "
                    + LibusbJava.usb_strerror());
        }

        logger.info("length written: " + lenWritten);
        if (logger.isLoggable(Level.FINEST)) {
            StringBuffer sb = new StringBuffer("interruptwrite, ep 0x"
                    + Integer.toHexString(out_ep_address) + ": " + lenWritten
                    + " Bytes sent: ");
            for (int i = 0; i < lenWritten; i++) {
                sb.append("0x" + String.format("%1$02X", data[i]) + " ");
            }
            logger.info(sb.toString());
        }
        return lenWritten;
    }

    /**
     * Read data from the device using a interrupt transfer.<br>
     *
     * @param in_ep_address
     *            endpoint address to read from
     * @param data
     *            data buffer for the data to be read
     * @param size
     *            the maximum requested data size
     * @param timeout
     *            amount of time in ms the device will try to receive data until
     *            a timeout exception is thrown
     * @param reopenOnTimeout
     *            if set to true, the device will try to open the connection and
     *            receive the data again before a timeout exception is thrown
     * @return the actual number of bytes read
     * @throws USBException
     */
    public int readInterrupt(int in_ep_address, byte[] data, int size,
            int timeout, boolean reopenOnTimeout) throws USBException {
        if (usbDevHandle == 0) {
            throw new USBException("invalid device handle");
        }
        if (data == null) {
            throw new USBException("data must not be null");
        }
        if (size <= 0 || size > data.length) {
            throw new ArrayIndexOutOfBoundsException("invalid size: " + size);
        }
        int lenRead = LibusbJava.usb_interrupt_read(usbDevHandle,
                in_ep_address, data, size, timeout);
        if (lenRead < 0) {
            if (lenRead == LibusbJava.ERROR_TIMEDOUT) {
                // try to reopen the device and send the data again
                if (reopenOnTimeout) {
                    logger.info("try to reopen");
                    reset();
                    open(dev_configuration, dev_interface, dev_altinterface);
                    return readInterrupt(in_ep_address, data, size, timeout,
                            false);
                }
                throw new USBTimeoutException("LibusbJava.usb_interrupt_read: "
                        + LibusbJava.usb_strerror());
            }
            throw new USBException("LibusbJava.usb_interrupt_read: "
                    + LibusbJava.usb_strerror());
        }

        logger.info("length read: " + lenRead);
        if (logger.isLoggable(Level.FINEST)) {
            StringBuffer sb = new StringBuffer("interrupt, ep 0x"
                    + Integer.toHexString(in_ep_address) + ": " + lenRead
                    + " Bytes received: ");
            for (int i = 0; i < lenRead; i++) {
                sb.append("0x" + String.format("%1$02X", data[i]) + " ");
            }
            logger.info(sb.toString());
        }
        return lenRead;
    }

    /**
     * Performs a control request to the default control pipe on a device.<br>
     * The parameters mirror the types of the same name in the USB
     * specification.
     *
     * @param requestType
     *            USB device request type (USB specification 9.3,
     *            bmRequestType). Use constants from {@link ch.ntb.usb.USB}
     *            (REQ_TYPE_xxx).
     * @param request
     *            specific request (USB specification 9.4, bRequest). Use
     *            constants from {@link ch.ntb.usb.USB} (REQ_xxx).
     * @param value
     *            field that varies according to request (USB specification 9.4,
     *            wValue)
     * @param index
     *            field that varies according to request (USB specification 9.4,
     *            wIndex)
     * @param data
     *            the send/receive buffer
     * @param size
     *            the buffer size. 0 is a valid value, but there must still be a
     *            dummy data buffer provided.
     * @param timeout
     *            amount of time in ms the device will try to send/receive data
     *            until a timeout exception is thrown
     * @param reopenOnTimeout
     *            if set to true, the device will try to open the connection and
     *            send/receive the data again before a timeout exception is
     *            thrown
     * @return the number of bytes written/read
     * @throws USBException
     */
    public int controlMsg(int requestType, int request, int value, int index,
            byte[] data, int size, int timeout, boolean reopenOnTimeout)
            throws USBException {
        if (usbDevHandle == 0) {
            throw new USBException("invalid device handle");
        }
        if (data == null) {
            throw new USBException("data must not be null");
        }
        if (size < 0 || size > data.length) {
            throw new ArrayIndexOutOfBoundsException("invalid size: " + size);
        }
        int len = LibusbJava.usb_control_msg(usbDevHandle, requestType,
                request, value, index, data, size, timeout);
        if (len < 0) {
            if (len == LibusbJava.ERROR_TIMEDOUT) {
                // try to reopen the device and send the data again
                if (reopenOnTimeout) {
                    logger.info("try to reopen");
                    reset();
                    open(dev_configuration, dev_interface, dev_altinterface);
                    return controlMsg(requestType, request, value, index, data,
                            size, timeout, false);
                }
                throw new USBTimeoutException("LibusbJava.controlMsg: "
                        + LibusbJava.usb_strerror());
            }
            throw new USBException("LibusbJava.controlMsg: "
                    + LibusbJava.usb_strerror());
        }

        logger.info("length read/written: " + len);
        if (logger.isLoggable(Level.FINEST)) {
            StringBuffer sb = new StringBuffer("controlMsg: " + len
                    + " Bytes received(written: ");
            for (int i = 0; i < len; i++) {
                sb.append("0x" + String.format("%1$02X", data[i]) + " ");
            }
            logger.info(sb.toString());
        }
        return len;
    }

    /**
     * Claim an interface to send and receive USB data.<br>
     *
     * @param usb_dev_handle
     *            the handle of the device <b>(MUST BE VALID)</b>
     * @param configuration
     *            the configuration to use
     * @param interface_
     *            the interface to claim
     * @param altinterface
     *            the alternate interface to use. If no alternate interface must
     *            be set <i>-1</i> can be used.
     * @throws USBException
     *             throws an USBException if the action fails
     */
    private void claim_interface(long usb_dev_handle, int configuration,
            int interface_, int altinterface) throws USBException {
        if (LibusbJava.usb_set_configuration(usb_dev_handle, configuration) < 0) {
            setUsbDevHandle(0);
            throw new USBException("LibusbJava.usb_set_configuration: "
                    + LibusbJava.usb_strerror());
        }
        if (LibusbJava.usb_claim_interface(usb_dev_handle, interface_) < 0) {
            setUsbDevHandle(0);
            throw new USBException("LibusbJava.usb_claim_interface: "
                    + LibusbJava.usb_strerror());
        }
        if (altinterface >= 0) {
            if (LibusbJava.usb_set_altinterface(usb_dev_handle, altinterface) < 0) {
                try {
                    release_interface(usb_dev_handle, interface_);
                } catch (USBException e) {
                    // ignore
                }
                setUsbDevHandle(0);
                throw new USBException("LibusbJava.usb_set_altinterface: "
                        + LibusbJava.usb_strerror());
            }
        }
        logger.info("interface claimed");
    }

    /**
     * Release a previously claimed interface.<br>
     *
     * @param dev_handle
     *            the handle of the device <b>(MUST BE VALID)</b>
     * @param interface_
     *            the interface to claim
     * @throws USBException
     *             throws an USBException if the action fails
     */
    private void release_interface(long dev_handle, int interface_)
            throws USBException {
        if (LibusbJava.usb_release_interface(dev_handle, interface_) < 0) {
            setUsbDevHandle(0);
            throw new USBException("LibusbJava.usb_release_interface: "
                    + LibusbJava.usb_strerror());
        }
        logger.info("interface released");
    }

    /**
     * Returns the product ID of the device.<br>
     *
     * @return the product ID of the device.
     */
    public int getProductId() {
        return productId;
    }

    /**
     * Returns the vendor ID of the device.<br>
     *
     * @return the vendor ID of the device.
     */
    public int getVendorId() {
        return vendorId;
    }

    /**
     * Returns the alternative interface.<br>
     * This value is only valid after opening the device.
     *
     * @return the alternative interface. This value is only valid after opening
     *         the device.
     */
    public int getAltinterface() {
        return dev_altinterface;
    }

    /**
     * Returns the current configuration used.<br>
     * This value is only valid after opening the device.
     *
     * @return the current configuration used. This value is only valid after
     *         opening the device.
     */
    public int getConfiguration() {
        return dev_configuration;
    }

    /**
     * Returns the current interface.<br>
     * This value is only valid after opening the device.
     *
     * @return the current interface. This value is only valid after opening the
     *         device.
     */
    public int getInterface() {
        return dev_interface;
    }

    /**
     * Returns the maximum packet size in bytes which is allowed to be
     * transmitted at once.<br>
     * The value is determined by reading the endpoint descriptor(s) when
     * opening the device. It is invalid before the device is opened! Note that
     * if some endpoints use different packet sizes the maximum packet size is
     * return. This value may be used to determine if a device is opened in
     * fullspeed or highspeed mode.
     *
     * @return the maximum packet size
     */
    public int getMaxPacketSize() {
        return maxPacketSize;
    }

    /**
     * Check if the device is open.<br>
     * This checks only for a valid device handle. It doesn't check if the
     * device is still attached or working.
     *
     * @return true if the device is open
     */
    public boolean isOpen() {
        return usbDevHandle != 0;
    }

    /**
     * If enabled, the device is reset when first opened. <br>
     * This will only happen once. When the application is started, the device
     * state is unknown. If the device is not reset, read or write may result in
     * a {@link USBTimeoutException}.<br>
     * <br>
     * This feature is disabled by default.
     *
     * @param enable
     *            true if the device should be reset when first opened
     * @param timeout
     *            the timeout between the reset and the reopening
     */
    public void setResetOnFirstOpen(boolean enable, int timeout) {
        setResetOnFirstOpen(enable);
        setResetTimeout(timeout);
    }
    
    /**
     * Returns the display name of this device, usually the combination of 
     * vendor and model. If the product id is not found "(Unknown)" will be used.
     * 
     * Note that in the vendor name Inc., Ltd, Corp. ecc are stripped out.
     * 
     * @return the display name of this device
     */
    public String getDisplayName() {

        return new DeviceDatabase().getModelDisplayName(
                   String.format("%1$04x", getVendorId()),
                   String.format("%1$04x", getProductId())
        );
    }

    /**
     * Returns the UsbDevice instance associated with this device. This value
     * is only valid after opening the device.
     *
     * @return the UsbDevice instance associated with this device.
     */
    public UsbDevice getUsbDevice() {
        return dev;
    }

    /**
     * Searches amongst the device interfaces a PTP interface. If found, the
     * interface is returned.
     *
     * @return The interface that supports PTP if found, false otherwise
     */
    public UsbInterfaceDescriptor getPTPInterface() {
        if (usbDevHandle == 0) {
            throw new IllegalStateException("The device has not been initialized");
        }

        UsbConfigDescriptor[] descriptors = dev.getConfig();

        if (descriptors == null) {
            return null;
        }

        UsbInterfaceDescriptor i = null;
        for (UsbConfigDescriptor descriptor: descriptors) {
            i = descriptor.getInterfaceByClass(UsbInterface.CLASS_IMAGE);
            if (i != null) {
                break;
            }
        }

        return i;
    }

    /**
     * Creates and returns an output stream for device and on a given endpoint.
     *
     * @param ep the endpoint to use is must be bulk and not input
     *
     * @return the output stream
     *
     * @throws IllegalArgumentException if the endpoint is not of appropriate type
     */
    public OutputStream getOutputStream(UsbEndpointDescriptor ep) {
        if (!ep.isTypeBulk() || ep.isInput())
	    throw new IllegalArgumentException ();
	return new BulkOutputStream (this, ep.getEndpointAddress());
    }

    /**
     * Creates and returns an input stream on a given endpoint.
     *
     * @param ep the endpoint to use is must be bulk and input
     *
     * @return the output stream
     *
     * @throws IllegalArgumentException if the endpoint is not of appropriate type
     */
    public InputStream getInputStream(UsbEndpointDescriptor ep) {
        if (!ep.isTypeBulk() || !ep.isInput())
	    throw new IllegalArgumentException ();
	return new BulkInputStream (this, ep.getEndpointAddress());
    }

    @Override
    public String toString() {
        return "idVendor: 0x" + Integer.toHexString(getVendorId() & 0xffff)
                + ", idProduct: 0x"
                + Integer.toHexString(getProductId() & 0xffff) + ", busName: "
                + getBusName() + ", filename: " + getFilename();
    }


    // ------------------------------------------------------- Protected methods

    /**
     * Returns the optional filename which is set when there are multiple
     * devices with the same vendor and product id. See
     * {@link USB#getUsbDevice(short, short, String, String)}. Use
     * {@link UsbDevice#getFilename()} to read the filename of a device.
     *
     * @return the filename or null
     */
    protected String getFilename() {
        return filename;
    }

    /**
     * Returns the optional bus name which is set when there are multiple
     * devices with the same vendor and product id. See
     * {@link USB#getUsbDevice(short, short, String, String)}. Use
     * {@link UsbBus#getDirname()} to read the name of a bus.
     *
     * @return the bus name or null
     */
    protected String getBusName() {
        return busName;
    }

    // --------------------------------------------------------- Private methods

    /**
     * @param dev_configuration the dev_configuration to set
     */
    private void setConfiguration(int dev_configuration) {
        this.dev_configuration = dev_configuration;
    }

    /**
     * @param dev_interface the dev_interface to set
     */
    private void setInterface(int dev_interface) {
        this.dev_interface = dev_interface;
    }

    /**
     * @param dev_altinterface the dev_altinterface to set
     */
    private void setAltinterface(int dev_altinterface) {
        this.dev_altinterface = dev_altinterface;
    }
}
