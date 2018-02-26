#ifndef _WIN32_WINNT		// Allow use of features specific to Windows XP or later.                   
#define _WIN32_WINNT 0x0501	// Change this to the appropriate value to target other versions of Windows.
#endif						

#ifndef _WIN32_DCOM
#define _WIN32_DCOM
#endif

#include "stdafx.h"
#include "MSXMLParserWrapper.h"
#import "msxml.tlb" raw_interfaces_only
#include <string>
using std::wstring;
#pragma warning(disable: 4996)
//
//using namespace std;

HXMLDOCUMENT JobCreateDocument()
{
	//static BOOL s_bInitialized = FALSE;

	IXMLDOMDocument *pIXMLDOMDocument;
	HRESULT hr;
	/*::CoInitialize(NULL);*/
	/*if(!s_bInitialized)
	{
		hr = ::CoInitialize(NULL);

		if(FAILED(hr))
		{
			return NULL;
		}

		s_bInitialized = TRUE;
	}
  */
	hr = ::CoCreateInstance(
			__uuidof(MSXML::DOMDocument),
			NULL,
			CLSCTX_INPROC_SERVER,
			__uuidof(IXMLDOMDocument),
			(void **)&pIXMLDOMDocument);



	if(FAILED(hr))
	{
		DWORD dwError = GetLastError();
		return NULL;
	}

	hr = pIXMLDOMDocument->put_async(VARIANT_FALSE);
	if(FAILED(hr))
	{
		pIXMLDOMDocument->Release();
		pIXMLDOMDocument = NULL;

		return NULL;
	}

	hr = pIXMLDOMDocument->put_validateOnParse(VARIANT_FALSE);
	if(FAILED(hr))
	{
		pIXMLDOMDocument->Release();
		pIXMLDOMDocument = NULL;

		return NULL;
	}

	hr = pIXMLDOMDocument->put_resolveExternals(VARIANT_FALSE);
	if(FAILED(hr))
	{
		pIXMLDOMDocument->Release();
		pIXMLDOMDocument = NULL;

		return NULL;
	}

	return pIXMLDOMDocument;
}

HXMLDOCUMENT JobCreateDocumentFromXML(LPCWSTR lpszXML)
{
	if(!lpszXML)
	{
		return NULL;
	}

	long bufSize = 0;
	int rc = JSERR_SUCCESS;
	wchar_t* pwszXmlBuf = NULL;
	if (JSXReadXML(lpszXML, NULL, &bufSize) == JSERR_BUFTOOSMALL)
	{
		pwszXmlBuf = (wchar_t *)malloc(bufSize*sizeof(char));
		if ((rc = JSXReadXML(lpszXML, pwszXmlBuf, &bufSize)) != JSERR_SUCCESS)
		{
			return NULL;
		}
	}
    if (rc)
    {
        wprintf(L"Read buffer from xml failed, rc = %d, path = %s",
            rc, lpszXML);
        return NULL;
    }

	IXMLDOMDocument *pIXMLDOMDocument = NULL;
	BSTR bstrXML = NULL;
	VARIANT_BOOL bSuccessful = VARIANT_FALSE;

	HRESULT hr;

	pIXMLDOMDocument = (IXMLDOMDocument *)JobCreateDocument();
	if(!pIXMLDOMDocument)
	{
		return NULL;
	}

	bstrXML = ::SysAllocString(pwszXmlBuf);
	if(!bstrXML)
	{
		pIXMLDOMDocument->Release();
		pIXMLDOMDocument = NULL;

		return NULL;
	}

	hr = pIXMLDOMDocument->loadXML(bstrXML, &bSuccessful);

	::SysFreeString(bstrXML);
	bstrXML = NULL;

	if(FAILED(hr) || (bSuccessful == VARIANT_FALSE))
	{
//		WriteDebugMessage("document->Load  Failed in the method JobCreateDocumentFromXML", bTrace);
		pIXMLDOMDocument->Release();
		pIXMLDOMDocument = NULL;

		return NULL;
	}	

	return pIXMLDOMDocument;
}

BOOL JobDestroyDocument(HXMLDOCUMENT hDocument)
{
	IXMLDOMDocument *pIXMLDOMDocument = NULL;

	pIXMLDOMDocument = (IXMLDOMDocument *)hDocument;
	
	if(!pIXMLDOMDocument)
	{
		return FALSE;
	}

	pIXMLDOMDocument->Release();
	pIXMLDOMDocument = NULL;

	return TRUE;
}

BOOL JobSerializeDocumentToXML(HXMLDOCUMENT hDocument, LPWSTR pXMLBuffer, LPDWORD pdwBufferSizeInWCHARs)
{
	IXMLDOMDocument *pIXMLDOMDocument = NULL;
	BSTR bstrXML = NULL;
	DWORD dwLength;

	HRESULT hr;
	BOOL bRet = FALSE;

	pIXMLDOMDocument = (IXMLDOMDocument *)hDocument;

	if(!pIXMLDOMDocument)
	{
		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}
	
	hr = pIXMLDOMDocument->get_xml(&bstrXML);
	if(FAILED(hr))
	{
		if(bstrXML)
		{
			::SysFreeString(bstrXML);
			bstrXML = NULL;
		}

		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}
	
	dwLength = wcslen(bstrXML) + 1;

	if(dwLength > *pdwBufferSizeInWCHARs)
	{
		bRet = FALSE;
	}
	else
	{
		wcscpy(pXMLBuffer, bstrXML);
		bRet = TRUE;
	}

	if(bstrXML)
	{
		::SysFreeString(bstrXML);
		bstrXML = NULL;
	}

	*pdwBufferSizeInWCHARs = dwLength;
	return bRet;
}

HXMLDOCUMENT JobCreateDocumentFromXMLStream(const TCHAR * in_XMLStream)
{
	BOOL bTrace = TRUE;//CheckTraceFlag();

	//WriteDebugMessage("Entered JobCreateDocumentFromXML method ", bTrace);

	IXMLDOMDocument *pIXMLDOMDocument = NULL;
	BSTR bstrXML = NULL;
	VARIANT_BOOL bSuccessful = VARIANT_FALSE;

	HRESULT hr;

	if(!in_XMLStream)
	{
		//WriteDebugMessage("The XML File name is null in method JobCreateDocumentFromXML", bTrace);
		return NULL;
	}

	pIXMLDOMDocument = (IXMLDOMDocument *)JobCreateDocument();

	if(!pIXMLDOMDocument)
	{
//		WriteDebugMessage("Job createDocument Failed in the method JobCreateDocumentFromXML", bTrace);
		return NULL;
	}

	bstrXML = ::SysAllocString(in_XMLStream);
	if(!bstrXML)
	{
//		WriteDebugMessage("Conversion 2 bstr Failed in the method JobCreateDocumentFromXML", bTrace);
		pIXMLDOMDocument->Release();
		pIXMLDOMDocument = NULL;

		return NULL;
	}

	hr = pIXMLDOMDocument->loadXML(bstrXML, &bSuccessful);
	// Load takes a _variant_t parameter;
	//Create a variant variable
	//_variant_t varXmlFileName;
	//varXmlFileName.bstrVal = bstrXML;
	//varXmlFileName.vt = VT_BSTR;
	//hr = pIXMLDOMDocument->load(varXmlFileName,  &bSuccessful);
	

	::SysFreeString(bstrXML);
	bstrXML = NULL;

	if(FAILED(hr) || (bSuccessful == VARIANT_FALSE))
	{
//		WriteDebugMessage("document->Load  Failed in the method JobCreateDocumentFromXML", bTrace);
		pIXMLDOMDocument->Release();
		pIXMLDOMDocument = NULL;

		return NULL;
	}	

	return pIXMLDOMDocument;	
}

HXMLELEMENT JobGetDocumentElement(HXMLDOCUMENT hDocument)
{
	IXMLDOMDocument *pIXMLDOMDocument = NULL;
	IXMLDOMElement *pIXMLDOMElement = NULL;

	HRESULT hr;

	pIXMLDOMDocument = (IXMLDOMDocument *)hDocument;
	if(!pIXMLDOMDocument)
	{
		return NULL;
	}

	hr = pIXMLDOMDocument->get_documentElement(&pIXMLDOMElement);
	if(FAILED(hr))
	{
		if(pIXMLDOMElement)
		{
			pIXMLDOMElement->Release();
			pIXMLDOMElement = NULL;
		}

		return NULL;
	}

	return pIXMLDOMElement;
}

BOOL JobSetDocumentElement(HXMLDOCUMENT hDocument, HXMLELEMENT hRootElement)
{
	IXMLDOMDocument *pIXMLDOMDocument = NULL;
	IXMLDOMElement *pIXMLDOMElement = NULL;

	HRESULT hr;

	pIXMLDOMDocument = (IXMLDOMDocument *)hDocument;
	if(!pIXMLDOMDocument)
	{
		return FALSE;
	}

	pIXMLDOMElement = (IXMLDOMElement *)hRootElement;
	if(!pIXMLDOMElement)
	{
		return FALSE;
	}

	hr = pIXMLDOMDocument->putref_documentElement(pIXMLDOMElement);
	if(FAILED(hr))
	{
		return FALSE;
	}
	else
	{
		return TRUE;
	}
}

HXMLELEMENT JobCreateElement(HXMLDOCUMENT hDocument, LPCWSTR lpszElementName, LPCWSTR lpszElementValue)
{
	IXMLDOMDocument *pIXMLDOMDocument = NULL;
	IXMLDOMElement *pIXMLDOMElement = NULL;
	BSTR bstrName = NULL;

	HRESULT hr;

	if(!lpszElementName)
	{
		return NULL;
	}

	pIXMLDOMDocument = (IXMLDOMDocument *)hDocument;
	if(!pIXMLDOMDocument)
	{
		return NULL;
	}

	bstrName = ::SysAllocString(lpszElementName);
	if(!bstrName)
	{
		return NULL;
	}

	hr = pIXMLDOMDocument->createElement(bstrName, &pIXMLDOMElement);

	::SysFreeString(bstrName);
	bstrName = NULL;

	if(FAILED(hr) || !pIXMLDOMElement)
	{
		return NULL;
	}

	if(lpszElementValue)
	{
		BSTR bstrValue = NULL;
		IXMLDOMText * pIXMLDOMText = NULL;
		IXMLDOMNode * pIXMLDOMNodeNewChild = NULL;

		bstrValue = ::SysAllocString(lpszElementValue);
		if(!bstrValue)
		{
			pIXMLDOMElement->Release();
			pIXMLDOMElement = NULL;

			return NULL;
		}

		hr = pIXMLDOMDocument->createTextNode(bstrValue, &pIXMLDOMText);

		::SysFreeString(bstrValue);
		bstrValue = NULL;

		if(FAILED(hr) || !pIXMLDOMText)
		{
			pIXMLDOMElement->Release();
			pIXMLDOMElement = NULL;

			return NULL;
		}

		hr = pIXMLDOMElement->appendChild(pIXMLDOMText, &pIXMLDOMNodeNewChild);
		
		if(pIXMLDOMNodeNewChild)
		{
			pIXMLDOMNodeNewChild->Release();
			pIXMLDOMNodeNewChild = NULL;
		}

		pIXMLDOMText->Release();
		pIXMLDOMText = NULL;
	}
	
	return pIXMLDOMElement;
}

BOOL JobDestroyElement(HXMLELEMENT hElement)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;

	pIXMLDOMElement = (IXMLDOMElement *)hElement;
	if(!pIXMLDOMElement)
	{
		return FALSE;
	}

	pIXMLDOMElement->Release();
	pIXMLDOMElement = NULL;

	return TRUE;
}

BOOL JobGetElementName(HXMLELEMENT hElement, LPWSTR pElementNameBuffer, LPDWORD pdwBufferSizeInWCHARs)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;
	BSTR bstrNodeName = NULL;
	DWORD dwLength;

	HRESULT hr;

	pIXMLDOMElement = (IXMLDOMElement *)hElement;
	if(!pIXMLDOMElement)
	{
		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}

	hr = pIXMLDOMElement->get_nodeName(&bstrNodeName);
	if(FAILED(hr) || !bstrNodeName)
	{
		if(bstrNodeName)
		{
			::SysFreeString(bstrNodeName);
			bstrNodeName = NULL;
		}

		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}

	dwLength = wcslen(bstrNodeName) + 1;
	if(dwLength > *pdwBufferSizeInWCHARs)
	{
		::SysFreeString(bstrNodeName);
		bstrNodeName = NULL;

		*pdwBufferSizeInWCHARs = dwLength;
		return FALSE;
	}

	wcscpy(pElementNameBuffer, bstrNodeName);

	::SysFreeString(bstrNodeName);
	bstrNodeName = NULL;

	*pdwBufferSizeInWCHARs = dwLength;
	return TRUE;
}

BOOL JobGetElementValue(HXMLELEMENT hElement, LPWSTR pElementValueBuffer, LPDWORD pdwBufferSizeInWCHARs)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;
	IXMLDOMNodeList *pIXMLDOMNodeListChildren = NULL;

	LONG nNodeListLength = 0;
	LONG i;

	HRESULT hr;

	pIXMLDOMElement = (IXMLDOMElement *)hElement;
	if(!pIXMLDOMElement)
	{
		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}

	hr = pIXMLDOMElement->get_childNodes(&pIXMLDOMNodeListChildren);
	if(FAILED(hr) || !pIXMLDOMNodeListChildren)
	{
		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}

	hr = pIXMLDOMNodeListChildren->get_length(&nNodeListLength);
	if(FAILED(hr))
	{
		pIXMLDOMNodeListChildren->Release();
		pIXMLDOMNodeListChildren = NULL;
		
		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}
	
	for(i = 0; i < nNodeListLength; i ++)
	{
		IXMLDOMNode *pIXMLDOMNodeChild = NULL;
		DOMNodeType nDOMNodeTypeChild = NODE_INVALID;

		hr = pIXMLDOMNodeListChildren->get_item(i, &pIXMLDOMNodeChild);
		if(FAILED(hr) || !pIXMLDOMNodeChild)
		{
			if(pIXMLDOMNodeChild)
			{
				pIXMLDOMNodeChild->Release();
				pIXMLDOMNodeChild = NULL;
			}

			pIXMLDOMNodeListChildren->Release();
			pIXMLDOMNodeListChildren = NULL;
			
			*pdwBufferSizeInWCHARs = 0;
			return FALSE;
		}

		nDOMNodeTypeChild = NODE_INVALID;
		hr = pIXMLDOMNodeChild->get_nodeType(&nDOMNodeTypeChild);
		if(FAILED(hr))
		{
			pIXMLDOMNodeChild->Release();
			pIXMLDOMNodeChild = NULL;

			pIXMLDOMNodeListChildren->Release();
			pIXMLDOMNodeListChildren = NULL;
			
			*pdwBufferSizeInWCHARs = 0;
			return FALSE;
		}

		if(NODE_TEXT == nDOMNodeTypeChild)
		{
			VARIANT varElementValue;
			DWORD dwLength;

			::VariantInit(&varElementValue);
			hr = pIXMLDOMNodeChild->get_nodeValue(&varElementValue);
			if(FAILED(hr) || (varElementValue.vt != VT_BSTR) || !(varElementValue.bstrVal))
			{
				::VariantClear(&varElementValue);

				pIXMLDOMNodeChild->Release();
				pIXMLDOMNodeChild = NULL;

				pIXMLDOMNodeListChildren->Release();
				pIXMLDOMNodeListChildren = NULL;
				
				*pdwBufferSizeInWCHARs = 0;
				return FALSE;
			}

			dwLength = wcslen(varElementValue.bstrVal) + 1;
			if(dwLength > *pdwBufferSizeInWCHARs)
			{
				::VariantClear(&varElementValue);

				pIXMLDOMNodeChild->Release();
				pIXMLDOMNodeChild = NULL;

				pIXMLDOMNodeListChildren->Release();
				pIXMLDOMNodeListChildren = NULL;
				
				*pdwBufferSizeInWCHARs = dwLength;
				return FALSE;
			}
			else
			{
				wcscpy(pElementValueBuffer, varElementValue.bstrVal);

				::VariantClear(&varElementValue);

				pIXMLDOMNodeChild->Release();
				pIXMLDOMNodeChild = NULL;

				pIXMLDOMNodeListChildren->Release();
				pIXMLDOMNodeListChildren = NULL;
				
				*pdwBufferSizeInWCHARs = dwLength;
				return TRUE;
			}
		}
		else
		{
			pIXMLDOMNodeChild->Release();
			pIXMLDOMNodeChild = NULL;
		}
	}

	pIXMLDOMNodeListChildren->Release();
	pIXMLDOMNodeListChildren = NULL;
	
	*pdwBufferSizeInWCHARs = 0;
	return FALSE;
}

BOOL JobSetElementValue(HXMLELEMENT hElement, LPCWSTR lpszElementValue)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;
	IXMLDOMNodeList *pIXMLDOMNodeListChildren = NULL;

	LONG nNodeListLength = 0;
	LONG i;

	HRESULT hr;

	pIXMLDOMElement = (IXMLDOMElement *)hElement;
	if(!pIXMLDOMElement)
	{
		return FALSE;
	}

	hr = pIXMLDOMElement->get_childNodes(&pIXMLDOMNodeListChildren);
	if(FAILED(hr))
	{
		return FALSE;
	}

	if(pIXMLDOMNodeListChildren)
	{
		hr = pIXMLDOMNodeListChildren->get_length(&nNodeListLength);
		if(FAILED(hr))
		{
			pIXMLDOMNodeListChildren->Release();
			pIXMLDOMNodeListChildren = NULL;
			
			return FALSE;
		}
		
		for(i = 0; i < nNodeListLength; i ++)
		{
			IXMLDOMNode *pIXMLDOMNodeChild = NULL;
			DOMNodeType nDOMNodeTypeChild = NODE_INVALID;

			hr = pIXMLDOMNodeListChildren->get_item(i, &pIXMLDOMNodeChild);
			if(FAILED(hr) || !pIXMLDOMNodeChild)
			{
				if(pIXMLDOMNodeChild)
				{
					pIXMLDOMNodeChild->Release();
					pIXMLDOMNodeChild = NULL;
				}

				pIXMLDOMNodeListChildren->Release();
				pIXMLDOMNodeListChildren = NULL;
				
				return FALSE;
			}

			nDOMNodeTypeChild = NODE_INVALID;
			hr = pIXMLDOMNodeChild->get_nodeType(&nDOMNodeTypeChild);
			if(FAILED(hr))
			{
				pIXMLDOMNodeChild->Release();
				pIXMLDOMNodeChild = NULL;

				pIXMLDOMNodeListChildren->Release();
				pIXMLDOMNodeListChildren = NULL;
				
				return FALSE;
			}

			if(NODE_TEXT == nDOMNodeTypeChild)
			{
				if(lpszElementValue)
				{
					VARIANT varElementValue;

					::VariantInit(&varElementValue);
					varElementValue.vt = VT_BSTR;
					varElementValue.bstrVal = ::SysAllocString(lpszElementValue);

					hr = pIXMLDOMNodeChild->put_nodeValue(varElementValue);

					::VariantClear(&varElementValue);
				}
				else
				{
					IXMLDOMNode *IXMLDOMNodeChildRemoved = NULL;

					hr = pIXMLDOMElement->removeChild(pIXMLDOMNodeChild, &IXMLDOMNodeChildRemoved);
					if(IXMLDOMNodeChildRemoved)
					{
						IXMLDOMNodeChildRemoved->Release();
						IXMLDOMNodeChildRemoved = NULL;
					}
				}

				pIXMLDOMNodeChild->Release();
				pIXMLDOMNodeChild = NULL;

				pIXMLDOMNodeListChildren->Release();
				pIXMLDOMNodeListChildren = NULL;

				if(FAILED(hr))
				{
					return FALSE;
				}
				else
				{
					return TRUE;
				}
			}
			else
			{
				pIXMLDOMNodeChild->Release();
				pIXMLDOMNodeChild = NULL;
			}
		}

		pIXMLDOMNodeListChildren->Release();
		pIXMLDOMNodeListChildren = NULL;
	}

	if(lpszElementValue)
	{
		IXMLDOMDocument *pIXMLDOMDocument = NULL;
		BSTR bstrValue = NULL;
		IXMLDOMText * pIXMLDOMText = NULL;
		IXMLDOMNode * pIXMLDOMNodeNewChild = NULL;

		hr = pIXMLDOMElement->get_ownerDocument(&pIXMLDOMDocument);
		if(FAILED(hr) || !pIXMLDOMDocument)
		{
			return FALSE;
		}

		bstrValue = ::SysAllocString(lpszElementValue);
		if(!bstrValue)
		{
			pIXMLDOMDocument->Release();
			pIXMLDOMDocument = NULL;

			return FALSE;
		}

		hr = pIXMLDOMDocument->createTextNode(bstrValue, &pIXMLDOMText);

		::SysFreeString(bstrValue);
		bstrValue = NULL;

		if(FAILED(hr) || !pIXMLDOMText)
		{
			if(pIXMLDOMText)
			{
				pIXMLDOMText->Release();
				pIXMLDOMText = NULL;
			}

			pIXMLDOMDocument->Release();
			pIXMLDOMDocument = NULL;

			return FALSE;
		}

		hr = pIXMLDOMElement->appendChild(pIXMLDOMText, &pIXMLDOMNodeNewChild);
		
		if(pIXMLDOMNodeNewChild)
		{
			pIXMLDOMNodeNewChild->Release();
			pIXMLDOMNodeNewChild = NULL;
		}

		pIXMLDOMText->Release();
		pIXMLDOMText = NULL;

		pIXMLDOMDocument->Release();
		pIXMLDOMDocument = NULL;

		if(FAILED(hr))
		{
			return FALSE;
		}
		else
		{
			return TRUE;
		}
	}

	return TRUE;
}

LONG JobGetElementChildCount(HXMLELEMENT hElement)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;
	IXMLDOMNodeList *pIXMLDOMNodeListChildren = NULL;

	LONG nNodeListLength = 0;
	LONG nElementChildCount = 0;
	LONG i;

	HRESULT hr;

	pIXMLDOMElement = (IXMLDOMElement *)hElement;
	if(!pIXMLDOMElement)
	{
		return -1;
	}

	hr = pIXMLDOMElement->get_childNodes(&pIXMLDOMNodeListChildren);
	if(FAILED(hr))
	{
		return -1;
	}

	if(!pIXMLDOMNodeListChildren)
	{
		return 0;
	}

	hr = pIXMLDOMNodeListChildren->get_length(&nNodeListLength);
	if(FAILED(hr))
	{
		pIXMLDOMNodeListChildren->Release();
		pIXMLDOMNodeListChildren = NULL;
		
		return -1;
	}
	
	nElementChildCount = 0;
	for(i = 0; i < nNodeListLength; i ++)
	{
		IXMLDOMNode *pIXMLDOMNodeChild = NULL;
		DOMNodeType nDOMNodeTypeChild = NODE_INVALID;

		hr = pIXMLDOMNodeListChildren->get_item(i, &pIXMLDOMNodeChild);
		if(FAILED(hr) || !pIXMLDOMNodeChild)
		{
			if(pIXMLDOMNodeChild)
			{
				pIXMLDOMNodeChild->Release();
				pIXMLDOMNodeChild = NULL;
			}

			pIXMLDOMNodeListChildren->Release();
			pIXMLDOMNodeListChildren = NULL;
			
			return -1;
		}

		nDOMNodeTypeChild = NODE_INVALID;
		hr = pIXMLDOMNodeChild->get_nodeType(&nDOMNodeTypeChild);
		if(FAILED(hr))
		{
			pIXMLDOMNodeChild->Release();
			pIXMLDOMNodeChild = NULL;

			pIXMLDOMNodeListChildren->Release();
			pIXMLDOMNodeListChildren = NULL;
			
			return -1;
		}

		if(NODE_ELEMENT == nDOMNodeTypeChild)
		{
			nElementChildCount ++;
		}
		pIXMLDOMNodeChild->Release();
		pIXMLDOMNodeChild = NULL;
	}

	pIXMLDOMNodeListChildren->Release();
	pIXMLDOMNodeListChildren = NULL;
	
	return nElementChildCount;
}

HXMLELEMENT JobGetElementChild(HXMLELEMENT hElement, LONG nIndex)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;
	IXMLDOMNodeList *pIXMLDOMNodeListChildren = NULL;

	LONG nNodeListLength = 0;
	LONG nElementChildCounter = -1;
	LONG i;

	HRESULT hr;

	if(nIndex < 0)
	{
		return NULL;
	}

	pIXMLDOMElement = (IXMLDOMElement *)hElement;
	if(!pIXMLDOMElement)
	{
		return NULL;
	}

	hr = pIXMLDOMElement->get_childNodes(&pIXMLDOMNodeListChildren);
	if(FAILED(hr) || !pIXMLDOMNodeListChildren)
	{
		return NULL;
	}

	hr = pIXMLDOMNodeListChildren->get_length(&nNodeListLength);
	if(FAILED(hr))
	{
		pIXMLDOMNodeListChildren->Release();
		pIXMLDOMNodeListChildren = NULL;
		
		return NULL;
	}
	
	if(nIndex >= nNodeListLength)
	{
		pIXMLDOMNodeListChildren->Release();
		pIXMLDOMNodeListChildren = NULL;

		return NULL;
	}

	nElementChildCounter = -1;
	for(i = 0; i < nNodeListLength; i ++)
	{
		IXMLDOMNode *pIXMLDOMNodeChild = NULL;
		DOMNodeType nDOMNodeTypeChild = NODE_INVALID;

		hr = pIXMLDOMNodeListChildren->get_item(i, &pIXMLDOMNodeChild);
		if(FAILED(hr) || !pIXMLDOMNodeChild)
		{
			if(pIXMLDOMNodeChild)
			{
				pIXMLDOMNodeChild->Release();
				pIXMLDOMNodeChild = NULL;
			}

			pIXMLDOMNodeListChildren->Release();
			pIXMLDOMNodeListChildren = NULL;
			
			return NULL;
		}

		nDOMNodeTypeChild = NODE_INVALID;
		hr = pIXMLDOMNodeChild->get_nodeType(&nDOMNodeTypeChild);
		if(FAILED(hr))
		{
			pIXMLDOMNodeChild->Release();
			pIXMLDOMNodeChild = NULL;

			pIXMLDOMNodeListChildren->Release();
			pIXMLDOMNodeListChildren = NULL;
			
			return NULL;
		}

		if(NODE_ELEMENT == nDOMNodeTypeChild)
		{
			nElementChildCounter ++;

			if(nElementChildCounter == nIndex)
			{
				pIXMLDOMNodeListChildren->Release();
				pIXMLDOMNodeListChildren = NULL;

				return pIXMLDOMNodeChild;
			}
		}

		pIXMLDOMNodeChild->Release();
		pIXMLDOMNodeChild = NULL;
	}

	pIXMLDOMNodeListChildren->Release();
	pIXMLDOMNodeListChildren = NULL;
	
	return NULL;
}

HXMLELEMENT JobGetElementChildByName(HXMLELEMENT hElement, LPCWSTR lpszChildElementName)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;
	IXMLDOMNodeList *pIXMLDOMNodeListChildren = NULL;

	LONG nNodeListLength = 0;
	LONG i;

	HRESULT hr;

	pIXMLDOMElement = (IXMLDOMElement *)hElement;
	if(!pIXMLDOMElement)
	{
		return NULL;
	}

	hr = pIXMLDOMElement->get_childNodes(&pIXMLDOMNodeListChildren);
	if(FAILED(hr) || !pIXMLDOMNodeListChildren)
	{
		return NULL;
	}

	hr = pIXMLDOMNodeListChildren->get_length(&nNodeListLength);
	if(FAILED(hr))
	{
		pIXMLDOMNodeListChildren->Release();
		pIXMLDOMNodeListChildren = NULL;
		
		return NULL;
	}
	
	for(i = 0; i < nNodeListLength; i ++)
	{
		IXMLDOMNode *pIXMLDOMNodeChild = NULL;
		DOMNodeType nDOMNodeTypeChild = NODE_INVALID;

		hr = pIXMLDOMNodeListChildren->get_item(i, &pIXMLDOMNodeChild);
		if(FAILED(hr) || !pIXMLDOMNodeChild)
		{
			if(pIXMLDOMNodeChild)
			{
				pIXMLDOMNodeChild->Release();
				pIXMLDOMNodeChild = NULL;
			}

			pIXMLDOMNodeListChildren->Release();
			pIXMLDOMNodeListChildren = NULL;
			
			return NULL;
		}

		nDOMNodeTypeChild = NODE_INVALID;
		hr = pIXMLDOMNodeChild->get_nodeType(&nDOMNodeTypeChild);
		if(FAILED(hr))
		{
			pIXMLDOMNodeChild->Release();
			pIXMLDOMNodeChild = NULL;

			pIXMLDOMNodeListChildren->Release();
			pIXMLDOMNodeListChildren = NULL;
			
			return NULL;
		}

		if(NODE_ELEMENT == nDOMNodeTypeChild)
		{
			BSTR bstrElementName = NULL;

			hr = pIXMLDOMNodeChild->get_nodeName(&bstrElementName);
			if(FAILED(hr) || !bstrElementName)
			{
				if(bstrElementName)
				{
					::SysFreeString(bstrElementName);
					bstrElementName = NULL;
				}

				pIXMLDOMNodeChild->Release();
				pIXMLDOMNodeChild = NULL;

				pIXMLDOMNodeListChildren->Release();
				pIXMLDOMNodeListChildren = NULL;

				return NULL;
			}

			if(!wcscmp(lpszChildElementName, bstrElementName))
			{
				::SysFreeString(bstrElementName);
				bstrElementName = NULL;

				pIXMLDOMNodeListChildren->Release();
				pIXMLDOMNodeListChildren = NULL;

				return pIXMLDOMNodeChild;
			}

			::SysFreeString(bstrElementName);
			bstrElementName = NULL;
		}

		pIXMLDOMNodeChild->Release();
		pIXMLDOMNodeChild = NULL;
	}

	pIXMLDOMNodeListChildren->Release();
	pIXMLDOMNodeListChildren = NULL;
	
	return NULL;
}

BOOL JobAddElementChild(HXMLELEMENT hElement, HXMLELEMENT hChildElement)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;
	IXMLDOMElement *pIXMLDOMElementChild = NULL;
	IXMLDOMNode *pIXMLDOMNodeNewChild = NULL;

	HRESULT hr;

	pIXMLDOMElement = (IXMLDOMElement *)hElement;
	if(!pIXMLDOMElement)
	{
		return FALSE;
	}

	pIXMLDOMElementChild = (IXMLDOMElement *)hChildElement;
	if(!pIXMLDOMElementChild)
	{
		return FALSE;
	}

	hr = pIXMLDOMElement->appendChild(pIXMLDOMElementChild, &pIXMLDOMNodeNewChild);

	if(pIXMLDOMNodeNewChild)
	{
		pIXMLDOMNodeNewChild->Release();
		pIXMLDOMNodeNewChild = NULL;
	}

	if(FAILED(hr))
	{
		return FALSE;
	}
	else
	{
		return TRUE;
	}
}

BOOL JobRemoveElementChild(HXMLELEMENT hElement, HXMLELEMENT hChildElement)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;
	IXMLDOMElement *pIXMLDOMElementChild = NULL;
	IXMLDOMNode *pIXMLDOMNodeChildRemoved = NULL;

	HRESULT hr;

	pIXMLDOMElement = (IXMLDOMElement *)hElement;
	if(!pIXMLDOMElement)
	{
		return FALSE;
	}

	pIXMLDOMElementChild = (IXMLDOMElement *)hChildElement;
	if(!pIXMLDOMElementChild)
	{
		return FALSE;
	}

	hr = pIXMLDOMElement->removeChild(pIXMLDOMElementChild, &pIXMLDOMNodeChildRemoved);

	if(pIXMLDOMNodeChildRemoved)
	{
		pIXMLDOMNodeChildRemoved->Release();
		pIXMLDOMNodeChildRemoved = NULL;
	}

	if(FAILED(hr))
	{
		return FALSE;
	}
	else
	{
		return TRUE;
	}
}

HXMLATTRIBUTE JobCreateAttribute(HXMLDOCUMENT hDocument, LPCWSTR lpszAttributeName, LPCWSTR lpszAttributeValue)
{
	IXMLDOMDocument *pIXMLDOMDocument = NULL;
	IXMLDOMAttribute *pIXMLDOMAttribute = NULL;
	BSTR bstrName = NULL;
	BSTR bstrValue = NULL;
	VARIANT varValue;

	HRESULT hr;

	if(!lpszAttributeName || !lpszAttributeValue)
	{
		return NULL;
	}
	
	pIXMLDOMDocument = (IXMLDOMDocument *)hDocument;
	if(!pIXMLDOMDocument)
	{
		return NULL;
	}

	bstrName = ::SysAllocString(lpszAttributeName);
	if(!bstrName)
	{
		return NULL;
	}

	bstrValue = ::SysAllocString(lpszAttributeValue);
	if(!bstrValue)
	{
		::SysFreeString(bstrName);
		bstrName = NULL;

		return NULL;
	}

	hr = pIXMLDOMDocument->createAttribute(bstrName, &pIXMLDOMAttribute);
	if(FAILED(hr) || !pIXMLDOMAttribute)
	{
		if(pIXMLDOMAttribute)
		{
			pIXMLDOMAttribute->Release();
			pIXMLDOMAttribute = NULL;
		}

		::SysFreeString(bstrName);
		bstrName = NULL;

		::SysFreeString(bstrValue);
		bstrValue = NULL;

		return NULL;
	}

	::SysFreeString(bstrName);
	bstrName = NULL;

	::VariantInit(&varValue);

	varValue.vt = VT_BSTR;
	varValue.bstrVal = bstrValue;
	bstrValue = NULL;

	hr = pIXMLDOMAttribute->put_nodeValue(varValue);

	::VariantClear(&varValue);

	if(FAILED(hr))
	{
		pIXMLDOMAttribute->Release();
		pIXMLDOMAttribute = NULL;

		return NULL;
	}

	return pIXMLDOMAttribute;
}

BOOL JobDestroyAttribute(HXMLATTRIBUTE hAttribute)
{
	IXMLDOMAttribute *pIXMLDOMAttribute = NULL;

	pIXMLDOMAttribute = (IXMLDOMAttribute *)hAttribute;
	
	if(!pIXMLDOMAttribute)
	{
		return FALSE;
	}

	pIXMLDOMAttribute->Release();
	pIXMLDOMAttribute = NULL;

	return TRUE;
}

BOOL JobGetAttributeName(HXMLATTRIBUTE hAttribute, LPWSTR pAttributeNameBuffer, LPDWORD pdwBufferSizeInWCHARs)
{
	IXMLDOMAttribute *pIXMLDOMAttribute = NULL;
	BSTR bstrNodeName = NULL;
	DWORD dwLength;

	HRESULT hr;

	pIXMLDOMAttribute = (IXMLDOMAttribute *)hAttribute;
	if(!pIXMLDOMAttribute)
	{
		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}

	hr = pIXMLDOMAttribute->get_nodeName(&bstrNodeName);
	if(FAILED(hr) || !bstrNodeName)
	{
		if(bstrNodeName)
		{
			::SysFreeString(bstrNodeName);
			bstrNodeName = NULL;
		}

		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}

	dwLength = wcslen(bstrNodeName) + 1;
	if(dwLength > *pdwBufferSizeInWCHARs)
	{
		::SysFreeString(bstrNodeName);
		bstrNodeName = NULL;

		*pdwBufferSizeInWCHARs = dwLength;
		return FALSE;
	}

	wcscpy(pAttributeNameBuffer, bstrNodeName);

	::SysFreeString(bstrNodeName);
	bstrNodeName = NULL;

	*pdwBufferSizeInWCHARs = dwLength;
	return TRUE;
}

BOOL JobGetAttributeValue(HXMLATTRIBUTE hAttribute, LPWSTR pAttributeValueBuffer, LPDWORD pdwBufferSizeInWCHARs)
{
	IXMLDOMAttribute *pIXMLDOMAttribute = NULL;
	VARIANT varAttributeValue;
	DWORD dwLength;

	HRESULT hr;

	pIXMLDOMAttribute = (IXMLDOMAttribute *)hAttribute;
	if(!pIXMLDOMAttribute)
	{
		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}

	::VariantInit(&varAttributeValue);

	hr = pIXMLDOMAttribute->get_nodeValue(&varAttributeValue);
	if(FAILED(hr) || (varAttributeValue.vt != VT_BSTR) || !(varAttributeValue.bstrVal))
	{
		::VariantClear(&varAttributeValue);

		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}

	dwLength = wcslen(varAttributeValue.bstrVal) + 1;
//	dwLength = _tcsclen(varAttributeValue.bstrVal) + 1;
	if(dwLength > *pdwBufferSizeInWCHARs)
	{
		::VariantClear(&varAttributeValue);

		*pdwBufferSizeInWCHARs = dwLength;
		return FALSE;
	}

	wcscpy(pAttributeValueBuffer, varAttributeValue.bstrVal);

	::VariantClear(&varAttributeValue);

	*pdwBufferSizeInWCHARs = dwLength;
	return TRUE;
}

BOOL JobSetAttributeValue(HXMLATTRIBUTE hAttribute, LPCWSTR lpszAttributeValue)
{
	IXMLDOMAttribute *pIXMLDOMAttribute = NULL;
	VARIANT varAttributeValue;

	HRESULT hr;

	if(!lpszAttributeValue)
	{
		return FALSE;
	}

	pIXMLDOMAttribute = (IXMLDOMAttribute *)hAttribute;
	if(!pIXMLDOMAttribute)
	{
		return FALSE;
	}

	::VariantInit(&varAttributeValue);
	varAttributeValue.vt = VT_BSTR;
	varAttributeValue.bstrVal = ::SysAllocString(lpszAttributeValue);

	if(!(varAttributeValue.bstrVal))
	{
		::VariantClear(&varAttributeValue);
		return FALSE;
	}

	hr = pIXMLDOMAttribute->put_nodeValue(varAttributeValue);

	::VariantClear(&varAttributeValue);

	if(FAILED(hr))
	{
		return FALSE;
	}
	else
	{
		return TRUE;
	}
}

LONG JobGetElementAttributeCount(HXMLELEMENT hElement)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;
	IXMLDOMNamedNodeMap *pIXMLDOMNamedNodeMapAttributes;
	LONG nAttributeCount;

	HRESULT hr;

	pIXMLDOMElement = (IXMLDOMElement *)hElement;
	if(!pIXMLDOMElement)
	{
		return -1;
	}

	hr = pIXMLDOMElement->get_attributes(&pIXMLDOMNamedNodeMapAttributes);
	if(FAILED(hr) || !pIXMLDOMNamedNodeMapAttributes)
	{
		if(pIXMLDOMNamedNodeMapAttributes)
		{
			pIXMLDOMNamedNodeMapAttributes->Release();
			pIXMLDOMNamedNodeMapAttributes = NULL;
		}

		return -1;
	}

	nAttributeCount = 0;
	hr = pIXMLDOMNamedNodeMapAttributes->get_length(&nAttributeCount);

	pIXMLDOMNamedNodeMapAttributes->Release();
	pIXMLDOMNamedNodeMapAttributes = NULL;

	if(FAILED(hr))
	{
		return -1;
	}
	else
	{
		return nAttributeCount;
	}
}

HXMLATTRIBUTE JobGetElementAttribute(HXMLELEMENT hElement, LONG nIndex)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;
	IXMLDOMNamedNodeMap *pIXMLDOMNamedNodeMapAttributes;
	IXMLDOMNode *pIXMLDOMNodeAttribute = NULL;
	LONG nAttributeCount;

	HRESULT hr;

	if(nIndex < 0)
	{
		return NULL;
	}

	pIXMLDOMElement = (IXMLDOMElement *)hElement;
	if(!pIXMLDOMElement)
	{
		return NULL;
	}

	hr = pIXMLDOMElement->get_attributes(&pIXMLDOMNamedNodeMapAttributes);
	if(FAILED(hr) || !pIXMLDOMNamedNodeMapAttributes)
	{
		if(pIXMLDOMNamedNodeMapAttributes)
		{
			pIXMLDOMNamedNodeMapAttributes->Release();
			pIXMLDOMNamedNodeMapAttributes = NULL;
		}

		return NULL;
	}

	nAttributeCount = 0;
	hr = pIXMLDOMNamedNodeMapAttributes->get_length(&nAttributeCount);

	if(FAILED(hr))
	{
		pIXMLDOMNamedNodeMapAttributes->Release();
		pIXMLDOMNamedNodeMapAttributes = NULL;

		return NULL;
	}

	if(nIndex >= nAttributeCount)
	{
		pIXMLDOMNamedNodeMapAttributes->Release();
		pIXMLDOMNamedNodeMapAttributes = NULL;

		return NULL;
	}

	hr = pIXMLDOMNamedNodeMapAttributes->get_item(nIndex, &pIXMLDOMNodeAttribute);

	pIXMLDOMNamedNodeMapAttributes->Release();
	pIXMLDOMNamedNodeMapAttributes = NULL;

	if(FAILED(hr) || !pIXMLDOMNodeAttribute)
	{
		if(pIXMLDOMNodeAttribute)
		{
			pIXMLDOMNodeAttribute->Release();
			pIXMLDOMNodeAttribute = NULL;
		}

		return NULL;
	}
	else
	{
		return pIXMLDOMNodeAttribute;
	}
}

HXMLATTRIBUTE JobGetElementAttributeByName(HXMLELEMENT hElement, LPCWSTR lpszAttributeName)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;
	IXMLDOMNamedNodeMap *pIXMLDOMNamedNodeMapAttributes = NULL;
	BSTR bstrAttributeName = NULL;
	IXMLDOMNode *pIXMLDOMNodeAttribute = NULL;

	HRESULT hr;

	if(!lpszAttributeName)
	{
		return NULL;
	}

	pIXMLDOMElement = (IXMLDOMElement *)hElement;
	if(!pIXMLDOMElement)
	{
		return NULL;
	}

	bstrAttributeName = ::SysAllocString(lpszAttributeName);
	if(!bstrAttributeName)
	{
		return NULL;
	}

	hr = pIXMLDOMElement->get_attributes(&pIXMLDOMNamedNodeMapAttributes);
	if(FAILED(hr) || !pIXMLDOMNamedNodeMapAttributes)
	{
		::SysFreeString(bstrAttributeName);
		bstrAttributeName = NULL;

		return NULL;
	}
	
	hr = pIXMLDOMNamedNodeMapAttributes->getNamedItem(bstrAttributeName, &pIXMLDOMNodeAttribute);
	if(FAILED(hr))
	{
		::SysFreeString(bstrAttributeName);
		bstrAttributeName = NULL;

		pIXMLDOMNamedNodeMapAttributes->Release();
		pIXMLDOMNamedNodeMapAttributes = NULL;

		if(pIXMLDOMNodeAttribute)
		{
			pIXMLDOMNodeAttribute->Release();
			pIXMLDOMNodeAttribute = NULL;
		}

		return FALSE;
	}

	::SysFreeString(bstrAttributeName);
	bstrAttributeName = NULL;

	pIXMLDOMNamedNodeMapAttributes->Release();
	pIXMLDOMNamedNodeMapAttributes = NULL;

	return pIXMLDOMNodeAttribute;
}

BOOL JobAddElementAttribute(HXMLELEMENT hElement, HXMLATTRIBUTE hAttribute)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;
	IXMLDOMAttribute *pIXMLDOMAttribute = NULL;
	IXMLDOMAttribute *pIXMLDOMAttributeAdded = NULL;

	HRESULT hr;

	pIXMLDOMElement = (IXMLDOMElement *)hElement;
	if(!pIXMLDOMElement)
	{
		return FALSE;
	}

	pIXMLDOMAttribute = (IXMLDOMAttribute *)hAttribute;
	if(!pIXMLDOMAttribute)
	{
		return FALSE;
	}

	hr = pIXMLDOMElement->setAttributeNode(pIXMLDOMAttribute, &pIXMLDOMAttributeAdded);
	if(pIXMLDOMAttributeAdded)
	{
		pIXMLDOMAttributeAdded->Release();
		pIXMLDOMAttributeAdded = NULL;
	}

	if(FAILED(hr))
	{
		return FALSE;
	}
	else
	{
		return TRUE;
	}
}

BOOL JobRemoveElementAttribute(HXMLELEMENT hElement, HXMLATTRIBUTE hAttribute)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;
	IXMLDOMAttribute *pIXMLDOMAttribute = NULL;
	IXMLDOMAttribute *pIXMLDOMAttributeRemoved = NULL;

	HRESULT hr;

	pIXMLDOMElement = (IXMLDOMElement *)hElement;
	if(!pIXMLDOMElement)
	{
		return FALSE;
	}

	pIXMLDOMAttribute = (IXMLDOMAttribute *)hAttribute;
	if(!pIXMLDOMAttribute)
	{
		return FALSE;
	}

	hr = pIXMLDOMElement->removeAttributeNode(pIXMLDOMAttribute, &pIXMLDOMAttributeRemoved);
	if(pIXMLDOMAttributeRemoved)
	{
		pIXMLDOMAttributeRemoved->Release();
		pIXMLDOMAttributeRemoved = NULL;
	}

	if(FAILED(hr))
	{
		return FALSE;
	}
	else
	{
		return TRUE;
	}
}

BOOL JobSerializeElementToXML(HXMLELEMENT hElement, LPWSTR pXMLBuffer, LPDWORD pdwBufferSizeInWCHARs)
{
	IXMLDOMElement *pIXMLDOMElement = NULL;
	BSTR bstrXML = NULL;
	DWORD dwLength;

	HRESULT hr;
	BOOL bRet = FALSE;

	pIXMLDOMElement = (IXMLDOMElement *)hElement;

	if(!pIXMLDOMElement)
	{
		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}
	
	hr = pIXMLDOMElement->get_xml(&bstrXML);
	if(FAILED(hr))
	{
		if(bstrXML)
		{
			::SysFreeString(bstrXML);
			bstrXML = NULL;
		}

		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}
	
	dwLength = wcslen(bstrXML) + 1;

	if(dwLength > *pdwBufferSizeInWCHARs)
	{
		bRet = FALSE;
	}
	else
	{
		wcscpy(pXMLBuffer, bstrXML);
		bRet = TRUE;
	}

	if(bstrXML)
	{
		::SysFreeString(bstrXML);
		bstrXML = NULL;
	}

	*pdwBufferSizeInWCHARs = dwLength;
	return bRet;
}

/*
 * pXPath is a string in XPath format such as /root/tag1/tag2
 */
HXMLELEMENT JobGetElement(HXMLDOCUMENT hDocument, LPWSTR pXPath)
{
	if( hDocument == NULL || pXPath == NULL )
		return NULL;

	IXMLDOMDocument *pIXMLDOMDocument = (IXMLDOMDocument *)hDocument;

	IXMLDOMNode *pNode = NULL;
	
	BSTR bstr = ::SysAllocString(pXPath);

	HRESULT hr = pIXMLDOMDocument->selectSingleNode(bstr, &pNode);

	::SysFreeString(bstr);
	bstr = NULL;

	if( FAILED(hr) )
	{
		return NULL;
	}

	return (HXMLELEMENT)pNode;
}

/*
 * pXPath is a string in XPath format such as /root/tag1/tag2
 */
HXMLELEMENTLIST JobGetElementList(HXMLDOCUMENT hDocument, LPWSTR pXPath)
{
	if( hDocument == NULL || pXPath == NULL )
		return NULL;

//	IXMLDOMDocument *pIXMLDOMDocument = (IXMLDOMDocument *)hDocument;

	IXMLDOMNode *pIXMLDOMDocument = (IXMLDOMNode *)hDocument;

	IXMLDOMNodeList *pNodeList = NULL;
	
	BSTR bstr = ::SysAllocString(pXPath);

	HRESULT hr = pIXMLDOMDocument->selectNodes(bstr, &pNodeList);

	::SysFreeString(bstr);
	bstr = NULL;

	if( FAILED(hr) )
	{
		return NULL;
	}

	return (HXMLELEMENTLIST)pNodeList;
}

BOOL JobDestroyElementList(HXMLELEMENTLIST hElementList)
{
	IXMLDOMNodeList *pNodeList = (IXMLDOMNodeList *)hElementList;

	if( !pNodeList )
		return FALSE;

	pNodeList->Release();

	return TRUE;
}

LONG JobGetElementCountFromList(HXMLELEMENTLIST hElementList)
{
	IXMLDOMNodeList *pNodeList = (IXMLDOMNodeList *) hElementList;
	LONG count = 0;

	HRESULT hr = pNodeList->get_length(&count);

	if( FAILED(hr) )
	{
		return -1;
	}

	return count;
}

/*
 * nIndex is 0-based.
 */
HXMLELEMENT JobGetElementFromList(HXMLELEMENTLIST hElementList, LONG nIndex)
{
	if( nIndex < 0 )
		return NULL;

	LONG count = JobGetElementCountFromList(hElementList);

	if( count < 0 )
		return NULL;

	if( nIndex >= count )
		return NULL;

	IXMLDOMNodeList *pNodeList = (IXMLDOMNodeList *) hElementList;
	IXMLDOMNode *pNode = NULL;

	HRESULT hr = pNodeList->get_item(nIndex, &pNode);

	if( FAILED(hr) )
	{
		return NULL;
	}

	return (HXMLELEMENT)pNode;
}

//BOOL JobSaveXMLDocument(HXMLDOCUMENT hDocument, CString SFileName)
BOOL JobSaveXMLDocument(HXMLDOCUMENT hDocument, const OLECHAR * SFileName)
{
	bool bRet = TRUE;
   IXMLDOMDocument *pIXMLDOMDocument = NULL;
   pIXMLDOMDocument = (IXMLDOMDocument *) hDocument;
   BSTR FileName = ::SysAllocString(SFileName);
   VARIANT var;
   var.bstrVal = FileName;
   var.vt = VT_BSTR;

   //WriteToServicePackLog(var.bstrVal);
  
	//varFileName.;
   if(pIXMLDOMDocument)
   {
	   
	   pIXMLDOMDocument ->save( var );
   }
   else
   {
	   bRet = FALSE;
   }

   return bRet;
}	

//We have to create Processing Instructions for xml and xml-stylesheet
//BOOL JobCreateProcessingInstruction(HXMLDOCUMENT hDocument, CString sInstruction, CString sData)
BOOL JobCreateProcessingInstruction(HXMLDOCUMENT hDocument, const OLECHAR * sInstruction, const OLECHAR * sData)
{
	HRESULT hr;
	IXMLDOMProcessingInstruction *pIXMLDOMProcessingInstruction = NULL;
	IXMLDOMNode *PIXMLDomNode = NULL;
	BOOL bRet = TRUE;
	IXMLDOMDocument *pIXMLDOMDocument = NULL;
	pIXMLDOMDocument = (IXMLDOMDocument *) hDocument;

	//BSTR bstrInstruction = sInstruction.AllocSysString();
	BSTR bstrInstruction = ::SysAllocString(sInstruction);
	//BSTR bstrData		 = sData.AllocSysString();
	BSTR bstrData		 = ::SysAllocString(sData);

	_bstr_t bsInstruction(bstrInstruction, TRUE);
	_bstr_t bsData(bstrData, TRUE);
	
	pIXMLDOMDocument->createProcessingInstruction(bsInstruction, bsData, &pIXMLDOMProcessingInstruction);
	if(pIXMLDOMProcessingInstruction)
	{
		hr = pIXMLDOMDocument->appendChild(pIXMLDOMProcessingInstruction,  &PIXMLDomNode);
	}

	if(FAILED(hr))
	{
		bRet = FALSE;
	}	
	
	return bRet;

}
BOOL JobGetDocumentSizeInWCHARs(HXMLDOCUMENT hDocument,LPDWORD pdwBufferSizeInWCHARs)
{
	IXMLDOMDocument *pIXMLDOMDocument = NULL;
	BSTR bstrXML = NULL;
	DWORD dwLength;

	HRESULT hr;
	BOOL bRet = FALSE;

	pIXMLDOMDocument = (IXMLDOMDocument *)hDocument;

	if(!pIXMLDOMDocument)
	{
		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}
	
	hr = pIXMLDOMDocument->get_xml(&bstrXML);
	if(FAILED(hr))
	{
		if(bstrXML)
		{
			::SysFreeString(bstrXML);
			bstrXML = NULL;
		}

		*pdwBufferSizeInWCHARs = 0;
		return FALSE;
	}
	
	dwLength = wcslen(bstrXML) + 1;
	*pdwBufferSizeInWCHARs = dwLength;
	bRet = TRUE;
	return bRet;

}
int JSXReadXML(const wchar_t *pcwsFilename, wchar_t *pwsXMLBuffer, long	*plXMLBufSize)
{
	HANDLE hFile = INVALID_HANDLE_VALUE;
	int jsRet = JSERR_SUCCESS;
	wchar_t *pwsFileText = NULL;
	unsigned char* pszTemp = NULL;
	long lBufferSizeNeeded = 0;
	size_t iSkip = 0;
	wstring wsEncoding;

	do
	{
		if (pcwsFilename == NULL || *pcwsFilename == L'\0' || plXMLBufSize == NULL)
		{
			//theLogFile.LogMessage(JSLL_CRITICAL, TEXT("Invalid parameters for reading XML.."));
			jsRet = JSERR_INVALIDPARAM;
			break;
		}

		hFile = CreateFileW(pcwsFilename, GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
		if (hFile == INVALID_HANDLE_VALUE)
		{
			//theLogFile.LogMessage(JSLL_CRITICAL, TEXT("Create file %s failed(ErrCode = %d)."), pcwsFilename, GetLastError());
			jsRet = JSERR_FILE;
			break;
		}

		DWORD dwFileSize = GetFileSize(hFile, NULL);

		if ( dwFileSize <= 0 )
		{
			//theLogFile.LogMessage(JSLL_CRITICAL, TEXT("Get size of file %s failed (ErrCode = %d)."), pcwsFilename, GetLastError());
			jsRet = JSERR_FILE;
			break;
		}

		pwsFileText = (wchar_t*)malloc(dwFileSize + sizeof(wchar_t));
		if (pwsFileText == NULL)
		{
			//theLogFile.LogMessage(JSLL_CRITICAL, TEXT("No enough memory for reading XML."));
			jsRet = JSERR_OUTOFMEM;
			break;
		}

		ZeroMemory (pwsFileText, dwFileSize + sizeof(wchar_t));

		DWORD ReadSize = dwFileSize;
		if (!ReadFile(hFile, pwsFileText, dwFileSize, &ReadSize, NULL)
			|| dwFileSize != dwFileSize)
		{
			free( pwsFileText );
			pwsFileText = NULL;
			//theLogFile.LogMessage(JSLL_CRITICAL, TEXT("Read file %s failed(ErrCode = %d)."), pcwsFilename, GetLastError());
			jsRet = JSERR_FILE;
			break;
		}

		// process encoding
		unsigned char *pb = (unsigned char*)pwsFileText;
		if( 0xFF == *pb && 0xFE == *(pb+1) )
		{
			// it is UTF_16LE
			wsEncoding = L"UNICODE";
			iSkip = 1;
			lBufferSizeNeeded = dwFileSize;
		}
		else if( 0xFE == *pb && 0xFF == *(pb+1) )
		{
			// it is UTF_16BE
			wsEncoding = L"UNICODE_BE";
			iSkip = 1;
			lBufferSizeNeeded = dwFileSize;
			DWORD dwChars = dwFileSize >> 1;
			DWORD i;
			unsigned char bChar;
			for( i = 0;i < dwChars;i ++ )
			{
				bChar = *pb;
				*pb = *(pb+1);
				*(pb+1) = bChar;
				pb += 2;
			}
		}
		else if( 0xEF == *pb && 0xBB == *(pb+1) && 0xBF == *(pb+2) )
		{
			// It is UTF8
			wsEncoding = L"UTF-8";
			iSkip = 3;

			pszTemp = (unsigned char*)pwsFileText;
			int ret = MultiByteToWideChar(CP_UTF8, 0, (LPCSTR)(pszTemp + iSkip), -1, NULL, 0);
			if (ret > 0)
			{
				pwsFileText = (wchar_t*)malloc((ret + 1)*sizeof(wchar_t));
				if (pwsFileText == NULL)
				{
					//theLogFile.LogMessage(JSLL_CRITICAL, TEXT("No enough memory for processing UTF-8 encoding."));
					jsRet = JSERR_OUTOFMEM;
					break;
				}
				ZeroMemory(pwsFileText, (ret + 1)*sizeof(wchar_t));
				ret = MultiByteToWideChar(CP_UTF8, 0, (LPCSTR)(pszTemp + iSkip), -1, pwsFileText, ret);
				if (ret <= 0)
				{
					//theLogFile.LogMessage(JSLL_CRITICAL, TEXT("Convert UTF-8 XML file failed."));
					jsRet = JSERR_XML;
					break;
				}
				else
				{
					iSkip = 0;
					lBufferSizeNeeded = (ret + 1)*sizeof(wchar_t);
				}
			}
		}
		else
		{
			// it is MBCS
			wsEncoding = L"ANSI";
			iSkip = 0;

			pszTemp = (unsigned char*)pwsFileText;
			int ret = MultiByteToWideChar(CP_ACP, 0, (LPCSTR)(pszTemp), -1, NULL, 0);
			if (ret > 0)
			{
				pwsFileText = (wchar_t*)malloc((ret + 1)*sizeof(wchar_t));
				if (pwsFileText == NULL)
				{
					//theLogFile.LogMessage(JSLL_CRITICAL, TEXT("No enough memory for processing ANSI encoding."));
					jsRet = JSERR_OUTOFMEM;
					break;
				}
				ZeroMemory(pwsFileText, (ret + 1)*sizeof(wchar_t));
				ret = MultiByteToWideChar(CP_ACP, 0, (LPCSTR)(pszTemp), -1, pwsFileText, ret);
				if (ret <= 0)
				{
					//theLogFile.LogMessage(JSLL_CRITICAL, TEXT("Convert UTF-8 XML file failed."));
					jsRet = JSERR_XML;
					break;
				}
				else
				{
					lBufferSizeNeeded = (ret + 1)*sizeof(wchar_t);
				}
			}
		}

		//remove encoding info in xml
		wchar_t *pWChar = pwsFileText + iSkip + 5;
		if (!IsBadReadPtr(pWChar, 1))
		{
			wchar_t WChar = *pWChar;
			*(pwsFileText + iSkip + 5) = L'\0';
			if( 0 == wcscmp(L"<?xml", pwsFileText + iSkip ) )
			{
				wchar_t* tagEnd = wcschr( pWChar + 1, L'>');
				if( NULL != tagEnd )
				{
					tagEnd ++;
					while( L'\0' != *tagEnd && (L' ' == *tagEnd || L'\t' == *tagEnd || L'\r' == *tagEnd || L'\n' == *tagEnd ) )
					{
						tagEnd ++;
					}
					iSkip = tagEnd - pwsFileText;
				}
			}
			*pWChar = WChar;
		}
		

		// copy to user's buffer
		if ( pwsXMLBuffer == NULL || *plXMLBufSize < lBufferSizeNeeded)
		{
			*plXMLBufSize = lBufferSizeNeeded;
			//theLogFile.LogMessage(JSLL_WARNING, TEXT("Need more buffers for XML(Size: %d, Need: %d)."), *plXMLBufSize, dwFileSize);
			jsRet = JSERR_BUFTOOSMALL;
			break;
		}
		else
		{
			if (IsBadWritePtr(pwsXMLBuffer, *plXMLBufSize))
			{
				//theLogFile.LogMessage(JSLL_CRITICAL, TEXT("XML Binary unwritable.(Ptr: 0x%p, Size: %d)"), pwsXMLBuffer, *plXMLBufSize);
				jsRet = JSERR_BINARY;
				break;
			}
			
			ZeroMemory(pwsXMLBuffer, *plXMLBufSize);
			if (memcpy_s(pwsXMLBuffer, *plXMLBufSize, pwsFileText+iSkip, wcslen(pwsFileText+iSkip)*sizeof(wchar_t)) != 0)
			{
				//theLogFile.LogMessage(JSLL_CRITICAL, TEXT("Copy XML to buffer failed."));
				jsRet = JSERR_BINARY;
				break;
			}
			else
			{
				//theLogFile.LogMessage(JSLL_NOTE, TEXT("Read XML successfully(%s)."), wsEncoding.c_str());
				jsRet = JSERR_SUCCESS;
				break;
			}
		}
	}while(0);

	if( NULL != pszTemp )
	{
		free( pszTemp );
	}
	if (NULL != pwsFileText)
	{
		free( pwsFileText );
	}
	if( INVALID_HANDLE_VALUE != hFile )
	{
		CloseHandle(hFile);
	}

	return jsRet;
}