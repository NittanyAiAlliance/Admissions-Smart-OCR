from fuzzywuzzy import process
import os
import sys
import csv
import pandas as pd
def get_ceeb(path):
    with open(path, "r") as my_input_file:
        temp_file =[(" ".join(row) + '\n') for row in csv.reader(my_input_file)]

    textract = [line.rstrip('\n') for line in temp_file]
    textract = ' '.join(textract)
    textract = textract.split(" ")

    ceeb = process.extractOne("CEEB Code", textract)
    textract = textract[textract.index(ceeb[0]):]
    for i in textract:
     if i.isdigit() and len(str(i)) == 9:
        return i
     return -1

def final_func(fp):
    total = []
    lst = []
    with open(fp, 'r') as inf:
        for line in inf:
            line
            temp = line.strip().split(",")

            for index, element in enumerate(temp):
                if len(element) == 0:
                    pass
                else:
                    course = element.split("'")

                    if course[-2] == 'COURSE':
                        length = len(lst)
                        for i in range(3 - length):
                            lst.append("")
                        total.append(lst)
                        lst = []
                        lst.append(temp[index - 1].split("'")[1])
                    if course[-2] == 'GRADE' and len(lst) == 1:
                        lst.append(temp[index - 1].split("'")[1])
                    if course[-2] == 'CREDIT' and len(lst) == 2:
                        lst.append(temp[index - 1].split("'")[1])

    lst = []

    lst.append(get_ceeb(fp))
    total.append(lst)
    return (total)
if __name__ == "__main__":
    fp = os.getcwd() + '/' + sys.argv[1]
    final_func_return = final_func(fp)
    print(final_func_return)
    f = open("result.csv", "w")
    f.write(str(final_func_return))