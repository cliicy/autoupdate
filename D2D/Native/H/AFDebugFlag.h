#pragma once

#define DEBUG_INFO  0x01
#define DEBUG_CRASH (DEBUG_INFO<<1)
#define DEBUG_HANG  (DEBUG_INFO<<2)
#define DEBUG_HOOK  (DEBUG_INFO<<3)
