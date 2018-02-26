#pragma once

#include <vector>
#include <drcommonlib.h>
using namespace std;

typedef struct _XML_NODE_ATTRIBUTE
{
   std::wstring strName;
   std::wstring strValue;
}XML_NODE_ATTRIBUTE,*PXML_NODE_ATTRIBUTE;

typedef struct _XML_NODE
{
   std::wstring strName;
   std::wstring strValue;
   std::vector<XML_NODE_ATTRIBUTE> vAttrList;
   std::vector<struct _XML_NODE> vChildren;
}XML_NODE,*PXML_NODE;

typedef std::vector<XML_NODE> VXMLNODE;
typedef std::vector<XML_NODE_ATTRIBUTE> VXMLNODEATTR;


class IXmlTree
{
public:
	virtual XML_NODE *GetRoot() = 0;
	virtual XML_NODE *GetNode( const wstring &strName ) = 0;
	virtual XML_NODE *GetParent( const wstring &strName ) = 0;
	virtual DWORD GetNodes( const wstring &strName, VXMLNODE &vList ) = 0;
	virtual DWORD CreateRoot( const XML_NODE &node ) = 0;
	virtual DWORD AddNode( const wstring &strName, const XML_NODE &node ) = 0;
	virtual DWORD AddNode ( XML_NODE &parent,const XML_NODE &node ) = 0;
	virtual DWORD AddNode ( const XML_NODE &node ) = 0;
	virtual DWORD DeleteNode( const wstring &strName ) = 0;
	virtual void DeleteNode ( XML_NODE &node ) = 0;
	virtual void NodeClear( XML_NODE &node ) = 0;
	virtual void AttributeClear( XML_NODE_ATTRIBUTE &attr ) = 0;
	virtual DWORD CheckNodeExist( LPCWSTR pName,BOOL &bExist ) = 0;
	virtual DWORD MergeNode( XML_NODE &ori, XML_NODE &dest ) = 0;
};


class IXmlParser
{
public:
	virtual DWORD InitializeParser( LPCWSTR pFile) = 0;
	virtual void UnInitializeParser() = 0;
    virtual void Release() = 0;
	virtual DWORD SetRoot( XML_NODE &node ) = 0;
	virtual DWORD GetRoot( LPCWSTR lpNodeName, XML_NODE &node ) = 0;
	virtual DWORD SaveNode( LPCWSTR pName, const XML_NODE &node ) = 0;
	virtual DWORD SaveNode( XML_NODE &parent, const XML_NODE &child ) = 0;
	virtual DWORD SaveFile( ) = 0;
	virtual DWORD GetNodes( LPCWSTR pName, VXMLNODE &vList ) = 0;
	virtual XML_NODE *GetNode( LPCWSTR pName ) = 0;
	virtual IXmlTree &GetTree() = 0;
};

#ifdef __cplusplus
extern "C" {
#endif

void XmlCopyNode(const XML_NODE &ori, XML_NODE &dest);
IXmlParser * XmlGetParser();

#ifdef __cplusplus
}
#endif

DWORD WINAPI CreateIXmlParser(IXmlParser **ppIXMLParser);