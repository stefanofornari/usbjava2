/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

/**
 * Represents the descriptor of a USB interface.<br>
 * The interface descriptor could be seen as a header or grouping of the
 * endpoints into a functional group performing a single feature of the device.<br>
 * <br>
 * The length of the interface descriptor is
 * {@link ch.ntb.usb.UsbDescriptor#USB_DT_INTERFACE_SIZE} and the type is
 * {@link ch.ntb.usb.UsbDescriptor#USB_DT_INTERFACE}.
 * 
 */
public class UsbInterfaceDescriptor extends UsbDescriptor {

    /**
     * Maximum number of interfaces
     */
    public static final int USB_MAXINTERFACES = 32;
    private byte bInterfaceNumber;
    private byte bAlternateSetting;
    private byte bNumEndpoints;
    private byte bInterfaceClass;
    private byte bInterfaceSubClass;
    private byte bInterfaceProtocol;
    private byte iInterface;
    private UsbEndpointDescriptor[] endpoint;
    private byte[] extra; /* Extra descriptors */

    private int extralen;

    @Override
    public String toString() {
        return "Usb_Interface_Descriptor bNumEndpoints: 0x"
                + Integer.toHexString(bNumEndpoints);
    }

    /**
     * Returns the value used to select the alternate setting ({@link LibusbJava#usb_set_altinterface(long, int)}).<br>
     *
     * @return the alternate setting
     */
    public byte getAlternateSetting() {
        return bAlternateSetting;
    }

    /**
     * Returns the class code (Assigned by <a
     * href="http://www.usb.org">www.usb.org</a>).<br>
     *
     * @return the class code
     */
    public byte getInterfaceClass() {
        return bInterfaceClass;
    }

    /**
     * Returns the number (identifier) of this interface.<br>
     *
     * @return the number (identifier) of this interface
     */
    public byte getInterfaceNumber() {
        return bInterfaceNumber;
    }

    /**
     * Returns the protocol code (Assigned by <a
     * href="http://www.usb.org">www.usb.org</a>).<br>
     *
     * @return the protocol code
     */
    public byte getInterfaceProtocol() {
        return bInterfaceProtocol;
    }

    /**
     * Returns the subclass code (Assigned by <a
     * href="http://www.usb.org">www.usb.org</a>).<br>
     *
     * @return the subclass code
     */
    public byte getInterfaceSubClass() {
        return bInterfaceSubClass;
    }

    /**
     * Returns the number of endpoints used for this interface.<br>
     *
     * @return the number of endpoints used for this interface
     */
    public byte getNumEndpoints() {
        return (byte)((endpoint == null) ? 0 : endpoint.length);
    }

    /**
     * Returns an array of endpoint descriptors.<br>
     *
     * @return an array of endpoint descriptors
     */
    public UsbEndpointDescriptor[] getEndpoints() {
        return endpoint;
    }

    /**
     * Returns the data of extra descriptor(s) if available.<br>
     *
     * @return null or a byte array with the extra descriptor data
     */
    public byte[] getExtra() {
        return extra;
    }

    /**
     * Returns the number of bytes of the extra descriptor.<br>
     *
     * @return the number of bytes of the extra descriptor
     */
    public int getExtralen() {
        return extralen;
    }

    /**
     * Returns the index of the String descriptor describing this interface.<br>
     *
     * @return the index of the String descriptor
     */
    public byte getInterface() {
        return iInterface;
    }

    /**
     * @param bInterfaceNumber the bInterfaceNumber to set
     */
    public void setInterfaceNumber(byte bInterfaceNumber) {
        this.bInterfaceNumber = bInterfaceNumber;
    }

    /**
     * @param bAlternateSetting the bAlternateSetting to set
     */
    public void setAlternateSetting(byte bAlternateSetting) {
        this.bAlternateSetting = bAlternateSetting;
    }

    /**
     * @param bInterfaceClass the bInterfaceClass to set
     */
    public void setInterfaceClass(byte bInterfaceClass) {
        this.bInterfaceClass = bInterfaceClass;
    }

    /**
     * @param bInterfaceSubClass the bInterfaceSubClass to set
     */
    public void setInterfaceSubClass(byte bInterfaceSubClass) {
        this.bInterfaceSubClass = bInterfaceSubClass;
    }

    /**
     * @param bInterfaceProtocol the bInterfaceProtocol to set
     */
    public void setInterfaceProtocol(byte bInterfaceProtocol) {
        this.bInterfaceProtocol = bInterfaceProtocol;
    }

    /**
     * @param iInterface the iInterface to set
     */
    public void setInterface(byte iInterface) {
        this.iInterface = iInterface;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoints(UsbEndpointDescriptor[] endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * @param extra the extra to set
     */
    public void setExtra(byte[] extra) {
        this.extra = extra;
    }

    /**
     * @param extralen the extralen to set
     */
    public void setExtralen(int extralen) {
        this.extralen = extralen;
    }
}
