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
import logging
from ocr_model import process_ocr

api = Flask(__name__)

logging.basicConfig(level=logging.DEBUG)

"""
@api {post} /api Upload File for OCR Processing
@apiName ProcessFile

@apiParam {String} file Base64 Encoded transcript file

@apiSuccess {String} data JSON result of OCR
"""
@api.route('/api/', methods=['POST'])
def process_transcript():

    api.logger.info('Received transcript POST call')

    # Decode request image to base 64
    img_str = request.get_json()['file']
    img_str = base64.b64decode(img_str)

    table_csv = get_table_csv_results(bytearray(img_str))

    # Process OCR on resulting CSV
    result_str = process_ocr(table_csv)
    
    # Pass result
    api.logger.info(result_str)
    return result_str

"""
@api {get} /api Check OCR API Status
@apiName GetStatus

@apiSuccess {String} Smart OCR is running string
"""
@api.route('/api/', methods=['GET'])
def get_status():
    return "Smart OCR is running"

def get_table_csv_results(bytes_test):

    api.logger.error('Received textract call') #DEBUG
    # process using image bytes
    # get the results
    client = boto3.client('textract', region_name='us-east-2')

    response = client.analyze_document(Document={'Bytes': bytes_test}, FeatureTypes=['TABLES'])

    # Get the text blocks
    blocks = response['Blocks']
    api.logger.error(blocks)

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

if __name__ == "__main__":
    api.run()
