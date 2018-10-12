# -*- coding: utf-8 -*-
# author: pengr

import os
from config import *


def getFileList():
    files = os.listdir(docPath)
    return files


def mergeFile():
    fileList = getFileList()
    fileList.sort(key=lambda x: int(x.split('.')[0]))
    menu = []

    menu.append('[阅览](https://duiliuliu.github.io/Interview/)\n\n')
    menu.append('> # 目录\n\n')
    for file in fileList:
        file = file.split(' ')[1]
        file = file.replace(SUFFIX, '')
        file = '- [{}](#{})\n'.format(file, file)
        menu.append(file)

    menu.append('\n')
    menu.append('> # 正文\n\n')
    with open(readmeFile, 'w', encoding=ENCODER) as f:
        f.writelines(menu)

        for file in fileList:
            f.write('\n\n## {}\n\n'.format(
                file.split(' ')[1].replace(SUFFIX, '')))
            file = docPath + file
            f.write(open(file, 'r', encoding=ENCODER).read().replace(
                "../images", "./images"))


if __name__ == '__main__':
    mergeFile()
