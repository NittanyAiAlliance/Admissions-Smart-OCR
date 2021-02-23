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
    nlp = spacy.load(Path(os.getcwd()+'/model/model-last'))  # load existing spaCy model
    ruler = nlp.add_pipe("attribute_ruler")
    patterns = [[{"ORTH": "	"}]]
    attrs = {"TAG": "TAB", "POS": "PUNCT"}
    ruler.add(patterns=patterns, attrs=attrs)
    print("Loaded model '%s'" % Path(os.getcwd()+'/model/model-last'))

    out_dict = {}
    for index, text in enumerate(csv_data):
        print('Processing ' + text)
        text = ''.join(line).replace('"', '').replace(',', '').rstrip('\n')

        doc = nlp(text)

        

        linedict = {}
        for ent in doc.ents:
            print('Doc ' + str(ent) + ' has label ' + str(ent.label_))
            linedict[ent.label_] = ent.text
        out_dict["line "+str(ind)] = linedict
        
    return out_dict