# 引用计数

import sys

# 引用计数减少的情况之一为显式的del对象引用
# 那么对这个对象的再次引用呢？
obj = 129
new_obj = obj
print('del对象别名前：', sys.getrefcount(new_obj))
del obj
print(new_obj)

# 所以del仅是对引用计数减一，并未销毁对象

print('del对象别名后：', sys.getrefcount(new_obj))
