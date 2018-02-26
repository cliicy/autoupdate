
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncManageStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.ca.arcserve.edge.app.base.webservice.arcserve package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ABFuncSessRec_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_SessRec");
    private final static QName _AnyURI_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyURI");
    private final static QName _DateTime_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "dateTime");
    private final static QName _Char_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "char");
    private final static QName _QName_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "QName");
    private final static QName _UnsignedShort_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedShort");
    private final static QName _Float_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "float");
    private final static QName _Long_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "long");
    private final static QName _ABFuncStringAndFlag_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_StringAndFlag");
    private final static QName _ABFuncTapeDataEx2W_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_TapeDataEx2W");
    private final static QName _Short_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "short");
    private final static QName _ArrayOfABFuncMsgRecW_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ArrayOfABFunc_MsgRecW");
    private final static QName _Base64Binary_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "base64Binary");
    private final static QName _Byte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "byte");
    private final static QName _ArrayOfABFuncTapePropertyRec_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ArrayOfABFunc_TapePropertyRec");
    private final static QName _ABFuncTapeRecW_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_TapeRecW");
    private final static QName _ABFuncDetailExtRecEXW_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_DetailExtRecEXW");
    private final static QName _Boolean_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "boolean");
    private final static QName _ABFuncVersionDataExW_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_VersionDataExW");
    private final static QName _ArrayOfABFuncDetailExtRecEXW_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ArrayOfABFunc_DetailExtRecEXW");
    private final static QName _ABFuncTAPEDATAEX_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_TAPEDATAEX");
    private final static QName _ABFuncDetailRec_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_DetailRec");
    private final static QName _UnsignedByte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedByte");
    private final static QName _AnyType_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyType");
    private final static QName _UnsignedInt_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedInt");
    private final static QName _ABFuncTapePropertyRec_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_TapePropertyRec");
    private final static QName _Int_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "int");
    private final static QName _ArrayOfABFuncTAPEDATAEX_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ArrayOfABFunc_TAPEDATAEX");
    private final static QName _Decimal_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "decimal");
    private final static QName _ABFuncAuthMode_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_AuthMode");
    private final static QName _Double_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "double");
    private final static QName _ArrayOfstring_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "ArrayOfstring");
    private final static QName _ArrayOfunsignedInt_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "ArrayOfunsignedInt");
    private final static QName _Guid_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "guid");
    private final static QName _Duration_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "duration");
    private final static QName _ABFuncSessionAndTapeRec_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_SessionAndTapeRec");
    private final static QName _String_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "string");
    private final static QName _ABFuncMsgRecW_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_MsgRecW");
    private final static QName _ABFuncServerType_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_ServerType");
    private final static QName _UnsignedLong_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedLong");
    private final static QName _ABFuncSessFlagEx_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_SessFlagEx");
    private final static QName _ABFuncManageStatus_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ABFunc_ManageStatus");
    private final static QName _GetTapeRecordWResponseGetTapeRecordWResult_QNAME = new QName("http://tempuri.org/", "GetTapeRecordWResult");
    private final static QName _GetManagedEdgeServerStrSessionNo_QNAME = new QName("http://tempuri.org/", "strSessionNo");
    private final static QName _SubmitABJobStrJobScript_QNAME = new QName("http://tempuri.org/", "strJobScript");
    private final static QName _GetStringFromSessionWResponseGetStringFromSessionWResult_QNAME = new QName("http://tempuri.org/", "GetStringFromSessionWResult");
    private final static QName _MarkArcserveManageStatusResponseMarkArcserveManageStatusResult_QNAME = new QName("http://tempuri.org/", "MarkArcserveManageStatusResult");
    private final static QName _GetSessionListEx2ResponseGetSessionListEx2Result_QNAME = new QName("http://tempuri.org/", "GetSessionListEx2Result");
    private final static QName _ABFuncVersionDataExWReserved_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "Reserved");
    private final static QName _ABFuncVersionDataExWLocation_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "Location");
    private final static QName _ABFuncVersionDataExWTapeName_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "TapeName");
    private final static QName _ABFuncVersionDataExWReserved2_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "Reserved2");
    private final static QName _GetDataListExResponseGetDataListExResult_QNAME = new QName("http://tempuri.org/", "GetDataListExResult");
    private final static QName _GetDataListExTapeDataEx_QNAME = new QName("http://tempuri.org/", "tapeDataEx");
    private final static QName _GetUsernameAndPasswordResponseGetUsernameAndPasswordResult_QNAME = new QName("http://tempuri.org/", "GetUsernameAndPasswordResult");
    private final static QName _QueryFileListEXWResponseQueryFileListEXWResult_QNAME = new QName("http://tempuri.org/", "QueryFileListEXWResult");
    private final static QName _MsgFindFileExWSPath_QNAME = new QName("http://tempuri.org/", "sPath");
    private final static QName _MsgFindFileExWSHost_QNAME = new QName("http://tempuri.org/", "sHost");
    private final static QName _MsgFindFileExWSPattern_QNAME = new QName("http://tempuri.org/", "sPattern");
    private final static QName _GetManagedEdgeServerResponseGetManagedEdgeServerResult_QNAME = new QName("http://tempuri.org/", "GetManagedEdgeServerResult");
    private final static QName _ConnectARCserveResponseConnectARCserveResult_QNAME = new QName("http://tempuri.org/", "ConnectARCserveResult");
    private final static QName _GetArcserveVersionInfoResponseGetArcserveVersionInfoResult_QNAME = new QName("http://tempuri.org/", "GetArcserveVersionInfoResult");
    private final static QName _GetStreamNameFromSessionWResponseGetStreamNameFromSessionWResult_QNAME = new QName("http://tempuri.org/", "GetStreamNameFromSessionWResult");
    private final static QName _MsgFindFileExWResponseMsgFindFileExWResult_QNAME = new QName("http://tempuri.org/", "MsgFindFileExWResult");
    private final static QName _GetSessFlagExResponseGetSessFlagExResult_QNAME = new QName("http://tempuri.org/", "GetSessFlagExResult");
    private final static QName _GetNameFromSessionWResponseGetNameFromSessionWResult_QNAME = new QName("http://tempuri.org/", "GetNameFromSessionWResult");
    private final static QName _SQLFindFileAddrListWResponseSQLFindFileAddrListWResult_QNAME = new QName("http://tempuri.org/", "SQLFindFileAddrListWResult");
    private final static QName _ConnectARCserveStrUser_QNAME = new QName("http://tempuri.org/", "strUser");
    private final static QName _ConnectARCserveStrPassword_QNAME = new QName("http://tempuri.org/", "strPassword");
    private final static QName _ABFuncTapeDataEx2WSerialNum_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "SerialNum");
    private final static QName _ABFuncTapeRecWPoolname_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "poolname");
    private final static QName _ABFuncTapeRecWSerialnum_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "serialnum");
    private final static QName _ABFuncTapeRecWTapename_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "tapename");
    private final static QName _CATLOGDBQueryNextExWResponseCATLOGDBQueryNextExWResult_QNAME = new QName("http://tempuri.org/", "CATLOGDB_QueryNextExWResult");
    private final static QName _GetArcserveManageStatusStrEdgeServerName_QNAME = new QName("http://tempuri.org/", "strEdgeServerName");
    private final static QName _SQLFindFileAddrListWDefStr_QNAME = new QName("http://tempuri.org/", "DefStr");
    private final static QName _QueryFileListEXWSComputerName_QNAME = new QName("http://tempuri.org/", "sComputerName");
    private final static QName _QueryFileListEXWSDir_QNAME = new QName("http://tempuri.org/", "sDir");
    private final static QName _GetSessionAndTapeRecBySesIDWResponseGetSessionAndTapeRecBySesIDWResult_QNAME = new QName("http://tempuri.org/", "GetSessionAndTapeRecBySesIDWResult");
    private final static QName _GetTapeRecordWStrTapeName_QNAME = new QName("http://tempuri.org/", "strTapeName");
    private final static QName _ABFuncSessionAndTapeRecSessRec_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "sessRec");
    private final static QName _ABFuncSessionAndTapeRecTapeRecW_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "tapeRecW");
    private final static QName _ABFuncMsgRecWObjname_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "objname");
    private final static QName _ABFuncMsgRecWObjinfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "objinfo");
    private final static QName _MarkArcserveManageStatusStrEdgeServerUserName_QNAME = new QName("http://tempuri.org/", "strEdgeServerUserName");
    private final static QName _MarkArcserveManageStatusStrEdgeServerPassword_QNAME = new QName("http://tempuri.org/", "strEdgeServerPassword");
    private final static QName _MarkArcserveManageStatusStrEdgeServerDomain_QNAME = new QName("http://tempuri.org/", "strEdgeServerDomain");
    private final static QName _MarkArcserveManageStatusStrEdgeServerId_QNAME = new QName("http://tempuri.org/", "strEdgeServerId");
    private final static QName _MarkArcserveManageStatusStrEdgeServiceWsdl_QNAME = new QName("http://tempuri.org/", "strEdgeServiceWsdl");
    private final static QName _ABFuncTapePropertyRecReserved_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "reserved");
    private final static QName _ABFuncTapePropertyRecTapeData_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "tapeData");
    private final static QName _MsgFindFileWResponseMsgFindFileWResult_QNAME = new QName("http://tempuri.org/", "MsgFindFileWResult");
    private final static QName _ABFuncDetailExtRecEXWLongName_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "LongName");
    private final static QName _ABFuncDetailExtRecEXWDetailRec_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "DetailRec");
    private final static QName _ABFuncDetailExtRecEXWPath_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "Path");
    private final static QName _ABFuncDetailExtRecEXWShortName_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "ShortName");
    private final static QName _CATLOGDBQueryFirstExWPattern_QNAME = new QName("http://tempuri.org/", "pattern");
    private final static QName _GetTapeRecListForRestoreResponseGetTapeRecListForRestoreResult_QNAME = new QName("http://tempuri.org/", "GetTapeRecListForRestoreResult");
    private final static QName _GetGDBServerResponseGetGDBServerResult_QNAME = new QName("http://tempuri.org/", "GetGDBServerResult");
    private final static QName _TestWebServiceResponseTestWebServiceResult_QNAME = new QName("http://tempuri.org/", "testWebServiceResult");
    private final static QName _GetUsernameAndPasswordSUser_QNAME = new QName("http://tempuri.org/", "sUser");
    private final static QName _GetUsernameAndPasswordSPwd_QNAME = new QName("http://tempuri.org/", "sPwd");
    private final static QName _GetTapeSessionInfoExWResponseGetTapeSessionInfoExWResult_QNAME = new QName("http://tempuri.org/", "GetTapeSessionInfoExWResult");
    private final static QName _ABFuncStringAndFlagStrValue_QNAME = new QName("http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", "strValue");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.ca.arcserve.edge.app.base.webservice.arcserve
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetTapeRecordWResponse }
     * 
     */
    public GetTapeRecordWResponse createGetTapeRecordWResponse() {
        return new GetTapeRecordWResponse();
    }

    /**
     * Create an instance of {@link GetManagedEdgeServer }
     * 
     */
    public GetManagedEdgeServer createGetManagedEdgeServer() {
        return new GetManagedEdgeServer();
    }

    /**
     * Create an instance of {@link SubmitABJob }
     * 
     */
    public SubmitABJob createSubmitABJob() {
        return new SubmitABJob();
    }

    /**
     * Create an instance of {@link GetTapeRecListForRestore }
     * 
     */
    public GetTapeRecListForRestore createGetTapeRecListForRestore() {
        return new GetTapeRecListForRestore();
    }

    /**
     * Create an instance of {@link GetStringFromSessionWResponse }
     * 
     */
    public GetStringFromSessionWResponse createGetStringFromSessionWResponse() {
        return new GetStringFromSessionWResponse();
    }

    /**
     * Create an instance of {@link ArrayOfABFuncDetailExtRecEXW }
     * 
     */
    public ArrayOfABFuncDetailExtRecEXW createArrayOfABFuncDetailExtRecEXW() {
        return new ArrayOfABFuncDetailExtRecEXW();
    }

    /**
     * Create an instance of {@link MarkArcserveManageStatusResponse }
     * 
     */
    public MarkArcserveManageStatusResponse createMarkArcserveManageStatusResponse() {
        return new MarkArcserveManageStatusResponse();
    }

    /**
     * Create an instance of {@link GetSessionListEx2Response }
     * 
     */
    public GetSessionListEx2Response createGetSessionListEx2Response() {
        return new GetSessionListEx2Response();
    }

    /**
     * Create an instance of {@link ABFuncVersionDataExW }
     * 
     */
    public ABFuncVersionDataExW createABFuncVersionDataExW() {
        return new ABFuncVersionDataExW();
    }

    /**
     * Create an instance of {@link GetDataListExResponse }
     * 
     */
    public GetDataListExResponse createGetDataListExResponse() {
        return new GetDataListExResponse();
    }

    /**
     * Create an instance of {@link GetDataListEx }
     * 
     */
    public GetDataListEx createGetDataListEx() {
        return new GetDataListEx();
    }

    /**
     * Create an instance of {@link GetUsernameAndPasswordResponse }
     * 
     */
    public GetUsernameAndPasswordResponse createGetUsernameAndPasswordResponse() {
        return new GetUsernameAndPasswordResponse();
    }

    /**
     * Create an instance of {@link ArrayOfABFuncMsgRecW }
     * 
     */
    public ArrayOfABFuncMsgRecW createArrayOfABFuncMsgRecW() {
        return new ArrayOfABFuncMsgRecW();
    }

    /**
     * Create an instance of {@link SubmitABJobResponse }
     * 
     */
    public SubmitABJobResponse createSubmitABJobResponse() {
        return new SubmitABJobResponse();
    }

    /**
     * Create an instance of {@link GetServerType }
     * 
     */
    public GetServerType createGetServerType() {
        return new GetServerType();
    }

    /**
     * Create an instance of {@link GetGDBServer }
     * 
     */
    public GetGDBServer createGetGDBServer() {
        return new GetGDBServer();
    }

    /**
     * Create an instance of {@link MsgFindFileExW }
     * 
     */
    public MsgFindFileExW createMsgFindFileExW() {
        return new MsgFindFileExW();
    }

    /**
     * Create an instance of {@link QueryFileListEXWResponse }
     * 
     */
    public QueryFileListEXWResponse createQueryFileListEXWResponse() {
        return new QueryFileListEXWResponse();
    }

    /**
     * Create an instance of {@link GetSessionAndTapeRecBySesIDW }
     * 
     */
    public GetSessionAndTapeRecBySesIDW createGetSessionAndTapeRecBySesIDW() {
        return new GetSessionAndTapeRecBySesIDW();
    }

    /**
     * Create an instance of {@link GetManagedEdgeServerResponse }
     * 
     */
    public GetManagedEdgeServerResponse createGetManagedEdgeServerResponse() {
        return new GetManagedEdgeServerResponse();
    }

    /**
     * Create an instance of {@link ArrayOfunsignedInt }
     * 
     */
    public ArrayOfunsignedInt createArrayOfunsignedInt() {
        return new ArrayOfunsignedInt();
    }

    /**
     * Create an instance of {@link ABFuncSessRec }
     * 
     */
    public ABFuncSessRec createABFuncSessRec() {
        return new ABFuncSessRec();
    }

    /**
     * Create an instance of {@link GetSessFlagEx }
     * 
     */
    public GetSessFlagEx createGetSessFlagEx() {
        return new GetSessFlagEx();
    }

    /**
     * Create an instance of {@link ArrayOfstring }
     * 
     */
    public ArrayOfstring createArrayOfstring() {
        return new ArrayOfstring();
    }

    /**
     * Create an instance of {@link CATLOGDBQueryFirstExWResponse }
     * 
     */
    public CATLOGDBQueryFirstExWResponse createCATLOGDBQueryFirstExWResponse() {
        return new CATLOGDBQueryFirstExWResponse();
    }

    /**
     * Create an instance of {@link ConnectARCserveResponse }
     * 
     */
    public ConnectARCserveResponse createConnectARCserveResponse() {
        return new ConnectARCserveResponse();
    }

    /**
     * Create an instance of {@link CATLOGDBQueryCloseWResponse }
     * 
     */
    public CATLOGDBQueryCloseWResponse createCATLOGDBQueryCloseWResponse() {
        return new CATLOGDBQueryCloseWResponse();
    }

    /**
     * Create an instance of {@link GetArcserveVersionInfo }
     * 
     */
    public GetArcserveVersionInfo createGetArcserveVersionInfo() {
        return new GetArcserveVersionInfo();
    }

    /**
     * Create an instance of {@link GetArcserveVersionInfoResponse }
     * 
     */
    public GetArcserveVersionInfoResponse createGetArcserveVersionInfoResponse() {
        return new GetArcserveVersionInfoResponse();
    }

    /**
     * Create an instance of {@link GetStreamNameFromSessionWResponse }
     * 
     */
    public GetStreamNameFromSessionWResponse createGetStreamNameFromSessionWResponse() {
        return new GetStreamNameFromSessionWResponse();
    }

    /**
     * Create an instance of {@link MsgFindFileExWResponse }
     * 
     */
    public MsgFindFileExWResponse createMsgFindFileExWResponse() {
        return new MsgFindFileExWResponse();
    }

    /**
     * Create an instance of {@link GetNameFromSessionWResponse }
     * 
     */
    public GetNameFromSessionWResponse createGetNameFromSessionWResponse() {
        return new GetNameFromSessionWResponse();
    }

    /**
     * Create an instance of {@link GetSessFlagExResponse }
     * 
     */
    public GetSessFlagExResponse createGetSessFlagExResponse() {
        return new GetSessFlagExResponse();
    }

    /**
     * Create an instance of {@link SQLFindFileAddrListWResponse }
     * 
     */
    public SQLFindFileAddrListWResponse createSQLFindFileAddrListWResponse() {
        return new SQLFindFileAddrListWResponse();
    }

    /**
     * Create an instance of {@link ConnectARCserve }
     * 
     */
    public ConnectARCserve createConnectARCserve() {
        return new ConnectARCserve();
    }

    /**
     * Create an instance of {@link GetNameFromSessionW }
     * 
     */
    public GetNameFromSessionW createGetNameFromSessionW() {
        return new GetNameFromSessionW();
    }

    /**
     * Create an instance of {@link ABFuncSessFlagEx }
     * 
     */
    public ABFuncSessFlagEx createABFuncSessFlagEx() {
        return new ABFuncSessFlagEx();
    }

    /**
     * Create an instance of {@link CATLOGDBQueryNextExW }
     * 
     */
    public CATLOGDBQueryNextExW createCATLOGDBQueryNextExW() {
        return new CATLOGDBQueryNextExW();
    }

    /**
     * Create an instance of {@link ABFuncTapeDataEx2W }
     * 
     */
    public ABFuncTapeDataEx2W createABFuncTapeDataEx2W() {
        return new ABFuncTapeDataEx2W();
    }

    /**
     * Create an instance of {@link ABFuncTapeRecW }
     * 
     */
    public ABFuncTapeRecW createABFuncTapeRecW() {
        return new ABFuncTapeRecW();
    }

    /**
     * Create an instance of {@link ABFuncTAPEDATAEX }
     * 
     */
    public ABFuncTAPEDATAEX createABFuncTAPEDATAEX() {
        return new ABFuncTAPEDATAEX();
    }

    /**
     * Create an instance of {@link TestWebService }
     * 
     */
    public TestWebService createTestWebService() {
        return new TestWebService();
    }

    /**
     * Create an instance of {@link CATLOGDBQueryNextExWResponse }
     * 
     */
    public CATLOGDBQueryNextExWResponse createCATLOGDBQueryNextExWResponse() {
        return new CATLOGDBQueryNextExWResponse();
    }

    /**
     * Create an instance of {@link GetArcserveManageStatus }
     * 
     */
    public GetArcserveManageStatus createGetArcserveManageStatus() {
        return new GetArcserveManageStatus();
    }

    /**
     * Create an instance of {@link SQLFindFileAddrListW }
     * 
     */
    public SQLFindFileAddrListW createSQLFindFileAddrListW() {
        return new SQLFindFileAddrListW();
    }

    /**
     * Create an instance of {@link MsgFindFileW }
     * 
     */
    public MsgFindFileW createMsgFindFileW() {
        return new MsgFindFileW();
    }

    /**
     * Create an instance of {@link QueryFileListEXW }
     * 
     */
    public QueryFileListEXW createQueryFileListEXW() {
        return new QueryFileListEXW();
    }

    /**
     * Create an instance of {@link ABFuncDetailRec }
     * 
     */
    public ABFuncDetailRec createABFuncDetailRec() {
        return new ABFuncDetailRec();
    }

    /**
     * Create an instance of {@link GetSessionAndTapeRecBySesIDWResponse }
     * 
     */
    public GetSessionAndTapeRecBySesIDWResponse createGetSessionAndTapeRecBySesIDWResponse() {
        return new GetSessionAndTapeRecBySesIDWResponse();
    }

    /**
     * Create an instance of {@link GetTapeRecordW }
     * 
     */
    public GetTapeRecordW createGetTapeRecordW() {
        return new GetTapeRecordW();
    }

    /**
     * Create an instance of {@link ABFuncMsgRecW }
     * 
     */
    public ABFuncMsgRecW createABFuncMsgRecW() {
        return new ABFuncMsgRecW();
    }

    /**
     * Create an instance of {@link ABFuncSessionAndTapeRec }
     * 
     */
    public ABFuncSessionAndTapeRec createABFuncSessionAndTapeRec() {
        return new ABFuncSessionAndTapeRec();
    }

    /**
     * Create an instance of {@link MarkArcserveManageStatus }
     * 
     */
    public MarkArcserveManageStatus createMarkArcserveManageStatus() {
        return new MarkArcserveManageStatus();
    }

    /**
     * Create an instance of {@link ABFuncTapePropertyRec }
     * 
     */
    public ABFuncTapePropertyRec createABFuncTapePropertyRec() {
        return new ABFuncTapePropertyRec();
    }

    /**
     * Create an instance of {@link ArrayOfABFuncTapePropertyRec }
     * 
     */
    public ArrayOfABFuncTapePropertyRec createArrayOfABFuncTapePropertyRec() {
        return new ArrayOfABFuncTapePropertyRec();
    }

    /**
     * Create an instance of {@link GetServerTypeResponse }
     * 
     */
    public GetServerTypeResponse createGetServerTypeResponse() {
        return new GetServerTypeResponse();
    }

    /**
     * Create an instance of {@link GetSessionListEx2 }
     * 
     */
    public GetSessionListEx2 createGetSessionListEx2() {
        return new GetSessionListEx2();
    }

    /**
     * Create an instance of {@link IsArcserveBranchResponse }
     * 
     */
    public IsArcserveBranchResponse createIsArcserveBranchResponse() {
        return new IsArcserveBranchResponse();
    }

    /**
     * Create an instance of {@link GetStringFromSessionW }
     * 
     */
    public GetStringFromSessionW createGetStringFromSessionW() {
        return new GetStringFromSessionW();
    }

    /**
     * Create an instance of {@link MsgFindFileWResponse }
     * 
     */
    public MsgFindFileWResponse createMsgFindFileWResponse() {
        return new MsgFindFileWResponse();
    }

    /**
     * Create an instance of {@link GetStreamNameFromSessionW }
     * 
     */
    public GetStreamNameFromSessionW createGetStreamNameFromSessionW() {
        return new GetStreamNameFromSessionW();
    }

    /**
     * Create an instance of {@link ArrayOfABFuncTAPEDATAEX }
     * 
     */
    public ArrayOfABFuncTAPEDATAEX createArrayOfABFuncTAPEDATAEX() {
        return new ArrayOfABFuncTAPEDATAEX();
    }

    /**
     * Create an instance of {@link ABFuncDetailExtRecEXW }
     * 
     */
    public ABFuncDetailExtRecEXW createABFuncDetailExtRecEXW() {
        return new ABFuncDetailExtRecEXW();
    }

    /**
     * Create an instance of {@link CATLOGDBQueryCloseW }
     * 
     */
    public CATLOGDBQueryCloseW createCATLOGDBQueryCloseW() {
        return new CATLOGDBQueryCloseW();
    }

    /**
     * Create an instance of {@link CATLOGDBQueryFirstExW }
     * 
     */
    public CATLOGDBQueryFirstExW createCATLOGDBQueryFirstExW() {
        return new CATLOGDBQueryFirstExW();
    }

    /**
     * Create an instance of {@link GetTapeSessionInfoExW }
     * 
     */
    public GetTapeSessionInfoExW createGetTapeSessionInfoExW() {
        return new GetTapeSessionInfoExW();
    }

    /**
     * Create an instance of {@link GetTapeRecListForRestoreResponse }
     * 
     */
    public GetTapeRecListForRestoreResponse createGetTapeRecListForRestoreResponse() {
        return new GetTapeRecListForRestoreResponse();
    }

    /**
     * Create an instance of {@link GetGDBServerResponse }
     * 
     */
    public GetGDBServerResponse createGetGDBServerResponse() {
        return new GetGDBServerResponse();
    }

    /**
     * Create an instance of {@link TestWebServiceResponse }
     * 
     */
    public TestWebServiceResponse createTestWebServiceResponse() {
        return new TestWebServiceResponse();
    }

    /**
     * Create an instance of {@link GetUsernameAndPassword }
     * 
     */
    public GetUsernameAndPassword createGetUsernameAndPassword() {
        return new GetUsernameAndPassword();
    }

    /**
     * Create an instance of {@link IsArcserveBranch }
     * 
     */
    public IsArcserveBranch createIsArcserveBranch() {
        return new IsArcserveBranch();
    }

    /**
     * Create an instance of {@link GetArcserveManageStatusResponse }
     * 
     */
    public GetArcserveManageStatusResponse createGetArcserveManageStatusResponse() {
        return new GetArcserveManageStatusResponse();
    }

    /**
     * Create an instance of {@link GetTapeSessionInfoExWResponse }
     * 
     */
    public GetTapeSessionInfoExWResponse createGetTapeSessionInfoExWResponse() {
        return new GetTapeSessionInfoExWResponse();
    }

    /**
     * Create an instance of {@link ABFuncStringAndFlag }
     * 
     */
    public ABFuncStringAndFlag createABFuncStringAndFlag() {
        return new ABFuncStringAndFlag();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncSessRec }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_SessRec")
    public JAXBElement<ABFuncSessRec> createABFuncSessRec(ABFuncSessRec value) {
        return new JAXBElement<ABFuncSessRec>(_ABFuncSessRec_QNAME, ABFuncSessRec.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyURI")
    public JAXBElement<String> createAnyURI(String value) {
        return new JAXBElement<String>(_AnyURI_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "dateTime")
    public JAXBElement<XMLGregorianCalendar> createDateTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DateTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "char")
    public JAXBElement<Integer> createChar(Integer value) {
        return new JAXBElement<Integer>(_Char_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "QName")
    public JAXBElement<QName> createQName(QName value) {
        return new JAXBElement<QName>(_QName_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedShort")
    public JAXBElement<Integer> createUnsignedShort(Integer value) {
        return new JAXBElement<Integer>(_UnsignedShort_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Float }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "float")
    public JAXBElement<Float> createFloat(Float value) {
        return new JAXBElement<Float>(_Float_QNAME, Float.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "long")
    public JAXBElement<Long> createLong(Long value) {
        return new JAXBElement<Long>(_Long_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncStringAndFlag }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_StringAndFlag")
    public JAXBElement<ABFuncStringAndFlag> createABFuncStringAndFlag(ABFuncStringAndFlag value) {
        return new JAXBElement<ABFuncStringAndFlag>(_ABFuncStringAndFlag_QNAME, ABFuncStringAndFlag.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncTapeDataEx2W }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_TapeDataEx2W")
    public JAXBElement<ABFuncTapeDataEx2W> createABFuncTapeDataEx2W(ABFuncTapeDataEx2W value) {
        return new JAXBElement<ABFuncTapeDataEx2W>(_ABFuncTapeDataEx2W_QNAME, ABFuncTapeDataEx2W.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "short")
    public JAXBElement<Short> createShort(Short value) {
        return new JAXBElement<Short>(_Short_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfABFuncMsgRecW }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ArrayOfABFunc_MsgRecW")
    public JAXBElement<ArrayOfABFuncMsgRecW> createArrayOfABFuncMsgRecW(ArrayOfABFuncMsgRecW value) {
        return new JAXBElement<ArrayOfABFuncMsgRecW>(_ArrayOfABFuncMsgRecW_QNAME, ArrayOfABFuncMsgRecW.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "base64Binary")
    public JAXBElement<byte[]> createBase64Binary(byte[] value) {
        return new JAXBElement<byte[]>(_Base64Binary_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Byte }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "byte")
    public JAXBElement<Byte> createByte(Byte value) {
        return new JAXBElement<Byte>(_Byte_QNAME, Byte.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfABFuncTapePropertyRec }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ArrayOfABFunc_TapePropertyRec")
    public JAXBElement<ArrayOfABFuncTapePropertyRec> createArrayOfABFuncTapePropertyRec(ArrayOfABFuncTapePropertyRec value) {
        return new JAXBElement<ArrayOfABFuncTapePropertyRec>(_ArrayOfABFuncTapePropertyRec_QNAME, ArrayOfABFuncTapePropertyRec.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncTapeRecW }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_TapeRecW")
    public JAXBElement<ABFuncTapeRecW> createABFuncTapeRecW(ABFuncTapeRecW value) {
        return new JAXBElement<ABFuncTapeRecW>(_ABFuncTapeRecW_QNAME, ABFuncTapeRecW.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncDetailExtRecEXW }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_DetailExtRecEXW")
    public JAXBElement<ABFuncDetailExtRecEXW> createABFuncDetailExtRecEXW(ABFuncDetailExtRecEXW value) {
        return new JAXBElement<ABFuncDetailExtRecEXW>(_ABFuncDetailExtRecEXW_QNAME, ABFuncDetailExtRecEXW.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<Boolean>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncVersionDataExW }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_VersionDataExW")
    public JAXBElement<ABFuncVersionDataExW> createABFuncVersionDataExW(ABFuncVersionDataExW value) {
        return new JAXBElement<ABFuncVersionDataExW>(_ABFuncVersionDataExW_QNAME, ABFuncVersionDataExW.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfABFuncDetailExtRecEXW }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ArrayOfABFunc_DetailExtRecEXW")
    public JAXBElement<ArrayOfABFuncDetailExtRecEXW> createArrayOfABFuncDetailExtRecEXW(ArrayOfABFuncDetailExtRecEXW value) {
        return new JAXBElement<ArrayOfABFuncDetailExtRecEXW>(_ArrayOfABFuncDetailExtRecEXW_QNAME, ArrayOfABFuncDetailExtRecEXW.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncTAPEDATAEX }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_TAPEDATAEX")
    public JAXBElement<ABFuncTAPEDATAEX> createABFuncTAPEDATAEX(ABFuncTAPEDATAEX value) {
        return new JAXBElement<ABFuncTAPEDATAEX>(_ABFuncTAPEDATAEX_QNAME, ABFuncTAPEDATAEX.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncDetailRec }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_DetailRec")
    public JAXBElement<ABFuncDetailRec> createABFuncDetailRec(ABFuncDetailRec value) {
        return new JAXBElement<ABFuncDetailRec>(_ABFuncDetailRec_QNAME, ABFuncDetailRec.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedByte")
    public JAXBElement<Short> createUnsignedByte(Short value) {
        return new JAXBElement<Short>(_UnsignedByte_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyType")
    public JAXBElement<Object> createAnyType(Object value) {
        return new JAXBElement<Object>(_AnyType_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedInt")
    public JAXBElement<Long> createUnsignedInt(Long value) {
        return new JAXBElement<Long>(_UnsignedInt_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncTapePropertyRec }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_TapePropertyRec")
    public JAXBElement<ABFuncTapePropertyRec> createABFuncTapePropertyRec(ABFuncTapePropertyRec value) {
        return new JAXBElement<ABFuncTapePropertyRec>(_ABFuncTapePropertyRec_QNAME, ABFuncTapePropertyRec.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "int")
    public JAXBElement<Integer> createInt(Integer value) {
        return new JAXBElement<Integer>(_Int_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfABFuncTAPEDATAEX }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ArrayOfABFunc_TAPEDATAEX")
    public JAXBElement<ArrayOfABFuncTAPEDATAEX> createArrayOfABFuncTAPEDATAEX(ArrayOfABFuncTAPEDATAEX value) {
        return new JAXBElement<ArrayOfABFuncTAPEDATAEX>(_ArrayOfABFuncTAPEDATAEX_QNAME, ArrayOfABFuncTAPEDATAEX.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "decimal")
    public JAXBElement<BigDecimal> createDecimal(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_Decimal_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncAuthMode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_AuthMode")
    public JAXBElement<ABFuncAuthMode> createABFuncAuthMode(ABFuncAuthMode value) {
        return new JAXBElement<ABFuncAuthMode>(_ABFuncAuthMode_QNAME, ABFuncAuthMode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "double")
    public JAXBElement<Double> createDouble(Double value) {
        return new JAXBElement<Double>(_Double_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/Arrays", name = "ArrayOfstring")
    public JAXBElement<ArrayOfstring> createArrayOfstring(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_ArrayOfstring_QNAME, ArrayOfstring.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfunsignedInt }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/Arrays", name = "ArrayOfunsignedInt")
    public JAXBElement<ArrayOfunsignedInt> createArrayOfunsignedInt(ArrayOfunsignedInt value) {
        return new JAXBElement<ArrayOfunsignedInt>(_ArrayOfunsignedInt_QNAME, ArrayOfunsignedInt.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "guid")
    public JAXBElement<String> createGuid(String value) {
        return new JAXBElement<String>(_Guid_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "duration")
    public JAXBElement<Duration> createDuration(Duration value) {
        return new JAXBElement<Duration>(_Duration_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncSessionAndTapeRec }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_SessionAndTapeRec")
    public JAXBElement<ABFuncSessionAndTapeRec> createABFuncSessionAndTapeRec(ABFuncSessionAndTapeRec value) {
        return new JAXBElement<ABFuncSessionAndTapeRec>(_ABFuncSessionAndTapeRec_QNAME, ABFuncSessionAndTapeRec.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncMsgRecW }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_MsgRecW")
    public JAXBElement<ABFuncMsgRecW> createABFuncMsgRecW(ABFuncMsgRecW value) {
        return new JAXBElement<ABFuncMsgRecW>(_ABFuncMsgRecW_QNAME, ABFuncMsgRecW.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncServerType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_ServerType")
    public JAXBElement<ABFuncServerType> createABFuncServerType(ABFuncServerType value) {
        return new JAXBElement<ABFuncServerType>(_ABFuncServerType_QNAME, ABFuncServerType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedLong")
    public JAXBElement<BigInteger> createUnsignedLong(BigInteger value) {
        return new JAXBElement<BigInteger>(_UnsignedLong_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncSessFlagEx }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_SessFlagEx")
    public JAXBElement<ABFuncSessFlagEx> createABFuncSessFlagEx(ABFuncSessFlagEx value) {
        return new JAXBElement<ABFuncSessFlagEx>(_ABFuncSessFlagEx_QNAME, ABFuncSessFlagEx.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncManageStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ABFunc_ManageStatus")
    public JAXBElement<ABFuncManageStatus> createABFuncManageStatus(ABFuncManageStatus value) {
        return new JAXBElement<ABFuncManageStatus>(_ABFuncManageStatus_QNAME, ABFuncManageStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncTapeRecW }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetTapeRecordWResult", scope = GetTapeRecordWResponse.class)
    public JAXBElement<ABFuncTapeRecW> createGetTapeRecordWResponseGetTapeRecordWResult(ABFuncTapeRecW value) {
        return new JAXBElement<ABFuncTapeRecW>(_GetTapeRecordWResponseGetTapeRecordWResult_QNAME, ABFuncTapeRecW.class, GetTapeRecordWResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetManagedEdgeServer.class)
    public JAXBElement<String> createGetManagedEdgeServerStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetManagedEdgeServer.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = SubmitABJob.class)
    public JAXBElement<String> createSubmitABJobStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, SubmitABJob.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strJobScript", scope = SubmitABJob.class)
    public JAXBElement<String> createSubmitABJobStrJobScript(String value) {
        return new JAXBElement<String>(_SubmitABJobStrJobScript_QNAME, String.class, SubmitABJob.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetTapeRecListForRestore.class)
    public JAXBElement<String> createGetTapeRecListForRestoreStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetTapeRecListForRestore.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncStringAndFlag }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetStringFromSessionWResult", scope = GetStringFromSessionWResponse.class)
    public JAXBElement<ABFuncStringAndFlag> createGetStringFromSessionWResponseGetStringFromSessionWResult(ABFuncStringAndFlag value) {
        return new JAXBElement<ABFuncStringAndFlag>(_GetStringFromSessionWResponseGetStringFromSessionWResult_QNAME, ABFuncStringAndFlag.class, GetStringFromSessionWResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "MarkArcserveManageStatusResult", scope = MarkArcserveManageStatusResponse.class)
    public JAXBElement<String> createMarkArcserveManageStatusResponseMarkArcserveManageStatusResult(String value) {
        return new JAXBElement<String>(_MarkArcserveManageStatusResponseMarkArcserveManageStatusResult_QNAME, String.class, MarkArcserveManageStatusResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfABFuncTAPEDATAEX }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetSessionListEx2Result", scope = GetSessionListEx2Response.class)
    public JAXBElement<ArrayOfABFuncTAPEDATAEX> createGetSessionListEx2ResponseGetSessionListEx2Result(ArrayOfABFuncTAPEDATAEX value) {
        return new JAXBElement<ArrayOfABFuncTAPEDATAEX>(_GetSessionListEx2ResponseGetSessionListEx2Result_QNAME, ArrayOfABFuncTAPEDATAEX.class, GetSessionListEx2Response.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "Reserved", scope = ABFuncVersionDataExW.class)
    public JAXBElement<String> createABFuncVersionDataExWReserved(String value) {
        return new JAXBElement<String>(_ABFuncVersionDataExWReserved_QNAME, String.class, ABFuncVersionDataExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "Location", scope = ABFuncVersionDataExW.class)
    public JAXBElement<String> createABFuncVersionDataExWLocation(String value) {
        return new JAXBElement<String>(_ABFuncVersionDataExWLocation_QNAME, String.class, ABFuncVersionDataExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "TapeName", scope = ABFuncVersionDataExW.class)
    public JAXBElement<String> createABFuncVersionDataExWTapeName(String value) {
        return new JAXBElement<String>(_ABFuncVersionDataExWTapeName_QNAME, String.class, ABFuncVersionDataExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "Reserved2", scope = ABFuncVersionDataExW.class)
    public JAXBElement<String> createABFuncVersionDataExWReserved2(String value) {
        return new JAXBElement<String>(_ABFuncVersionDataExWReserved2_QNAME, String.class, ABFuncVersionDataExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfABFuncTAPEDATAEX }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetDataListExResult", scope = GetDataListExResponse.class)
    public JAXBElement<ArrayOfABFuncTAPEDATAEX> createGetDataListExResponseGetDataListExResult(ArrayOfABFuncTAPEDATAEX value) {
        return new JAXBElement<ArrayOfABFuncTAPEDATAEX>(_GetDataListExResponseGetDataListExResult_QNAME, ArrayOfABFuncTAPEDATAEX.class, GetDataListExResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetDataListEx.class)
    public JAXBElement<String> createGetDataListExStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetDataListEx.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncTAPEDATAEX }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "tapeDataEx", scope = GetDataListEx.class)
    public JAXBElement<ABFuncTAPEDATAEX> createGetDataListExTapeDataEx(ABFuncTAPEDATAEX value) {
        return new JAXBElement<ABFuncTAPEDATAEX>(_GetDataListExTapeDataEx_QNAME, ABFuncTAPEDATAEX.class, GetDataListEx.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetUsernameAndPasswordResult", scope = GetUsernameAndPasswordResponse.class)
    public JAXBElement<ArrayOfstring> createGetUsernameAndPasswordResponseGetUsernameAndPasswordResult(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_GetUsernameAndPasswordResponseGetUsernameAndPasswordResult_QNAME, ArrayOfstring.class, GetUsernameAndPasswordResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetServerType.class)
    public JAXBElement<String> createGetServerTypeStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetServerType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetGDBServer.class)
    public JAXBElement<String> createGetGDBServerStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetGDBServer.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfABFuncDetailExtRecEXW }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "QueryFileListEXWResult", scope = QueryFileListEXWResponse.class)
    public JAXBElement<ArrayOfABFuncDetailExtRecEXW> createQueryFileListEXWResponseQueryFileListEXWResult(ArrayOfABFuncDetailExtRecEXW value) {
        return new JAXBElement<ArrayOfABFuncDetailExtRecEXW>(_QueryFileListEXWResponseQueryFileListEXWResult_QNAME, ArrayOfABFuncDetailExtRecEXW.class, QueryFileListEXWResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sPath", scope = MsgFindFileExW.class)
    public JAXBElement<String> createMsgFindFileExWSPath(String value) {
        return new JAXBElement<String>(_MsgFindFileExWSPath_QNAME, String.class, MsgFindFileExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sHost", scope = MsgFindFileExW.class)
    public JAXBElement<String> createMsgFindFileExWSHost(String value) {
        return new JAXBElement<String>(_MsgFindFileExWSHost_QNAME, String.class, MsgFindFileExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = MsgFindFileExW.class)
    public JAXBElement<String> createMsgFindFileExWStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, MsgFindFileExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sPattern", scope = MsgFindFileExW.class)
    public JAXBElement<String> createMsgFindFileExWSPattern(String value) {
        return new JAXBElement<String>(_MsgFindFileExWSPattern_QNAME, String.class, MsgFindFileExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetManagedEdgeServerResult", scope = GetManagedEdgeServerResponse.class)
    public JAXBElement<String> createGetManagedEdgeServerResponseGetManagedEdgeServerResult(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerResponseGetManagedEdgeServerResult_QNAME, String.class, GetManagedEdgeServerResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetSessionAndTapeRecBySesIDW.class)
    public JAXBElement<String> createGetSessionAndTapeRecBySesIDWStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetSessionAndTapeRecBySesIDW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetSessFlagEx.class)
    public JAXBElement<String> createGetSessFlagExStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetSessFlagEx.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "ConnectARCserveResult", scope = ConnectARCserveResponse.class)
    public JAXBElement<String> createConnectARCserveResponseConnectARCserveResult(String value) {
        return new JAXBElement<String>(_ConnectARCserveResponseConnectARCserveResult_QNAME, String.class, ConnectARCserveResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetArcserveVersionInfo.class)
    public JAXBElement<String> createGetArcserveVersionInfoStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetArcserveVersionInfo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetArcserveVersionInfoResult", scope = GetArcserveVersionInfoResponse.class)
    public JAXBElement<ArrayOfstring> createGetArcserveVersionInfoResponseGetArcserveVersionInfoResult(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_GetArcserveVersionInfoResponseGetArcserveVersionInfoResult_QNAME, ArrayOfstring.class, GetArcserveVersionInfoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncStringAndFlag }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetStreamNameFromSessionWResult", scope = GetStreamNameFromSessionWResponse.class)
    public JAXBElement<ABFuncStringAndFlag> createGetStreamNameFromSessionWResponseGetStreamNameFromSessionWResult(ABFuncStringAndFlag value) {
        return new JAXBElement<ABFuncStringAndFlag>(_GetStreamNameFromSessionWResponseGetStreamNameFromSessionWResult_QNAME, ABFuncStringAndFlag.class, GetStreamNameFromSessionWResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfABFuncMsgRecW }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "MsgFindFileExWResult", scope = MsgFindFileExWResponse.class)
    public JAXBElement<ArrayOfABFuncMsgRecW> createMsgFindFileExWResponseMsgFindFileExWResult(ArrayOfABFuncMsgRecW value) {
        return new JAXBElement<ArrayOfABFuncMsgRecW>(_MsgFindFileExWResponseMsgFindFileExWResult_QNAME, ArrayOfABFuncMsgRecW.class, MsgFindFileExWResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncSessFlagEx }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetSessFlagExResult", scope = GetSessFlagExResponse.class)
    public JAXBElement<ABFuncSessFlagEx> createGetSessFlagExResponseGetSessFlagExResult(ABFuncSessFlagEx value) {
        return new JAXBElement<ABFuncSessFlagEx>(_GetSessFlagExResponseGetSessFlagExResult_QNAME, ABFuncSessFlagEx.class, GetSessFlagExResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncStringAndFlag }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetNameFromSessionWResult", scope = GetNameFromSessionWResponse.class)
    public JAXBElement<ABFuncStringAndFlag> createGetNameFromSessionWResponseGetNameFromSessionWResult(ABFuncStringAndFlag value) {
        return new JAXBElement<ABFuncStringAndFlag>(_GetNameFromSessionWResponseGetNameFromSessionWResult_QNAME, ABFuncStringAndFlag.class, GetNameFromSessionWResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfunsignedInt }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "SQLFindFileAddrListWResult", scope = SQLFindFileAddrListWResponse.class)
    public JAXBElement<ArrayOfunsignedInt> createSQLFindFileAddrListWResponseSQLFindFileAddrListWResult(ArrayOfunsignedInt value) {
        return new JAXBElement<ArrayOfunsignedInt>(_SQLFindFileAddrListWResponseSQLFindFileAddrListWResult_QNAME, ArrayOfunsignedInt.class, SQLFindFileAddrListWResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetNameFromSessionW.class)
    public JAXBElement<String> createGetNameFromSessionWStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetNameFromSessionW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strUser", scope = ConnectARCserve.class)
    public JAXBElement<String> createConnectARCserveStrUser(String value) {
        return new JAXBElement<String>(_ConnectARCserveStrUser_QNAME, String.class, ConnectARCserve.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strPassword", scope = ConnectARCserve.class)
    public JAXBElement<String> createConnectARCserveStrPassword(String value) {
        return new JAXBElement<String>(_ConnectARCserveStrPassword_QNAME, String.class, ConnectARCserve.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = CATLOGDBQueryNextExW.class)
    public JAXBElement<String> createCATLOGDBQueryNextExWStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, CATLOGDBQueryNextExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "SerialNum", scope = ABFuncTapeDataEx2W.class)
    public JAXBElement<String> createABFuncTapeDataEx2WSerialNum(String value) {
        return new JAXBElement<String>(_ABFuncTapeDataEx2WSerialNum_QNAME, String.class, ABFuncTapeDataEx2W.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "poolname", scope = ABFuncTapeRecW.class)
    public JAXBElement<String> createABFuncTapeRecWPoolname(String value) {
        return new JAXBElement<String>(_ABFuncTapeRecWPoolname_QNAME, String.class, ABFuncTapeRecW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "serialnum", scope = ABFuncTapeRecW.class)
    public JAXBElement<String> createABFuncTapeRecWSerialnum(String value) {
        return new JAXBElement<String>(_ABFuncTapeRecWSerialnum_QNAME, String.class, ABFuncTapeRecW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "tapename", scope = ABFuncTapeRecW.class)
    public JAXBElement<String> createABFuncTapeRecWTapename(String value) {
        return new JAXBElement<String>(_ABFuncTapeRecWTapename_QNAME, String.class, ABFuncTapeRecW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfABFuncDetailExtRecEXW }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "CATLOGDB_QueryNextExWResult", scope = CATLOGDBQueryNextExWResponse.class)
    public JAXBElement<ArrayOfABFuncDetailExtRecEXW> createCATLOGDBQueryNextExWResponseCATLOGDBQueryNextExWResult(ArrayOfABFuncDetailExtRecEXW value) {
        return new JAXBElement<ArrayOfABFuncDetailExtRecEXW>(_CATLOGDBQueryNextExWResponseCATLOGDBQueryNextExWResult_QNAME, ArrayOfABFuncDetailExtRecEXW.class, CATLOGDBQueryNextExWResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetArcserveManageStatus.class)
    public JAXBElement<String> createGetArcserveManageStatusStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetArcserveManageStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strEdgeServerName", scope = GetArcserveManageStatus.class)
    public JAXBElement<String> createGetArcserveManageStatusStrEdgeServerName(String value) {
        return new JAXBElement<String>(_GetArcserveManageStatusStrEdgeServerName_QNAME, String.class, GetArcserveManageStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sHost", scope = SQLFindFileAddrListW.class)
    public JAXBElement<String> createSQLFindFileAddrListWSHost(String value) {
        return new JAXBElement<String>(_MsgFindFileExWSHost_QNAME, String.class, SQLFindFileAddrListW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = SQLFindFileAddrListW.class)
    public JAXBElement<String> createSQLFindFileAddrListWStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, SQLFindFileAddrListW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sPattern", scope = SQLFindFileAddrListW.class)
    public JAXBElement<String> createSQLFindFileAddrListWSPattern(String value) {
        return new JAXBElement<String>(_MsgFindFileExWSPattern_QNAME, String.class, SQLFindFileAddrListW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "DefStr", scope = SQLFindFileAddrListW.class)
    public JAXBElement<String> createSQLFindFileAddrListWDefStr(String value) {
        return new JAXBElement<String>(_SQLFindFileAddrListWDefStr_QNAME, String.class, SQLFindFileAddrListW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sPath", scope = MsgFindFileW.class)
    public JAXBElement<String> createMsgFindFileWSPath(String value) {
        return new JAXBElement<String>(_MsgFindFileExWSPath_QNAME, String.class, MsgFindFileW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sHost", scope = MsgFindFileW.class)
    public JAXBElement<String> createMsgFindFileWSHost(String value) {
        return new JAXBElement<String>(_MsgFindFileExWSHost_QNAME, String.class, MsgFindFileW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = MsgFindFileW.class)
    public JAXBElement<String> createMsgFindFileWStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, MsgFindFileW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sPattern", scope = MsgFindFileW.class)
    public JAXBElement<String> createMsgFindFileWSPattern(String value) {
        return new JAXBElement<String>(_MsgFindFileExWSPattern_QNAME, String.class, MsgFindFileW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = QueryFileListEXW.class)
    public JAXBElement<String> createQueryFileListEXWStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, QueryFileListEXW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sComputerName", scope = QueryFileListEXW.class)
    public JAXBElement<String> createQueryFileListEXWSComputerName(String value) {
        return new JAXBElement<String>(_QueryFileListEXWSComputerName_QNAME, String.class, QueryFileListEXW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sDir", scope = QueryFileListEXW.class)
    public JAXBElement<String> createQueryFileListEXWSDir(String value) {
        return new JAXBElement<String>(_QueryFileListEXWSDir_QNAME, String.class, QueryFileListEXW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncSessionAndTapeRec }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetSessionAndTapeRecBySesIDWResult", scope = GetSessionAndTapeRecBySesIDWResponse.class)
    public JAXBElement<ABFuncSessionAndTapeRec> createGetSessionAndTapeRecBySesIDWResponseGetSessionAndTapeRecBySesIDWResult(ABFuncSessionAndTapeRec value) {
        return new JAXBElement<ABFuncSessionAndTapeRec>(_GetSessionAndTapeRecBySesIDWResponseGetSessionAndTapeRecBySesIDWResult_QNAME, ABFuncSessionAndTapeRec.class, GetSessionAndTapeRecBySesIDWResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetTapeRecordW.class)
    public JAXBElement<String> createGetTapeRecordWStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetTapeRecordW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strTapeName", scope = GetTapeRecordW.class)
    public JAXBElement<String> createGetTapeRecordWStrTapeName(String value) {
        return new JAXBElement<String>(_GetTapeRecordWStrTapeName_QNAME, String.class, GetTapeRecordW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncSessRec }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "sessRec", scope = ABFuncSessionAndTapeRec.class)
    public JAXBElement<ABFuncSessRec> createABFuncSessionAndTapeRecSessRec(ABFuncSessRec value) {
        return new JAXBElement<ABFuncSessRec>(_ABFuncSessionAndTapeRecSessRec_QNAME, ABFuncSessRec.class, ABFuncSessionAndTapeRec.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncTapeRecW }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "tapeRecW", scope = ABFuncSessionAndTapeRec.class)
    public JAXBElement<ABFuncTapeRecW> createABFuncSessionAndTapeRecTapeRecW(ABFuncTapeRecW value) {
        return new JAXBElement<ABFuncTapeRecW>(_ABFuncSessionAndTapeRecTapeRecW_QNAME, ABFuncTapeRecW.class, ABFuncSessionAndTapeRec.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "objname", scope = ABFuncMsgRecW.class)
    public JAXBElement<String> createABFuncMsgRecWObjname(String value) {
        return new JAXBElement<String>(_ABFuncMsgRecWObjname_QNAME, String.class, ABFuncMsgRecW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "objinfo", scope = ABFuncMsgRecW.class)
    public JAXBElement<String> createABFuncMsgRecWObjinfo(String value) {
        return new JAXBElement<String>(_ABFuncMsgRecWObjinfo_QNAME, String.class, ABFuncMsgRecW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strEdgeServerUserName", scope = MarkArcserveManageStatus.class)
    public JAXBElement<String> createMarkArcserveManageStatusStrEdgeServerUserName(String value) {
        return new JAXBElement<String>(_MarkArcserveManageStatusStrEdgeServerUserName_QNAME, String.class, MarkArcserveManageStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strEdgeServerPassword", scope = MarkArcserveManageStatus.class)
    public JAXBElement<String> createMarkArcserveManageStatusStrEdgeServerPassword(String value) {
        return new JAXBElement<String>(_MarkArcserveManageStatusStrEdgeServerPassword_QNAME, String.class, MarkArcserveManageStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = MarkArcserveManageStatus.class)
    public JAXBElement<String> createMarkArcserveManageStatusStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, MarkArcserveManageStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strEdgeServerDomain", scope = MarkArcserveManageStatus.class)
    public JAXBElement<String> createMarkArcserveManageStatusStrEdgeServerDomain(String value) {
        return new JAXBElement<String>(_MarkArcserveManageStatusStrEdgeServerDomain_QNAME, String.class, MarkArcserveManageStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strEdgeServerId", scope = MarkArcserveManageStatus.class)
    public JAXBElement<String> createMarkArcserveManageStatusStrEdgeServerId(String value) {
        return new JAXBElement<String>(_MarkArcserveManageStatusStrEdgeServerId_QNAME, String.class, MarkArcserveManageStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strEdgeServiceWsdl", scope = MarkArcserveManageStatus.class)
    public JAXBElement<String> createMarkArcserveManageStatusStrEdgeServiceWsdl(String value) {
        return new JAXBElement<String>(_MarkArcserveManageStatusStrEdgeServiceWsdl_QNAME, String.class, MarkArcserveManageStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "reserved", scope = ABFuncTapePropertyRec.class)
    public JAXBElement<String> createABFuncTapePropertyRecReserved(String value) {
        return new JAXBElement<String>(_ABFuncTapePropertyRecReserved_QNAME, String.class, ABFuncTapePropertyRec.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncTapeDataEx2W }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "tapeData", scope = ABFuncTapePropertyRec.class)
    public JAXBElement<ABFuncTapeDataEx2W> createABFuncTapePropertyRecTapeData(ABFuncTapeDataEx2W value) {
        return new JAXBElement<ABFuncTapeDataEx2W>(_ABFuncTapePropertyRecTapeData_QNAME, ABFuncTapeDataEx2W.class, ABFuncTapePropertyRec.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetSessionListEx2 .class)
    public JAXBElement<String> createGetSessionListEx2StrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetSessionListEx2 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncTAPEDATAEX }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "tapeDataEx", scope = GetSessionListEx2 .class)
    public JAXBElement<ABFuncTAPEDATAEX> createGetSessionListEx2TapeDataEx(ABFuncTAPEDATAEX value) {
        return new JAXBElement<ABFuncTAPEDATAEX>(_GetDataListExTapeDataEx_QNAME, ABFuncTAPEDATAEX.class, GetSessionListEx2 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetStringFromSessionW.class)
    public JAXBElement<String> createGetStringFromSessionWStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetStringFromSessionW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetStreamNameFromSessionW.class)
    public JAXBElement<String> createGetStreamNameFromSessionWStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetStreamNameFromSessionW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfABFuncMsgRecW }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "MsgFindFileWResult", scope = MsgFindFileWResponse.class)
    public JAXBElement<ArrayOfABFuncMsgRecW> createMsgFindFileWResponseMsgFindFileWResult(ArrayOfABFuncMsgRecW value) {
        return new JAXBElement<ArrayOfABFuncMsgRecW>(_MsgFindFileWResponseMsgFindFileWResult_QNAME, ArrayOfABFuncMsgRecW.class, MsgFindFileWResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "LongName", scope = ABFuncDetailExtRecEXW.class)
    public JAXBElement<String> createABFuncDetailExtRecEXWLongName(String value) {
        return new JAXBElement<String>(_ABFuncDetailExtRecEXWLongName_QNAME, String.class, ABFuncDetailExtRecEXW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncDetailRec }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "DetailRec", scope = ABFuncDetailExtRecEXW.class)
    public JAXBElement<ABFuncDetailRec> createABFuncDetailExtRecEXWDetailRec(ABFuncDetailRec value) {
        return new JAXBElement<ABFuncDetailRec>(_ABFuncDetailExtRecEXWDetailRec_QNAME, ABFuncDetailRec.class, ABFuncDetailExtRecEXW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "Path", scope = ABFuncDetailExtRecEXW.class)
    public JAXBElement<String> createABFuncDetailExtRecEXWPath(String value) {
        return new JAXBElement<String>(_ABFuncDetailExtRecEXWPath_QNAME, String.class, ABFuncDetailExtRecEXW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "ShortName", scope = ABFuncDetailExtRecEXW.class)
    public JAXBElement<String> createABFuncDetailExtRecEXWShortName(String value) {
        return new JAXBElement<String>(_ABFuncDetailExtRecEXWShortName_QNAME, String.class, ABFuncDetailExtRecEXW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = CATLOGDBQueryCloseW.class)
    public JAXBElement<String> createCATLOGDBQueryCloseWStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, CATLOGDBQueryCloseW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = CATLOGDBQueryFirstExW.class)
    public JAXBElement<String> createCATLOGDBQueryFirstExWStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, CATLOGDBQueryFirstExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pattern", scope = CATLOGDBQueryFirstExW.class)
    public JAXBElement<String> createCATLOGDBQueryFirstExWPattern(String value) {
        return new JAXBElement<String>(_CATLOGDBQueryFirstExWPattern_QNAME, String.class, CATLOGDBQueryFirstExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sComputerName", scope = CATLOGDBQueryFirstExW.class)
    public JAXBElement<String> createCATLOGDBQueryFirstExWSComputerName(String value) {
        return new JAXBElement<String>(_QueryFileListEXWSComputerName_QNAME, String.class, CATLOGDBQueryFirstExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sDir", scope = CATLOGDBQueryFirstExW.class)
    public JAXBElement<String> createCATLOGDBQueryFirstExWSDir(String value) {
        return new JAXBElement<String>(_QueryFileListEXWSDir_QNAME, String.class, CATLOGDBQueryFirstExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfABFuncTapePropertyRec }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetTapeRecListForRestoreResult", scope = GetTapeRecListForRestoreResponse.class)
    public JAXBElement<ArrayOfABFuncTapePropertyRec> createGetTapeRecListForRestoreResponseGetTapeRecListForRestoreResult(ArrayOfABFuncTapePropertyRec value) {
        return new JAXBElement<ArrayOfABFuncTapePropertyRec>(_GetTapeRecListForRestoreResponseGetTapeRecListForRestoreResult_QNAME, ArrayOfABFuncTapePropertyRec.class, GetTapeRecListForRestoreResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetTapeSessionInfoExW.class)
    public JAXBElement<String> createGetTapeSessionInfoExWStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetTapeSessionInfoExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncTAPEDATAEX }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "tapeDataEx", scope = GetTapeSessionInfoExW.class)
    public JAXBElement<ABFuncTAPEDATAEX> createGetTapeSessionInfoExWTapeDataEx(ABFuncTAPEDATAEX value) {
        return new JAXBElement<ABFuncTAPEDATAEX>(_GetDataListExTapeDataEx_QNAME, ABFuncTAPEDATAEX.class, GetTapeSessionInfoExW.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetGDBServerResult", scope = GetGDBServerResponse.class)
    public JAXBElement<String> createGetGDBServerResponseGetGDBServerResult(String value) {
        return new JAXBElement<String>(_GetGDBServerResponseGetGDBServerResult_QNAME, String.class, GetGDBServerResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "testWebServiceResult", scope = TestWebServiceResponse.class)
    public JAXBElement<String> createTestWebServiceResponseTestWebServiceResult(String value) {
        return new JAXBElement<String>(_TestWebServiceResponseTestWebServiceResult_QNAME, String.class, TestWebServiceResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sHost", scope = GetUsernameAndPassword.class)
    public JAXBElement<String> createGetUsernameAndPasswordSHost(String value) {
        return new JAXBElement<String>(_MsgFindFileExWSHost_QNAME, String.class, GetUsernameAndPassword.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = GetUsernameAndPassword.class)
    public JAXBElement<String> createGetUsernameAndPasswordStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, GetUsernameAndPassword.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sUser", scope = GetUsernameAndPassword.class)
    public JAXBElement<String> createGetUsernameAndPasswordSUser(String value) {
        return new JAXBElement<String>(_GetUsernameAndPasswordSUser_QNAME, String.class, GetUsernameAndPassword.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sPwd", scope = GetUsernameAndPassword.class)
    public JAXBElement<String> createGetUsernameAndPasswordSPwd(String value) {
        return new JAXBElement<String>(_GetUsernameAndPasswordSPwd_QNAME, String.class, GetUsernameAndPassword.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "strSessionNo", scope = IsArcserveBranch.class)
    public JAXBElement<String> createIsArcserveBranchStrSessionNo(String value) {
        return new JAXBElement<String>(_GetManagedEdgeServerStrSessionNo_QNAME, String.class, IsArcserveBranch.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ABFuncVersionDataExW }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetTapeSessionInfoExWResult", scope = GetTapeSessionInfoExWResponse.class)
    public JAXBElement<ABFuncVersionDataExW> createGetTapeSessionInfoExWResponseGetTapeSessionInfoExWResult(ABFuncVersionDataExW value) {
        return new JAXBElement<ABFuncVersionDataExW>(_GetTapeSessionInfoExWResponseGetTapeSessionInfoExWResult_QNAME, ABFuncVersionDataExW.class, GetTapeSessionInfoExWResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", name = "strValue", scope = ABFuncStringAndFlag.class)
    public JAXBElement<String> createABFuncStringAndFlagStrValue(String value) {
        return new JAXBElement<String>(_ABFuncStringAndFlagStrValue_QNAME, String.class, ABFuncStringAndFlag.class, value);
    }

}
