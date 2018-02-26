#ifndef __GRPNAME_H
#define __GRPNAME_H

#ifdef _FRENCH
#define AS_GROUP_NAME			_T("ARCserveIT pour Windows NT")
#define AS_NTAGENT_GROUP_NAME	_T("Agent client ARCserveIT pour Windows NT")
#define AS_95AGENT_GROUP_NAME	_T("Agent client ARCserveIT pour Windows 95")
#define AS_DBAGENT_GROUP_NAME	_T("ARCserve Backup Agent")
#define ASO_GROUP_NAME			_T("Unicenter TNG Advanced Storage Option")
#define ASO_NTAGENT_GROUP_NAME	_T("Agent client ASO pour Windows NT")
#define ASO_95AGENT_GROUP_NAME	_T("Agent client ASO pour Windows 95")
#define ASO_DBAGENT_GROUP_NAME	_T("ASO Backup Agent")

#elif  _GERMAN
#define AS_GROUP_NAME			_T("ARCserveIT für Windows NT")
#define AS_NTAGENT_GROUP_NAME	_T("ARCserveIT Client Agent für Windows NT")
#define AS_95AGENT_GROUP_NAME	_T("ARCserveIT Client Agent für Windows 95")
#define AS_DBAGENT_GROUP_NAME	_T("ARCserve Backup Agent")
#define ASO_GROUP_NAME			_T("Unicenter TNG Advanced Storage Option")
#define ASO_NTAGENT_GROUP_NAME	_T("ASO Client Agent für Windows NT")
#define ASO_95AGENT_GROUP_NAME	_T("ASO Client Agent für Windows 95")
#define ASO_DBAGENT_GROUP_NAME	_T("ASO Backup Agent")

#elif  _JAPANESE
#define AS_GROUP_NAME			_T("ARCserveIT for Windows NT")
#define AS_NTAGENT_GROUP_NAME	_T("ARCserveIT Client Agent for Windows NT")
#define AS_95AGENT_GROUP_NAME	_T("ARCserveIT Client Agent for Windows 95")
#define AS_DBAGENT_GROUP_NAME	_T("ARCserve Backup Agent")
#define ASO_GROUP_NAME			_T("Unicenter TNG Advanced Storage Option")
#define ASO_NTAGENT_GROUP_NAME	_T("ASO Client Agent for Windows NT")
#define ASO_95AGENT_GROUP_NAME	_T("ASO Client Agent for Windows 95")
#define ASO_DBAGENT_GROUP_NAME	_T("ASO Backup Agent")

#else//_ENGLISH
#define AS_GROUP_NAME			_T("ARCserve")
#define AS_NTAGENT_GROUP_NAME	_T("ARCserve Client Agent for Windows NT")
#define AS_95AGENT_GROUP_NAME	_T("ARCserve Client Agent for Windows 95")
#define AS_DBAGENT_GROUP_NAME	_T("ARCserve Backup Agent")
#define ASO_GROUP_NAME			_T("Unicenter TNG Advanced Storage Option")
#define ASO_NTAGENT_GROUP_NAME	_T("ASO Client Agent for Windows NT")
#define ASO_95AGENT_GROUP_NAME	_T("ASO Client Agent for Windows 95")
#define ASO_DBAGENT_GROUP_NAME	_T("ASO Backup Agent")
#endif

#endif __GRPNAME_H