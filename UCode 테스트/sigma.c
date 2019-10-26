/*
재귀 호출 방식으로 시그마 값 계산
*/
#include <stdio.h>
int sigma(int n) {
	if (n == 1)
		return 1;
	else
		return n + sigma(n - 1);
}
int main(void)
{
	int n, s;
	scanf_s("%d", &n);
	s = sigma(n);
	printf("%d ", s);
	printf("\n");
	return;
}