from fuzzywuzzy import process
from fuzzywuzzy import fuzz
import pandas as pd
import os
import sys
import json
import spacy
import difflib

# Configuration for course codes excel file
fp = pd.ExcelFile("subject_code.xlsx")
df = pd.read_excel(fp,"Course Codes", engine='openpyxl')
subject_col = 'Subject Code'
code_col = 'Course Code'
course_col = 'Generic Course Name'

# Create array of nlp-processed course generic title docs
generic_course_titles = df[course_col].values.tolist()
#title_docs = []
#for title in generic_course_titles:
#    title_docs.append(nlp(title))

# Function to find matching STARS course for returned NLP data
def process_stars(data):
    
    for line in data:
        line_course = data[line][0]
        # Search ocr result for course label
        #for label in data[line][1:]:
        #    if(label[0] == "COURSE"):
        #        line_course = label[1]
        # Ignore lack of course label
        if(len(line_course) > 0):
            line_course = lemmatizer(line_course)
            try:
                # Process generic course title similarity with fuzzywuzzy
                similar_course = process.extractOne(line_course, generic_course_titles, scorer=fuzz.partial_ratio)
                if (similar_course[1] > 75):
                    # Add complete STARS info to return record
                    codes = find_code(similar_course[0])
                    data[line].append(["COURSE_NAME", similar_course[0]])
                    data[line].append(["COURSE_SUBJECT", codes[0]])
                    data[line].append(["COURSE_CODE", codes[1]])
                    #print("Returning " + line_course + " " + str(similar_course[1]) + " perct similar to " + similar_course[0])
                else:
                    data[line].append(["COURSE_NAME", "OTHER"])
            except:
                data[line].append(["COURSE_NAME", "ERROR"])
                pass
            
            # Process generic course title similarity with spacy vectors
            #line_course_doc = nlp(line_course)
            #most_similar = 0
            #similar_title = 'OTHER'
            #for title in title_docs:
            #    if(line_course_doc.similarity(title) > most_similar):
            #        most_similar = line_course_doc.similarity(title)
            #        similar_title = title.text
            #codes = find_code(similar_title)
            #data[line].append(["COURSE_NAME", similar_title])
            #data[line].append(["COURSE_SUBJECT", codes[0]])
            #data[line].append(["COURSE_CODE", codes[1]])
        else:
            data[line].append(["COURSE_NAME", "NONE"])

    return data

# Function to test comparators
def test_compare():
    courses = ["US History", "United States History", "Calculus BC", "Calc BC", "Calculus (BC)"]
    for course in courses:
        course = lemmatizer(course)
        print('For ' + course + str(difflib.get_close_matches(course, generic_course_titles, n=1, cutoff=0.2)))
        answer = process.extractOne(course, generic_course_titles, scorer=fuzz.partial_ratio)
        print("Returning " + course + " " + str(answer[1]) + " perct similar to " + answer[0])

# Lemmatize synonymous strings to STARS compatibility
def lemmatizer(line):
    course_dict = {
        'mgmt': 'management',
        'hr': 'human resources',
        'bus': 'business',
        'acc': 'accounting',
        'acct': 'accounting',
        'cs': 'computer science',
        'eng': 'english',
        'engl': 'english',
        'comp': 'composition',
        'lit': 'literature',
        'hist': 'history',
        'orch': 'orchestra',
        'photo': 'photography',
        'vid': 'video',
        'lang': 'language',
        'alg': 'algebra',
        'trig': 'trigonometry',
        'stat': 'statistics',
        'precalc': 'precalculus',
        'prog': 'programming',
        'geom': 'geometry',
        'diffeq': 'differential equations',
        'calc': 'calculus',
        'sci': 'science',
        'env': 'environment',
        'chem': 'chemistry',
        'bio': 'biology',
        'civ': 'civilization',
        'united': 'us',
        'states': '',
        'psych': 'psychology',
        'euro': 'european',
        'gov': 'government',
        'poli': 'polotics',
        'geog': 'geography',
        'econ': 'economics',
        '1': '9',
        '2': '10',
        '3': '11',
        '4': '12',
        'pe': 'physical education',
        'i': '1',
        'ii': '2',
        'iii': '3',
        'iv': '4',
        'v': '5',
        'vi': '6'
    }
    line = line.split(' ')
    index = 0
    for token in line:
        if(token.lower() in course_dict):
            line[index] = course_dict[token.lower()]
        index += 1
    return ' '.join(line)

# Function to find subject and course code given STARS generic course name
def find_code(title):
    for index, row in df.iterrows():
        if(str(row[course_col]) == str(title)):
            return (str(row[subject_col]), str(row[code_col]))
