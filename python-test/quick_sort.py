# python快排

nums = [1, 4, 5, 6, 3, 9, 2, 7, 8]


def sort(nums, low=0, high=len(nums)-1):
    if low >= high:
        return
    mid = partition(nums, low, high)
    sort(nums, low, mid-1)
    sort(nums, mid+1, high)


def partition(nums, low, high):
    n = nums[low]
    i = low
    j = high
    while True:
        while i < high and nums[i] < n:
            i += 1
        while j > low and n < nums[j]:
            j -= 1
        if i >= j:
            break
        nums[i], nums[j] = nums[j], nums[i]
    n, nums[i] = nums[i], n
    return i


if __name__ == '__main__':
    sort(nums)
    print(nums)
