package com.coco.lock2.app.view;

public class Tools {
	public static boolean mCanClickArea(float paramFloat1, float paramFloat2,
			float paramFloat3, float paramFloat4, float paramFloat5,
			float paramFloat6) {
		return (paramFloat1 >= paramFloat3)
				&& (paramFloat1 <= paramFloat3 + paramFloat5)
				&& (paramFloat2 >= paramFloat4)
				&& (paramFloat2 <= paramFloat4 + paramFloat6);
	}

	public static int maxHeight(int[] paramArrayOfInt, int paramInt) {
		for (int i = 1;; i++) {
			if (i >= paramInt)
				return paramArrayOfInt[0];
			if (paramArrayOfInt[i] <= paramArrayOfInt[0])
				continue;
			paramArrayOfInt[0] = paramArrayOfInt[i];
		}
	}
}
