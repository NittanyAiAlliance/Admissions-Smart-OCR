from __future__ import unicode_literals, print_function

import csv
import plac
import random
from pathlib import Path
import spacy
from spacy.util import minibatch, compounding
import pandas as pd
import sys
import os 

# training data: texts, heads and dependency labels
# for no relation, we simply chose an arbitrary dependency label, e.g. '-'
'''
TRAIN_DATA = {
    (
        "PE Grade 9 Semester A 2.00", {"entities": [(0, 19, "COURSE"), (20, 21, "GRADE"), (22, 26, "CREDIT")]}
    ),
    (

    )
}
'''
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


@plac.annotations(
    model=("Model name. Defaults to blank 'en' model.", "option", "m", str),
    output_dir=("Optional output directory", "option", "o", Path),
    n_iter=("Number of training iterations", "option", "n", int),
    file_path=("Path to the text file", "positional", "o", Path),
)
def main(model=None, output_dir=None, n_iter=15, file_path=None):
    """Load the model, set up the pipeline and train the parser."""
    if model is not None:
        nlp = spacy.load(model)  # load existing spaCy model
        print("Loaded model '%s'" % model)
    else:
        nlp = spacy.blank("en")  # create blank Language class
        print("Created blank 'en' model")

    # We'll use the built-in dependency parser class, but we want to create a
    # fresh instance – just in case.
    if "parser" in nlp.pipe_names:
        nlp.remove_pipe("parser")
    parser = nlp.create_pipe("parser")
    nlp.add_pipe(parser, first=True)

    for text, annotations in TRAIN_DATA:
        for dep in annotations.get("deps", []):
            parser.add_label(dep)

    other_pipes = [pipe for pipe in nlp.pipe_names if pipe != "parser"]
    with nlp.disable_pipes(*other_pipes):  # only train parser
        optimizer = nlp.begin_training()
        for itn in range(n_iter):
            random.shuffle(TRAIN_DATA)
            losses = {}
            # batch up the examples using spaCy's minibatch
            batches = minibatch(TRAIN_DATA, size=compounding(4.0, 32.0, 1.001))
            for batch in batches:
                texts, annotations = zip(*batch)
                nlp.update(texts, annotations, sgd=optimizer, losses=losses)
            print("Losses", losses)

    # test the trained model
    test_model(nlp, file_path)

    # save model to output directory
    if output_dir is not None:
        output_dir = Path(output_dir)
        if not output_dir.exists():
            output_dir.mkdir()
        nlp.to_disk(output_dir)
        print("Saved model to", output_dir)

        # test the saved model
        print("Loading from", output_dir)
        nlp2 = spacy.load(output_dir)
        test_model(nlp2, file_path)


def test_model(nlp, file_path):
    output_filename = os.getcwd() + '/' + "result.csv"
    #Read inputted file
    texts = read_text_file(path=file_path)
    #Pass data to model
    docs = nlp.pipe(texts)
    writer = csv.writer(open(output_filename, "w+"))
    for doc in docs:
       # temp_list = [doc.text.encode('utf-8')]
        temp_list = []
        course_name = u""
        for t in doc:

            if(t.dep_ != "-" and t.dep != '' and 'Table' not in (t.text)):
                print(t, t.dep_)
                #If it is a course name, add to course name variable
                if (t.dep_ == u"ROOT" or t.dep_ == u"COURSE" ):
                    course_name = course_name + " " + t.text
                elif course_name:
                    temp_list.append([course_name,'COURSE'])
                    course_name = ''
                else:
                    temp_list.append([t.text, t.dep_])
        writer.writerow(temp_list)
    #output(output_filename)
    output(os.getcwd()+'/'+"result.csv")


def read_text_file(path=None):
    texts = []
    #Open file
    f = open(path, "r+")
    for line in f:
        #Clean each line and split into individual words
        line = line.rstrip()
        words = line.split(",")
        start_index = 0

        for x in range(len(words)):

            word = words[x]
            last_word = '' if x==0 else words[x-1]
            #If the word is number or previous word is not a number, move on because this cannot be the end of course data
            if(is_string_float(word)):
                continue
            if(not is_string_float(last_word)):
                continue
            #Convert from unioode
            #word = word.encode("utf-8")
            if(len(word) >= 2):
                #If word has + or - in it, continue
                if(len(word) == 2 and ("+" in word or "-" in word)):
                    continue
                #put course values in array
                texts.append(' '.join(words[start_index:x]))
                start_index = x

        #Put remaining words in array
        end_line = ' '.join(words[start_index:])
        #end_line = ' '.join(words)
        texts.append(end_line)
    f.close()
    return texts

def is_string_float(word):
    try:
        float(word)
        return True
    except ValueError:
        return False

def output(path):
    # reading csv file from path
    #file=file_path+"/"+file_name
    #data = pd.read_csv("/home/admin_cpp5231@WINDOM.OUTREACH.PSU.EDU/Desktop/NER Scripts/Output/result_7-30.csv", delim_whitespace=True, header=None)
    print(path)
    data = pd.read_csv(path, skipinitialspace=True, names=['Class','Course','Weight','Grade','Credit'])
    #data.shape
    print(data.head())
     
    # dropping null value columns to avoid errors 
    data.dropna(inplace = True) 
  
# new data frame with split value columns 
    newWeight = data["Weight"].str.split(" ", n = 1, expand = True)
#    for x in range(0, len(newWeight[0])):
#        value = newWeight[0][x]
#        newWeight[0][x] = value.encode("utf-8")
    newGrade = data["Grade"].str.split(" ", n = 1, expand = True)  
    newCredit = data["Credit"].str.split(" ", n = 1, expand = True) 
  
# making separate first name column from new data frame
    #data["Weight"]= newWeight[0].astype(str)
    data["Weight"]=newWeight[0].str.replace("[([,{':]", "")
    data["Grade"]=newGrade[0].str.replace("[([,{':]", "")
    data["Credit"]=newCredit[0].str.replace("[([,{':]", "")

    data.to_csv("output.csv")

if __name__ == "__main__":
    fp = os.getcwd() + '/' + sys.argv[1]
    main(file_path=fp,output_dir='output')