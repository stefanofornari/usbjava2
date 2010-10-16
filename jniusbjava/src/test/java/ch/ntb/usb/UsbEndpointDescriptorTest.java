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
public class UsbEndpointDescriptorTest extends TestCase {

    private UsbEndpointDescriptor ENDPOINT = null;
    
    public UsbEndpointDescriptorTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ENDPOINT = new UsbEndpointDescriptor();
        ENDPOINT.setAttributes((byte)0xFF);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testTypes() {
        ENDPOINT.setAttributes((byte)0);
        assertTrue(ENDPOINT.isTypeControl());
        ENDPOINT.setAttributes((byte)1);
        assertTrue(ENDPOINT.isTypeIsochronous());
        ENDPOINT.setAttributes((byte)2);
        assertTrue(ENDPOINT.isTypeBulk());
        ENDPOINT.setAttributes((byte)3);
        assertTrue(ENDPOINT.isTypeInterrupt());
    }

}
