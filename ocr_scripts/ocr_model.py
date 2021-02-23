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
from collections import defaultdict
from pprint import pprint
import json
from course_codes_final import course_codes

def process_ocr(csv_data):
    # selects an existing model or creates a new model
    nlp = spacy.load(Path(os.getcwd()+'/model/model-best'))  # load existing spaCy model
    ruler = nlp.add_pipe("attribute_ruler")
    patterns = [[{"ORTH": "	"}]]
    attrs = {"TAG": "TAB", "POS": "PUNCT"}
    ruler.add(patterns=patterns, attrs=attrs)
    print("Loaded model '%s'" % Path(os.getcwd()+'/model/model-best'))

    lines = {}
    for index, text in enumerate(csv_data.split('\n')):
        text = ''.join(text).replace('"', '').replace(',', '').rstrip('\n')
        if len(text) > 0:
            doc = nlp(text)
            for token in doc:
                print(str(token.i) + ": " + token.text)
            this_course = [doc.text]
            for ent in doc.ents:
                this_ent = (ent.label_, ent.text)
                this_course.append(this_ent)
            lines["Line " + str(index)] = this_course
        
    return lines