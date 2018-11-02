import collections

# 求最长斐波那契子数列
# 波那契数列数列从第3项开始，每一项都等于前两项之和


def lenLongestFibSubseq(A):
    """
    :type A: List[int]
    :rtype: int
    """
    vset = set(A)
    dp = collections.defaultdict(lambda: collections.defaultdict(int))
    # print("dp", dp)
    size = len(A)
    ans = 0
    for i in range(size):
        x = A[i]
        for j in range(i + 1, size):
            y = A[j]
            if x + y not in vset:
                continue
            dp[y][x + y] = max(dp[y][x + y], dp[x][y] + 1)
            ans = max(dp[y][x + y], ans)
    return ans and ans + 2 or 0


nums = [1, 2, 3, 4, 5, 6, 7, 8]
print(lenLongestFibSubseq(nums))
