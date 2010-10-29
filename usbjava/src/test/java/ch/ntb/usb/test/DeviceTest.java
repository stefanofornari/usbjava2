/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import junit.framework.TestCase;

import ch.ntb.usb.Device;
import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;
import ch.ntb.usb.UsbBus;
import ch.ntb.usb.UsbConfigDescriptor;
import ch.ntb.usb.UsbDeviceDescriptor;
import ch.ntb.usb.Utils;
import ch.ntb.usb.AbstractDeviceInfo;
import ch.ntb.usb.AbstractDeviceInfo.TransferMode;

public class DeviceTest extends TestCase {

    private static final String testdevicePropertiesFile = "/testdevice.properties";
    private static final String deviceInfoKey = "testdeviceInfo";
    private static AbstractDeviceInfo devinfo;
    private static byte[] testData;
    private static byte[] readData;
    private static Device dev;
    private static Logger log = Logger.getLogger(DeviceTest.class.getName());

    public void setUp() throws Exception {
        // load the device info class with the key
        // from 'testdevice.properties'
        InputStream propInputStream = getClass().getResourceAsStream(testdevicePropertiesFile);
        Properties devInfoProp = new Properties();
        devInfoProp.load(propInputStream);
        String devInfoClazzName = devInfoProp.getProperty(deviceInfoKey);
        if (devInfoClazzName == null) {
            throw new Exception("property " + deviceInfoKey
                    + " not found in file " + testdevicePropertiesFile);
        }
        Class<?> devInfoClazz = Class.forName(devInfoClazzName);
        devinfo = (AbstractDeviceInfo) devInfoClazz.newInstance();
        LibusbJava.init(devinfo);
        // setup test data
        testData = new byte[devinfo.getMaxDataSize()];
        readData = new byte[testData.length];
        // initialise the device
        LibusbJava.usb_set_debug(255);
        dev = USB.getDevice(devinfo.getIdVendor(), devinfo.getIdProduct(),
                devinfo.getBusName(), devinfo.getFilename());
        assertNotNull(dev);

        // print the devices
        LibusbJava.usb_init();
        LibusbJava.usb_find_busses();
        LibusbJava.usb_find_devices();
        UsbBus bus = LibusbJava.usb_get_busses();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Utils.logBus(bus, ps);
        log.info(baos.toString());
    }

    public void testGetDescriptors() throws Exception {
        dev.updateDescriptors();
        UsbDeviceDescriptor devDescriptor = dev.getDeviceDescriptor();
        assertNotNull(devDescriptor);
        assertEquals(devinfo.getIdProduct(), devDescriptor.getProductId());
        assertEquals(devinfo.getIdVendor(), devDescriptor.getVendorId());
        UsbConfigDescriptor confDescriptors[] = dev.getConfigDescriptors();
        assertNotNull(confDescriptors);
        assertTrue(confDescriptors[0].getInterfaces().length > 0);
    }

    public void testInitalReset() throws Exception {
        doOpen();
        // this may change the bus and file name of the device
        dev.reset();
        timeout();
    }

    public void testClose() throws Exception {
        // this calls must not throw an exception
        doOpen();
        doClose();
        try {
            // this call must throw an exception, because the device is closed
            dev.writeBulk(devinfo.getOutEPBulk(), testData, testData.length,
                    devinfo.getTimeout(), false);
            fail("Write into a closed device should not be possible");
        } catch (USBException e) {
            //
            // This is OK
            //
        }
    }

    public void testReset1() throws Exception {
        doOpen();
        dev.reset();
        timeout();

        try {
            // this call must throw an exception, because the device is closed
            dev.writeBulk(devinfo.getOutEPBulk(), testData, testData.length,
                    devinfo.getTimeout(), false);
            fail("Write into a closed device should not be possible");
        } catch (USBException e) {
            //
            // This is OK
            //
        }
    }

    public void testReset2() throws Exception {
        doOpen();
        dev.reset();
        timeout();

        try {
            // this call must throw an exception, because the device can't be close
            doClose();
            fail("teh device can't be close!");
       } catch (USBException e) {
           //
           // OK
           //
       }
    }

    public void testOpen() throws Exception {
        // get device by busname
        doOpen();
        assertNotNull(dev.getDevice());
        String oldFilename = dev.getDevice().getFilename();
        String oldBusName = dev.getDevice().getBus().getDirname();
        assertNotNull(oldFilename);
        assertNotNull(oldBusName);
        log.info("filename: " + oldFilename + ", busName: " + oldBusName);
        Device dev2 = USB.getDevice(devinfo.getIdVendor(), devinfo.getIdProduct());
        doClose();
        assertEquals(dev, dev2);
    }

    public void testOpenWithBusName() throws Exception {
        // get device by busname
        doOpen();
        assertNotNull(dev.getDevice());
        String oldFilename = dev.getDevice().getFilename();
        String oldBusName = dev.getDevice().getBus().getDirname();
        assertNotNull(oldFilename);
        assertNotNull(oldBusName);
        log.info("filename: " + oldFilename + ", busName: " + oldBusName);
        Device dev2 = USB.getDevice(devinfo.getIdVendor(), devinfo.getIdProduct(), oldBusName, null);
        doClose();
        assertEquals(dev, dev2);
    }

    public void testOpenWithFilename() throws Exception {
        // get device by busname
        doOpen();
        assertNotNull(dev.getDevice());
        String oldFilename = dev.getDevice().getFilename();
        String oldBusName = dev.getDevice().getBus().getDirname();
        assertNotNull(oldFilename);
        assertNotNull(oldBusName);
        log.info("filename: " + oldFilename + ", busName: " + oldBusName);
        Device dev2 = USB.getDevice(devinfo.getIdVendor(), devinfo.getIdProduct(), null, oldFilename);
        doClose();
        assertEquals(dev, dev2);
    }

    public void testOpenWithBusAndFilename() throws Exception {
        // get device by busname and filename
        doOpen();
        assertNotNull(dev.getDevice());
        String oldFilename = dev.getDevice().getFilename();
        String oldBusName = dev.getDevice().getBus().getDirname();
        assertNotNull(oldFilename);
        assertNotNull(oldBusName);
        log.info("filename: " + oldFilename + ", busName: " + oldBusName);
        Device dev2 = USB.getDevice(devinfo.getIdVendor(), devinfo.getIdProduct(), oldBusName, oldFilename);
        doClose();
        assertEquals(dev, dev2);
    }

    public void testBulkWriteRead() throws Exception {
        checkBulkEndpoints();
        devinfo.setMode(TransferMode.Bulk);
        doOpenWriteReadClose();
    }

    private void checkBulkEndpoints() {
        if (devinfo.getInEPBulk() == -1 && devinfo.getOutEPBulk() == -1) {
            throw new UnsupportedOperationException(
                    "no bulk endpoints defined in test device definition");
        }
    }

    public void testInterruptWriteRead() throws Exception {
        checkInterruptEndpoints();
        devinfo.setMode(TransferMode.Interrupt);
        doOpenWriteReadClose();
    }

    private void checkInterruptEndpoints() {
        if (devinfo.getInEPInt() == -1 && devinfo.getOutEPInt() == -1) {
            throw new UnsupportedOperationException(
                    "no interrupt endpoints defined in test device definition");
        }
    }

    public void testBulkWriteReadMultiple() throws Exception {
        final int NumberOfIterations = 100;

        devinfo.setMode(TransferMode.Bulk);
        doOpen();
        for (int i = 0; i < NumberOfIterations; i++) {
            doWriteRead();
        }
        doClose();
    }

    public void testMultipleOpenCloseWithBulkWrite() throws Exception {
        devinfo.setMode(TransferMode.Bulk);
        for (int i = 0; i < 5; i++) {
            doOpen();
            doClose();
        }
        doOpenWriteReadClose();
        for (int i = 0; i < 10; i++) {
            doOpen();
            doWriteRead();
            doClose();
        }
        doOpenWriteReadClose();
        for (int i = 0; i < 5; i++) {
            doOpen();
            doClose();
        }
    }

    public void testBulkAndInterrupt() throws Exception {
        doOpen();
        // BULK
        devinfo.setMode(TransferMode.Bulk);
        doWriteRead();
        // INTERRUPT
        devinfo.setMode(TransferMode.Interrupt);
        doWriteRead();
        doClose();
    }

    public void testBulkAndInterruptMultiple() throws Exception {
        for (int i = 0; i < 20; i++) {
            devinfo.setMode(TransferMode.Bulk);
            doOpenWriteReadClose();
            devinfo.setMode(TransferMode.Interrupt);
            doOpenWriteReadClose();
        }
    }

    public void testControlMsg() throws Exception {
        try {
            dev.open(devinfo.getConfiguration(), devinfo.getInterface(),
                    devinfo.getAltinterface());
            // GET STATUS (device)
            byte[] data = getTestData(2);
            int length = dev.controlMsg(USB.REQ_TYPE_DIR_DEVICE_TO_HOST
                    | USB.REQ_TYPE_TYPE_STANDARD | USB.REQ_TYPE_RECIP_DEVICE,
                    USB.REQ_GET_STATUS, 0, 0, data, data.length, devinfo.getTimeout(), false);
            assertTrue(length > 0);
            assertEquals((byte) 0x01, data[0]);
            assertEquals((byte) 0x00, data[1]);
            // GET STATUS (interface)
            data = getTestData(2);
            length = dev.controlMsg(
                    USB.REQ_TYPE_DIR_DEVICE_TO_HOST
                    | USB.REQ_TYPE_TYPE_STANDARD
                    | USB.REQ_TYPE_RECIP_INTERFACE, USB.REQ_GET_STATUS,
                    0, 0, data, data.length, devinfo.getTimeout(), false);
            assertTrue(length > 0);
            assertEquals((byte) 0x00, data[0]);
            assertEquals((byte) 0x00, data[1]);
            // GET STATUS (endpoint)
            data = getTestData(2);
            length = dev.controlMsg(USB.REQ_TYPE_DIR_DEVICE_TO_HOST
                    | USB.REQ_TYPE_TYPE_STANDARD | USB.REQ_TYPE_RECIP_ENDPOINT,
                    USB.REQ_GET_STATUS, 0, 0, data, data.length, devinfo.getTimeout(), false);
            assertTrue(length > 0);
            assertEquals((byte) 0x00, data[0]);
            assertEquals((byte) 0x00, data[1]);
            // GET CONFIGURATION
            data = getTestData(1);
            length = dev.controlMsg(USB.REQ_TYPE_DIR_DEVICE_TO_HOST
                    | USB.REQ_TYPE_TYPE_STANDARD | USB.REQ_TYPE_RECIP_DEVICE,
                    USB.REQ_GET_CONFIGURATION, 0, 0, data, data.length, devinfo.getTimeout(), false);
            assertTrue(length > 0);
            assertEquals((byte) devinfo.getConfiguration(), data[0]);
            // // GET INTERFACE
            // data = byte[1];
            // length = dev.controlMsg(USB.REQ_TYPE_DIR_DEVICE_TO_HOST
            // | USB.REQ_TYPE_TYPE_STANDARD | USB.REQ_TYPE_RECIP_INTERFACE,
            // USB.REQ_GET_INTERFACE, 0, devinfo.getInterfaces(), data,
            // data.length,
            // devinfo
            // .getTimeout(), false);
            // logData(data, length);
            // GET DESCRIPTOR (device descriptor)
            data = getTestData(128);
            length = dev.controlMsg(USB.REQ_TYPE_DIR_DEVICE_TO_HOST
                    | USB.REQ_TYPE_TYPE_STANDARD | USB.REQ_TYPE_RECIP_DEVICE,
                    USB.REQ_GET_DESCRIPTOR, 1 << 8, 0, data, data.length,
                    devinfo.getTimeout(), false);
            validateDeviceDescriptor(data, length);
            // GET DESCRIPTOR (string descriptor (1))
            data = getTestData(128);
            length = dev.controlMsg(USB.REQ_TYPE_DIR_DEVICE_TO_HOST
                    | USB.REQ_TYPE_TYPE_STANDARD | USB.REQ_TYPE_RECIP_DEVICE,
                    USB.REQ_GET_DESCRIPTOR, (3 << 8) + 1, 0, data, data.length,
                    devinfo.getTimeout(), false);
            String s = getString(data, length);
            assertEquals(devinfo.getManufacturer(), s);
            // GET DESCRIPTOR (string descriptor (2))
            data = getTestData(128);
            length = dev.controlMsg(USB.REQ_TYPE_DIR_DEVICE_TO_HOST
                    | USB.REQ_TYPE_TYPE_STANDARD | USB.REQ_TYPE_RECIP_DEVICE,
                    USB.REQ_GET_DESCRIPTOR, (3 << 8) + 2, 0, data, data.length,
                    devinfo.getTimeout(), false);
            s = getString(data, length);
            assertEquals(devinfo.getProduct(), s);
            // GET DESCRIPTOR (string descriptor (3))
            data = getTestData(128);
            length = dev.controlMsg(USB.REQ_TYPE_DIR_DEVICE_TO_HOST
                    | USB.REQ_TYPE_TYPE_STANDARD | USB.REQ_TYPE_RECIP_DEVICE,
                    USB.REQ_GET_DESCRIPTOR, (3 << 8) + 3, 0, data, data.length,
                    devinfo.getTimeout(), false);
            s = getString(data, length);
            assertEquals(devinfo.getSerialVersion(), s);
            // close the device
            dev.close();
        } catch (Exception e) {
            closeOnException();
            throw e;
        } catch (AssertionError e) {
            closeOnException();
            throw e;
        }
    }

    private void closeOnException() {
        try {
            dev.close();
        } catch (USBException e1) {
            // ignore exceptions
        }
    }

    private void validateDeviceDescriptor(byte[] data, int length) {
        // length read
        assertEquals(18, length);
        // descriptor length
        assertEquals((byte) 18, data[0]);
        // descriptor type
        assertEquals((byte) 1, data[1]);
        // USB specification number LSB
        assertEquals((byte) 0, data[2]);
        // USB specification number MSB
        assertEquals((byte) 0x02, data[3]);
        // device class (vendor specific)
        assertEquals((byte) 0xff, data[4]);
        // device subclass (vendor specific)
        assertEquals((byte) 0xff, data[5]);
        // device protocol (vendor specific)
        assertEquals((byte) 0xff, data[6]);
        // maximum packet size for endpoint zero
        assertEquals((byte) 64, data[7]);
        // Vendor ID (NTB) LSB
        assertEquals((byte) 0x35, data[8]);
        // Vendor ID (NTB) MSB
        assertEquals((byte) 0x82, data[9]);
        // Product ID (JUnit test board) LSB
        assertEquals((byte) 0x22, data[10]);
        // Product ID (JUnit test board) MSB
        assertEquals((byte) 0x02, data[11]);

        // Device release number LSB
        assertEquals((byte) 0x00, data[12]);
        // Device release number MSB
        assertEquals((byte) 0x10, data[13]);
        // Index of manufacturer string descriptor
        assertEquals((byte) 0x01, data[14]);
        // Index of product string descriptor
        assertEquals((byte) 0x02, data[15]);
        // Index of serial number string descriptor
        assertEquals((byte) 0x03, data[16]);
        // Number of possible configurations
        assertEquals((byte) 0x01, data[17]);
    }

    private byte[] getTestData(int length) {
        byte[] b = new byte[length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) (Math.random() * 256);
        }
        return b;
    }

    @SuppressWarnings("unused")
    private void logData(byte[] data, int length) {
        if (length > 0) {
            log.info("length: " + length);
            String logData = "";
            for (int i = 0; i < length; i++) {
                logData += "0x" + Integer.toHexString(data[i] & 0xff) + "\t";
            }
            log.info(logData);
        }
    }

    private String getString(byte[] data, int length)
            throws UnsupportedEncodingException {
        // data length
        assertTrue(length > 2);
        // string length
        assertTrue(data[0] > 2);
        // string descriptor ident
        assertEquals((byte) 3, data[1]);
        // create string from data
        return new String(data, 2, length - 2, "UTF-16LE");
    }

    /**
     * Currently I do not know how this should fail :(
     * @throws Exception
     */
    public void _testInvalidConfig() throws Exception {
        try {
            dev.open(devinfo.getConfiguration() + 5, devinfo.getInterface(),
                    devinfo.getAltinterface());
            fail("USBException expected");
        } catch (USBException e) {
            log.severe("could not set config "
                    + (devinfo.getConfiguration() + 5));
        }
        doOpenWriteReadClose();
    }

    /**
     * Currently I do not know how this should fail :(
     * @throws Exception
     */
    public void _testInvalidInterface() throws Exception {
        try {
            dev.open(devinfo.getConfiguration(), devinfo.getInterface() + 5,
                    devinfo.getAltinterface());
            fail("USBException expected");
        } catch (USBException e) {
            log.severe("could not claim interface "
                    + (devinfo.getInterface() + 5));
        }
        doOpenWriteReadClose();
    }

    /**
     * Currently I do not know how this should fail :(
     * @throws Exception
     */
    public void _testInvalidAltinterface() throws Exception {
        try {
            dev.open(devinfo.getConfiguration(), devinfo.getInterface(),
                    devinfo.getAltinterface() + 5);
            fail("USBException expected");
        } catch (USBException e) {
            log.severe("could not set alt interface "
                    + (devinfo.getAltinterface() + 5));
        }
        doOpenWriteReadClose();
    }

    public void testGetIdProduct() {
        assertEquals(devinfo.getIdProduct(), dev.getIdProduct());
    }

    public void testGetIdVendor() {
        assertEquals(devinfo.getIdVendor(), dev.getIdVendor());
    }

    public void testGetAltinterface() throws Exception {
        dev.open(
            devinfo.getConfiguration(),
            devinfo.getInterface(),
            devinfo.getAltinterface()
        );
        assertEquals(devinfo.getAltinterface(), dev.getAltinterface());
    }

    public void testGetConfiguration() {
        assertEquals(devinfo.getConfiguration(), dev.getConfiguration());
    }

    public void testGetInterface() {
        assertEquals(devinfo.getInterface(), dev.getInterface());
    }

    public void testGetMaxPacketSize() throws USBException {
        doOpen();
        assertEquals(devinfo.getMaxDataSize(), dev.getMaxPacketSize());
        doClose();
    }

    public void testGetVendor() {
        assertEquals(devinfo.getVendor(), dev.getVendor());
    }

    public void tearDown() throws Exception {
        if (dev != null && dev.isOpen()) {
            dev.close();
        }
    }

    private void doOpen() throws USBException {
        dev.open(devinfo.getConfiguration(), devinfo.getInterface(), devinfo.getAltinterface());
    }

    private void doClose() throws USBException {
        dev.close();
    }

    private void doOpenWriteReadClose() throws Exception {
        doOpen();
        doWriteRead();
        doClose();
    }

    private void doWriteRead() throws Exception {
        initTestData();
        try {
            if (devinfo.getMode().equals(TransferMode.Bulk)) {
                if (devinfo.getOutEPBulk() != -1) {
                    dev.writeBulk(devinfo.getOutEPBulk(), testData,
                            testData.length, devinfo.getTimeout(), false);
                }
                if (devinfo.getInEPBulk() != -1) {
                    dev.readBulk(devinfo.getInEPBulk(), readData,
                            readData.length, devinfo.getTimeout(), false);
                }
            } else if (devinfo.getMode().equals(TransferMode.Interrupt)) {
                if (devinfo.getOutEPInt() != -1) {
                    dev.writeInterrupt(devinfo.getOutEPInt(), testData,
                            testData.length, devinfo.getTimeout(), false);
                }
                if (devinfo.getInEPInt() != -1) {
                    dev.readInterrupt(devinfo.getInEPInt(), readData,
                            readData.length, devinfo.getTimeout(), false);
                }
            }
            if (devinfo.doCompareData()) {
                compare(testData, readData);
            }
        } catch (AssertionError e) {
            closeOnException();
            throw e;
        } catch (Exception e) {
            closeOnException();
            throw e;
        }
    }

    private static void compare(byte[] d1, byte[] d2) {
        int minLength = Math.min(d1.length, d2.length);
        for (int i = 0; i < minLength; i++) {
            assertEquals(d1[i], d2[i]);
        }
    }

    private static void timeout() {
        try {
            Thread.sleep(devinfo.getSleepTimeout());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void initTestData() {
        for (int i = 0; i < testData.length; i++) {
            testData[i] = (byte) (Math.random() * 0xff);
            readData[i] = 0;
        }
    }
}
