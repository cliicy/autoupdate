
/*
 *	The export header file for JSXConvert module.
 *
 *	Important : 
 *	The JSXConvert library is a single-thread supported module, 
 *	so please make sure that there aren't multi threads calling the export functions at the same time.
 */

#pragma once

#ifndef _JSXMLCONVERSION_H_
#define _JSXMLCONVERSION_H_

//#include "stdafx.h"	

#ifdef __cplusplus
extern "C"
{
#endif

// Error code.
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

// XML Encoding
enum JSEncoding
{
	JSE_UNICODE_LE			= 0,
	JSE_UNICODE_BE			= 1,
	JSE_UTF8				= 2,
	JSE_ANSI				= 3
};

#define JS_UNICODE_PRECODE		0xFEFF

typedef void* JSHANDLE;

/*
 *	Get the last error message information.
 */
void JSXGetLastError(
	wchar_t *pwsError, 
	long	lBufSize
	);

/*
*	Read XML file to memory.
*	pcwsFilename - [in] file name of the XML file.
*	pwsXMLBuffer, plXMLBufSize - [out] the XML description text buffer.
*     If pwsXMLBuffer is NULL or plXMLBufSize is smaller than needed, it returns JSERR_BUFTOOSMALL and set plXMLBufSize to be the buffer size needed.
*/
int JSXReadXML(
	const wchar_t *pcwsFilename,
	wchar_t *pwsXMLBuffer,
	long	*plXMLBufSize
	);

/*
*	Write XML text to file.
*	pcwsFilename - [in] file name of the XML file.
*	pcwsXMLBuffer - [in] the XML description text buffer.
*	iEncoding - [in] encoding of XML file.
*/
int JSXWriteXML(
			   const wchar_t *pcwsFilename,
			   const wchar_t *pcwsXMLBuffer,
			   const int	iEncoding
			   );

/*
 *	Convert a job script binary buffer to XML description text.
 *	pBinary, lBinarySize - [in] the job script buffer, which should be loaded by ASLoadQueueJobFromScriptW().
 *	pwsXMLBuffer, plXMLBufSize - [out] the XML description text buffer.
 *	fPrecode - [in] indicate to insert UNICODE PRECODE at the beginning of XML text, or not.
 */
int JSXBinaryToXML_Text(
	HMODULE hMod,
	INT resIDPdbXml,
	INT resIDRuleXml,
	const wchar_t *pwsRootStructName, 
	void		*pBinary,
	long		lBinarySize,
	wchar_t		*pwsXMLBuffer,
	long		*plXMLBufSize,
	int		fPrecode
	);

/*
 *	Convert a job script binary buffer to a XML description file.
 */
int JSXBinaryToXML_File(
	HMODULE hMod,
	INT resIDPdbXml,
	INT resIDRuleXml,
	const wchar_t *pwsRootStructName, 
	void			*pBinary,
	long			lBinarySize,
	const wchar_t	*pcwsFilename,
	int			fPrecode
	);

/*
 *	Convert a XML description text to a job script binary buffer.
 *	pcwsJSXML - [in] the job script XML description string.
 *	pJSHandle - [out] save the buffer handle, use JSXHandleToBuffer() to convert this handle to buffer pointer.
 */
int JSXXMLToBinary(
	HMODULE hMod,
	INT resIDPdbXml,
	INT resIDRuleXml,
	const wchar_t *pwsRootStructName, 
	const wchar_t	*pcwsJSXML,
	JSHANDLE		*pJSHandle
	);

/*
 *	Convert a JSHandle to buffer pointer, and the buffer size. 
 *	If the handle is freed by JSXFreeJSHandle, the buffer pointer will be invalid.
 */
int JSXHandleToBuffer(
	JSHANDLE	jsHandle, 
	void		**ppBuffer, 
	long		*plBufSize
	);

/*
 *	Free a JSHandle, which is created by JSXXMLToBinary().
 */
int JSXFreeJSHandle(
	JSHANDLE	jsHandle
	);

/*
 * Check the XML structure description is correct or not
 * hMod: the module contains the XML files
 * restIDPdbXml: resource id of XML struction description
 * resIDRuleXml: resource id of rule description
 * pszStructName: structure name
 * cbStructSize: size of structure
 * pcbStructSizeInXML: size of structure in XML description
 * Return code: =0 ok; other: the size is different that xml description must be out of date!
*/
int JSXVerifyStructSize( IN HMODULE hMod, IN INT resIDPdbXml, IN INT resIDRuleXml, IN const wchar_t* pszStructName, IN size_t cbStructSize, OUT size_t* pcbStructSizeInXML );

#ifdef __cplusplus
}
#endif

#endif