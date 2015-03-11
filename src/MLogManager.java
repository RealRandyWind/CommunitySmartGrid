public class MLogManager
{
	public enum ELogType{
		LOG,INFO,SUCCESS,WARNING,ERROR,DEBUG,
		ANY,ALL,DEFAULT
	}

	private static final String LOG_LOG_STR = "LOG: ";
	private static final String LOG_ERROR_STR = "ERROR: ";
	private static final String LOG_SUCCESS_STR = "SUCCESS: ";
	private static final String LOG_INFO_STR = "INFO: ";
	private static final String LOG_WARNING_STR = "WARNING: ";
	private static final String LOG_DEBUG_STR = "DEBUG: ";

	private static final long LOG_WARNING_USLEEP_TIME = (long)(2*1e+6);
	private static final long LOG_FILE_FLUSHRATE = 32;
	private static final long LOG_TYPE_COUNT = 6;

	private static MLogManager a_oInstance = null;
	private boolean a_bIsSetUp = false;
	private boolean a_bIsShutDown = true;

	private MLogManager() {
		a_bIsShutDown = false;
		a_bIsSetUp = false;
	} 

	public static MLogManager GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new MLogManager();
		return a_oInstance;
	}

	public void SetUp() {
		if(a_bIsSetUp) {
			Warning("[MLogManager.SetUp] already setup already.",0);
			return; 
		}
		a_bIsShutDown = false;

		// TODO MLogManager SetUp

		a_bIsSetUp = true;
		Success("[MLogManager.SetUp] setup MLogManager is done.",0);
	}

	public void ShutDown() {
		if(a_bIsShutDown) {
			System.out.printf("_" + LOG_WARNING_STR + "[MLogManager.ShutDown] shutdown already.\n");
			return; 
		}
		a_bIsSetUp = false;

		// TODO MLogManager ShutDown

		a_bIsShutDown = true;
		System.out.printf("_" + LOG_SUCCESS_STR + "[MLogManager.ShutDown] shutdown already.\n");
	}

	public void SetLogFile(String sFilePath) {
		SetLogFile(sFilePath,ELogType.ALL);
	}

	public void SetLogFile(String sFilePath, ELogType eType) {
		/*if(LOG_ERROR & eType) { Fx_SetLogFile(sFilePath,0); }*/
	}

	public void Log(String sFormat, int iCode, Object... olArgs) {
		String sString = String.format(LOG_LOG_STR + sFormat + "\n", olArgs);
		Fx_PrintLog(sString,iCode);
		/*
		Fx_WriteLog(sString,0,iCode)
		if(a_sTypeIsPrint[0]) { Fx_PrintLog(sString,iCode)}
		*/
	}

	public void Success(String sFormat, int iCode, Object... olArgs) {
		String sString = String.format(LOG_SUCCESS_STR + sFormat + "\n", olArgs);
		Fx_PrintLog(sString,iCode);
		/*
		Fx_WriteLog(sString,1,iCode)
		if(a_sTypeIsPrint[1]) { Fx_PrintLog(sString,iCode)}
		*/
	}

	public void Info(String sFormat, int iCode, Object... olArgs) {
		String sString = String.format(LOG_INFO_STR + sFormat + "\n", olArgs);
		Fx_PrintLog(sString,iCode);
		/*
		Fx_WriteLog(sString,2,iCode)
		if(a_sTypeIsPrint[2]) { Fx_PrintLog(sString,iCode)}
		*/
	}

	public void Warning(String sFormat, int iCode, Object... olArgs) {
		String sString = String.format(LOG_WARNING_STR + sFormat + "\n", olArgs);
		Fx_PrintLog(sString,iCode);
		/*
		Fx_WriteLog(sString,3,iCode)
		if(a_sTypeIsPrint[3]) { Fx_PrintLog(sString,iCode)}
		*/
	}

	public void Error(String sFormat, int iCode, Object... olArgs) {
		String sString = String.format(LOG_ERROR_STR + sFormat + "\n", olArgs);
		Fx_PrintLog(sString,iCode);
		/*
		Fx_WriteLog(sString,4,iCode)
		if(a_sTypeIsPrint[4]) { Fx_PrintLog(sString,iCode)}
		Main.ErrorShutDown();
		*/
	}

	public void Debug(String sFormat, int iCode, Object... olArgs) {
		String sString = String.format(LOG_DEBUG_STR + sFormat + "\n", olArgs);
		Fx_PrintLog(sString,iCode);
		/*
		Fx_WriteLog(sString,5,iCode)
		if(a_sTypeIsPrint[5]) { Fx_PrintLog(sString,iCode)}
		*/
	}

	private void Fx_PrintLog(String sMessage,int iCode) {
		/*Todo make treath prove*/
		System.out.print(sMessage);
	}
}
