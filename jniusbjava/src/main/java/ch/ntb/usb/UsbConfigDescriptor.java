/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

/**
 * Represents the descriptor of a USB configuration.<br>
 * A USB device can have several different configuration.<br>
 * <br>
 * The length of the configuration descriptor is
 * {@link ch.ntb.usb.UsbDescriptor#USB_DT_CONFIG_SIZE} and the type is
 * {@link ch.ntb.usb.UsbDescriptor#USB_DT_CONFIG}.
 * 
 */
public class UsbConfigDescriptor extends UsbDescriptor {

    /**
     * Maximum number of configurations per device
     */
    public static final int USB_MAXCONFIG = 8;
    private short wTotalLength;
    private byte bNumInterfaces;
    private byte bConfigurationValue;
    private byte iConfiguration;
    private byte bmAttributes;
    private byte MaxPower;
    private UsbInterface[] interfaces;
    private byte[] extra; /* Extra descriptors */

    private int extralen;

    /**
     * Returns the value to use as an argument to select this configuration ({@link LibusbJava#usb_set_configuration(long, int)}).
     *
     * @return the value to use as an argument to select this configuration
     */
    public byte getConfigurationValue() {
        return bConfigurationValue;
    }

    /**
     * Returns the power parameters for this configuration.<br>
     * <br>
     * Bit 7: Reserved, set to 1 (USB 1.0 Bus Powered)<br>
     * Bit 6: Self Powered<br>
     * Bit 5: Remote Wakeup<br>
     * Bit 4..0: Reserved, set to 0
     *
     * @return the power parameters for this configuration
     */
    public byte getAttributes() {
        return bmAttributes;
    }

    /**
     * Returns the number of interfaces.<br>
     *
     * @return the number of interfaces
     */
    public byte getNumInterfaces() {
        return bNumInterfaces;
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
     * Returns the index of the String descriptor describing this configuration.<br>
     *
     * @return the index of the String descriptor
     */
    public byte getConfiguration() {
        return iConfiguration;
    }

    /**
     * Returns the USB interface descriptors.<br>
     *
     * @return the USB interface descriptors
     */
    public UsbInterface[] getInterfaces() {
        return interfaces;
    }

    /**
     * Returns the USB interface descriptor given the index .<br>
     *
     * @param index the index
     *
     * @return the USB interface descriptors
     *
     * @throws IllegalArgumentException if index is out of range
     */
    public UsbInterface getInterface(int index) {
        if (interfaces == null) {
            throw new IllegalArgumentException("interfaces not initializated yet");
        }

        if ((index < 0) || (index >= interfaces.length)) {
            throw new IllegalArgumentException("index cannot be < 0 or >= " + interfaces.length);
        }
        
        return interfaces[index];
    }

    /**
     * Returns the USB interface descriptor given the index .<br>
     *
     * @param index the interface index
     * @param alt the alternate settings index
     *
     * @return the USB interface descriptors
     *
     * @throws IllegalArgumentException if index is out of range
     */
    public UsbInterface getInterface(int index, int alt) {
        UsbInterface i = getInterface(index);
        UsbInterfaceDescriptor[] interfaceSettings = i.getAlternateSetting();

        /*
        if (i.getAlternateSetting() == null) {
            throw new IllegalArgumentException(" not initializated yet");
        }
         */

        if ((alt < 0) || (alt >= interfaceSettings.length)) {
            throw new IllegalArgumentException("index cannot be < 0 or >= " + interfaceSettings.length);
        }

        return i;
    }

    /**
     * Returns the maximum power consumption in 2mA units.<br>
     *
     * @return the maximum power consumption in 2mA units
     */
    public byte getMaxPower() {
        return MaxPower;
    }

    /**
     * Returns the total length in bytes of all descriptors.<br>
     * When the configuration descriptor is read, it returns the entire
     * configuration hierarchy which includes all related interface and endpoint
     * descriptors. The <code>wTotalLength</code> field reflects the number of
     * bytes in the hierarchy.
     *
     * @return the total length in bytes of all descriptors
     */
    public short getTotalLength() {
        return wTotalLength;
    }

    /**
     * @param wTotalLength the wTotalLength to set
     */
    public void setTotalLength(short wTotalLength) {
        this.wTotalLength = wTotalLength;
    }

    /**
     * @param bNumInterfaces the bNumInterfaces to set
     */
    //
    // TODO: to be removed???
    //
    public void setNumInterfaces(byte bNumInterfaces) {
        this.bNumInterfaces = bNumInterfaces;
    }

    /**
     * @param bConfigurationValue the bConfigurationValue to set
     */
    public void setConfigurationValue(byte bConfigurationValue) {
        this.bConfigurationValue = bConfigurationValue;
    }

    /**
     * @param iConfiguration the iConfiguration to set
     */
    public void setConfiguration(byte iConfiguration) {
        this.iConfiguration = iConfiguration;
    }

    /**
     * @param bmAttributes the bmAttributes to set
     */
    public void setAttributes(byte bmAttributes) {
        this.bmAttributes = bmAttributes;
    }

    /**
     * @param MaxPower the MaxPower to set
     */
    public void setMaxPower(byte MaxPower) {
        this.MaxPower = MaxPower;
    }

    /**
     * @param interfaces the interfaces to set
     */
    public void setInterfaces(UsbInterface[] interfaces) {
        this.interfaces = interfaces;
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

    @Override
    public String toString() {
        return "Usb_Config_Descriptor bNumInterfaces: 0x"
                + Integer.toHexString(bNumInterfaces);
    }
}
