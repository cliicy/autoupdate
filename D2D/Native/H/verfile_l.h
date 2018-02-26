/* Header file for verinfo.rc
   It's derived from old as8.mak
   It's part of old verfile.h
    Defines:
	version language info
	product short name
	file version major, minor
   Some other macroes are defined in verinfo.h
   Build number, build revision, and build date are dynamically defined in verfile.h
   yanzh02, Aug. 26, 2013
	
*/

/*
#undef  BUILD
#define BUILD $(BUILD_NUMBER)
#undef  BUILD_STR
#define BUILD_STR "$(BUILD_NUMBER)"

#undef  ECN
#define ECN $(BUILD_REVISION)
#undef  ECN_STR
#define ECN_STR "$(BUILD_REVISION)"

#undef  BUILD_DATE_STR
#define BUILD_DATE_STR "$(BUILD_DATE_STR)"
*/

/*
#undef  VERSION_FILENAME
#define VERSION_FILENAME "FILENAMETAG"
*/

/*
#undef  VERSION_BLOCK
#define VERSION_BLOCK "$(LANGCODEHEX)$(CODEPAGE)"
#undef  VERSION_TRANSLATION
#define VERSION_TRANSLATION 0x$(LANGCODEHEX), 0x$(CODEPAGE)
*/
#undef VERSION_BLOCK
#undef VERSION_TRANSLATION
#ifdef _JAPANESE
#define VERSION_BLOCK "041104E4"
#define VERSION_TRANSLATION 0x0411, 0x04E4
#elif _GERMAN
#define VERSION_BLOCK "040704E4"
#define VERSION_TRANSLATION 0x0407, 0x04E4
#elif  _FRENCH
#define VERSION_BLOCK "040C04E4"
#define VERSION_TRANSLATION 0x040C, 0x04E4
#elif  _CHINESE
#define VERSION_BLOCK "080404E4"
#define VERSION_TRANSLATION 0x0804, 0x04E4
#elif  _TCHINESE
#define VERSION_BLOCK "040404E4"
#define VERSION_TRANSLATION 0x0404, 0x04E4
#elif  _SPANISH
#define VERSION_BLOCK "040A04E4"
#define VERSION_TRANSLATION 0x040A, 0x04E4
#elif  _ITALIAN
#define VERSION_BLOCK "041004E4"
#define VERSION_TRANSLATION 0x0410, 0x04E4
#elif  _PORTUGUESE
#define VERSION_BLOCK "041604E4"
#define VERSION_TRANSLATION 0x0416, 0x04E4
#else
#define VERSION_BLOCK "040904E4"
#define VERSION_TRANSLATION 0x0409, 0x04E4
#endif

