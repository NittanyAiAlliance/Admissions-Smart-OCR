import spacy
import numpy as np
from spacy.util import minibatch, compounding
from spacy.tokens import DocBin, Span
from spacy.training import Corpus, Example
from spacy.lang.en import English
from tqdm.auto import tqdm

nlp = English()

# Function to begin compiling training and evaluation data
def compile(batch = 500):
    fp = "./train_data.npy"
    data = np.load(fp, allow_pickle=True).tolist()
    create_datafiles(batch, data)

# Function to format training/evaluation data into Spacy-readable Docs in binary format
# Accepts a list of tuples of (text, ents)
# Each ent is of format [start, end, value]
# Returns an array of Doc objects
def format_docs(data, batch):

    # Create docs return object
    docs = []
    for doc, ents in tqdm(nlp.pipe(data, as_tuples=True, batch_size=batch), total=len(data)):
        this_ents = []
        for ent in ents:
            this_ent = Span(doc, start=int(ent[0]), end=int(ent[1] + 1), label=str(ent[2]))
            this_ents.append(this_ent)
        doc.set_ents(this_ents)
        docs.append(doc)

    return docs

# Function to create training files from datasets
def create_datafiles(batch, train_data):
    doc_bin = DocBin(docs=format_docs(train_data, batch))
    doc_bin.to_disk("./train.spacy")

if __name__=="__main__":
    compile()