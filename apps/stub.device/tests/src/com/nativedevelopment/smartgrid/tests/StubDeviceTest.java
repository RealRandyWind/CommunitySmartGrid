package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.IController;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.stub.device.StubDevice;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class StubDeviceTest implements ITestCase {
    StubDevice a_oApplication = null;
    MLogManager a_mLogManager = null;
    Thread a_oStubDeviceThread = null;

    Deque<Serializable> a_lDataQueue = null;
    Deque<Serializable> a_lActionQueue = null;
    Deque<Serializable> a_lResultQueue = null;
    IController a_oController = null;

    @Before
    public void setUp() throws Exception {
        a_oApplication = (StubDevice)StubDevice.GetInstance();
        a_oStubDeviceThread = new Thread(() -> a_oApplication.Entry());
        a_mLogManager = MLogManager.GetInstance();
        a_mLogManager.SetUp();
        a_lDataQueue = new ConcurrentLinkedDeque<>();
        a_lActionQueue = new ConcurrentLinkedDeque<>();
        a_lResultQueue = new ConcurrentLinkedDeque<>();
    }

    @After
    public void tearDown() throws Exception {
        a_mLogManager.ShutDown();
    }

    @Test
    public void run() throws Exception {
        a_mLogManager.Test("begin",0);
        AnalyticServerStub oAnalyticServerStub = new AnalyticServerStub(null,"localhost","localhost",5672,27017,5675,55539,1099);
        oAnalyticServerStub.SetQueues(null,a_lDataQueue,a_lActionQueue,a_lResultQueue);
        oAnalyticServerStub.SetControllers(ControllerServerAnalyticStubController.GetInstance());
        oAnalyticServerStub.Start();
        a_oStubDeviceThread.start();
        Thread.sleep(5000);
        oAnalyticServerStub.Stop();
        a_oApplication.Exit();
        a_oStubDeviceThread.join();
        a_mLogManager.Test("end",0);
    }

}