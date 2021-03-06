from spacy.tokenizer import Tokenizer
from spacy.lang.en import English
import csv
import pandas as pd
import re
import numpy as np
import random
from tqdm.auto import tqdm

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

        data_entry = (text, [])

        # Create courseline text
        text = str(row['course_title']) + ' ' + str(row['course_academic_grade']) + ' ' + str(row['course_credit_value'])

        # Parse ocr tokens
        doc = nlp(text)

        # Detect and create grade, credit spans
        for token in doc:
            if(token.text == row['course_academic_grade']):
                data_entry[1].append((token.i, token.i, 'GRADE'))
            if(token.text == row['course_credit_value']):
                data_entry[1].append((token.i, token.i, 'CREDIT'))

            print(str(token.i) + ": " + token.text)

        # Separate level from course

        # Create course and level spans

        # Add to training set
        TRAIN_DATA.append(data_entry)
    