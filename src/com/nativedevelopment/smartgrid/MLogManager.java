package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MLogManager {
	public enum ELogType {
		LOG,INFO,SUCCESS,WARNING,ERROR,DEBUG,
		ANY,ALL,DEFAULT
	}

	private static final String LOG_DEFAULT_FILE = "default.log";

	private static final String LOG_RESET_COL = "\u001b[0m";

	private static final String LOG_LOG_COL = "\u001B[37m";
	private static final String LOG_ERROR_COL = "\u001b[31m";
	private static final String LOG_SUCCESS_COL = "\u001b[32m";
	private static final String LOG_INFO_COL = "\u001b[36m";
	private static final String LOG_WARNING_COL = "\u001b[33m";
	private static final String LOG_DEBUG_COL = LOG_RESET_COL;
	private static final String LOG_TEST_COL = "\u001b[34m";

	private static final String LOG_LOG_STR = "LOG: ";
	private static final String LOG_ERROR_STR = "ERROR: ";
	private static final String LOG_SUCCESS_STR = "SUCCESS: ";
	private static final String LOG_INFO_STR = "INFO: ";
	private static final String LOG_WARNING_STR = "WARNING: ";
	private static final String LOG_DEBUG_STR = "DEBUG: ";
	private static final String LOG_TEST_STR = "TEST: ";

	private static final long LOG_WARNING_USLEEP_TIME = (long)(2*1e+6);
	private static final long LOG_FILE_FLUSHRATE = 32;
	private static final long LOG_TYPE_COUNT = 6;
	private static final int LOG_STACKTRACE_SELF_INDEX = 2;
	private static final int LOG_STACKTRACE_INDEX = 3;

	private static MLogManager a_oInstance = null;
	private boolean a_bIsSetUp = false;
	private boolean a_bIsShutDown = false;
	private boolean a_bIsShutDownOnError = false;

	private Queue<Serializable> a_lLogQueue = null;

	private MLogManager() {
		a_bIsShutDown = true;
		a_bIsSetUp = false;
		a_bIsShutDownOnError = true;
	} 

	public static MLogManager GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new MLogManager();
		return a_oInstance;
	}

	public void SetUp() {
		if(a_bIsSetUp) {
			Warning("setup already.",0);
			return; 
		}
		a_bIsShutDown = false;

		a_lLogQueue = new ConcurrentLinkedQueue<>();

		a_bIsSetUp = true;
		Success("",0);
	}

	public void ShutDown() {
		if(a_bIsShutDown) {
			System.out.printf("_" + LOG_WARNING_STR + MethodName() + "already shutdown.\n");
			return;
		}
		a_bIsSetUp = false;

		// TODO MLogManager ShutDown

		a_bIsShutDown = true;
		System.out.printf("_" + LOG_SUCCESS_STR + MethodName() + "\n");
	}

	public void SetLogFile(String sFilePath) {
		SetLogFile(sFilePath,ELogType.ALL);
	}

	public void SetLogFile(String sFilePath, ELogType eType) {
		/*if(LOG_ERROR & eType) { Fx_SetLogFile(sFilePath,0); }*/
	}

	public void SetShutDownOnError(boolean bIsShutDownOnError) {
		a_bIsShutDownOnError = bIsShutDownOnError;
	}

	public void Log(String sFormat, int iCode, Object... olArgs) {
		String sString = String.format(LOG_LOG_COL + LOG_LOG_STR + sFormat, olArgs);
		Fx_PrintLog(sString,iCode);
		/*
		Fx_WriteLog(sString,0,iCode)
		if(a_sTypeIsPrint[0]) { Fx_PrintLog(sString,iCode)}
		*/
	}

	public void Success(String sFormat, int iCode, Object... olArgs) {
		String sString = String.format(LOG_SUCCESS_COL + LOG_SUCCESS_STR + Fx_MethodName() + sFormat, olArgs);
		Fx_PrintLog(sString,iCode);
		/*
		Fx_WriteLog(sString,1,iCode)
		if(a_sTypeIsPrint[1]) { Fx_PrintLog(sString,iCode)}
		*/
	}

	public void Info(String sFormat, int iCode, Object... olArgs) {
		String sString = String.format(LOG_INFO_COL + LOG_INFO_STR + Fx_MethodName() + sFormat, olArgs);
		Fx_PrintLog(sString,iCode);
		/*
		Fx_WriteLog(sString,2,iCode)
		if(a_sTypeIsPrint[2]) { Fx_PrintLog(sString,iCode)}
		*/
	}

	public void Warning(String sFormat, int iCode, Object... olArgs) {
		String sString = String.format(LOG_WARNING_COL + LOG_WARNING_STR + Fx_MethodName() + sFormat, olArgs);
		Fx_PrintLog(sString,iCode);
		/*
		Fx_WriteLog(sString,3,iCode)
		if(a_sTypeIsPrint[3]) { Fx_PrintLog(sString,iCode)}
		*/
	}

	public void Error(String sFormat, int iCode, Object... olArgs) {
		String sString = String.format(LOG_ERROR_COL + LOG_ERROR_STR + Fx_MethodName() + sFormat, olArgs);
		Fx_PrintLog(sString,iCode);
		/*
		Fx_WriteLog(sString,4,iCode)
		if(a_sTypeIsPrint[4]) { Fx_PrintLog(sString,iCode)}
		*/
		if(a_bIsShutDownOnError) {
			Main.ErrorShutDown();
		}
	}

	public void Debug(String sFormat, int iCode, Object... olArgs) {
		String sString = String.format(LOG_DEBUG_COL + LOG_DEBUG_STR + Fx_MethodName() + sFormat, olArgs);
		Fx_PrintLog(sString,iCode);
		/*
		Fx_WriteLog(sString,5,iCode)
		if(a_sTypeIsPrint[5]) { Fx_PrintLog(sString,iCode)}
		*/
	}

	public void Test(String sFormat, int iCode, Object... olArgs) {
		String sString = String.format(LOG_TEST_COL + LOG_TEST_STR + Fx_MethodName() + sFormat, olArgs);
		Fx_PrintLog(sString,iCode);
		/*
		Fx_WriteLog(sString,5,iCode)
		if(a_sTypeIsPrint[5]) { Fx_PrintLog(sString,iCode)}
		*/
	}

	public Queue<Serializable> GetLogQueue() {
		return a_lLogQueue;
	}

	private void Fx_PrintLog(String sMessage,int iCode) {
		/*TODO make threat prove*/
		Timestamp oTimeStamp = new Timestamp(System.currentTimeMillis());
		System.out.print(sMessage + LOG_RESET_COL + " - " + oTimeStamp + "\n");
	}

	public static String MethodName() {
		StackTraceElement oMethod = Thread.currentThread().getStackTrace()[LOG_STACKTRACE_SELF_INDEX];
		String sMethod = String.format("[%s.%s] ", oMethod.getClassName(),oMethod.getMethodName());
		return sMethod;
	}

	private static String Fx_MethodName() {
		StackTraceElement oMethod = Thread.currentThread().getStackTrace()[LOG_STACKTRACE_INDEX];
		String sMethod = String.format("[%s.%s] ", oMethod.getClassName(),oMethod.getMethodName());
		return sMethod;
	}
}
