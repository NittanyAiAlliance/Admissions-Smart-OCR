#!/usr/bin/env python
# coding: utf8
from __future__ import unicode_literals, print_function

import random
from pathlib import Path
import spacy
import os
import sys
import numpy
import pandas as pd
from spacy.util import minibatch, compounding
from spacy.lang.en import English
from collections import defaultdict
from pprint import pprint
import json

# select latest spaCy model and configure pipelines and patterns
nlp = spacy.load(Path(os.getcwd()+'/model/model-last')) 
nlpe = English()
ruler = nlp.add_pipe("attribute_ruler")
patterns = [[{"ORTH": "	"}]]
attrs = {"TAG": "TAB", "POS": "PUNCT"}
ruler.add(patterns=patterns, attrs=attrs)
print("Loaded model '%s'" % Path(os.getcwd()+'/model/model-last'))

# Function to find course level in token sen
def find_level(doc):
    levels = ['H', 'HON', 'HONORS', 'HNRS', 'HON.', 'HN', 'PBIB', 'IB', 'PD', 'AP']
    for token in doc:
        if(token.text.upper() in levels):
            return token.i
    return -1

# Preprocess with some blanket functions to improve noise reduction accuracy
def preprocess_ocr(text):
    if(text.replace(' ', '').isalpha()):
        return ''
    if(text.find('GPA') >= 0):
        return ''
    text = text.replace('(', '') #This improves extra lowers course and level
    text = text.replace(')', '') #This improves extra lowers course and level
    return text

# Process CSV data through OCR model
def process_ocr(csv_data):

    lines = {}
    index = 0
    for text in csv_data.split('\n'):
        text = ''.join(text).replace('"', '').replace(',', '').rstrip('\n')
        text = preprocess_ocr(text)
        if len(text) > 0:
            if(text.find("Table:") < 0):
                this_course = [text]
                doce = nlpe(text)
                levelToken = find_level(doce)
                if(levelToken >= 0):
                    level_ent = ("LEVEL", doce[levelToken].text)
                    this_course.append(level_ent)
                    process_text = doce[0:levelToken].text + ' ' + doce[levelToken+1:].text
                else:
                    process_text = text
                doc = nlp(process_text)
                for ent in doc.ents:
                    this_ent = (ent.label_, ent.text)
                    this_course.append(this_ent)
                lines["Line " + str(index)] = this_course
                index += 1
    
    return lines