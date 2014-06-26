package com.totalannihilationroadrage;

public class RotationTransformation
{
	public float angle;
    public boolean isFlippedHorizontally;
    public boolean isFlippedVertically;

    RotationTransformation()
    {
        angle = 0.0f;
        isFlippedHorizontally = false;
        isFlippedVertically = false;
    }
    RotationTransformation(float a, boolean isH, boolean isV)
    {
        angle = a;
        isFlippedHorizontally = isH;
        isFlippedVertically = isV;
    }
}
