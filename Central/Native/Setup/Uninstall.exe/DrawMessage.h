/* DrawMessage()
 * Drop-in replacement for DrawMessage() supporting a tiny subset of Message.
 */

#if defined __cplusplus
extern "C"
#endif
int __stdcall DrawMessage(
                       HDC     hdc,        // handle of device context
                       LPCTSTR lpString,   // address of string to draw
                       INT_PTR nCount,     // string length, in characters
                       LPRECT  lpRect,     // address of structure with formatting dimensions
                       UINT    uFormat     // text-drawing flags
                      );
