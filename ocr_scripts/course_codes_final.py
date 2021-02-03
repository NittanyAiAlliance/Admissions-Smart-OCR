from fuzzywuzzy import process
import pandas as pd
import os
import sys
import json
def course_codes(data):
    df = pd.ExcelFile("subject_code.xlsx")
    df1 = pd.read_excel(df,"Course Codes", engine='openpyxl')
    arr = df1['Unnamed: 2'].values.tolist()

    for i in data:
        try:
            if (process.extractOne(data[i]['COURSE'], arr)[1] > 75):

                data[i]['COURSE_NAME'] = process.extractOne(data[i]['COURSE'], arr)[0]
            else:
                data[i]['COURSE_NAME'] = "Others"
        except:
            data[i]['COURSE_NAME'] = "None"
            pass

    return data