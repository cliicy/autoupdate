#pragma once

#include <time.h>
#include <string>
using namespace std;

/*
==> A leap second is a one-second adjustment that is occasionally applied to Coordinated Universal Time (UTC) in order to keep its time of day close to the mean solar time.
=> A positive leap second is inserted between second 23:59:59 of a chosen UTC calendar date (the last day of a month, usually June 30 or December 31)
and second 00:00:00 of the following date. This extra second is displayed on UTC clocks as 23:59:60. On clocks that display local time tied to UTC,
the leap second may be inserted at the end of some other hour (or half-hour or quarter-hour), depending on the local time zone.
=> A negative leap second would suppress second 23:59:59 of the last day of a chosen month, so that second 23:59:58 of that date would be followed
immediately by second 00:00:00 of the following date. However, since the UTC standard was established, negative leap seconds have never been needed.

==> UTC Time: (Coordinated Universal Time)
==> Calendar Time: The number of seconds intervals since a standard time point, such as 1970.1.1 00:00:00
struct tm {
int tm_sec;     //ZZ: seconds after the minute - [0,60] tm_sec is generally 0-59. The extra range is to accommodate for leap seconds in certain systems.
int tm_min;     //ZZ: minutes after the hour - [0,59]
int tm_hour;    //ZZ: hours since midnight - [0,23]
int tm_mday;    //ZZ: day of the month - [1,31]
int tm_mon;     //ZZ: months since January - [0,11]
int tm_year;    //ZZ: years since 1900
int tm_wday;    //ZZ: days since Sunday - [0,6]
int tm_yday;    //ZZ: days since January 1 - [0,365]
int tm_isdst;   //ZZ: daylight savings time flag
};

typedef struct _SYSTEMTIME {
WORD wYear;          //ZZ: The year. The valid values for this member are 1601 through 30827.
WORD wMonth;         //ZZ: The month. This valid values for this member are 1(January) through 12(December)
WORD wDayOfWeek;     //ZZ: The day of the week. This valid values for this member are 0(Sunday) through 6(Saturday)
WORD wDay;           //ZZ: The day of the month. The valid values for this member are 1 through 31.
WORD wHour;          //ZZ: The hour. The valid values for this member are 0 through 23.
WORD wMinute;        //ZZ: The minute. The valid values for this member are 0 through 59.
WORD wSecond;        //ZZ: The second. The valid values for this member are 0 through 59.
WORD wMilliseconds;  //ZZ: The millisecond. The valid values for this member are 0 through 999.
} SYSTEMTIME, *PSYSTEMTIME, *LPSYSTEMTIME;

========================================================================================================

==> File Time
Contains a 64-bit value representing the number of 100-nanosecond intervals since January 1, 1601 (UTC).
typedef struct _FILETIME
{
DWORD dwLowDateTime;
DWORD dwHighDateTime;
} FILETIME, FAR * LPFILETIME, *PFILETIME;

==> The number of seconds elapsed since 00:00 hours, Jan 1, 1970 UTC.
time_t(__time64_t / __time32_t)

==> Dos time - use 16 bits value to indicate date and another 16 bits to indicate time.
wFatDate(16 bits): The MS-DOS date. The date is a packed value with the following format.
[0-4 bit]  Day of the month (1¨C31)
[5-8 bit]  Month (1 = January, 2 = February, and so on)
[9-15 bit] Year offset from 1980 (add 1980 to get actual year)

==> wFatTime(16 bits): The MS-DOS time. The time is a packed value with the following format.
[0-4 bit]   Second divided by 2
[5-10 bit]  Minute (0¨C59)
[11-15 bit] Hour (0¨C23 on a 24-hour clock)

========================================================================================================

==> Time Functions
1. Convert time value to string
asctime ==> Format struct tm(Calendar Time: UTC) to string
ctime ==> Covert time_t to local time and convert to string
strftime ==> Convert struct tm(Calendar Time: UTC) to string which is formated by user's format.
_mktime64 ==> Convert struct tm(always consider tm as local time) to time_t(Calendar Time: UTC).
_localtime64_s ==> Convert time_t(local time or UTC is considered as UTC) to struct tm (local time).
gmtime_s ==> Convert time_t(local time or UTC is considered as UTC) to struct tm (UTC).
_mkgmtime64 ==> Convert UTC time represented by a tm struct to a UTC time represented by a time_t type.
*/

//ZZ: There are 89 leap day between 1601.1.1 and 1970.1.1. There the gas of this 2 value in 100 ns should be
//ZZ: 10000000 * 60 * 60 * 24 * (365 * 369 + 89) = 116444736000000000I64
#define TIMEWRAP_FILE_TIME_GREATER_THAN_DOS_TIME_IN_100NS    116444736000000000I64
#define TIMEWRAP_HUNDRED_NANOSECOND_IN_ONE_SECOND            10000000
#define TIMEWRAP_YEAR_BASE_OF_STRUCT_TM                      1900
#define TIMEWRAP_NUM_OF_SUPPORTED_TIME_TYPES                 2
#define TIMEWRAP_MAX_TIME_STR_LENGTH                         MAX_PATH
#define TIMEWRAP_MAX_TIME_ZONE_STR_LENGTH                    12

typedef enum
{
    ETVT_UTC = 0,
    ETVT_LOCAL
}E_TIMEVAL_TYPE;

typedef enum
{
    ETSF_UNKNOWN = 0,
    ETSF_DETAIL,
    ETSF_DATE_TIME,              //ZZ: ISO 8601: YYYY-MM-DD HH:MM:SS
    ETSF_DATE_TIME_DD_MM_YYYY,   //ZZ: DD/MM/YYYY HH:MM:SS
    ETSF_DATE_TIME_MM_DD_YYYY,   //ZZ: MM/DD/YYYY HH:MM:SS
    ETSF_TIME_ZONE_ONLY
}E_TIMESTR_FORMAT;

typedef struct
{
    __time64_t llTimeVal;
    struct tm  stTimeVal;
}ST_TIME_VAL, *PST_TIME_VAL;

typedef struct
{
    LONG lTZBias;    //ZZ: local time = UTC + lTZBias. The bias is the difference, in minutes, between UTC time and local time.
    WCHAR wzTZStr[TIMEWRAP_MAX_TIME_ZONE_STR_LENGTH + 1];
}ST_TIME_ZONE, *PST_TIME_ZONE;

static string U2A(const WCHAR* pwzInStr, DWORD dwCodePage = CP_ACP)
{
    DWORD dwConvertFlags = WC_COMPOSITECHECK | WC_SEPCHARS;
    if ((50220 == dwCodePage) || (50221 == dwCodePage) || (50222 == dwCodePage) || (50225 == dwCodePage) || (50227 == dwCodePage)
        || (50229 == dwCodePage) || (52936 == dwCodePage) || (54936 == dwCodePage) || (65000 == dwCodePage) || (65001 == dwCodePage)
        || (42 == dwCodePage) || ((57002 <= dwCodePage) && (57011 >= dwCodePage)))
        dwConvertFlags = 0;

    string sOutStr;
    long lRetCode = 0;
    //ZZ: cchWideChar(-1): Deal with all input string till terminating null character, including null character in output buffer.
    DWORD dwAnsiStrLen = WideCharToMultiByte(dwCodePage, dwConvertFlags, pwzInStr, -1, NULL, 0, NULL, NULL);
    if (0 == dwAnsiStrLen)
        lRetCode = GetLastError();
    else
    {
        CHAR* pzAnsiBuf = NULL;
        do
        {
            pzAnsiBuf = new CHAR[dwAnsiStrLen];
            memset(pzAnsiBuf, 0, dwAnsiStrLen);

            dwAnsiStrLen = WideCharToMultiByte(dwCodePage, dwConvertFlags, pwzInStr, -1, pzAnsiBuf, dwAnsiStrLen, NULL, NULL);
            if (0 == dwAnsiStrLen)
                lRetCode = GetLastError();
            else
                sOutStr = pzAnsiBuf;
        } while (0);

        if (pzAnsiBuf)
        {
            delete[]pzAnsiBuf;
            pzAnsiBuf = NULL;
        }
    }

    SetLastError(lRetCode);

    return sOutStr;
}

static wstring A2U(const CHAR* pzInStr, DWORD dwCodePage = CP_ACP)
{
    DWORD dwConvertFlags = MB_PRECOMPOSED;
    if ((50220 == dwCodePage) || (50221 == dwCodePage) || (50222 == dwCodePage) || (50225 == dwCodePage) || (50227 == dwCodePage)
        || (50229 == dwCodePage) || (52936 == dwCodePage) || (54936 == dwCodePage) || (65000 == dwCodePage) || (65001 == dwCodePage)
        || (42 == dwCodePage) || ((57002 <= dwCodePage) && (57011 >= dwCodePage)))
        dwConvertFlags = 0;

    wstring wsOutStr;
    long lRetCode = 0;
    //ZZ: cchMultiByte(-1): Deal with all input string till terminating null character, including null character in output buffer.
    DWORD dwUnicodeStrLen = MultiByteToWideChar(dwCodePage, dwConvertFlags, pzInStr, -1, NULL, 0);
    if (0 == dwUnicodeStrLen)
        lRetCode = GetLastError();
    else
    {
        WCHAR* pwzUnicodeBuf = NULL;
        do
        {
            pwzUnicodeBuf = new WCHAR[dwUnicodeStrLen];
            memset(pwzUnicodeBuf, 0, dwUnicodeStrLen * sizeof(WCHAR));

            dwUnicodeStrLen = MultiByteToWideChar(dwCodePage, dwConvertFlags, pzInStr, -1, pwzUnicodeBuf, dwUnicodeStrLen);
            if (0 == dwUnicodeStrLen)
                lRetCode = GetLastError();
            else
                wsOutStr = pwzUnicodeBuf;
        } while (0);

        if (pwzUnicodeBuf)
        {
            delete[]pwzUnicodeBuf;
            pwzUnicodeBuf = NULL;
        }
    }

    SetLastError(lRetCode);

    return wsOutStr;
}

class CTimeWrap
{
public:
    CTimeWrap(__time64_t llTimeVal = 0,
        E_TIMEVAL_TYPE eTimeValType = ETVT_UTC,
        E_TIMESTR_FORMAT eTimeStrFormat = ETSF_DETAIL)
        : m_eTimeValType(eTimeValType)
        , m_eTimeStrFormat(eTimeStrFormat)
    {
        (*this) = llTimeVal;
    }

    CTimeWrap(const struct tm& stTimeVal,
        E_TIMEVAL_TYPE eTimeValType = ETVT_UTC,
        E_TIMESTR_FORMAT eTimeStrFormat = ETSF_DETAIL)
        : m_eTimeValType(eTimeValType)
        , m_eTimeStrFormat(eTimeStrFormat)
    {
        (*this) = stTimeVal;
    }

    CTimeWrap(const FILETIME& stFileTime,
        E_TIMEVAL_TYPE eTimeValType = ETVT_UTC,
        E_TIMESTR_FORMAT eTimeStrFormat = ETSF_DETAIL)
        : m_eTimeValType(eTimeValType)
        , m_eTimeStrFormat(eTimeStrFormat)
    {
        (*this) = stFileTime;
    }

    CTimeWrap(const SYSTEMTIME& stSysTime,
        E_TIMEVAL_TYPE eTimeValType = ETVT_UTC,
        E_TIMESTR_FORMAT eTimeStrFormat = ETSF_DETAIL)
        : m_eTimeValType(eTimeValType)
        , m_eTimeStrFormat(eTimeStrFormat)
    {
        (*this) = stSysTime;
    }

    CTimeWrap(const WCHAR* pwzTimeStr,
        E_TIMEVAL_TYPE eTimeValType = ETVT_UTC,
        E_TIMESTR_FORMAT eTimeStrFormat = ETSF_DETAIL)
        : m_eTimeValType(eTimeValType)
        , m_eTimeStrFormat(eTimeStrFormat)
    {
        (*this) = pwzTimeStr;
    }

    CTimeWrap(string sTimeStr,
        E_TIMEVAL_TYPE eTimeValType = ETVT_UTC,
        E_TIMESTR_FORMAT eTimeStrFormat = ETSF_DETAIL)
        : m_eTimeValType(eTimeValType)
        , m_eTimeStrFormat(eTimeStrFormat)
    {
        (*this) = sTimeStr;
    }

    CTimeWrap(wstring wsTimeStr,
        E_TIMEVAL_TYPE eTimeValType = ETVT_UTC,
        E_TIMESTR_FORMAT eTimeStrFormat = ETSF_DETAIL)
        : m_eTimeValType(eTimeValType)
        , m_eTimeStrFormat(eTimeStrFormat)
    {
        (*this) = wsTimeStr;
    }

    CTimeWrap(const CTimeWrap& TimeWrap)
    {
        (*this) = TimeWrap;
    }

    CTimeWrap(const CTimeWrap& TimeWrap,
        E_TIMEVAL_TYPE eTimeValType,
        E_TIMESTR_FORMAT eTimeStrFormat)
    {
        (*this) = TimeWrap;
        m_eTimeValType = eTimeValType;
        m_eTimeStrFormat = eTimeStrFormat;
    }

public:
    CTimeWrap& operator = (__time64_t llTimeVal)
    {
        long lRetCode = 0;
        memset(m_stTimeVal, 0, sizeof(m_stTimeVal));
        memset(m_wzTimsStr, 0, sizeof(m_wzTimsStr));
        m_stTimeZone = CTimeWrap::GetTimeZone();

        ST_TIME_VAL& stUTCTimeVal = m_stTimeVal[ETVT_UTC];
        ST_TIME_VAL& stLocalTimeVal = m_stTimeVal[ETVT_LOCAL];

        if (ETVT_UTC == m_eTimeValType)
        {
            stUTCTimeVal.stTimeVal = CTimeWrap::TimeVal2TM(llTimeVal);
            stUTCTimeVal.llTimeVal = llTimeVal;

            stLocalTimeVal.stTimeVal = CTimeWrap::TimeVal2LocalTM(llTimeVal);
            stLocalTimeVal.llTimeVal = CTimeWrap::TM2TimeVal(stLocalTimeVal.stTimeVal);
        }
        else
        {
            stLocalTimeVal.llTimeVal = llTimeVal;
            stLocalTimeVal.stTimeVal = CTimeWrap::TimeVal2TM(llTimeVal);

            stUTCTimeVal.llTimeVal = CTimeWrap::LocalTM2TimeVal(stLocalTimeVal.stTimeVal);
            stUTCTimeVal.stTimeVal = CTimeWrap::TimeVal2TM(stUTCTimeVal.llTimeVal);
        }

        return (*this);
    }

    CTimeWrap& operator = (const struct tm& stTimeVal)
    {
        long lRetCode = 0;
        memset(m_stTimeVal, 0, sizeof(m_stTimeVal));
        memset(m_wzTimsStr, 0, sizeof(m_wzTimsStr));
        m_stTimeZone = CTimeWrap::GetTimeZone();

        ST_TIME_VAL& stUTCTimeVal = m_stTimeVal[ETVT_UTC];
        ST_TIME_VAL& stLocalTimeVal = m_stTimeVal[ETVT_LOCAL];

        struct tm& stCurTimeVal = const_cast<struct tm&>(stTimeVal);
        if (ETVT_UTC == m_eTimeValType)
        {
            stUTCTimeVal.stTimeVal = stTimeVal;
            stUTCTimeVal.llTimeVal = CTimeWrap::TM2TimeVal(stCurTimeVal);

            stLocalTimeVal.stTimeVal = CTimeWrap::TimeVal2LocalTM(stUTCTimeVal.llTimeVal);
            stLocalTimeVal.llTimeVal = CTimeWrap::TM2TimeVal(stLocalTimeVal.stTimeVal);
        }
        else
        {
            stLocalTimeVal.stTimeVal = stTimeVal;
            stLocalTimeVal.llTimeVal = CTimeWrap::TM2TimeVal(stCurTimeVal);

            stUTCTimeVal.llTimeVal = CTimeWrap::LocalTM2TimeVal(stCurTimeVal);
            stUTCTimeVal.stTimeVal = CTimeWrap::TimeVal2TM(stUTCTimeVal.llTimeVal);
        }

        return (*this);
    }

    CTimeWrap& operator = (const FILETIME& stFileTime)
    {
        (*this) = CTimeWrap::FileTime2TimeVal(stFileTime);
        return (*this);
    }

    CTimeWrap& operator = (const SYSTEMTIME& stSysTime)
    {
        (*this) = CTimeWrap::SysTime2TM(stSysTime);
        return (*this);
    }

    CTimeWrap& operator = (const WCHAR* pwzTimeStr)
    {
        (*this) = CTimeWrap::TimeStr2TM(pwzTimeStr, m_eTimeStrFormat);
        return (*this);
    }

    CTimeWrap& operator = (string sTimeStr)
    {
        wstring wsTimeStr = A2U(sTimeStr.c_str(), CP_ACP);
        (*this) = CTimeWrap::TimeStr2TM(wsTimeStr.c_str(), m_eTimeStrFormat);
        return (*this);
    }

    CTimeWrap& operator = (wstring wsTimeStr)
    {
        (*this) = CTimeWrap::TimeStr2TM(wsTimeStr.c_str(), m_eTimeStrFormat);
        return (*this);
    }

    CTimeWrap& operator = (const CTimeWrap& TimeWrap)
    {
        memcpy(m_stTimeVal, TimeWrap.m_stTimeVal, sizeof(m_stTimeVal));
        memcpy(m_wzTimsStr, TimeWrap.m_wzTimsStr, sizeof(m_wzTimsStr));
        m_stTimeZone = TimeWrap.m_stTimeZone;

        m_eTimeValType = TimeWrap.m_eTimeValType;
        m_eTimeStrFormat = TimeWrap.m_eTimeStrFormat;
        return (*this);
    }

    bool operator < (CTimeWrap& TimeWrap)
    {
        return (m_stTimeVal[m_eTimeValType].llTimeVal < (__time64_t)TimeWrap);
    }

    CTimeWrap operator [] (E_TIMEVAL_TYPE eTimeValType)
    {
        return CTimeWrap(*this, eTimeValType, m_eTimeStrFormat);
    }

    CTimeWrap operator () (E_TIMEVAL_TYPE eTimeValType, E_TIMESTR_FORMAT eTimeStrFormat)
    {
        return CTimeWrap(*this, eTimeValType, eTimeStrFormat);
    }

    operator __time64_t ()
    {
        return m_stTimeVal[m_eTimeValType].llTimeVal;
    }

    operator struct tm()
    {
        return m_stTimeVal[m_eTimeValType].stTimeVal;
    }

    operator FILETIME ()
    {
        return CTimeWrap::TimeVal2FileTime(m_stTimeVal[m_eTimeValType].llTimeVal);
    }

    operator SYSTEMTIME ()
    {
        return CTimeWrap::TM2SysTime(m_stTimeVal[m_eTimeValType].stTimeVal);
    }

    operator ST_TIME_ZONE ()
    {
        return m_stTimeZone;
    }

    operator WCHAR* ()
    {
        long lRetCode = 0;
        struct tm& stTimeVal = m_stTimeVal[m_eTimeValType].stTimeVal;
        memset(m_wzTimsStr, 0, sizeof(m_wzTimsStr));
        if (ETSF_DETAIL == m_eTimeStrFormat)
        {
            DWORD dwStrLen = (DWORD)wcsftime(m_wzTimsStr, _countof(m_wzTimsStr), L"%c %a", &stTimeVal);
            if (0 == dwStrLen)
            {
                lRetCode = GetLastError();
                swprintf_s(m_wzTimsStr, _countof(m_wzTimsStr), L"%04u-%02u-%02u %02u:%02u:%02u",
                    stTimeVal.tm_year + 1900, stTimeVal.tm_mon + 1, stTimeVal.tm_mday,
                    stTimeVal.tm_hour, stTimeVal.tm_min, stTimeVal.tm_sec);
            }

            if (ETVT_LOCAL == m_eTimeValType)
            {
                wcscat_s(m_wzTimsStr, _countof(m_wzTimsStr), L" ");
                wcscat_s(m_wzTimsStr, _countof(m_wzTimsStr), m_stTimeZone.wzTZStr);
            }
        }
        else if (ETSF_DATE_TIME == m_eTimeStrFormat)
        {
            swprintf_s(m_wzTimsStr, _countof(CTimeWrap::m_wzTimsStr), L"%04u-%02u-%02u %02u:%02u:%02u",
                stTimeVal.tm_year + 1900, stTimeVal.tm_mon + 1, stTimeVal.tm_mday,
                stTimeVal.tm_hour, stTimeVal.tm_min, stTimeVal.tm_sec);
        }
        else if (ETSF_DATE_TIME_DD_MM_YYYY == m_eTimeStrFormat)
        {
            swprintf_s(m_wzTimsStr, _countof(CTimeWrap::m_wzTimsStr), L"%02u/%02u/%04u %02u:%02u:%02u",
                stTimeVal.tm_mday, stTimeVal.tm_mon + 1, stTimeVal.tm_year + 1900,
                stTimeVal.tm_hour, stTimeVal.tm_min, stTimeVal.tm_sec);
        }
        else if (ETSF_DATE_TIME_MM_DD_YYYY == m_eTimeStrFormat)
        {
            swprintf_s(m_wzTimsStr, _countof(CTimeWrap::m_wzTimsStr), L"%02u/%02u/%04u %02u:%02u:%02u",
                stTimeVal.tm_mon + 1, stTimeVal.tm_mday, stTimeVal.tm_year + 1900,
                stTimeVal.tm_hour, stTimeVal.tm_min, stTimeVal.tm_sec);
        }
        else if (ETSF_TIME_ZONE_ONLY == m_eTimeStrFormat)
            return m_stTimeZone.wzTZStr;

        return m_wzTimsStr;
    }

public:
    static ST_TIME_ZONE GetTimeZone()
    {
        ST_TIME_ZONE stTimeZone = { 0 };

        long lRetCode = 0;
        TIME_ZONE_INFORMATION stTimeZoneInfo = { 0 };
        DWORD dwTZInfo = GetTimeZoneInformation(&stTimeZoneInfo);
        if (TIME_ZONE_ID_INVALID == dwTZInfo)
            lRetCode = GetLastError();
        else if ((TIME_ZONE_ID_UNKNOWN == dwTZInfo) || (TIME_ZONE_ID_STANDARD == dwTZInfo) || (TIME_ZONE_ID_DAYLIGHT == dwTZInfo))
        {
            stTimeZone.lTZBias = -1 * stTimeZoneInfo.Bias;
            swprintf_s(stTimeZone.wzTZStr, _countof(stTimeZone.wzTZStr), L"(UTC %+02d:%02d)", stTimeZone.lTZBias / 60, abs(stTimeZone.lTZBias) % 60);
        }

        return stTimeZone;
    }

    static struct tm TimeStr2TM(const WCHAR* pwzTimeStr, E_TIMESTR_FORMAT eTimeStrFormat = ETSF_DATE_TIME)
    {
        struct tm stTimeVal = { 0 };
        if (pwzTimeStr)
        {
            DWORD dwTimeField = 0;
            const WCHAR* pwzDelimit = L"-/ :";
            DWORD dwInputStrLen = (DWORD)wcslen(pwzTimeStr);
            if (0 != dwInputStrLen)
            {
                WCHAR* pwzStr2Token = NULL;
                do
                {
                    pwzStr2Token = new WCHAR[dwInputStrLen + 1];
                    if (NULL == pwzStr2Token)
                        break;

                    memset(pwzStr2Token, 0, sizeof(WCHAR) * (dwInputStrLen + 1));
                    wcsncpy_s(pwzStr2Token, dwInputStrLen + 1, pwzTimeStr, _TRUNCATE);
                    for (WCHAR *pwzNextToken = NULL, *pwzCurToken = wcstok_s(pwzStr2Token, pwzDelimit, &pwzNextToken);
                        pwzCurToken;
                        pwzCurToken = wcstok_s(NULL, pwzDelimit, &pwzNextToken))
                    {
                        switch (dwTimeField++)
                        {
                            case 0:
                            {
                                if (ETSF_DATE_TIME_DD_MM_YYYY == eTimeStrFormat)
                                    stTimeVal.tm_mday = wcstoul(pwzCurToken, NULL, 10);
                                else if (ETSF_DATE_TIME_MM_DD_YYYY == eTimeStrFormat)
                                    stTimeVal.tm_mon = wcstoul(pwzCurToken, NULL, 10) - 1;
                                else
                                    stTimeVal.tm_year = wcstoul(pwzCurToken, NULL, 10) - TIMEWRAP_YEAR_BASE_OF_STRUCT_TM;
                                break;
                            }
                            case 1:
                            {
                                if (ETSF_DATE_TIME_DD_MM_YYYY == eTimeStrFormat)
                                    stTimeVal.tm_mon = wcstoul(pwzCurToken, NULL, 10) - 1;
                                else if (ETSF_DATE_TIME_MM_DD_YYYY == eTimeStrFormat)
                                    stTimeVal.tm_mday = wcstoul(pwzCurToken, NULL, 10);
                                else
                                    stTimeVal.tm_mon = wcstoul(pwzCurToken, NULL, 10) - 1;
                                break;
                            }
                            case 2:
                            {
                                if ((ETSF_DATE_TIME_DD_MM_YYYY == eTimeStrFormat) || (ETSF_DATE_TIME_MM_DD_YYYY == eTimeStrFormat))
                                    stTimeVal.tm_year = wcstoul(pwzCurToken, NULL, 10) - TIMEWRAP_YEAR_BASE_OF_STRUCT_TM;
                                else
                                    stTimeVal.tm_mday = wcstoul(pwzCurToken, NULL, 10);
                                break;
                            }
                            case 3:
                            {
                                stTimeVal.tm_hour = wcstoul(pwzCurToken, NULL, 10);
                                break;
                            }
                            case 4:
                            {
                                stTimeVal.tm_min = wcstoul(pwzCurToken, NULL, 10);
                                break;
                            }
                            case 5:
                            {
                                stTimeVal.tm_sec = wcstoul(pwzCurToken, NULL, 10);
                                break;
                            }
                            default:
                                break;
                        }
                    }
                } while (0);

                if (pwzStr2Token)
                {
                    delete[] pwzStr2Token;
                    pwzStr2Token = NULL;
                }
            }
        }

        return stTimeVal;
    }

    //ZZ: Convert time_t(local time or UTC is considered as UTC) to struct tm(local time).
    static struct tm TimeVal2LocalTM(__time64_t llTimeVal)
    {
        struct tm stTimeVal = { 0 };
        long lRetCode = _localtime64_s(&stTimeVal, &llTimeVal);
        return stTimeVal;
    }

    //ZZ: Convert time_t(local time or UTC is considered as UTC) to struct tm(UTC).
    static struct tm TimeVal2TM(__time64_t llTimeVal)
    {
        struct tm stTimeVal = { 0 };
        long lRetCode = gmtime_s(&stTimeVal, &llTimeVal);
        return stTimeVal;
    }

    static FILETIME TimeVal2FileTime(__time64_t llTimeVal)
    {
        ULARGE_INTEGER UliTimeVal = { 0 };
        UliTimeVal.QuadPart = llTimeVal * TIMEWRAP_HUNDRED_NANOSECOND_IN_ONE_SECOND + TIMEWRAP_FILE_TIME_GREATER_THAN_DOS_TIME_IN_100NS;
        FILETIME stFileTime = { UliTimeVal.LowPart, UliTimeVal.HighPart };
        return stFileTime;
    }

    static SYSTEMTIME TimeVal2SysTime(__time64_t llTimeVal)
    {
        return CTimeWrap::TM2SysTime(CTimeWrap::TimeVal2TM(llTimeVal));
    }

    //ZZ: Convert struct tm(always consider tm as local time) to time_t(Calendar Time : UTC).
    static __time64_t LocalTM2TimeVal(struct tm& stTimeVal)
    {
        return _mktime64(&stTimeVal);
    }

    static struct tm LocalTM2TM(struct tm& stTimeVal)
    {
        return CTimeWrap::TimeVal2TM(CTimeWrap::LocalTM2TimeVal(stTimeVal));
    }

    static FILETIME LocalTM2FileTime(struct tm& stTimeVal)
    {
        return CTimeWrap::TimeVal2FileTime(CTimeWrap::LocalTM2TimeVal(stTimeVal));
    }

    static SYSTEMTIME LocalTM2SysTime(struct tm& stTimeVal)
    {
        return CTimeWrap::TM2SysTime(CTimeWrap::LocalTM2TM(stTimeVal));
    }

    //ZZ: Convert UTC time represented by a tm struct to a UTC time represented by a time_t type.
    static __time64_t TM2TimeVal(struct tm& stTimeVal)
    {
        return _mkgmtime64(&stTimeVal);
    }

    static struct tm TM2LocalTM(struct tm& stTimeVal)
    {
        return CTimeWrap::TimeVal2LocalTM(CTimeWrap::TM2TimeVal(stTimeVal));
    }

    static FILETIME TM2FileTime(struct tm& stTimeVal)
    {
        return CTimeWrap::TimeVal2FileTime(CTimeWrap::TM2TimeVal(stTimeVal));
    }

    static SYSTEMTIME TM2SysTime(const struct tm& stTimeVal)
    {
        SYSTEMTIME stSysTime = {
            TIMEWRAP_YEAR_BASE_OF_STRUCT_TM + stTimeVal.tm_year,
            1 + stTimeVal.tm_mon,
            stTimeVal.tm_wday,
            stTimeVal.tm_mday,
            stTimeVal.tm_hour,
            stTimeVal.tm_min,
            stTimeVal.tm_sec,
            0 };
        return stSysTime;
    }

    static __time64_t FileTime2TimeVal(const FILETIME& stFileTime)
    {
        __time64_t llTimeVal = 0;
        ULARGE_INTEGER UliTimeVal = { stFileTime.dwLowDateTime, stFileTime.dwHighDateTime };
        if (UliTimeVal.QuadPart > TIMEWRAP_FILE_TIME_GREATER_THAN_DOS_TIME_IN_100NS)
            llTimeVal = (UliTimeVal.QuadPart - TIMEWRAP_FILE_TIME_GREATER_THAN_DOS_TIME_IN_100NS) / TIMEWRAP_HUNDRED_NANOSECOND_IN_ONE_SECOND;

        return llTimeVal;
    }

    static struct tm FileTime2TM(const FILETIME& stFileTime)
    {
        return CTimeWrap::TimeVal2TM(CTimeWrap::FileTime2TimeVal(stFileTime));
    }

    static struct tm FileTime2LocalTM(const FILETIME& stFileTime)
    {
        return CTimeWrap::TimeVal2LocalTM(CTimeWrap::FileTime2TimeVal(stFileTime));
    }

    static SYSTEMTIME FileTime2SysTime(const FILETIME& stFileTime)
    {
        long lRetCode = 0;
        SYSTEMTIME stSysTime = { 0 };
        if (!FileTimeToSystemTime(&stFileTime, &stSysTime))
            lRetCode = GetLastError();
        return stSysTime;
    }

    static __time64_t SysTime2TimeVal(const SYSTEMTIME& stSysTime)
    {
        return CTimeWrap::TM2TimeVal(CTimeWrap::SysTime2TM(stSysTime));
    }

    static struct tm  SysTime2TM(const SYSTEMTIME& stSysTime)
    {
        struct tm stTimeVal = {
            stSysTime.wSecond,
            stSysTime.wMinute,
            stSysTime.wHour,
            stSysTime.wDay,
            stSysTime.wMonth - 1,
            stSysTime.wYear - TIMEWRAP_YEAR_BASE_OF_STRUCT_TM,
            stSysTime.wDayOfWeek,
            0, 0 };
        return stTimeVal;
    }

    static struct tm  SysTime2LocalTM(const SYSTEMTIME& stSysTime)
    {
        return CTimeWrap::TM2LocalTM(CTimeWrap::SysTime2TM(stSysTime));
    }

    static FILETIME SysTime2FileTime(const SYSTEMTIME& stSysTime)
    {
        long lRetCode = 0;
        FILETIME stFileTime = { 0 };
        if (!SystemTimeToFileTime(&stSysTime, &stFileTime))
            lRetCode = GetLastError();
        return stFileTime;
    }

    //ZZ: Local FILETIME to UTC SYSTEMTIME. By default, current active time zone will be used to convert.
    static SYSTEMTIME LocalFileTime2SysTime(const SYSTEMTIME& stLocalSysTime, PTIME_ZONE_INFORMATION pstTimeZoneInfo = NULL)
    {
        long lRetCode = 0;
        SYSTEMTIME stUTCSysTime = { 0 };
        if (!TzSpecificLocalTimeToSystemTime(pstTimeZoneInfo, &stLocalSysTime, &stUTCSysTime))
            lRetCode = GetLastError();
        return stUTCSysTime;
    }

    //ZZ: UTC SYSTEMTIME to Local FILETIME. By default, current active time zone will be used to convert.
    static SYSTEMTIME SysTime2LocalFileTime(const SYSTEMTIME& stUTCSysTime, PTIME_ZONE_INFORMATION pstTimeZoneInfo = NULL)
    {
        long lRetCode = 0;
        SYSTEMTIME stLocalSysTime = { 0 };
        if (!SystemTimeToTzSpecificLocalTime(pstTimeZoneInfo, &stUTCSysTime, &stLocalSysTime))
            lRetCode = GetLastError();
        return stLocalSysTime;
    }

private:
    WCHAR            m_wzTimsStr[TIMEWRAP_MAX_TIME_STR_LENGTH];
    ST_TIME_VAL      m_stTimeVal[TIMEWRAP_NUM_OF_SUPPORTED_TIME_TYPES];
    ST_TIME_ZONE     m_stTimeZone;
    E_TIMEVAL_TYPE   m_eTimeValType;
    E_TIMESTR_FORMAT m_eTimeStrFormat;
};
