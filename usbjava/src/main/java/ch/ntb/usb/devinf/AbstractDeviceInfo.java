/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.devinf;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDeviceInfo {

    private short vendorId;
    private short productId;
    private String filename = null;
    private String busName = null;
    private int timeout;
    private int configuration;
    private int interface_;
    private int altinterface;
    private int outEPBulk = -1;
    private int inEPBulk = -1;
    private int outEPInt = -1;
    private int inEPInt = -1;
    private int sleepTimeout;
    private int maxDataSize;
    private TransferMode mode;
    private boolean compareData = true;
    private String manufacturer = null;
    private String product = null;
    private String serialVersion = null;
    private byte interfaceClass;
    private byte interfaceSubClass;
    private byte interfaceProtocol;


    /**
     * @return the interfaceClass
     */
    public byte getInterfaceClass() {
        return interfaceClass;
    }

    /**
     * @param interfaceClass the interfaceClass to set
     */
    public void setInterfaceClass(byte interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    /**
     * @return the interfaceSubClass
     */
    public byte getInterfaceSubClass() {
        return interfaceSubClass;
    }

    /**
     * @param interfaceSubClass the interfaceSubClass to set
     */
    public void setInterfaceSubClass(byte interfaceSubClass) {
        this.interfaceSubClass = interfaceSubClass;
    }

    /**
     * @return the interfaceProtocol
     */
    public byte getInterfaceProtocol() {
        return interfaceProtocol;
    }

    /**
     * @param interfaceProtocol the interfaceProtocol to set
     */
    public void setInterfaceProtocol(byte interfaceProtocol) {
        this.interfaceProtocol = interfaceProtocol;
    }

    public static enum TransferMode {
        Bulk, Interrupt
    }

    public AbstractDeviceInfo() {
        initValues();
    }

    abstract public void initValues();

    public int getAltinterface() {
        return altinterface;
    }

    public int getConfiguration() {
        return configuration;
    }

    public short getProductId() {
        return productId;
    }

    public short getVendorId() {
        return vendorId;
    }

    public int getInEPBulk() {
        return inEPBulk;
    }

    public int getInEPInt() {
        return inEPInt;
    }

    public int getInterface() {
        return interface_;
    }

    public int getMaxDataSize() {
        return maxDataSize;
    }

    public int getOutEPBulk() {
        return outEPBulk;
    }

    public int getOutEPInt() {
        return outEPInt;
    }

    public int getSleepTimeout() {
        return sleepTimeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setAltinterface(int altinterface) {
        this.altinterface = altinterface;
    }

    public void setConfiguration(int configuration) {
        this.configuration = configuration;
    }

    public void setIdProduct(short idProduct) {
        this.productId = idProduct;
    }

    public void setIdVendor(short idVendor) {
        this.vendorId = idVendor;
    }

    public void setInEPBulk(int in_ep_bulk) {
        this.inEPBulk = in_ep_bulk;
    }

    public void setInEPInt(int in_ep_int) {
        this.inEPInt = in_ep_int;
    }

    public void setInterface(int interface_) {
        this.interface_ = interface_;
    }

    public void setMaxDataSize(int maxDataSize) {
        this.maxDataSize = maxDataSize;
    }

    public void setOutEPBulk(int out_ep_bulk) {
        this.outEPBulk = out_ep_bulk;
    }

    public void setOutEPInt(int out_ep_int) {
        this.outEPInt = out_ep_int;
    }

    public void setSleepTimeout(int sleepTimeout) {
        this.sleepTimeout = sleepTimeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public TransferMode getMode() {
        return mode;
    }

    public void setMode(TransferMode mode) {
        this.mode = mode;
    }

    public boolean doCompareData() {
        return compareData;
    }

    public void setDoCompareData(boolean compareData) {
        this.compareData = compareData;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getSerialVersion() {
        return serialVersion;
    }

    public void setSerialVersion(String serialVersion) {
        this.serialVersion = serialVersion;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }
}
