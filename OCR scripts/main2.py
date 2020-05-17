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
#from train import train


def main(file, model=Path(os.getcwd()+'/model'), output_dir=None, n_iter=50):
    # selects an existing model or creates a new model
    if model is not None:
        nlp = spacy.load(Path(os.getcwd()+'/model'))  # load existing spaCy model
        print("Loaded model '%s'" % Path(os.getcwd()+'/model'))
    else:
        nlp = spacy.blank("en")  # create blank Language class
        print("Created blank 'en' model")

    # test the trained model

    # # makes a list of files in the designated file directory
    # AWS_Textract_tables = []
    # for dirname, _, filenames in os.walk(file_path, topdown=True):
    #     for filename in filenames:
    #         if 'table' in filename:
    #             AWS_Textract_tables.append(os.path.join(dirname, filename))
    #         if filename == 'keyValues.csv':
    #             AWS_Textract_keyValues = os.path.join(dirname, filename)

    out_dict = {}
    f = open(Path(file))
    for ind, line in enumerate(f):
        text = ''.join(line).replace('"', '').replace(',', '').rstrip('\n')

        #text = "College Prep Algebra II A- 1.00"
        doc = nlp(text)
        ## SCORE
        # scores = numpy.zeros((len(doc), nlp.entity.model.nr_class))
        # with nlp.entity.step_through(doc) as state:
        #     while not state.is_final:
        #         action = state.predict()
        #         next_tokens = state.queue
        #         scores[next_tokens[0].i] = state.scores
        #         state.transition(action)
        # print(scores)
        linedict = {}
        for ent in doc.ents:
            linedict[ent.label_] = ent.text
        out_dict["line "+str(ind)] = linedict
        #print([(ent.text, ent.label_) for ent in doc.ents])
        #print("Tokens", [(t.text, t.ent_type_, t.ent_iob) for t in doc])

        # Number of alternate analyses to consider. More is slower, and not necessarily better -- you need to experiment on your problem.
        beam_width = 16
        # This clips solutions at each step. We multiply the score of the top-ranked action by this value, and use the result as a threshold. This prevents the parser from exploring options that look very unlikely, saving a bit of efficiency. Accuracy may also improve, because we've trained on greedy objective.
        beam_density = 0.0001 

        # doc = nlp(text)
        # for ent in doc.ents:
        #     print (ent.start_char, ent.text, ent.label_)
        #     docs = list(nlp.pipe(list(text), disable=['ner']))
        # beams = nlp.entity.beam_parse(docs, beam_width=16, beam_density=0.0001)

        # for beam in beams:
        #     for score, ents in nlp.entity.moves.get_beam_parses(beam):
        #         print (score, ents)

        #         entity_scores = defaultdict(float)
        #         for start, end, label in ents:
        #             # print ("here")
        #             entity_scores[(start, end, label)] += score
        #     print ('entity_scores', entity_scores)
        
        # with nlp.disable_pipes('ner'):
        #     docs = list(nlp.pipe(text))
        # beams = nlp.entity.beam_parse(docs, beam_width=beam_width, beam_density=beam_density)

        # for doc, beam in zip(docs, beams):
        #     entity_scores = defaultdict(float)
        #     for score, ents in nlp.entity.moves.get_beam_parses(beam):
        #         for start, end, label in ents:
        #             entity_scores[(start, end, label)] += score
        #         print(entity_scores)
    with open("output.json", "w+") as out:
        json.dump(out_dict, out)


if __name__ == "__main__":
    main(sys.argv[1])