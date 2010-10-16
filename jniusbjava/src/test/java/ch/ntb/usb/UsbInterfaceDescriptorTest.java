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
public class UsbInterfaceDescriptorTest extends TestCase {

    private UsbInterfaceDescriptor DESCRIPTOR = null;
    
    public UsbInterfaceDescriptorTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        DESCRIPTOR = new UsbInterfaceDescriptor();

        UsbEndpointDescriptor[] endpoints = {
            new UsbEndpointDescriptor(),
            new UsbEndpointDescriptor(),
            new UsbEndpointDescriptor()
        };

        DESCRIPTOR.setEndpoints(endpoints);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNullEndPoints() {
        UsbInterfaceDescriptor d = new UsbInterfaceDescriptor();

        assertNull(d.getEndpoints());
        assertEquals(0, d.getNumEndpoints());
    }

    public void testGetNumAlternateSetting() {
        assertEquals(3, DESCRIPTOR.getNumEndpoints());
    }

}
