# -*- coding: utf-8 -*-
# author: pengr

from config import *
import re
import os


def readFile():
    with open(readmeFile, 'r', encoding=ENCODER) as f:
        return [row for row in f.readlines()]


def clearFile():
    files = os.listdir(docPath)
    for file in files:
        os.remove(os.path.join(docPath, file))


def splitFile():
    clearFile()
    rows = readFile()

    i = 0
    count = 0
    while '##' not in rows[i].split(' '):
        i += 1

    filename = str(count)+'. '+rows[i].split(' ')[1].strip()
    filename = docPath+filename+SUFFIX
    rows = rows[i:]

    for row in rows:
        if '##' not in row.split(' '):
            with open(filename, 'a', encoding=ENCODER) as f:
                row = row.replace("./images", "../images")
                f.write(row)
                # f.write('\n')
        else:
            count += 1
            filename = str(count)+'. '+row.split(' ')[1].strip()
            filename = docPath+filename+SUFFIX


if __name__ == '__main__':
    splitFile()
