from fuzzywuzzy import process
import pandas as pd
import os
import sys
import json
def course_codes(input):
    df = pd.ExcelFile("subject_code.xlsx")
    df1 = pd.read_excel(df,"Course Codes")
    arr = df1['Unnamed: 2'].values.tolist()
    with open(input) as f:
        data = json.load(f)

    for i in data:
        try:
            if (process.extractOne(data[i]['COURSE'], arr)[1] > 75):

                data[i]['COURSE_NAME'] = process.extractOne(data[i]['COURSE'], arr)[0]
            else:
                data[i]['COURSE_NAME'] = "Others"
        except:
            data[i]['COURSE_NAME'] = "None"
            pass

    with open('final.json', 'w') as outfile:
        json.dump(data, outfile)
        
if __name__ == "__main__":
    relative_path = sys.argv[1]

    if os.path.exists(relative_path):
        course_codes(os.path.abspath(relative_path))
