/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

/**
 * Represents the descriptor of a USB device.<br>
 * A USB device can only have one device descriptor. It specifies some basic,
 * yet important information about the device.<br>
 * <br>
 * The length of the device descriptor is
 * {@link ch.ntb.usb.UsbDescriptor#USB_DT_DEVICE_SIZE} and the type is
 * {@link ch.ntb.usb.UsbDescriptor#USB_DT_DEVICE}.
 * 
 */
public class UsbDeviceDescriptor extends UsbDescriptor {

    /**
     * Device and/or interface class codes.
     */
    public static final int USB_CLASS_PER_INTERFACE = 0, USB_CLASS_AUDIO = 1,
            USB_CLASS_COMM = 2, USB_CLASS_HID = 3, USB_CLASS_PRINTER = 7,
            USB_CLASS_MASS_STORAGE = 8, USB_CLASS_HUB = 9, USB_CLASS_DATA = 10,
            USB_CLASS_VENDOR_SPEC = 0xff;
    private short bcdUSB;
    private byte bDeviceClass;
    private byte bDeviceSubClass;
    private byte bDeviceProtocol;
    //
    // TODO: is this an int???
    //
    private byte bMaxPacketSize0;
    private short idVendor;
    private short idProduct;
    private short bcdDevice;
    private byte iManufacturer;
    private byte iProduct;
    private byte iSerialNumber;
    private byte bNumConfigurations;

    /**
     * Returns the device release number.<br>
     * Assigned by the manufacturer of the device.
     *
     * @return the device release number
     */
    public short getBcdDevice() {
        return bcdDevice;
    }

    /**
     * Returns the USB specification number to which the device complies to.<br>
     * This field reports the highest version of USB the device supports. The
     * value is in binary coded decimal with a format of 0xJJMN where JJ is the
     * major version number, M is the minor version number and N is the sub
     * minor version number.<br>
     * Examples: USB 2.0 is reported as 0x0200, USB 1.1 as 0x0110 and USB 1.0 as
     * 0x100
     *
     * @return the USB specification number to which the device complies to
     */
    public short getBcdUSB() {
        return bcdUSB;
    }

    /**
     * Returns the class code (Assigned by <a
     * href="http://www.usb.org">www.usb.org</a>)<br>
     * If equal to zero, each interface specifies it's own class code. If equal
     * to 0xFF, the class code is vendor specified. Otherwise the field is a
     * valid class code.
     *
     * @return the class code
     */
    public byte getDeviceClass() {
        return bDeviceClass;
    }

    /**
     * Returns the protocol code (Assigned by <a
     * href="http://www.usb.org">www.usb.org</a>)<br>
     *
     * @return the protocol code
     */
    public byte getDeviceProtocol() {
        return bDeviceProtocol;
    }

    /**
     * Returns the subclass code (Assigned by <a
     * href="http://www.usb.org">www.usb.org</a>)<br>
     *
     * @return the subclass code
     */
    public byte getDeviceSubClass() {
        return bDeviceSubClass;
    }

    /**
     * Returns the maximum packet size for endpoint zero.<br>
     * Valid sizes are 8, 16, 32, 64.
     *
     * @return the maximum packet size for endpoint zero
     */
    public byte getMaxPacketSize0() {
        return bMaxPacketSize0;
    }

    /**
     * Returns the number of possible configurations supported at its current
     * speed.<br>
     *
     * @return the number of possible configurations supported at its current
     *         speed
     */
    public byte getNumConfigurations() {
        return bNumConfigurations;
    }

    /**
     * Returns the product ID (Assigned by <a
     * href="http://www.usb.org">www.usb.org</a>)<br>
     *
     * @return the product ID
     */
    public short getProductId() {
        return idProduct;
    }

    /**
     * Returns the Vendor ID (Assigned by <a
     * href="http://www.usb.org">www.usb.org</a>)<br>
     *
     * @return the Vendor ID
     */
    public short getVendorId() {
        return idVendor;
    }

    /**
     * Returns the index of the manufacturer string descriptor.<br>
     * If this value is 0, no string descriptor is used.
     *
     * @return the index of the manufacturer string descriptor
     */
    public byte getManufacturer() {
        return iManufacturer;
    }

    /**
     * Returns the index of the product string descriptor.<br>
     * If this value is 0, no string descriptor is used.
     *
     * @return the index of the product string descriptor
     */
    public byte getProduct() {
        return iProduct;
    }

    /**
     * Returns the index of serial number string descriptor.<br>
     * If this value is 0, no string descriptor is used.
     *
     * @return the index of serial number string descriptor
     */
    public byte getSerialNumber() {
        return iSerialNumber;
    }

    /**
     * @param bcdUSB the bcdUSB to set
     */
    public void setCdUSB(short bcdUSB) {
        this.bcdUSB = bcdUSB;
    }

    /**
     * @param bDeviceClass the bDeviceClass to set
     */
    public void setDeviceClass(byte bDeviceClass) {
        this.bDeviceClass = bDeviceClass;
    }

    /**
     * @param bDeviceSubClass the bDeviceSubClass to set
     */
    public void setDeviceSubClass(byte bDeviceSubClass) {
        this.bDeviceSubClass = bDeviceSubClass;
    }

    /**
     * @param bDeviceProtocol the bDeviceProtocol to set
     */
    public void setDeviceProtocol(byte bDeviceProtocol) {
        this.bDeviceProtocol = bDeviceProtocol;
    }

    /**
     * @param bMaxPacketSize0 the bMaxPacketSize0 to set
     */
    public void setMaxPacketSize0(byte bMaxPacketSize0) {
        this.bMaxPacketSize0 = bMaxPacketSize0;
    }

    /**
     * @param idVendor the idVendor to set
     */
    public void setVendorId(short idVendor) {
        this.idVendor = idVendor;
    }

    /**
     * @param idProduct the idProduct to set
     */
    public void setProductId(short idProduct) {
        this.idProduct = idProduct;
    }

    /**
     * @param bcdDevice the bcdDevice to set
     */
    public void setCdDevice(short bcdDevice) {
        this.bcdDevice = bcdDevice;
    }

    /**
     * @param iManufacturer the iManufacturer to set
     */
    public void setManufacturer(byte iManufacturer) {
        this.iManufacturer = iManufacturer;
    }

    /**
     * @param iProduct the iProduct to set
     */
    public void setProduct(byte iProduct) {
        this.iProduct = iProduct;
    }

    /**
     * @param iSerialNumber the iSerialNumber to set
     */
    public void setSerialNumber(byte iSerialNumber) {
        this.iSerialNumber = iSerialNumber;
    }

    /**
     * @param bNumConfigurations the bNumConfigurations to set
     */
    public void setNumConfigurations(byte bNumConfigurations) {
        this.bNumConfigurations = bNumConfigurations;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usb_Device_Descriptor idVendor: 0x")
          .append(Integer.toHexString(idVendor & 0xFFFF))
          .append(", idProduct: 0x")
          .append(Integer.toHexString(idProduct & 0xFFFF));
        return sb.toString();
    }
}
