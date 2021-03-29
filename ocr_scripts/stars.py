from fuzzywuzzy import process
from difflib import SequenceMatcher
import pandas as pd
import os
import sys
import json

def similar(a, b):
    return SequenceMatcher(None, a, b).ratio()

def process_stars(data):
    df = pd.ExcelFile("subject_code.xlsx")
    df1 = pd.read_excel(df,"Course Codes", engine='openpyxl')
    generic_course_titles = df1['Unnamed: 2'].values.tolist()

    for line in data:
        # Search ocr result for course label
        line_course = ""
        for label in data[line][1:]:
            if(label[0] == "COURSE"):
                line_course = label[1]
        # Ignore lack of course label
        if(len(line_course) > 0):
            #try:
                # Process generic course title similarity with fuzzywuzzy
                #similar_course = process.extractOne(line_course, generic_course_titles)
                #if (similar_course[1] > 75):
                #    data[line].append(["COURSE_NAME", similar_course[0]])
                #    print("Returning " + line_course + " " + str(similar_course[1]) + " perct similar to " + similar_course[0])
                #else:
                #    data[line].append(["COURSE_NAME", "OTHER"])
            closest = 0
            course = ""
            for star in generic_course_titles:
                if(len(str(star)) > 0):
                    sim = similar(line_course, str(star))
                    if(sim > closest):
                        closest = sim
                        course = str(star)
            print("Returning " + line_course + " " + str(closest) + " perct similar to " + course)
            data[line].append(["COURSE_NAME", course])
            #except:
            #    data[line].append(["COURSE_NAME", "ERROR"])
            #    pass
        else:
            data[line].append(["COURSE_NAME", "NONE"])

    return data