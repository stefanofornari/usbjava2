/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.ntb.usb;

import java.util.ArrayList;
import junit.framework.TestCase;

/**
 *
 * @author ste
 */
public class UsbInterfaceTest extends TestCase {

    private UsbInterface INTERFACE = null;
    
    public UsbInterfaceTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        INTERFACE = new UsbInterface();

        UsbInterfaceDescriptor[] desc = new UsbInterfaceDescriptor[] {
            new UsbInterfaceDescriptor(),
            new UsbInterfaceDescriptor()
        };

        UsbEndpointDescriptor[] endpoints1 = new UsbEndpointDescriptor[] {
            new UsbEndpointDescriptor(),
            new UsbEndpointDescriptor(),
        };

        UsbEndpointDescriptor[] endpoints2 = new UsbEndpointDescriptor[] {
            new UsbEndpointDescriptor()
        };

        desc[0].setInterfaceClass((byte)0x01);
        desc[1].setInterfaceClass((byte)0x02);
        desc[0].setInterfaceSubClass((byte)0x3);
        desc[1].setInterfaceSubClass((byte)0x4);
        desc[0].setInterfaceProtocol((byte)0x05);
        desc[1].setInterfaceProtocol((byte)0x06);
        desc[0].setEndpoints(endpoints1);
        desc[1].setEndpoints(endpoints2);

        INTERFACE.setAlternateSetting(desc);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNullAltSettings() {
        UsbInterface i = new UsbInterface();

        assertNull(i.getAlternateSetting());
        assertEquals(0, i.getNumAlternateSetting());
    }

    public void testGetNumAlternateSetting() {
        assertEquals(2, INTERFACE.getNumAlternateSetting());
    }

    public void testDoesNotBelongToClass() {
        assertFalse(INTERFACE.belongsToClass((byte)0x06));
    }
    
    public void testBelongsToClass() {
        assertTrue(INTERFACE.belongsToClass((byte)0x01));
        assertTrue(INTERFACE.belongsToClass((byte)0x02));
    }

    public void testDoesNotBelongToSubClass() {
        assertFalse(INTERFACE.belongsToSubClass((byte)0x06));
    }

    public void testBelongsToSubClass() {
        assertTrue(INTERFACE.belongsToSubClass((byte)0x03));
        assertTrue(INTERFACE.belongsToSubClass((byte)0x04));
    }

    public void testDoesNotSupportProtocol() {
        assertFalse(INTERFACE.supportsProtocol((byte)0xff));
    }

    public void testSupportsProtocol() {
        assertTrue(INTERFACE.supportsProtocol((byte)0x05));
        assertTrue(INTERFACE.supportsProtocol((byte)0x06));
    }

    public void testNotNullEndpoints() {
        ArrayList endpoints = new UsbInterface().getAllEndpoints();

        assertNotNull(endpoints);
    }

    public void testGetAllEndpointsEmpty() {
        ArrayList endpoints = new UsbInterface().getAllEndpoints();

        assertEquals(0, endpoints.size());
    }
    
    public void testGetAllEndpointsFull() {
        assertEquals(3, INTERFACE.getAllEndpoints().size());
    }


}
