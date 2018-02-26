// Product.h: interface for the Product class.
//
//////////////////////////////////////////////////////////////////////
#include <afxtempl.h>
typedef CArray<CString,CString> CAString;
#if !defined(AFX_PRODUCT_H__E2F352F7_F839_405E_B660_1D8AEC12C1AF__INCLUDED_)
#define AFX_PRODUCT_H__E2F352F7_F839_405E_B660_1D8AEC12C1AF__INCLUDED_

#define MAX_SIZE 100

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CProduct
#else
class __declspec(dllimport) CProduct
#endif
{
public:
	CProduct();
	CProduct(LPCTSTR lpInfFileName,int iProduct=-1,LPCTSTR lpszMachineName=NULL);	
	virtual ~CProduct();	
private:
	CString sName;
	CString sProductCode;
	WORD wBuildNo;
	WORD wVersionNo;	
	CString sProductSubType;
	int iUpgradeMsg;
	int iInvalidUpgradeMsg;
	BOOL bForbidUpgrade;

	CString sVersionRange;
	CString sBuildRange;	
	CString sRegRootKey;		
	CAString aRegSubKey;	
	CString sProductRegKeyName;
	CString sRegBKRootKey;
	CAString aRegBKValues;	
	CAString aRegNewPathSubKey;	//added by luocl01 for upgrade support:the SQLAgent Instance reg path in Oripin isn't same to those in 11.5 
public:					
	void setName(LPTSTR lpszName){sName=lpszName;}
	CString getName(){return sName;}

	void setProductCode(LPTSTR lpszProductCode){sProductCode=lpszProductCode;}
	CString getProductCode(){return sProductCode;}
	
	void setBuildNo(WORD wBNo){wBuildNo=wBNo;}
	WORD getBuildNo(){return wBuildNo;}

	void setVersionNo(WORD wVerNo){wVersionNo=wVerNo;}
	void setVersionNo(CString sVerNo);
	WORD getVersionNo(){return wVersionNo;}
	BYTE getMajorVersionNo(){return HIBYTE(wVersionNo);}
	BYTE getMinorVersionNo(){return LOBYTE(wVersionNo);}

	void setProductSubType(LPTSTR lpszType){sProductSubType=lpszType;}	
	CString getProductSubType(){return sProductSubType;}

	void setUpgradeMsg(int iTmpId){iUpgradeMsg=iTmpId;}
	int getUpgradeMsg(){return iUpgradeMsg;}

	void setInvalidUpgradeMsg(int iTmpId){iInvalidUpgradeMsg=iTmpId;}
	int getInvalidUpgradeMsg(){return iInvalidUpgradeMsg;}
	
	void setForbidUpgrade(BOOL bForbid){bForbidUpgrade =bForbid;}
	int getForbidUpgrade(){return bForbidUpgrade;}

	void setVersionRange(LPTSTR lpszVersionRange){sVersionRange=lpszVersionRange;}
	CString getVersionRange(){return sVersionRange;}
	WORD getMaxVersionNo();
	WORD getMinVersionNo();
	int getMajorMaxVersionNo();
	int getMinorMaxVersionNo();
	int getMajorMinVersionNo();
	int getMinorMinVersionNo();

	void setBuildRange(LPTSTR lpszBuildRange){sBuildRange=lpszBuildRange;}
	CString getBuildRange(){return sBuildRange;}
	int getMaxBuildNo();
	int getMinBuildNo();

	void setProductDescriptor(LPTSTR lpszProductRegKeyName){sProductRegKeyName=lpszProductRegKeyName;}
	CString getProductDescriptor(){return sProductRegKeyName;}

	void setRegRootKey(LPTSTR lpszRegRootKey){sRegRootKey=lpszRegRootKey;}
	CString getRegRootKey(){return sRegRootKey;}

	void setRegBKRootKey(LPTSTR lpszRegBKRootKey){sRegBKRootKey=lpszRegBKRootKey;}
	CString getRegBKRootKey(){return sRegBKRootKey;}

	void setRegSubKey(LPTSTR lpszSubKey);
	CAString * getRegSubKey(){return &aRegSubKey;}
	
	void setRegBKValues(LPTSTR lpszBKValues);
	CAString * getRegBKValues(){return &aRegBKValues;}

	//added by luocl01 for upgrade support:the SQLAgent Instance reg path in Oripin isn't same to those in 11.5 
	void setRegNewPathSubKey(LPTSTR lpszNewPathSubKey);
	CAString * getRegNewPathSubKey(){return &aRegNewPathSubKey;}
	//added by luocl01 for upgrade support:the SQLAgent Instance reg path in Oripin isn't same to those in 11.5 
};
//////////////////////////////
#endif // !defined(AFX_PRODUCT_H__E2F352F7_F839_405E_B660_1D8AEC12C1AF__INCLUDED_)
