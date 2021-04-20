from fuzzywuzzy import process
import pandas as pd
import os
import sys
import json
import spacy
import en_core_web_md

# Configuration for course codes excel file
nlp = spacy.load("en_core_web_md")
fp = pd.ExcelFile("subject_code.xlsx")
df = pd.read_excel(fp,"Course Codes", engine='openpyxl')
subject_col = 'Subject Code'
code_col = 'Course Code'
course_col = 'Generic Course Name'

# Create array of nlp-processed course generic title docs
generic_course_titles = df[course_col].values.tolist()
title_docs = []
for title in generic_course_titles:
    title_docs.append(nlp(title))

# Function to find matching STARS course for returned NLP data
def process_stars(data):
    
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
            #    # Add complete STARS info to return record
            #    codes = find_code(similar_course[0])
            #    data[line].append(["COURSE_NAME", similar_course[0]])
            #    data[line].append(["COURSE_SUBJECT", codes[0]])
            #    data[line].append(["COURSE_CODE", codes[1]])
            #    print("Returning " + line_course + " " + str(similar_course[1]) + " perct similar to " + similar_course[0])
            #else:
            #    data[line].append(["COURSE_NAME", "OTHER"])
            #except:
            #    data[line].append(["COURSE_NAME", "ERROR"])
            #    pass
            
            # Process generic course title similarity with spacy vectors
            line_course_doc = nlp(line_course)
            most_similar = 0
            similar_title = 'OTHER'
            for title in title_docs:
                if(line_course_doc.similarity(title) > most_similar):
                    most_similar = line_course_doc.similarity(title)
                    similar_title = title.text
            codes = find_code(similar_title)
            data[line].append(["COURSE_NAME", similar_title])
            data[line].append(["COURSE_SUBJECT", codes[0]])
            data[line].append(["COURSE_CODE", codes[1]])
        else:
            data[line].append(["COURSE_NAME", "NONE"])

    return data

# Function to find subject and course code given STARS generic course name
def find_code(title):
    for index, row in df.iterrows():
        if(str(row[course_col]) == str(title)):
            return (str(row[subject_col]), str(row[code_col]))
