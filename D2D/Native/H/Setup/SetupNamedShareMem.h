#pragma once

#define SHARMEM_MsgPoolSize 8
#define SHARMEM_MSG_BUFFER_SIZE 1024

#define SETUP_SHAREMEM_MSGPOOL_NAME		_T("AS090701Setup_ShareMem_LocalMsgPool")
#define MSGPOOL_EVENT_NAME_PREFIX		_T("AS090701Setup_MsgPool_Event_")
#define MSGPOOL_QUEUE_EVENT_NAME		_T("AS090701Setup_MsgPool_QueueEvent")

//named share memory struct
//[INT32 Array for the msg pool queue][buffer][buffer]......



