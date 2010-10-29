/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2008 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.logging.Logger;

import junit.framework.TestCase;

import ch.ntb.usb.Device;
import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;
import ch.ntb.usb.UsbBus;
import ch.ntb.usb.Utils;
import ch.ntb.usb.AbstractDeviceInfo;
import ch.ntb.usb.AbstractDeviceInfo.TransferMode;

public class MemoryLeakTest extends TestCase {

    private static final String testdevicePropertiesFile = "/testdevice.properties";
    private static final String deviceInfoKey = "testdeviceInfo";
    private static AbstractDeviceInfo devinfo;
    private static byte[] testData;
    private static byte[] readData;
    private static Device dev;
    private static Logger log = Logger.getLogger(MemoryLeakTest.class.getName());

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
        // setup test data
        testData = new byte[devinfo.getMaxDataSize()];
        readData = new byte[testData.length];
        // initialise the device
        LibusbJava.usb_set_debug(255);
        dev = USB.getDevice(devinfo.getIdVendor(), devinfo.getIdProduct());
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

    public void testInitalReset() throws Exception {
        doOpen();
        dev.reset();
        timeout();
    }

    public void testBulkWriteReadMultiple() throws Exception {
        final int NumberOfIterations = 3000;

        devinfo.setMode(TransferMode.Bulk);
        doOpen();
        for (int i = 0; i < NumberOfIterations; i++) {
            doWriteRead();
            if (i % 1000 == 0) {
                System.out.print(".");
            }
        }
        doClose();
    }

    public void testInterruptWriteReadMultiple() throws Exception {
        final int NumberOfIterations = 3000;

        devinfo.setMode(TransferMode.Interrupt);
        doOpen();
        for (int i = 0; i < NumberOfIterations; i++) {
            doWriteRead();
            if (i % 1000 == 0) {
                System.out.print(".");
            }
        }
        doClose();
    }

    private void closeOnException() {
        try {
            dev.close();
        } catch (USBException e1) {
            // ignore exceptions
        }
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
        final int minLength = Math.min(d1.length, d2.length);
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
