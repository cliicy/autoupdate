#pragma once

#define PME_OK							0
#define PME_FILE_CREATED				1
#define PME_SESSION_ALREADY_EXIST		-1
#define PME_SESSION_NOT_FOUND			-2
#define PME_BUFFER_TOO_SMALL			-3
#define PME_PASSWORD_TOO_LONG			-4
#define PME_CREATE_FILE_FAILED			-5
#define PME_WRITE_FILE_FAILED			-6
#define PME_INAVLID_FILE_FORMAT			-7
#define PME_MASTER_MISMATCH             -8
#define PME_ENCRYPT_FAILED				-9
#define PME_INVALID_PARAMETER			-10

class IVisitSessionPasswordCallback
{
public:
	virtual void OnVisitSessionPassword(const GUID& idSession, LPCWSTR szPassword) = 0;
};

class IPasswordManagement
{
public:
	virtual void Delete() = 0;

public:
	// Add a session password
	// 0 - Succeed
	// -1 - Already exists
	// -4 - Password is too long
	virtual int AddPassword(const GUID& sessionId, LPCWSTR szPassword, ULONG nPasswordLength) = 0;

	// Get password for a session
	// 0 - Succeed
	// -2 - Could not found the session
	// -3 - The buffer is too small
	virtual int GetPassword(const GUID& sessionId, PWCHAR szBuffer, ULONG& nBufferSize) = 0;

	// Remove a entry from password list
	// 0 - Succeed
	// -2 - Could not found the session
	virtual int RemoveEntry(const GUID& sessionId) = 0;

	// Update Machine key in password management file
	// 0 - Succeed
	// -5 - Unable to create backup file
	virtual int UpdateMachineKey(LPCTSTR szNewUserName, LPCTSTR szNewPassword) = 0;

	// Shrink the password management file
	// 0 - Succeed
	// -5 - Unable to create backup file
	virtual int ShrinkFile() = 0;

    // Validate if current master key is same as the one in key management file.
    // 0 - Succeed. (master key is matched)
    // -8 - Master key is mismatch.
    virtual int ValidateMasterKey() = 0;

	virtual int VisitAllSessionPassword(IVisitSessionPasswordCallback *pCallback) = 0;
};
