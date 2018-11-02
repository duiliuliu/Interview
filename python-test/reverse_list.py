# 反转列表
nums = [i for i in range(10)]
print('初始数组：', nums)


# 通过遍历交换
new_nums = nums[:]
i = 0
j = len(nums)-1
while i < j:
    temp = new_nums[j]
    new_nums[j] = new_nums[i]
    new_nums[i] = temp
    i += 1
    j -= 1
print('遍历交换：', new_nums)

# 通过list内建方法reverse
# reversed()函数返回的是一个迭代器，而不是一个List，所以需要list函数转换一下
print('list.reverse()方法：', list(reversed(nums)))


# sorted方法  sorted(iterable[, cmp[, key[, reverse]]])
print('sorted方法:', sorted(nums, reverse=True))


# 切片方法
print('切片方法:', nums[::-1])
