/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.ntb.usb;

import junit.framework.TestCase;

/**
 *
 * @author ste
 */
public class DeviceDatabaseTest extends TestCase {
    
    public DeviceDatabaseTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetVendorWithInvalidId() {
        DeviceDatabase d = new DeviceDatabase();

        try {
            d.getVendor(null);
            fail("Invalid vendor id must be checked");
        } catch (IllegalArgumentException e) {
            //
            // This is OK
            //
        }
    }

    public void testGetVendorNotFound() {
        DeviceDatabase d = new DeviceDatabase();

        assertNull(d.getVendor("0000"));
    }

    public void testGetVendor() {
        DeviceDatabase d = new DeviceDatabase();

        assertEquals("Canon, Inc.", d.getVendor("04a9"));
    }

    public void testGetModelWithInvalidId() {
        DeviceDatabase d = new DeviceDatabase();

        try {
            d.getModel("04a9", null);
            fail("Invalid model id must be checked");
        } catch (IllegalArgumentException e) {
            //
            // This is OK
            //
        }
    }

    public void testGetNotExistingModel() {
        DeviceDatabase d = new DeviceDatabase();

        String model = d.getModel("04a9", "0000");

        assertNull(model);
    }

    public void testGetNotExistingVendorModel() {
        DeviceDatabase d = new DeviceDatabase();

        String model = d.getModel("0000", "317b");

        assertNull(model);
    }

    public void testGetExistingModel() {
        DeviceDatabase d = new DeviceDatabase();

        String model = d.getModel("04a9", "317b");

        assertEquals("EOS 1000D", model);
    }

    public void testGetExistingModelDisplayName() {
        DeviceDatabase d = new DeviceDatabase();

        String model = d.getModelDisplayName("04a9", "317b");

        assertEquals("Canon EOS 1000D", model);
    }

    public void testGetDisplayNameWithVendorOnly() {
        DeviceDatabase d = new DeviceDatabase();

        String model = d.getModelDisplayName("04a9", "0000");

        assertEquals("Canon (Unknown - 0x0000)", model);
    }

    public void testGetDisplayNameWithNoVendor() {
        DeviceDatabase d = new DeviceDatabase();

        String model = d.getModelDisplayName("0000", "317b");

        assertNull(model);
    }


}
