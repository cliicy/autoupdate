#pragma once

#include <windows.h>
#include <tchar.h>

#define FLAG_END	0x100
#define STACK_SIZE		8

#define FV_BOLD        0x01
#define FV_ITALIC      (FV_BOLD << 1)
#define FV_UNDERLINE   (FV_ITALIC << 1)
#define FV_SUPERSCRIPT (FV_UNDERLINE << 1)
#define FV_SUBSCRIPT   (FV_SUPERSCRIPT << 1)
#define FV_NUMBER      (FV_SUBSCRIPT << 1)

enum { tNONE, tB, tBR, tFONT, tI, tP, tSUB, tSUP, tU, tNUMTAGS };

struct
{
	TCHAR *pMnemonic;
	short token, param, block;
}

g_Tags[] =
{
	{ NULL,         tNONE, 0, 0},
	{ _T("b"),      tB,    0, 0},
	{ _T("br"),     tBR,   0, 1},
	{ _T("em"),     tI,    0, 0},
	{ _T("font"),   tFONT, 1, 0},
	{ _T("i"),      tI,    0, 0},
	{ _T("p"),      tP,    0, 1},
	{ _T("strong"), tB,    0, 0},
	{ _T("sub"),    tSUB,  0, 0},
	{ _T("sup"),    tSUP,  0, 0},
	{ _T("u"),      tU,    0, 0},
};

static COLORREF g_stack[STACK_SIZE];
static int g_nStackTop;

static int GetMessageToken(LPCTSTR *lpctString, int *nSize, int *nTokenLength, BOOL *nWhiteSpace)
{
	LPCTSTR nStart, nEndToken;
	int nLength, nEntryWhiteSpace, nIndex, nIsEndTag;

	nStart = *lpctString;

	if (nWhiteSpace != NULL)
	{
		nEntryWhiteSpace = *nWhiteSpace;
		*nWhiteSpace = nEntryWhiteSpace || _istspace(*nStart);
	}
	else
	{
		nEntryWhiteSpace = FALSE;
	}

	while (*nSize > 0 && _istspace(*nStart))
	{
		nStart++;
		*nSize -= 1;
	}

	if (*nSize <= 0)
		return -1;

	nEndToken = nStart;
	nLength = 0;
	nIsEndTag = 0;

	if (*nEndToken == _T('<'))
	{
		nEndToken++;
		nLength++;
		if (nLength < *nSize && *nEndToken == _T('/'))
		{
			nIsEndTag = FLAG_END;
			nEndToken++;
			nLength++;
		}

		while (nLength < *nSize && !_istspace(*nEndToken)
			&& *nEndToken != _T('<') && *nEndToken != _T('>'))
		{
			nEndToken++;
			nLength++;
		}

		for (nIndex = sizeof(g_Tags) / sizeof(g_Tags[0]) - 1; nIndex > 0; nIndex--)
		{
			if (!_tcsnicmp(nStart + (nIsEndTag ? 2 : 1), g_Tags[nIndex].pMnemonic,
				_tcslen(g_Tags[nIndex].pMnemonic)))
				break;
		}

		if (nIndex > 0)
		{
			if (g_Tags[nIndex].param && !nIsEndTag)
			{
				while (nLength < *nSize
					&& *nEndToken != _T('<') && *nEndToken != _T('>'))
				{
					nEndToken++;
					nLength++;
				}
			}
			else if (*nEndToken != _T('>'))
			{
				nIndex = 0;
			}

			if (nWhiteSpace != NULL && g_Tags[nIndex].block)
				*nWhiteSpace = FALSE;
		}

		if (*nEndToken == _T('>'))
		{
			nEndToken++;
			nLength++;
		}

		if (nIndex > 0 && (g_Tags[nIndex].block || nEntryWhiteSpace))
		{
			while (nLength < *nSize && _istspace(*nEndToken))
			{
				nEndToken++;
				nLength++;
			}
		}
	}
	else
	{
		nIndex = 0;
		while (nLength < *nSize && !_istspace(*nEndToken) && *nEndToken != _T('<'))
		{
			nEndToken++;
			nLength++;
		}
	}

	if (nTokenLength != NULL)
		*nTokenLength = nLength;

	*nSize -= nLength;
	*lpctString = nStart;

	return g_Tags[nIndex].token | nIsEndTag;
}

static int Hex2Digit(TCHAR ch)
{
	if (ch >= _T('0') && ch <= _T('9'))
		return ch - _T('0');

	if (ch >= _T('A') && ch <= _T('F'))
		return ch - _T('A') + 10;

	if (ch >= _T('a') && ch <= _T('f'))
		return ch - _T('a') + 10;

	return 0;
}

static COLORREF ParseMessageColor(LPCTSTR String)
{
	int Red, Green, Blue;

	if (*String == _T('\'') || *String == _T('"'))
		String++;

	if (*String == _T('#'))
		String++;

	Red = (Hex2Digit(String[0]) << 4) | Hex2Digit(String[1]);
	Green = (Hex2Digit(String[2]) << 4) | Hex2Digit(String[3]);
	Blue  = (Hex2Digit(String[4]) << 4) | Hex2Digit(String[5]);

	return RGB(Red, Green, Blue);
}

static BOOL PushMessageColor(HDC hdc, COLORREF clr)
{
	if (g_nStackTop < STACK_SIZE)
		g_stack[g_nStackTop++] = GetTextColor(hdc);

	SetTextColor(hdc, clr);

	return TRUE;
}

static BOOL PopMessageColor(HDC hdc)
{
	COLORREF clr;
	BOOL okay = (g_nStackTop > 0);

	if (okay)
		clr = g_stack[--g_nStackTop];
	else
		clr = g_stack[0];

	SetTextColor(hdc, clr);

	return okay;
}

static HFONT GetMessageFontVariant(HDC hdc, HFONT hfontSource, int Styles)
{
	LOGFONT logFont = { 0 };

	SelectObject(hdc, (HFONT)GetStockObject(SYSTEM_FONT));

	if (!GetObject(hfontSource, sizeof(logFont), &logFont))
		return NULL;

	logFont.lfWeight = (Styles & FV_BOLD) ? FW_BOLD : FW_NORMAL;
	logFont.lfItalic = (BYTE)(Styles & FV_ITALIC) != 0;
	logFont.lfUnderline = (BYTE)(Styles & FV_UNDERLINE) != 0;

	if (Styles & (FV_SUPERSCRIPT | FV_SUBSCRIPT))
		logFont.lfHeight = logFont.lfHeight * 7 / 10;

	return CreateFontIndirect(&logFont);
}

#if defined __cplusplus
extern "C"
#endif
int __stdcall DrawMessage(
						  HDC     hdc,
						  LPCTSTR lpString,
						  INT_PTR nCount,
						  LPRECT  lpRect,
						  UINT    uFormat
					   )
{
	LPCTSTR lpctStart;
	int nLeft, nTop, nMaxWidth, nMinWidth, nHeight, nSavedDC, nTag, nTokenLength;
	int nStyles, nCurStyles, nIndex, nLineHeight, nWidthOfSPace, nXPos;
	BOOL bWhiteSpace;
	RECT rc;
	SIZE size;
	POINT CurPos;
	HFONT hfontBase, hfontSpecial[FV_NUMBER];
	LPCTSTR lpctColorFlag = _T("color=");

	if (hdc == NULL || lpString == NULL)
		return 0;

	if (nCount < 0)
		nCount = _tcslen(lpString);

	if (lpRect != NULL)
	{
		nLeft = lpRect->left;
		nTop = lpRect->top;
		nMaxWidth = lpRect->right - lpRect->left;
	}
	else
	{
		GetCurrentPositionEx(hdc, &CurPos);
		nLeft = CurPos.x;
		nTop = CurPos.y;
		nMaxWidth = GetDeviceCaps(hdc, HORZRES) - nLeft;
	}

	if (nMaxWidth < 0)
		nMaxWidth = 0;

	uFormat &= ~(DT_CENTER | DT_RIGHT | DT_TABSTOP);
	uFormat |= (DT_LEFT | DT_NOPREFIX);

	nSavedDC = SaveDC(hdc);
	hfontBase = SelectObject(hdc, (HFONT)GetStockObject(SYSTEM_FONT));
	SelectObject(hdc, hfontBase);

	for (nIndex = 0; nIndex < FV_NUMBER; nIndex++)
		hfontSpecial[nIndex] = NULL;

	hfontSpecial[0] = hfontBase;
	nStyles = 0;

	GetTextExtentPoint32(hdc, _T("Åy"), 2, &size);
	nLineHeight = size.cy;

	nXPos = 0;
	nMinWidth = 0;
	g_nStackTop = 0;
	nCurStyles = -1;
	nHeight = 0;
	bWhiteSpace = FALSE;

	lpctStart = lpString;
	for ( ;; )
	{
		nTag = GetMessageToken(&lpctStart, &nCount, &nTokenLength, &bWhiteSpace);
		if (nTag < 0)
			break;

		switch (nTag & ~FLAG_END)
		{
		case tP:
			if ((nTag & FLAG_END) == 0 && (uFormat & DT_SINGLELINE) == 0)
			{
				if (lpctStart != lpString)
					nHeight += 3 * nLineHeight / 2;
				nXPos = 0;
			}
			break;
		case tBR:
			if ((nTag & FLAG_END) == 0 && (uFormat & DT_SINGLELINE) == 0)
			{
				nHeight += nLineHeight;
				nXPos = 0;
			}
			break;
		case tB:
			nStyles = (nTag & FLAG_END) ? nStyles & ~FV_BOLD : nStyles | FV_BOLD;
			break;
		case tI:
			nStyles = (nTag & FLAG_END) ? nStyles & ~FV_ITALIC : nStyles | FV_ITALIC;
			break;
		case tU:
			nStyles = (nTag & FLAG_END) ? nStyles & ~FV_UNDERLINE : nStyles | FV_UNDERLINE;
			break;
		case tSUB:
			nStyles = (nTag & FLAG_END) ? nStyles & ~FV_SUBSCRIPT : nStyles | FV_SUBSCRIPT;
			break;
		case tSUP:
			nStyles = (nTag & FLAG_END) ? nStyles & ~FV_SUPERSCRIPT : nStyles | FV_SUPERSCRIPT;
			break;
		case tFONT:
			if ((nTag & FLAG_END) == 0)
			{
				if (_tcsnicmp(lpctStart + _tcslen(lpctColorFlag), lpctColorFlag, _tcslen(lpctColorFlag)) == 0)
					PushMessageColor(hdc, ParseMessageColor(lpctStart + 12));
			}
			else
			{
				PopMessageColor(hdc);
			}

			break;
		default:
			if (nTag == (tNONE | FLAG_END))
				break;

			if (nCurStyles != nStyles)
			{
				if (hfontSpecial[nStyles] == NULL)
					hfontSpecial[nStyles] = GetMessageFontVariant(hdc, hfontBase, nStyles);

				nCurStyles = nStyles;
				SelectObject(hdc, hfontSpecial[nStyles]);

				GetTextExtentPoint32(hdc, _T(" "), 1, &size);
				nWidthOfSPace = size.cx;
			}

			GetTextExtentPoint32(hdc, lpctStart, nTokenLength, &size);
			if (size.cx > nMaxWidth)
				nMaxWidth = size.cx;

			if (bWhiteSpace)
				nXPos += nWidthOfSPace;

			if (nXPos + size.cx > nMaxWidth && bWhiteSpace)
			{
				if ((uFormat & DT_WORDBREAK) != 0)
				{
					nHeight += nLineHeight;
					nXPos = 0;
				}
				else
				{
					nMaxWidth = nXPos + size.cx;
				}
			}

			if ((uFormat & DT_CALCRECT) == 0)
			{
				SetRect(&rc, nLeft + nXPos, nTop + nHeight, nLeft + nMaxWidth, nTop + nHeight + nLineHeight);

				DrawText(hdc, lpctStart, nTokenLength, &rc,
					uFormat | ((nStyles & FV_SUBSCRIPT) ? DT_BOTTOM | DT_SINGLELINE : 0));

				if (bWhiteSpace && (nStyles & FV_UNDERLINE) && nXPos >= nWidthOfSPace) 
				{
					if (nTop < 0)
					{
						SetRect(&rc, nLeft + nXPos - nWidthOfSPace, nTop - nHeight,
							nLeft + nXPos, nTop - (nHeight + nLineHeight));
					}
					else
					{
						SetRect(&rc, nLeft + nXPos - nWidthOfSPace, nTop + nHeight,
							nLeft + nXPos, nTop + nHeight + nLineHeight);
					}
					DrawText(hdc, _T(" "), 1, &rc, uFormat);
				}
			}

			nXPos += size.cx;
			if (nXPos > nMinWidth)
				nMinWidth = nXPos;
			bWhiteSpace = FALSE;
		}

		lpctStart += nTokenLength;
	}

	RestoreDC(hdc, nSavedDC);

	for (nIndex = 1; nIndex < FV_NUMBER; nIndex++)
	{
		if (hfontSpecial[nIndex] != NULL)
			DeleteObject(hfontSpecial[nIndex]);
	}

	if ((uFormat & DT_CALCRECT) != 0 && lpRect != NULL)
	{
		lpRect->right = lpRect->left + nMinWidth;
		if (lpRect->top < 0)
			lpRect->bottom = lpRect->top - (nHeight + nLineHeight);
		else
			lpRect->bottom = lpRect->top + nHeight + nLineHeight;
	}

	return nHeight;
}