#pragma once
#ifndef __MSXMLPARSERWRAPPER_H_
#define __MSXMLPARSERWRAPPER_H_

#include <wtypes.h>

typedef HANDLE HXMLDOCUMENT;
typedef HANDLE HXMLELEMENT;
typedef HANDLE HXMLELEMENTLIST;
typedef HANDLE HXMLATTRIBUTE;

#ifdef __cplusplus
extern "C" //__declspec(dllexport)
{
#endif

enum JSERR_CODE
{
	JSERR_INVALID_ERRCODE	= -10000,
	JSERR_SUCCESS			= 0,
	JSERR_FAIL				= -1,
	JSERR_INVALIDPARAM		= -2,
	JSERR_OUTOFMEM			= -3,
	JSERR_PDB				= -4,
	JSERR_INITED			= -5,
	JSERR_COM				= -6,
	JSERR_UNKNOWN			= -7,
	JSERR_NOIMPL			= -8,
	JSERR_XML				= -9,
	JSERR_BUFTOOSMALL		= -10,
	JSERR_FILE				= -11,
	JSERR_BINARY			= -12
};

HXMLDOCUMENT JobCreateDocument();
HXMLDOCUMENT JobCreateDocumentFromXML(LPCWSTR lpszXML); 
HXMLDOCUMENT JobCreateDocumentFromXMLStream(const TCHAR * in_XMLStream);
BOOL JobDestroyDocument(HXMLDOCUMENT hDocument);

BOOL JobSerializeDocumentToXML(HXMLDOCUMENT hDocument, LPWSTR pXMLBuffer, LPDWORD pdwBufferSizeInWCHARs);
BOOL JobGetDocumentSizeInWCHARs(HXMLDOCUMENT hDocument,LPDWORD pdwBufferSizeInWCHARs);

HXMLELEMENT JobGetDocumentElement(HXMLDOCUMENT hDocument);
BOOL JobSetDocumentElement(HXMLDOCUMENT hDocument, HXMLELEMENT hRootElement);

HXMLELEMENT JobCreateElement(HXMLDOCUMENT hDocument, LPCWSTR lpszElementName, LPCWSTR lpszElementValue);
BOOL JobDestroyElement(HXMLELEMENT hElement);

BOOL JobGetElementName(HXMLELEMENT hElement, LPWSTR pElementNameBuffer, LPDWORD pdwBufferSizeInWCHARs);
BOOL JobGetElementValue(HXMLELEMENT hElement, LPWSTR pElementValueBuffer, LPDWORD pdwBufferSizeInWCHARs);
BOOL JobSetElementValue(HXMLELEMENT hElement, LPCWSTR lpszElementValue);

LONG JobGetElementChildCount(HXMLELEMENT hElement);
HXMLELEMENT JobGetElementChild(HXMLELEMENT hElement, LONG nIndex);
HXMLELEMENT JobGetElementChildByName(HXMLELEMENT hElement, LPCWSTR lpszChildElementName);

BOOL JobAddElementChild(HXMLELEMENT hElement, HXMLELEMENT hChildElement);
BOOL JobRemoveElementChild(HXMLELEMENT hElement, HXMLELEMENT hChildElement);

HXMLATTRIBUTE JobCreateAttribute(HXMLDOCUMENT hDocument, LPCWSTR lpszAttributeName, LPCWSTR lpszAttributeValue);
BOOL JobDestroyAttribute(HXMLATTRIBUTE hAttribute);

BOOL JobGetAttributeName(HXMLATTRIBUTE hAttribute, LPWSTR pAttributeNameBuffer, LPDWORD pdwBufferSizeInWCHARs);
BOOL JobGetAttributeValue(HXMLATTRIBUTE hAttribute, LPWSTR pAttributeValueBuffer, LPDWORD pdwBufferSizeInWCHARs);
BOOL JobSetAttributeValue(HXMLATTRIBUTE hAttribute, LPCWSTR lpszAttributeValue);

LONG JobGetElementAttributeCount(HXMLELEMENT hElement);
HXMLATTRIBUTE JobGetElementAttribute(HXMLELEMENT hElement, LONG nIndex);
HXMLATTRIBUTE JobGetElementAttributeByName(HXMLELEMENT hElement, LPCWSTR lpszAttributeName);

BOOL JobAddElementAttribute(HXMLELEMENT hElement, HXMLATTRIBUTE hAttribute);
BOOL JobRemoveElementAttribute(HXMLELEMENT hElement, HXMLATTRIBUTE hAttribute);

BOOL JobSerializeElementToXML(HXMLELEMENT hElement, LPWSTR pXMLBuffer, LPDWORD pdwBufferSizeInWCHARs);

HXMLELEMENT JobGetElement(HXMLDOCUMENT hDocument, LPWSTR pXPath);
HXMLELEMENTLIST JobGetElementList(HXMLDOCUMENT hDocument, LPWSTR pXPath);
BOOL JobDestroyElementList(HXMLELEMENTLIST hElementList);
LONG JobGetElementCountFromList(HXMLELEMENTLIST hElementList);
HXMLELEMENT JobGetElementFromList(HXMLELEMENTLIST hElementList, LONG nIndex);
//BOOL JobSaveXMLDocument(HXMLDOCUMENT hDocument, CString SFileName);
BOOL JobSaveXMLDocument(HXMLDOCUMENT hDocument, LPCTSTR SFileName);
//BOOL JobCreateProcessingInstruction(HXMLDOCUMENT hDocument, CString sInstruction, CString sData);
BOOL JobCreateProcessingInstruction(HXMLDOCUMENT hDocument, LPCTSTR sInstruction, LPCTSTR sData);
int JSXReadXML(const wchar_t *pcwsFilename, wchar_t *pwsXMLBuffer, long	*plXMLBufSize);
#ifdef __cplusplus
}
#endif


#endif // __MSXMLPARSERWRAPPER_H_