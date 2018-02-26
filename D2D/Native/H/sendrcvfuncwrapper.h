#pragma once
#include <stdio.h>
#pragma warning(push, 3)
#include <WinSock2.h>
#pragma warning(pop)
#pragma warning(push, 4)

static int Send(SOCKET sock, const char * pData, int Length, int flag)
{
    const char *p = (const char*)pData;
    int snd = 0;
    while (1)
    {
        int result = send(sock, (const char *)(p + snd), Length - snd, flag);
        if(result <= 0)
        {
            snd = result;
            break;
        }
        snd += result;
        if(snd == Length)
        {
            break;
        }
    }
    return snd;
}

static int Recv(SOCKET sock, char * pData, int Length, int flag)
{
    char *p = (char*)pData;
    int rcv = 0;
    while (1)
    {
        int result = recv(sock, (char *)(p + rcv), Length - rcv, flag);
        if(result <= 0)
        {
            rcv = result;
            break;
        }
        rcv += result;
        if(rcv == Length)
        {
            break;
        }
    }
    return rcv;
}

static int SendSoleCommand(SOCKET sock, unsigned long ulCmd, unsigned long ulStat = 0)
{
    ST_PACKAGE_HEADER header;
    header.ulCommand = htonl(ulCmd);
    header.ulPackageSize = 0;
    header.ulStat = htonl(ulStat);

    if (Send(sock, (char*)&header, sizeof(header), 0) != sizeof(header))
        return WSAGetLastError();
    else
        return 0;
}

static int ReceiveSoleCommand(SOCKET sock, unsigned long& ulCmd)
{
    ST_PACKAGE_HEADER header;
    header.ulCommand = htonl(ulCmd);
    header.ulPackageSize = 0;

    if (Recv(sock, (char*)&header, sizeof(header), 0) != sizeof(header))
        return WSAGetLastError();
    
    ulCmd = ntohl(header.ulCommand);

    return 0;
}

static int ReceivePackageHeader(SOCKET sock, ST_PACKAGE_HEADER* pPackHeader)
{
    if (Recv(sock, (char*)pPackHeader, sizeof(ST_PACKAGE_HEADER), 0) != sizeof(ST_PACKAGE_HEADER))
    {
        int nRet = WSAGetLastError();
        return nRet ? nRet : -1;
    }
    else
        return 0;
}

static int SendPackageHeader(SOCKET sock, ST_PACKAGE_HEADER* pPackHeader)
{
    if (Send(sock, (char*)pPackHeader, sizeof(ST_PACKAGE_HEADER), 0) != sizeof(ST_PACKAGE_HEADER))
    {
        int nRet = WSAGetLastError();
        return nRet ? nRet : -1;
    }
    else
        return 0;
}

static int SetSocketBuffSize(SOCKET sock, int opname, unsigned long& nSize)
{
    int nRet = 0;
    int nTrySize = 0;

    for (nTrySize = nSize; nTrySize >= BUFFER_SIZE_16K; nTrySize = nTrySize - BUFFER_SIZE_16K)
    {
        if (setsockopt(sock, SOL_SOCKET, opname, (const char*)&nTrySize, sizeof(unsigned long)))
        {
            nRet = GetLastError();
            if (nRet == WSAENOPROTOOPT || nRet == WSAEINVAL)
                break;
            else
                nRet = 0; //continue
        }
        else
        {
            int nLen = sizeof (UINT);
            getsockopt(sock, SOL_SOCKET, opname, (char*)&nSize, &nLen);
            if (opname == SO_RCVBUF)
                printf("set socket receive buffer size.\r\n");
                //printf("set socket receive buffer size to %d \n", nSize);
            else if (opname == SO_SNDBUF)
                printf("set socket send buffer size.\r\n");

            fflush(stdout);
            break;
        }
    }

    return nRet;
}


