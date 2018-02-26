// APMSettingDlg.cpp : implementation file
//

#include "stdafx.h"
#include "APMUtility.h"
#include "APMSettingDlg.h"
#include "Utility.h"
#include "APMUtilityDlg.h"
#include <atlrx.h>

// CAPMSettingDlg dialog

IMPLEMENT_DYNAMIC(CAPMSettingDlg, CDialog)

CAPMSettingDlg::CAPMSettingDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CAPMSettingDlg::IDD, pParent)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
	GetEdgeRootDir(m_strIniFilePath);
	m_strD2DIniFilePath = m_strIniFilePath;	
	m_strIniFilePath += wstring(FOLDER_UPDATEMANAGER) + L"\\" + FOLDER_UPDATEMANAGER_ARCAPP + L"\\" + 
		FILE_EDGE_PMSETTING_INI;	
	m_strD2DIniFilePath += wstring(FOLDER_UPDATEMANAGER) + L"\\" + FILE_D2D_PMSETTING_INI;
	m_strCaption.LoadStringW(IDS_productName);	
}

CAPMSettingDlg::~CAPMSettingDlg()
{
}

void CAPMSettingDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_RADIO_CASERVER, m_radioCaserver);
	DDX_Control(pDX, IDC_CHECK_PROXY, m_ckProxy);
	DDX_Control(pDX, IDC_CHECK_PROXY_AUTH, m_ckProxyAuth);
	DDX_Control(pDX, IDC_EDIT_PROXY_SERVER, m_tbProxyServer);
	DDX_Control(pDX, IDC_EDIT_PROXY_USER, m_tbProxyUser);
	DDX_Control(pDX, IDC_EDIT_PROXY_PORT, m_tbProxyPort);
	DDX_Control(pDX, IDC_EDIT_PROXY_PWD, m_tbProxyPwd);
	DDX_Control(pDX, IDC_EDIT_STAGING_SERVER, m_tbStagingServer);
	DDX_Control(pDX, IDC_EDIT_STAGING_PORT, m_tbStagingPort);
	DDX_Radio(pDX, IDC_RADIO_CASERVER, m_apmSettingModel.m_nServerType);
	DDX_Check(pDX, IDC_CHECK_PROXY, m_apmSettingModel.m_bProxy);
	DDX_Text(pDX, IDC_EDIT_PROXY_SERVER, m_apmSettingModel.m_proxyServer.m_strServer);
	DDX_Text(pDX, IDC_EDIT_PROXY_PORT, m_apmSettingModel.m_proxyServer.m_strPort);
	DDX_Check(pDX, IDC_CHECK_PROXY_AUTH, m_apmSettingModel.m_bProxyAuth);
	DDX_Text(pDX, IDC_EDIT_PROXY_USER, m_apmSettingModel.m_strProxyUsername);
	DDX_Text(pDX, IDC_EDIT_PROXY_PWD, m_apmSettingModel.m_strProxyPwd);
	DDX_Text(pDX, IDC_EDIT_STAGING_SERVER, m_apmSettingModel.m_stagingServer.m_strServer);
	DDX_Text(pDX, IDC_EDIT_STAGING_PORT, m_apmSettingModel.m_stagingServer.m_strPort);
	DDX_Control(pDX, IDC_LBL_PROXYSERVER, m_lblProxyServer);
	DDX_Control(pDX, IDC_LBL_PROXYPORT, m_lblProxyPort);
	DDX_Control(pDX, IDC_LBL_PROXY_USER, m_lblProxyUser);
	DDX_Control(pDX, IDC_LBL_PROXY_PWD, m_lblProxyPwd);
	DDX_Control(pDX, IDC_LBL_STAGING_SERVER, m_lblStagingServer);
	DDX_Control(pDX, IDC_LBL_STAGING_PORT, m_lblStagingPort);
}


BEGIN_MESSAGE_MAP(CAPMSettingDlg, CDialog)
	ON_BN_CLICKED(IDC_BUTTON3, &CAPMSettingDlg::OnBnClickedButton3)
	ON_BN_CLICKED(IDC_BTN_CANCEL, &CAPMSettingDlg::onBnClickedButtonCancel)
	ON_BN_CLICKED(IDC_RADIO_CASERVER, &CAPMSettingDlg::OnBnClickedRadioCaserver)
	ON_BN_CLICKED(IDC_RADIO_STAGIN, &CAPMSettingDlg::OnBnClickedRadioStagin)
	ON_BN_CLICKED(IDC_CHECK_PROXY, &CAPMSettingDlg::OnBnClickedCheck1)
	ON_BN_CLICKED(IDC_CHECK_PROXY_AUTH, &CAPMSettingDlg::OnBnClickedCheckProxyAuth)
END_MESSAGE_MAP()


// CAPMSettingDlg message handlers

BOOL CAPMSettingDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// TODO:  Add extra initialization here
	
	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

	APMSetting apmSetting;
	LoadAPMSetting( m_strIniFilePath, apmSetting );
	m_apmSettingModel = ConvertToModel(apmSetting);
	UpdateData(FALSE);
	if(m_apmSettingModel.m_nServerType == APM_SERVERTYPE_CA){
		EnableCAServerControls(TRUE);
		EnableStagingControls(FALSE);
	}else{
		EnableCAServerControls(FALSE);
		EnableStagingControls(TRUE);
	}

	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}

void CAPMSettingDlg::EnableStagingControls( BOOL bEnable )
{
	m_tbStagingServer.EnableWindow(bEnable);
	m_tbStagingPort.EnableWindow(bEnable);
	m_lblStagingServer.EnableWindow(bEnable);
	m_lblStagingPort.EnableWindow(bEnable);
}

void CAPMSettingDlg::OnBnClickedButton3()
{
	APMSetting apmSetting;

	UpdateData(TRUE);
	apmSetting = ConvertFromModel( m_apmSettingModel );
	if( Validate() ){		
		SaveAPMSetting( m_strIniFilePath, apmSetting );
		if(IsProductInstalled( APM_EDGE_CM ))
		{
			SaveAPMSetting( m_strD2DIniFilePath, apmSetting );
		}		
		this->ShowWindow( SW_HIDE );
		CAPMUtilityDlg *processDlg = new CAPMUtilityDlg();		
		processDlg->Create(IDD_APMUTILITY_DIALOG, this);
		processDlg->ShowWindow(SW_SHOW);		
		processDlg->UpdateNow(&apmSetting);
	}
}

void CAPMSettingDlg::EnableCAServerControls( BOOL bEnable )
{
	m_ckProxy.EnableWindow( bEnable );
	EnableProxyControls( bEnable );
}

void CAPMSettingDlg::EnableProxyAuthControls( BOOL bEnable )
{
	if( bEnable && m_radioCaserver.GetCheck() && m_ckProxy.GetCheck() && m_ckProxyAuth.GetCheck()){
		m_tbProxyUser.EnableWindow(TRUE);
		m_tbProxyPwd.EnableWindow(TRUE);
		m_lblProxyUser.EnableWindow(TRUE);
		m_lblProxyPwd.EnableWindow(TRUE);
	}
	else{
		m_tbProxyUser.EnableWindow(FALSE);
		m_tbProxyPwd.EnableWindow(FALSE);
		m_lblProxyUser.EnableWindow(FALSE);
		m_lblProxyPwd.EnableWindow(FALSE);
	}
	
}

void CAPMSettingDlg::EnableProxyControls( BOOL bEnable )
{
	if( bEnable && m_radioCaserver.GetCheck() && m_ckProxy.GetCheck()){
		m_tbProxyServer.EnableWindow(TRUE);
		m_tbProxyPort.EnableWindow(TRUE);
		m_ckProxyAuth.EnableWindow(TRUE);
		m_lblProxyServer.EnableWindow(TRUE);
		m_lblProxyPort.EnableWindow(TRUE);
		EnableProxyAuthControls(TRUE);
	}else{
		m_tbProxyServer.EnableWindow(FALSE);
		m_tbProxyPort.EnableWindow(FALSE);
		m_ckProxyAuth.EnableWindow(FALSE);
		m_lblProxyServer.EnableWindow(FALSE);
		m_lblProxyPort.EnableWindow(FALSE);
		EnableProxyAuthControls(FALSE);
	}
}

APMSettingModel CAPMSettingDlg::ConvertToModel( const APMSetting &apmSetting )
{
	APMSettingModel model;

	model.m_bProxy = apmSetting.m_bProxy;
	model.m_bProxyAuth = apmSetting.m_bProxyAuth;
	model.m_nServerType = apmSetting.m_nServerType;
	model.m_proxyServer = ConvertToModel( apmSetting.m_proxyServer );
	model.m_strProxyPwd.Format(L"%s", apmSetting.m_strProxyPwd.c_str());
	model.m_strProxyUsername.Format(L"%s", apmSetting.m_strProxyUsername.c_str());
	
	if(apmSetting.m_vecStaging.size() > 0)
		model.m_stagingServer = ConvertToModel( *apmSetting.m_vecStaging.begin() );
	if( model.m_stagingServer.m_strPort.IsEmpty() )
		model.m_stagingServer.m_strPort = "8015";
	return model;
}

ServerInfoModel CAPMSettingDlg::ConvertToModel( const ServerInfo &serverInfo )
{
	ServerInfoModel model;
	model.m_strServer.Format( L"%s", serverInfo.m_strServer.c_str() );
	if( serverInfo.m_nPort == 0 )
		model.m_strPort = L"";
	else
		model.m_strPort.Format( L"%d", serverInfo.m_nPort );
	return model;
}

EdgeAPM::APMSetting CAPMSettingDlg::ConvertFromModel( const APMSettingModel &model )
{
	APMSetting apmSetting;
	apmSetting.m_bProxy = model.m_bProxy;
	apmSetting.m_bProxyAuth = model.m_bProxyAuth;
	apmSetting.m_nServerType = model.m_nServerType;
	apmSetting.m_proxyServer = ConvertFromModel( model.m_proxyServer );
	apmSetting.m_strProxyPwd = model.m_strProxyPwd;
	apmSetting.m_strProxyUsername = model.m_strProxyUsername;
	if( !model.m_stagingServer.m_strServer.IsEmpty() )
		apmSetting.m_vecStaging.push_back( ConvertFromModel(model.m_stagingServer) );
	return apmSetting;
}

EdgeAPM::ServerInfo CAPMSettingDlg::ConvertFromModel( const ServerInfoModel &model )
{
	ServerInfo svrInfo;

	svrInfo.m_strServer = model.m_strServer;
	if( model.m_strPort.IsEmpty() )
		svrInfo.m_nPort = 0;
	else
		svrInfo.m_nPort = _wtoi(model.m_strPort);
	return svrInfo;
}

ServerInfoModel::ServerInfoModel()
{
}

APMSettingModel::APMSettingModel():m_nServerType(APM_SERVERTYPE_CA), m_bProxy(FALSE), m_bProxyAuth(FALSE)
{
}

void CAPMSettingDlg::OnBnClickedRadioCaserver()
{
	EnableCAServerControls(TRUE);
	EnableStagingControls(FALSE);
}

void CAPMSettingDlg::OnBnClickedRadioStagin()
{
	EnableCAServerControls(FALSE);
	EnableStagingControls(TRUE);
}

void CAPMSettingDlg::OnBnClickedCheck1()
{
	if( m_ckProxy.GetCheck() == BST_CHECKED )
		EnableProxyControls(TRUE);
	else
		EnableProxyControls(FALSE);
}

void CAPMSettingDlg::OnBnClickedCheckProxyAuth()
{
	if( m_ckProxyAuth.GetCheck() == BST_CHECKED )
		EnableProxyAuthControls(TRUE);
	else
		EnableProxyAuthControls(FALSE);
}

BOOL CAPMSettingDlg::Validate()
{
	CString strError;

	UpdateData(TRUE);
	if( m_apmSettingModel.m_nServerType == APM_SERVERTYPE_CA ){
		if( m_apmSettingModel.m_bProxy ){
			if( !ValidateServerName(m_apmSettingModel.m_proxyServer.m_strServer) ){
				strError.LoadString( IDS_ERROR_PROXY_SERVER_EMPTY );			
				MessageBoxW(strError, m_strCaption, MB_ICONERROR);
				return FALSE;
			}
			if( !ValidatePort(m_apmSettingModel.m_proxyServer.m_strPort) ){
				strError.LoadString( IDS_ERROR_PROXY_PORT_EMPTY );
				MessageBoxW(strError, m_strCaption, MB_ICONERROR);
				return FALSE;
			}
			if( m_apmSettingModel.m_bProxyAuth ){
				if( m_apmSettingModel.m_strProxyUsername.IsEmpty() ){
					strError.LoadString( IDS_ERROR_PROXY_USERNAME_EMPTY );
					MessageBoxW(strError, m_strCaption, MB_ICONERROR);
					return FALSE;
				}
			}
		}
	}else{
		if( !ValidateServerName(m_apmSettingModel.m_stagingServer.m_strServer) ){
			strError.LoadString( IDS_ERROR_STAGING_SERVER_EMPTY );
			MessageBoxW(strError, m_strCaption, MB_ICONERROR);
			return FALSE;
		}
		if( !ValidatePort(m_apmSettingModel.m_stagingServer.m_strPort) ){
			strError.LoadString( IDS_ERROR_STAGING_PORT_EMPTY );
			MessageBoxW(strError, m_strCaption, MB_ICONERROR);
			return FALSE;
		}
	}
	return TRUE;
}

BOOL CAPMSettingDlg::ValidateServerName( const CString &strName )
{
	wstring strReg = L"[\\^`~!@#\\$\\^&\\*\\(\\)=\\+\\[\\]{}\\\\\\|;:'\",<>/\\?%]+";
	CAtlRegExp<> reUrl;
	REParseError status = REPARSE_ERROR_OK;
	CAtlREMatchContext<> mcUrl;

	if( strName.IsEmpty() )
		return FALSE;
	status = reUrl.Parse( strReg.c_str() );
	if (REPARSE_ERROR_OK != status){
		return FALSE;
	}
	return !reUrl.Match(strName, &mcUrl);
}

BOOL CAPMSettingDlg::ValidatePort( const CString &strPort )
{
	wstring port = strPort;
	int nPort;
	if( String2Int( port, nPort) )
		return FALSE;
	if( nPort > 0 && nPort < 65535 )
		return TRUE;
	else
		return FALSE;
}

void CAPMSettingDlg::onBnClickedButtonCancel()
{
	if(((CAPMUtilityApp*)AfxGetApp())->IsRebootRequired())
	{
		CString strCaption;
		strCaption.LoadStringW(IDS_productName);
		CString	strText;
		strText.LoadStringW(IDS_REBOOT_SYSTEM_ON_DEMAND);
		if( IDYES == MessageBoxW(strText, strCaption, MB_YESNO | MB_ICONINFORMATION))
		RebootSystem(true);		
	}
		
	OnCancel();
}
