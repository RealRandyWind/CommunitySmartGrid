package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.stub.server.analytic.gui.StubServerAnalyticGUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Deque;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

public class StubServerAnalyticGUITest implements ITestCase {
    StubServerAnalyticGUI a_oApplication = null;
    MLogManager a_mLogManager = null;
    Thread a_oStubServerAnalyticGUIThread = null;

    Deque<Serializable> a_lDataQueue = null;
    Deque<Serializable> a_lActionQueue = null;
    Deque<Serializable> a_lResultQueue = null;

    UUID a_iAnalyticServer = UUID.fromString("f89a1319-c937-4305-a6b5-d51b88e80e7e");

    @Before
    public void setUp() throws Exception {
        a_oApplication = (StubServerAnalyticGUI)StubServerAnalyticGUI.GetInstance();
        a_oStubServerAnalyticGUIThread = new Thread(() -> a_oApplication.Entry());
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
        AnalyticServerStub oAnalyticServerStub = new AnalyticServerStub(a_iAnalyticServer,"localhost","localhost",5672,27017,5675,55539,1099);
        oAnalyticServerStub.SetQueues(null,a_lDataQueue,a_lActionQueue,a_lResultQueue);
        oAnalyticServerStub.SetControllers(ControllerServerAnalyticStubController.GetInstance());
        oAnalyticServerStub.Start();
        a_oStubServerAnalyticGUIThread.start();
        Thread.sleep(5000);
        a_oApplication.Exit();
        oAnalyticServerStub.Stop();
        a_oStubServerAnalyticGUIThread.join();
        a_mLogManager.Test("end",0);
    }

}