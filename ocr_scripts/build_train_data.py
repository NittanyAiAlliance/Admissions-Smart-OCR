from spacy.tokenizer import Tokenizer
from spacy.lang.en import English
import csv
import pandas as pd
import re
import numpy as np
import random
from tqdm.auto import tqdm

# Function to find course level in token sen
def find_level(doc, level):
    synms = {'H': ['HON', 'HONORS', 'HNRS', 'HON.', 'HN'], 'PBIB': ['IB']}
    for token in doc:
        if(token.text.upper() == level):
            return token.i
        if(level in synms.keys()):
            if(token.text.upper() in synms[level]):
                return token.i
    return -1

# Function to return if a row is invalid
def is_valid(row):
    if(not len(str(row['course_title']).strip()) > 0):
        return False
    if(not str(row['course_title']).isprintable()):
        return False
    if(not str(row['course_academic_grade']).isalpha()): # TODO replace this script
        return False
    if(not len(str(row['course_academic_grade']).strip()) > 0):
        return False
    if(not len(str(row['course_credit_value']).strip()) > 0):
        return False
    return True

# Load training data numpy
tfp = "./train_data.npy"
TRAIN_DATA = np.load(tfp, allow_pickle=True).tolist()
nlp = English()

# Read SRAR CSV
with open ("srar.csv", "r") as file:
    df = pd.read_csv(file, delimiter = ",")

    print("Loaded training and SRAR data - compiling training set.")

    # Iterate pandas dataframe
    for index, row in tqdm(df.iterrows()):

        # Check for valid entries
        if(is_valid(row)):

            # Randomize credit length
            credit = str(row['course_credit_value'])
            for i in range(random.randint(0, 5)):
                credit = credit + '0'

            # Create courseline text
            course = ' '.join(str(row['course_title']).strip().split())
            text = course + ' ' + str(row['course_academic_grade']).strip() + ' ' + credit.strip()
            
            data_entry = (text, [])
            gradeToken = 1

            # Parse ocr tokens
            doc = nlp(text)

            # Detect and create grade, credit spans
            gradeToken = len(doc) - 2
            data_entry[1].append((gradeToken, gradeToken, 'GRADE'))
            data_entry[1].append((len(doc) - 1, len(doc) - 1, 'CREDIT'))

            # Handle regular courses
            if(str(row['course_level']) == 'RG'):
                data_entry[1].append((0, gradeToken - 1, 'COURSE'))

            # Handle course levels
            else:

                # Find index of level token
                levelToken = find_level(doc, row['course_level'])

                if(levelToken >= 0):

                    # Tag level token
                    data_entry[1].append((levelToken, levelToken, 'LEVEL'))

                    # Tag up to level token
                    if(levelToken > 0):
                        data_entry[1].append((0, levelToken - 1, 'COURSE'))

                    # Tag after level token
                    if(gradeToken - levelToken > 1):
                        data_entry[1].append((levelToken + 1, gradeToken - 1, 'COURSE'))

                # Handle no level token
                else:
                    data_entry[1].append((0, gradeToken - 1, 'COURSE')) 

            # Add to training set
            TRAIN_DATA.append(data_entry)

np.save(tfp, np.asarray(TRAIN_DATA))
print('Finished generating training data.')