#pragma once
#include <string>
#include <vector>
#include <map>
/**
* Class Name: CXXmlNode
* Usage: This class is used to parse an XML file/string. Internally, it uses 
*        MSXML or XMLLite to parse an XML file/string.
*/
typedef enum _XXN_FLAG
{
    XXN_FIRST = 0, // Insert as or get the first child node
    XXN_LAST  = 1, // Insert as or get the last child node
    XXN_NEXT  = 2, // Insert as or get the next sibling node
    XXN_PREV  = 3, // Insert as or get the previous sibling node
    XXN_ROOT  = 4, // Get the root node
    XXN_PARENT= 5, // Get the parent node
}XXN_FLAG;

class CXXmlNode
{
public:
    virtual ~CXXmlNode();

public:
    // create an XML node with given tag.
    static CXXmlNode* CreateXmlNode( const std::wstring& strTagName = L"" );

    // create an XML form a specified XML file
    static CXXmlNode* LoadFromFile( const std::wstring& strXmlFile );

    // create an XML form a specified XML String
    static CXXmlNode* LoadFromString( const std::wstring& strXml );

    static LONG GetLastError( );

    static void SetLastError( LONG lErr );

public:

    // convert an XML node to a string
    std::wstring ToString( );

    // Save an XML node to a specified file. 0 is success.
    DWORD SaveToFile( const std::wstring& strXmlFile );
	DWORD SaveToFile2( const std::wstring& strXmlFile );

    // Get the node text.
    std::wstring GetText( ) const;

    // get the node tag
    std::wstring GetTag( ) const;

    // set node text
    void SetText( const std::wstring& strXmlText );

    // set node tag
    BOOL SetTag( const std::wstring& strXmlTag );

    // detect if the node has specified attribute
    BOOL HasAttribute( const std::wstring& strAttrib );

    // get the value of a specified attribute name
    std::wstring GetAttribute( const std::wstring& strAttrib, const std::wstring& strDefault=L"" ) const;

    // get the value of a specified attribute name
    int GetAttributeInt( const std::wstring& strAttrib, int nDefault=0 ) const;

    // get the value of a specified attribute name
    DWORD GetAttributeDWORD( const std::wstring& strAttrib, DWORD dwDefault=0 ) const;

    // get the value of a specified attribute name
    LONGLONG GetAttributeLongLong( const std::wstring& strAttrib, LONGLONG llDefault=0 ) const;

    // set the attribute
    GUID GetAttributeGUID( const std::wstring& strAttrib ) const;

    // set the attribute
    BOOL SetAttribute( const std::wstring& strAttrib, const std::wstring& strValue );

    // set the attribute
    BOOL SetAttributeInt( const std::wstring& strAttrib, int nValue );

    // set the attribute
    BOOL SetAttributeDWORD( const std::wstring& strAttrib, DWORD dwValue );

    // set the attribute
    BOOL SetAttributeLongLong( const std::wstring& strAttrib, LONGLONG llValue );

    // set the attribute
    BOOL SetAttributeGUID( const std::wstring& strAttrib, GUID guid );

    // get all attributes name
    void GetAllAttributes( std::vector<std::wstring>& vecAttributes );

    // delete a specified attribute
    void DeleteAttribute( const std::wstring& strAttrib );

    // delete all attributes
    void CleanAllAttributes(  );

    // get a node from node tree, like child/previos/next/parent node
    CXXmlNode* GetNode(  XXN_FLAG flag ) const;

    // get all child nodes which has specified tag
    CXXmlNode* GetChildNode( const std::wstring& strTag  );

    // get all child nodes
    void GetAllChildNodes( std::vector<CXXmlNode*>& vecChilds  );

    // get the first node by specified path, like \root\child1\child2\...
    CXXmlNode* GetNodeByPath( const std::wstring& strXmlPath  );

    // search a node under specified path
    CXXmlNode* SearchNode( const std::wstring& strXmlPath, const std::wstring& strAttr, const std::wstring& strValue, BOOL bCaseSensitive );

    // insert a node by specified flag, insert a child/next/previous ndoe
    void InsertNode( CXXmlNode* pNode, XXN_FLAG flag );

    // dettach a node from node tree.
    CXXmlNode* DettachNode( );

    // delete all child nodes
    void CleanChildNodes( );

    // compare two nodes
    BOOL IsEqualNode( CXXmlNode* pNode );

    // clone a new node
    CXXmlNode* Clone(  );

protected:
    /**
    *  Construct an object with the given tage name.
    *  NOTE: 
    *     DO NOT support tag name which contains SPACE characters
    */
    CXXmlNode( const std::wstring& strTagName );

protected:
    std::wstring _child2str( int nDepth );

    CXXmlNode*  _getRoot( ) const;

    CXXmlNode*	_getLastChild(  ) const;

    void		_insertAsFirstChild( CXXmlNode* pChild );

    void		_insertAsLastChild( CXXmlNode* pChild );

    void		_insertAsNextNode( CXXmlNode* pNode );

    void		_insertAsPreviousNode( CXXmlNode* pNode );

protected:
    std::wstring m_strXmlText;
    std::wstring m_strTagName;
    std::map<std::wstring,std::wstring> m_xmlAttributes;

    CXXmlNode* m_pParent;
    CXXmlNode* m_pNext;
    CXXmlNode* m_pPrevious;
    CXXmlNode* m_pFirstChild;
protected:
    static LONG   m_lLastError;
};

/*
*   Error Define
*/

// Invalid Tag
#define E_INVALID_TAG_NAME	-100

// Invalid attribute name
#define E_INVALID_ATTRIB_NAME	-101

// Invalid attribute value
#define E_INVALID_ATTRIB_VALUE	-102