#pragma once

class CStatusObserver;

class CApmFactory
{
public:
	static const CStatusObserver& GetEdgeStatusObserver();

private:
	CApmFactory(void);
};
