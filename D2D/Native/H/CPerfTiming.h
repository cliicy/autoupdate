
#pragma once

struct CPerfTiming
{
    ///<ZZ[zhoyu03: 2009/12/21]: Add valid time for application restore, excluding prepare time.
    LARGE_INTEGER m_liValidTimeStart;
    LARGE_INTEGER m_liValidTimeDuration;  ///ZZ: Total validation time.
    LARGE_INTEGER m_liValidTimeBlock;     ///ZZ: A piece of time, since last ResetValidTime() to End()
	//<data>2009-8-25 ###???
	LARGE_INTEGER m_liStart;
	LARGE_INTEGER m_liEnd;
	LARGE_INTEGER m_liFrequency;
	BOOL m_bHighResolution;
	//</data>

	//<function>2009-8-25 ###???
	CPerfTiming(BOOL bHighResolution)
	{
		m_liStart.QuadPart = 0;
		m_liEnd.QuadPart = 0;
		m_liFrequency.QuadPart = 0;
        m_liValidTimeBlock.QuadPart = 0;
        m_liValidTimeDuration.QuadPart = 0;
		m_bHighResolution = FALSE;

		if (bHighResolution)
		{
			m_bHighResolution = QueryPerformanceFrequency(&m_liFrequency);
		}
	}

	~CPerfTiming()
	{
		m_liStart.QuadPart = 0;
		m_liEnd.QuadPart = 0;
		m_liFrequency.QuadPart = 0;
        m_liValidTimeStart.QuadPart = 0;
        m_liValidTimeBlock.QuadPart = 0;
        m_liValidTimeDuration.QuadPart = 0;
		m_bHighResolution = FALSE;
	}

	LONGLONG Start()
	{
		if (m_bHighResolution)
		{
			QueryPerformanceCounter(&m_liStart);
		}
		else
		{
			m_liStart.QuadPart = GetTickCount();
		}

        ///<ZZ[zhoyu03: 2009/12/21]: Add valid time for application restore, excluding prepare time.
        m_liValidTimeStart.QuadPart = m_liStart.QuadPart;
        m_liValidTimeBlock.QuadPart = 0;

		return m_liStart.QuadPart;
	}

	LONGLONG End()
	{
		if (m_bHighResolution)
		{
			QueryPerformanceCounter(&m_liEnd);
		}
		else
		{
			m_liEnd.QuadPart = GetTickCount();
		}

        ///<ZZ[zhoyu03: 2009/12/21]: Add valid time for application restore, excluding prepare time.
        m_liValidTimeDuration.QuadPart += (m_liEnd.QuadPart - m_liValidTimeStart.QuadPart);
        m_liValidTimeBlock.QuadPart = (m_liEnd.QuadPart - m_liValidTimeStart.QuadPart);
        m_liValidTimeStart.QuadPart = m_liEnd.QuadPart;

		return m_liEnd.QuadPart;
	}

	DWORD Timing(LPDWORD pdwMsecond)
	{
		DWORD dwSecond = 0;
		if (m_bHighResolution)
		{
			dwSecond = (DWORD)((m_liEnd.QuadPart - m_liStart.QuadPart)/m_liFrequency.QuadPart);
			if (pdwMsecond)
			{
				LONGLONG llMsecond = (m_liEnd.QuadPart - m_liStart.QuadPart)%m_liFrequency.QuadPart;
				*pdwMsecond =(DWORD)((llMsecond*1000)/m_liFrequency.QuadPart);
			}
		}
		else
		{
			dwSecond = (DWORD)((m_liEnd.QuadPart - m_liStart.QuadPart)/1000);
			if (pdwMsecond)
			{
				*pdwMsecond = (DWORD)((m_liEnd.QuadPart - m_liStart.QuadPart)%1000);
			}
		}
		return dwSecond;
	}

    ULONGLONG TimingMS()
    {
        DWORD dwTimeMS = 0;
        ULONGLONG ullTimeSec = Timing(&dwTimeMS);
        return (ullTimeSec * 1000 + dwTimeMS);
    }

    ///<ZZ[zhoyu03: 2009/12/21]: Add valid time for application restore, excluding prepare time.
    ///ZZ: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    ///ZZ: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    ///ZZ: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    ///ZZ: Please pay much attention! This function return total valid time no matter how many times
    ///ZZ: Start() or End() is called, while Timing() return a piece of time since Start() is called
    ///ZZ: to End() is called.
    DWORD ValidTiming(LPDWORD pdwMsecond)
    {
        DWORD dwSecond = 0;
        if (m_bHighResolution)
        {
            dwSecond = (DWORD)(m_liValidTimeDuration.QuadPart / m_liFrequency.QuadPart);
            if(pdwMsecond)
            {
                LONGLONG llMsecond = m_liValidTimeDuration.QuadPart % m_liFrequency.QuadPart;
                *pdwMsecond = (DWORD)((llMsecond*1000) / m_liFrequency.QuadPart);
            }
        }
        else
        {
            dwSecond = (DWORD)(m_liValidTimeDuration.QuadPart / 1000);
            if(pdwMsecond)
                *pdwMsecond = m_liValidTimeDuration.QuadPart % 1000;;
        }
        return dwSecond;
    }

    ULONGLONG ValidTimingMS()
    {
        DWORD dwTimeMS = 0;
        ULONGLONG ullTimeSec = ValidTiming(&dwTimeMS);
        return (ullTimeSec * 1000 + dwTimeMS);
    }

    ///ZZ: Time duration sincce ResetValidTime() to End(). 
    DWORD ValidTimingBlock(LPDWORD pdwMsecond)
    {
        DWORD dwSecond = 0;
        if (m_bHighResolution)
        {
            dwSecond = (DWORD)(m_liValidTimeBlock.QuadPart / m_liFrequency.QuadPart);
            if(pdwMsecond)
            {
                LONGLONG llMsecond = m_liValidTimeBlock.QuadPart % m_liFrequency.QuadPart;
                *pdwMsecond = (DWORD)((llMsecond*1000) / m_liFrequency.QuadPart);
            }
        }
        else
        {
            dwSecond = (DWORD)(m_liValidTimeBlock.QuadPart / 1000);
            if(pdwMsecond)
                *pdwMsecond = m_liValidTimeBlock.QuadPart % 1000;;
        }
        return dwSecond;
    }

    ULONGLONG ValidTimingBlockMS()
    {
        DWORD dwTimeMS = 0;
        ULONGLONG ullTimeSec = ValidTimingBlock(&dwTimeMS);
        return (ullTimeSec * 1000 + dwTimeMS);
    }

    ///ZZ: Sometime we need know how many time used to operate data but not other assist time after Start() is called.
    ///ZZ: e.g. there are 4 steps in a job, #1, #2, #3 AND #4. we want to know time #2+#4, we need call in such sequence
    ///ZZ: Start(), #1, ResetValidTime(), #2, End(), #3, ResetValidTime(), #4, End(). Then T(#2+#4)=ValidTiming(), while
    ///ZZ: T(total)=Timing()
    LONGLONG ResetValidTime()
    {
        if(m_bHighResolution)
            QueryPerformanceCounter(&m_liValidTimeStart);
        else
            m_liValidTimeStart.QuadPart = GetTickCount();

        m_liValidTimeBlock.QuadPart = 0;

        return m_liValidTimeStart.QuadPart;
    }

	//</function>
};
