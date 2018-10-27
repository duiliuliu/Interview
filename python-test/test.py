# -*- coding: utf-8 -*-
# author: pengr

# lambda 两个数相乘


def cal(a, b): return a*b
# cal = lambda a, b: a*b


print(cal(4, 5))

# 对字典根据键排序
dict_test = {
    'name': 'wangsan',
    'age': '20',
    'gender': 'female',
    'city': 'shenzhen',
    'tel': '1212121'
}
# 结果以元组保存，因为元组是有序的
print(sorted(dict_test.items(), key=lambda x: x[0]))

# 利用collections库中Counter方法统计词频
from collections import Counter
words = ['hello', 'world', 'do', 'you',
         'hello', 'fine', 'thank', 'you', 'hello']
print(Counter(words))


def my_counter(words):
    # 自己实现
    res = {}
    for word in words:
        if word not in res:
            res[word] = 1
        else:
            res[word] += 1
    return res


print(my_counter(words))

# 求奇数列表
list_test = [i for i in range(1, 11)]
# filter过滤后得到的是一个filter对象
print([i for i in filter(lambda x: x % 2 == 1, list_test)])
print([i for i in list_test if i % 2 == 1])
