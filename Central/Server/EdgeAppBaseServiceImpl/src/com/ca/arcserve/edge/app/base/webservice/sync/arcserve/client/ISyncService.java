
package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "ISyncService", targetNamespace = "http://tempuri.org/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ISyncService {


    /**
     * 
     * @param strSessionNo
     * @param utcOffset
     * @param fullDumpDataBaseResult
     */
    @WebMethod(operationName = "FullDumpDataBase", action = "http://tempuri.org/ISyncService/FullDumpDataBase")
    @RequestWrapper(localName = "FullDumpDataBase", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.FullDumpDataBase")
    @ResponseWrapper(localName = "FullDumpDataBaseResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.FullDumpDataBaseResponse")
    public void fullDumpDataBase(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "UTCOffset", targetNamespace = "http://tempuri.org/", mode = WebParam.Mode.INOUT)
        Holder<Integer> utcOffset,
        @WebParam(name = "FullDumpDataBaseResult", targetNamespace = "http://tempuri.org/", mode = WebParam.Mode.OUT)
        Holder<Integer> fullDumpDataBaseResult);

    /**
     * 
     * @param syncFileInfo
     * @param strSessionNo
     * @param transferDataResult
     */
    @WebMethod(operationName = "TransferData", action = "http://tempuri.org/ISyncService/TransferData")
    @RequestWrapper(localName = "TransferData", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.TransferData")
    @ResponseWrapper(localName = "TransferDataResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.TransferDataResponse")
    public void transferData(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "syncFileInfo", targetNamespace = "http://tempuri.org/", mode = WebParam.Mode.INOUT)
        Holder<SyncFileType> syncFileInfo,
        @WebParam(name = "TransferDataResult", targetNamespace = "http://tempuri.org/", mode = WebParam.Mode.OUT)
        Holder<byte[]> transferDataResult);

    /**
     * 
     * @param strSessionNo
     * @param fileName
     */
    @WebMethod(operationName = "SyncFileEnd", action = "http://tempuri.org/ISyncService/SyncFileEnd")
    @RequestWrapper(localName = "SyncFileEnd", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncFileEnd")
    @ResponseWrapper(localName = "SyncFileEndResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncFileEndResponse")
    public void syncFileEnd(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "fileName", targetNamespace = "http://tempuri.org/")
        String fileName);

    /**
     * 
     * @param strSessionNo
     * @param syncInfo
     * @param incrementalSyncDataTransferResult
     */
    @WebMethod(operationName = "IncrementalSyncDataTransfer", action = "http://tempuri.org/ISyncService/IncrementalSyncDataTransfer")
    @RequestWrapper(localName = "IncrementalSyncDataTransfer", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.IncrementalSyncDataTransfer")
    @ResponseWrapper(localName = "IncrementalSyncDataTransferResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.IncrementalSyncDataTransferResponse")
    public void incrementalSyncDataTransfer(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "syncInfo", targetNamespace = "http://tempuri.org/", mode = WebParam.Mode.INOUT)
        Holder<SyncTranInfo> syncInfo,
        @WebParam(name = "IncrementalSyncDataTransferResult", targetNamespace = "http://tempuri.org/", mode = WebParam.Mode.OUT)
        Holder<byte[]> incrementalSyncDataTransferResult);

    /**
     * 
     * @param branchServeName
     * @param strSessionNo
     * @return
     *     returns java.lang.Boolean
     */
    @WebMethod(operationName = "UnRegisterBranchServer", action = "http://tempuri.org/ISyncService/UnRegisterBranchServer")
    @WebResult(name = "UnRegisterBranchServerResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "UnRegisterBranchServer", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.UnRegisterBranchServer")
    @ResponseWrapper(localName = "UnRegisterBranchServerResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.UnRegisterBranchServerResponse")
    public Boolean unRegisterBranchServer(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "branchServeName", targetNamespace = "http://tempuri.org/")
        String branchServeName);

    /**
     * 
     * @param strSessionNo
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.ArrayOfBranchSiteInfo
     */
    @WebMethod(operationName = "EnumBranchServer", action = "http://tempuri.org/ISyncService/EnumBranchServer")
    @WebResult(name = "EnumBranchServerResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "EnumBranchServer", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.EnumBranchServer")
    @ResponseWrapper(localName = "EnumBranchServerResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.EnumBranchServerResponse")
    public ArrayOfBranchSiteInfo enumBranchServer(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo);

    /**
     * 
     * @param strSessionNo
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.ArrayOfstring
     */
    @WebMethod(operationName = "GetSyncFileList", action = "http://tempuri.org/ISyncService/GetSyncFileList")
    @WebResult(name = "GetSyncFileListResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetSyncFileList", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.GetSyncFileList")
    @ResponseWrapper(localName = "GetSyncFileListResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.GetSyncFileListResponse")
    public ArrayOfstring getSyncFileList(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo);

    /**
     * 
     * @param strSessionNo
     * @param syncInfo
     * @param lastID
     * @return
     *     returns java.lang.Integer
     */
    @WebMethod(operationName = "SyncIncrementalEnd", action = "http://tempuri.org/ISyncService/SyncIncrementalEnd")
    @WebResult(name = "SyncIncrementalEndResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SyncIncrementalEnd", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncIncrementalEnd")
    @ResponseWrapper(localName = "SyncIncrementalEndResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncIncrementalEndResponse")
    public Integer syncIncrementalEnd(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "syncInfo", targetNamespace = "http://tempuri.org/")
        SyncTranInfo syncInfo,
        @WebParam(name = "lastID", targetNamespace = "http://tempuri.org/")
        Long lastID);

    /**
     * 
     * @param edgeHostid
     * @param strSessionNo
     * @param syncGDBDatabaseResult
     * @param utcOffset
     * @param branchid
     */
    @WebMethod(operationName = "SyncGDBDatabase", action = "http://tempuri.org/ISyncService/SyncGDBDatabase")
    @RequestWrapper(localName = "SyncGDBDatabase", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncGDBDatabase")
    @ResponseWrapper(localName = "SyncGDBDatabaseResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncGDBDatabaseResponse")
    public void syncGDBDatabase(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "edgeHostid", targetNamespace = "http://tempuri.org/")
        Integer edgeHostid,
        @WebParam(name = "branchid", targetNamespace = "http://tempuri.org/")
        Integer branchid,
        @WebParam(name = "UTCOffset", targetNamespace = "http://tempuri.org/", mode = WebParam.Mode.INOUT)
        Holder<Integer> utcOffset,
        @WebParam(name = "SyncGDBDatabaseResult", targetNamespace = "http://tempuri.org/", mode = WebParam.Mode.OUT)
        Holder<Integer> syncGDBDatabaseResult);

    /**
     * 
     * @param edgeHostid
     * @param strSessionNo
     * @param branchid
     * @return
     *     returns com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.ArrayOfstring
     */
    @WebMethod(operationName = "SyncFileList", action = "http://tempuri.org/ISyncService/SyncFileList")
    @WebResult(name = "SyncFileListResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SyncFileList", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncFileList")
    @ResponseWrapper(localName = "SyncFileListResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncFileListResponse")
    public ArrayOfstring syncFileList(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "edgeHostid", targetNamespace = "http://tempuri.org/")
        Integer edgeHostid,
        @WebParam(name = "branchid", targetNamespace = "http://tempuri.org/")
        Integer branchid);

    /**
     * 
     * @param syncFileInfo
     * @param transferDataWithBase64Result
     * @param strSessionNo
     */
    @WebMethod(operationName = "TransferDataWithBase64", action = "http://tempuri.org/ISyncService/TransferDataWithBase64")
    @RequestWrapper(localName = "TransferDataWithBase64", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.TransferDataWithBase64")
    @ResponseWrapper(localName = "TransferDataWithBase64Response", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.TransferDataWithBase64Response")
    public void transferDataWithBase64(
        @WebParam(name = "strSessionNo", targetNamespace = "http://tempuri.org/")
        String strSessionNo,
        @WebParam(name = "syncFileInfo", targetNamespace = "http://tempuri.org/", mode = WebParam.Mode.INOUT)
        Holder<SyncFileType> syncFileInfo,
        @WebParam(name = "TransferDataWithBase64Result", targetNamespace = "http://tempuri.org/", mode = WebParam.Mode.OUT)
        Holder<String> transferDataWithBase64Result);

    /**
     * 
     * @param strUser
     * @param strPassword
     * @param mode
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "ConnectARCserve", action = "http://tempuri.org/ISyncService/ConnectARCserve")
    @WebResult(name = "ConnectARCserveResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "ConnectARCserve", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.ConnectARCserve")
    @ResponseWrapper(localName = "ConnectARCserveResponse", targetNamespace = "http://tempuri.org/", className = "com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.ConnectARCserveResponse")
    public String connectARCserve(
        @WebParam(name = "strUser", targetNamespace = "http://tempuri.org/")
        String strUser,
        @WebParam(name = "strPassword", targetNamespace = "http://tempuri.org/")
        String strPassword,
        @WebParam(name = "mode", targetNamespace = "http://tempuri.org/")
        ABFuncAuthMode mode);

}