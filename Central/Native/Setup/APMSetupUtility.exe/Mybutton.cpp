#include "StdAfx.h"

#include "MyButton.h"

CMyButton::CMyButton(void)

{

  m_DownColor = m_UpColor = RGB(0,0,0);

}

CMyButton::~CMyButton(void)

{

}

 

BOOL CMyButton::Attach(const UINT nID,CWnd* pParent)

{

    if (!SubclassDlgItem(nID, pParent))

    return FALSE;

    return TRUE;

}

void CMyButton::SetDownColor(COLORREF color)

{

    m_DownColor = color;

}

void CMyButton::SetUpColor(COLORREF color)

{

    m_UpColor = color;

}

void CMyButton::DrawItem(LPDRAWITEMSTRUCT lpDrawItemStruct)
{

   CDC dc;

   dc.Attach(lpDrawItemStruct->hDC);

    VERIFY(lpDrawItemStruct->CtlType==ODT_BUTTON);

    const int bufSize = 512;

    TCHAR buffer[bufSize];

    GetWindowText(buffer, bufSize);

   int size=_countof(buffer);

   DrawText(lpDrawItemStruct->hDC,buffer,size,&lpDrawItemStruct->rcItem,DT_CENTER|DT_VCENTER|DT_SINGLELINE|DT_TABSTOP);

   SetBkMode(lpDrawItemStruct->hDC,TRANSPARENT);

   if (lpDrawItemStruct->itemState&ODS_SELECTED)

   {

         CBrush brush(m_DownColor);

          dc.FillRect(&(lpDrawItemStruct->rcItem),&brush);


         DrawText(lpDrawItemStruct->hDC,buffer,size,&lpDrawItemStruct->rcItem,DT_CENTER|DT_VCENTER|DT_SINGLELINE|DT_TABSTOP);

          SetBkMode(lpDrawItemStruct->hDC,TRANSPARENT);

    }

  else

    {

           CBrush brush(m_UpColor);

            dc.FillRect(&(lpDrawItemStruct->rcItem),&brush);//

            DrawText(lpDrawItemStruct->hDC,buffer,size,&lpDrawItemStruct->rcItem,DT_CENTER|DT_VCENTER|DT_SINGLELINE|DT_TABSTOP);

            SetBkMode(lpDrawItemStruct->hDC,TRANSPARENT);

     }

    if ((lpDrawItemStruct->itemState&ODS_SELECTED)&&(lpDrawItemStruct->itemAction &(ODA_SELECT|ODA_DRAWENTIRE)))

     {

               COLORREF fc=RGB(255-GetRValue(m_UpColor),255-GetGValue(m_UpColor),255-GetBValue(m_UpColor));

             CBrush brush(fc);

            dc.FrameRect(&(lpDrawItemStruct->rcItem),&brush);

       }

     if (!(lpDrawItemStruct->itemState &ODS_SELECTED) &&(lpDrawItemStruct->itemAction & ODA_SELECT))

         {

          CBrush brush(m_UpColor); 

         dc.FrameRect(&lpDrawItemStruct->rcItem,&brush);//}

        dc.Detach();

}
}

