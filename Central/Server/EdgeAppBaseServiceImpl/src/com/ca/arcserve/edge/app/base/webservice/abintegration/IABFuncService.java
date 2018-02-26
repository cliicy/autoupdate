
package com.ca.arcserve.edge.app.base.webservice.abintegration;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

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
import com.ca.arcserve.edge.app.base.webservice.arcserve.ObjectFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncManageStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "IABFuncService", targetNamespace = "http://tempuri.org/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface IABFuncService {


   /* *//**
     * 
     * @param strJobScript
     * @param strSessionNo
     * @return
     *     returns java.lang.Integer
     *//*
    @WebMethod(operationName = "SubmitABJob", action = "http://tempuri.org/IABFuncService/SubmitABJob")
    @WebResult(name = "SubmitABJobResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SubmitABJob", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.SubmitABJob")
    @ResponseWrapper(localName = "SubmitABJobResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.SubmitABJobResponse")
    public Integer submitABJob(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "strJobScript", targetNamespace = "http://tempuri.org/")
        String strJobScript);

    *//**
     * 
     * @return
     *     returns java.lang.String
     *//*
    @WebMethod(action = "http://tempuri.org/IABFuncService/testWebService")
    @WebResult(name = "testWebServiceResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "testWebService", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.TestWebService")
    @ResponseWrapper(localName = "testWebServiceResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.TestWebServiceResponse")
    public String testWebService();*/

    /**
     * 
     * @param strUser
     * @param strPassword
     * @param mode
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "ConnectARCserve", action = "http://tempuri.org/IABFuncService/ConnectARCserve")
    @WebResult(name = "ConnectARCserveResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "ConnectARCserve", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.ConnectARCserve")
    @ResponseWrapper(localName = "ConnectARCserveResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.ConnectARCserveResponse")
    public String connectARCserve(
        @WebParam(name = "strUser", targetNamespace = "http://tempuri.org/")
        String strUser,
        @WebParam(name = "strPassword", targetNamespace = "http://tempuri.org/")
        String strPassword,
        @WebParam(name = "mode", targetNamespace = "http://tempuri.org/")
        ABFuncAuthMode mode);

    /**
     * 
     * @param strSessionNo
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncServerType
     */
    @WebMethod(operationName = "GetServerType", action = "http://tempuri.org/IABFuncService/GetServerType")
    @WebResult(name = "GetServerTypeResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetServerType", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetServerType")
    @ResponseWrapper(localName = "GetServerTypeResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetServerTypeResponse")
    public ABFuncServerType getServerType(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo);

    /**
     * 
     * @param strSessionNo
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfstring
     */
    @WebMethod(operationName = "GetArcserveVersionInfo", action = "http://tempuri.org/IABFuncService/GetArcserveVersionInfo")
    @WebResult(name = "GetArcserveVersionInfoResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetArcserveVersionInfo", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetArcserveVersionInfo")
    @ResponseWrapper(localName = "GetArcserveVersionInfoResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetArcserveVersionInfoResponse")
    public ArrayOfstring getArcserveVersionInfo(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo);

    /**
     * 
     * @param strSessionNo
     * @return
     *     returns java.lang.Boolean
     */
    @WebMethod(operationName = "IsArcserveBranch", action = "http://tempuri.org/IABFuncService/IsArcserveBranch")
    @WebResult(name = "IsArcserveBranchResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "IsArcserveBranch", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.IsArcserveBranch")
    @ResponseWrapper(localName = "IsArcserveBranchResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.IsArcserveBranchResponse")
    public Boolean isArcserveBranch(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo);

    /**
     * 
     * @param strSessionNo
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GetManagedEdgeServer", action = "http://tempuri.org/IABFuncService/GetManagedEdgeServer")
    @WebResult(name = "GetManagedEdgeServerResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetManagedEdgeServer", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetManagedEdgeServer")
    @ResponseWrapper(localName = "GetManagedEdgeServerResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetManagedEdgeServerResponse")
    public String getManagedEdgeServer(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo);

    /**
     * 
     * @param strEdgeServerId
     * @param strEdgeServerDomain
     * @param status
     * @param strEdgeServerPassword
     * @param strSessionNo
     * @param bOverwrite
     * @param strEdgeServerUserName
     * @param strEdgeServiceWsdl
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "MarkArcserveManageStatus", action = "http://tempuri.org/IABFuncService/MarkArcserveManageStatus")
    @WebResult(name = "MarkArcserveManageStatusResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "MarkArcserveManageStatus", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.MarkArcserveManageStatus")
    @ResponseWrapper(localName = "MarkArcserveManageStatusResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.MarkArcserveManageStatusResponse")
    public String markArcserveManageStatus(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "strEdgeServerId", targetNamespace = "http://tempuri.org/")
        String strEdgeServerId,
        @WebParam(name = "strEdgeServerUserName", targetNamespace = "http://tempuri.org/")
        String strEdgeServerUserName,
        @WebParam(name = "strEdgeServerPassword", targetNamespace = "http://tempuri.org/")
        String strEdgeServerPassword,
        @WebParam(name = "strEdgeServerDomain", targetNamespace = "http://tempuri.org/")
        String strEdgeServerDomain,
        @WebParam(name = "strEdgeServiceWsdl", targetNamespace = "http://tempuri.org/")
        String strEdgeServiceWsdl,
        @WebParam(name = "bOverwrite", targetNamespace = "http://tempuri.org/")
        Boolean bOverwrite,
        @WebParam(name = "status", targetNamespace = "http://tempuri.org/")
        ABFuncManageStatus status);

    /**
     * 
     * @param strEdgeServerName
     * @param strSessionNo
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncManageStatus
     */
    @WebMethod(operationName = "GetArcserveManageStatus", action = "http://tempuri.org/IABFuncService/GetArcserveManageStatus")
    @WebResult(name = "GetArcserveManageStatusResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetArcserveManageStatus", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetArcserveManageStatus")
    @ResponseWrapper(localName = "GetArcserveManageStatusResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetArcserveManageStatusResponse")
    public ABFuncManageStatus getArcserveManageStatus(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "strEdgeServerName", targetNamespace = "http://tempuri.org/")
        String strEdgeServerName);

   /* *//**
     * 
     * @param strSessionNo
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfABFuncTapePropertyRec
     *//*
    @WebMethod(operationName = "GetTapeRecListForRestore", action = "http://tempuri.org/IABFuncService/GetTapeRecListForRestore")
    @WebResult(name = "GetTapeRecListForRestoreResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetTapeRecListForRestore", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetTapeRecListForRestore")
    @ResponseWrapper(localName = "GetTapeRecListForRestoreResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetTapeRecListForRestoreResponse")
    public ArrayOfABFuncTapePropertyRec getTapeRecListForRestore(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo);

    *//**
     * 
     * @param strSessionNo
     * @param tapeDataEx
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncVersionDataExW
     *//*
    @WebMethod(operationName = "GetTapeSessionInfoExW", action = "http://tempuri.org/IABFuncService/GetTapeSessionInfoExW")
    @WebResult(name = "GetTapeSessionInfoExWResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetTapeSessionInfoExW", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetTapeSessionInfoExW")
    @ResponseWrapper(localName = "GetTapeSessionInfoExWResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetTapeSessionInfoExWResponse")
    public ABFuncVersionDataExW getTapeSessionInfoExW(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "tapeDataEx", targetNamespace = "http://tempuri.org/")
        ABFuncTAPEDATAEX tapeDataEx);

    *//**
     * 
     * @param randomID
     * @param strTapeName
     * @param strSessionNo
     * @param seqnum
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncTapeRecW
     *//*
    @WebMethod(operationName = "GetTapeRecordW", action = "http://tempuri.org/IABFuncService/GetTapeRecordW")
    @WebResult(name = "GetTapeRecordWResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetTapeRecordW", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetTapeRecordW")
    @ResponseWrapper(localName = "GetTapeRecordWResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetTapeRecordWResponse")
    public ABFuncTapeRecW getTapeRecordW(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "strTapeName", targetNamespace = "http://tempuri.org/")
        String strTapeName,
        @WebParam(name = "randomID", targetNamespace = "http://tempuri.org/")
        Short randomID,
        @WebParam(name = "seqnum", targetNamespace = "http://tempuri.org/")
        Short seqnum);

    *//**
     * 
     * @param beginSesstime
     * @param strSessionNo
     * @param endSesstime
     * @param tapeDataEx
     * @param lFlag
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfABFuncTAPEDATAEX
     *//*
    @WebMethod(operationName = "GetSessionListEx2", action = "http://tempuri.org/IABFuncService/GetSessionListEx2")
    @WebResult(name = "GetSessionListEx2Result", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetSessionListEx2", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetSessionListEx2")
    @ResponseWrapper(localName = "GetSessionListEx2Response", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetSessionListEx2Response")
    public ArrayOfABFuncTAPEDATAEX getSessionListEx2(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "tapeDataEx", targetNamespace = "http://tempuri.org/")
        ABFuncTAPEDATAEX tapeDataEx,
        @WebParam(name = "begin_sesstime", targetNamespace = "http://tempuri.org/")
        Integer beginSesstime,
        @WebParam(name = "end_sesstime", targetNamespace = "http://tempuri.org/")
        Integer endSesstime,
        @WebParam(name = "lFlag", targetNamespace = "http://tempuri.org/")
        Integer lFlag);

    *//**
     * 
     * @param sessionID
     * @param strSessionNo
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncSessionAndTapeRec
     *//*
    @WebMethod(operationName = "GetSessionAndTapeRecBySesIDW", action = "http://tempuri.org/IABFuncService/GetSessionAndTapeRecBySesIDW")
    @WebResult(name = "GetSessionAndTapeRecBySesIDWResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetSessionAndTapeRecBySesIDW", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetSessionAndTapeRecBySesIDW")
    @ResponseWrapper(localName = "GetSessionAndTapeRecBySesIDWResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetSessionAndTapeRecBySesIDWResponse")
    public ABFuncSessionAndTapeRec getSessionAndTapeRecBySesIDW(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "SessionID", targetNamespace = "http://tempuri.org/")
        Integer sessionID);

    *//**
     * 
     * @param strSessionNo
     * @param sessid
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncSessFlagEx
     *//*
    @WebMethod(operationName = "GetSessFlagEx", action = "http://tempuri.org/IABFuncService/GetSessFlagEx")
    @WebResult(name = "GetSessFlagExResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetSessFlagEx", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetSessFlagEx")
    @ResponseWrapper(localName = "GetSessFlagExResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetSessFlagExResponse")
    public ABFuncSessFlagEx getSessFlagEx(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "sessid", targetNamespace = "http://tempuri.org/")
        Integer sessid);

    *//**
     * 
     * @param strSessionNo
     * @param tapeDataEx
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfABFuncTAPEDATAEX
     *//*
    @WebMethod(operationName = "GetDataListEx", action = "http://tempuri.org/IABFuncService/GetDataListEx")
    @WebResult(name = "GetDataListExResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetDataListEx", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetDataListEx")
    @ResponseWrapper(localName = "GetDataListExResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetDataListExResponse")
    public ArrayOfABFuncTAPEDATAEX getDataListEx(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "tapeDataEx", targetNamespace = "http://tempuri.org/")
        ABFuncTAPEDATAEX tapeDataEx);

    *//**
     * 
     * @param sessionID
     * @param strSessionNo
     * @param ulID
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncStringAndFlag
     *//*
    @WebMethod(operationName = "GetNameFromSessionW", action = "http://tempuri.org/IABFuncService/GetNameFromSessionW")
    @WebResult(name = "GetNameFromSessionWResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetNameFromSessionW", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetNameFromSessionW")
    @ResponseWrapper(localName = "GetNameFromSessionWResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetNameFromSessionWResponse")
    public ABFuncStringAndFlag getNameFromSessionW(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "SessionID", targetNamespace = "http://tempuri.org/")
        Integer sessionID,
        @WebParam(name = "ulID", targetNamespace = "http://tempuri.org/")
        Long ulID);

    *//**
     * 
     * @param sessionID
     * @param strSessionNo
     * @param ulID
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncStringAndFlag
     *//*
    @WebMethod(operationName = "GetStreamNameFromSessionW", action = "http://tempuri.org/IABFuncService/GetStreamNameFromSessionW")
    @WebResult(name = "GetStreamNameFromSessionWResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetStreamNameFromSessionW", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetStreamNameFromSessionW")
    @ResponseWrapper(localName = "GetStreamNameFromSessionWResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetStreamNameFromSessionWResponse")
    public ABFuncStringAndFlag getStreamNameFromSessionW(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "SessionID", targetNamespace = "http://tempuri.org/")
        Integer sessionID,
        @WebParam(name = "ulID", targetNamespace = "http://tempuri.org/")
        Long ulID);

    *//**
     * 
     * @param sessionID
     * @param strSessionNo
     * @param ulID
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ABFuncStringAndFlag
     *//*
    @WebMethod(operationName = "GetStringFromSessionW", action = "http://tempuri.org/IABFuncService/GetStringFromSessionW")
    @WebResult(name = "GetStringFromSessionWResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetStringFromSessionW", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetStringFromSessionW")
    @ResponseWrapper(localName = "GetStringFromSessionWResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetStringFromSessionWResponse")
    public ABFuncStringAndFlag getStringFromSessionW(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "SessionID", targetNamespace = "http://tempuri.org/")
        Integer sessionID,
        @WebParam(name = "ulID", targetNamespace = "http://tempuri.org/")
        Long ulID);*/

    /**
     * 
     * @param strSessionNo
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GetGDBServer", action = "http://tempuri.org/IABFuncService/GetGDBServer")
    @WebResult(name = "GetGDBServerResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetGDBServer", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetGDBServer")
    @ResponseWrapper(localName = "GetGDBServerResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetGDBServerResponse")
    public String getGDBServer(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo);

    /**
     * 
     * @param beginSesstime
     * @param bIncludeSubDir
     * @param pattern
     * @param strSessionNo
     * @param bCaseSensitive
     * @param sDir
     * @param endSesstime
     * @param sComputerName
     * @return
     *     returns java.lang.Long
     *//*
    @WebMethod(operationName = "CATLOGDB_QueryFirstExW", action = "http://tempuri.org/IABFuncService/CATLOGDB_QueryFirstExW")
    @WebResult(name = "CATLOGDB_QueryFirstExWResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "CATLOGDB_QueryFirstExW", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.CATLOGDBQueryFirstExW")
    @ResponseWrapper(localName = "CATLOGDB_QueryFirstExWResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.CATLOGDBQueryFirstExWResponse")
    public Long catlogdbQueryFirstExW(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "sComputerName", targetNamespace = "http://tempuri.org/")
        String sComputerName,
        @WebParam(name = "sDir", targetNamespace = "http://tempuri.org/")
        String sDir,
        @WebParam(name = "bCaseSensitive", targetNamespace = "http://tempuri.org/")
        Boolean bCaseSensitive,
        @WebParam(name = "bIncludeSubDir", targetNamespace = "http://tempuri.org/")
        Boolean bIncludeSubDir,
        @WebParam(name = "begin_sesstime", targetNamespace = "http://tempuri.org/")
        Long beginSesstime,
        @WebParam(name = "end_sesstime", targetNamespace = "http://tempuri.org/")
        Long endSesstime,
        @WebParam(name = "pattern", targetNamespace = "http://tempuri.org/")
        String pattern);

    *//**
     * 
     * @param handle
     * @param strSessionNo
     * @param nRequest
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfABFuncDetailExtRecEXW
     *//*
    @WebMethod(operationName = "CATLOGDB_QueryNextExW", action = "http://tempuri.org/IABFuncService/CATLOGDB_QueryNextExW")
    @WebResult(name = "CATLOGDB_QueryNextExWResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "CATLOGDB_QueryNextExW", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.CATLOGDBQueryNextExW")
    @ResponseWrapper(localName = "CATLOGDB_QueryNextExWResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.CATLOGDBQueryNextExWResponse")
    public ArrayOfABFuncDetailExtRecEXW catlogdbQueryNextExW(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "handle", targetNamespace = "http://tempuri.org/")
        Long handle,
        @WebParam(name = "nRequest", targetNamespace = "http://tempuri.org/")
        Long nRequest);

    *//**
     * 
     * @param handle
     * @param strSessionNo
     *//*
    @WebMethod(operationName = "CATLOGDB_QueryCloseW", action = "http://tempuri.org/IABFuncService/CATLOGDB_QueryCloseW")
    @RequestWrapper(localName = "CATLOGDB_QueryCloseW", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.CATLOGDBQueryCloseW")
    @ResponseWrapper(localName = "CATLOGDB_QueryCloseWResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.CATLOGDBQueryCloseWResponse")
    public void catlogdbQueryCloseW(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "handle", targetNamespace = "http://tempuri.org/")
        Long handle);

    *//**
     * 
     * @param sPattern
     * @param strSessionNo
     * @param bCaseSensitive
     * @param sHost
     * @param defStr
     * @param bFirst
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfunsignedInt
     *//*
    @WebMethod(operationName = "SQLFindFileAddrListW", action = "http://tempuri.org/IABFuncService/SQLFindFileAddrListW")
    @WebResult(name = "SQLFindFileAddrListWResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SQLFindFileAddrListW", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.SQLFindFileAddrListW")
    @ResponseWrapper(localName = "SQLFindFileAddrListWResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.SQLFindFileAddrListWResponse")
    public ArrayOfunsignedInt sqlFindFileAddrListW(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "sPattern", targetNamespace = "http://tempuri.org/")
        String sPattern,
        @WebParam(name = "sHost", targetNamespace = "http://tempuri.org/")
        String sHost,
        @WebParam(name = "bCaseSensitive", targetNamespace = "http://tempuri.org/")
        Boolean bCaseSensitive,
        @WebParam(name = "DefStr", targetNamespace = "http://tempuri.org/")
        String defStr,
        @WebParam(name = "bFirst", targetNamespace = "http://tempuri.org/")
        Boolean bFirst);

    *//**
     * 
     * @param nameID
     * @param bIncludeSubDir
     * @param strSessionNo
     * @param nRequest
     * @param bCaseSensitive
     * @param sDir
     * @param sComputerName
     * @param bFirst
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfABFuncDetailExtRecEXW
     *//*
    @WebMethod(operationName = "QueryFileListEXW", action = "http://tempuri.org/IABFuncService/QueryFileListEXW")
    @WebResult(name = "QueryFileListEXWResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "QueryFileListEXW", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.QueryFileListEXW")
    @ResponseWrapper(localName = "QueryFileListEXWResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.QueryFileListEXWResponse")
    public ArrayOfABFuncDetailExtRecEXW queryFileListEXW(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "sComputerName", targetNamespace = "http://tempuri.org/")
        String sComputerName,
        @WebParam(name = "sDir", targetNamespace = "http://tempuri.org/")
        String sDir,
        @WebParam(name = "bCaseSensitive", targetNamespace = "http://tempuri.org/")
        Boolean bCaseSensitive,
        @WebParam(name = "bIncludeSubDir", targetNamespace = "http://tempuri.org/")
        Boolean bIncludeSubDir,
        @WebParam(name = "NameID", targetNamespace = "http://tempuri.org/")
        Long nameID,
        @WebParam(name = "bFirst", targetNamespace = "http://tempuri.org/")
        Boolean bFirst,
        @WebParam(name = "nRequest", targetNamespace = "http://tempuri.org/")
        Long nRequest);

    *//**
     * 
     * @param beginSesstime
     * @param sPattern
     * @param strSessionNo
     * @param ulFlags
     * @param sPath
     * @param sHost
     * @param endSesstime
     * @param bFirst
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfABFuncMsgRecW
     *//*
    @WebMethod(operationName = "MsgFindFileExW", action = "http://tempuri.org/IABFuncService/MsgFindFileExW")
    @WebResult(name = "MsgFindFileExWResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "MsgFindFileExW", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.MsgFindFileExW")
    @ResponseWrapper(localName = "MsgFindFileExWResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.MsgFindFileExWResponse")
    public ArrayOfABFuncMsgRecW msgFindFileExW(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "ulFlags", targetNamespace = "http://tempuri.org/")
        Integer ulFlags,
        @WebParam(name = "sHost", targetNamespace = "http://tempuri.org/")
        String sHost,
        @WebParam(name = "sPath", targetNamespace = "http://tempuri.org/")
        String sPath,
        @WebParam(name = "sPattern", targetNamespace = "http://tempuri.org/")
        String sPattern,
        @WebParam(name = "begin_sesstime", targetNamespace = "http://tempuri.org/")
        Long beginSesstime,
        @WebParam(name = "end_sesstime", targetNamespace = "http://tempuri.org/")
        Long endSesstime,
        @WebParam(name = "bFirst", targetNamespace = "http://tempuri.org/")
        Boolean bFirst);

    *//**
     * 
     * @param sPattern
     * @param strSessionNo
     * @param ulFlags
     * @param sPath
     * @param sHost
     * @param bFirst
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfABFuncMsgRecW
     *//*
    @WebMethod(operationName = "MsgFindFileW", action = "http://tempuri.org/IABFuncService/MsgFindFileW")
    @WebResult(name = "MsgFindFileWResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "MsgFindFileW", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.MsgFindFileW")
    @ResponseWrapper(localName = "MsgFindFileWResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.MsgFindFileWResponse")
    public ArrayOfABFuncMsgRecW msgFindFileW(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "ulFlags", targetNamespace = "http://tempuri.org/")
        Integer ulFlags,
        @WebParam(name = "sHost", targetNamespace = "http://tempuri.org/")
        String sHost,
        @WebParam(name = "sPath", targetNamespace = "http://tempuri.org/")
        String sPath,
        @WebParam(name = "sPattern", targetNamespace = "http://tempuri.org/")
        String sPattern,
        @WebParam(name = "bFirst", targetNamespace = "http://tempuri.org/")
        Boolean bFirst);

    *//**
     * 
     * @param sUser
     * @param strSessionNo
     * @param sHost
     * @param sPwd
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfstring
     *//*
    @WebMethod(operationName = "GetUsernameAndPassword", action = "http://tempuri.org/IABFuncService/GetUsernameAndPassword")
    @WebResult(name = "GetUsernameAndPasswordResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetUsernameAndPassword", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetUsernameAndPassword")
    @ResponseWrapper(localName = "GetUsernameAndPasswordResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.arcserve.GetUsernameAndPasswordResponse")
    public ArrayOfstring getUsernameAndPassword(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "sUser", targetNamespace = "http://tempuri.org/")
        String sUser,
        @WebParam(name = "sPwd", targetNamespace = "http://tempuri.org/")
        String sPwd,
        @WebParam(name = "sHost", targetNamespace = "http://tempuri.org/")
        String sHost);*/

}
