import spacy
from spacy.util import minibatch, compounding
from spacy.tokens import DocBin, Span
from spacy.training import Corpus, Example
from spacy.lang.en import English
from train_data import TRAIN_DATA
from tqdm.auto import tqdm

nlp = English()

# Function to begin compiling training and evaluation data
def compile(batch = 50):
    create_datafiles(batch)

# Function to format training/evaluation data into Spacy-readable Docs in binary format
# Accepts a list of tuples of (text, ents)
# Each ent is of format [start, end, value]
# Returns an array of Doc objects
def format_docs(data, batch):
    docs = []
    for doc, ents in tqdm(nlp.pipe(data, as_tuples=True, batch_size=batch)):
        this_ents = []
        for ent in ents:
            this_ent = Span(doc, ent[0], ent[1], ent[2])
            this_ents.append(this_ent)
        doc.set_ents(ents)
        docs.append(doc)
    return docs

# Function to create training and evaluation files from datasets
def create_datafiles(batch):
    doc_bin = DocBin(format_docs(TRAIN_DATA, batch))
    doc_bin.to_disk("./train.spacy")
    doc_bin = DocBin(format_docs(TRAIN_DATA, batch))
    doc_bin.to_disk("./eval.spacy")

if __name__=="__main__":
    compile()