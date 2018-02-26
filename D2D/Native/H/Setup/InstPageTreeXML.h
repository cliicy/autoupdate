#pragma once

namespace AS_SETUP_GUI_PROCESSTREE
{


class CPageTreeNode
{
public:
	CPageTreeNode();
	CPageTreeNode( const CPageTreeNode& TreeNode );
	~CPageTreeNode();
	const CPageTreeNode& operator = ( const CPageTreeNode& srcTreeNode );

public:
	CString GetDisplayName() const { return m_strDisplayName; }
	void SetDisplayName( LPCTSTR strDisplayName ){ m_strDisplayName = strDisplayName; }

	DWORD GetPageID() const { return m_dwPageID; }
	void SetPageID( const DWORD dwPageID ){ m_dwPageID = dwPageID; }

	DWORD GetParentPageID() const { return m_dwParentPageID; }
	void SetParentPageID( const DWORD dwParentPageID ){ m_dwParentPageID = dwParentPageID; }

	DWORD GetPageIndex() const { return m_dwIndexCompareParent; }
	void SetPageIndex( const DWORD dwPageIndex ){ m_dwIndexCompareParent = dwPageIndex; }

	void Clearup()
	{
		m_strDisplayName.Empty();
		m_dwPageID = 0;
		m_dwParentPageID = 0;
		m_dwIndexCompareParent = 0;
	}

protected:

	DWORD m_dwPageID;
	DWORD m_dwParentPageID;
	DWORD m_dwIndexCompareParent;
	CString m_strDisplayName;
};

class CInstallPageTree
{
public:
	CInstallPageTree();
	CInstallPageTree( const CInstallPageTree& PageTree );
	const CInstallPageTree& operator = ( const CInstallPageTree& Other );
	~CInstallPageTree();

public:
	DWORD InsertPageNode( const DWORD dwParenetPageID, const DWORD dwPageID, const DWORD dwIndex, LPCTSTR strDisplayName );
	DWORD InsertPageNode( const CPageTreeNode& PageNode );
	DWORD SortTreeNode( CArray< CPageTreeNode, CPageTreeNode >& TreeNodeArray );

	DWORD CreateTreeByTreeCtrl( CTreeCtrl* pTreeCtrl, HTREEITEM hRoot );
	DWORD CreateTreeCtrlByTree( CTreeCtrl* pTreeCtrl, HTREEITEM hRoot );
	void AppendPageTree( const CInstallPageTree& OtherPageTree );
	DWORD GetItemIndexCompareSiblingNode( CTreeCtrl* pTreeCtrl, HTREEITEM hCurrent );

	void Clearup();
	CArray< CPageTreeNode, CPageTreeNode >* GetArrayPointer(){ return &m_PageTree; }
	DWORD WriteTreeDataToXMLDOM( IXMLDOMNode* pRootNode );
	DWORD LoadTreeDataFromXMLDOM( IXMLDOMNode* pRootNode );

protected:
	DWORD FillTreeItemToNode( IXMLDOMNode* pItemNode, const CPageTreeNode& TreeNode );
	DWORD GetTreeItemDataFromNode( IXMLDOMNode* pItemNode, CPageTreeNode& TreeNode );
	DWORD GetNodeArray_ByParentID( const DWORD dwParentID, CArray< CPageTreeNode, CPageTreeNode >& NodeArray );
	DWORD GetNodeInfoFromTreeItem( CPageTreeNode& TempNode, CTreeCtrl* pTreeCtrl, HTREEITEM hItem );


protected:
	CArray< CPageTreeNode, CPageTreeNode > m_PageTree;
};

class CInstPageTreeXML
{
public:
	CInstPageTreeXML(void);
	~CInstPageTreeXML(void);

public:
	CString GetXMLFilePath() const { return m_strXMLFilePath; }
	void SetXMLFilePath( LPCTSTR strXMLFilePath ){ m_strXMLFilePath = strXMLFilePath; }

public:
	BOOL ValidateXMLFile();
	DWORD ReadTreeData( CInstallPageTree& PageTree );
	DWORD WriteTreeData( CInstallPageTree& PageTree );


protected:
	CString m_strXMLFilePath;
};

}
DWORD ExpendAllItems( CTreeCtrl* pTreeCtrl, const HTREEITEM hItemRoot );
DWORD SetCurPageOnTree( CTreeCtrl* pTreeCtrl,  HTREEITEM hItemRoot, const DWORD dwPageID, BOOL& BeforeCurPage );

