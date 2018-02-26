package com.ca.arcserve.edge.app.base.webservice;
import javax.jws.WebService;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncSessFlagEx;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncSessionAndTapeRec;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncStringAndFlag;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncTAPEDATAEX;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncTapeRecW;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncVersionDataExW;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfABFuncDetailExtRecEXW;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfABFuncMsgRecW;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfABFuncTAPEDATAEX;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfABFuncTapePropertyRec;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfstring;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfunsignedInt;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncManageStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;

@WebService(targetNamespace="http://webservice.ABIntergation.edge.arcserve.ca.com/")
public interface IEdgeABFuncIntegrationService {
	String ConnectARCserve(GatewayEntity gateway, String strARCServer, String strUser, String strPassword, ABFuncAuthMode mode, int port, Protocol  protocol) throws EdgeServiceFault;
	/*int submitABJob(String strSessionNo, String strJobScript) throws EdgeServiceFault;*/		
	ABFuncServerType GetServerType(String strSessionNo) throws EdgeServiceFault;
	String MarkArcserveManageStatus(String strSessionNo, String strEdgeServerName, Boolean bOverwrite, ABFuncManageStatus status) throws EdgeServiceFault;
	ABFuncManageStatus GetArcserveManageStatus(String strSessionNo, String strEdgeServerName) throws EdgeServiceFault;
	/*ArrayOfABFuncTapePropertyRec GetTapeRecListForRestore(String strSessionNo) throws EdgeServiceFault;
	ABFuncVersionDataExW getTapeSessionInfoExW(String strSessionNo, ABFuncTAPEDATAEX tapeDataEx) throws EdgeServiceFault;
	ABFuncTapeRecW getTapeRecordW(String strSessionNo, String strTapeName, Short randomID, Short seqnum) throws EdgeServiceFault;
	ArrayOfABFuncTAPEDATAEX getSessionListEx2( String strSessionNo, ABFuncTAPEDATAEX tapeDataEx, Integer beginSesstime, Integer endSesstime, Integer lFlag) throws EdgeServiceFault;
	ABFuncSessionAndTapeRec getSessionAndTapeRecBySesIDW( String strSessionNo, Integer sessionID) throws EdgeServiceFault;
	ABFuncSessFlagEx getSessFlagEx(String strSessionNo, Integer sessid) throws EdgeServiceFault;
	ArrayOfABFuncTAPEDATAEX getDataListEx(String strSessionNo, ABFuncTAPEDATAEX tapeDataEx) throws EdgeServiceFault;
	ABFuncStringAndFlag getNameFromSessionW(String strSessionNo, Integer sessionID, Long ulID) throws EdgeServiceFault;
	ABFuncStringAndFlag getStreamNameFromSessionW(String strSessionNo, Integer sessionID, Long ulID) throws EdgeServiceFault;
	ABFuncStringAndFlag getStringFromSessionW(String strSessionNo, Integer sessionID, Long ulID) throws EdgeServiceFault;*/
	String getGDBServer(String strSessionNo) throws EdgeServiceFault;	
	/*Long catlogdbQueryFirstExW(String strSessionNo, String sComputerName, String sDir, Boolean bCaseSensitive, Boolean bIncludeSubDir, Long begin_sesstime, Long end_sesstime, String pattern) throws EdgeServiceFault;
	ArrayOfABFuncDetailExtRecEXW catlogdbQueryNextExW(String strSessionNo, Long handle, Long nRequest) throws EdgeServiceFault;
    void catlogdbQueryCloseW(String strSessionNo, Long handle) throws EdgeServiceFault;
    ArrayOfunsignedInt SQLFindFileAddrListW(String strSessionNo, String  sPattern, String  sHost, Boolean bCaseSensitive, String  DefStr, Boolean bFirst) throws EdgeServiceFault;
    ArrayOfABFuncDetailExtRecEXW QueryFileListEXW(String strSessionNo, String sComputerName, String sDir, Boolean bCaseSensitive, Boolean bIncludeSubDir, Long NameID, Boolean bFirst, Long nRequest) throws EdgeServiceFault;
    ArrayOfABFuncMsgRecW MsgFindFileExW(String strSessionNo, Integer ulFlags, String sHost, String sPath, String sPattern, Long begin_sesstime, Long end_sesstime, Boolean bFirst) throws EdgeServiceFault;
    ArrayOfABFuncMsgRecW MsgFindFileW(String strSessionNo, Integer ulFlags, String sHost, String sPath, String sPattern, Boolean bFirst) throws EdgeServiceFault;
    ArrayOfstring GetUsernameAndPassword(String strSessionNo, String sUser, String sPwd, String sHost) throws EdgeServiceFault;*/
    ArrayOfstring getArcserveVersionInfo(String strSessionNo)throws EdgeServiceFault;
    Boolean IsArcserveBranch(String strSessionNo)throws EdgeServiceFault;
    String GetManagedEdgeServer(String strSessionNo)throws EdgeServiceFault;
}
