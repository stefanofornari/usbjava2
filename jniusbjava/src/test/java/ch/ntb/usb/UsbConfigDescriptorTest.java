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
public class UsbConfigDescriptorTest extends TestCase {

    private final UsbInterface[] INTERFACES1 = new UsbInterface[] {
        new UsbInterface(),
        new UsbInterface(),
        new UsbInterface()
    };
    private final UsbInterface[] INTERFACES2 = new UsbInterface[] {
        new UsbInterface(),
        new UsbInterface(),
        new UsbInterface()
    };
    private final UsbInterfaceDescriptor[] INT_DESC1 = new UsbInterfaceDescriptor[] {
        new UsbInterfaceDescriptor()
    };
    private final UsbInterfaceDescriptor[] INT_DESC2 = new UsbInterfaceDescriptor[] {
        new UsbInterfaceDescriptor(),
        new UsbInterfaceDescriptor()
    };
    
    public UsbConfigDescriptorTest(String testName) {
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

    private UsbConfigDescriptor buildConfigDescriptor(
        boolean interfaces, boolean descriptors
    ) {
        UsbConfigDescriptor desc = new UsbConfigDescriptor();
        
        if (interfaces) {
            desc.setInterfaces((descriptors) ? INTERFACES1 : INTERFACES2);
        }

        if (descriptors) {
            INT_DESC1[0].setAlternateSetting((byte)0);
            INT_DESC2[0].setAlternateSetting((byte)1);
            INT_DESC2[1].setAlternateSetting((byte)2);
            INT_DESC2[1].setInterfaceClass((byte)UsbInterface.CLASS_IMAGE);

            INTERFACES1[0].setAlternateSetting(INT_DESC1);
            INTERFACES1[1].setAlternateSetting(INT_DESC2);
            
            INT_DESC1[0].setUsbInterface(INTERFACES1[0]);
            INT_DESC2[0].setUsbInterface(INTERFACES1[1]);
            INT_DESC2[1].setUsbInterface(INTERFACES1[1]);
        }

        return desc;
    }

    /**
     * Test of getInterfaces method, of class UsbConfigDescriptor.
     */
    public void testGetInterfaceInvalidIndex() {
        UsbConfigDescriptor desc = buildConfigDescriptor(true, true);
        try {
            desc.getInterface(-1);
            fail("index out of bound must be caught");
        } catch (IllegalArgumentException e) {
            //
            // OK
            //
        }

        try {
            desc.getInterface(3);
            fail("index out of bound must be caught");
        } catch (IllegalArgumentException e) {
            //
            // OK
            //
        }
    }

    public void testGetInterfaceNull() {
        UsbConfigDescriptor desc = buildConfigDescriptor(false, false);

        try {
            desc.getInterface(0);
            fail("null interfaces must be caught");
        } catch (IllegalArgumentException e) {
            //
            // OK
            //
        }
    }

    public void testGetInterfaceValidIndex() {
        UsbConfigDescriptor desc = buildConfigDescriptor(true, true);

        assertSame(INTERFACES1[0], desc.getInterface(0));
        assertSame(INTERFACES1[1], desc.getInterface(1));
        assertSame(INTERFACES1[2], desc.getInterface(2));

    }

    public void testGetInterfaceWithValidAltSettingsIndex() {
        UsbConfigDescriptor desc = buildConfigDescriptor(true, true);

        assertSame(INTERFACES1[0], desc.getInterfaceByAlternateSetting(0));
        assertSame(INTERFACES1[1], desc.getInterfaceByAlternateSetting(1));
        assertSame(INTERFACES1[1], desc.getInterfaceByAlternateSetting(2));
    }

    public void testGetInterfaceWithInvalidAltSettingsIndex() {
        UsbConfigDescriptor desc = buildConfigDescriptor(true, true);

        assertNull(desc.getInterfaceByAlternateSetting(10));
    }

    public void testGetInterfaceByClass() {
        UsbConfigDescriptor desc = buildConfigDescriptor(true, true);

        assertNull(desc.getInterfaceByClass(0x01));

        assertSame(INT_DESC2[1], desc.getInterfaceByClass(UsbInterface.CLASS_IMAGE));
    }
}
