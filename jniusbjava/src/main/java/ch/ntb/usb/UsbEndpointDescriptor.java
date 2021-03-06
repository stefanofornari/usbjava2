/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

/**
 * Represents the descriptor of an USB endpoint.<br>
 * Endpoint descriptors are used to describe endpoints other than endpoint zero.
 * Endpoint zero is always assumed to be a control endpoint and is configured
 * before any descriptors are even requested. The host will use the information
 * returned from these descriptors to determine the bandwidth requirements of
 * the bus.<br>
 * <br>
 * The length of the configuration descriptor is
 * {@link ch.ntb.usb.UsbDescriptor#USB_DT_ENDPOINT_SIZE} and the type is
 * {@link ch.ntb.usb.UsbDescriptor#USB_DT_ENDPOINT}.
 * 
 */
public class UsbEndpointDescriptor extends UsbDescriptor {

    /**
     * Maximum number of endpoints
     */
    public static final int USB_MAXENDPOINTS = 32;
    
    /**
     * Endpoint address mask (in address).
     */
    public static final int USB_ENDPOINT_ADDRESS_MASK = 0x0f,
                            USB_ENDPOINT_DIR_MASK     = 0x80;
    /**
     * Endpoint type mask (in attributes).
     */
    public static final int USB_ENDPOINT_TYPE_MASK = 0x03;

    /**
     * Endpoint directions
     */
    public static final int USB_ENDPOINT_INPUT  = 0x80;
    public static final int USB_ENDPOINT_OUTPUT = 0x00;

    /**
     * Possible endpoint types (in attributes).
     */
    public static final int 
        USB_ENDPOINT_TYPE_CONTROL     = 0,
        USB_ENDPOINT_TYPE_ISOCHRONOUS = 1,
        USB_ENDPOINT_TYPE_BULK        = 2,
        USB_ENDPOINT_TYPE_INTERRUPT   = 3;
        
    private byte   address      ;
    private byte   attributes   ;
    private short  maxPacketSize;
    private byte   interval     ;
    private byte   refresh      ;
    private byte   synchAddress ;
    private byte[] extra        ; /* Extra descriptors */

    private int extralen;

    /**
     * Returns the endpoint address.<br>
     * <br>
     * Bits 3..0: Endpoint number <br>
     * Bits 6..4: Reserved. Set to zero <br>
     * Bit 7: Direction (host to device). 0 = OUT (send data from host to
     * device), 1 = IN (host receives data from device). Note: these values are
     * ignored for control endpoints.<br>
     *
     * @return the endpoint address
     */
    public byte getEndpointAddress() {
        return address;
    }

    /**
     * Returns the intervall for polling endpoint data transfers.<br>
     * Value in frame counts. Ignored for Bulk & Control eEndpoints. Isochronous
     * endpoints must equal 1 and field may range from 1 to 255 for interrupt
     * endpoints.
     *
     * @return the intervall for polling endpoint data transfers
     */
    public byte getInterval() {
        return interval;
    }

    /**
     * Returns the attributes of this endpoint.<br>
     *
     * Bits 1..0: Transfer Type (see <i>USB_ENDPOINT_TYPE_XXX</i>).<br>
     * Bits 7..2: Reserved.<br>
     *
     * <pre>
     * 	If isochronous endpoint:
     * 		Bits 3..2: Synchronisation type
     *  		00 = No synchronisation
     * 			01 = Asynchronous
     *                  10 = Adaptive
     *                  11 = Synchronous
     *     	Bits 5..4: Usage Type
     *      	        00 = Data endpoint
     *      	        01 = Feedback endpoint
     *      	        10 = Explicit feedback data endpoint
     *      	        11 = Reserved
     * </pre>
     *
     * @return the attributes of this endpoint
     */
    public byte getAttributes() {
        return attributes;
    }

    public byte getRefresh() {
        return refresh;
    }

    public byte getSynchAddress() {
        return synchAddress;
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
     * Returns the maximum packet size of this endpoint is capable of sending or
     * receiving.<br>
     *
     * @return the maximum packet size
     */
    public short getMaxPacketSize() {
        return maxPacketSize;
    }

    /**
     * This is used by gphoto2... but I do not know what it is used for yet...
     *
     * @return and empty byte[] for now.
     */
    public byte[] recvInterrupt() {
        return new byte[0];
    }

    @Override
    public String toString() {
        return "Usb_Endpoint_Descriptor bEndpointAddress: 0x"
                + Integer.toHexString(address & 0xFF);
    }

    /**
     * @param bEndpointAddress the bEndpointAddress to set
     */
    public void setEndpointAddress(byte bEndpointAddress) {
        this.address = bEndpointAddress;
    }

    /**
     * @param bmAttributes the bmAttributes to set
     */
    public void setAttributes(byte bmAttributes) {
        this.attributes = bmAttributes;
    }

    /**
     * @param wMaxPacketSize the wMaxPacketSize to set
     */
    public void setMaxPacketSize(short wMaxPacketSize) {
        this.maxPacketSize = wMaxPacketSize;
    }

    /**
     * @param bInterval the bInterval to set
     */
    public void setInterval(byte bInterval) {
        this.interval = bInterval;
    }

    /**
     * @param bRefresh the bRefresh to set
     */
    public void setRefresh(byte bRefresh) {
        this.refresh = bRefresh;
    }

    /**
     * @param bSynchAddress the bSynchAddress to set
     */
    public void setSynchAddress(byte bSynchAddress) {
        this.synchAddress = bSynchAddress;
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

    /**
     * Is the endpoint a control endpoint?
     *
     * @return true if the endpoint is a control endpoint, false otherwise
     */
    public boolean isTypeControl() {
        return (attributes & USB_ENDPOINT_TYPE_MASK) == USB_ENDPOINT_TYPE_CONTROL;
    }

    /**
     * Is the endpoint a isochronous endpoint?
     *
     * @return true if the endpoint is a isochronous endpoint, false otherwise
     */
    public boolean isTypeIsochronous() {
        return (attributes & USB_ENDPOINT_TYPE_MASK) == USB_ENDPOINT_TYPE_ISOCHRONOUS;
    }

    /**
     * Is the endpoint a bulk endpoint?
     *
     * @return true if the endpoint is a bulk endpoint, false otherwise
     */
    public boolean isTypeBulk() {
        return (attributes & USB_ENDPOINT_TYPE_MASK) == USB_ENDPOINT_TYPE_BULK;
    }

    /**
     * Is the endpoint an interrupt endpoint?
     *
     * @return true if the endpoint is an interrupt endpoint, false otherwise
     */
    public boolean isTypeInterrupt() {
        return (attributes & USB_ENDPOINT_TYPE_MASK) == USB_ENDPOINT_TYPE_INTERRUPT;
    }

    /**
     * Returns true if this is an input endpoint (data flows to host),
     * false if it is instead an output endpoint (data flows to device).
     *
     * @return true if this is an input endpoint, false otherwise
     */
    public boolean isInput() {
        return (address & USB_ENDPOINT_DIR_MASK) != 0;
    }
    
     /**
     * Returns true if this is an output endpoint (data flows to host),
     * false if it is instead an input endpoint (data flows to device).
     *
     * @return true if this is an input endpoint, false otherwise
     */
    public boolean isOutput() {
        return (address & USB_ENDPOINT_DIR_MASK) == 0;
    }


}
