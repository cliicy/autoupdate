#pragma once

class CMemExDC : public CDC
{
private:	
	CBitmap m_bitmap;
	CBitmap* m_oldBitmap;
	CDC* m_pDC;
	CRect m_rect;
	BOOL m_bMemDC;
public:

	CMemExDC(CDC* pDC, const CRect* pRect = NULL) : CDC()
	{
		ASSERT(pDC != NULL); 

		m_pDC = pDC;
		m_oldBitmap = NULL;
		m_bMemDC = !pDC->IsPrinting();

		if (pRect == NULL)
		{
			pDC->GetClipBox(&m_rect);
		}
		else
		{
			m_rect = *pRect;
		}

		if (m_bMemDC)
		{
			CreateCompatibleDC(pDC);

			m_bitmap.CreateCompatibleBitmap(pDC, m_rect.Width(), m_rect.Height());
			m_oldBitmap = SelectObject(&m_bitmap);

			SetMapMode(pDC->GetMapMode());

			SetWindowOrg(m_rect.left, m_rect.top);
		}
		else
		{
			m_bPrinting = pDC->m_bPrinting;
			m_hDC = pDC->m_hDC;
			m_hAttribDC = pDC->m_hAttribDC;
		}

		FillSolidRect(m_rect, RGB(255, 255, 255));
	}

	~CMemExDC()	
	{		
		if (m_bMemDC)
		{
			m_pDC->BitBlt(m_rect.left, m_rect.top, m_rect.Width(), m_rect.Height(),
				this, m_rect.left, m_rect.top, SRCCOPY);

			SelectObject(m_oldBitmap);
		}
		else
		{
			m_hDC = m_hAttribDC = NULL;
		}	
	}

	CMemExDC* operator->()
	{
		return this;
	}	

	operator CMemExDC*()
	{
		return this;
	}
};