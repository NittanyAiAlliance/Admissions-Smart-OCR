import webbrowser, os
import json
import boto3
import io
from io import BytesIO
import sys
from pprint import pprint
from flask import Flask, request, json
import base64
import pandas as pd
from pathlib import Path
import spacy
from spacy.util import minibatch, compounding
import csv
import plac
import random

api = Flask(__name__)

@api.route('/', methods=['POST'])
def process_transcript():
    img_str = request.get_json('img_str')['file']
    img_str = base64.b64decode(img_str)
    table_csv = get_table_csv_results(bytearray(img_str))
    #output = parse_transcript(table_csv=table_csv)
    return "vibe"

TRAIN_DATA = [
    (
        "Spanish 2 0 A+ 1 ",
        {
            "heads": [0, 0, 0, 0, 0],
            "deps": ["COURSE", "COURSE", "WEIGHTED", "GRADE", "CREDIT"],
        },
    ),
    (
        "Driver Safety 0 A 0.25",
        {
            "heads": [0, 0, 0, 0, 0],
            "deps": ["COURSE", "COURSE", "WEIGHTED", "GRADE", "CREDIT"],
        },
    ),
    (
        "12 A Taste of America 86 0.5",
        {
            "heads": [0, 0, 0, 0, 0, 4, 4],
            "deps": ["YEAR", "COURSE", "COURSE", "COURSE", "COURSE", "GRADE", "CREDIT"],
        },
    ),
    (
        "Biology 1 0 A 1",
        {
            "heads": [0, 0, 0, 0, 0],
            "deps": ["COURSE", "COURSE", "WEIGHTED", "GRADE", "CREDIT"],
        },
    ),
    (
        "English 9 93 1 1",
        {
            "heads": [0, 0, 0, 0, 3],
            "deps": ["COURSE", "COURSE", "GRADE", "CREDIT", "ATTEMPTED"],
        },
    ),
    (
        "GERMAN 1 100 1 1",
        {
            "heads": [0, 0, 0, 0, 3],
            "deps": ["COURSE", "COURSE", "GRADE", "CREDIT", "ATTEMPTED"],
        },
    ),
    (
        "Science 1 91 1",
        {
            "heads": [0, 0, 0, 0],
            "deps": ["COURSE", "WEIGHTED", "GRADE", "CREDIT"],
        },
    ),
    (
        "Speech & Debate Eng 0 A 0.5",
        {
            "heads": [0, 0, 0, 0, 0, 0, 0],
            "deps": ["COURSE", "COURSE", "COURSE", "COURSE", "WEIGHTED", "GRADE", "CREDIT"]
        }
    ),
    (
        "Nurse Aide Training 0 A+ 2",
        {
            "heads": [0, 0, 0, 0, 0, 0],
            "deps": ["COURSE", "COURSE", "COURSE", "WEIGHTED", "GRADE", "CREDIT"]
        }
    ),
    (
        "Chemistry 1 0 A 1",
        {
            "heads": [0, 0, 0, 0, 0],
            "deps": ["COURSE", "COURSE", "WEIGHTED", "GRADE", "CREDIT"]
        }
    ),
    (
        "IB Math St 90 1.00 1.00",
        {
            "heads": [0, 0, 0, 0, 0, 4],
            "deps": ["COURSE", "COURSE", "COURSE", "GRADE", "CREDIT", "ATTEMPTED"],
        },
    ),
   ]

def parse_transcript(table_csv, model=None, n_iter=15):
    if model is not None:
        nlp = spacy.load(model)
        print("Loaded model '%s'" % model)
    else:
        nlp = spacy.blank("en")
        print("Created blank 'en' model")
    
    if "parser" in nlp.pipe_names:
        nlp.remove_pipe("parser")
    parser = nlp.create_pipe("parser")
    nlp.add_pipe(parser, first=True)
    
    for text, annotations in TRAIN_DATA:
        for dep in annotations.get("deps", []):
            parser.add_label(dep)
    
    other_pipes = [pipe for pipe in nlp.pipe_names if pipe != "parser"]
    with nlp.disable_pipes(*other_pipes):
        optimizer = nlp.begin_training()
        for itn in range(n_iter):
            random.shuffle(TRAIN_DATA)
            losses = {}
            batches = minibatch(TRAIN_DATA, size=compounding(4.0, 32.0, 1.001))
            for batch in batches:
                texts, annotations = zip(*batch)
                nlp.update(texts, annotations, sgd=optimizer, losses=losses)
            print("Losses", losses)
    
    return test_model(nlp, table_csv)
    
    
def test_model(nlp, table_csv):
    texts = []
    for line in table_csv:
        line = line.rstrip()
        words = line.split(",")
        start_index = 0
        for x in range(len(words)):
            word = words[x]
            last_word = '' if x==0 else words[x-1]
            if(is_string_float(word)):
                continue
            if(not is_string_float(last_word)):
                continue
            if(len(word) >= 2):
                if(len(word) == 2 and ("+" in word or "-" in word)):
                    continue
                texts.append(' '.join(words[start_index:x]))
                start_index = x
        texts.append(' '.join(words[start_index:]))
    docs = nlp.pipe(texts)
    for doc in docs:
        temp_list = []
        course_name = u""
        output = io.StringIO()
        writer = csv.writer(output, quoting=csv.QUOTE_NONNUMERIC)
        for t in doc:
            if(t.dep_ != "-" and t.dep != '' and 'Table' not in (t.text)):
                if(t.dep_ == u"ROOT" or t.dep_ == u"COURSE"):
                    course_name = course_name + " " + t.text
                elif course_name:
                    temp_list.append([course_name,'COURSE'])
                    course_name = ''
                else:
                    temp_list.append([t.text, t.dep_])
        writer.writerow(temp_list)
    return format_output(output)

def format_output(output_str_io):
    data = pd.read_csv(output_str_io, skipinitialspace=True, names=['Class','Course','Weight','Grade','Credit'])
    data.head()
    data.dropna(inplace=True)
    newWeight = data["Weight"].str.split(" ", n = 1, expand = True)
    newGrade = data["Grade"].str.split(" ", n = 1, expand = True)
    newCredit = data["Credit"].str.split(" ", n = 1, expand = True)
    data["Weight"]=newWeight[0].str.replace("[([,{':]", "")
    data["Grade"]=newGrade[0].str.replace("[([,{':]", "")
    data["Credit"]=newCredit[0].str.replace("[([,{':]", "")
    return data.to_csv(io.StringIO())

def is_string_float(word):
    try:
        float(word)
        return True
    except ValueError:
        return False
    
def get_rows_columns_map(table_result, blocks_map):
    rows = {}
    for relationship in table_result['Relationships']:
        if relationship['Type'] == 'CHILD':
            for child_id in relationship['Ids']:
                cell = blocks_map[child_id]
                if cell['BlockType'] == 'CELL':
                    row_index = cell['RowIndex']
                    col_index = cell['ColumnIndex']
                    if row_index not in rows:
                        # create new row
                        rows[row_index] = {}

                    # get the text value
                    rows[row_index][col_index] = get_text(cell, blocks_map)
    return rows


def get_text(result, blocks_map):
    text = ''
    if 'Relationships' in result:
        for relationship in result['Relationships']:
            if relationship['Type'] == 'CHILD':
                for child_id in relationship['Ids']:
                    word = blocks_map[child_id]
                    if word['BlockType'] == 'WORD':
                        text += word['Text'] + ' '
                    if word['BlockType'] == 'SELECTION_ELEMENT':
                        if word['SelectionStatus'] == 'SELECTED':
                            text += 'X '
    return text


def get_table_csv_results(bytes_test):
    # process using image bytes
    # get the results
    client = boto3.client('textract',region_name='us-east-2')

    response = client.analyze_document(Document={'Bytes': bytes_test}, FeatureTypes=['TABLES'])

    # Get the text blocks
    blocks = response['Blocks']
    pprint(blocks)

    blocks_map = {}
    table_blocks = []
    for block in blocks:
        blocks_map[block['Id']] = block
        if block['BlockType'] == "TABLE":
            table_blocks.append(block)

    if len(table_blocks) <= 0:
        return "<b> NO Table FOUND </b>"

    csv = ''
    for index, table in enumerate(table_blocks):
        csv += generate_table_csv(table, blocks_map, index + 1)
        csv += '\n\n'

    return csv


def generate_table_csv(table_result, blocks_map, table_index):
    rows = get_rows_columns_map(table_result, blocks_map)

    table_id = 'Table_' + str(table_index)

    # get cells.
    csv = 'Table: {0}\n\n'.format(table_id)

    for row_index, cols in rows.items():

        for col_index, text in cols.items():
            csv += '{}'.format(text) + ","
        csv += '\n'

    csv += '\n\n\n'
    return csv


if __name__ == "__main__":
    api.run()
